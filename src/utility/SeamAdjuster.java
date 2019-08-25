package utility;

public interface SeamAdjuster {

  void adjustByXInclusive(Coordinate[] toAdjust);

  void adjustByXExclusive(Coordinate[] toAdjust);

  void adjustByYInclusive(Coordinate[] toAdjust);

  void adjustByYExclusive(Coordinate[] toAdjust);
}
