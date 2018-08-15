package seammanipulators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import seammanipulators.SeamManipulator;
import masks.Mask;

public class SeamUtilitiesManipulator implements SeamManipulator {

  @Override
  public void resize(int newWidth, int newHeight, Path input, Path outputImage, Path outputGIF)
      throws IllegalArgumentException, FileNotFoundException, IOException {

  }

  @Override
  public void resize(int newWidth, int newHeight, Path input, Path outputImage, Path outputGIF,
      Mask mask) throws IllegalArgumentException, FileNotFoundException, IOException {

  }

  @Override
  public void deleteObject(Path input, Path outputImage, Path outputGIF, Mask mask)
      throws IllegalArgumentException, FileNotFoundException, IOException {

  }
}
