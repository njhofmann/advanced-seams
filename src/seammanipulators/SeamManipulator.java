package seammanipulators;

import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.IOException;
import masks.Mask;

/**
 * Interface for implementing the operations of image resizing and object deletion with content
 * aware image resizing upon a given image, then saving the resulting image and a GIF of whichever
 * called process.
 */
public interface SeamManipulator {

  /**
   * Resizes the starting image at the given {@param input} file path to the given new width
   * and height using content aware image resizing. Saves the resulting image to the file
   * path location given by {@param outputImage}, and an animated GIF of the resizing process to
   * to the path location given by {@param outputGIF}.
   *
   * @param newWidth new width to size starting image to
   * @param newHeight new height to size starting image to
   * @param input file path of starting image
   * @param outputImage file path to save result of resizing process applied to starting image
   * @param outputGIF file path to save GIF of resulting resizing process applied to starting image
   * @throws IllegalArgumentException if any of the given input parameters are null, or if given
   *                                  mask contains coordinates that are outside the range the
   *                                  starting image's size
   * @throws FileNotFoundException if any of the given file paths do not exist
   * @throws IOException if starting image could not be read, or if could not save resulting image
   *                     or GIF
   */
  void resize(int newWidth, int newHeight, Path input, Path outputImage, Path outputGIF)
      throws IllegalArgumentException, FileNotFoundException, IOException;

  /**
   * Same method as {@code resize}, but with a {@code Mask} object applied to the input image.
   *
   * @param newWidth new width to size starting image to
   * @param newHeight new height to size starting image to
   * @param input file path of starting image
   * @param outputImage file path to save result of resizing process applied to starting image
   * @param outputGIF file path to save GIF of resulting resizing process applied to starting image
   * @param mask mask to apply to starting image, designated what parts of the starting image to
   *             ignore coordinate wise
   * @throws IllegalArgumentException if any of the given input parameters are null, or if given
   *                                  mask contains coordinates that are outside the range the
   *                                  starting image's size
   * @throws FileNotFoundException if any of the given file paths do not exist
   * @throws IOException if starting image could not be read, or if could not save resulting image
   *                     or GIF
   */
  void resize(int newWidth, int newHeight, Path input, Path outputImage, Path outputGIF,
      Mask mask) throws IllegalArgumentException, FileNotFoundException, IOException;

  /**
   * For the starting image given by the {@param input} file path, removes and replaces all parts
   * of the image as given by the given input {@code mask} using content aware image resizing.
   *
   * @param input file path of starting image
   * @param outputImage file path to save result of resizing process applied to starting image
   * @param outputGIF file path to save GIF of resulting resizing process applied to starting image
   * @param mask mask to apply to starting image, designated what parts of the starting image to
   *             remove and replace
   * @throws IllegalArgumentException if any of the given input parameters are null, or if given
   *                                  mask contains coordinates that are outside the range the
   *                                  starting image's size
   * @throws FileNotFoundException if any of the given file paths do not exist
   * @throws IOException if starting image could not be read, or if could not save resulting image
   *                     or GIF
   */
  void deleteObject(Path input, Path outputImage, Path outputGIF, Mask mask)
      throws IllegalArgumentException, FileNotFoundException, IOException;
}
