package org.jacpfx.image.canvas;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.image.Image;

public class ImageContainerTest extends ApplicationTest {

    private static Path RED_IMAGE_PATH;
    private static ImageFactory DEFAULT_IMAGE_FACTORY;

    @BeforeAll
    public static void setup() {
        DEFAULT_IMAGE_FACTORY = new DefaultImageFactory();
        File file = new File("src/test/resources/images/red.png");
        RED_IMAGE_PATH = file.toPath();
    }

    @Test
    public void testImageContainerCreation() {
        ImageContainer container = new ImageContainer(RED_IMAGE_PATH, DEFAULT_IMAGE_FACTORY, 200, 200);
        assertNotNull(container);
        assertEquals(RED_IMAGE_PATH, container.getImagePath());
    }

    @Test
    public void testGetImage() {
        ImageContainer container = new ImageContainer(RED_IMAGE_PATH, DEFAULT_IMAGE_FACTORY, 200, 200);
        Image image = container.getImage();
        assertNotNull(image);
        // Updated to 1x maxHeight for better memory efficiency
        assertEquals(200, image.getHeight());
    }
}
