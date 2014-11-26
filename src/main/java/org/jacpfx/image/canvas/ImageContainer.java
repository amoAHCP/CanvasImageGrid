package org.jacpfx.image.canvas;

import javafx.scene.image.Image;

import java.nio.file.Path;

/**
 * Created by amo on 11.04.14.
 */
public class ImageContainer implements Cloneable {
    /**
     * start point x
     */
    private double startX;
    /**
     * start point y
     */
    private double startY;
    /**
     * end point x (the image width)
     */
    private double endX;
    /**
     * end point y (the image hight)
     */
    private double endY;
    /**
     * the scaled point x
     */
    private double scaledX;
    /**
     * the scaled point y
     */
    private double scaledY;
    /**
     * image in landscape
     */
    private boolean landsScape;
    /**
     * zooming/scaling factor
     */
    private double scaleFactor = 1;
    /**
     * the path to image
     */
    private Path imagePath;
    /**
     * the image (maxHight * 2)
     */
    private Image image;
    /**
     * The image creation factory
     */
    private ImageFactory factory;
    /**
     * maximum hight
     */
    private double maxHight;
    /**
     * maximum width
     */
    private double maxWidth;

    /**
     * position in row
     */
    private int position=0;

    public ImageContainer(Path imagePath, ImageFactory factory, double maxHight, double maxWidth) {
        this.imagePath = imagePath;
        this.factory = factory;
        this.maxHight = maxHight;
        this.maxWidth = maxWidth;
        if (this.imagePath != null) {
            try {
                image = this.factory.createImage(this.imagePath, maxWidth, maxHight);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (image != null) {
                endX = image.getWidth();
                endY = image.getHeight();
                this.landsScape = endX > endY;

            }
        }

    }


    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public boolean isLandsScape() {
        return landsScape;
    }

    public void setLandsScape(boolean landsScape) {
        this.landsScape = landsScape;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        this.scaledX = this.endX * scaleFactor;
        this.scaledY = this.endY * scaleFactor;
    }

    public double getScaledX() {
        return this.scaledX;
    }

    public double getScaledY() {
        return this.scaledY;
    }


    public Image getScaledImage() {
        return this.image;
    }

    public Path getImagePath() {
        return imagePath;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "ImageContainer{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", scaledX=" + scaledX +
                ", scaledY=" + scaledY +
                ", landsScape=" + landsScape +
                ", scaleFactor=" + scaleFactor +
                ", imagePath=" + imagePath +
                ", image=" + image +
                ", factory=" + factory +
                ", maxHight=" + maxHight +
                ", maxWidth=" + maxWidth +
                ", position=" + position +
                '}';
    }

    public ImageContainer resetStart() {
        this.startX = 0d;
        this.startY = 0d;
        return this;
    }

    public Object clone() {
        this.startX = 0d;
        this.startY = 0d;
        return this;
    }
}
