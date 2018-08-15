package seamutilities;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import masks.Mask;
import seamutilities.utilities.EnergyMap;

public class DefaultSeamUtilities implements SeamUtilities {

  private BufferedImage loadedImage;

  private void validFilePath(Path filePath) throws IllegalArgumentException, IOException {
    if (filePath == null) {
      throw new IllegalArgumentException("Given file path can't be null!");
    }
    else if (Files.notExists(filePath)) {
      throw new FileNotFoundException("Given file path does not exist!");
    }
  }

  @Override
  public void loadImage(Path filePath)
      throws IllegalArgumentException, IOException {

    try {
      validFilePath(filePath);
      loadedImage = ImageIO.read(filePath.toFile());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void applyMask(Mask mask)
      throws IllegalStateException, IOException, IllegalArgumentException {

  }

  @Override
  public void assignEnergyMap(EnergyMap energyMap) throws IllegalArgumentException {

  }

  @Override
  public BufferedImage getEnergyMap() throws IllegalStateException {
    return null;
  }

  @Override
  public void saveCurrentImage(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {
    validFilePath(filePath.getParent()); // Need to check if parent folder of new file exists, new
                                         // file may not exist - will either create a new file or
                                         // overwrite an existing one.
    ImageIO.write(loadedImage, "jpeg", filePath.toFile());
  }

  @Override
  public BufferedImage getCurrentImage() throws IllegalStateException {
    return null;
  }

  @Override
  public void saveCurrentProcess(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {

  }

  @Override
  public void removeVerticalSeam() throws IllegalStateException {

  }

  @Override
  public void insertVerticalSeam() throws IllegalStateException {

  }

  @Override
  public void removeHorizontalSeam() throws IllegalStateException {

  }

  @Override
  public void insertHorizontalSeam() throws IllegalStateException {

  }

  @Override
  public void removeDiagonalSeam() throws IllegalStateException {

  }

  @Override
  public void insertDiagonalSeam() throws IllegalStateException {

  }
}
