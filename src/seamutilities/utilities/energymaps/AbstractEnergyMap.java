package seamutilities.utilities.energymaps;

import java.awt.Color;
import java.awt.image.BufferedImage;
import seamutilities.utilities.ImageMatrix.ImageMatrix;

public abstract class AbstractEnergyMap implements EnergyMap {

  protected ImageMatrix imageMatrix;

  protected double maxEnergy = 0;

  @Override
  public BufferedImage outputAsBufferedImage() {
    if (imageMatrix == null) {
      throw new IllegalStateException("This gradient has not yet been assigned an image matrix!");
    }

    BufferedImage toReturn = new BufferedImage(imageMatrix.getWidth(), imageMatrix.getHeight(),
        BufferedImage.TYPE_3BYTE_BGR);
    for (int row = 0; row < imageMatrix.getHeight(); row += 1) {
      for (int column = 0; column < imageMatrix.getWidth(); column += 1) {
        double currentPixelEnergy = imageMatrix.getPixel(column, row).getEnergy() / maxEnergy;
        toReturn.setRGB(column, row, Color.HSBtoRGB(0, 0, (float)currentPixelEnergy));
      }
    }
    return toReturn;
  }
}
