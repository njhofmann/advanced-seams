package seamutilities.utilities.seams;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import seamutilities.SeamUtilities;
import seamutilities.utilities.pixel.Pixel;

public class DefaultSeam implements Seam {

  private final List<Pixel> pixels = new ArrayList<>();

  private double energy = 0;

  @Override
  public void addPixel(Pixel toAdd) {
    if (toAdd == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    else if (pixels.contains(toAdd)) {
      throw new IllegalArgumentException("Given pixel has already been added to this seam!");
    }

    pixels.add(toAdd);
    energy += toAdd.getEnergy();
  }

  @Override
  public void highlight(Color toColor) {
    if (toColor == null) {
      throw new IllegalArgumentException("Given color can't be null!");
    }

    for (Pixel pixel : pixels) {
      pixel.assignColor(toColor);
    }
  }

  @Override
  public List<Pixel> getPixels() {
    return pixels;
  }

  @Override
  public double getEnergy() {
    return energy;
  }
}
