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

  private int matrixHeight;

  private int matrixWidth;

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

  private Pixel getPixel(int x, int y) {
    if (!(0 <= x && x <= matrixWidth)) {
      throw new IllegalArgumentException("Given x coordinate is out of current image's bounds!");
    }
    else if (!(0 <= y && y <= matrixHeight)) {
      throw new IllegalArgumentException("Given y coordinate is out of current image's bounds!");
    }
    return imageMatrix[y][x];
  }

  private void imageLoaded() {
    if (imageMatrix == null) {
      throw new IllegalStateException("An image hasn't been loaded yet!");
    }
  }

  private BufferedImage imageMatrixToBufferImage(int width, int height) {
    imageLoaded();

    if (1 > width || matrixWidth > width) {
      throw new IllegalArgumentException("Given width must be >= the current image's width!");
    }
    else if (1 > height || matrixHeight > height) {
      throw new IllegalArgumentException("Given height must be >= the current image's height!");
    }

    BufferedImage toReturn = new BufferedImage(width, height, BufferedImageType);
    for (int row = 0; row < matrixHeight; row += 1) {
      for (int column = 0; column < matrixWidth; column += 1) {
        toReturn.setRGB(column, row, getPixel(column, row).getColor().getRGB());
      }
    }
    return toReturn;
  }

  private void calculateEnergy() {
    imageLoaded();

    for (int row = 0; row < matrixHeight; row += 1) {
      for (int column = 0; column < matrixWidth; column += 1) {

        Pixel currentPixel = getPixel(column, row);
        Color currentColor = currentPixel.getColor();

        Color leftColor;
        if (column == 0) {
          leftColor = getPixel(matrixWidth - 1, row).getColor();
        }
        else {
          leftColor = getPixel(column - 1, row).getColor();
        }

        Color rightColor;
        if (column == matrixWidth - 1) {
          rightColor = getPixel(0, row).getColor();
        }
        else {
          rightColor = getPixel(column + 1, row).getColor();
        }

        Color topColor;
        if (row == 0) {
          topColor = getPixel(column, matrixHeight - 1).getColor();
        }
        else {
          topColor = getPixel(column, row - 1).getColor();
        }

        Color bottomColor;
        if (row == matrixHeight - 1) {
          bottomColor = getPixel(column, 0).getColor();
        }
        else {
          bottomColor = getPixel(column, row + 1).getColor();
        }

        Color topLeftColor;
        if (column == 0 && row == 0) {
          topLeftColor = getPixel(matrixWidth - 1, matrixHeight - 1).getColor();
        }
        else if (column == 0) {
          topLeftColor = getPixel(matrixWidth - 1, row - 1).getColor();
        }
        else if (row == 0) {
          topLeftColor = getPixel(column - 1, matrixHeight - 1).getColor();
        }
        else {
          topLeftColor = getPixel(column - 1, row - 1).getColor();
        }

        Color topRightColor;
        if (column == matrixWidth - 1 && row == 0) {
          topRightColor = getPixel(0, matrixHeight - 1).getColor();
        }
        else if (column == matrixWidth - 1) {
          topRightColor = getPixel(0, row - 1).getColor();
        }
        else if (row == 0) {
          topRightColor = getPixel(column + 1, matrixHeight - 1).getColor();
        }
        else {
          topRightColor = getPixel(column + 1, row - 1).getColor();
        }

        Color bottomLeftColor;
        if (column == 0 && row == matrixHeight - 1) {
          bottomLeftColor = getPixel(matrixWidth - 1, 0).getColor();
        }
        else if (row == matrixHeight - 1) {
          bottomLeftColor = getPixel(column - 1, 0).getColor();
        }
        else if (column == 0) {
          bottomLeftColor = getPixel(matrixWidth - 1, row + 1).getColor();
        }
        else {
          bottomLeftColor = getPixel(column - 1, row + 1).getColor();
        }

        Color bottomRightColor;
        if (column == matrixWidth - 1 && row == matrixHeight - 1) {
          bottomRightColor = getPixel(0, 0).getColor();
        }
        else if (row == matrixHeight - 1) {
          bottomRightColor = getPixel(column + 1, 0).getColor();
        }
        else if (column == matrixWidth - 1) {
          bottomRightColor = getPixel(0, row + 1).getColor();
        }
        else {
          bottomRightColor = getPixel(column + 1, row + 1).getColor();
        }

        Color[] colors = new Color[]{topColor, bottomColor, leftColor, rightColor, topLeftColor,
            topRightColor, bottomLeftColor, bottomRightColor};

        class ColorDifference {
          private float compute(Color color) {
            int red = Math.abs(color.getRed() - currentColor.getRed());
            int green = Math.abs(color.getGreen() - currentColor.getGreen());
            int blue = Math.abs(color.getBlue() - currentColor.getBlue());
            return (float)((red + green + blue) / 3);
          }
        }

        ColorDifference colorDifference = new ColorDifference();
        float finalValue = 0;
        for (Color color : colors) {
          finalValue += colorDifference.compute(color);
        }

        currentPixel.assignEnergy(finalValue / (8 * 255));
      }
    }
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
    matrixWidth = startingWidth;
    matrixHeight = startingHeight;
    imageMatrix = new Pixel[startingHeight][startingWidth];

    for (int row = 0; row < startingHeight; row += 1) {
      for (int column = 0; column < startingWidth; column += 1) {
        Color colorToAdd = new Color(loadedImage.getRGB(column, row));
        Coordinate coordinateToAdd = new Coordinate(column, row);
        Pixel pixelToAdd = new ImagePixel(colorToAdd, coordinateToAdd);
        imageMatrix[row][column] = pixelToAdd;
      }
    }
    calculateEnergy();
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
    imageLoaded();

    BufferedImage toReturn = new BufferedImage(matrixWidth, matrixHeight, BufferedImageType);
    for (int row = 0; row < matrixHeight; row += 1) {
      for (int column = 0; column < matrixWidth; column += 1) {
        float currentPixelEnergy = getPixel(column, row).getEnergy() * 10;
        toReturn.setRGB(column, row, Color.HSBtoRGB(0, 0, currentPixelEnergy));
      }
    }
    return toReturn;
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
    imageLoaded();
    return imageMatrixToBufferImage(imageMatrix[0].length, imageMatrix.length);
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
