package seamutilities.utilities.ImageMatrix;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import seamutilities.utilities.Coordinate;
import seamutilities.utilities.ImageMatrix.ImageMatrix;
import seamutilities.utilities.energymaps.EnergyMap;
import seamutilities.utilities.pixel.ImagePixel;
import seamutilities.utilities.pixel.Pixel;
import seamutilities.utilities.seams.DefaultSeam;
import seamutilities.utilities.seams.Seam;

public class DefaultImageMatrix implements ImageMatrix {

  private Pixel[][] imageMatrix;

  private final EnergyMap energyMap;

  private double maxEnergy;

  private int matrixWidth;

  private int matrixHeight;

  public DefaultImageMatrix(BufferedImage bufferedImage, EnergyMap energyMap) {
    if (bufferedImage == null) {
      throw new IllegalArgumentException("Given buffered image can't be null!");
    }
    else if (energyMap == null) {
      throw new IllegalArgumentException("Given energy map can't be null!");
    }

    this.energyMap = energyMap;

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

    maxEnergy = computeEnergyMap();
  }

  private double computeEnergyMap() {

    double maxEnergy = energyMap.computeEnergyMap(this);

    double[][] costMatrix = new double[matrixHeight][matrixWidth];
    double dummyValue = maxEnergy * 2;
    Arrays.fill(costMatrix[0], dummyValue);

    for (double[] row : costMatrix) {
      row[0] = dummyValue;
      row[costMatrix[0].length - 1] = dummyValue;
    }

    // First row has no rows above it to compute
    for (int row = 1; row < matrixHeight; row += 1) {
      // Pixels on edge columns only have two top neighbors
      for (int column = 1; column < matrixWidth - 1; column += 1) {

        double leftPixelEnergy = getPixel(column - 1, row).getEnergy();
        double rightPixelEnergy = getPixel(column + 1, row).getEnergy();
        double abovePixelEnergy = getPixel(column, row - 1).getEnergy();
        double leftRightDifference = Math.abs(leftPixelEnergy - rightPixelEnergy);

        // From upper left
        double leftDifference = leftRightDifference + Math.abs(abovePixelEnergy - leftPixelEnergy);
        double upperLeftPixelEnergy = getPixel(column - 1, row - 1).getEnergy();
        double upperLeft = upperLeftPixelEnergy + leftDifference;

        // From upper center
        double centerDifference = leftRightDifference;
        double upperCenter = abovePixelEnergy + centerDifference;

        // From upper right
        double rightDifference = leftRightDifference + Math.abs(abovePixelEnergy - rightPixelEnergy);
        double upperRightPixelEnergy = getPixel(column + 1, row - 1).getEnergy();
        double upperRight = upperRightPixelEnergy + rightDifference;

        double energyToAdd = Math.min(upperLeft, Math.min(upperCenter, upperRight));
        Pixel currentPixel = getPixel(column, row);
        double finalEnergy = currentPixel.getEnergy() + energyToAdd;

        Coordinate curCoor = currentPixel.getCoordinate();
        costMatrix[curCoor.getY()][curCoor.getX()] = finalEnergy;
      }
    }

    Seam seamToDelete = new DefaultSeam();

    double[] finalRow = costMatrix[costMatrix.length - 1];
    int curLowestColumn = 1;
    double curLowestEnergy = finalRow[curLowestColumn];
    for (int column = 1; column < costMatrix[0].length - 1; column += 1) {
      double curEnergy = finalRow[column];
      if (curEnergy < curLowestEnergy) {
        curLowestEnergy = curEnergy;
        curLowestColumn = column;
      }
    }

    int curColumn = curLowestColumn;
    Pixel firstPixel = getPixel(curColumn, matrixHeight - 1);
    seamToDelete.addPixel(firstPixel);

    for (int row = matrixHeight - 2; row >= 0; row -= 1) {
      int upperLeftColumn = curColumn - 1;
      int upperCenterColumn = curColumn;
      int upperRightColumn = curColumn + 1;

      double upperLeftEnergy = costMatrix[row][upperLeftColumn];
      double upperCenterEnergy = costMatrix[row][upperCenterColumn];
      double upperRightEnergy = costMatrix[row][upperRightColumn];

      double minEnergy = Math.min(upperLeftEnergy, Math.min(upperCenterEnergy, upperRightEnergy));

      if (Double.compare(minEnergy, upperLeftEnergy) == 0) {
        curColumn = upperLeftColumn;
      }
      else if (Double.compare(minEnergy, upperRightEnergy) == 0) {
        curColumn = upperRightColumn;
      }

      Pixel curPixel = getPixel(curColumn, row);
      seamToDelete.addPixel(curPixel);
    }

    seamToDelete.highlight(Color.RED);
    return maxEnergy;
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
    BufferedImage toReturn = new BufferedImage(matrixWidth, matrixHeight,
        BufferedImage.TYPE_3BYTE_BGR);

    for (int row = 0; row < matrixHeight; row += 1) {
      for (int column = 0; column < matrixWidth; column += 1) {
        Color currentPixelColor = getPixel(column, row).getColor();
        toReturn.setRGB(column, row, currentPixelColor.getRGB() ) ;
      }
    }
    return toReturn;
  }

  @Override
  public BufferedImage getEnergyMap() {
    BufferedImage toReturn = new BufferedImage(matrixWidth, matrixHeight,
        BufferedImage.TYPE_3BYTE_BGR);
    for (int row = 0; row < matrixHeight; row += 1) {
      for (int column = 0; column < matrixWidth; column += 1) {
        double currentPixelEnergy = getPixel(column, row).getEnergy() / maxEnergy;
        toReturn.setRGB(column, row, Color.HSBtoRGB(0, 0, (float)currentPixelEnergy));
      }
    }
    return toReturn;
  }

  @Override
  public Iterator<Pixel> iterator() {
    return null;
  }
}
