package org.jacpfx.image.canvas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by amo on 11.04.14.
 */
public class ApplicationMain extends Application {

    private static Random rnd = new Random();

    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    private static final double MAX_HIGHT = 200d;
    private static final double MAX_WIDTH = 200d;
    private static final int HIGHT = 790;
    private static final int WIDTH = 590;
    private static final double PADDING = 5;

    @Override
    public void start(Stage stage) throws Exception {
        long startTime = System.currentTimeMillis();

        //Path rootFolder = FileSystems.getDefault().getPath("/home/pi/bilder/");
        Path rootFolder = FileSystems.getDefault().getPath("/Users/amo/Pictures/April_Mai/");
        final List<Path> subfolders = getSubfolders(rootFolder).parallelStream().filter(file -> file.toString().endsWith("jpg")).sequential().collect(Collectors.toList());
        StackPane root = new StackPane();
        Scene scene = new Scene(root, HIGHT,WIDTH);

        ImageFactory factory = new DefaultImageFactory();
        List<ImageContainer> all = subfolders.parallelStream().map(path -> getConatiner(path, factory)).collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms");

        CanvasPanel canvas = new CanvasPanel(HIGHT,WIDTH, PADDING, 0.1d,MAX_HIGHT, MAX_HIGHT);
        root.getChildren().add(canvas);

        canvas.widthProperty().bind(root.widthProperty().subtract(10));
        canvas.heightProperty().bind(root.heightProperty().subtract(10));

        stage.setTitle(getClass().getSimpleName());
        stage.setScene(scene);

        stage.show();

        canvas.getChildren().addAll(all);


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

    private ImageContainer getConatiner(Path path, ImageFactory factory) {
        return new ImageContainer(path, factory, MAX_HIGHT, MAX_WIDTH);
    }


}
