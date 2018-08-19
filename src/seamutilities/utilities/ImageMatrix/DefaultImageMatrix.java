package seamutilities.utilities.ImageMatrix;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import seamutilities.utilities.Coordinate;
import seamutilities.utilities.ImageMatrix.ImageMatrix;
import seamutilities.utilities.pixel.ImagePixel;
import seamutilities.utilities.pixel.Pixel;

public class DefaultImageMatrix implements ImageMatrix {

  private Pixel[][] imageMatrix;

  private int matrixWidth;

  private int matrixHeight;

  public DefaultImageMatrix(BufferedImage bufferedImage) {
    if (bufferedImage == null) {
      throw new IllegalArgumentException("Given buffered image can't be null!");
    }

    matrixWidth = bufferedImage.getWidth();
    matrixHeight = bufferedImage.getHeight();

    imageMatrix = new Pixel[matrixHeight][matrixWidth];

    for (int row = 0; row < matrixHeight; row += 1) {
      for (int column = 0; column < matrixWidth; column += 1) {
        Color colorToAdd = new Color(bufferedImage.getRGB(column, row));
        Coordinate coordinateToAdd = new Coordinate(column, row);
        Pixel pixelToAdd = new ImagePixel(colorToAdd, coordinateToAdd);
        imageMatrix[row][column] = pixelToAdd;
      }
    }
  }

  @Override
  public int getWidth() {
    return matrixWidth;
  }

  @Override
  public int getHeight() {
    return matrixHeight;
  }

  @Override
  public Pixel getPixel(int x, int y) {
    if (!(0 <= x && x <= matrixWidth)) {
      throw new IllegalArgumentException("Given x coordinate is out of current image's bounds!");
    }
    else if (!(0 <= y && y <= matrixHeight)) {
      throw new IllegalArgumentException("Given y coordinate is out of current image's bounds!");
    }
    return imageMatrix[y][x];
  }

  @Override
  public BufferedImage toBufferedImage() {
    return null;
  }

  @Override
  public Iterator<Pixel> iterator() {
    return null;
  }
}
