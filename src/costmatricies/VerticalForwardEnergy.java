package costmatricies;

import pixel.Pixel;

public class VerticalForwardEnergy implements VerticalCostMatrix {

  @Override
  public void compute(Pixel pixel) {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }

    double upperLeftCostMatrixEnergy = pixel.getUpperLeftPixel().getCostMatrixEnergy();
    double upperCenterCostMatrixEnergy = pixel.getAbovePixel().getCostMatrixEnergy();
    double upperRightCostMatrixEnergy = pixel.getUpperRightPixel().getCostMatrixEnergy();

    double leftPixelEnergy = pixel.getLeftPixel().getEnergyMapEnergy();
    double rightPixelEnergy = pixel.getRightPixel().getEnergyMapEnergy();
    double leftRightDifference = Math.abs(leftPixelEnergy - rightPixelEnergy);

    double aboveEnergy = pixel.getAbovePixel().getEnergyMapEnergy();

    double leftDifference = leftRightDifference + Math.abs(aboveEnergy - leftPixelEnergy);
    double upperLeft = upperLeftCostMatrixEnergy + leftDifference;

    double upperCenter = upperCenterCostMatrixEnergy + leftRightDifference;

    double rightDifference = leftRightDifference + Math.abs(aboveEnergy - rightPixelEnergy);
    double upperRight = upperRightCostMatrixEnergy + rightDifference;

    double energyToAdd = Math.min(upperLeft, Math.min(upperCenter, upperRight));
    pixel.setCostMatrixEnergy(pixel.getEnergyMapEnergy() + energyToAdd);
  }
}
