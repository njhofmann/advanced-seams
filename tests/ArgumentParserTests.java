import arg_parsing.ArgumentParser;
import arg_parsing.TestSeamManipulatorFactory;
import org.junit.jupiter.api.BeforeEach;
import seam_manipulators.SeamManipulator;
import seam_manipulators.TestSeamManipulator;

/**
 * Tests verifying that inputs given to {@link ArgumentParser} give the desired outcome using a
 * mock implementation of {@link SeamManipulator}, a {@link TestSeamManipulator}.
 */
public class ArgumentParserTests {

  ArgumentParser argumentParser;

  @BeforeEach
  public void init() {
    argumentParser = new ArgumentParser(new TestSeamManipulatorFactory());
  }

}
