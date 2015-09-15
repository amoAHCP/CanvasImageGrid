package org.jacpfx.image.canvas;

/**
 * Created by Andy Moncsek on 25.08.15.
 */
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.IntBuffer;
import java.util.Arrays;

public class WritableImageDemo extends Application {

    private Image src;
    private WritableImage dest;
    private int kernelSize = 1;
    private int width;
    private int height;

    private RadioButton blurButton;
    private RadioButton blur2Button;
    private RadioButton mosaicButton;

    @Override
    public void start(Stage stage) {

        AnchorPane root = new AnchorPane();

        initImage(root);

        Scene scene = new Scene(root);

        stage.setTitle("WritableImage Demo");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void initImage(AnchorPane root) {
        src = new Image("file:/Users/amo/Pictures/April_Mai/DSCF5136.jpg",640, 480, false, false);
        ImageView srcView = new ImageView(src);
        root.getChildren().add(srcView);
        AnchorPane.setTopAnchor(srcView, 0.0);
        AnchorPane.setLeftAnchor(srcView, 0.0);

        width = (int) src.getWidth();
        height = (int) src.getHeight();
        root.setPrefSize(width * 2.0, height + 50);

        dest = new WritableImage(width, height);
        ImageView destView = new ImageView(dest);
        destView.setTranslateX(width);
        root.getChildren().add(destView);
        AnchorPane.setTopAnchor(destView, 0.0);
        AnchorPane.setRightAnchor(destView, (double) width);

        Slider slider = new Slider(0, 10, kernelSize);
        slider.setPrefSize(width, 50);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        slider.setMajorTickUnit(1.0);
        slider.setMinorTickCount(0);

        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                DoubleProperty value = (DoubleProperty) o;
                int intValue = (int) value.get();
                if (intValue != kernelSize) {
                    kernelSize = intValue;
                    if (blurButton.isSelected()) {
                        copy();
                    } else if (blur2Button.isSelected()) {
                        blur2();
                    } else {
                        mosaic();
                    }
                }
            }
        });

        root.getChildren().add(slider);
        AnchorPane.setBottomAnchor(slider, 0.0);
        AnchorPane.setRightAnchor(slider, 10.0);

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPrefWidth(width);
        hbox.setPrefHeight(50);
        root.getChildren().add(hbox);
        AnchorPane.setBottomAnchor(hbox, 0.0);
        AnchorPane.setLeftAnchor(hbox, 10.0);

        ToggleGroup group = new ToggleGroup();
        blurButton = new RadioButton("Blur");
        blurButton.setToggleGroup(group);
        blurButton.setSelected(true);
        hbox.getChildren().add(blurButton);
        blur2Button = new RadioButton("Blur2");
        blur2Button.setToggleGroup(group);
        hbox.getChildren().add(blur2Button);
        mosaicButton = new RadioButton("Mosaic");
        mosaicButton.setToggleGroup(group);
        hbox.getChildren().add(mosaicButton);

        blur();
    }

    private void blur() {
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = dest.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double red = 0;
                double green = 0;
                double blue = 0;
                double alpha = 0;
                int count = 0;
                for (int i = -kernelSize; i <= kernelSize; i++) {
                    for (int j = -kernelSize; j <= kernelSize; j++) {
                        if (x + i < 0 || x + i >= width
                                || y + j < 0 || y + j >= height) {
                            continue;
                        }
                        Color color = reader.getColor(x + i, y + j);
                        red += color.getRed();
                        green += color.getGreen();
                        blue += color.getBlue();
                        alpha += color.getOpacity();
                        count++;
                    }
                }
                Color blurColor = Color.color(red / count,
                        green / count,
                        blue / count,
                        alpha / count);
                writer.setColor(x, y, blurColor);
            }
        }
    }

    private void blur2() {
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = dest.getPixelWriter();
        WritablePixelFormat<IntBuffer> format
                = WritablePixelFormat.getIntArgbInstance();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int centerX = x - kernelSize;
                int centerY = y - kernelSize;
                int kernelWidth = kernelSize * 2 + 1;
                int kernelHeight = kernelSize * 2 + 1;

                if (centerX < 0) {
                    centerX = 0;
                    kernelWidth = x + kernelSize;
                } else if (x + kernelSize >= width) {
                    kernelWidth = width - centerX;
                }

                if (centerY < 0) {
                    centerY = 0;
                    kernelHeight = y + kernelSize;
                } else if (y + kernelSize >= height) {
                    kernelHeight = height - centerY;
                }

                int[] buffer = new int[kernelWidth * kernelHeight];
                reader.getPixels(centerX, centerY,
                        kernelWidth, kernelHeight,
                        format, buffer, 0, kernelWidth);

                int alpha = 0;
                int red = 0;
                int green = 0;
                int blue = 0;

                for (int color : buffer) {
                    alpha += (color >>> 24);
                    red += (color >>> 16 & 0xFF);
                    green += (color >>> 8 & 0xFF);
                    blue += (color & 0xFF);
                }
                alpha = alpha / kernelWidth / kernelHeight;
                red = red / kernelWidth / kernelHeight;
                green = green / kernelWidth / kernelHeight;
                blue = blue / kernelWidth / kernelHeight;

                int blurColor = (alpha << 24)
                        + (red << 16)
                        + (green << 8)
                        + blue;
                writer.setArgb(x, y, blurColor);
            }
        }
    }

    private void mosaic() {
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = dest.getPixelWriter();
        WritablePixelFormat<IntBuffer> format
                = WritablePixelFormat.getIntArgbInstance();

        for (int x = kernelSize; x < width - kernelSize * 2; x += kernelSize * 2 + 1) {
            for (int y = kernelSize; y < height - kernelSize * 2; y += kernelSize * 2 + 1) {
                int kernelWidth = kernelSize * 2 + 1;
                int kernelHeight = kernelSize * 2 + 1;

                int[] buffer = new int[kernelWidth * kernelHeight];
                reader.getPixels(x, y,
                        kernelWidth, kernelHeight,
                        format, buffer, 0, kernelWidth);

                int alpha = 0;
                int red = 0;
                int green = 0;
                int blue = 0;

                for (int color : buffer) {
                    alpha += (color >>> 24);
                    red += (color >>> 16 & 0xFF);
                    green += (color >>> 8 & 0xFF);
                    blue += (color & 0xFF);
                }
                alpha = alpha / kernelWidth / kernelHeight;
                red = red / kernelWidth / kernelHeight;
                green = green / kernelWidth / kernelHeight;
                blue = blue / kernelWidth / kernelHeight;

                int blurColor = (alpha << 24)
                        + (red << 16)
                        + (green << 8)
                        + blue;
                Arrays.fill(buffer, blurColor);
                writer.setPixels(x, y,
                        kernelWidth, kernelHeight,
                        format, buffer, 0, kernelWidth);
            }
        }
    }

    private void copy() {
        PixelReader reader = src.getPixelReader();
        PixelWriter writer = dest.getPixelWriter();
        WritablePixelFormat<IntBuffer> format
                = WritablePixelFormat.getIntArgbInstance();

        PixelFormat f = reader.getPixelFormat();

        WritablePixelFormat wf = f.getIntArgbInstance(); //???

        int[] buffer = new int[(int)src.getWidth()*(int)src.getHeight()*4];

        reader.getPixels(0,0, (int)src.getWidth(), (int)src.getHeight(), wf, buffer, 0, 0);

       // writer.setPixels();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // reading a pixel from src image,
                // then writing a pixel to dest image
                //Color color = reader.getColor(x, y);
                //writer.setColor(x, y, color);

                // this way is also OK
            int argb = reader.getArgb(x, y);
            writer.setArgb(x, y, argb);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}