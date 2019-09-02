package seam_manipulators;

import arg_parsing.ArgumentParser;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import masks.Mask;

/**
 * Implementation of the {@link SeamManipulator} interface, used only for testing the validity of
 * {@link ArgumentParser}.
 */
public class TestSeamManipulator implements SeamManipulator {

  @Override
  public void resize(int newWidth, int newHeight) throws IllegalArgumentException {

  }

  @Override
  public void resize(int newWidth, int newHeight, Mask areaToProtect)
      throws IllegalArgumentException {

  }

  @Override
  public void removeArea(Mask areaToRemove) throws IllegalArgumentException {

  }

  @Override
  public void replaceArea(Mask areaToReplace) throws IllegalArgumentException {

  }

  @Override
  public BufferedImage getCurrentImage() {
    return null;
  }

  @Override
  public BufferedImage getCurrentEnergyMap() {
    return null;
  }

  @Override
  public BufferedImage getCurrentCostMatrix() {
    return null;
  }

  @Override
  public void saveCurrentImage(Path filePath) throws IOException {

  }

  @Override
  public void saveCurrentProcess(Path filePath) throws IOException, IllegalStateException {

  }
}
