package seamutilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import seamutilities.utilities.Coordinate;
import seamutilities.utilities.energymaps.EnergyMapMaker;

public class DefaultSeamUtilities implements SeamUtilities {

  private int BufferedImageType;

  private List<BufferedImage> previousStates = new ArrayList<>();

  private final EnergyMapMaker energyMapMaker;

  private BufferedImage currentBufferedImage;

  private double[][] energyMap;
  
  private double[][] costMatrix;

  private int matrixWidth;

  private int matrixHeight;
  
  /**
   *
   * @param filePath
   * @param energyMapMaker
   */
  public DefaultSeamUtilities(Path filePath, EnergyMapMaker energyMapMaker) throws IOException {
    if (energyMapMaker == null) {
      throw new IllegalArgumentException("Given energy map can't be null!");
    }

    this.energyMapMaker = energyMapMaker;

    validFilePath(filePath);
    BufferedImage loadedImage = ImageIO.read(filePath.toFile());
    BufferedImageType = loadedImage.getType();
    previousStates.add(loadedImage);

    currentBufferedImage = loadedImage;
    matrixWidth = currentBufferedImage.getWidth();
    matrixHeight = currentBufferedImage.getHeight();
    computeEnergyMap();
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

  private double maxDoubleInDoubleMatrix(double[][] matrix) {
    double maxEnergy = 0;
    for (double[] row : matrix) {
      for (double currentEnergy : row) {
        if (currentEnergy > maxEnergy && Math.round(currentEnergy) != Integer.MAX_VALUE ) {
          maxEnergy = currentEnergy;
        }
      }
    }
    return maxEnergy;
  }

  /**
   * 
   * @param x
   * @param y
   * @return
   */
  private double getEnergy(int x, int y) {
    if (!(0 <= x && x <= matrixWidth)) {
      throw new IllegalArgumentException("Given x coordinate is out of current image's bounds!");
    }
    else if (!(0 <= y && y <= matrixHeight)) {
      throw new IllegalArgumentException("Given y coordinate is out of current image's bounds!");
    }
    return energyMap[y][x];
  }

  private double getCostMatrixEnergy(int x, int y) {
    if (!(0 <= x && x <= matrixWidth)) {
      throw new IllegalArgumentException("Given x coordinate is out of current image's bounds!");
    }
    else if (!(0 <= y && y <= matrixHeight)) {
      throw new IllegalArgumentException("Given y coordinate is out of current image's bounds!");
    }
    return costMatrix[y][x];
  }

  private void computeNormalVerticalCostMatrix() {
    costMatrix = new double[matrixHeight][matrixWidth];

    for (int i = 0; i < matrixWidth; i += 1) {
      costMatrix[0][i] = energyMap[0][i];
    }

    double dummyValue = maxDoubleInDoubleMatrix(costMatrix) * 2;
    costMatrix[0] = energyMap[0];

    for (int i = 1; i < matrixHeight; i += 1) {
      double[] row = costMatrix[i];
      row[0] = dummyValue;
      row[costMatrix[0].length - 1] = dummyValue;
    }

    // First row has no rows above it to compute
    for (int row = 1; row < matrixHeight; row += 1) {
      // Pixels on edge columns only have two top neighbors
      for (int column = 1; column < matrixWidth - 1; column += 1) {

        double upperCenterCostMatrixEnergy = getCostMatrixEnergy(column, row - 1);
        double upperLeftCostMatrixEnergy = getCostMatrixEnergy(column - 1, row - 1);
        double upperRightCostMatrixEnergy = getCostMatrixEnergy(column + 1, row - 1);

        double energyToAdd = Math.min(upperLeftCostMatrixEnergy, Math.min(upperCenterCostMatrixEnergy, upperRightCostMatrixEnergy));
        double currentEnergy = getEnergy(column, row);
        double finalEnergy = currentEnergy + energyToAdd;
        costMatrix[row][column] = finalEnergy;
      }
    }
  }

  private void computeForwardCostMatrix() {
    costMatrix = new double[matrixHeight][matrixWidth];

    for (int i = 0; i < matrixWidth; i += 1) {
      costMatrix[0][i] = energyMap[0][i];
    }

    double dummyValue = Integer.MAX_VALUE;
    costMatrix[0] = energyMap[0];

    for (int i = 1; i < matrixHeight; i += 1) {
      double[] row = costMatrix[i];
      row[0] = dummyValue;
      row[costMatrix[0].length - 1] = dummyValue;
    }

    // First row has no rows above it to compute
    for (int row = 1; row < matrixHeight; row += 1) {
      // Pixels on edge columns only have two top neighbors
      for (int column = 1; column < matrixWidth - 1; column += 1) {

        double upperLeftCostMatrixEnergy = getCostMatrixEnergy(column - 1, row - 1);
        double upperCenterCostMatrixEnergy = getCostMatrixEnergy(column, row - 1);
        double upperRightCostMatrixEnergy = getCostMatrixEnergy(column + 1, row - 1);

        double leftPixelEnergy = getEnergy(column - 1, row);
        double rightPixelEnergy = getEnergy(column + 1, row);
        double leftRightDifference = Math.abs(leftPixelEnergy - rightPixelEnergy);

        double aboveEnergy = getEnergy(column, row - 1);

        double leftDifference = leftRightDifference + Math.abs(aboveEnergy - leftPixelEnergy);
        double upperLeft = upperLeftCostMatrixEnergy + leftDifference;

        double upperCenter = upperCenterCostMatrixEnergy + leftRightDifference;

        double rightDifference = leftRightDifference + Math.abs(aboveEnergy - rightPixelEnergy);
        double upperRight = upperRightCostMatrixEnergy + rightDifference;

        double energyToAdd = Math.min(upperLeft, Math.min(upperCenter, upperRight));
        double currentEnergy = getEnergy(column, row);
        double finalEnergy = currentEnergy + energyToAdd;
        costMatrix[row][column] = finalEnergy;
      }
    }
  }

  /**
   * 
   * @param matrix
   * @return
   */
  private BufferedImage doubleMatrixToBufferedImage(double[][] matrix) {
    if (matrix == null) {
      throw new IllegalArgumentException("Given matrix can't be null!");
    }
    else if (matrix.length == 0) {
      throw new IllegalArgumentException("Given matrix must have a height greater than 0!");
    }
    else if (matrix[0].length == 0) {
      throw new IllegalArgumentException("Given matrix must have a width greater than 0!");
    }

    BufferedImage toReturn = new BufferedImage(matrixWidth, matrixHeight, BufferedImageType);

    double maxEnergy = maxDoubleInDoubleMatrix(matrix);
    for (int row = 1; row < matrixHeight - 1; row += 1) {
      for (int column = 1; column < matrixWidth - 1; column += 1) {
        double currentEnergy = matrix[row][column];
        toReturn.setRGB(column, row, Color.HSBtoRGB(0, 0, (float)(currentEnergy / maxEnergy)));
      }
    }
    return toReturn;
  }

  @Override
  public BufferedImage getEnergyMap() throws IllegalStateException {
    return doubleMatrixToBufferedImage(energyMap);
  }

  @Override
  public BufferedImage getCostMatrix() {
    computeForwardCostMatrix();
    return doubleMatrixToBufferedImage(costMatrix);
  }

  private List<Coordinate> findMinVerticalSeam() {

    List<Coordinate> seam = new ArrayList<>();

    double[] finalRow = costMatrix[costMatrix.length - 1];
    int curLowestColumn = 1;
    double curLowestEnergy = finalRow[curLowestColumn];
    for (int column = 1; column < matrixWidth - 1; column += 1) {
      double curEnergy = finalRow[column];
      if (curEnergy < curLowestEnergy) {
        curLowestEnergy = curEnergy;
        curLowestColumn = column;
      }
    }

    int curColumn = curLowestColumn;
    Coordinate firstCoor = new Coordinate(curColumn, currentBufferedImage.getHeight() - 1);
    seam.add(firstCoor);

    for (int row = currentBufferedImage.getHeight() - 2; row >= 0; row -= 1) {
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

      Coordinate curCoor = new Coordinate(curColumn, row);
      seam.add(curCoor);
    }
    return seam;
  }

  @Override
  public void saveCurrentImage(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {
    validFilePath(filePath.getParent()); // Need to check if parent folder of new file exists, new
                                         // file may not exist - will either create a new file or
                                         // overwrite an existing one.
    
    ImageIO.write(currentBufferedImage, "jpeg", filePath.toFile());
  }

  @Override
  public BufferedImage getCurrentImage() throws IllegalStateException {
    return currentBufferedImage;
  }

  @Override
  public void saveCurrentProcess(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {
    validFilePath(filePath.getParent());

    SeekableByteChannel out = null;
    try {
      out = NIOUtils.writableFileChannel(filePath.toString());
      // for Android use: AndroidSequenceEncoder
      AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(25, 1));
      for (BufferedImage bufferedImage : previousStates) {
        bufferedImage = toEvenSidedImage(bufferedImage, 640, 460);
        // Encode the image
        encoder.encodeImage(bufferedImage);
      }
      // Finalize the encoding, i.e. clear the buffers, write the header, etc.
      encoder.finish();
    } finally {
      NIOUtils.closeQuietly(out);
    }
  }

  private void computeEnergyMap() {
    energyMap = energyMapMaker.computeEnergyMap(currentBufferedImage);
  }

  private BufferedImage toEvenSidedImage(BufferedImage toConvert, int x, int y) {
    if (!(0 < x && x >= toConvert.getWidth())) {
      throw new IllegalArgumentException("Given x value is not in range of given image!");
    }
    else if (!(0 < y && x >= toConvert.getHeight())) {
      throw new IllegalArgumentException("Given y value is not in range of given image!");
    }
    else if (x % 2 == 1) {
      throw new IllegalArgumentException("Given x value must be even!");
    }
    else if (y % 2 == 1) {
      throw new IllegalArgumentException("Given y value must be even!");
    }

    BufferedImage toReturn = new BufferedImage(x, y, BufferedImageType);

    for (int row = 0; row < toConvert.getHeight(); row += 1) {
      for (int column = 1; column < toConvert.getWidth(); column += 1) {
        toReturn.setRGB(column, row, toConvert.getRGB(column, row));
      }
    }
    return toReturn;
  }

  @Override
  public void removeVerticalSeam() throws IllegalStateException {
    computeForwardCostMatrix();
    List<Coordinate> seamToDelete = findMinVerticalSeam();

    int newMatrixWidth = matrixWidth - 1;
    BufferedImage newBufferedImage = new BufferedImage(newMatrixWidth, matrixHeight, BufferedImageType);

    for (int row = matrixHeight - 1; row >= 0; row -= 1) {
      Coordinate rowCoor = seamToDelete.get(matrixHeight - row - 1);
      boolean rowCoorFound = false;
      for (int column = 0; column < matrixWidth; column += 1) {
        if (column == rowCoor.getX()) {
          rowCoorFound = true;
        }
        else if (rowCoorFound) {
          newBufferedImage.setRGB(column - 1, row, currentBufferedImage.getRGB(column, row));
        }
        else {
          newBufferedImage.setRGB(column, row, currentBufferedImage.getRGB(column, row));
        }
      }
    }
    previousStates.add(currentBufferedImage);
    currentBufferedImage = newBufferedImage;
    computeEnergyMap();
    matrixWidth = newMatrixWidth;
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
