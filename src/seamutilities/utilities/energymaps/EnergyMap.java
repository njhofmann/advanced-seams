package seamutilities.utilities.energymaps;

import java.awt.image.BufferedImage;
import seamutilities.utilities.ImageMatrix.ImageMatrix;

public interface EnergyMap {
  void computeEnergyMap(ImageMatrix imageMatrix);

  BufferedImage outputAsBufferedImage();

}
