package pixel;

import java.awt.Color;

public interface Pixel {
   Color getColor();

  boolean isBorderPixel();

  void setColor(Color color);

  double getEnergyMapEnergy();

  void setEnergyMapEnergy(double energyMapEnergy);

  double getCostMatrixEnergy();

  void setCostMatrixEnergy(double costMatrixEnergy);

  Pixel getLeftPixel();
  
  void setLeftPixel(Pixel leftPixel);
  
  Pixel getUpperLeftPixel();

  Pixel getAbovePixel();

  void setAbovePixel(Pixel abovePixel);
  
  Pixel getUpperRightPixel();

  Pixel getRightPixel();

  void setRightPixel(Pixel rightPixel);

  Pixel getLowerRightPixel();

  Pixel getBelowPixel();

  void setBelowPixel(Pixel belowPixel);

  Pixel getLowerLeftPixel();
}
