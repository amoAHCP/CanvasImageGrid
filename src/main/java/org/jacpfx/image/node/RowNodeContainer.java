package org.jacpfx.image.node;

import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an image row, containig the ImageContainer and the specific rowEndHight
 * Created by Andy Moncsek on 11.04.14.
 */
public class RowNodeContainer {
    private double rowEndHight;
    private double rowStartHight;
    private double maxWitdht;
    private WritableImage row;


    private final List<ImageNodeContainer> images = new ArrayList<>();

    public void setRowEndHight(double rowEndHight) {
        this.rowEndHight = rowEndHight;
        this.row =null;
    }

    public double getRowEndHight() {
        return this.rowEndHight;
    }

    public void add(ImageNodeContainer image) {
        this.images.add(image);
    }

    public List<ImageNodeContainer> getImages() {
        return this.images;
    }

    public double getMaxWitdht() {
        return maxWitdht;
    }

    public void setMaxWitdht(double maxWitdht) {
        this.maxWitdht = maxWitdht;
        this.row =null;
    }

    public double getRowStartHight() {
        return rowStartHight;
    }

    public void setRowStartHight(double rowStartHight) {
        this.rowStartHight = rowStartHight;
        this.row =null;
    }


    @Override
    public String toString() {
        return "RowContainer{" +
                "rowEndHight=" + rowEndHight +
                ", rowStartHight=" + rowStartHight +
                ", maxWitdht=" + maxWitdht +
                ", images=" + images +
                '}';
    }
}
