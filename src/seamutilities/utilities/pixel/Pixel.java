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
  void setCoordinate(Coordinate newCoordinate) throws IllegalArgumentException;

  /**
   * Retrieves the current color of this Pixel.
   * @return current color of this Pixel
   */
  Color getColor();

  /**
   * Assigns the given Color to this Pixel.
   *
   * @param newColor new Color to assign this Pixel
   * @throws IllegalArgumentException if given Color is null
   */
  void assignColor(Color newColor) throws IllegalArgumentException;

  /**
   * Assigns a new energy value to this Pixel of this Pixel, usually called whenever its
   * neighboring pixels change.
   */
  void assignEnergy(double newEnergy);

  /**
   * Retrieves this Pixel's last computed energy value.
   * @return this Pixel's current energy value
   */
  double getEnergy();
}
