package seams;

import pixel.Pixel;

public interface Seam {

  void add(Pixel pixel);

  void remove();

  double getAverageEnergy();

}
