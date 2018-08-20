package seamutilities.utilities.energymaps;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * EnergyMap that for each Pixel in a given ImageMatrix, assigns its Energy based on the average
 * color difference of its 8 surrounding Pixel's colors and its own color.
 */
public class AverageSurroundingGradient implements EnergyMapMaker {

  @Override
  public double[][] computeEnergyMap(BufferedImage bufferedImage) {
    if (bufferedImage == null) {
      throw new IllegalArgumentException("Given image can't be null!");
    }
    
    int bufferedImageWidth = bufferedImage.getWidth();
    int bufferedImageHeight = bufferedImage.getHeight();
    double[][] energyMap = new double[bufferedImageHeight][bufferedImageWidth];
    
    class GetColor {
      public Color from(int x, int y) {
        if (!(0 <= x && x <= bufferedImage.getWidth())) {
          throw new IllegalArgumentException("Given x coordinate is out of current image's bounds!");
        }
        else if (!(0 <= y && y <= bufferedImage.getHeight())) {
          throw new IllegalArgumentException("Given y coordinate is out of current image's bounds!");
        }
        return new Color(bufferedImage.getRGB(x, y));
      }
    }
    
    GetColor getColor = new GetColor();

    double maxEnergy = 0;

    for (int row = 0; row < bufferedImageHeight; row += 1) {
      for (int column = 0; column < bufferedImageWidth; column += 1) {

        Color currentColor = getColor.from(column, row);

        Color leftColor;
        if (column == 0) {
          leftColor = getColor.from(bufferedImageWidth - 1, row);
        }
        else {
          leftColor = getColor.from(column - 1, row);
        }

        Color rightColor;
        if (column == bufferedImageWidth - 1) {
          rightColor = getColor.from(0, row);
        }
        else {
          rightColor = getColor.from(column + 1, row);
        }

        Color topColor;
        if (row == 0) {
          topColor = getColor.from(column, bufferedImageHeight - 1);
        }
        else {
          topColor = getColor.from(column, row - 1);
        }

        Color bottomColor;
        if (row == bufferedImageHeight - 1) {
          bottomColor = getColor.from(column, 0);
        }
        else {
          bottomColor = getColor.from(column, row + 1);
        }

        Color topLeftColor;
        if (column == 0 && row == 0) {
          topLeftColor = getColor.from(bufferedImageWidth - 1, bufferedImageHeight - 1);
        }
        else if (column == 0) {
          topLeftColor = getColor.from(bufferedImageWidth - 1, row - 1);
        }
        else if (row == 0) {
          topLeftColor = getColor.from(column - 1, bufferedImageHeight - 1);
        }
        else {
          topLeftColor = getColor.from(column - 1, row - 1);
        }

        Color topRightColor;
        if (column == bufferedImageWidth - 1 && row == 0) {
          topRightColor = getColor.from(0, bufferedImageHeight - 1);
        }
        else if (column == bufferedImageWidth - 1) {
          topRightColor = getColor.from(0, row - 1);
        }
        else if (row == 0) {
          topRightColor = getColor.from(column + 1, bufferedImageHeight - 1);
        }
        else {
          topRightColor = getColor.from(column + 1, row - 1);
        }

        Color bottomLeftColor;
        if (column == 0 && row == bufferedImageHeight - 1) {
          bottomLeftColor = getColor.from(bufferedImageWidth - 1, 0);
        }
        else if (row == bufferedImageHeight - 1) {
          bottomLeftColor = getColor.from(column - 1, 0);
        }
        else if (column == 0) {
          bottomLeftColor = getColor.from(bufferedImageWidth - 1, row + 1);
        }
        else {
          bottomLeftColor = getColor.from(column - 1, row + 1);
        }

        Color bottomRightColor;
        if (column == bufferedImageWidth - 1 && row == bufferedImageHeight - 1) {
          bottomRightColor = getColor.from(0, 0);
        }
        else if (row == bufferedImageHeight - 1) {
          bottomRightColor = getColor.from(column + 1, 0);
        }
        else if (column == bufferedImageWidth - 1) {
          bottomRightColor = getColor.from(0, row + 1);
        }
        else {
          bottomRightColor = getColor.from(column + 1, row + 1);
        }

        Color[] colors = new Color[]{topColor, bottomColor, leftColor, rightColor, topLeftColor,
            topRightColor, bottomLeftColor, bottomRightColor};

        class ColorDifference {
          private float compute(Color color) {
            int red = Math.abs(color.getRed() - currentColor.getRed());
            int green = Math.abs(color.getGreen() - currentColor.getGreen());
            int blue = Math.abs(color.getBlue() - currentColor.getBlue());
            return (float)((red + green + blue) / 3);
          }
        }

        ColorDifference colorDifference = new ColorDifference();
        double finalValue = 0;
        for (Color color : colors) {
          finalValue += colorDifference.compute(color);
        }

        double finalEnergy = finalValue / 8;

        if (finalEnergy > maxEnergy) {
          maxEnergy = finalEnergy;
        }

        energyMap[row][column] = finalEnergy;
      }
    }
    return energyMap;
  }
}
