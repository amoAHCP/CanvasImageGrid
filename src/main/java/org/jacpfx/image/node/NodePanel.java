package org.jacpfx.image.node;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import org.jacpfx.image.canvas.ImageFactory;

import java.nio.file.Path;
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
public class NodePanel extends FlowPane {


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

    private List<RowNodeContainer> containers = Collections.emptyList();
    private final ObservableList<ImageNodeContainer> children = FXCollections.observableList(new ArrayList<>());


    private NodeSelectionListener selectionListener = (x, y, images) -> {
    };


    private NodePanel(int x, int y, double padding, double lineBreakLimit, double maxHight, double maxWidth, final List<Path> imageFolder, final ImageFactory factory, NodeSelectionListener selectionListener) {
        super(x, y);


        this.paddingProperty.set(padding);
        this.maxImageHightProperty.set(maxHight);
        this.maxImageWidthProperty.set(maxWidth);
        this.lineBreakThresholdProperty.set(lineBreakLimit);
        this.selectionListener = selectionListener;
        this.setHgap(padding);
        this.setVgap(padding);

        addImages(maxHight, maxWidth, imageFolder, factory);
       // registerScroll();
        registerZoom();
       // registerScale();
        // registerScale(this.getGraphicsContext2D());
        // registerMaxHightListener(this.getGraphicsContext2D());
        // registerZoomListener(this.getGraphicsContext2D());
        // registerPaddingListener(this.getGraphicsContext2D());
        registerChildListener(null);
        // registerLineBreakThresholdProperty(this.getGraphicsContext2D());
        // registerScrollProperty(this.getGraphicsContext2D());
        // registerMouseClickListener(selectionListener);

    }


    // Builder
    interface ImagePathBuilder {
        FactoryBuilder imagePath(final List<Path> imageFolder);
    }

    interface FactoryBuilder {
        WidthBuilder imageFactory(ImageFactory factory);
    }

    interface WidthBuilder {
        HightBuilder width(int width);
    }

    interface HightBuilder {
        PaddingBuilder hight(int hight);
    }

    interface PaddingBuilder {
        LineBreakLimitBuilder padding(double padding);
    }

    interface LineBreakLimitBuilder {
        MaxImageWidthBuilder lineBreakLimit(double lineBreakLimit);
    }

    interface MaxImageWidthBuilder {
        MaxImageHightBuilder maxImageWidth(double maxImageWidth);
    }

    interface MaxImageHightBuilder {
        SelectionListenerBuilder maxImageHight(double maxImageHight);
    }

    interface SelectionListenerBuilder {
        NodePanel selectionListener(final NodeSelectionListener listener);
    }

    public static ImagePathBuilder createCanvasPanel() {
        return imagePath -> imageFactory -> width -> hight -> padding -> lineBreakLimit -> maxImageWidth -> maxImageHight -> selectionListsner -> new NodePanel(width, hight, padding, lineBreakLimit, maxImageHight, maxImageWidth, imagePath, imageFactory, selectionListsner);
    }

    private void registerMouseClickListener(NodeSelectionListener selectionListener) {
        setOnMouseClicked(event -> children.forEach(image -> {
            final double tmpY = image.getStartY() + image.getScaledY();
            final double tmpX = image.getStartX() + image.getScaledX();
            boolean xCoord = image.getStartX() < event.getX() && tmpX > event.getX();
            boolean yCoord = image.getStartY() < event.getY() - offset && tmpY > event.getY() - offset;
            if (yCoord && xCoord) {
                //  image.drawSelectedImageOnConvas(this.getGraphicsContext2D());
                selectionListener.selected(event.getX(), event.getY(), image);
            } else if (image.isSelected()) {
                //  image.drawSelectedImageOnConvas(this.getGraphicsContext2D());
            }

        }));
    }

    private void addImages(double maxHight, double maxWidth, List<Path> imageFolder, ImageFactory factory) {
        final List<ImageNodeContainer> all = imageFolder.parallelStream().map(path -> getConatiner(path, factory, maxHight, maxWidth)).collect(Collectors.toList());
        children.addAll(all);
    }


    private ImageNodeContainer getConatiner(Path path, ImageFactory factory, double maxHight, double maxWidth) {
        return new ImageNodeContainer(path, factory, maxHight, maxWidth);
    }


    private void registerMaxHightListener(final GraphicsContext gc) {
        maxImageHightProperty.addListener(change -> containers = paintImages(children));
    }

    private void registerZoomListener() {
        zoomFactorProperty.addListener(change ->
                        containers = paintImages(children)
        );
    }

    private void registerPaddingListener(final GraphicsContext gc) {
        paddingProperty.addListener(change ->
                        containers = paintImages(children)
        );
    }

    private void registerLineBreakThresholdProperty(final GraphicsContext gc) {
        lineBreakThresholdProperty.addListener(change ->
                        containers = paintImages(children)
        );
    }

    public void paint() {
        containers = paintImages(children);
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
                renderCanvas(this.containers, start, end, offset);
            }

        });

    }

    private void registerChildListener(final GraphicsContext gc) {
        children.addListener((ListChangeListener) change -> containers = paintImages(children));
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
        final AtomicReference<Double> lastFactor = new AtomicReference<>(1d);
        this.setOnZoom(handler -> {
            double zoomFactor = zoomFactorProperty.doubleValue();
            double zoomFactorTmp = inRange(handler.getTotalZoomFactor() * zoomFactor);
            if (lastFactor.get() != zoomFactorTmp && skip.get()) {
                zoomFactorProperty.set(zoomFactorTmp);
            }
            lastFactor.set(zoomFactorProperty.doubleValue());
            skip.set(!skip.get());
            handler.consume();
        });

    }

    private void registerScale() {
        this.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            if (oldSceneWidth.doubleValue() != newSceneWidth.doubleValue()) {
                containers = paintImages(children);
            }


        });
        this.heightProperty().addListener((observableValue, oldSceneHight, newSceneHight) -> {
            if (oldSceneHight.doubleValue() != newSceneHight.doubleValue()) {
                containers = paintImages(children);
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

    private List<RowNodeContainer> createContainer(final List<ImageNodeContainer> all) {
        final List<ImageNodeContainer> collect = all.stream().map(container -> container.resetStart()).collect(Collectors.toList());
        return getLines(paddingProperty.doubleValue(),
                maxImageHightProperty.multiply(zoomFactorProperty).doubleValue(), collect);
    }

    private double computeMaxRowHight(final List<RowNodeContainer> containers) {
        final RowNodeContainer lastElement = !containers.isEmpty() ? containers.get(containers.size() - 1) : null;
        return lastElement != null ? lastElement.getRowEndHight() : 0d;
    }

    private List<RowNodeContainer> paintImages(final List<ImageNodeContainer> all) {
        if (all == null || all.isEmpty()) return Collections.emptyList();
        final List<RowNodeContainer> containers = createContainer(all);
        final double allRowHight = computeMaxRowHight(containers);
        currentMaxHight = (allRowHight - this.getHeight()) + (paddingProperty.getValue() / 2);
        final double currentZoom = zoomFactorProperty.doubleValue();
        if (currentZoom < 1d) offset = offset * currentZoom;
        final double start = offset * -1;
        final double end = start + this.getHeight() + (this.getHeight() * clippingOffset);

        renderCanvas(containers, start, end, offset);


        return containers;
    }

    private void renderCanvas(final List<RowNodeContainer> containers, final double start, final double end, final double offset) {
        getChildren().clear();

        final List<ImageView> reduce = containers
                .stream()
                .map(container -> container.     // TODO test rows with parallel stream
                        getImages())
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                })
               // .parallelStream()
               // .filter(imgElem -> filterImagesVisible(start, end, imgElem))
               // .sequential()
                .stream()
                .map(image -> image.getImageView())
                .collect(Collectors.toList());
        getChildren().addAll(reduce);

    }

    private boolean filterImagesVisible(double start, double end, ImageNodeContainer imgElem) {
        final double tmp = imgElem.getStartY() + imgElem.getScaledY();
        return tmp > start && tmp < end;
    }


    private List<RowNodeContainer> getLines(final double padding, final double maxHight, final List<ImageNodeContainer> all) {
        final List<RowNodeContainer> rows = createRows(this.widthProperty().doubleValue(), maxHight, all);
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
    private List<RowNodeContainer> createRows(final double maxWidth, final double maxHight, final List<ImageNodeContainer> all) {
        final List<RowNodeContainer> rows = new ArrayList<>();
        int i = 0;
        double currentWidth = 0;
        RowNodeContainer row = new RowNodeContainer();
        row.setMaxWitdht(maxWidth);
        rows.add(row);
        for (final ImageNodeContainer c : all) {
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
                    row = new RowNodeContainer();
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


    private List<RowNodeContainer> normalizeRows(final List<RowNodeContainer> rows, final double padding) {
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

    private void handleFirstRow(final RowNodeContainer row, final double padding) {
        if (row.getImages().isEmpty()) return;
        final double v = padding / 2;
        row.getImages().forEach(img -> img.setStartY(v));
        final Optional<ImageNodeContainer> first = row.getImages().stream().findFirst();
        // all images are normalized, take first and set row hight
        first.ifPresent(firstElement -> {
            row.setRowStartHight(v);
            row.setRowEndHight(firstElement.getScaledY() + padding * 1.5);
        });
    }


    private void normalizeHight(final RowNodeContainer row, final double padding, final double maxHight) {
        if (row.getImages().isEmpty()) return;
        row.getImages().forEach(img -> img.setStartY(maxHight));
        final Optional<ImageNodeContainer> first = row.getImages().stream().findFirst();

        // all images are normalized, take first and set row hight
        first.ifPresent(firstElement -> {
            row.setRowStartHight(maxHight);
            row.setRowEndHight(maxHight + firstElement.getScaledY() + padding);
        });


    }


    private RowNodeContainer normalizeWidth(final RowNodeContainer row, final double padding) {
        if (row.getImages().isEmpty()) return row;
        final double max = row.getMaxWitdht();
        final double length = row.getImages().stream().map(ImageNodeContainer::getScaledX).reduce(0d, (a, b) -> a + b);
        final double amount = row.getImages().size();
        final double paddingAll = (amount + 2) * padding;
        final double scaleFactorNew = (max / (length + paddingAll)) * 1.02;
        boolean scale = row.getImages().size() > 2;
        final Optional<ImageNodeContainer> first = row.getImages().stream().findFirst();
        first.ifPresent(fe -> {
            final ImageNodeContainer firstElement = handleFirstImage(fe, padding, scaleFactorNew, scale);
            row.getImages().
                    stream().
                    filter(i -> i != firstElement).
                    peek(img ->
                    {
                        if (scale) img.setScaleFactor(img.getScaleFactor() * scaleFactorNew);
                    }).
                    reduce(firstElement, (a, b) -> normalizeImageContainer(a, b, padding));
        });


        return row;
    }

    private ImageNodeContainer normalizeImageContainer(ImageNodeContainer a, ImageNodeContainer b, double padding) {
        b.setStartX(a.getScaledX() + padding + a.getStartX());
        b.setPosition(a.getPosition() + 1);
        return b;
    }


    private ImageNodeContainer handleFirstImage(final ImageNodeContainer firstImage, final double padding, final double scaleFactorNew, final boolean scale) {
        if (scale) firstImage.setScaleFactor(firstImage.getScaleFactor() * scaleFactorNew);
        firstImage.setStartX(padding / 2);
        firstImage.setPosition(1);
        return firstImage;
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
     *
     * @return the Double property
     */
    public DoubleProperty lineBreakThresholdPropertyProperty() {
        return lineBreakThresholdProperty;
    }

    /**
     * Set the line break threshold property
     *
     * @param lineBreakThresholdProperty
     */
    public void setLineBreakThresholdProperty(double lineBreakThresholdProperty) {
        this.lineBreakThresholdProperty.set(lineBreakThresholdProperty);
    }

    /**
     * The scroll property to trigger scrolling
     *
     * @return The scroll property
     */
    public DoubleProperty scrollPropertyProperty() {
        return scrollProperty;
    }

    /**
     * Set the scroll property value
     *
     * @param scrollProperty
     */
    public void setScrollProperty(double scrollProperty) {
        this.scrollProperty.set(scrollProperty);
    }
}
