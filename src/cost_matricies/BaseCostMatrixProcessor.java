package cost_matricies;

import pixel.BorderPixel;
import pixel.Pixel;

/**
 * Abstract implementation of {@link CostMatrixProcessor} interface which provides universal support
 * for determining a valid Pixel
 */
public abstract class BaseCostMatrixProcessor implements CostMatrixProcessor {

  /**
   * Determines if given {@link Pixel} is a valid Pixel by checking that it is non-null and not a
   * {@link BorderPixel}. If either is true, throws an exception.
   * @param pixel Pixel to verify
   * @throws IllegalArgumentException if given Pixel is null or a BorderPixel
   */
  protected void validPixel(Pixel pixel) throws IllegalArgumentException {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    else if (pixel.isBorderPixel()) {
      throw new IllegalArgumentException("Given pixel can't be a border pixel!");
    }
  }
}
