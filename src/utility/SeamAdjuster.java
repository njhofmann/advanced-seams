package utility;

public interface SeamAdjuster {

  void adjustCoordinatesByXInclusive(Coordinate[] toAdjust);

  void adjustCoordinatesByXExclusive(Coordinate[] toAdjust);

  void adjustCoordinatesByY(Coordinate[] toAdjust);

}
