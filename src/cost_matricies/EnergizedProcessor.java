package cost_matricies;

import cost_matricies.BaseCostMatrixProcessor;
import cost_matricies.CostMatrixProcessor;
import java.util.LinkedList;
import pixel.Pixel;

public class EnergizedProcessor extends BaseCostMatrixProcessor
    implements CostMatrixProcessor {

  @Override
  public void computeHorizontally(Pixel pixel) {
    validPixel(pixel);
    double upperLeftEnergy = pixel.getUpperLeftPixel().getCostMatrixEnergy();
    double leftEnergy = pixel.getLeftPixel().getCostMatrixEnergy();
    double lowerLeftEnergy = pixel.getLowerLeftPixel().getCostMatrixEnergy();

    pixel.setCostMatrixEnergy(pixel.getEnergyMapEnergy() + Math.min(upperLeftEnergy,
        Math.min(leftEnergy, lowerLeftEnergy)));
  }

  @Override
  public void computeVertically(Pixel pixel) {
    validPixel(pixel);

    double upperLeftEnergy = pixel.getUpperLeftPixel().getCostMatrixEnergy();
    double upperCenterEnergy = pixel.getAbovePixel().getCostMatrixEnergy();
    double upperRightEnergy = pixel.getUpperRightPixel().getCostMatrixEnergy();

    pixel.setCostMatrixEnergy(pixel.getEnergyMapEnergy() +
        Math.min(upperLeftEnergy, Math.min(upperCenterEnergy, upperRightEnergy)));
  }
}
