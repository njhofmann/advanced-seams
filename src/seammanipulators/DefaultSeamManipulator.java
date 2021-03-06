package seammanipulators;

import costmatricies.horizontal.HorizontalCostMatrix;
import costmatricies.horizontal.HorizontalEnergy;
import costmatricies.vertical.VerticalCostMatrix;
import costmatricies.vertical.VerticalEnergy;
import java.util.Stack;
import utility.Coordinate;
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
import utility.DefaultSeamAdjuster;
import utility.SeamAdjuster;

public class DefaultSeamManipulator implements SeamManipulator, Iterable<Pixel> {

  private final boolean record;

  private static final int maskValue = 5000;

  List<BufferedImage> previousStates = new ArrayList<>();

  private Pixel upperLeftCorner;

  private final EnergyMapMaker energyMapMaker;

  private final int BufferedImageType;

  private int imageWidth;

  private int imageHeight;

  private double maxEnergyMapEnergy;

  private double maxCostMatrixEnergy;

  public DefaultSeamManipulator(Path inputFilePath, EnergyMapMaker energyMapMaker, boolean record) throws IOException {
     validFilePath(inputFilePath);
     this.record = record;

    if (energyMapMaker == null) {
      throw new IllegalArgumentException("Given energy map can't be null!");
    }
    this.energyMapMaker = energyMapMaker;

    BufferedImage loadedImage = ImageIO.read(inputFilePath.toFile());
    BufferedImageType = loadedImage.getType();
    imageWidth = loadedImage.getWidth();
    imageHeight = loadedImage.getHeight();
    upperLeftCorner = bufferedImageToPixel(loadedImage);
    storeCurrentState();
  }

  private Pixel bufferedImageToPixel(BufferedImage toConvert) {
    if (toConvert == null) {
      throw new IllegalArgumentException("Given image can't be null!");
    }

    int imageWidth = toConvert.getWidth();
    int imageHeight = toConvert.getHeight();

    Pixel[][] tempImageArray = new Pixel[imageHeight][imageWidth];
    for (int row = 0; row < imageHeight; row += 1) {
      for (int column = 0; column < imageWidth; column += 1) {
        Pixel currentPixel = new ImagePixel(new Color(toConvert.getRGB(column, row)));
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
    return tempImageArray[0][0];
  }

  private Pixel copyCurrentImage() {
    return bufferedImageToPixel(getCurrentImage());
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

  private void computeEnergyMap(Pixel upperLeftCorner) {
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

  private void computeVerticalCostMatrix(VerticalCostMatrix costMatrix, Pixel upperLeftCorner) {
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

  private Seam findMinimumVerticalSeam(Pixel upperLeftCorner) {
    computeEnergyMap(upperLeftCorner);
    computeVerticalCostMatrix(new VerticalEnergy(), upperLeftCorner);

    int currentX = -1;
    int currentY = -1;

    ColumnIterator columnIterator = new ColumnIterator(upperLeftCorner);
    Pixel lowerRightCorner = new BorderPixel();
    while (columnIterator.hasNext()) {
      currentY += 1;
      lowerRightCorner = columnIterator.next();
    }

    int tempX = -1;
    RowIterator rowIterator = new RowIterator(lowerRightCorner);
    Pixel currentStartingPixel = new BorderPixel();
    while (rowIterator.hasNext()) {
      Pixel currentPixel = rowIterator.next();
      tempX += 1;
      if (currentPixel.getCostMatrixEnergy() < currentStartingPixel.getCostMatrixEnergy()) {
        currentX = tempX;
        currentStartingPixel = currentPixel;
      }
    }

    Seam currentSeam = new VerticalSeam();
    currentSeam.add(currentStartingPixel, new Coordinate(currentX, currentY));

    for (int row = imageHeight - 2; row >= 0; row -= 1) {
      Pixel upperLeftPixel = currentStartingPixel.getUpperLeftPixel();
      Pixel upperCenterPixel = currentStartingPixel.getAbovePixel();
      Pixel upperRightPixel = currentStartingPixel.getUpperRightPixel();

      double upperLeftEnergy = upperLeftPixel.getCostMatrixEnergy();
      double upperCenterEnergy = upperCenterPixel.getCostMatrixEnergy();
      double upperRightEnergy = upperRightPixel.getCostMatrixEnergy();

      double minEnergy = Math.min(upperLeftEnergy, Math.min(upperCenterEnergy, upperRightEnergy));

      if (Double.compare(minEnergy, upperLeftEnergy) == 0) {
        currentX -= 1;
        currentStartingPixel = upperLeftPixel;
      }
      else if (Double.compare(minEnergy, upperRightEnergy) == 0) {
        currentX += 1;
        currentStartingPixel = upperRightPixel;
      }
      else {
        currentStartingPixel = upperCenterPixel;
      }
      currentY -= 1;
      currentSeam.add(currentStartingPixel, new Coordinate(currentX, currentY));
    }
    return currentSeam;
  }

  private Seam findMinimumVerticalSeam() {
    return findMinimumVerticalSeam(upperLeftCorner);
  }

  private void computeHorizontalCostMatrix(HorizontalCostMatrix costMatrix, Pixel upperLeftCorner) {
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

  private Seam findMinimumHorizontalSeam(Pixel upperLeftCorner) {
    computeEnergyMap(upperLeftCorner);
    computeHorizontalCostMatrix(new HorizontalEnergy(), upperLeftCorner);

    int currentX = -1;
    int currentY = -1;

    RowIterator rowIterator = new RowIterator(upperLeftCorner);
    Pixel upperRightCorner = new BorderPixel();
    while (rowIterator.hasNext()) {
      currentX += 1;
      upperRightCorner = rowIterator.next();
    }

    int tempY = -1;
    ColumnIterator columnIterator = new ColumnIterator(upperRightCorner);
    Pixel currentStartingPixel = new BorderPixel();
    while (columnIterator.hasNext()) {
      tempY += 1;
      Pixel currentPixel = columnIterator.next();
      if (currentPixel.getCostMatrixEnergy() < currentStartingPixel.getCostMatrixEnergy()) {
        currentStartingPixel = currentPixel;
        currentY = tempY;
      }
    }

    Seam seam = new HorizontalSeam();
    seam.add(currentStartingPixel, new Coordinate(currentX, currentY));

    for (int row = imageWidth - 2; row >= 0; row -= 1) {
      Pixel upperLeftPixel = currentStartingPixel.getUpperLeftPixel();
      Pixel leftPixel = currentStartingPixel.getLeftPixel();
      Pixel lowerLeftPixel = currentStartingPixel.getLowerLeftPixel();

      double upperLeftEnergy = upperLeftPixel.getCostMatrixEnergy();
      double leftEnergy = leftPixel.getCostMatrixEnergy();
      double lowerLeftEnergy = lowerLeftPixel.getCostMatrixEnergy();

      double minEnergy = Math.min(upperLeftEnergy, Math.min(leftEnergy, lowerLeftEnergy));

      if (Double.compare(minEnergy, upperLeftEnergy) == 0) {
        currentY -= 1;
        currentStartingPixel = upperLeftPixel;
      }
      else if (Double.compare(minEnergy, lowerLeftEnergy) == 0) {
        currentY += 1;
        currentStartingPixel = lowerLeftPixel;
      } else {
        currentStartingPixel = leftPixel;
      }
      currentX -= 1;
      seam.add(currentStartingPixel, new Coordinate(currentX, currentY));
    }
    return seam;
  }

  private Seam findMinimumHorizontalSeam() {
    return findMinimumHorizontalSeam(upperLeftCorner);
  }

  @Override
  public void resize(int newWidth, int newHeight) {
    if (newWidth < 1) {
      throw new IllegalArgumentException("Given new width can't be less than 1 pixel");
    }
    else if (newHeight < 1) {
      throw new IllegalArgumentException("Given new width can't be less than 1 pixel");
    }

    // Downsize then upsize
    while (imageWidth > newWidth || imageHeight > newHeight) {
      if (imageWidth > newWidth && imageHeight > newHeight) {
        // Remove which ever seam removes less average energy
        Seam verticalSeam = findMinimumVerticalSeam();
        Seam horizontalSeam = findMinimumHorizontalSeam();
        if (verticalSeam.getAverageEnergy() < horizontalSeam.getAverageEnergy()) {
          removeSeam(verticalSeam);
        }
        else {
          removeSeam(horizontalSeam);
        }
      }
      else if (imageWidth > newWidth) {
        removeSeam(findMinimumVerticalSeam());
      }
      else if (imageHeight > newHeight) {
        removeSeam(findMinimumHorizontalSeam());
      }
    }

    while (imageWidth < newWidth || imageHeight < newHeight) {
      if (imageWidth < newWidth) {
        Pixel copiedUpperLeftCorner = copyCurrentImage();
        int widthDifference = newWidth - imageWidth;
        Coordinate[][] coordinatesToAdd = new Coordinate[widthDifference][];
        SeamAdjuster removalSeamAdjuster = new DefaultSeamAdjuster(imageWidth);
        for (int i = 0; i < widthDifference; i += 1) {
          Seam toAdd = findMinimumVerticalSeam(copiedUpperLeftCorner);
          toAdd.remove();
          Coordinate[] currentCoordinates = toAdd.getCoordinates();
          removalSeamAdjuster.adjustCoordinatesByXInclusive(currentCoordinates);
          coordinatesToAdd[i] = currentCoordinates;
        }
        insertVerticalCoordinates(coordinatesToAdd);
      }
      else if (imageHeight < newHeight) {
        Pixel copiedUpperLeftCorner = copyCurrentImage();
        int heightDifference = newHeight - imageHeight;
        Coordinate[][] coordinatesToAdd = new Coordinate[heightDifference][];
        SeamAdjuster removalSeamAdjuster = new DefaultSeamAdjuster(imageHeight);
        for (int i = 0; i < heightDifference; i += 1) {
          Seam toAdd = findMinimumHorizontalSeam(copiedUpperLeftCorner);
          toAdd.remove();
          Coordinate[] currentCoordinate = toAdd.getCoordinates();
          removalSeamAdjuster.adjustCoordinatesByYInclusive(currentCoordinate);
          coordinatesToAdd[i] = currentCoordinate;
        }
        insertHorizontalCoordinates(coordinatesToAdd);
      }
    }
  }

  private void insertVerticalCoordinates(Coordinate[]... coordinatesToAdd) {
    SeamAdjuster upscalingSeamAdjuster = new DefaultSeamAdjuster(imageWidth);
    for (Coordinate[] coordinates : coordinatesToAdd) {
      if (coordinatesToAdd.length > 1) {
        upscalingSeamAdjuster.adjustCoordinatesByXExclusive(coordinates);
      }

      Pixel prevLeft = new BorderPixel();
      Pixel prevMiddle = new BorderPixel();
      Pixel prevRight = new BorderPixel();
      int previousX = -1;
      Pixel currentLeft = new BorderPixel();
      Pixel currentMiddle = new BorderPixel();
      Pixel currentRight = new BorderPixel();

      for (int i = 0; i < coordinates.length; i += 1) {
        Coordinate coordinate = coordinates[i];
        int x = coordinate.getX();
        int y = coordinate.getY();

        if (i == 0) {
          currentLeft = getPixel(x, y);
          currentMiddle = currentLeft.createPixelWithRight();
          currentRight = currentLeft.getRightPixel();
        }
        else if (x == previousX - 1) {
          currentLeft = prevLeft.getLowerLeftPixel();
          currentMiddle = currentLeft.createPixelWithRight();
          currentRight = prevRight.getLowerLeftPixel();

          prevMiddle.setBelowPixel(currentRight);
          currentRight.setAbovePixel(prevMiddle);
          prevLeft.setBelowPixel(currentMiddle);
          currentMiddle.setAbovePixel(prevLeft);
        }
        else if (x == previousX + 1) {
          currentLeft = prevLeft.getLowerRightPixel();
          currentMiddle = currentLeft.createPixelWithRight();
          currentRight = prevRight.getLowerRightPixel();

          prevMiddle.setBelowPixel(currentLeft);
          currentLeft.setAbovePixel(prevMiddle);
          prevRight.setBelowPixel(currentMiddle);
          currentMiddle.setAbovePixel(prevRight);
        }
        else {
          currentLeft = prevLeft.getBelowPixel();
          currentMiddle = currentLeft.createPixelWithRight();
          currentRight = prevRight.getBelowPixel();

          prevMiddle.setBelowPixel(currentMiddle);
          currentMiddle.setAbovePixel(prevMiddle);
        }
        currentLeft.setRightPixel(currentMiddle);
        currentMiddle.setLeftPixel(currentLeft);
        currentMiddle.setRightPixel(currentRight);
        currentRight.setLeftPixel(currentMiddle);

        prevLeft = currentLeft;
        prevMiddle = currentMiddle;
        prevRight = currentRight;
        previousX = x;
      }
      imageWidth += 1;
      storeCurrentState();
    }
  }

  private void insertHorizontalCoordinates(Coordinate[]... coordinatesToAdd) {
    SeamAdjuster upscalingSeamAdjuster = new DefaultSeamAdjuster(imageHeight);
    for (Coordinate[] coordinates : coordinatesToAdd) {
      if (coordinatesToAdd.length > 1) {
        upscalingSeamAdjuster.adjustCoordinatesByYExclusive(coordinates);
      }

      Pixel prevAbove = new BorderPixel();
      Pixel prevMiddle = new BorderPixel();
      Pixel prevBelow = new BorderPixel();
      int prevY = -1;
      Pixel curAbove = new BorderPixel();
      Pixel curMiddle = new BorderPixel();
      Pixel curBelow = new BorderPixel();

      for (int i = 0; i < coordinates.length; i += 1) {
        Coordinate coordinate = coordinates[i];
        int x = coordinate.getX();
        int y = coordinate.getY();

        if (i == 0) {
          curAbove = getPixel(x, y);
          curMiddle = curAbove.createPixelWithBelow();
          curBelow = curAbove.getBelowPixel();
        }
        else if (y == prevY - 1) {
          curAbove = prevAbove.getUpperRightPixel();
          curMiddle = curAbove.createPixelWithBelow();
          curBelow = prevBelow.getRightPixel().getAbovePixel();

          prevAbove.setRightPixel(curMiddle);
          curMiddle.setLeftPixel(prevAbove);
          prevMiddle.setRightPixel(curBelow);
          curBelow.setLeftPixel(prevMiddle);
        }
        else if (y == prevY + 1) {
          curAbove = prevAbove.getRightPixel().getBelowPixel();
          curMiddle = curAbove.createPixelWithBelow();
          curBelow = prevBelow.getLowerRightPixel();

          prevMiddle.setRightPixel(curAbove);
          curAbove.setLeftPixel(prevMiddle);
          prevBelow.setRightPixel(curMiddle);
          curMiddle.setLeftPixel(prevBelow);
        }
        else {
          curAbove = prevAbove.getRightPixel();
          curMiddle = curAbove.createPixelWithBelow();
          curBelow = prevBelow.getRightPixel();

          prevMiddle.setRightPixel(curMiddle);
          curMiddle.setLeftPixel(prevMiddle);
        }
        curAbove.setBelowPixel(curMiddle);
        curMiddle.setAbovePixel(curAbove);
        curMiddle.setBelowPixel(curBelow);
        curBelow.setAbovePixel(curMiddle);

        prevAbove = curAbove;
        prevMiddle = curMiddle;
        prevBelow = curBelow;
        prevY = y;
      }
      imageHeight += 1;
      storeCurrentState();
    }
  }

  private void removeSeam(Seam toRemove) {
    if (toRemove == null) {
      throw new IllegalArgumentException("Given seam can't be null!");
    }
    else if (toRemove.isVerticalSeam()) {
      imageWidth -= 1;
    }
    else {
      imageHeight -= 1;
    }
    toRemove.remove();
    storeCurrentState();
  }

  @Override
  public void resize(int newWidth, int newHeight, Mask areaToProtect) {
    applyMask(areaToProtect, DefaultSeamManipulator.maskValue);
    resize(newWidth, newHeight);
  }

  @Override
  public void removeArea(Mask areaToRemove) {
    applyMask(areaToRemove, -DefaultSeamManipulator.maskValue);

    int horzToRemove = areaToRemove.getMaxX() - areaToRemove.getMinX() + 1;
    int vertToRemove = areaToRemove.getMaxY() - areaToRemove.getMinY() + 1;

    BooleanSupplier hasMask = () -> {
        for (Pixel pixel : this) {
          if (pixel.isMask()) {
            return true;
          }
        }
        return false;
    };

    while (hasMask.getAsBoolean()) {
      Seam seam;
      if (horzToRemove > vertToRemove) {
        seam = findMinimumHorizontalSeam();
        imageHeight -= 1;
        horzToRemove -= 1;
      }
      else {
        seam = findMinimumVerticalSeam();
        imageWidth -= 1;
        vertToRemove -= 1;
      }
      seam.remove();
      storeCurrentState();
    }
  }

  @Override
  public void replaceArea(Mask areaToRemove) {
    int startingWidth = imageWidth;
    int startingHeight = imageHeight;
    removeArea(areaToRemove);
    resize(startingWidth, startingHeight);
  }

  @Override
  public BufferedImage getCurrentImage() {
    RowColumnIterator rowColumnIterator = new RowColumnIterator(upperLeftCorner);
    BufferedImage toReturn = new BufferedImage(imageWidth, imageHeight, BufferedImageType);
    while (rowColumnIterator.hasNext()) {
      int x = rowColumnIterator.getX();
      int y = rowColumnIterator.getY();
      Pixel currentPixel = rowColumnIterator.next();
      Color currentColor = currentPixel.getColor();
      toReturn.setRGB(x, y, currentColor.getRGB());
    }
    return toReturn;
  }

  @Override
  public BufferedImage getCurrentEnergyMap() {
    computeEnergyMap(upperLeftCorner);
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
    computeEnergyMap(upperLeftCorner);
    computeHorizontalCostMatrix(new HorizontalEnergy(), upperLeftCorner);
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

  private void storeCurrentState() {
    if (record) {
      previousStates.add(getCurrentImage());
    }
  }

  @Override
  public void saveCurrentImage(Path filePath) throws IOException {
    validFilePath(filePath.getParent());
    ImageIO.write(getCurrentImage(), "png", filePath.toFile());
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
    if (!record) {
      throw new IllegalArgumentException("This seam manipulator has been set not to record!");
    }

    validFilePath(filePath.getParent());
    SeekableByteChannel out = null;

    BufferedImage start = previousStates.get(0);
    BufferedImage end = previousStates.get(previousStates.size() - 1);
    int widthToUse = Math.max(start.getWidth(), end.getWidth());
    int heightToUse = Math.max(start.getHeight(), end.getHeight());

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
