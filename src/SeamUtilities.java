import java.awt.image.BufferedImage;
import java.nio.file.Path;

/**
 * Interface for implementing basic operations related to content aware image resizing. Be aware
 * that the order these operations are called in. Most importantly one must call {@code loadImage}
 * before any other method or throws an {@code IllegalStateArgument}.
 */
public interface SeamUtilities {
  void loadImage(Path path);

  void applyMask(Mask mask);

  void assignEnergyMap(EnergyMap energyMap);

  BufferedImage getEnergyMap();

  void saveCurrentImage(Path path);

  BufferedImage getCurrentImage();

  void saveCurrentProcess(Path path);

  void removeVerticalSeam();

  void insertVerticalSeam();

  void removeHorizontalSeam();

  void insertHorizontalSeam();

  void removeDiagonalSeam();

  void insertDiagonalSeam();




}
