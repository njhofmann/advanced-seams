package cost_matricies;

import pixel.BorderPixel;
import pixel.Pixel;
import seam_manipulators.SeamManipulator;

/**
 * Abstract representation of a cost matrix for image being processed by a {@link SeamManipulator}.
 * Given a {@link Pixel}, processes it by computing and assigning a new cost matrix energy to the
 * Pixel based on the Pixel's surrounding it - as per this {@link CostMatrixProcessor}'s
 * definition of cost matrix and its energy. Processes a Pixel horizontally or vertically.
 */
public interface CostMatrixProcessor {

  /**
   * Computes a new energy value for a {@link Pixel} horizontally and reassigns it with
   * {@link Pixel#setCostMatrixEnergy(double)}. Considers Pixels around given Pixel left to right
   * for energy computation.
   * @param pixel Pixel to compute and assign energy to
   * @throws IllegalArgumentException if given Pixel is null or a {@link BorderPixel}.
   */
  void computeHorizontally(Pixel pixel);

  /**
   * Computes a new energy value for a {@link Pixel} vertically and reassigns it with
   * {@link Pixel#setCostMatrixEnergy(double)}. Considers Pixels around given Pixel top to bottom
   * for energy computation.
   * @param pixel Pixel to compute and assign energy to
   * @throws IllegalArgumentException if given Pixel is null or a {@link BorderPixel}.
   */
  void computeVertically(Pixel pixel);
}
