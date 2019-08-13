package cost_matricies;

import pixel.BorderPixel;
import pixel.Pixel;
import seam_manipulators.SeamManipulator;

/**
 * Abstract representation of a cost matrix for image being processed by a {@link SeamManipulator}.
 * Given a {@link Pixel}, processes it by computing and assigning a new cost matrix energy to the
 * Pixel based on the Pixel's surrounding it - as per this {@link CostMatrixProcessor}'s
 * definition of cost matrix and its energy.
 *
 * Is either horizontal or vertical, depending on the direction it bases a Pixel's energy on.
 */
public interface CostMatrixProcessor {

  /**
   * Computes a new energy value for a {@link Pixel} and assigns it with
   * {@link Pixel#setCostMatrixEnergy(double)}
   * @param pixel Pixel to work on
   * @throws IllegalArgumentException if given Pixel is null or a {@link BorderPixel}.
   */
  void compute(Pixel pixel);

  /**
   * Signals if this {@link CostMatrixProcessor} is a horizontal cost matrix, one that computes
   * energies left to right on a Pixel. Should return the negation of
   * {@link CostMatrixProcessor#isVertical()}.
   * @return if CostMatrix is horizontal
   */
  boolean isHorizontal();

  /**
   * Signals if this {@link CostMatrixProcessor} is a vertical cost matrix, one that computes energies
   * top to bottom on a Pixel Should return the negation of
   * {@link CostMatrixProcessor#isHorizontal()}.
   * @return if CostMatrix is vertical
   */
  boolean isVertical();
}
