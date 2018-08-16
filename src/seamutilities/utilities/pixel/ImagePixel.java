package seamutilities.utilities.pixel;

import java.awt.Color;
import seamutilities.utilities.Coordinate;

/**
 * Represents a pixel on some image currently being seam carved - contains the pixel's coordinate
 * location, color, last computed energy (in relation to its surrounding pixels), and the pixels
 * surrounding it.
 */
public class ImagePixel implements Pixel {

  /**
   * This ImagePixel's current color - in the sRGB color space.
   */
  private final Color color;

  /**
   * This ImagePixel's current coordinate - or its x, y location on its associated image.
   */
  private Coordinate coordinate;

  /**
   * This ImagePixel's current energy - or value in relation to the pixels around it.
   */
  private float energy = 0;

  public ImagePixel(Color color, Coordinate coordinate) {
    if (color == null) {
      throw new IllegalArgumentException("Given color can't be null!");
    }
    else if (coordinate == null) {
      throw new IllegalArgumentException("Given coordinate can't be null!");
    }

    this.color = color;
  }

  @Override
  public Coordinate getCoordinate() {
    return coordinate;
  }

  @Override
  public void setCoordinate(Coordinate newCoordinate) {
    if (newCoordinate == null) {
      throw new IllegalArgumentException("Given coordinate can't be null!");
    }
    this.coordinate = newCoordinate;
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public void assignEnergy(float newEnergy) {
    this.energy = newEnergy;
  }

  @Override
  public float getEnergy() {
    return energy;
  }
}
