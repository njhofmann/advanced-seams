package seamutilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.IOException;
import masks.Mask;
import seamutilities.utilities.EnergyMap;

/**
 * Interface for implementing basic operations related to content aware image resizing. Be aware
 * that the order these operations are called in. Most importantly one must call {@code loadImage}
 * before any other method or throws an {@code IllegalStateArgument}.
 */
public interface SeamUtilities {

  /**
   * Loads the image at the given file path to this SeamUtilities for further editing. Must be
   * called at least once before calling any other methods in this SeamUtilities.
   *
   * @param filePath file path of image to edit
   * @throws IllegalArgumentException if given {@param filePath} is null
   * @throws FileNotFoundException if given {@param filePath} does not exist
   * @throws IOException if given {@param filePath} does not have an image, or fails to load image
   */
  void loadImage(Path filePath) throws IllegalArgumentException, IOException, FileNotFoundException;

  /**
   * Applies a masks.Mask to the image currently loaded into this SeamUtilities.
   *
   * @param mask mask to apply to currently loaded image
   * @throws IllegalArgumentException if given {@param mask} is null
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   * @throws IllegalArgumentException if given {@param mask} contains coordinates that are outside
   *                                  the dimensions of the image currently loaded into this
   *                                  SeamUtilities
   */
  void applyMask(Mask mask)
      throws IllegalStateException, IOException, IllegalArgumentException;

  /**
   * Gives a new type of EnergyMap for this SeamUtilities to use when computing the energy map of
   * any image loaded, overwrites any previously assigned EnergyMaps.
   *
   * @param energyMap new EnergyMap for this SeamUtilities
   * @throws IllegalArgumentException if given {@param energyMap} is null
   */
  void assignEnergyMap(EnergyMap energyMap) throws IllegalArgumentException;

  /**
   * Returns a BufferedImage of the last computed {@code EnergyMap} of the image last loaded into
   * this SeamUtilities.
   *
   * @return last computed {@code EnergyMap} of last loaded image
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  BufferedImage getEnergyMap() throws IllegalStateException;

  /**
   * Saves the last loaded image in its latest state (after any edits have been made to it) to the
   * given {@param filePath}.
   *
   * @param filePath filePath to save the current image to
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   * @throws FileNotFoundException if given {@param filePath} does not exist
   * @throws IOException if given {@param filePath} fails to save current image
   */
  void saveCurrentImage(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException;

  /**
   * Retrieves the last loaded image in its latest state (after any edits have been made to it) to
   * it as a BufferedImage.
   *
   * @return BufferedImage of last loaded image in its current state post any edits applied to it
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  BufferedImage getCurrentImage() throws IllegalStateException;

  /**
   * Saves a animation (GIF, video, etc.) of the edits that have so far been last loaded image
   * - a process of the image from the state it was in when it was first loaded to the state it is
   * currently in now.
   *
   * @param filePath filePath to save the animation to
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   * @throws FileNotFoundException if given {@param filePath} does not exist
   * @throws IOException if given {@param filePath} fails to save the animation
   */
  void saveCurrentProcess(Path filePath)
      throws IllegalStateException, FileNotFoundException, IOException;

  /**
   * Removes the vertical seam with the least amount of energy from the last loaded image, as
   * determined by the last computed EnergyMap.
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  void removeVerticalSeam() throws IllegalStateException;

  /**
   * Inserts a vertical seam into the last loaded image, the nth insertion is the vertical seam
   * in the loaded image's starting state with the nth least amount of energy, as determined by the
   * last loaded EnergyMap.
   *
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  void insertVerticalSeam() throws IllegalStateException;

  /**
   * Removes the horizontal seam with the least amount of energy from the last loaded image, as
   * determined by the last computed EnergyMap.
   *
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  void removeHorizontalSeam() throws IllegalStateException;

  /**
   * Inserts a horizontal seam into the last loaded image, the nth insertion is the horizontal seam
   * in the loaded image's starting state with the nth least amount of energy, as determined by the
   * last computed EnergyMap.
   *
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  void insertHorizontalSeam() throws IllegalStateException;

  /**
   * Removes the diagonal seam (a horizontal and vertical seam pair) with the least amount of energy
   * from the last loaded image, as determined by the last computed EnergyMap.
   *
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  void removeDiagonalSeam() throws IllegalStateException;

  /**
   * Inserts a diagonal seam (a horizontal and vertical seam pair) into the last loaded image, the
   * nth insertion is the horizontal and vertical seams in the loaded image's starting state with
   * the nth least amount of energy, as determined by the last computed EnergyMap.
   *
   * @throws IllegalStateException if this SeamUtilities has not yet has an image loaded into it
   */
  void insertDiagonalSeam() throws IllegalStateException;
}
