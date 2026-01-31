#
CanvasImageGrid - Detailed Agent Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Core Agent Architecture](#core-agent-architecture)
3. [Canvas-Based Agents](#canvas-based-agents)
4. [Node-Based Agents](#node-based-agents)
5. [Image Processing Agents](#image-processing-agents)
6. [Factory Pattern Agents](#factory-pattern-agents)
7. [Container Management Agents](#container-management-agents)
8. [Selection and Interaction Agents](#selection-and-interaction-agents)
9. [Memory Management Agents](#memory-management-agents)
10. [Performance Optimization Strategies](#performance-optimization-strategies)
11. [Agent Communication Patterns](#agent-communication-patterns)
12. [Build and Deployment Agents](#build-and-deployment-agents)

## Project Overview

**CanvasImageGrid** is a high-performance JavaFX component that implements a sophisticated agent-based architecture for displaying large collections of images in a responsive, scrollable, and zoomable justified mosaic layout. The project uses a combination of canvas-based and node-based approaches to efficiently handle thousands of images with memory-conscious loading and rendering optimizations.

### Key Statistics
- **Language**: Java 21 (JPMS Module System)
- **Framework**: JavaFX 21.0.4  
- **Build Tool**: Maven 3.x
- **Architecture**: Multi-agent modular design
- **Performance Target**: Handle 10,000+ images efficiently
- **Memory Strategy**: SoftReference-based caching with viewport clipping

## Core Agent Architecture

The system follows a **multi-agent architectural pattern** where each component acts as an autonomous agent with specific responsibilities:

### Agent Classification
```
Primary Agents (Core System)
├── CanvasPanel Agent (Main UI Controller)
├── ImageContainer Agent (Individual Image Management)
├── RowContainer Agent (Row Layout Management)
└── NodePanel Agent (Alternative UI Implementation)

Secondary Agents (Supporting Services)
├── ImageFactory Agent (Image Creation Strategy)
├── SelectionListener Agent (User Interaction)
├── ImageMetadata Agent (Lightweight Parsing)
└── MemoryManager Agent (Cache & Performance)
```

### Agent Communication Protocol
- **Property-Based Messaging**: JavaFX properties for reactive updates
- **Event-Driven Architecture**: Scroll, zoom, and selection events
- **Builder Pattern Commands**: Fluent API for configuration
- **Observable Collections**: Dynamic child management

## Canvas-Based Agents

### 1. CanvasPanel Agent
**File**: `src/main/java/org/jacpfx/image/canvas/CanvasPanel.java`  
**Type**: Primary UI Controller Agent  
**Extends**: `javafx.scene.canvas.Canvas`

#### Agent Responsibilities
- **Layout Computation**: Dynamic row organization and justified alignment
- **Viewport Management**: Efficient clipping and visible range calculation  
- **User Interaction**: Mouse clicks, scroll events, zoom handling
- **Property Coordination**: Manages all reactive properties
- **Rendering Pipeline**: Orchestrates drawing operations

#### Key Properties (Agent State)
```java
// Core State Properties
private final DoubleProperty zoomFactorProperty = new SimpleDoubleProperty(1.0);
private final DoubleProperty maxImageHightProperty = new SimpleDoubleProperty();
private final DoubleProperty maxImageWidthProperty = new SimpleDoubleProperty();
private final DoubleProperty paddingProperty = new SimpleDoubleProperty();
private final DoubleProperty scrollProperty = new SimpleDoubleProperty();
private final DoubleProperty lineBreakThresholdProperty = new SimpleDoubleProperty();

// Performance State
private double offset = 0.0;
private double currentMaxHight = 0.0;
private static final double SCROLL_EPSILON = 0.9;
private final double clippingOffset = 0.9;
```

#### Agent Builder Pattern
```java
// Fluent API for Agent Configuration
CanvasPanel canvas = CanvasPanel.createCanvasPanel()
    .imagePath(imagePaths)                    // Data source
    .imageFactory(new DefaultImageFactory())  // Processing strategy
    .width(800).hight(600)                   // Viewport dimensions
    .padding(5.0)                            // Layout spacing
    .lineBreakLimit(0.1)                     // Row break threshold
    .maxImageWidth(200).maxImageHight(200)   // Size constraints
    .selectionListener((x, y, images) -> {   // Interaction callback
        // Handle selection events
    });
```

#### Performance Characteristics
- **Viewport Clipping**: Only renders visible images (`filterImagesVisible()`)
- **Scroll Optimization**: Epsilon-based filtering to reduce micro-scroll redraws
- **Memory Efficiency**: SoftReference caching with automatic eviction
- **Parallel Processing**: Multi-threaded image metadata extraction

### 2. ImageContainer Agent
**File**: `src/main/java/org/jacpfx/image/canvas/ImageContainer.java`  
**Type**: Individual Image Management Agent  
**Implements**: `Cloneable`

#### Agent Responsibilities
- **Lazy Loading**: On-demand image loading with placeholder fallback
- **Soft Caching**: Memory-pressure-aware image retention
- **Coordinate Management**: Position, scaling, and viewport calculations
- **Selection State**: Visual feedback and state management
- **Draw Operations**: Canvas rendering with optimization

#### Agent State Variables
```java
// Position and Scaling
private double startX, startY, endX, endY;
private double scaledX, scaledY;
private double scaleFactor = 1.0;
private boolean landscape;

// Image Management
private Path imagePath;
private ImageFactory factory;
private double maxHight, maxWidth;
private int position = 0;

// Performance State
private double lastDrawingStartPosition;
private SoftReference<Image> imageReference;
private Image placeholderImage;
```

#### Memory Management Strategy
```java
// Soft Reference Pattern for Memory-Aware Caching
private SoftReference<Image> imageReference;

public Image getImage() {
    if (imageReference != null) {
        Image cached = imageReference.get();
        if (cached != null) return cached;
    }
    
    // Load image and create soft reference
    Image newImage = loadImage();
    imageReference = new SoftReference<>(newImage);
    return newImage;
}
```

### 3. RowContainer Agent
**File**: `src/main/java/org/jacpfx/image/canvas/RowContainer.java`  
**Type**: Row Layout Management Agent

#### Agent Responsibilities
- **Width Normalization**: Proportional scaling for justified layout
- **Height Coordination**: Consistent row heights and vertical positioning
- **Image Grouping**: Logical organization of images into rows
- **Layout Algorithms**: Optimal width utilization calculations

#### Key Operations
```java
// Justified Layout Algorithm
public void normalizeWidth(double targetWidth, double padding) {
    double currentWidth = calculateCurrentWidth();
    double scaleFactor = targetWidth / currentWidth;
    
    for (ImageContainer image : images) {
        image.applyWidthScale(scaleFactor);
    }
}

// Vertical Position Management
public void normalizeHeight(double startY) {
    for (ImageContainer image : images) {
        image.setStartY(startY);
        image.setEndY(startY + getRowHeight());
    }
}
```

## Node-Based Agents

### 4. NodePanel Agent
**File**: `src/main/java/org/jacpfx/image/node/NodePanel.java`  
**Type**: Alternative UI Implementation Agent  
**Extends**: `javafx.scene.layout.FlowPane`

#### Agent Characteristics
- **FlowPane-Based**: Uses JavaFX node graph instead of canvas
- **Automatic Layout**: Leverages JavaFX layout system
- **Development Stage**: Partially implemented alternative approach
- **Memory Pattern**: Different from canvas-based approach

#### Comparison: Canvas vs Node Agents
| Aspect | Canvas Agent | Node Agent |
|--------|-------------|------------|
| **Rendering** | Direct GPU drawing | Scene graph nodes |
| **Memory** | Manual management | JavaFX automatic |
| **Performance** | Higher for large datasets | Better for small datasets |
| **Customization** | Full control | Layout system constraints |
| **Complexity** | Higher implementation | Simpler implementation |

## Image Processing Agents

### 5. ImageFactory Agent Interface
**File**: `src/main/java/org/jacpfx/image/canvas/ImageFactory.java`  
**Type**: Strategy Pattern Agent Interface

#### Agent Contract
```java
public interface ImageFactory {
    // Primary image creation
    Image createImage(Path imagePath, double maxWidth, double maxHight) throws Exception;
    
    // Optional post-processing
    default Image postProcess(Image image, double maxHight, double maxWidth) {
        return image;
    }
    
    // Lightweight dimension extraction
    default Map.Entry<Double,Double> getImageSize(Path imagePath, double maxHight) 
            throws IOException {
        ImageMetadata metadata = new ImageMetadata(imagePath.toFile());
        return new AbstractMap.SimpleEntry<>(
            getTargetWidth(metadata, maxHight),
            getTargetHight(maxHight)
        );
    }
}
```

### 6. DefaultImageFactory Agent
**File**: `src/main/java/org/jacpfx/image/canvas/DefaultImageFactory.java`  
**Type**: Standard Image Processing Agent

#### Agent Capabilities
- **Aspect Ratio Preservation**: Maintains original image proportions
- **Performance Loading**: 2x maxHeight for quality/performance balance
- **Background Loading**: Asynchronous image creation
- **Error Handling**: Graceful fallback for corrupted images

### 7. SquareImageFactory Agent
**File**: `src/main/java/org/jacpfx/image/canvas/SquareImageFactory.java`  
**Type**: Square Crop Processing Agent

#### Agent Capabilities
- **Center Crop Algorithm**: Smart cropping to square format
- **Pixel-Level Operations**: Direct image manipulation
- **Uniform Grid**: Consistent square dimensions
- **Quality Preservation**: Maintains image clarity during crop

### 8. ImageMetadata Agent
**File**: `src/main/java/org/jacpfx/image/canvas/ImageMetadata.java`  
**Type**: Lightweight Header Parsing Agent

#### Supported Formats
- **GIF**: Graphics Interchange Format
- **JPEG**: Joint Photographic Experts Group
- **PNG**: Portable Network Graphics
- **BMP**: Bitmap Image File
- **TIFF**: Tagged Image File Format

#### Agent Benefits
- **No Full Decode**: Extracts dimensions without loading pixel data
- **Fast Startup**: Rapid layout computation for large collections
- **Memory Efficient**: Minimal memory footprint during analysis
- **MIME Detection**: Format identification for processing decisions

## Factory Pattern Agents

### Factory Strategy Comparison

| Factory Agent | Use Case | Memory Impact | Performance | Quality |
|---------------|----------|---------------|-------------|---------|
| **DefaultImageFactory** | General purpose | Medium | High | Original aspect |
| **SquareImageFactory** | Uniform grid | Higher | Medium | Cropped square |
| **Custom Factory** | Specialized needs | Variable | Variable | Custom |

### Custom Factory Implementation Example
```java
public class ThumbnailCacheFactory implements ImageFactory {
    private final Map<Path, SoftReference<Image>> cache = new ConcurrentHashMap<>();
    
    @Override
    public Image createImage(Path imagePath, double maxWidth, double maxHight) 
            throws Exception {
        // Check disk cache first
        Image cached = loadFromDiskCache(imagePath);
        if (cached != null) return cached;
        
        // Load and cache
        Image image = new Image(imagePath.toUri().toString(), 
                               maxWidth, maxHight, true, true, true);
        saveToDiskCache(imagePath, image);
        return image;
    }
}
```

## Container Management Agents

### Row Organization Algorithm
```java
// Intelligent Row Breaking Algorithm
private void createRows() {
    List<RowContainer> rows = new ArrayList<>();
    RowContainer currentRow = new RowContainer();
    double currentWidth = 0.0;
    double availableWidth = getWidth() - (2 * padding);
    
    for (ImageContainer image : images) {
        double imageWidth = image.getScaledWidth() + padding;
        double remainingSpace = availableWidth - currentWidth;
        double thresholdWidth = imageWidth * lineBreakThreshold;
        
        if (currentWidth + imageWidth <= availableWidth || 
            remainingSpace < thresholdWidth) {
            // Add to current row
            currentRow.addImage(image);
            currentWidth += imageWidth;
        } else {
            // Start new row
            rows.add(currentRow);
            currentRow = new RowContainer();
            currentRow.addImage(image);
            currentWidth = imageWidth;
        }
    }
    
    if (!currentRow.isEmpty()) {
        rows.add(currentRow);
    }
    
    // Normalize row widths for justified layout
    for (RowContainer row : rows) {
        row.normalizeWidth(availableWidth, padding);
    }
}
```

## Selection and Interaction Agents

### 9. SelectionListener Agent Interface
**File**: `src/main/java/org/jacpfx/image/canvas/SelectionListener.java`  
**Type**: User Interaction Agent Interface

#### Agent Contract
```java
@FunctionalInterface
public interface SelectionListener {
    void onSelection(double x, double y, ImageContainer[] selectedImages);
}
```

### Selection State Management
```java
// Multi-Selection Support
private final Set<ImageContainer> selectedImages = new HashSet<>();

public void toggleSelection(ImageContainer image) {
    if (selectedImages.contains(image)) {
        selectedImages.remove(image);
        image.clearSelectionEffect();
    } else {
        selectedImages.add(image);
        image.applySelectionEffect();
    }
    
    // Notify listeners
    selectionListener.onSelection(
        lastClickX, lastClickY, 
        selectedImages.toArray(new ImageContainer[0])
    );
}
```

### Visual Selection Effects
```java
// Drop Shadow Selection Effect
public void applySelectionEffect() {
    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLUE);
    dropShadow.setRadius(5.0);
    dropShadow.setOffsetX(2.0);
    dropShadow.setOffsetY(2.0);
    
    // Apply to image view or snapshot
    // Implementation varies by rendering approach
}
```

## Memory Management Agents

### SoftReference Strategy
```java
// Memory-Aware Image Management
public class MemoryManagedImageContainer {
    private SoftReference<Image> primaryImage;
    private SoftReference<Image> thumbnailImage;
    private Image placeholderImage; // Strong reference for immediate display
    
    public Image getDisplayImage() {
        // Try primary image first
        if (primaryImage != null) {
            Image image = primaryImage.get();
            if (image != null) return image;
        }
        
        // Fall back to thumbnail
        if (thumbnailImage != null) {
            Image thumb = thumbnailImage.get();
            if (thumb != null) return thumb;
        }
        
        // Return placeholder and trigger background load
        scheduleImageLoad();
        return placeholderImage;
    }
}
```

### Performance Monitoring Agent
```java
public class PerformanceMonitor {
    private final AtomicLong imageLoads = new AtomicLong();
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong renderCalls = new AtomicLong();
    private final LongAdder totalRenderTime = new LongAdder();
    
    public void recordImageLoad() { imageLoads.incrementAndGet(); }
    public void recordCacheHit() { cacheHits.incrementAndGet(); }
    
    public void recordRender(long duration) {
        renderCalls.incrementAndGet();
        totalRenderTime.add(duration);
    }
    
    public PerformanceMetrics getMetrics() {
        return new PerformanceMetrics(
            getCacheHitRatio(),
            getAverageRenderTime(),
            getImagesPerSecond()
        );
    }
}
```

## Performance Optimization Strategies

### Viewport Clipping Agent
```java
// Efficient Visible Range Calculation
private List<ImageContainer> filterImagesVisible() {
    double viewportTop = offset;
    double viewportBottom = offset + getHeight();
    
    return containers.stream()
        .flatMap(row -> row.getImages().stream())
        .filter(image -> {
            double imageTop = image.getStartY();
            double imageBottom = image.getEndY();
            
            // Check if image intersects viewport
            return !(imageBottom < viewportTop - clippingOffset || 
                    imageTop > viewportBottom + clippingOffset);
        })
        .collect(Collectors.toList());
}
```

### Scroll Optimization Agent
```java
// Epsilon-Based Scroll Filtering
private static final double SCROLL_EPSILON = 0.9;

private void handleScroll(ScrollEvent event) {
    double delta = event.getDeltaY();
    
    // Filter micro-scrolls to reduce redraws
    if (Math.abs(delta) < SCROLL_EPSILON) {
        return;
    }
    
    // Apply scroll with bounds checking
    double newOffset = Math.max(0, 
        Math.min(offset - delta, getMaxScrollOffset()));
    
    if (Math.abs(newOffset - offset) > SCROLL_EPSILON) {
        offset = newOffset;
        renderCanvas();
    }
}
```

### Zoom Management Agent
```java
// Bounded Zoom with Smooth Transitions
private static final double MIN_ZOOM = 0.2;
private static final double MAX_ZOOM = 1.5;

public void setZoomFactor(double zoom) {
    double clampedZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
    
    if (Math.abs(clampedZoom - zoomFactorProperty.get()) > 0.01) {
        zoomFactorProperty.set(clampedZoom);
        
        // Trigger layout recalculation
        invalidateLayout();
        renderCanvas();
    }
}
```

## Agent Communication Patterns

### Property-Based Messaging
```java
// Reactive Property Updates
private void registerPropertyListeners() {
    // Zoom changes trigger layout updates
    zoomFactorProperty.addListener((obs, oldVal, newVal) -> {
        if (Math.abs(oldVal.doubleValue() - newVal.doubleValue()) > 0.01) {
            updateImageSizes();
            createRows();
            renderCanvas();
        }
    });
    
    // Padding changes affect layout
    paddingProperty.addListener((obs, oldVal, newVal) -> {
        createRows();
        renderCanvas();
    });
    
    // Size changes require full recalculation
    widthProperty().addListener((obs, oldVal, newVal) -> {
        if (Math.abs(oldVal.doubleValue() - newVal.doubleValue()) > 1.0) {
            createRows();
            renderCanvas();
        }
    });
}
```

### Event-Driven Architecture
```java
// Mouse Event Processing Chain
private void registerMouseClickListener(SelectionListener listener) {
    setOnMouseClicked(event -> {
        double x = event.getX();
        double y = event.getY() + offset; // Adjust for scroll
        
        // Find clicked image
        Optional<ImageContainer> clicked = findImageAt(x, y);
        
        if (clicked.isPresent()) {
            ImageContainer image = clicked.get();
            
            // Handle selection state
            if (event.isControlDown()) {
                toggleSelection(image);
            } else {
                clearAllSelections();
                selectImage(image);
            }
            
            // Notify listeners
            listener.onSelection(x, y, getSelectedImages());
            
            // Trigger visual update
            renderCanvas();
        }
    });
}
```

## Build and Deployment Agents

### Maven Configuration Agent
**File**: `pom.xml`

#### Key Dependencies
```xml
<dependencies>
    <!-- JavaFX Core -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.4</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- UI Styling -->
    <dependency>
        <groupId>com.guigarage</groupId>
        <artifactId>flatter</artifactId>
        <version>0.7</version>
    </dependency>
</dependencies>
```

#### Build Plugins
```xml
<plugins>
    <!-- Java 21 Compilation -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
            <release>21</release>
        </configuration>
    </plugin>
    
    <!-- JavaFX Runtime -->
    <plugin>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-maven-plugin</artifactId>
        <version>0.0.8</version>
    </plugin>
    
    <!-- Native Image Support -->
    <plugin>
        <groupId>org.graalvm.buildtools</groupId>
        <artifactId>native-maven-plugin</artifactId>
        <version>0.9.28</version>
    </plugin>
</plugins>
```

### Module System Agent
**File**: `src/main/java/module-info.java`

```java
module canvasImageGrid {
    requires transitive javafx.base;
    requires transitive javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    
    exports org.jacpfx.image.canvas;
    exports org.jacpfx.image.node;
}
```

### Runtime Execution Agents

#### Standard JAR Execution
```bash
# Maven-based execution
mvn clean package
mvn javafx:run

# Manual execution with module path
java --module-path /path/to/javafx/lib \
     --add-modules javafx.controls,javafx.graphics \
     -cp target/classes \
     org.jacpfx.image.canvas.ApplicationMainSingleWindow
```

#### Native Image Compilation
```bash
# GraalVM Native Image
mvn clean package -Pnative

# Direct native-image execution
./target/CanvasImageGrid
```

### Performance Tuning Agents
```bash
# JVM Optimization Flags
java -XX:+UseG1GC \
     -XX:InitiatingHeapOccupancyPercent=30 \
     -XX:+ParallelRefProcEnabled \
     -Xmx4g \
     -Dprism.verbose=true \
     --module-path /path/to/javafx/lib \
     --add-modules javafx.controls,javafx.graphics \
     -cp target/classes \
     org.jacpfx.image.canvas.ApplicationMainSingleWindow
```

## Agent Interaction Diagram

```
User Events
    ↓
CanvasPanel Agent (Main Controller)
    ↓
┌─────────────────────────────────────────────────────┐
│  Property Change Propagation                        │
├─────────────────────────────────────────────────────┤
│ ZoomFactor → ImageContainer Agents (Scale Update)   │
│ Padding → RowContainer Agents (Layout Update)       │
│ MaxHeight → ImageFactory Agents (Size Recalc)       │
│ Scroll → Viewport Filter Agent (Visibility)         │
└─────────────────────────────────────────────────────┘
    ↓
RowContainer Agents (Layout Management)
    ↓
ImageContainer Agents (Individual Management)
    ↓
ImageFactory Agents (Content Loading)
    ↓
Rendering Pipeline
    ↓
Display Update
```

## Future Agent Enhancements

### 1. Advanced Caching Agent
```java
public class TieredCacheAgent {
    private final Map<Path, Image> l1Cache = new ConcurrentHashMap<>();
    private final Map<Path, SoftReference<Image>> l2Cache = new ConcurrentHashMap<>();
    private final Path diskCacheDir;
    
    public Image getImage(Path imagePath, double maxWidth, double maxHeight) {
        // L1 Cache (Strong References)
        Image l1Image = l1Cache.get(imagePath);
        if (l1Image != null) return l1Image;
        
        // L2 Cache (Soft References)
        SoftReference<Image> l2Ref = l2Cache.get(imagePath);
        if (l2Ref != null) {
            Image l2Image = l2Ref.get();
            if (l2Image != null) {
                promoteToL1(imagePath, l2Image);
                return l2Image;
            }
        }
        
        // Disk Cache
        Image diskImage = loadFromDisk(imagePath);
        if (diskImage != null) {
            cacheImage(imagePath, diskImage);
            return diskImage;
        }
        
        // Load new image
        return loadAndCache(imagePath, maxWidth, maxHeight);
    }
}
```

### 2. Predictive Loading Agent
```java
public class PredictiveLoadAgent {
    private final ScheduledExecutorService executor = 
        Executors.newScheduledThreadPool(2);
    
    public void predictiveLoad(double scrollVelocity, double currentOffset) {
        if (Math.abs(scrollVelocity) > PREDICTION_THRESHOLD) {
            double futureOffset = currentOffset + (scrollVelocity * PREDICTION_TIME);
            List<ImageContainer> futureVisible = calculateFutureVisible(futureOffset);
            
            executor.schedule(() -> {
                futureVisible.forEach(ImageContainer::preloadImage);
            }, PREDICTION_DELAY, TimeUnit.MILLISECONDS);
        }
    }
}
```

### 3. Memory Pressure Agent
```java
public class MemoryPressureAgent {
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final double PRESSURE_THRESHOLD = 0.8;
    
    public void monitorMemoryPressure() {
        double usageRatio = getCurrentMemoryUsage();
        
        if (usageRatio > PRESSURE_THRESHOLD) {
            triggerAggressiveCleanup();
        } else if (usageRatio > PRESSURE_THRESHOLD * 0.7) {
            triggerModerateCleanup();
        }
    }
    
    private void triggerAggressiveCleanup() {
        // Clear all soft references
        // Reduce cache sizes
        // Force garbage collection
        System.gc();
    }
}
```

## Conclusion

The CanvasImageGrid project demonstrates a sophisticated multi-agent architecture that efficiently handles large-scale image display with JavaFX. Each agent has clearly defined responsibilities, communication patterns, and performance characteristics. The system's modular design allows for easy extension and customization while maintaining high performance and memory efficiency.

Key architectural strengths:
- **Agent Autonomy**: Each component manages its own state and responsibilities
- **Reactive Communication**: Property-based messaging for loose coupling
- **Performance Focus**: Memory-aware design with viewport optimization
- **Extensibility**: Factory patterns and strategy interfaces for customization
- **Scalability**: Handles thousands of images with efficient resource management

This agent-based approach provides a solid foundation for building high-performance image grid components while maintaining code clarity and maintainability.

---

**Document Version**: 1.0  
**Last Updated**: September 6, 2025  
**Maintainer**: Andy Moncsek (amo.ahcp@gmail.com)  
**License**: Apache 2.0