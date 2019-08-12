package masks;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import pixel.Pixel;
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

  private final int maxX, maxY, maxY, minY;

  public ImageMask(Path toMask) throws IllegalArgumentException, IOException {
    if (toMask == null) {
      throw new IllegalArgumentException("Given path can'be be null!");
    }
    else if (!Files.exists(toMask)) {
      throw new IllegalArgumentException("Given path to mask doesn't exist!");
    }

    Set<Coordinate> mockSet = new HashSet<>();
    BufferedImage maskImage = ImageIO.read(toMask.toFile());
    for (int row = 0; row < maskImage.getHeight(); row++) {
      for (int col = 0; col < maskImage.getWidth(); col++) {
        int currentRGB = maskImage.getRGB(col, row); // TODO how to decipher
      }
    }
    coordinates = Collections.unmodifiableSet(mockSet);
  }

  private boolean pixelIsWhite(int red, int green, int blue) {
    return inThreshold(red) && inThreshold(green) && inThreshold(blue);
  }

  private boolean inThreshold(int code) {
    return MinRGBThreshold <= code && code <= MaxRGBThreshold
  }

  public Coordinate[] getCoordinates() {
    return coordinates;
  }

  @Override
  public int getMaxX() {
    return 0;
  }

  @java.lang.Override
  public int getMinX() {
    return 0;
  }

  @java.lang.Override
  public int getMaxY() {
    return 0;
  }

  @java.lang.Override
  public int getMinY() {
    return 0;
  }
}
