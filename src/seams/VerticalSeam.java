package seams;

import java.util.Collections;
import pixel.Pixel;

public class VerticalSeam extends AbstractSeam implements Seam {

  @Override
  public void remove() {
    Collections.reverse(pixels);
    for (int i = 0; i < pixels.size(); i += 1) {
      Pixel currentPixel = pixels.get(i);

      Pixel leftPixel = currentPixel.getLeftPixel();
      Pixel rightPixel = currentPixel.getRightPixel();
      leftPixel.setRightPixel(rightPixel);
      rightPixel.setLeftPixel(leftPixel);

      if (i < pixels.size() - 1) {
        Pixel nextPixel = pixels.get(i + 1);
        Pixel belowPixel = currentPixel.getBelowPixel();
        if (nextPixel.equals(currentPixel.getLowerLeftPixel())) {
          belowPixel.setAbovePixel(leftPixel);
          leftPixel.setBelowPixel(belowPixel);
        }
        else if (nextPixel.equals(currentPixel.getLowerRightPixel())) {
          belowPixel.setAbovePixel(rightPixel);
          rightPixel.setBelowPixel(belowPixel);
        }
      }
    }
  }
}
