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
    Path inputPath = Paths.get("", "resources\\yidris.jpg").toAbsolutePath();
    Path outputPath = Paths.get("", "resources\\costmatrix.jpg").toAbsolutePath();
    Path videoPath = Paths.get("", "resources\\video.mp4").toAbsolutePath();
    EnergyMapMaker energyMap = new AverageSurroundingGradient();

    Mask mask = new DefaultMask(300, 100, 450, 200);
    SeamManipulator seamManipulator = new DefaultSeamManipulator(inputPath, energyMap);
    //seamManipulator.removeArea(mask);
    seamManipulator.resize(800, 800, mask);
    seamManipulator.saveCurrentImage(outputPath);
    seamManipulator.saveCurrentProcess(videoPath);
    ImageIO.write(seamManipulator.getCurrentCostMatrix(), "jpg", outputPath.toFile());
    //ImageIO.write(seamManipulator.getCurrentEnergyMap(), "jpg", outputPath.toFile());
  }
}
