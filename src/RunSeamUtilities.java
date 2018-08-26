import energymaps.AverageSurroundingGradient;
import energymaps.EnergyMapMaker;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import masks.DefaultMask;
import masks.Mask;
import seammanipulators.DefaultSeamManipulator;
import seammanipulators.SeamManipulator;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  public static void main(String[] args) throws IOException {
    Path inputPath = Paths.get("", "resources\\baron.png").toAbsolutePath();
    Path outputPath = Paths.get("", "resources\\output.png").toAbsolutePath();
    Path videoPath = Paths.get("", "resources\\video.mp4").toAbsolutePath();
    Path maskPath = Paths.get("", "resources\\pathmask2.png").toAbsolutePath();
    EnergyMapMaker energyMap = new AverageSurroundingGradient();

    Mask rectMask = new DefaultMask(300, 100, 450, 200);
    Mask pathMask = new DefaultMask(maskPath);

    SeamManipulator seamManipulator = new DefaultSeamManipulator(inputPath, energyMap, true);
    seamManipulator.replaceArea(pathMask);
    //seamManipulator.resize(300, 600);
    seamManipulator.saveCurrentImage(outputPath);
    seamManipulator.saveCurrentProcess(videoPath);
    //ImageIO.write(seamManipulator.getCurrentCostMatrix(), "png", outputPath.toFile());
    //ImageIO.write(seamManipulator.getCurrentCostMatrix(), "png", outputPath.toFile());
  }
}
