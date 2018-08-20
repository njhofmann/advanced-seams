package masks;

import seamutilities.utilities.Coordinate;

/**
 * Represents a series of coordinates to "mask" upon a given image, usually to be targeted for some
 * purpose irrelevant to this Mask - likely object protection or deletion by a content aware
 * image resizer.
 */
public interface Mask {

  /**
   * Returns an array of all the coordinates that are apart of this masks.Mask.
   * @return array of all coordinates
   */
  Coordinate[] getCoordinates();
}
