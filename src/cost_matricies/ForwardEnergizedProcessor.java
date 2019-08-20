package cost_matricies;

import cost_matricies.BaseCostMatrixProcessor;
import cost_matricies.CostMatrixProcessor;
import pixel.Pixel;

public class ForwardEnergizedProcessor extends BaseCostMatrixProcessor
    implements CostMatrixProcessor {

  @Override
  public void computeVertically(Pixel pixel) {
    validPixel(pixel);

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

  @Override
  public void computeHorizontally(Pixel pixel) {
    validPixel(pixel);

    double lowerRightCostMatrixEnergy = pixel.getLowerRightPixel().getCostMatrixEnergy();
    double leftCostMatrixEnergy = pixel.getLeftPixel().getCostMatrixEnergy();
    double upperLeftCostMatrixEnergy = pixel.getUpperLeftPixel().getCostMatrixEnergy();

    double belowEnergy = pixel.getBelowPixel().getEnergyMapEnergy();
    double aboveEnergy = pixel.getAbovePixel().getEnergyMapEnergy();
    double aboveBelowDifference = Math.abs(belowEnergy - aboveEnergy);

    double leftEnergy = pixel.getLeftPixel().getEnergyMapEnergy();

    double leftDifference = aboveBelowDifference + Math.abs(leftEnergy - belowEnergy);
    double lowerRight = lowerRightCostMatrixEnergy + leftDifference;

    double left = leftCostMatrixEnergy + aboveBelowDifference;

    double rightDifference = aboveBelowDifference + Math.abs(leftEnergy - aboveEnergy);
    double upperLeft = upperLeftCostMatrixEnergy + rightDifference;

    double energyToAdd = Math.min(lowerRight, Math.min(left, upperLeft));
    pixel.setCostMatrixEnergy(pixel.getEnergyMapEnergy() + energyToAdd);
  }
}
