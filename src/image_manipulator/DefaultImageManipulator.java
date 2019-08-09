package image_manipulator;

import java.io.IOException;
import java.nio.file.Path;

import energy_maps.AverageSurroundingGradient;
import energy_maps.EnergyMapMaker;
import masks.DefaultMask;
import masks.Mask;
import seam_manipulators.DefaultSeamManipulator;
import seam_manipulators.SeamManipulator;

/**
 * Default implementation of the {@link ImageManipulator} interface, provides implementations for each supported method
 * via a singular instantiation of a {@link SeamManipulator}.
 */
public class DefaultImageManipulator implements ImageManipulator {

    private final EnergyMapMaker defaultEnergyMapMaker;

    private DefaultImageManipulator() {
        defaultEnergyMapMaker = new AverageSurroundingGradient();
    }

    @Override
    public void resize(Path toImage, Path toSave, int newWidth, int newHeight) throws IOException {
        SeamManipulator seamManipulator = new DefaultSeamManipulator(toImage, defaultEnergyMapMaker, false);
        seamManipulator.resize(newWidth, newHeight);
        seamManipulator.saveCurrentImage(toSave);
    }

    @Override
    public void resizeHeight(Path toImage, Path toSave, int newHeight) {
        resize();
    }

    @Override
    public void resizeWidth(Path toImage, Path toSave, int newWidth) {

    }

    @Override
    public void resize(Path toImage, Path toSave, int newWidth, int newHeight, Path toProtect) {
        SeamManipulator seamManipulator = new DefaultSeamManipulator(toImage, defaultEnergyMapMaker, false);
        Mask toProtect = new DefaultMask(t)
        seamManipulator.resize(newWidth, newHeight, toProtect);
        seamManipulator.saveCurrentImage(toSave);
    }

    @Override
    public void resizeHeight(Path toImage, Path toSave, int newHeight, Path toProtect) {

    }

    @Override
    public void resizeWidth(Path toImage, Path toSave, int newWidth, int toProtect) {

    }

    @Override
    public void removeArea(Path toImage, Path toSave, Path toRemove) {

    }

    @Override
    public void replaceArea(Path toImage, Path toSave, Path toReplace) {

    }
}
