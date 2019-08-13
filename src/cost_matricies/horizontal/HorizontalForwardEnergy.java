package cost_matricies.horizontal;

import cost_matricies.BaseCostMatrixProcessor;
import cost_matricies.CostMatrixProcessor;
import pixel.Pixel;

public class HorizontalForwardEnergy extends BaseCostMatrixProcessor implements
    CostMatrixProcessor {

  public HorizontalForwardEnergy() {
    super(true);
  }

  @Override
  public void compute(Pixel pixel) {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }

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
