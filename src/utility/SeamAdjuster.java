package utility;

public interface SeamAdjuster {

  void adjustCoordinatesByXInclusive(Coordinate[] toAdjust);

  void adjustCoordinatesByXExclusive(Coordinate[] toAdjust);

  void adjustCoordinatesByYInclusive(Coordinate[] toAdjust);

  void adjustCoordinatesByYExclusive(Coordinate[] toAdjust);

}
