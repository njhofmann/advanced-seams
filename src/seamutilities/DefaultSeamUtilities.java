package seamutilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import masks.Mask;
import seamutilities.utilities.ImageMatrix.DefaultImageMatrix;
import seamutilities.utilities.ImageMatrix.ImageMatrix;
import seamutilities.utilities.energymaps.EnergyMap;
import seamutilities.utilities.pixel.Pixel;

public class DefaultSeamUtilities implements SeamUtilities {

  private int BufferedImageType = 0;

  private List<BufferedImage> previousStates = new ArrayList<>();

  private ImageMatrix imageMatrix;

  /**
   *
   * @param filePath
   * @param energyMap
   */
  public DefaultSeamUtilities(Path filePath, EnergyMap energyMap) throws IOException {
    if (energyMap == null) {
      throw new IllegalArgumentException("Given energy map can't be null!");
    }

    validFilePath(filePath);

    BufferedImage loadedImage = ImageIO.read(filePath.toFile());
    previousStates.add(loadedImage);
    BufferedImageType = loadedImage.getType();

    imageMatrix = new DefaultImageMatrix(loadedImage, energyMap);
  }

  public DefaultSeamUtilities(Path filePath, EnergyMap energyMap, Mask mask) throws IOException{
    this(filePath, energyMap);

  }

  /**
   * Checks if the given {@param filePath}  is both not null and does exist, else throws a corresponding
   * exception.
   *
   * @param filePath file path to check
   * @throws IllegalArgumentException if the given {@param filePath}  is null
   * @throws IOException if the given {@param filePath} does not exist
   */
  private void validFilePath(Path filePath) throws IllegalArgumentException, FileNotFoundException {
    if (filePath == null) {
      throw new IllegalArgumentException("Given file path can't be null!");
    }
    else if (Files.notExists(filePath)) {
      throw new FileNotFoundException("Given file path does not exist!");
    }
  }


  private BufferedImage imageMatrixToBufferImage(int width, int height) {
    if (1 > width || imageMatrix.getWidth() > width) {
      throw new IllegalArgumentException("Given width must be >= the current image's width!");
    }
    else if (1 > height || imageMatrix.getHeight() > height) {
      throw new IllegalArgumentException("Given height must be >= the current image's height!");
    }

    BufferedImage toReturn = new BufferedImage(width, height, BufferedImageType);
    for (int row = 0; row < imageMatrix.getHeight(); row += 1) {
      for (int column = 0; column < imageMatrix.getWidth(); column += 1) {
        toReturn.setRGB(column, row, imageMatrix.getPixel(column, row).getColor().getRGB());
      }
    }
    return toReturn;
  }

  @Override
  public BufferedImage getEnergyMap() throws IllegalStateException {
    return imageMatrix.getEnergyMap();
  }

  @Override
  public void saveCurrentImage(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {
    validFilePath(filePath.getParent()); // Need to check if parent folder of new file exists, new
                                         // file may not exist - will either create a new file or
                                         // overwrite an existing one.

    BufferedImage toSave = imageMatrixToBufferImage(imageMatrix.getWidth(), imageMatrix.getHeight());
    ImageIO.write(toSave, "jpeg", filePath.toFile());
  }

  @Override
  public BufferedImage getCurrentImage() throws IllegalStateException {
    return imageMatrixToBufferImage(imageMatrix.getWidth(), imageMatrix.getHeight());
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
