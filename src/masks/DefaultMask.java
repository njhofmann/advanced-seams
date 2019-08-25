package masks;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import utility.Coordinate;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * The default implementation of the {@link Mask} interface, defines a Mask by a given rectangular
 * bounding box or  {@link Path} to an "image of a mask". Such a image should ideally be of the same
 * size as the image one is manipulating, and should only be black (0, 0, 0) and white
 * (255, 255, 255) - white for the part of the image to remove, black for the part to leave alone.
 * There are some "misreadings" when reading RGB codes of such images, so some leway is given in
 * interpreting what is "white".
 */
public class DefaultMask implements Mask {

  private static final int MaxRGBThreshold = 255;

  private static final int MinRGBThreshold = 250;

  /**
   * The maximum and minimum X and Y coordinates that make up the bounding box of this {@link Mask}.
   */
  private final int maxX, minX, maxY, minY;

  /**
   * The array of Coordinates this Mask contains.
   */
  private final Set<Coordinate> coordinates;

  /**
   * Constructor for this DefaultMask that assigns it all the coordinates within the bounds of a
   * given, hypothetical rectangular box - as per the x and y coordinates of both the upper left
   * corner and the lower right corner of said hypothetical box.
   *
   * @param upperLeftX x coordinate of the box's upper left corner
   * @param upperLeftY y coordinate of the box's upper left corner
   * @param lowerRightX x coordinate of the box's lower right corner
   * @param lowerRightY y coordinate of the box's lower right corner
   * @throws IllegalArgumentException if any of the four values are negative, or if any of the
   *                                  values in the upper left corner are equal to or greater than
   *                                  any of the associated values in the lower right corner - must
   *                                  have a gap of at least 1 between each x and y coordinate
   */
  public DefaultMask(int upperLeftX, int upperLeftY, int lowerRightX, int lowerRightY) {
    if (upperLeftX < 0 || upperLeftY < 0|| lowerRightX < 0 || lowerRightY < 0) {
      throw new IllegalArgumentException("Give coordinates can't must be positive numbers!");
    }
    else if (upperLeftX >= lowerRightX) {
      throw new IllegalArgumentException("Given upper left x coordinate must be at least 1 unit"
          + "less than the given lower right x coordinate!");
    }
    else if (upperLeftY >= lowerRightY) {
      throw new IllegalArgumentException("Given upper left y coordinate must be at least 1 unit"
          + "less than the given lower right y coordinate!");
    }

    maxX = lowerRightX;
    minX = upperLeftX;
    maxY = lowerRightY;
    minY = upperLeftY;

    int xDist = lowerRightX - upperLeftX + 1;
    int yDist = lowerRightY - upperLeftY + 1;

    Set<Coordinate> tempCoordinates = new HashSet<>();
    int curArrayPosn = 0;

    for (int row = upperLeftY; row <= lowerRightY; row += 1) {
      for (int column = upperLeftX; column <= lowerRightX; column += 1) {
        tempCoordinates.add(new Coordinate(column, row));
        curArrayPosn += 1;
      }
    }
    coordinates = Collections.unmodifiableSet(tempCoordinates);
  }

  public DefaultMask(Path toMask) throws IllegalArgumentException, IOException {
    if (toMask == null) {
      throw new IllegalArgumentException("Given path can'be be null!");
    }
    else if (!Files.exists(toMask)) {
      throw new IllegalArgumentException("Given path to mask doesn't exist!");
    }

    int mockMaxX = Integer.MIN_VALUE;
    int mockMaxY = Integer.MIN_VALUE;
    int mockMinX = Integer.MAX_VALUE;
    int mockMinY = Integer.MAX_VALUE;
    Set<Coordinate> mockSet = new HashSet<>();
    BufferedImage maskImage = ImageIO.read(toMask.toFile());
    for (int row = 0; row < maskImage.getHeight(); row++) {
      for (int col = 0; col < maskImage.getWidth(); col++) {
        Color currentColor = new Color(maskImage.getRGB(col, row));
        if (pixelIsWhite(currentColor)) {
          mockSet.add(new Coordinate(col, row));
          mockMaxX = Math.max(mockMaxX, col);
          mockMaxY = Math.max(mockMaxY, row);
          mockMinX = Math.min(mockMinX, col);
          mockMinY = Math.min(mockMinY, row);
        }
      }
    }
    maxX = mockMaxX;
    maxY = mockMaxY;
    minX = mockMinX;
    minY = mockMinY;
    coordinates = Collections.unmodifiableSet(mockSet);
  }


  /**
   * Returns if the given {@link Color} is "close enough" to white to be added to this mask's set
   * of {@link Coordinate}s.
   * @param color color to test
   * @return if Color is close enough to white
   * @throws IllegalArgumentException if given Color is null
   */
  private boolean pixelIsWhite(Color color) {
    if (color == null) {
      throw new IllegalArgumentException("Given color can't be null!");
    }
    return inThreshold(color.getRed()) && inThreshold(color.getGreen()) && inThreshold(color.getBlue());
  }

  /**
   * Returns if the given band of that makes up a color is within the denoted thresholds for what
   * makes a color "white".
   * @param band band of color to check
   * @return if band is in threshold
   */
  private boolean inThreshold(int band) {
    return MinRGBThreshold <= band && band <= MaxRGBThreshold;
  }

  @Override
  public Set<Coordinate> getCoordinates() {
    return coordinates;
  }

  public int getMaxX() {
    return maxX;
  }

  public int getMinX() {
    return minX;
  }

  public int getMaxY() {
    return maxY;
  }

  public int getMinY() {
    return minY;
  }
}
