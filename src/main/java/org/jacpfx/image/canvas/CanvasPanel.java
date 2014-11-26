package org.jacpfx.image.canvas;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by amo on 11.04.14.
 */
public class CanvasPanel extends Canvas {


    private double offset = 0d;
    private double lastOffset = 0d;
    private double currentMaxHight = 0d;
    private final double clippingOffset = 0.9d;

    private final DoubleProperty zoomFactorProperty = new SimpleDoubleProperty(1d);
    private final DoubleProperty maxImageHightProperty = new SimpleDoubleProperty();
    private final DoubleProperty maxImageWidthProperty = new SimpleDoubleProperty();
    private final DoubleProperty paddingProperty = new SimpleDoubleProperty();
    private final DoubleProperty scrollProperty = new SimpleDoubleProperty();
    private final DoubleProperty lineBreakThresholdProperty = new SimpleDoubleProperty();

    private List<RowContainer> containers = Collections.emptyList();
    private final ObservableList<ImageContainer> children = FXCollections.observableList(new ArrayList<>());

    public CanvasPanel(int x, int y, double padding,double lineBreakLimit ,double maxHight, double maxWidth) {
        super(x, y);
        paddingProperty.set(padding);
        maxImageHightProperty.set(maxHight);
        maxImageWidthProperty.set(maxWidth);
        lineBreakThresholdProperty.set(lineBreakLimit);
        registerScroll();
        registerZoom();
        registerScale(this.getGraphicsContext2D());
        registerMaxHightListener(this.getGraphicsContext2D());
        registerZoomListener(this.getGraphicsContext2D());
        registerPaddingListener(this.getGraphicsContext2D());
        registerChildListener(this.getGraphicsContext2D());
        registerLineBreakThresholdProperty(this.getGraphicsContext2D());
        registerScrollProperty(this.getGraphicsContext2D());
    }

    public ObservableList<ImageContainer> getChildren() {
        return children;
    }

    private void registerMaxHightListener(final GraphicsContext gc) {
        maxImageHightProperty.addListener(change -> containers = paintImages(gc, children));
    }

    private void registerZoomListener(final GraphicsContext gc) {
        zoomFactorProperty.addListener(change ->
                        containers = paintImages(gc, children)
        );
    }

    private void registerPaddingListener(final GraphicsContext gc) {
       paddingProperty.addListener(change ->
                        containers = paintImages(gc, children)
        );
    }

    private void registerLineBreakThresholdProperty(final GraphicsContext gc) {
        lineBreakThresholdProperty.addListener(change ->
                        containers = paintImages(gc, children)
        );
    }

    private void registerScrollProperty(final GraphicsContext gc) {
        scrollProperty.addListener((observableValue, oldScrollDeltaY, newsScrollDeltaY) -> {
            lastOffset = offset;
            if (lastOffset * -1 <= currentMaxHight || (lastOffset + newsScrollDeltaY.doubleValue()) * -1 < currentMaxHight)
                offset = offset + newsScrollDeltaY.doubleValue();

            final double start = offset * -1;

            if (offset > 0d)
                offset = 0d;

            if (start <= currentMaxHight) {
                final double end = start + this.getHeight() + (this.getHeight() * clippingOffset);
                renderCanvas(this.containers, gc, start, end, offset);
            }

        });

    }

    private void registerChildListener(final GraphicsContext gc) {
        children.addListener((ListChangeListener) change -> containers = paintImages(gc, children));
    }

    private void registerScroll() {
        this.setOnScroll(handler -> {
            scrollProperty.set(handler.getDeltaY());
            handler.consume();
        });
    }

    private void registerZoom() {
            // Todo change to zoomListener
        final AtomicBoolean skip = new AtomicBoolean(true);
        final AtomicReference<Double> lastFactor = new AtomicReference<>(new Double(1d));
        this.setOnZoom(handler -> {
            handler.consume();
            double zoomFactor = zoomFactorProperty.doubleValue();
            double zoomFactorTmp = inRange(handler.getTotalZoomFactor() * zoomFactor);
            if (lastFactor.get() != zoomFactorTmp && skip.get()) {
                zoomFactorProperty.set(zoomFactorTmp);
            }
            lastFactor.set(zoomFactorProperty.doubleValue());
            skip.set(!skip.get());
        });

    }

    private void registerScale(final GraphicsContext gc) {
        this.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            if (oldSceneWidth.doubleValue() != newSceneWidth.doubleValue()) {
                containers = paintImages(gc, children);
            }


        });
        this.heightProperty().addListener((observableValue, oldSceneHight, newSceneHight) -> {
            if (oldSceneHight.doubleValue() != newSceneHight.doubleValue()) {
                containers = paintImages(gc, children);
            }

        });
    }



    private double inRange(final double val) {
        if (val > 1.5d) {
            return 1.5d;
        } else if (val < 0.2d) {
            return 0.2;
        }
        return val;
    }

    private List<RowContainer> createContainer(final List<ImageContainer> all) {
        final List<ImageContainer> collect = all.stream().map(ImageContainer::resetStart).collect(Collectors.toList());
        return getLines(paddingProperty.doubleValue(),
                maxImageHightProperty.multiply(zoomFactorProperty).doubleValue(), collect);
    }

    private double computeMaxRowHight(final List<RowContainer> containers) {
        final RowContainer lastElement = !containers.isEmpty() ? containers.get(containers.size() - 1) : null;
        return lastElement != null?lastElement.getRowEndHight():0d;
    }

    private List<RowContainer> paintImages(final GraphicsContext gc, final List<ImageContainer> all) {
        if (all == null || all.isEmpty()) return Collections.emptyList();
        final List<RowContainer> containers = createContainer(all);
        final double allRowHight = computeMaxRowHight(containers);
        currentMaxHight = (allRowHight - this.getHeight())+(paddingProperty.getValue()/2);
        final double currentZoom = zoomFactorProperty.doubleValue();
        if(currentZoom<1d) offset=offset*currentZoom;
        final double start = offset * -1;
        final double end = start + this.getHeight() + (this.getHeight() * clippingOffset);

        renderCanvas(containers, gc, start, end, offset);


        return containers;
    }

    private void renderCanvas(final List<RowContainer> containers, GraphicsContext gc, final double start, final double end, final double offset) {
        setCacheHint(CacheHint.SPEED);
        gc.clearRect(0, 0, getWidth(), getHeight());
        containers.forEach(container -> container.
                        getImages().
                        stream().
                        filter(imgElem -> {
                            final double tmp = imgElem.getStartY() + imgElem.getScaledY();
                            return tmp > start && tmp < end;
                        }).
                        forEach(c ->
                                        gc.drawImage(c.getScaledImage(), c.getStartX(), container.getRowStartHight() + offset, c.getScaledX(), c.getScaledY())
                        )
        );
        setCacheHint(CacheHint.DEFAULT);
    }


    private List<RowContainer> getLines(final double padding, final double maxHight, final List<ImageContainer> all) {
        final double maxWidth = this.widthProperty().doubleValue();
        final List<RowContainer> rows = createRows(maxWidth, maxHight, all);
        return normalizeRows(rows, padding);

    }


    /**
     * create rows with images that fit in each row
     *
     * @param maxWidth
     * @param maxHight
     * @param all
     * @return
     */
    private List<RowContainer> createRows(final double maxWidth, final double maxHight, final List<ImageContainer> all) {
        final List<RowContainer> rows = new ArrayList<>();
        int i = 0;
        double currentWidth = 0;
        RowContainer row = new RowContainer();
        row.setMaxWitdht(maxWidth);
        rows.add(row);
        for (final ImageContainer c : all) {
            c.setScaleFactor(maxHight / c.getEndY());

            final double tempWidth = c.getScaledX();
            if (i == 0) {
                currentWidth = tempWidth;
                row.add(c);
                i++;
                continue;
            }
            double currentWidthTmp = currentWidth + tempWidth;
            if (currentWidthTmp < maxWidth) {
                currentWidth = currentWidthTmp;
                row.add(c);
            } else {
                final double leftSpace = maxWidth - currentWidth;
                final double percentOfCurrentImage = leftSpace / tempWidth;
                if (percentOfCurrentImage > lineBreakThresholdProperty.get()) {
                    currentWidth += tempWidth;
                    row.add(c);
                } else {
                    row = new RowContainer();
                    currentWidth = tempWidth;
                    row.add(c);
                    row.setMaxWitdht(maxWidth);
                    rows.add(row);
                }

            }

            i++;
        }
        return rows;
    }


    private List<RowContainer> normalizeRows(final List<RowContainer> rows, final double padding) {
        if (rows.isEmpty()) return rows;
        rows.stream().findFirst().ifPresent(firstRow -> {
            // normalize width
            rows.stream().
                    peek(row -> normalizeWidth(row, padding)).
                    filter(r ->
                    {
                        if (r == firstRow) {
                            handleFirstRow(firstRow, padding);
                            return false;
                        } else {
                            return true;
                        }
                    }).
                    reduce(firstRow, (a, b) -> {
                        normalizeHight(b, padding, a.getRowEndHight());
                        return b;
                    });
        });
        return rows;
    }

    private void handleFirstRow(final RowContainer row, final double padding) {
        if (row.getImages().isEmpty()) return;
        final double v = padding / 2;
        row.getImages().forEach(img -> img.setStartY(v));
        final Optional<ImageContainer> first = row.getImages().stream().findFirst();
        // all images are normalized, take first and set row hight
        first.ifPresent(firstElement -> {
            row.setRowStartHight(v);
            row.setRowEndHight(firstElement.getScaledY() + padding * 1.5);
        });
    }


    private void normalizeHight(final RowContainer row, final double padding, final double maxHight) {
        if (row.getImages().isEmpty()) return;
        row.getImages().forEach(img -> img.setStartY(maxHight));
        final Optional<ImageContainer> first = row.getImages().stream().findFirst();

        // all images are normalized, take first and set row hight
        first.ifPresent(firstElement -> {
            row.setRowStartHight(maxHight);
            row.setRowEndHight(maxHight + firstElement.getScaledY() + padding);
        });


    }


    private RowContainer normalizeWidth(final RowContainer row, final double padding) {
        if (row.getImages().isEmpty()) return row;
        final double max = row.getMaxWitdht();
        final double length = row.getImages().stream().map(ImageContainer::getScaledX).reduce(0d, (a, b) -> a + b);
        final double amount = row.getImages().size();
        final double paddingAll = (amount + 2) * padding;
        final double scaleFactorNew = (max / (length + paddingAll)) * 1.02;
        boolean scale = row.getImages().size() > 2;
        final Optional<ImageContainer> first = row.getImages().stream().findFirst();
        first.ifPresent(fe -> {
            final ImageContainer firstElement = handleFirstImage(fe, padding, scaleFactorNew, scale);
            row.getImages().
                    stream().
                    filter(i -> i != firstElement).
                    peek(img ->
                    {
                        if (scale) img.setScaleFactor(img.getScaleFactor() * scaleFactorNew);
                    }).
                    reduce(firstElement,
                            (a, b) -> {
                                b.setStartX(a.getScaledX() + padding + a.getStartX());
                                b.setPosition(a.getPosition() + 1);
                                return b;
                            }
                    );
        });


        return row;
    }


    private ImageContainer handleFirstImage(final ImageContainer firstImage, final double padding, final double scaleFactorNew, final boolean scale) {
        if (scale) firstImage.setScaleFactor(firstImage.getScaleFactor() * scaleFactorNew);
        firstImage.setStartX(padding / 2);
        firstImage.setPosition(1);
        return firstImage;
    }
    /**
     * Set image padding (Hgap and VGap)
     *
     * @return DoubleProperty
     */
    public DoubleProperty paddingProperty() {
        return this.paddingProperty;
    }

    /**
     * set the image padding value (Hgap / Vgap)
     *
     * @param padding
     */
    public void setPadding(final double padding) {
        this.paddingProperty.set(padding);
    }

    /**
     * The zoom factor property
     *
     * @return a DoubleProperty
     */
    public DoubleProperty zoomFactorProperty() {
        return this.zoomFactorProperty;
    }


    /**
     * set the zoom factor
     *
     * @param zoom
     */
    public void setZoomFactor(final double zoom) {
        this.zoomFactorProperty.set(zoom);
    }

    /**
     * The maximum hight of images property
     *
     * @return the DoubleProperty
     */
    public DoubleProperty maxImageHightProperty() {
        return this.maxImageHightProperty;
    }

    /**
     * set the maximum hight of images
     *
     * @param maxImageHight
     */
    public void setMaxImageHight(final double maxImageHight) {
        this.maxImageHightProperty.set(maxImageHight);
    }

    /**
     * The maximum width of images property
     *
     * @return the DoubleProperty
     */
    public DoubleProperty maxImageWidthProperty() {
        return this.maxImageWidthProperty;
    }

    /**
     * set the maximum hight of images
     *
     * @param maxImageWidth
     */
    public void setMaxImageWidth(final double maxImageWidth) {
        this.maxImageWidthProperty.set(maxImageWidth);
    }


    /**
     * The line break threshold property
     * @return the Double property
     */
    public DoubleProperty lineBreakThresholdPropertyProperty() {
        return lineBreakThresholdProperty;
    }

    /**
     *  Set the line break threshold property
     * @param lineBreakThresholdProperty
     */
    public void setLineBreakThresholdProperty(double lineBreakThresholdProperty) {
        this.lineBreakThresholdProperty.set(lineBreakThresholdProperty);
    }

    /**
     * The scroll property to trigger scrolling
     * @return The scroll property
     */
    public DoubleProperty scrollPropertyProperty() {
        return scrollProperty;
    }

    /**
     * Set the scroll property value
     * @param scrollProperty
     */
    public void setScrollProperty(double scrollProperty) {
        this.scrollProperty.set(scrollProperty);
    }
}