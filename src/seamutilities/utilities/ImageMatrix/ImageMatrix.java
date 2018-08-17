package seamutilities.utilities.ImageMatrix;

import java.awt.image.BufferedImage;
import seamutilities.utilities.pixel.Pixel;

public interface ImageMatrix extends Iterable<Pixel> {

  int getWidth();

  int getHeight();

  Pixel getPixel(int x, int y);

  BufferedImage toBufferedImage();

}
