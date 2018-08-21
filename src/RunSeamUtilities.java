import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import seammanipulators.DefaultSeamManipulator;
import seammanipulators.SeamManipulator;
import energymaps.AverageSurroundingGradient;
import energymaps.EnergyMapMaker;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  public static void main(String[] args) throws IOException {
    Path inputPath = Paths.get("", "resources\\mountains.jpeg").toAbsolutePath();
    Path outputPath = Paths.get("", "resources\\output.jpg").toAbsolutePath();
    Path videoPath = Paths.get("", "resources\\video.mp4").toAbsolutePath();
    EnergyMapMaker energyMap = new AverageSurroundingGradient();

    SeamManipulator seamManipulator = new DefaultSeamManipulator(inputPath, energyMap);
    seamManipulator.resize(10, 100);
    seamManipulator.saveCurrentProcess(videoPath);
    //ImageIO.write(seamManipulator.getCurrentCostMatrix(), "jpg", outputPath.toFile());
    //ImageIO.write(seamUtilities.getEnergyMap(), "jpg", outputPath.toFile());
  }
}
