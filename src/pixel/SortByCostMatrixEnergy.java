package pixel;

import java.util.Comparator;

public class SortByCostMatrixEnergy implements Comparator<Pixel> {

  @Override
  public int compare(Pixel pixel1, Pixel pixel2) {
    return (int)Math.round(pixel1.getCostMatrixEnergy() - pixel2.getCostMatrixEnergy());
  }
}
