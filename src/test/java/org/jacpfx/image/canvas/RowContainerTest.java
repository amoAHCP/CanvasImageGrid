package org.jacpfx.image.canvas;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class RowContainerTest {

    @Test
    void should_add_image_and_get_images() {
        RowContainer rowContainer = new RowContainer();
        assertTrue(rowContainer.getImages().isEmpty(), "A new RowContainer should have no images.");

        ImageContainer imageContainer = new ImageContainer(Paths.get(""), new DefaultImageFactory(), 200, 200);
        rowContainer.add(imageContainer);

        assertEquals(1, rowContainer.getImages().size(), "RowContainer should have one image after adding one.");
        assertSame(imageContainer, rowContainer.getImages().get(0), "The retrieved image should be the same as the one added.");
    }

    @Test
    void should_set_and_get_row_hights_and_width() {
        RowContainer rowContainer = new RowContainer();
        rowContainer.setRowStartHight(50.0);
        assertEquals(50.0, rowContainer.getRowStartHight(), 0.001, "Start height should be set correctly.");

        rowContainer.setRowEndHight(250.0);
        assertEquals(250.0, rowContainer.getRowEndHight(), 0.001, "End height should be set correctly.");

        rowContainer.setMaxWitdht(800.0);
        assertEquals(800.0, rowContainer.getMaxWitdht(), 0.001, "Max width should be set correctly.");
    }
}
