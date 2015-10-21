package org.jacpfx.image.canvas;

import javafx.scene.image.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Andy Moncsek on 14.04.14.
 */
public class SquareImageFactory implements ImageFactory {
    @Override
    public Image createImage(Path imagePath, double maxWidth, double maxHight) throws Exception {
        return new Image(imagePath.toFile().toURI().toURL().toExternalForm(), 0d, maxHight * 2, true, false, true);

    }

    @Override
    public Image postProcess(Image src, double maxHight, double maxWidth) {
        final PixelReader reader = src.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraPreInstance();

        final int width = (int) src.getWidth();
        final int height = (int) src.getHeight();
        WritableImage dest = null;
        if (width > height) {
            byte[] rowBuffer = new byte[height * width * 4]; // * 3 to hold RGB

            reader.getPixels(width / 4, 0, height, height, format, rowBuffer, 0, width * 4);
            dest = new WritableImage(height, height);
            final PixelWriter writer = dest.getPixelWriter();
            writer.setPixels(0, 0, height, height, format, rowBuffer, 0, width * 4);
        } else {
            byte[] rowBuffer = new byte[height * width * 4]; // * 3 to hold RGB

            reader.getPixels(0, height / 4, width, width, format, rowBuffer, 0, width * 4);
            dest = new WritableImage(width, width);
            final PixelWriter writer = dest.getPixelWriter();
            writer.setPixels(0, 0, width, width, format, rowBuffer, 0, width * 4);
        }

        final ImageView originalView = new ImageView(dest);
        return originalView.snapshot(null, null);
    }



    @Override
    public Map.Entry<Double, Double> getImageSize(Path imagePath, double maxHight) throws IOException {
        return new Map.Entry<Double, Double>() {

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
