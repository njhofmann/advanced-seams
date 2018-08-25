package seams;

import java.util.Collections;
import pixel.Pixel;

public class HorizontalSeam extends AbstractSeam implements Seam {

  @Override
  public void remove() {
    Collections.reverse(pixels);
    for (int i = 0; i < pixels.size(); i += 1) {
      Pixel currentPixel = pixels.get(i);

      Pixel abovePixel = currentPixel.getAbovePixel();
      Pixel belowPixel = currentPixel.getBelowPixel();
      abovePixel.setBelowPixel(belowPixel);
      belowPixel.setAbovePixel(abovePixel);

      if (i < pixels.size() - 1) {
        Pixel nextPixel = pixels.get(i + 1);
        Pixel rightPixel = currentPixel.getRightPixel();
        if (nextPixel.equals(currentPixel.getUpperRightPixel())) {
          rightPixel.setLeftPixel(abovePixel);
          abovePixel.setRightPixel(rightPixel);
        }
        else if (nextPixel.equals(currentPixel.getLowerRightPixel())) {
          rightPixel.setLeftPixel(belowPixel);
          belowPixel.setRightPixel(rightPixel);
        }
      }
    }
  }

  @Override
  public boolean isVerticalSeam() {
    return false;
  }
}
