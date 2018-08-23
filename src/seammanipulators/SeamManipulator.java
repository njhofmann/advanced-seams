package seammanipulators;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.IOException;
import masks.Mask;

/**
 * Interface for implementing the operations of image resizing and object deletion with content
 * aware image resizing upon a given image, then saving the resulting image and a GIF of whichever
 * called process.
 */
public interface SeamManipulator {

  void resize(int newWidth, int newHeight);

  void resize(int newWidth, int newHeight, Mask areaToProtect);

  void removeArea(Mask areaToRemove);

  void replaceArea(Mask areaToRemove);

  BufferedImage getCurrentImage();

  BufferedImage getCurrentEnergyMap();

  BufferedImage getCurrentCostMatrix();

  void saveCurrentImage(Path filePath) throws IOException;

  void saveCurrentProcess(Path filePath) throws IOException;
}
