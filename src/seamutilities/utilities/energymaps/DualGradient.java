package seamutilities.utilities.energymaps;

import java.awt.Color;
import seamutilities.utilities.ImageMatrix.ImageMatrix;
import seamutilities.utilities.pixel.Pixel;

/**
 * EnergyMap that for each Pixel in a given ImageMatrix, assigns its Energy based on the squared
 * sum of the squared absolute difference of its upper and lower neighboring Pixels' colors, as
 * well the squared absolute difference of its left and right neighboring Pixel's colors.
 */
public class DualGradient implements EnergyMap {

  @Override
  public double computeEnergyMap(ImageMatrix imageMatrix) {
    if (imageMatrix == null) {
      throw new IllegalArgumentException("Given image matrix can't be null!");
    }

    double maxEnergy = 0;

    for (int row = 0; row < imageMatrix.getHeight(); row += 1) {
      for (int column = 0; column < imageMatrix.getWidth(); column += 1) {

        Color topColor, bottomColor;
        if (row == 0) {
          topColor = imageMatrix.getPixel(column, imageMatrix.getHeight() - 1).getColor();
          bottomColor =  imageMatrix.getPixel(column, row + 1).getColor();
        }
        else if (row == imageMatrix.getHeight() - 1) {
          topColor = imageMatrix.getPixel(column, row - 1).getColor();
          bottomColor =  imageMatrix.getPixel(column, 0).getColor();
        }
        else {
          topColor = imageMatrix.getPixel(column, row - 1).getColor();
          bottomColor =  imageMatrix.getPixel(column, row + 1).getColor();
        }

        Color leftColor, rightColor;
        if (column == 0) {
          leftColor = imageMatrix.getPixel(imageMatrix.getWidth() - 1, row).getColor();
          rightColor = imageMatrix.getPixel(column + 1, row).getColor();
        }
        else if (column == imageMatrix.getWidth() - 1) {
          leftColor = imageMatrix.getPixel(column - 1, row).getColor();
          rightColor = imageMatrix.getPixel(0, row).getColor();
        }
        else {
          leftColor = imageMatrix.getPixel(column - 1, row).getColor();
          rightColor = imageMatrix.getPixel(column + 1, row).getColor();
        }

        class ComputeAbsoluteDifference {
          public double compute(Color a, Color b) {
            double red = Math.pow(Math.abs(a.getRed() - b.getRed()), 2);
            double green = Math.pow(Math.abs(a.getGreen() - b.getGreen()), 2);
            double blue = Math.pow(Math.abs(a.getBlue() - b.getBlue()), 2);
            return red + green + blue;
          }
        }

        ComputeAbsoluteDifference computeAbsoluteDifference = new ComputeAbsoluteDifference();
        double xEnergy = computeAbsoluteDifference.compute(topColor, bottomColor);
        double yEnergy = computeAbsoluteDifference.compute(leftColor, rightColor);

        xEnergy /= 3;
        yEnergy /= 3;
        double finalEnergy = xEnergy + yEnergy;

        if (finalEnergy > maxEnergy) {
          maxEnergy = finalEnergy;
        }

        Pixel currentPixel = imageMatrix.getPixel(column, row);
        currentPixel.assignEnergy(xEnergy + yEnergy);
      }
    }
    return maxEnergy;
  }
}
