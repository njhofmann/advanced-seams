package seamutilities.utilities.pixel;

import java.awt.Color;
import seamutilities.utilities.Coordinate;

/**
 * Represents a pixel that is apart of some image currently being seam carved - contains its
 * latest color, coordinate location, energy value, and if it is a border pixel.
 */
public interface Pixel {

  /**
   * Returns the coordinate value of this Pixel.
   * @return this Pixel's current coordinate value
   */
  Coordinate getCoordinate();

  /**
   * Assigns a new Coordinate location to this Pixel.
   *
   * @param newCoordinate new Coordinate to assign to this Pixel
   * @throws IllegalArgumentException if given new Coordinate is null
   */
  void setCoordinate(Coordinate newCoordinate);

  /**
   * Retrieves the current color of this Pixel.
   * @return current color of this Pixel
   */
  Color getColor();

  /**
   * Recomputes the energy of this Pixel, usually called whenever its neighboring pixels change.
   */
  void computeEnergy();

  /**
   * Retrieves this Pixel's last computed energy value.
   * @return this Pixel's current energy value
   */
  int getEnergy();

  /**
   * Returns if this Pixel is on the border surrounding its associate image - meant as a stand
   * in for pixels with neighbors on the border of an image.
   *
   * @return if this Pixel is a border pixel
   */
  boolean isBorderPixel();
}