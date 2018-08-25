package seams;

import java.awt.Color;
import utility.Coordinate;
import pixel.Pixel;

public interface Seam {

  void add(Pixel pixel, Coordinate pixelCoor);

  void remove();

  void highlight(Color color);

  double getAverageEnergy();

  boolean isVerticalSeam();

  Coordinate[] getCoordinates();
}
