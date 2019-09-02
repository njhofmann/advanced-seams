package arg_parsing;

import cost_matricies.CostMatrixProcessor;
import energy_maps.EnergyMapMaker;
import java.io.IOException;
import java.nio.file.Path;
import seam_manipulators.DefaultSeamManipulator;
import seam_manipulators.SeamManipulator;
import seam_manipulators.TestSeamManipulator;

/**
 * Implementation of {@link SeamManipulatorFactory} for creating {@link TestSeamManipulator}s, used
 * solely for testing.
 */
public class TestSeamManipulatorFactory implements SeamManipulatorFactory{

  @Override
  public SeamManipulator create(Path inputFilePath, EnergyMapMaker energyMapMaker,
      CostMatrixProcessor costMatrixProcessor, boolean record) throws IOException {
    // TODO implement TestSM
    return null;
  }
}
