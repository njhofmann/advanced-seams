package seams;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import utility.Coordinate;
import pixel.Pixel;

public abstract class AbstractSeam implements Seam {

  protected final List<Pixel> pixels = new ArrayList<>();

  protected double totalEnergy = 0;

  protected final List<Coordinate> coordinates = new ArrayList<>();

  @Override
  public void add(Pixel pixel, Coordinate pixelCoor) {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    else if (pixelCoor == null) {
      throw new IllegalArgumentException("Given coordinate can't be null!");
    }
    totalEnergy += pixel.getCostMatrixEnergy();
    pixels.add(pixel);
    coordinates.add(pixelCoor);
  }

  @Override
  public void highlight(Color newColor) {
    if (newColor == null) {
      throw new IllegalArgumentException("Given color can't be null!");
    }

    for (Pixel pixel : pixels) {
      pixel.setColor(newColor);
    }
  }

  @Override
  public double getAverageEnergy() {
    return totalEnergy / pixels.size();
  }

  @Override
  public Coordinate[] getCoordinates() {
    Coordinate[] toReturn = new Coordinate[coordinates.size()];
    Collections.reverse(coordinates);
    toReturn = coordinates.toArray(toReturn);
    return toReturn;
  }
}
