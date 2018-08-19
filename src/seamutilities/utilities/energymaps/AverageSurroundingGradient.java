package seamutilities.utilities.energymaps;

import java.awt.Color;
import java.awt.image.BufferedImage;
import seamutilities.utilities.ImageMatrix.ImageMatrix;
import seamutilities.utilities.pixel.Pixel;

public class AverageSurroundingGradient extends AbstractEnergyMap implements EnergyMap{


  @Override
  public void computeEnergyMap(ImageMatrix imageMatrix) {
    if (imageMatrix == null) {
      throw new IllegalArgumentException("Given image matrix can't be null!");
    }

    this.imageMatrix = imageMatrix;
    maxEnergy = 0;

    for (int row = 0; row < imageMatrix.getHeight(); row += 1) {
      for (int column = 0; column < imageMatrix.getWidth(); column += 1) {

        Pixel currentPixel = imageMatrix.getPixel(column, row);
        Color currentColor = currentPixel.getColor();

        Color leftColor;
        if (column == 0) {
          leftColor = imageMatrix.getPixel(imageMatrix.getWidth() - 1, row).getColor();
        }
        else {
          leftColor = imageMatrix.getPixel(column - 1, row).getColor();
        }

        Color rightColor;
        if (column == imageMatrix.getWidth() - 1) {
          rightColor = imageMatrix.getPixel(0, row).getColor();
        }
        else {
          rightColor = imageMatrix.getPixel(column + 1, row).getColor();
        }

        Color topColor;
        if (row == 0) {
          topColor = imageMatrix.getPixel(column, imageMatrix.getHeight() - 1).getColor();
        }
        else {
          topColor = imageMatrix.getPixel(column, row - 1).getColor();
        }

        Color bottomColor;
        if (row == imageMatrix.getHeight() - 1) {
          bottomColor = imageMatrix.getPixel(column, 0).getColor();
        }
        else {
          bottomColor = imageMatrix.getPixel(column, row + 1).getColor();
        }

        Color topLeftColor;
        if (column == 0 && row == 0) {
          topLeftColor = imageMatrix.getPixel(imageMatrix.getWidth() - 1, imageMatrix.getHeight() - 1).getColor();
        }
        else if (column == 0) {
          topLeftColor = imageMatrix.getPixel(imageMatrix.getWidth() - 1, row - 1).getColor();
        }
        else if (row == 0) {
          topLeftColor = imageMatrix.getPixel(column - 1, imageMatrix.getHeight() - 1).getColor();
        }
        else {
          topLeftColor = imageMatrix.getPixel(column - 1, row - 1).getColor();
        }

        Color topRightColor;
        if (column == imageMatrix.getWidth() - 1 && row == 0) {
          topRightColor = imageMatrix.getPixel(0, imageMatrix.getHeight() - 1).getColor();
        }
        else if (column == imageMatrix.getWidth() - 1) {
          topRightColor = imageMatrix.getPixel(0, row - 1).getColor();
        }
        else if (row == 0) {
          topRightColor = imageMatrix.getPixel(column + 1, imageMatrix.getHeight() - 1).getColor();
        }
        else {
          topRightColor = imageMatrix.getPixel(column + 1, row - 1).getColor();
        }

        Color bottomLeftColor;
        if (column == 0 && row == imageMatrix.getHeight() - 1) {
          bottomLeftColor = imageMatrix.getPixel(imageMatrix.getWidth() - 1, 0).getColor();
        }
        else if (row == imageMatrix.getHeight() - 1) {
          bottomLeftColor = imageMatrix.getPixel(column - 1, 0).getColor();
        }
        else if (column == 0) {
          bottomLeftColor = imageMatrix.getPixel(imageMatrix.getWidth() - 1, row + 1).getColor();
        }
        else {
          bottomLeftColor = imageMatrix.getPixel(column - 1, row + 1).getColor();
        }

        Color bottomRightColor;
        if (column == imageMatrix.getWidth() - 1 && row == imageMatrix.getHeight() - 1) {
          bottomRightColor = imageMatrix.getPixel(0, 0).getColor();
        }
        else if (row == imageMatrix.getHeight() - 1) {
          bottomRightColor = imageMatrix.getPixel(column + 1, 0).getColor();
        }
        else if (column == imageMatrix.getWidth() - 1) {
          bottomRightColor = imageMatrix.getPixel(0, row + 1).getColor();
        }
        else {
          bottomRightColor = imageMatrix.getPixel(column + 1, row + 1).getColor();
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
        float finalValue = 0;
        for (Color color : colors) {
          finalValue += colorDifference.compute(color);
        }

        float finalEnergy = finalValue / 8;

        if (finalEnergy > maxEnergy) {
          maxEnergy = finalEnergy;
        }

        currentPixel.assignEnergy(finalEnergy);
      }
    }
  }
}
