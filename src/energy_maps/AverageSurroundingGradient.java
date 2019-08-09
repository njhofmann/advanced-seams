package energy_maps;

import java.awt.Color;
import pixel.Pixel;

public class AverageSurroundingGradient implements EnergyMapMaker {

  @Override
  public void computeEnergy(Pixel pixel) {
    if (pixel == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
    else if (pixel.isBorderPixel()) {
      throw new IllegalArgumentException("Given pixel can't be a border pixel!");
    }

    Color[] colors = new Color[]{pixel.getLeftPixel().getColor(),
        pixel.getUpperLeftPixel().getColor(), pixel.getAbovePixel().getColor(),
        pixel.getUpperRightPixel().getColor(), pixel.getRightPixel().getColor(),
        pixel.getLowerRightPixel().getColor(), pixel.getLowerLeftPixel().getColor()};

    Color currentColor = pixel.getColor();
    int currentRed = currentColor.getRed();
    int currentGreen = currentColor.getGreen();
    int currentBlue = currentColor.getBlue();

    double cumulativeDifference = 0;

    for (Color color : colors) {
      int red = Math.abs(color.getRed() - currentRed);
      int green = Math.abs(color.getGreen() - currentGreen);
      int blue = Math.abs(color.getBlue() - currentBlue);
      cumulativeDifference += (double)((red + green + blue) / 3);
    }

    pixel.setEnergyMapEnergy(cumulativeDifference / colors.length);
  }
}
