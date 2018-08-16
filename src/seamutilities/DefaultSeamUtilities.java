package seamutilities;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import masks.Mask;
import seamutilities.utilities.Coordinate;
import seamutilities.utilities.EnergyMap;
import seamutilities.utilities.pixel.ImagePixel;
import seamutilities.utilities.pixel.Pixel;

public class DefaultSeamUtilities implements SeamUtilities {

  private int startingWidth = 0;

  private int startingHeight = 0;

  private int BufferedImageType = 0;

  private List<BufferedImage> previousStates = new ArrayList<>();

  private Pixel[][] imageMatrix;

  public DefaultSeamUtilities() {}

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

  private void imageLoaded() {
    if (imageMatrix == null) {
      throw new IllegalStateException("An image hasn't been loaded yet!");
    }
  }

  private BufferedImage imageMatrixToBufferImage(int width, int height) {
    imageLoaded();

    if (1 > width || imageMatrix[0].length > width) {
      throw new IllegalArgumentException("Given width must be >= the current image's width!");
    }
    else if (1 > height || imageMatrix.length > height) {
      throw new IllegalArgumentException("Given height must be >= the current image's height!");
    }

    BufferedImage toReturn = new BufferedImage(width, height, BufferedImageType);
    for (int row = 0; row < imageMatrix.length; row += 1) {
      for (int column = 0; column < imageMatrix[0].length; column += 1) {
        toReturn.setRGB(column, row, imageMatrix[row][column].getColor().getRGB());
      }
    }
    return toReturn;
  }

  @Override
  public void loadImage(Path filePath)
      throws IllegalArgumentException, IOException {

    validFilePath(filePath);
    BufferedImage loadedImage = ImageIO.read(filePath.toFile());
    previousStates.add(loadedImage);
    BufferedImageType = loadedImage.getType();

    startingHeight = loadedImage.getHeight();
    startingWidth = loadedImage.getWidth();
    imageMatrix = new Pixel[startingHeight][startingWidth];

    for (int row = 0; row < startingHeight; row += 1) {
      for (int column = 0; column < startingWidth; column += 1) {
        Color colorToAdd = new Color(loadedImage.getRGB(column, row));
        Coordinate coordinateToAdd = new Coordinate(column, row);
        Pixel pixelToAdd = new ImagePixel(colorToAdd, coordinateToAdd);
        imageMatrix[row][column] = pixelToAdd;
      }
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

    imageLoaded();
    BufferedImage toSave = imageMatrixToBufferImage(imageMatrix[0].length, imageMatrix.length);
    ImageIO.write(toSave, "jpeg", filePath.toFile());
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
