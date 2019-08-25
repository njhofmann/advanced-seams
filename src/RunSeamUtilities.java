import energy_maps.AverageSurroundingGradient;
import energy_maps.EnergyMapMaker;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import masks.DefaultMask;
import masks.Mask;
import seam_manipulators.DefaultSeamManipulator;
import seam_manipulators.SeamManipulator;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  public static void parseArgs(String[] args) {
    int argsLength = args.length;
    if (argsLength < 4) {
      throw new IllegalArgumentException("Must be given at least four arguments: input path, save"
          + "path, and a seam operation.");
    }

    Path inputPath = Paths.get(args[0]);
    Path savePath = Paths.get(args[1]);

    SeamManipulator seamManipulator = new DefaultSeamManipulator(inputPath, )

    String seamOperation = args[2];
    switch (seamOperation) {
      case ("-resize"):

      case ("-remove"):

      case("-replace")
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
