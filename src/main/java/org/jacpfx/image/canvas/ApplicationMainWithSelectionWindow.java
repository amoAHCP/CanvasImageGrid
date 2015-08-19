package org.jacpfx.image.canvas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.MalformedURLException;
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
public class ApplicationMainWithSelectionWindow extends Application {


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

    @Override
    public void start(Stage stage) throws Exception {
        long startTime = System.currentTimeMillis();

        Path rootFolder = FileSystems.getDefault().getPath("/Users/amo/Pictures/April_Mai/");
        final List<Path> subfolders = getSubfolders(rootFolder).parallelStream().filter(file -> file.toString().endsWith("jpg")).sequential().collect(Collectors.toList());

        VBox main = new VBox();
        VBox imageBox = new VBox();
        VBox.setVgrow(imageBox, Priority.ALWAYS);
        StackPane root = new StackPane();
        Scene scene = new Scene(main, WIDTH, HIGHT);
        imageBox.setPrefHeight(300);
        root.setPrefHeight(490);


        ImageFactory factory = new DefaultImageFactory();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");


        main.getChildren().addAll(root, imageBox);
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
                        try {
                            Image i = new Image(myImage.getImagePath().toFile().toURI().toURL().toExternalForm(), imageBox.getWidth(), imageBox.getHeight(), true, false, false);
                            ImageView view = new ImageView(i);

                            imageBox.getChildren().setAll(view);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });


        canvas.widthProperty().bind(root.widthProperty().subtract(10));
        canvas.heightProperty().bind(root.heightProperty().subtract(10));
        root.getChildren().add(canvas);




    }

    private List<java.nio.file.Path> getSubfolders(java.nio.file.Path root) {
        final List<java.nio.file.Path> roots = new ArrayList<>();
        try (DirectoryStream<Path> folders = Files.newDirectoryStream(root)) {
            // level one check, can have entries like floppy, so check level two
            for (final java.nio.file.Path pathElement : folders) {
                roots.add(pathElement);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return roots;
    }




}
