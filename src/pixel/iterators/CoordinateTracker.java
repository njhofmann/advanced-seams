package pixel.iterators;

import java.util.Iterator;
import pixel.Pixel;

public interface CoordinateTracker extends Iterator<Pixel> {

  int getX();

  int getY();

}
