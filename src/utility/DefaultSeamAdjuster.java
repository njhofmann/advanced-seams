package utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultSeamAdjuster implements SeamAdjuster {

  private final int adjustmentValue = 1;

  private final int[] coordinateValues;

  private final int coordinateValuesLength;

  public DefaultSeamAdjuster(int coordinateRange) {
    if (coordinateRange < 1) {
      throw new IllegalArgumentException("Give coordinate range must contain at least one value"
          + "and be non-negative!");
    }
    coordinateValues = new int[coordinateRange];
    coordinateValuesLength = coordinateRange;
  }

  private void inCoordinateRange(int toCheck) {
    if (toCheck < 0 || toCheck >= coordinateValuesLength) {
      throw new IllegalArgumentException("Given index is out of the range of this SeamAdjuster [0, "
          + Integer.toString(coordinateValuesLength - 1) + "]!");
    }
  }

  private void addValueTo(int idx) throws IllegalArgumentException {
    inCoordinateRange(idx);
    for (int i = idx; i < coordinateValuesLength; i += 1) {
      coordinateValues[i] += adjustmentValue;
    }
  }

  private int getValueAt(int idx) throws IllegalArgumentException {
    inCoordinateRange(idx);
    return coordinateValues[idx];
  }

  private void validCoordinateArray(Coordinate[] coordinates) {
    if (coordinates == null) {
      throw new IllegalArgumentException("Given array can't be null!");
    }
    else if (coordinates.length == 0) {
      throw new IllegalArgumentException("Given coordinate array must contain coordiantes to adjust!");
    }
  }

  private int adjustCoordinatesByX(Coordinate[] toAdjust) {
    validCoordinateArray(toAdjust);

    int coordinateX = toAdjust[0].getX();
    inCoordinateRange(coordinateX);
    int valueToAdjustBy = getValueAt(coordinateX);

    for (int i = 0; i < toAdjust.length; i += 1) {
      Coordinate oldCoordinate = toAdjust[i];
      int newX = oldCoordinate.getX() + valueToAdjustBy;
      int newY = oldCoordinate.getY();
      toAdjust[i] = new Coordinate(newX, newY);
    }

    return coordinateX;
  }

  @Override
  public void adjustCoordinatesByXInclusive(Coordinate[] toAdjust) {
    int adjustmentStart = adjustCoordinatesByX(toAdjust);
    addValueTo(adjustmentStart);
  }

  @Override
  public void adjustCoordinatesByXExclusive(Coordinate[] toAdjust) {
    int adjustmentStart = adjustCoordinatesByX(toAdjust);
    addValueTo(adjustmentStart + 1);
  }

  @Override
  public void adjustCoordinatesByY(Coordinate[] toAdjust) {
    validCoordinateArray(toAdjust);

    int coordinateY = toAdjust[0].getY();
    inCoordinateRange(coordinateY);
    int valueToAdjustBy = getValueAt(coordinateY);

    for (int i = 0; i < toAdjust.length; i += 1) {
      Coordinate oldCoordinate = toAdjust[i];
      int newX = oldCoordinate.getX();
      int newY = oldCoordinate.getY() + valueToAdjustBy;
      toAdjust[i] = new Coordinate(newX, newY);
    }

    addValueTo(coordinateY);
  }
}
