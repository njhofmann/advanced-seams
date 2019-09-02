package arg_parsing;

import cost_matricies.CostMatrixProcessor;
import cost_matricies.EnergizedProcessor;
import cost_matricies.ForwardEnergizedProcessor;
import energy_maps.AverageSurroundingGradient;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import masks.DefaultMask;
import masks.Mask;
import seam_manipulators.DefaultSeamManipulator;
import seam_manipulators.SeamManipulator;

/**
 * Given a set of user entered arguments, parses them to allow for a user to carry out a "seam
 * operation" on a desired image through a {@link SeamManipulator}. See
 * {@link ArgumentParser#parseArgs} for details on how given arguments should be structured.
 */
public class ArgumentParser {

  /**
   * "Seam operations" that a user can perform on a image. User is allowed to only select one of
   * these operations.
   */
  private static final Set<String> SeamOperations = Arrays.
      stream(new String[]{"remove", "replace", "resize"}).collect(
      Collectors.toSet());

  /**
   * Allowed optional arguments a user may enter.
   */
  private static final Set<String> AllowedOptionalArgs = Arrays.
      stream(new String[]{"record", "cost", "protect"}).collect(
      Collectors.toSet());

  /**
   * {@link SeamManipulatorFactory} this {@link ArgumentParser} used for creating
   * {@link SeamManipulator}s it uses to carry out user entered seam carving details on.
    */
  private final SeamManipulatorFactory factory;

  /**
   * Creates a {@link ArgumentParser} which carries out user specified seam carving operations
   * on the {@link SeamManipulator}s created by the given {@link SeamManipulatorFactory}.
   * @param factory factory for creating SeamManipulators to work on
   */
  public ArgumentParser(SeamManipulatorFactory factory) {
    if (factory == null) {
      throw new IllegalArgumentException("Given seam manipulator factory can't be null!");
    }
    this.factory = factory;
  }

  /**
   * Given a array of user entered arguments, parses them to allow a user to carry out a "seam
   * operation" on a {@link SeamManipulator}.
   *
   * First two arguments should be a path to the image the user wishes to process, and a path to a
   * non-existent file where the user wishes to save the resulting image.
   *
   * Rest of the entered arguments should be entered in the fashion of "-arg param1 param2 ...",
   * but can be entered in any order user wishes (ie can go "-arg1 ... -arg2 ..." OR
   * "-arg2 ... -arg1 ...").
   *
   * Only other required argument is the "seam operation" the user wishes to carry out, can be one
   * of:
   * <li><b>resize</b>: resize the image to a desired width and height, can designate parts of the
   * image to leave alone via a "mask" (denoted by optional "protect" argument</li>
   *
   * <li><b>remove</b>: delete part of the image designated via a "mask"</li>
   *
   * <li><b>replace</b>: remove then refill part of the image designated via a "mask"</li>
   *
   *
   * Following arguments are optional:
   * <li><b>record</b>: if user wishes to record the process of the selected seam operation being
   * carried out on the image, saves to same directory as save path</li>
   *
   * <li><b>cost</b>: designates what type of "cost matrix" should be used when processing the
   * image, either "forward" or "backward" - defaults to "forwards"</li>
   *
   * <li><b>protect</b>: optional parameter for if user selects the "resize" seam operation,
   * designates part of the image to "protect" from resizing via a "mask"</li>
   *
   * All "maskes" are denoted either by a two coordinate bounding box (upper left x, upper left y,
   * lower right x, lower right y), or a path to a black and white "mask" image - with image parts
   * denoting which part of the image to remove.
   *
   * @param args user entered arguments to parse
   * @throws IOException file in reading or writing to a given string {@link Path}
   */
  public void parseArgs(String[] args) throws IOException {
    int argsLength = args.length;
    if (argsLength < 4) {
      throw new IllegalArgumentException("Must be given at least four arguments: input path, save"
          + "path, and a seam operation.");
    }

    // Input image and save image paths required
    Path inputPath = Paths.get(args[0]);
    Path savePath = Paths.get(args[1]);

    // Get rid of required paths, create map of other args
    args = Arrays.copyOfRange(args, 2, argsLength - 1);
    Map<String, List<String>> flagCollector = createFlagCollector(args);

    // Check for cost matrix processor
    CostMatrixProcessor costMatrixProcessor = costMatrixProcessorParser(flagCollector);

    // Check option to record, get path if true
    boolean record = recordArgumentIncluded(flagCollector);

    // Create seam manipulator
    SeamManipulator seamManipulator = factory.create(inputPath,
        new AverageSurroundingGradient(),
        costMatrixProcessor,
        record);

    executeSeamOperation(seamManipulator, flagCollector);

    // Save resulting image of operation
    seamManipulator.saveCurrentImage(savePath);

    // Save process if record flag
    if (record) {
      String recordPath = savePath.toString().split(".")[0];
      recordPath += "_process.mp4";
      seamManipulator.saveCurrentProcess(Paths.get(recordPath));
    }
  }

  private Map<String, List<String>> createFlagCollector(String[] args) {
    Map<String, List<String>> flagCollector = new HashMap<>();
    List<String> currentCollection = new LinkedList<>();
    boolean allowedArgFound = false;
    for (int i = 0; i < args.length; i++) {
      String curArg = args[i];
      if (curArg.startsWith("-")) {
        curArg = curArg.substring(1).toLowerCase();

        if (allowedArgFound) { // Another seam operation found, throw exception
          throw new IllegalArgumentException(
              String.format("Duplicate seam operation found - allowed only one operation of %s!",
                  SeamOperations.toString()));
        }
        else if (SeamOperations.contains(curArg)) {
          allowedArgFound = true;
        }
        if (!AllowedOptionalArgs.contains(curArg)) {
          throw new IllegalArgumentException("Illegal argument flag given!");
        }
        currentCollection = new LinkedList<>();
        flagCollector.put(curArg, currentCollection);
      }
      else {
        currentCollection.add(curArg);
      }
    }

    if (!allowedArgFound) {
      throw new IllegalArgumentException("Seam operation not found!");
    }
    else if (flagCollector.containsKey("protect") && !flagCollector.containsKey("protect")) {
      throw new IllegalArgumentException("'protect' argument can only be included if 'resize' "
          + "seam operation is included!");
    }

    return flagCollector;
  }

  private CostMatrixProcessor costMatrixProcessorParser(Map<String, List<String>> flagCollector) {
    if (flagCollector.containsKey("cost")) {
      List<String> selectedProcessorList = flagCollector.get("cost");
      String selectedProcessor;
      if (selectedProcessorList.size() != 1) {
        throw new IllegalArgumentException("Cost matrix processor accepts only one argument - "
            + "forward or backwards!");
      }
      selectedProcessor = selectedProcessorList.get(0);
      switch (selectedProcessor) {
        case ("forward"):
          new ForwardEnergizedProcessor();
        case("backward"):
          return new EnergizedProcessor();
        default:
          throw new IllegalArgumentException("Invalid cost matrix processor argument!");
      }
    }
    return new EnergizedProcessor();
  }

  private boolean recordArgumentIncluded(Map<String, List<String>> flagCollector) {
    if (flagCollector.containsKey("record")) {
      if (flagCollector.get("record").size() != 0) {
        throw new IllegalArgumentException("'record' argument can't include any arguments!");
      }
      return true;
    }
    return false;
  }

  private void executeSeamOperation(SeamManipulator seamManipulator,
      Map<String, List<String>> flagCollector) throws IOException {
    if (flagCollector.containsKey("resize")) {
      int[] entries = listToSizedIntArray(flagCollector.get("resize"), 2);
      int width = entries[0];
      int height = entries[1];

      if (flagCollector.containsKey("protect")) {
        Mask protectMask = listToMask(flagCollector.get("protect"));
        seamManipulator.resize(width, height, protectMask);
      }
      else {
        seamManipulator.resize(width, height);
      }
    }
    else if (flagCollector.containsKey("replace") || flagCollector.containsKey("remove")) {

      // Sanity check to ensure only one of these is selected
      assert !(flagCollector.containsKey("replace") && flagCollector.containsKey("remove"));

      List<String> args;
      boolean replaceSelected = flagCollector.containsKey("replace");
      if (replaceSelected) {
        args = flagCollector.get("replace");
      }
      else {
        args = flagCollector.get("remove");
      }

      Mask mask = listToMask(args);
      if (replaceSelected) {
        seamManipulator.replaceArea(mask);
      }
      else {
        seamManipulator.removeArea(mask);
      }
    }
    else {
      throw new IllegalArgumentException("Valid seam operation not selected!");
    }
  }

  private String singleItemListToString(List<String> args) {
    if (args.size() != 1) {
      throw new IllegalArgumentException("Given list of arguments should only contain a single arg!");
    }
    return args.get(0);
  }

  private int[] listToSizedIntArray(List<String> args, int size) {
    if (args.size() != size) {
      throw new IllegalArgumentException("Given list of arguments should only contain a four args!");
    }

    for (String arg : args) {
      for (char argChar : arg.toCharArray()) {
        if (Character.isDigit(argChar)) {
          throw new IllegalArgumentException("Char is only a valid int only if between 0 to 9, "
              + "inclusive!");
        }
      }
    }

    // Check for non-positive entries
    int[] ints = args.stream().mapToInt(Integer::parseInt).toArray();
    for (int entry : ints) {
      if (entry < 1) {
        throw new IllegalArgumentException("Positive integers only!");
      }
    }
    return ints;
  }

  private Mask listToMask(List<String> args) throws IOException {
    if (args.size() == 1) {
      String maskString = singleItemListToString(args);
      Path maskPath = Paths.get(maskString);
      return new DefaultMask(maskPath);
    }
    else if (args.size() == 4) {
      int[] boundingBox = listToSizedIntArray(args, 4);
      return new DefaultMask(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3]);
    }
    else {
      throw new IllegalArgumentException("Invalid number of arguments for a mask - expects either"
          + "a path to a mask image or four coordinates making the bounding box of a mask!");
    }
  }
}
