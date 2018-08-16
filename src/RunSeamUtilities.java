import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import seamutilities.DefaultSeamUtilities;
import seamutilities.SeamUtilities;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  public static void main(String[] args) throws IOException {
    Path inputPath = Paths.get("", "resources\\mountains.jpeg").toAbsolutePath();
    Path outputPath = Paths.get("", "resources\\foo.jpeg").toAbsolutePath();

    SeamUtilities seamUtilities = new DefaultSeamUtilities();
    seamUtilities.loadImage(inputPath);
    //seamUtilities.saveCurrentImage(outputPath);
    ImageIO.write(seamUtilities.getEnergyMap(), "jpeg", outputPath.toFile());
  }
}
