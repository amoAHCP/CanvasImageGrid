package org.jacpfx.image.canvas;

/**
 * Created by Andy Moncsek on 31.07.15.
 */
public interface SelectionListener {

    void selected(double startX, double endY, ImageContainer ...image);
}
