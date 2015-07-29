package org.jacpfx.image.canvas;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Andy Moncsek on 14.04.14.
 */
public class SquareImageFactory implements ImageFactory {
    @Override
    public Image createImage(Path imagePath,double maxWidth, double maxHight) throws Exception{
        return new Image(imagePath.toFile().toURI().toURL().toExternalForm(),0d,maxHight*2,true,false,true);

    }
    @Override
    public Image postProcess(Image image,double maxHight, double maxWidth) {
        ImageView maskView = new ImageView();
        maskView.setPreserveRatio(true);
        maskView.setFitWidth(maxWidth);
        maskView.setSmooth(false);
        maskView.setImage(image);
        maskView.setClip(initLayer(image, Color.WHITE, 1.0,maxHight,maxWidth));

        return maskView.snapshot(null, null);
    }

    private Rectangle initLayer(Image image, Color color, double opacity,double maxHight, double maxWidth) {
        Rectangle rectangle = null;
        if (image.getWidth() > image.getHeight()) {
            rectangle = new Rectangle(maxWidth / 4, 0, maxHight, maxHight);
        } else {
            rectangle = new Rectangle(0, maxHight / 4, maxWidth, maxWidth);
        }

        rectangle.setFill(color);
        rectangle.setOpacity(opacity);
        return rectangle;
    }

    @Override
    public Map.Entry<Double,Double> getImageSize(Path imagePath,double maxHight) throws IOException {
        return new Map.Entry<Double,Double>(){

            @Override
            public Double getKey() {
                return maxHight;
            }

            @Override
            public Double getValue() {
                return maxHight;
            }

            @Override
            public Double setValue(Double value) {
                return null;
            }
        };
    }
}
