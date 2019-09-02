package arg_parsing;

import cost_matricies.CostMatrixProcessor;
import energy_maps.EnergyMapMaker;
import java.io.IOException;
import java.nio.file.Path;
import seam_manipulators.SeamManipulator;

/**
 * Factory for creating a {@link SeamManipulator} for from different sets of input arguments.
 * Created to allow for the usage of {@link ArgumentParser} with different SeamManipulator
 * implementations. Different implementations of this interface equate to different SeamManipulator
 * implementations.
 */
public interface SeamManipulatorFactory {

  /**
   * Creates a {@link SeamManipulator} from a given input file {@link Path}, a
   * {@link EnergyMapMaker}, a {@link CostMatrixProcessor}, and a flag to record the seam carving
   * process.
   * @param inputFilePath path to starting image to use for the seam carving process
   * @param energyMapMaker energy map method to use in seam carving process
   * @param costMatrixProcessor cost matrix algorithm to use in the seam carving process
   * @param record boolean flag to record to
   * @return fully usable {@link SeamManipulator} instance
   * @throws IOException if there is an issue reading from or writing to any given image
   */
  SeamManipulator create(Path inputFilePath, EnergyMapMaker energyMapMaker,
      CostMatrixProcessor costMatrixProcessor, boolean record) throws IOException;

}
