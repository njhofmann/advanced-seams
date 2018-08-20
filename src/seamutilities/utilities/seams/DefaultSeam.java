package seamutilities.utilities.seams;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import seamutilities.utilities.pixel.Pixel;

/**
 * Default Seam implementation to use when using a Seam.
 */
public class DefaultSeam implements Seam {

  /**
   * The Pixels associated with this Seam, all Pixels that have so far been added to this Seam.
   */
  private final List<Pixel> pixels = new ArrayList<>();

  /**
   * The cumulative energy of this Seam so far, updated whenever a new Pixel is added.
   */
  private double energy = 0;

  @Override
  public void addPixel(Pixel toAdd) {
    if (toAdd == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    else if (pixels.contains(toAdd)) {
      throw new IllegalArgumentException("Given pixel has already been added to this seam!");
    }

    pixels.add(toAdd);
    energy += toAdd.getEnergy();
  }

  @Override
  public void highlight(Color toColor) {
    if (toColor == null) {
      throw new IllegalArgumentException("Given color can't be null!");
    }

    for (Pixel pixel : pixels) {
      pixel.assignColor(toColor);
    }
  }

  @Override
  public List<Pixel> getPixels() {
    return pixels;
  }

  @Override
  public double getEnergy() {
    return energy;
  }
}
