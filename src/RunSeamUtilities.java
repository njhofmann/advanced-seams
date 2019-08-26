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

  public static void main(String[] args) throws IOException {
    ArgumentParser argParser = new ArgumentParser();
    argParser.parseArgs(args);
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
