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

class DefaultImageFactoryTest extends ApplicationTest {

    private static Path RED_IMAGE_PATH;
    private static ImageFactory DEFAULT_IMAGE_FACTORY;

    @BeforeAll
    static void setup() {
        DEFAULT_IMAGE_FACTORY = new DefaultImageFactory();
        File file = new File("src/test/resources/images/red.png");
        RED_IMAGE_PATH = file.toPath();
    }

    @Test
    void should_create_image_with_correct_dimensions() {
        try {
            Image image = DEFAULT_IMAGE_FACTORY.createImage(RED_IMAGE_PATH, 200, 150);
            assertNotNull(image);
            // Default factory loads image with 2x maxHight
            assertEquals(300, image.getHeight(), "Image height should be 2 * maxHight");
        } catch (Exception e) {
            fail("Image creation should not fail.", e);
        }
    }
}
