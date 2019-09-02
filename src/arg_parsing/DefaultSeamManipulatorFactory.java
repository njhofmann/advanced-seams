package arg_parsing;

import cost_matricies.CostMatrixProcessor;
import energy_maps.EnergyMapMaker;
import java.io.IOException;
import java.nio.file.Path;
import seam_manipulators.DefaultSeamManipulator;
import seam_manipulators.SeamManipulator;

/**
 * Implementation of {@link SeamManipulatorFactory} for creating {@link DefaultSeamManipulator}s.
 */
public class DefaultSeamManipulatorFactory implements SeamManipulatorFactory {

  @Override
  public SeamManipulator create(Path inputFilePath, EnergyMapMaker energyMapMaker,
      CostMatrixProcessor costMatrixProcessor, boolean record) throws IOException {
    return new DefaultSeamManipulator(inputFilePath, energyMapMaker, costMatrixProcessor, record);
  }
}
