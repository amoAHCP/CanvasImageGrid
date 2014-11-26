package org.jacpfx.image.canvas;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an image row, containig the ImageContainer and the specific rowEndHight
 * Created by Andy Moncsek on 11.04.14.
 */
public class RowContainer {
    private double rowEndHight;
    private double rowStartHight;
    private double maxWitdht;
    private WritableImage row;


    private final List<ImageContainer> images = new ArrayList<>();

    public void setRowEndHight(double rowEndHight) {
        this.rowEndHight = rowEndHight;
        this.row =null;
    }

    public double getRowEndHight() {
        return this.rowEndHight;
    }

    public void add(ImageContainer image) {
        this.images.add(image);
    }

    public List<ImageContainer> getImages() {
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

    @Deprecated
    //WritableImage img = container.getRow();
    //gc.drawImage(img,0d,container.getRowStartHight()+ offset,container.getMaxWitdht(),container.getRowEndHight()-container.getRowStartHight());
    public WritableImage getRow() {
        if(row==null) {
            double hight = rowEndHight - rowStartHight;
            row = new WritableImage(
                    Double.valueOf(maxWitdht).intValue(),
                    Double.valueOf(hight).intValue());
            final PixelWriter pixelWriter = row.getPixelWriter();
            this.images.parallelStream().forEach(img -> pixelWriter.setPixels(Double.valueOf(img.getStartX()).intValue()
                    , 0
                    , Double.valueOf(img.getScaledX()).intValue()
                    , Double.valueOf(img.getScaledY()).intValue()
                    , img.getScaledImage().getPixelReader(), 0, 0));
        }


         return row;
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
