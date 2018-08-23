package seams;

import java.util.ArrayList;
import java.util.List;
import pixel.Pixel;

public abstract class AbstractSeam implements Seam {

  protected final List<Pixel> pixels = new ArrayList<>();

  protected double totalEnergy = 0;

  @Override
  public void add(Pixel pixel) {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    totalEnergy += pixel.getCostMatrixEnergy();
    pixels.add(pixel);
  }

  @Override
  public double getAverageEnergy() {
    return totalEnergy / pixels.size();
  }
}
