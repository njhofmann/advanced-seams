package pixel;

import java.awt.Color;

public class ImagePixel implements Pixel{

  private Color color;

  private boolean isMask =  false;

  private double energyMapEnergy = 0;

  private double costMatrixEnergy = 0;

  private Pixel leftPixel = new BorderPixel();

  private Pixel abovePixel = new BorderPixel();

  private Pixel rightPixel = new BorderPixel();

  private Pixel belowPixel = new BorderPixel();

  public ImagePixel(Color color) {
    this.color = color;
  }

  private void checkPixelNull(Pixel toCheck) {
    if (toCheck == null) {
      throw new IllegalArgumentException("Given pixel can't be null!");
    }
  }

  @Override
  public void makeMask(double maskValue) {
    if (!isMask) {
      isMask = true;
      energyMapEnergy = maskValue;
    }
  }

  @Override
  public boolean isMask() {
    return isMask;
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public boolean isBorderPixel() {
    return false;
  }

  @Override
  public void setColor(Color color) {
    if (color == null) {
      throw new IllegalArgumentException("Given color can't be null!");
    }
    this.color = color;
  }

  @Override
  public double getEnergyMapEnergy() {
    return energyMapEnergy;
  }

  @Override
  public void setEnergyMapEnergy(double energyMapEnergy) {
    if (!isMask) {
      this.energyMapEnergy = energyMapEnergy;
    }
  }

  @Override
  public double getCostMatrixEnergy() {
    return costMatrixEnergy;
  }

  @Override
  public void setCostMatrixEnergy(double costMatrixEnergy) {
    this.costMatrixEnergy = costMatrixEnergy;
  }

  @Override
  public Pixel getLeftPixel() {
    return leftPixel;
  }

  @Override
  public void setLeftPixel(Pixel leftPixel) {
    checkPixelNull(leftPixel);
    this.leftPixel = leftPixel;
  }

  @Override
  public Pixel getUpperLeftPixel() {
    return abovePixel.getLeftPixel();
  }

  @Override
  public Pixel getAbovePixel() {
    return abovePixel;
  }

  @Override
  public void setAbovePixel(Pixel abovePixel) {
    checkPixelNull(abovePixel);
    this.abovePixel = abovePixel;
  }

  @Override
  public Pixel getUpperRightPixel() {
    return abovePixel.getRightPixel();
  }

  @Override
  public Pixel getRightPixel() {
    return rightPixel;
  }

  @Override
  public void setRightPixel(Pixel rightPixel) {
    checkPixelNull(rightPixel);
    this.rightPixel = rightPixel;
  }

  @Override
  public Pixel getLowerRightPixel() {
    return belowPixel.getRightPixel();
  }

  @Override
  public Pixel getBelowPixel() {
    return belowPixel;
  }

  @Override
  public void setBelowPixel(Pixel belowPixel) {
    checkPixelNull(belowPixel);
    this.belowPixel = belowPixel;
  }

  @Override
  public Pixel getLowerLeftPixel() {
    return belowPixel.getLeftPixel();
  }
}
