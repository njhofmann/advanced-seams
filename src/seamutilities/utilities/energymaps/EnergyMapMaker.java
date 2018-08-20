package seamutilities.utilities.energymaps;

import java.awt.image.BufferedImage;

public interface EnergyMapMaker {

  double[][] computeEnergyMap(BufferedImage bufferedImage);
}
