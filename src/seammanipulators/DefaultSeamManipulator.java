package seammanipulators;

import costmatricies.HorizontalCostMatrix;
import costmatricies.HorizontalEnergy;
import costmatricies.VerticalCostMatrix;
import costmatricies.VerticalEnergy;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.imageio.ImageIO;
import masks.Mask;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import pixel.BorderPixel;
import pixel.ImagePixel;
import pixel.Pixel;
import pixel.iterators.ColumnIterator;
import pixel.iterators.ColumnRowIterator;
import pixel.iterators.RowColumnIterator;
import energymaps.EnergyMapMaker;
import pixel.iterators.RowIterator;
import seams.HorizontalSeam;
import seams.Seam;
import seams.VerticalSeam;
import seamutilities.utilities.Coordinate;

public class DefaultSeamManipulator implements SeamManipulator {

  List<BufferedImage> previousStates = new ArrayList<>();

  private final int startingWidth;

  private final int startingHeight;

  private Pixel upperLeftCorner;

  private final EnergyMapMaker energyMapMaker;

  private final int BufferedImageType;

  private int imageWidth;

  private int imageHeight;

  private double maxEnergyMapEnergy;

  private double maxCostMatrixEnergy;

  public DefaultSeamManipulator(Path inputFilePath, EnergyMapMaker energyMapMaker) throws IOException {
     validFilePath(inputFilePath);

    if (energyMapMaker == null) {
      throw new IllegalArgumentException("Given energy map can't be null!");
    }
    this.energyMapMaker = energyMapMaker;

    BufferedImage loadedImage = ImageIO.read(inputFilePath.toFile());
    BufferedImageType = loadedImage.getType();
    previousStates.add(loadedImage);
    imageWidth = loadedImage.getWidth();
    imageHeight = loadedImage.getHeight();

    if (imageWidth % 2 == 1) {
      startingWidth = imageWidth + 1;
    }
    else {
      startingWidth = imageWidth;
    }

    if (imageHeight % 2 == 1) {
      startingHeight = imageHeight + 1;
    }
    else {
      startingHeight = imageHeight;
    }

    Pixel[][] tempImageArray = new Pixel[imageHeight][imageWidth];
    for (int row = 0; row < imageHeight; row += 1) {
      for (int column = 0; column < imageWidth; column += 1) {
        Pixel currentPixel = new ImagePixel(new Color(loadedImage.getRGB(column, row)));
        tempImageArray[row][column] = currentPixel;

        if (row != 0) {
          Pixel abovePixel = tempImageArray[row - 1][column];
          abovePixel.setBelowPixel(currentPixel);
          currentPixel.setAbovePixel(abovePixel);

          /**
          if (column != imageWidth - 1) {
            Pixel upperRight = tempImageArray[row - 1][column + 1];
            upperRight.setLowerLeftPixel(currentPixel);
            currentPixel.setUpperRightPixel(upperRight);
          }
           **/
        }

        if (column != 0) {
          Pixel leftPixel = tempImageArray[row][column - 1];
          leftPixel.setRightPixel(currentPixel);
          currentPixel.setLeftPixel(leftPixel);

          /**
          if (row != 0) {
            Pixel upperLeft = tempImageArray[row - 1][column - 1];
            upperLeft.setLowerRightPixel(currentPixel);
            currentPixel.setUpperLeftPixel(upperLeft);
          }
           **/
        }
      }
    }
    upperLeftCorner = tempImageArray[0][0];
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

  private void computeEnergyMap() {
    maxEnergyMapEnergy = 0;
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    while (rowColumnIterator.hasNext()) {
      Pixel currentPixel = rowColumnIterator.next();
      energyMapMaker.computeEnergy(currentPixel);
      if (currentPixel.getEnergyMapEnergy() > maxEnergyMapEnergy) {
        maxEnergyMapEnergy = currentPixel.getEnergyMapEnergy();
      }
    }
  }

  @Override
  public void resize(int newX, int newY) {
    int xDiff = imageWidth - newX;
    int yDiff = imageHeight - newY;
    for (int i = 0; i < yDiff; i += 1) {
      computeHorizontalCostMatrix(new HorizontalEnergy());
      findMinimumHorizontalSeam();
      previousStates.add(colorsToBufferedImageHorizontal());
      computeEnergyMap();
    }
  }

  @Override
  public void resize(int newX, int newY, Mask areaToProtect) {

  }

  @Override
  public void removeArea(Mask areaToRemove) {

  }

  @Override
  public void replaceArea(Mask areaToRemove) {

  }

  private BufferedImage colorsToBufferedImage() {
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (rowColumnIterator.hasNext()) {
      int x = rowColumnIterator.getX();
      int y = rowColumnIterator.getY();
      Color currentColor = rowColumnIterator.next().getColor();
      //System.out.print(Integer.toString(x) + ", " + Integer.toString(y) + "\n");
      toReturn.setRGB(x, y, currentColor.getRGB());
    }
    return toReturn;
  }

  private BufferedImage colorsToBufferedImageHorizontal() {
    ColumnRowIterator columnRowIterator = new ColumnRowIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (columnRowIterator.hasNext()) {
      int x = columnRowIterator.getX();
      int y = columnRowIterator.getY();
      Color currentColor = columnRowIterator.next().getColor();
      //System.out.print(Integer.toString(x) + ", " + Integer.toString(y) + "\n");
      toReturn.setRGB(x, y, currentColor.getRGB());
    }
    return toReturn;
  }

  @Override
  public BufferedImage getCurrentImage() {
    return colorsToBufferedImage();
  }

  @Override
  public BufferedImage getCurrentEnergyMap() {
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (rowColumnIterator.hasNext()) {
      double currentEnergy = rowColumnIterator.next().getEnergyMapEnergy();
      toReturn.setRGB(rowColumnIterator.getX(), rowColumnIterator.getY(),
          Color.HSBtoRGB(0, 0, (float)(currentEnergy / maxEnergyMapEnergy)));
    }
    return toReturn;
  }

  private void computeVerticalCostMatrix(VerticalCostMatrix costMatrix) {
    maxCostMatrixEnergy = 0;
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    while (rowColumnIterator.hasNext()) {
      Pixel currentPixel = rowColumnIterator.next();

      if (rowColumnIterator.getY() == 0) {
        currentPixel.setCostMatrixEnergy(currentPixel.getEnergyMapEnergy());
      }
      else {
        costMatrix.compute(currentPixel);
      }

      if (currentPixel.getCostMatrixEnergy() > maxCostMatrixEnergy) {
        maxCostMatrixEnergy = currentPixel.getCostMatrixEnergy();
      }
    }
  }

  private void computeHorizontalCostMatrix(HorizontalCostMatrix costMatrix) {
    maxCostMatrixEnergy = 0;
    ColumnRowIterator columnRowIterator = new ColumnRowIterator(upperLeftCorner);
    while (columnRowIterator.hasNext()) {
      Pixel currentPixel = columnRowIterator.next();

      if (columnRowIterator.getX() == 0) {
        currentPixel.setCostMatrixEnergy(currentPixel.getEnergyMapEnergy());
      }
      else {
        costMatrix.compute(currentPixel);
      }

      if (currentPixel.getCostMatrixEnergy() > maxCostMatrixEnergy) {
        maxCostMatrixEnergy = currentPixel.getCostMatrixEnergy();
      }
    }
  }

  private void findMinimumVerticalSeam() {
    ColumnIterator columnIterator = new ColumnIterator(upperLeftCorner);
    Pixel lowerRightCorner = new BorderPixel();
    while (columnIterator.hasNext()) {
      lowerRightCorner = columnIterator.next();
    }

    RowIterator rowIterator = new RowIterator(lowerRightCorner);
    Pixel currentStartingPixel = new BorderPixel();
    while (rowIterator.hasNext()) {
      Pixel currentPixel = rowIterator.next();
      if (currentPixel.getCostMatrixEnergy() < currentStartingPixel.getCostMatrixEnergy()) {
        currentStartingPixel = currentPixel;
      }
    }

    Seam seam = new VerticalSeam();
    seam.add(currentStartingPixel);

    for (int row = imageHeight - 2; row >= 0; row -= 1) {
      Pixel upperLeftPixel = currentStartingPixel.getUpperLeftPixel();
      Pixel upperCenterPixel = currentStartingPixel.getAbovePixel();
      Pixel upperRightPixel = currentStartingPixel.getUpperRightPixel();

      double upperLeftEnergy = upperLeftPixel.getCostMatrixEnergy();
      double upperCenterEnergy = upperCenterPixel.getCostMatrixEnergy();
      double upperRightEnergy = upperRightPixel.getCostMatrixEnergy();

      double minEnergy = Math.min(upperLeftEnergy, Math.min(upperCenterEnergy, upperRightEnergy));

      if (Double.compare(minEnergy, upperLeftEnergy) == 0) {
        currentStartingPixel = upperLeftPixel;
      }
      else if (Double.compare(minEnergy, upperRightEnergy) == 0) {
        currentStartingPixel = upperRightPixel;
      }
      else {
        currentStartingPixel = upperCenterPixel;
      }
      seam.add(currentStartingPixel);
    }
    seam.remove();
    imageWidth -= 1;
  }

  private void findMinimumHorizontalSeam() {
    RowIterator rowIterator = new RowIterator(upperLeftCorner);
    Pixel upperRightCorner = new BorderPixel();
    while (rowIterator.hasNext()) {
      upperRightCorner = rowIterator.next();
    }

    ColumnIterator columnIterator = new ColumnIterator(upperRightCorner);
    Pixel currentStartingPixel = new BorderPixel();
    while (columnIterator.hasNext()) {
      Pixel currentPixel = columnIterator.next();
      if (currentPixel.getCostMatrixEnergy() < currentStartingPixel.getCostMatrixEnergy()) {
        currentStartingPixel = currentPixel;
      }
    }

    Seam seam = new HorizontalSeam();
    seam.add(currentStartingPixel);

    for (int row = imageWidth - 2; row >= 0; row -= 1) {
      Pixel upperLeftPixel = currentStartingPixel.getUpperLeftPixel();
      Pixel leftPixel = currentStartingPixel.getLeftPixel();
      Pixel lowerLeftPixel = currentStartingPixel.getLowerLeftPixel();

      double upperLeftEnergy = upperLeftPixel.getCostMatrixEnergy();
      double leftEnergy = leftPixel.getCostMatrixEnergy();
      double lowerLeftEnergy = lowerLeftPixel.getCostMatrixEnergy();

      double minEnergy = Math.min(upperLeftEnergy, Math.min(leftEnergy, lowerLeftEnergy));

      if (Double.compare(minEnergy, upperLeftEnergy) == 0) {
        currentStartingPixel = upperLeftPixel;
      }
      else if (Double.compare(minEnergy, lowerLeftEnergy) == 0) {
        currentStartingPixel = lowerLeftPixel;
      }
      else {
        currentStartingPixel = leftPixel;
      }
      seam.add(currentStartingPixel);
    }
    seam.remove();
    imageHeight -= 1;
  }

  @Override
  public BufferedImage getCurrentCostMatrix() {
    computeHorizontalCostMatrix(new HorizontalEnergy());
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (rowColumnIterator.hasNext()) {
      int x = rowColumnIterator.getX();
      int y = rowColumnIterator.getY();
      double currentEnergy = rowColumnIterator.next().getCostMatrixEnergy();
      toReturn.setRGB(x, y,
          Color.HSBtoRGB(0, 0, (float)(currentEnergy / maxCostMatrixEnergy)));
    }
    return toReturn;
  }

  @Override
  public void saveCurrentImage(Path filePath) throws IOException {
    validFilePath(filePath.getParent());
    ImageIO.write(colorsToBufferedImage(), "jpeg", filePath.toFile());

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
  public void saveCurrentProcess(Path filePath) throws IOException {
    validFilePath(filePath.getParent());
    SeekableByteChannel out = null;
    try {
      out = NIOUtils.writableFileChannel(filePath.toString());
      AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(25, 1));
      for (BufferedImage bufferedImage : previousStates) {
        bufferedImage = toEvenSidedImage(bufferedImage, startingWidth, startingHeight);
        // Encode the image
        encoder.encodeImage(bufferedImage);
      }
      // Finalize the encoding, i.e. clear the buffers, write the header, etc.
      encoder.finish();
    }
    finally {
      NIOUtils.closeQuietly(out);
    }
  }
}
