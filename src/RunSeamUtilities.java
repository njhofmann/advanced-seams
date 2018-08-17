import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import seamutilities.DefaultSeamUtilities;
import seamutilities.SeamUtilities;
import seamutilities.utilities.energymaps.AverageSurroundingGradient;
import seamutilities.utilities.energymaps.EnergyMap;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  public static void main(String[] args) throws IOException {
    Path inputPath = Paths.get("", "resources\\mountains.jpeg").toAbsolutePath();
    Path outputPath = Paths.get("", "resources\\foo.jpeg").toAbsolutePath();
    EnergyMap energyMap = new AverageSurroundingGradient();

    SeamUtilities seamUtilities = new DefaultSeamUtilities(inputPath, energyMap);
    //seamUtilities.saveCurrentImage(outputPath);
    ImageIO.write(seamUtilities.getEnergyMap(), "jpeg", outputPath.toFile());
  }
}
