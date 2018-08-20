package seamutilities.utilities.seams;

import java.awt.Color;
import java.util.List;
import seamutilities.utilities.pixel.Pixel;

/**
 * A horizontal or vertical collection of Pixels on an image, from top to bottom or from left to
 * right.
 */
public interface Seam {

  /**
   * Adds a Pixel to this Seam - given that it hasn't been added to this Seam before, and is
   * connected to the previously added Pixel (unless it is a brand new Seam with no Pixels added
   * before.
   *
   * @param toAdd Pixel to add to this Seam
   * @throws IllegalArgumentException if given Pixel was already added to this Seam, or if it can't
   *                                  be connected to the previously added Seam
   * */
  void addPixel(Pixel toAdd);

  /**
   * "Highlights" this Seam by turning all its Pixels to the given Color {@param toColor}.
   *
   * @param toColor color to change this Seam's Pixels to
   */
  void highlight(Color toColor);

  /**
   * Returns all this Seam's associated Pixels in the order they were added.
   *
   * @return all Pixels added to this Seam
   */
  List<Pixel> getPixels();

  /**
   * Returns this Seam's cumulative energy of all its associated Pixels.
   * @return Seam's cumulative energy
   */
  double getEnergy();
}
