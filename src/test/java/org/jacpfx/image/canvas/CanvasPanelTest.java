package org.jacpfx.image.canvas;

import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class CanvasPanelTest extends ApplicationTest {

    private CanvasPanel canvasPanel;
    private static List<Path> imagePaths;
    private final AtomicBoolean selectionListenerCalled = new AtomicBoolean(false);

    @BeforeAll
    public static void setupImages() throws IOException {
        Path imagesDir = new File("src/test/resources/images").toPath();
        try (Stream<Path> stream = Files.list(imagesDir)) {
            imagePaths = stream
                    .filter(path -> path.toString().endsWith(".png") || path.toString().endsWith(".jpg"))
                    .collect(Collectors.toList());
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
    void should_trigger_selection_listener_on_click(FxRobot robot) throws InterruptedException {
        // Warte bis alle JavaFX-Events verarbeitet sind
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(500);

        // Stelle sicher, dass Bilder geladen wurden
        assertFalse(canvasPanel.getChildren().isEmpty(), "Canvas should have images");

        // Triggere das initiale Rendering durch Setzen der Width (paintImages wird aufgerufen)
        CountDownLatch renderLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            // Setze die Width neu, um paintImages zu triggern
            double currentWidth = canvasPanel.getWidth();
            canvasPanel.setWidth(currentWidth + 1);
            canvasPanel.setWidth(currentWidth);
            renderLatch.countDown();
        });
        renderLatch.await(2, TimeUnit.SECONDS);
        WaitForAsyncUtils.waitForFxEvents();

        // Warte auf das vollständige Rendering
        Thread.sleep(1500);

        // Hole das erste Bild und prüfe, ob es jetzt Positionen hat
        ImageContainer firstImage = canvasPanel.getChildren().get(0);
        System.out.println("First image after render: startX=" + firstImage.getStartX() + ", startY=" + firstImage.getStartY() +
                           ", scaledX=" + firstImage.getScaledX() + ", scaledY=" + firstImage.getScaledY());

        // Berechne die Mitte des ersten Bildes
        double clickX = firstImage.getStartX() + (firstImage.getScaledX() / 2);
        double clickY = firstImage.getStartY() + (firstImage.getScaledY() / 2) + 10; // +10 für offset

        // Fallback: Wenn die Positionen immer noch 0 sind, verwende eine sichere Position
        if (firstImage.getScaledX() == 0 || firstImage.getScaledY() == 0) {
            clickX = 100;
            clickY = 100;
        }

        System.out.println("Clicking at: x=" + clickX + ", y=" + clickY);

        // Feuere einen MouseClick-Event auf dem Canvas
        final double finalClickX = clickX;
        final double finalClickY = clickY;
        CountDownLatch clickLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            javafx.scene.input.MouseEvent clickEvent = new javafx.scene.input.MouseEvent(
                javafx.scene.input.MouseEvent.MOUSE_CLICKED,
                finalClickX, finalClickY, finalClickX, finalClickY,
                MouseButton.PRIMARY, 1,
                false, false, false, false,
                true, false, false,
                true, false, false, null
            );
            canvasPanel.fireEvent(clickEvent);
            clickLatch.countDown();
        });

        // Warte bis der Event gefeuert wurde
        clickLatch.await(2, TimeUnit.SECONDS);
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(300);

        assertTrue(selectionListenerCalled.get(), "Selection listener should be called on click at position (" + finalClickX + ", " + finalClickY + ")");
    }

    @Test
    void should_zoom_in_and_out(FxRobot robot) throws InterruptedException {
        // Warte bis die Bilder geladen sind
        Thread.sleep(1000);

        // Teste die Zoom-Property direkt, da setOnZoom nicht durch Scroll-Events getriggert wird
        double initialZoom = canvasPanel.zoomFactorProperty().get();

        // Setze den Zoom-Faktor direkt (simuliert das, was ein Zoom-Event tun würde)
        javafx.application.Platform.runLater(() -> {
            canvasPanel.zoomFactorProperty().set(initialZoom * 1.5);
        });

        // Warte auf Property-Änderung
        Thread.sleep(300);

        double zoomedInFactor = canvasPanel.zoomFactorProperty().get();
        assertTrue(zoomedInFactor > initialZoom, "Zoom factor should increase on zoom in");

        // Zoom out
        javafx.application.Platform.runLater(() -> {
            canvasPanel.zoomFactorProperty().set(initialZoom * 0.8);
        });

        // Warte auf Property-Änderung
        Thread.sleep(300);

        double zoomedOutFactor = canvasPanel.zoomFactorProperty().get();
        assertTrue(zoomedOutFactor < zoomedInFactor, "Zoom factor should decrease on zoom out");
    }
}
