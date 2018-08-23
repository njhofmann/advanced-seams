package pixel;

import java.awt.Color;

public class BorderPixel implements Pixel {

  private final static UnsupportedOperationException unsupportedOperationException
      = new UnsupportedOperationException("Can't change the parameters of a border pixel!");

  @Override
  public void makeMask(double maskValue) {

  }

  @Override
  public Color getColor() {
    return Color.BLACK;
  }

  @Override
  public boolean isBorderPixel() {
    return true;
  }

  @Override
  public void setColor(Color color) {
    return;
  }

  @Override
  public double getEnergyMapEnergy() {
    return Integer.MAX_VALUE;
  }

  @Override
  public void setEnergyMapEnergy(double energyMapEnergy) {
    return;
  }

  @Override
  public double getCostMatrixEnergy() {
    return Integer.MAX_VALUE;
  }

  @Override
  public void setCostMatrixEnergy(double costMatrixEnergy) {
    return;
  }

  @Override
  public Pixel getLeftPixel() {
    return this;
  }

  @Override
  public void setLeftPixel(Pixel leftPixel) {
    return;
  }

  @Override
  public Pixel getUpperLeftPixel() {
    return this;
  }

  @Override
  public Pixel getAbovePixel() {
    return this;
  }

  @Override
  public void setAbovePixel(Pixel abovePixel) {
    return;
  }

  @Override
  public Pixel getUpperRightPixel() {
    return this;
  }

  @Override
  public Pixel getRightPixel() {
    return this;
  }

  @Override
  public void setRightPixel(Pixel rightPixel) {
    return;
  }

  @Override
  public Pixel getLowerRightPixel() {
    return this;
  }

  @Override
  public Pixel getBelowPixel() {
    return this;
  }

  @Override
  public void setBelowPixel(Pixel belowPixel) {
    return;
  }

  @Override
  public Pixel getLowerLeftPixel() {
    return this;
  }
}
