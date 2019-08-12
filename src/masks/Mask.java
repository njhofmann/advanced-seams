package masks;

import java.util.Set;
import utility.Coordinate;

/**
 * Represents a series of coordinates to "mask" upon a given image, usually to be targeted for some
 * purpose irrelevant to this Mask - likely object protection or deletion by a content aware
 * image resizer.
 */
public interface Mask {

  /**
   * Returns an array of all the coordinates that are apart of this {@link Mask}.
   * @return set of all coordinates
   */
  Set<Coordinate> getCoordinates();

  /**
   * Return the "furthest" or "min" X coordinate present in this {@link Mask}.
   * @return max X coordinate in this Mask
   */
  int getMaxX();

  /**
   * Return the "closest" or "min" X coordinate present in this {@link Mask}.
   * @return max X coordinate in this Mask
   */
  int getMinX();

  /**
   * Return the "furthest" or "max" Y coordinate present in this {@link Mask}.
   * @return max Y coordinate in this Mask
   */
  int getMaxY();

  /**
   * Return the "closest" or "min" X coordinate present in this {@link Mask}.
   * @return max Y coordinate in this Mask
   */
  int getMinY();
}
