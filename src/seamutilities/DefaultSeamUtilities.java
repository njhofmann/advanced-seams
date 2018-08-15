package seamutilities;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import masks.Mask;
import seamutilities.utilities.EnergyMap;

public class DefaultSeamUtilities implements SeamUtilities {

  @Override
  public void loadImage(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {

  }

  @Override
  public void applyMask(Mask mask)
      throws IllegalStateException, IOException, IllegalArgumentException {

  }

  @Override
  public void assignEnergyMap(EnergyMap energyMap) throws IllegalArgumentException {

  }

  @Override
  public BufferedImage getEnergyMap() throws IllegalStateException {
    return null;
  }

  @Override
  public void saveCurrentImage(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {

  }

  @Override
  public BufferedImage getCurrentImage() throws IllegalStateException {
    return null;
  }

  @Override
  public void saveCurrentProcess(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException {

  }

  @Override
  public void removeVerticalSeam() throws IllegalStateException {

  }

  @Override
  public void insertVerticalSeam() throws IllegalStateException {

  }

  @Override
  public void removeHorizontalSeam() throws IllegalStateException {

  }

  @Override
  public void insertHorizontalSeam() throws IllegalStateException {

  }

  @Override
  public void removeDiagonalSeam() throws IllegalStateException {

  }

  @Override
  public void insertDiagonalSeam() throws IllegalStateException {

  }
}
