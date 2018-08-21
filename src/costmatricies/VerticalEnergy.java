package costmatricies;

import pixel.Pixel;

public class VerticalEnergy implements VerticalCostMatrix {

  @Override
  public void compute(Pixel pixel) {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    else if (pixel.isBorderPixel()) {
      throw new IllegalArgumentException("Given pixel can't be a border pixel!");
    }

    double upperLeftEnergy = pixel.getUpperLeftPixel().getCostMatrixEnergy();
    double upperCenterEnergy = pixel.getAbovePixel().getCostMatrixEnergy();
    double upperRightEnergy = pixel.getUpperRightPixel().getCostMatrixEnergy();

    pixel.setCostMatrixEnergy(pixel.getEnergyMapEnergy() + Math.min(upperLeftEnergy,
        Math.min(upperCenterEnergy, upperRightEnergy)));
  }
}
