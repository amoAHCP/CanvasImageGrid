package org.jacpfx.image.canvas;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.image.Image;

class SquareImageFactoryTest extends ApplicationTest {

    private static Path RED_IMAGE_PATH;
    private static ImageFactory SQUARE_IMAGE_FACTORY;

    @BeforeAll
    static void setup() {
        SQUARE_IMAGE_FACTORY = new SquareImageFactory();
        File file = new File("src/test/resources/images/red.png");
        RED_IMAGE_PATH = file.toPath();
    }

    @Test
    void should_create_square_image() {
        try {
            Image image = SQUARE_IMAGE_FACTORY.createImage(RED_IMAGE_PATH, 200, 150);
            final Image[] processedImage = new Image[1];

            // postProcess muss auf dem JavaFX Application Thread laufen
            javafx.application.Platform.runLater(() -> {
                try {
                    processedImage[0] = SQUARE_IMAGE_FACTORY.postProcess(image, 150, 200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Warte, bis der JavaFX Thread fertig ist
            Thread.sleep(500);

            assertNotNull(processedImage[0]);
            assertEquals(processedImage[0].getWidth(), processedImage[0].getHeight(), "Processed image should be a square");
        } catch (Exception e) {
            fail("Image creation and processing should not fail.", e);
        }
    }
}
