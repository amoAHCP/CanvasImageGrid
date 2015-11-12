package org.jacpfx.image.node;

import com.sun.javafx.perf.PerformanceTracker;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jacpfx.image.canvas.DefaultImageFactory;
import org.jacpfx.image.canvas.ImageFactory;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by amo on 11.04.14.
 */
public class NodeApplicationMainSingleWindowFPS extends Application {


    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    private static final double MAX_HIGHT = 150;
    private static final double MAX_WIDTH = 150;
    private static final int HIGHT = 1024;
    private static final int WIDTH = 710;
    private static final double PADDING = 5;
    private Label fpsLabel;
    private PerformanceTracker tracker;

    private AtomicLong counter = new AtomicLong(0);

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("javafx.animation.fullspeed", "true");
        long startTime = System.currentTimeMillis();

        Path rootFolder = FileSystems.getDefault().getPath("/Users/amo/Pictures/demo/");
        final List<Path> subfolders = getSubfolders(rootFolder).parallelStream().filter(file -> file.toString().endsWith("jpg")).sequential().collect(Collectors.toList());

        VBox main = new VBox();
        VBox imageBox = new VBox();
        imageBox.setStyle("-fx-background-color: gainsboro");
        StackPane root = new StackPane();
        Scene scene = new Scene(main, WIDTH, HIGHT);
        imageBox.setPrefHeight(80);
        root.setPrefHeight(1024);


        ImageFactory factory = new DefaultImageFactory();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");


        main.getChildren().addAll(imageBox, root);
        stage.setTitle(getClass().getSimpleName());
        stage.setScene(scene);

        stage.show();

        NodePanel canvas = NodePanel.createCanvasPanel().
                imagePath(subfolders).
                imageFactory(factory).
                width(WIDTH).
                hight(HIGHT).
                padding(PADDING).
                lineBreakLimit(0.1d).
                maxImageWidth(MAX_WIDTH).
                maxImageHight(MAX_HIGHT).selectionListener(null);

         canvas.paint();
        ScrollPane container = new ScrollPane(canvas);
        container.setFitToHeight(true);
        container.setFitToWidth(true);
        fpsLabel = new Label("FPS:");
        fpsLabel.setOnMouseClicked((event) -> {
            tracker.resetAverageFPS();
        });
        fpsLabel.setStyle("-fx-font-size: 5em;-fx-text-fill: red;");

        canvas.setPrefHeight(HIGHT);
        canvas.setPrefWidth(WIDTH);
        createPerformanceTracker(scene);
        imageBox.getChildren().add(fpsLabel);
        root.getChildren().addAll(container);


       // canvas.setCache(true);
       // canvas.setCacheHint(CacheHint.SCALE);

    }

    public void createPerformanceTracker(Scene scene) {
        tracker = PerformanceTracker.getSceneTracker(scene);
        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {

                float fps = getFPS();
                fpsLabel.setText(String.format("Current fps: %.0f fps", fps));

            }
        };

        frameRateMeter.start();
    }

    private float getFPS() {
        float fps = tracker.getAverageFPS();
        if(counter.incrementAndGet()%100==0) {
            tracker.resetAverageFPS();
            counter.set(0);
        }
        return fps;
    }

    private List<Path> getSubfolders(Path root) {
        final List<Path> roots = new ArrayList<>();
        try (DirectoryStream<Path> folders = Files.newDirectoryStream(root)) {
            // level one check, can have entries like floppy, so check level two
            for (final Path pathElement : folders) {
                roots.add(pathElement);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return roots;
    }


}
