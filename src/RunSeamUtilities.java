import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.imageio.ImageIO;
import masks.DefaultMask;
import masks.Mask;
import seamutilities.DefaultSeamUtilities;
import seamutilities.SeamUtilities;
import seamutilities.utilities.Coordinate;
import seamutilities.utilities.energymaps.AverageSurroundingGradient;
import seamutilities.utilities.energymaps.DualGradient;
import seamutilities.utilities.energymaps.EnergyMap;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  public static void main(String[] args) throws IOException {
    Path inputPath = Paths.get("", "resources\\beach.jpg").toAbsolutePath();
    Path outputPath = Paths.get("", "resources\\output.jpeg").toAbsolutePath();
    Path maskPath = Paths.get("", "resources\\test_mask.jpg").toAbsolutePath();
    Mask mask = new DefaultMask(maskPath);
    EnergyMap energyMap = new AverageSurroundingGradient();


    SeamUtilities seamUtilities = new DefaultSeamUtilities(inputPath, energyMap);
    seamUtilities.saveCurrentImage(outputPath);
    //ImageIO.write(seamUtilities.getEnergyMap(), "jpeg", outputPath.toFile());
  }
}
