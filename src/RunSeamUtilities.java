import cost_matricies.CostMatrixProcessor;
import cost_matricies.EnergizedProcessor;
import cost_matricies.ForwardEnergizedProcessor;
import energy_maps.AverageSurroundingGradient;
import energy_maps.EnergyMapMaker;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Collections;
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
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  private static final Set<String> AllowedArgs = Arrays.
      stream(new String[]{"remove", "replace", "resize"}).collect(
      Collectors.toSet());


  private static final Set<String> AllowedMiscArgs = Arrays.
      stream(new String[]{"record", "cost", "protect"}).collect(
      Collectors.toSet());


  public static void parseArgs(String[] args) throws IOException {
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
    boolean record = flagCollector.containsKey("record");
    Path recordPath = null;
    if (record) {
      String recordPathString = singleItemListToString(flagCollector.get("record"));
      recordPath = Paths.get(recordPathString);
    }

    // Create seam manipulator
    SeamManipulator seamManipulator = new DefaultSeamManipulator(inputPath,
        new AverageSurroundingGradient(),
        costMatrixProcessor, record);

    executeSeamOperation(seamManipulator, flagCollector);

    // Save resulting image of operation
    seamManipulator.saveCurrentImage(savePath);

    // Save process if flag
    if (record) {
      assert recordPath != null;
      seamManipulator.saveCurrentProcess(recordPath);
    }
  }

  private static Map<String, List<String>> createFlagCollector(String[] args) {
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
                  AllowedArgs.toString()));
        }
        else if (AllowedArgs.contains(curArg)) {
          allowedArgFound = true;
        }
        if (!AllowedMiscArgs.contains(curArg)) {
          throw new IllegalArgumentException("Illegal argument flag given!");
        }
        currentCollection = new LinkedList<>();
        flagCollector.put(curArg, currentCollection);
      }
      else {
        currentCollection.add(curArg);
      }
    }
    return flagCollector;
  }

  private static CostMatrixProcessor costMatrixProcessorParser(Map<String, List<String>> flagCollector) {
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

  private static void executeSeamOperation(SeamManipulator seamManipulator,
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

  private static String singleItemListToString(List<String> args) {
    if (args.size() != 1) {
      throw new IllegalArgumentException("Given list of arguments should only contain a single arg!");
    }
    return args.get(0);
  }

  private static int[] listToSizedIntArray(List<String> args, int size) {
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

  private static Mask listToMask(List<String> args) throws IOException {
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

  public static void main(String[] args) throws IOException {
    System.out.println(args.length);
    Path inputPath = Paths.get("", "resources\\jaya.jpg");
    Path outputPath = Paths.get("", "resources\\output.png");
    Path videoPath = Paths.get("", "resources\\video.mp4");
    Path maskPath = Paths.get("", "resources\\pathmask2.png");
    EnergyMapMaker energyMap = new AverageSurroundingGradient();
    System.out.println(Files.exists(maskPath));
/*    Mask rectMask = new DefaultMask(300, 100, 450, 200);
    Mask pathMask = new DefaultMask(maskPath);

    SeamManipulator seamManipulator = new DefaultSeamManipulator(inputPath, energyMap, false);
    seamManipulator.replaceArea(pathMask);
    seamManipulator.resize(1920, 1080);
    seamManipulator.saveCurrentImage(outputPath);*/
    //seamManipulator.saveCurrentProcess(videoPath);
    //ImageIO.write(seamManipulator.getCurrentCostMatrix(), "png", outputPath.toFile());
    //ImageIO.write(seamManipulator.getCurrentCostMatrix(), "png", outputPath.toFile());
  }
}
