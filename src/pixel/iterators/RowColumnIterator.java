package pixel.iterators;

import java.util.Iterator;
import pixel.Pixel;

public class RowColumnIterator implements Iterator<Pixel>, CoordinateTracker{

  private final ColumnIterator leftMostColumn;

  private RowIterator currentRow;

  private int currentX = 0;

  private int currentY = 0;

  public RowColumnIterator(Pixel upperLeftCorner) {
    if (upperLeftCorner == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    leftMostColumn = new ColumnIterator(upperLeftCorner);
    currentRow = new RowIterator(leftMostColumn.next());
  }

  @Override
  public boolean hasNext() {
    if (currentRow.hasNext()) {
      return true;
    }
    else if (leftMostColumn.hasNext()) {
      currentX = 0;
      currentY += 1;
      currentRow = new RowIterator(leftMostColumn.next());
      return currentRow.hasNext();
    }
    return false;

  }

  @Override
  public Pixel next() {
    currentX += 1;
    return currentRow.next();
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
