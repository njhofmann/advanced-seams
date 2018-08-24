package seammanipulators;

import costmatricies.horizontal.HorizontalCostMatrix;
import costmatricies.horizontal.HorizontalEnergy;
import costmatricies.vertical.VerticalCostMatrix;
import costmatricies.vertical.VerticalEnergy;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import masks.Coordinate;
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

public class DefaultSeamManipulator implements SeamManipulator, Iterable<Pixel> {

  private static final int maskValue = 255;

  List<BufferedImage> previousStates = new ArrayList<>();

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

    Pixel[][] tempImageArray = new Pixel[imageHeight][imageWidth];
    for (int row = 0; row < imageHeight; row += 1) {
      for (int column = 0; column < imageWidth; column += 1) {
        Pixel currentPixel = new ImagePixel(new Color(loadedImage.getRGB(column, row)));
        tempImageArray[row][column] = currentPixel;

        if (row != 0) {
          Pixel abovePixel = tempImageArray[row - 1][column];
          abovePixel.setBelowPixel(currentPixel);
          currentPixel.setAbovePixel(abovePixel);
        }

        if (column != 0) {
          Pixel leftPixel = tempImageArray[row][column - 1];
          leftPixel.setRightPixel(currentPixel);
          currentPixel.setLeftPixel(leftPixel);
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

  private Pixel getPixel(int x, int y) {
    if (x < 0 || x >= imageWidth) {
      throw new IllegalArgumentException("Given x coordinate must be in the bounds of the image!");
    }
    else if (y < 0 || y >= imageHeight) {
      throw new IllegalArgumentException("Given y coordinate must be in the bounds of the image!");
    }

    Pixel pixelTopColumn = new BorderPixel();
    RowIterator rowIterator = new RowIterator(upperLeftCorner);
    while (x >= 0) {
      pixelTopColumn = rowIterator.next();
      x -= 1;
    }

    Pixel toReturn = new BorderPixel();
    ColumnIterator columnIterator = new ColumnIterator(pixelTopColumn);
    while (y >= 0) {
      toReturn = columnIterator.next();
      y -= 1;
    }

    return toReturn;
  }

  private void applyMask(Mask maskToApply, double valueToApply) {
    if (maskToApply == null) {
      throw new IllegalArgumentException("Given mask can't be null!");
    }
    else if (maskToApply.getMaxX() > imageWidth - 1) {
      throw new IllegalArgumentException("Given mask's coordinates must fit with this image's "
          + "current width!");
    }
    else if (maskToApply.getMinY() > imageHeight - 1) {
      throw new IllegalArgumentException("Given mask's coordinates must fit with this image's "
          + "current height!");
    }

    Coordinate[] coordinates = maskToApply.getCoordinates();
    for (Coordinate coordinate : coordinates) {
      int currentX = coordinate.getX();
      int currentY = coordinate.getY();
      Pixel toChange = getPixel(currentX, currentY);
      toChange.makeMask(valueToApply);
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

  private void computeVerticalCostMatrix(VerticalCostMatrix costMatrix) {
    maxCostMatrixEnergy = 0;
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    while (rowColumnIterator.hasNext()) {
      int currentY = rowColumnIterator.getY();
      Pixel currentPixel = rowColumnIterator.next();
      if (currentY == 0) {
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

  private Seam findMinimumVerticalSeam() {
    computeVerticalCostMatrix(new VerticalEnergy());

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
    return seam;
  }

  private void computeHorizontalCostMatrix(HorizontalCostMatrix costMatrix) {
    maxCostMatrixEnergy = 0;
    ColumnRowIterator columnRowIterator = new ColumnRowIterator(upperLeftCorner);
    while (columnRowIterator.hasNext()) {
      int currentX = columnRowIterator.getX();
      Pixel currentPixel = columnRowIterator.next();
      if (currentX == 0) {
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

  private Seam findMinimumHorizontalSeam() {
    computeHorizontalCostMatrix(new HorizontalEnergy());

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
    return seam;
  }

  @Override
  public void resize(int newWidth, int newHeight) {
    if (newWidth < 3) {
      throw new IllegalArgumentException("Given new width can't be less than 1 pixel");
    }
    else if (newHeight < 3) {
      throw new IllegalArgumentException("Given new width can't be less than 1 pixel");
    }

    while (imageWidth != newWidth || imageHeight != newHeight) {
      computeEnergyMap();
      Seam verticalSeam = findMinimumVerticalSeam();
      Seam horizontalSeam = findMinimumHorizontalSeam();

      if (imageWidth != newWidth && imageHeight != newHeight) {
        if (verticalSeam.getAverageEnergy() < horizontalSeam.getAverageEnergy()) {
          verticalSeam.remove();
          imageWidth -= 1;
        }
        else {
          horizontalSeam.remove();
          imageHeight -= 1;
        }
      }
      else if (imageWidth != newWidth) {
        verticalSeam.remove();
        imageWidth -= 1;
      }
      else if (imageHeight != newHeight) {
        horizontalSeam.remove();
        imageHeight -= 1;
      }
      previousStates.add(getCurrentImage());
    }
  }

  @Override
  public void resize(int newWidth, int newHeight, Mask areaToProtect) {
    applyMask(areaToProtect, DefaultSeamManipulator.maskValue);
    resize(newWidth, newHeight);
  }

  @Override
  public void removeArea(Mask areaToRemove) {
    applyMask(areaToRemove, -DefaultSeamManipulator.maskValue);

    BooleanSupplier hasMask = () -> {
        for (Pixel pixel : this) {
          if (pixel.isMask()) {
            return true;
          }
        }
        return false;
    };

    while (hasMask.getAsBoolean()) {
      computeEnergyMap();
      Seam verticalSeam = findMinimumVerticalSeam();
      Seam horizontalSeam = findMinimumHorizontalSeam();

      verticalSeam.remove();
      imageWidth -= 1;

      previousStates.add(getCurrentImage());
    }
  }

  @Override
  public void replaceArea(Mask areaToRemove) {

  }

  @Override
  public BufferedImage getCurrentImage() {
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (rowColumnIterator.hasNext()) {
      int x = rowColumnIterator.getX();
      int y = rowColumnIterator.getY();
      Color currentColor = rowColumnIterator.next().getColor();
      toReturn.setRGB(x, y, currentColor.getRGB());
    }
    return toReturn;
  }

  @Override
  public BufferedImage getCurrentEnergyMap() {
    computeEnergyMap();
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (rowColumnIterator.hasNext()) {
      int x = rowColumnIterator.getX();
      int y = rowColumnIterator.getY();
      double currentEnergy = rowColumnIterator.next().getEnergyMapEnergy();

      if (currentEnergy < 0) {
        currentEnergy = 0;
      }

      toReturn.setRGB(x, y,
          Color.HSBtoRGB(0, 0, (float)(currentEnergy / maxEnergyMapEnergy)));
    }
    return toReturn;
  }

  @Override
  public BufferedImage getCurrentCostMatrix() {
    computeEnergyMap();
    computeHorizontalCostMatrix(new HorizontalEnergy());
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (rowColumnIterator.hasNext()) {
      int x = rowColumnIterator.getX();
      int y = rowColumnIterator.getY();
      double currentEnergy = rowColumnIterator.next().getCostMatrixEnergy();

      if (currentEnergy < 0) {
        currentEnergy = 0;
      }

      toReturn.setRGB(x, y,
          Color.HSBtoRGB(0, 0, (float)(currentEnergy / maxCostMatrixEnergy)));
    }
    return toReturn;
  }

  @Override
  public void saveCurrentImage(Path filePath) throws IOException {
    validFilePath(filePath.getParent());
    ImageIO.write(getCurrentImage(), "jpeg", filePath.toFile());
  }

  private BufferedImage placeOnLargerImage(BufferedImage toConvert, int x, int y) {
    if (!(0 < x && x >= toConvert.getWidth())) {
      throw new IllegalArgumentException("Given x value is not in range of given image!");
    }
    else if (!(0 < y && x >= toConvert.getHeight())) {
      throw new IllegalArgumentException("Given y value is not in range of given image!");
    }

    BufferedImage toReturn = new BufferedImage(x, y, BufferedImage.TYPE_3BYTE_BGR);

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

    int widthToUse = previousStates.get(0).getWidth();
    int heightToUse = previousStates.get(0).getHeight();

    if (widthToUse % 2 == 1) {
      widthToUse += 1;
    }

    if (heightToUse % 2 == 1) {
      heightToUse += 1;
    }

    try {
      out = NIOUtils.writableFileChannel(filePath.toString());
      AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(25, 1));
      for (BufferedImage bufferedImage : previousStates) {
        bufferedImage = placeOnLargerImage(bufferedImage, widthToUse, heightToUse);
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

  @Override
  public Iterator<Pixel> iterator() {
    return new RowColumnIterator(upperLeftCorner);
  }
}
