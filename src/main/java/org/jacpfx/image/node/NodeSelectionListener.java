package org.jacpfx.image.node;

/**
 * Created by Andy Moncsek on 31.07.15.
 */
public interface NodeSelectionListener {

    void selected(double startX, double endY, ImageNodeContainer... image);
}
