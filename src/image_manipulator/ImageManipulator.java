package image_manipulator;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Describes set of manipulations that can be carried out on a given image as represented by some
 * file path such as image resizing, object replacement, object removal, etc. which options to
 * save the resulting image and manipulation process. Each method is suppose to represent a
 * singular and isolated operation, takes and saves an image.
 */
public interface ImageManipulator {

    void resize(Path toImage, Path toSave, int newWidth, int newHeight) throws IOException;

    void resizeHeight(Path toImage, Path toSave, int newHeight) throws IOException;

    void resizeWidth(Path toImage, Path toSave, int newWidth) throws IOException;

    void resize(Path toImage, Path toSave, int newWidth, int newHeight, Path toProtect) throws IOException;

    void resizeHeight(Path toImage, Path toSave, int newHeight, Path toProtect) throws IOException;

    void resizeWidth(Path toImage, Path toSave, int newWidth, int toProtect) throws IOException;

    void removeArea(Path toImage, Path toSave, Path toRemove) throws IOException;

    void replaceArea(Path toImage, Path toSave, Path toReplace) throws IOException;
}
