package org.jacpfx.image.canvas;

import javafx.scene.image.Image;

import java.nio.file.Path;

/**
 * Created by Andy Moncsek on 14.04.14.
 */
public interface ImageFactory {

    Image createImage(Path imagePath,double maxWidth,double maxHight) throws Exception ;
}
