import arg_parsing.ArgumentParser;
import arg_parsing.DefaultSeamManipulatorFactory;
import java.io.IOException;

/**
 * Point of entry to run the SeamUtilities as a program.
 */
public class RunSeamUtilities {

  /**
   * Point of entry to run this SeamUtilities as a program, user entered arguments are parsed by
   * {@link ArgumentParser} to execute desired behavior.
   * @param args arguments to parse
   * @throws IOException if there is a issue reading from or writing to one or more of the given
   * paths
   */
  public static void main(String[] args) throws IOException {
    ArgumentParser argParser = new ArgumentParser(new DefaultSeamManipulatorFactory());
    argParser.parseArgs(args);
  }
}
