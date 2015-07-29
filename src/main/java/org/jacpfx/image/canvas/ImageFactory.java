package org.jacpfx.image.canvas;

import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Andy Moncsek on 14.04.14.
 */
public interface ImageFactory {

    Image createImage(Path imagePath, double maxWidth, double maxHight) throws Exception;


    default Image postProcess(Image image,double maxHight, double maxWidth) {


        return image;
    }

    /**
     * retrieve image size, while key = width and value = hight
     * @param imagePath
     * @param maxHight
     * @return
     * @throws IOException
     */
    default Map.Entry<Double,Double> getImageSize(Path imagePath,double maxHight) throws IOException {
        ImageMetadata metadata = new ImageMetadata(imagePath.toFile());
        return new Map.Entry<Double,Double>(){

            @Override
            public Double getKey() {
                return getTargetWidth(metadata,maxHight);
            }

            @Override
            public Double getValue() {
                return getTargetHight(maxHight);
            }

            @Override
            public Double setValue(Double value) {
                return null;
            }
        };
    }

    default double getTargetHight(double maxHight) {
        return maxHight * 2;
    }

    default double getTargetWidth(ImageMetadata metadata, double maxHight) {
        int nativeWidth = metadata.getWidth();
        int nativeHight = metadata.getHeight();
        double targetHight = getTargetHight(maxHight);
        double scalingFactor = Double.valueOf(nativeHight) / Double.valueOf(targetHight);
        return nativeWidth / scalingFactor;
    }

}
