package masks;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import seamutilities.utilities.Coordinate;

/**
 * The default Mask to use when making Masks.
 */
public class DefaultMask implements Mask {

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
   * white - or pixels with colors (255, 255, 255) in the sRGB color space. Assigns all white
   * pixels as this DefaultMask's associated coordinates.
   *
   * @param maskPath
   * @throws IllegalArgumentException if given {@param maskPath} is null or doesn't exist
   * @throws IOException if the path to the given image can't be read.
   */
  public DefaultMask(Path maskPath) {
    if (maskPath == null) {
      throw new IllegalArgumentException("Given path can't be null!");
    }
    else if (Files.notExists(maskPath)) {
      throw new IllegalArgumentException("Given path doesn't exist!");
    }

    try {
      BufferedImage toRead = ImageIO.read(maskPath.toFile());
      List<Coordinate> tempCoors = new ArrayList<>();

      for (int row = 0; row < toRead.getHeight(); row += 1) {
        for (int column = 0; column < toRead.getWidth(); column += 1) {
          Color currentColor = new Color(toRead.getRGB(column, row));

          if (currentColor.equals(Color.WHITE)) {
            tempCoors.add(new Coordinate(column, row));
          }

        }
      }
      coordinates = new Coordinate[tempCoors.size()];
      for (int posn = 0; posn < coordinates.length; posn += 1) {
        coordinates[posn] = tempCoors.get(posn);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Coordinate[] getCoordinates() {
    return coordinates;
  }
}
