package costmatricies.horizontal;

import costmatricies.horizontal.HorizontalCostMatrix;
import pixel.Pixel;

public class HorizontalEnergy implements HorizontalCostMatrix {

  @Override
  public void compute(Pixel pixel) {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    else if (pixel.isBorderPixel()) {
      throw new IllegalArgumentException("Given pixel can't be a border pixel!");
    }

    double upperLeftEnergy = pixel.getUpperLeftPixel().getCostMatrixEnergy();
    double leftEnergy = pixel.getLeftPixel().getCostMatrixEnergy();
    double lowerLeftEnergy = pixel.getLowerLeftPixel().getCostMatrixEnergy();

    pixel.setCostMatrixEnergy(pixel.getEnergyMapEnergy() + Math.min(upperLeftEnergy,
        Math.min(leftEnergy, lowerLeftEnergy)));
  }
}
