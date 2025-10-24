package org.jacpfx.image.canvas;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Andy Moncsek on 14.04.14.
 */
public class DefaultImageFactory implements ImageFactory {
    @Override
    public Image createImage(Path imagePath,double maxWidth, double maxHight) throws Exception{
        try (InputStream is = Files.newInputStream(imagePath)) {
            return new Image(is, 0d, maxHight * 2, true, false);
        }
    }
}
