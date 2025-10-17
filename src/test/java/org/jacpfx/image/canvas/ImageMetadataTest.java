package org.jacpfx.image.canvas;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

class ImageMetadataTest {

    @Test
    void should_read_png_metadata() throws IOException {
        File file = new File("src/test/resources/images/blue.png");
        ImageMetadata metadata = new ImageMetadata(file);
        assertEquals("image/png", metadata.getMimeType());
        assertTrue(metadata.getWidth() > 0, "Width should be positive");
        assertTrue(metadata.getHeight() > 0, "Height should be positive");
    }

    @Test
    void should_read_jpeg_metadata() throws IOException {
        // Create a dummy jpg file for testing if it does not exist.
        File file = new File("src/test/resources/images/dummy.jpg");
        if (!file.exists()) {
            file.createNewFile();
            // Write minimal JPG header
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                fos.write(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xC0, 0x00, 0x11, 0x08, 0x00, 0x01, 0x00, 0x01, 0x03, 0x01, 0x22, 0x00, 0x02, 0x11, 0x01, 0x03, 0x11, 0x01});
            }
        }
        ImageMetadata metadata = new ImageMetadata(file);
        assertEquals("image/jpeg", metadata.getMimeType());
    }

    @Test
    void should_throw_exception_for_unsupported_type() {
        File file = new File("src/test/resources/images/dummy.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write("dummy text");
                }
            }
            assertThrows(IOException.class, () -> new ImageMetadata(file));
        } catch (IOException e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
}
