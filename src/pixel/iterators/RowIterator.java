package pixel.iterators;

import java.util.Iterator;
import pixel.Pixel;

public class RowIterator implements Iterator<Pixel> {

  private Pixel currentPixel;

  public RowIterator(Pixel startingPixel) {
    if (startingPixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    currentPixel = startingPixel;
  }

  @Override
  public boolean hasNext() {
    return !currentPixel.isBorderPixel();
  }

  @Override
  public Pixel next() {
    Pixel toReturn = currentPixel;
    currentPixel = currentPixel.getRightPixel();
    return toReturn;
  }
}
