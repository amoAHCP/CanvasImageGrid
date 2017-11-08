package org.jacpfx.image.canvas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by amo on 11.04.14.
 */
public class ApplicationMainSingleWindow extends Application {


    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    private static final double MAX_HIGHT = 150;
    private static final double MAX_WIDTH = 150;
    private static final int HIGHT = 1024;
    private static final int WIDTH = 790;
    private static final double PADDING = 5;
    private Label fpsLabel;

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("javafx.animation.fullspeed", "true");
        long startTime = System.currentTimeMillis();

        Path rootFolder = FileSystems.getDefault().getPath("/Users/amo/Pictures/andydd_mila-schulfotos-2016/");
        final List<Path> subfolders = getSubfolders(rootFolder).parallelStream().filter(file -> file.toString().endsWith("jpg")).sequential().collect(Collectors.toList());

        VBox main = new VBox();
        StackPane root = new StackPane();
        VBox.setVgrow(root, Priority.ALWAYS);
        Scene scene = new Scene(main, WIDTH, HIGHT);


        ImageFactory factory = new DefaultImageFactory();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");


        main.getChildren().addAll(root);
        stage.setTitle(getClass().getSimpleName());
        stage.setScene(scene);

        stage.show();
        CanvasPanel canvas = CanvasPanel.createCanvasPanel().
                imagePath(subfolders).
                imageFactory(factory).
                width(WIDTH).
                hight(HIGHT).
                padding(PADDING).
                lineBreakLimit(0.1d).
                maxImageWidth(MAX_WIDTH).
                maxImageHight(MAX_HIGHT).
                selectionListener((x, y, image) -> {
                    if (image.length == 1) {
                        ImageContainer myImage = image[0];
                        System.out.println("selected image: "+myImage.getImagePath().toString());
                    }
                });


        canvas.widthProperty().bind(root.widthProperty().subtract(10));
        canvas.heightProperty().bind(root.heightProperty().subtract(10));

        fpsLabel = new Label("FPS:");
        fpsLabel.setStyle("-fx-font-size: 1em;-fx-text-fill: white;");


        root.getChildren().addAll(canvas, fpsLabel);




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
