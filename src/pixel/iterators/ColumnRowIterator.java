package pixel.iterators;

import java.util.Iterator;
import pixel.Pixel;

public class ColumnRowIterator implements Iterator<Pixel>, CoordinateTracker {

  private final RowIterator topMostRow;

  private ColumnIterator currentColumn;

  private int currentX = 0;

  private int currentY = 0;

  public ColumnRowIterator(Pixel upperLeftCorner) {
    if (upperLeftCorner == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    topMostRow = new RowIterator(upperLeftCorner);
    currentColumn = new ColumnIterator(topMostRow.next());
  }

  @Override
  public boolean hasNext() {
    if (currentColumn.hasNext()) {
      return true;
    }
    else if (topMostRow.hasNext()) {
      currentY = 0;
      currentX += 1;
      currentColumn = new ColumnIterator(topMostRow.next());
      return currentColumn.hasNext();
    }
    return false;

  }

  @Override
  public Pixel next() {
    currentY += 1;
    return currentColumn.next();
  }

  @Override
  public int getX() {
    return currentX;
  }

  @Override
  public int getY() {
    return currentY;
  }
}
