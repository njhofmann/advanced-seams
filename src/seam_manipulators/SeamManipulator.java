package seam_manipulators;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.io.IOException;
import masks.Mask;

/**
 * Interface for implementing the operations of image resizing and object deletion with content
 * aware image resizing techniques upon a given image, then saving the resulting image.
 * Has optional mode to well as saving the manipulation process up to the last operation as a video.
 */
public interface SeamManipulator {

  /**
   * Resizes image associated with this {@link SeamManipulator} to the given width and height.
   * @param newWidth width to resize to
   * @param newHeight height to resize to
   * @throws IllegalArgumentException if given width and/or height are negative
   */
  void resize(int newWidth, int newHeight) throws IllegalArgumentException;

  /**
   * Resizes image associated with this {@link SeamManipulator} to the given width and height,
   * with a Mask representing parts of the image to leave alone when resizing.
   * @param newWidth width to resize to
   * @param newHeight height to resize to
   * @param areaToProtect parts of the image to leave along when resizing
   * @throws IllegalArgumentException if given width and/or height are non-positive
   */
  void resize(int newWidth, int newHeight, Mask areaToProtect) throws IllegalArgumentException;

  /**
   * Removes the areas given by the the given Mask from image assoaicated with this
   * {@link SeamManipulator}.
   * @param areaToRemove areas to remove from this image
   * @throws IllegalArgumentException if given Mask is null or whose size parameters don't align
   *                                  with the manipulated image's current state
   */
  void removeArea(Mask areaToRemove) throws IllegalArgumentException;

  /**
   * Removes, then replaces the areas given by the the given Mask from image associated with this
   * {@link SeamManipulator}.
   * @param areaToReplace areas to replace from this image
   * @throws IllegalArgumentException if given Mask is null or whose size parameters don't align
   *                                  with the manipulated image's current state
   */
  void replaceArea(Mask areaToReplace) throws IllegalArgumentException;

  /**
   * Returns the current state of the uploaded image as a BufferedImage.
   * @return BufferedImage of image's current state
   */
  BufferedImage getCurrentImage();

  /**
   * Returns the last computed energy map of the uploaded image as a BufferedImage.
   * @return BufferedImage of image's last computed energy map
   */
  BufferedImage getCurrentEnergyMap();

  /**
   * Returns the last computed cost matrix of the uploaded image as a BufferedImage.
   * @return BufferedImage of image's last computed cost matrix
   */
  BufferedImage getCurrentCostMatrix();

  /**
   * Saves the current state of the uploaded image to the given file path as a JPEG.
   * @param filePath file path to save image to
   * @throws IllegalArgumentException if given Path is null, points to an existing file, or parent
   * directory of given file doesn't exist
   * @throws IOException if image fails to save to given file path
   */
  void saveCurrentImage(Path filePath) throws IOException;

  /**
   * Saves the manipulation process of the uploaded image so far to the given file path as a JPEG.
   * @param filePath file path to save video to
   * @throws IOException if video fails to save to given file path
   * @throws IllegalStateException if the SeamManipulator has been set to not record video, or given
   * path points to existing file, or if parent directory of given path doesn't exist
   */
  void saveCurrentProcess(Path filePath) throws IOException, IllegalStateException;
}
