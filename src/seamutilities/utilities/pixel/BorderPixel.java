package seamutilities.utilities.pixel;

import java.awt.Color;
import seamutilities.utilities.Coordinate;

/**
 * Represents a pixel bordering an image - default color is black, energy is always 0, and has
 * no coordinate location. Throws an {@code UnsupportedOperationException} on any method involving
 * coordinates.
 */
public class BorderPixel implements Pixel {

  /**
   * Universal method signalling that a method call involving a Coordinates is unsupported.
   */
  private final static UnsupportedOperationException noCoordinate
      = new UnsupportedOperationException("A border pixel has no coordinate!");

  @Override
  public Coordinate getCoordinate() {
    throw BorderPixel.noCoordinate;
  }

  @Override
  public void setCoordinate(Coordinate newCoordinate) {
    throw BorderPixel.noCoordinate;
  }

  @Override
  public Color getColor() {
    return Color.BLACK;
  }

  @Override
  public void computeEnergy() {
    // Has nothing to compute
  }

  @Override
  public int getEnergy() {
    return 0;
  }

  @Override
  public boolean isBorderPixel() {
    return true;
  }
}
