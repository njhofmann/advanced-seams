package seamutilities.utilities.energymaps;

import seamutilities.utilities.ImageMatrix.ImageMatrix;

/**
 * An EnergyMap that computes energy map for a given ImageMatrix - or the energy for every Pixel
 * in an ImageMatrix.
 */
public interface EnergyMap {

  /**
   * Given an ImageMatrix, computes the energy for every Pixel inside the ImageMatrix.
   *
   * @param imageMatrix ImageMatrix this EnergyMap computes on
   * @return the largest computed energy value, used for outputting the energy map of the given
   *         ImageMatrix as an image
   * @throws IllegalArgumentException if given ImageMatrix is null
   */
  double computeEnergyMap(ImageMatrix imageMatrix);
}
