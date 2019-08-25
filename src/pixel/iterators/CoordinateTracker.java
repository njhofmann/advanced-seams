package pixel.iterators;

import java.util.Iterator;
import pixel.Pixel;

/**
 * Extension of a {@link Iterator} over a group of {@link Pixel}s, allows for the calling of the
 * coordinate position of the Pixel currently being iterated over.
 */
public interface CoordinateTracker extends Iterator<Pixel> {

  /**
   * Returns the X coordinate of the position of the {@link Pixel} currently being iterated over by
   * this Pixel Iterator.
   * @return X coordinate of currently iterated Pixel
   */
  int getX();

  /**
   * Returns the Y coordinate of the position of the {@link Pixel} currently being iterated over by
   * this Pixel Iterator.
   * @return Y coordinate of currently iterated Pixel
   */
  int getY();

}
