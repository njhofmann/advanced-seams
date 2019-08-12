package masks;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import utility.Coordinate;
import java.nio.file.Path;

/**
 * Implementation of the {@link Mask} interface, provides for the creation of a mask via a
 * {@link Path} to an "image of a mask". Such a image should ideally be of the same size as the
 * image one is manipulating, and should only be black (0, 0, 0) and white (255, 255, 255) - white
 * white for the part of the image to remove, black for the part to leave alone. There are some
 * "misreadings" when reading RGB codes of such images, so some leway is given in interpreting what
 * is "white".
 */
public class ImageMask implements Mask {

  private static final int MaxRGBThreshold = 255;

  private static final int MinRGBThreshold = 250;

  private final Set<Coordinate> coordinates;

  private final int maxX, maxY, minX, minY;

  public ImageMask(Path toMask) throws IllegalArgumentException, IOException {
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

  @Override
  public int getMaxX() {
    return maxX;
  }

  @Override
  public int getMinX() {
    return minX;
  }

  @Override
  public int getMaxY() {
    return maxY;
  }

  @Override
  public int getMinY() {
    return minY;
  }
}
