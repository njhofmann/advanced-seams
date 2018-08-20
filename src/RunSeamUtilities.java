import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import masks.DefaultMask;
import masks.Mask;
import seamutilities.DefaultSeamUtilities;
import seamutilities.SeamUtilities;
import seamutilities.utilities.energymaps.AverageSurroundingGradient;
import seamutilities.utilities.energymaps.EnergyMapMaker;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  public static void main(String[] args) throws IOException {
    Path inputPath = Paths.get("", "resources\\bird.jpg").toAbsolutePath();
    Path outputPath = Paths.get("", "resources\\output.jpg").toAbsolutePath();
    Path videoPath = Paths.get("", "resources\\video.mp4").toAbsolutePath();
    EnergyMapMaker energyMap = new AverageSurroundingGradient();

    SeamUtilities seamUtilities = new DefaultSeamUtilities(inputPath, energyMap);

    for (int i = 0; i < 300; i += 1) {
      seamUtilities.removeVerticalSeam();
    }

    seamUtilities.saveCurrentImage(outputPath);
    seamUtilities.saveCurrentProcess(videoPath);
    //ImageIO.write(seamUtilities.getEnergyMap(), "jpg", outputPath.toFile());
  }
}
