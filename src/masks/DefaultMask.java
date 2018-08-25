package masks;

import java.awt.Color;
import utility.Coordinate;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * The default Mask to use when making Masks.
 */
public class DefaultMask implements Mask {

  private final int maxX;

  private final int minX;

  private final int maxY;

  private final int minY;

  /**
   * The array of Coordinates this Mask contains.
   */
  private Coordinate[] coordinates;

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
          + "less than the given lower right x coordiante!");
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

    coordinates = new Coordinate[xDist * yDist];
    int curArrayPosn = 0;

    for (int row = upperLeftY; row <= lowerRightY; row += 1) {
      for (int column = upperLeftX; column <= lowerRightX; column += 1) {
        coordinates[curArrayPosn] = new Coordinate(column, row);
        curArrayPosn += 1;
      }
    }
  }

  /**
   * Constructor for this DefaultMask that a takes in a file path for an image consisting of only
   * white or black pixels - or pixels with colors (255, 255, 255) or (0, 0, 0) in the sRGB color
   * space. Assigns all white pixels as this DefaultMask's associated coordinates.
   *
   * @param maskPath file path to the mask to use
   * @throws IllegalArgumentException if given {@param maskPath} is null or doesn't exist, or if
   *                                  one of its pixel's isn't exactly white or black
   * @throws IOException if the path to the given image can't be read.
   */
  public DefaultMask(Path maskPath) throws IllegalArgumentException, IOException {
    if (maskPath == null) {
      throw new IllegalArgumentException("Given path can't be null!");
    }
    else if (Files.notExists(maskPath)) {
      throw new IOException("Given path doesn't exist!");
    }

    int tempMaxX, tempMinX, tempMaxY, tempMinY;
    BufferedImage toRead = ImageIO.read(maskPath.toFile());
    List<Coordinate> tempCoors = new ArrayList<>();

    tempMaxX = 0;
    tempMaxY = 0;
    tempMinX = toRead.getWidth() - 1;
    tempMinY = toRead.getHeight() - 1;

    for (int row = 0; row < toRead.getHeight(); row += 1) {
      for (int column = 0; column < toRead.getWidth(); column += 1) {
        Color currentColor = new Color(toRead.getRGB(column, row));

        if (currentColor.equals(Color.WHITE)) {
          tempCoors.add(new Coordinate(column, row));

          if (column > tempMaxX) {
            tempMaxX = column;
          }
          else if (column < tempMinX) {
            tempMinX = column;
          }

          if (row > tempMaxY) {
            tempMaxY = row;
          }
          else if (row < tempMinY) {
            tempMinY = row;
          }
        }
        /*
        else if (!currentColor.equals(Color.BLACK)) {
          throw new IllegalArgumentException("Given image can only contain exactly white or "
              + "black pixels!");
        }
        */
      }
    }
    coordinates = new Coordinate[tempCoors.size()];
    coordinates = tempCoors.toArray(coordinates);

    minX = tempMinX;
    maxX = tempMaxX;
    minY = tempMinY;
    maxY = tempMaxY;
  }


  @Override
  public Coordinate[] getCoordinates() {
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
