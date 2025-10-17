package org.jacpfx.image.canvas;

import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CanvasPanelTest extends ApplicationTest {

    private CanvasPanel canvasPanel;
    private static List<Path> imagePaths;
    private final AtomicBoolean selectionListenerCalled = new AtomicBoolean(false);

    @BeforeAll
    public static void setupImages() throws IOException {
        Path imagesDir = new File("src/test/resources/images").toPath();
        try (Stream<Path> stream = Files.list(imagesDir)) {
            imagePaths = stream.collect(Collectors.toList());
        }
    }

    @Override
    public void start(Stage stage) {
        canvasPanel = CanvasPanel.createCanvasPanel()
                .imagePath(imagePaths)
                .imageFactory(new DefaultImageFactory())
                .width(800)
                .hight(600)
                .padding(5.0)
                .lineBreakLimit(0.1)
                .maxImageWidth(200)
                .maxImageHight(200)
                .selectionListener((x, y, image) -> selectionListenerCalled.set(true));

        StackPane pane = new StackPane(canvasPanel);
        Scene scene = new Scene(pane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void should_load_images_on_creation() {
        assertNotNull(canvasPanel);
        assertFalse(canvasPanel.getChildren().isEmpty());
        assertEquals(imagePaths.size(), canvasPanel.getChildren().size());
    }

    @Test
    void should_trigger_selection_listener_on_click(FxRobot robot) {
        // Click on the first image (approximated position)
        robot.clickOn(canvasPanel, MouseButton.PRIMARY);
        assertTrue(selectionListenerCalled.get(), "Selection listener should be called on click");
    }

    @Test
    void should_zoom_in_and_out(FxRobot robot) {
        double initialZoom = canvasPanel.zoomFactorProperty().get();
        robot.scroll(10, javafx.geometry.VerticalDirection.UP); // Simulate zoom in
        double zoomedInFactor = canvasPanel.zoomFactorProperty().get();
        assertTrue(zoomedInFactor > initialZoom, "Zoom factor should increase on zoom in");

        robot.scroll(10, javafx.geometry.VerticalDirection.DOWN); // Simulate zoom out
        double zoomedOutFactor = canvasPanel.zoomFactorProperty().get();
        assertTrue(zoomedOutFactor < zoomedInFactor, "Zoom factor should decrease on zoom out");
    }
}
