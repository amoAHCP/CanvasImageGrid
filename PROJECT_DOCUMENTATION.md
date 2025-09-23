# CanvasImageGrid - JavaFX Image Grid Component

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Core Components](#core-components)
4. [Build & Dependencies](#build--dependencies)
5. [Usage Examples](#usage-examples)
6. [Performance & Optimization](#performance--optimization)
7. [Known Issues & Limitations](#known-issues--limitations)
8. [Performance Recommendations](#performance-recommendations)
9. [Contributing](#contributing)

## Project Overview

CanvasImageGrid is a high-performance JavaFX component designed to display large collections of images in a responsive, scrollable, and zoomable justified mosaic layout. The component efficiently handles thousands of images with memory-conscious loading and rendering optimizations.

### Key Features
- **Justified Layout**: Automatically arranges images in rows with optimal width utilization
- **Virtual Scrolling**: Only renders visible images for improved performance
- **Zoom Support**: Dynamic scaling with configurable zoom factors (0.2x - 1.5x)
- **Memory Management**: SoftReference-based image caching with automatic eviction
- **Lazy Loading**: Images are loaded on-demand as they become visible
- **Selection Support**: Click-to-select with visual feedback
- **Configurable Layout**: Adjustable padding, image sizes, and line break thresholds

### Project Statistics
- **Language**: Java 21
- **Framework**: JavaFX 21.0.4
- **Build Tool**: Maven
- **License**: Apache 2.0
- **Architecture**: Modular (JPMS)

## Architecture

### Module Structure
```
module canvasImageGrid {
  requires transitive javafx.base;
  requires transitive javafx.controls;
  requires javafx.graphics;
  requires javafx.media;
  exports org.jacpfx.image.canvas;
}
```

### Package Organization
```
org.jacpfx.image.canvas/    # Core canvas-based implementation
org.jacpfx.image.node/      # Alternative node-based implementation
```

### Design Patterns
- **Builder Pattern**: Fluent API for component configuration
- **Strategy Pattern**: Pluggable image factories for different loading strategies
- **Observer Pattern**: Property-based reactive updates
- **Factory Pattern**: Abstract image creation and processing

## Core Components

### 1. CanvasPanel
The main UI component extending JavaFX Canvas.

**Key Responsibilities:**
- Layout computation and row organization
- Scroll and zoom handling
- Dynamic viewport-based rendering
- Mouse interaction and selection

**Properties:**
- `maxImageHightProperty`: Maximum image height constraint
- `maxImageWidthProperty`: Maximum image width constraint
- `paddingProperty`: Spacing between images
- `zoomFactorProperty`: Current zoom level (0.2-1.5)
- `lineBreakThresholdProperty`: Row filling threshold (0.0-1.0)

### 2. ImageContainer
Represents individual images with metadata and rendering logic.

**Features:**
- Lazy image loading with SoftReference caching
- Automatic scaling and positioning
- Selection state management
- Memory-efficient placeholder handling

### 3. RowContainer
Manages groups of images organized in horizontal rows.

**Responsibilities:**
- Row height calculation
- Width normalization for justified layout
- Vertical positioning coordination

### 4. ImageFactory Interface
Strategy interface for image loading and processing.

**Implementations:**
- `DefaultImageFactory`: Standard image loading with aspect ratio preservation
- `SquareImageFactory`: Center-crop square image generation

### 5. ImageMetadata
Lightweight image header parser supporting:
- GIF, JPEG, PNG, BMP, TIFF formats
- Dimension extraction without full decode
- MIME type detection

## Build & Dependencies

### Maven Configuration
```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <javafx.version>21.0.4</javafx.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    <!-- Additional JavaFX modules -->
</dependencies>
```

### Build Commands
```bash
# Compile and package
mvn clean package

# Run with JavaFX plugin
mvn javafx:run

# Run with explicit module path (alternative)
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.graphics \
     -cp target/classes \
     org.jacpfx.image.canvas.ApplicationMainSingleWindow
```

## Usage Examples

### Basic Setup
```java
// Create image paths list
Path rootFolder = Paths.get("/path/to/images");
List<Path> imagePaths = Files.list(rootFolder)
    .filter(path -> path.toString().toLowerCase().endsWith(".jpg"))
    .collect(Collectors.toList());

// Configure the canvas panel
CanvasPanel canvas = CanvasPanel.createCanvasPanel()
    .imagePath(imagePaths)
    .imageFactory(new DefaultImageFactory())
    .width(800)
    .hight(600)  // Note: "hight" spelling maintained for compatibility
    .padding(5.0)
    .lineBreakLimit(0.1)  // 10% threshold for row breaks
    .maxImageWidth(200)
    .maxImageHight(200)
    .selectionListener((x, y, selectedImages) -> {
        if (selectedImages.length == 1) {
            System.out.println("Selected: " + selectedImages[0].getImagePath());
        }
    });

// Bind to parent container
canvas.widthProperty().bind(parentContainer.widthProperty());
canvas.heightProperty().bind(parentContainer.heightProperty());

// Add to scene
parentContainer.getChildren().add(canvas);
```

### Custom Image Factory
```java
public class CustomImageFactory implements ImageFactory {
    @Override
    public Image createImage(Path imagePath, double maxWidth, double maxHight) 
            throws Exception {
        // Custom loading logic
        return new Image(imagePath.toUri().toString(), 
                        maxWidth, maxHight, true, true, true);
    }
    
    @Override
    public Image postProcess(Image image, double maxHight, double maxWidth) {
        // Apply custom effects, filters, or transformations
        return image;
    }
}
```

### Dynamic Property Updates
```java
// Adjust zoom programmatically
canvas.setZoomFactor(1.2);

// Change padding
canvas.setPadding(10.0);

// Modify line break behavior
canvas.setLineBreakThresholdProperty(0.15);  // More aggressive row packing

// Update maximum image dimensions
canvas.setMaxImageHight(300);
canvas.setMaxImageWidth(300);
```

## Performance & Optimization

### Current Optimizations

#### 1. Memory Management
- **SoftReference Caching**: Images automatically evicted under memory pressure
- **Viewport Clipping**: Only visible images are rendered
- **Lazy Loading**: Images loaded on-demand when entering viewport
- **Parallel Processing**: Multi-threaded image metadata extraction

#### 2. Rendering Efficiency
- **Canvas-based Rendering**: Direct GPU-accelerated drawing
- **Minimal Redraws**: Property-based invalidation system
- **Efficient Layout**: O(n) row computation algorithm

#### 3. I/O Optimization
- **Header-only Parsing**: Image dimensions extracted without full decode
- **Asynchronous Loading**: Non-blocking image loading with callbacks

### Memory Usage Patterns
```
Typical Memory Footprint:
- Metadata: ~100 bytes per image
- Cached thumbnails: ~50-200KB per visible image
- Peak usage: 2-3x visible viewport content
```

## Known Issues & Limitations

### API Issues
- **Spelling**: Uses "Hight" instead of "Height" in multiple APIs
- **Compatibility**: Breaking changes would be required to fix spelling

### Performance Limitations
- **No Cancellation**: Long-running I/O operations cannot be cancelled
- **Cache Thrashing**: SoftReferences may cause frequent reloads under memory pressure
- **Thread Management**: No throttling for rapid scroll/zoom combinations

### Platform Dependencies
- **Hardcoded Paths**: Demo applications contain macOS-specific paths
- **File System**: Assumes local file system access

### Testing & Quality
- **Legacy Tests**: Uses outdated JUnit 3.8.1
- **Test Coverage**: Limited automated test coverage
- **Path Validation**: Missing validation for invalid/unreadable image paths

## Performance Recommendations

### Overall System Performance

#### 1. JVM Tuning
```bash
# Recommended JVM flags for large image collections
-XX:+UseG1GC
-XX:InitiatingHeapOccupancyPercent=30
-XX:+ParallelRefProcEnabled
-Xmx4g  # Adjust based on image collection size
```

#### 2. Memory Optimization Strategies

**High Impact, Low Risk:**
- Implement shared placeholder images instead of unique rectangles
- Add explicit LRU cache to complement SoftReference strategy
- Pool and reuse RowContainer objects during layout
- Defer allocation of optional fields until needed

**Medium Impact, Medium Risk:**
- Implement two-tier loading (thumbnail → full resolution)
- Add viewport-based eviction for off-screen images
- Batch property invalidations to reduce layout thrashing
- Use progressive image decoding for large files

**High Impact, High Risk:**
- Implement true virtualization with metadata-only distant images
- Add disk-based cache for pre-scaled thumbnails
- Use off-heap buffers for image processing operations

#### 3. Rendering Performance

**Immediate Improvements:**
```java
// Add epsilon filtering for micro-scrolls
private static final double SCROLL_EPSILON = 0.3;

private void canvasScroll(GraphicsContext gc, ScrollEvent handler) {
    double delta = handler.getDeltaY();
    if (Math.abs(delta) < SCROLL_EPSILON) return;  // Skip tiny movements
    // ... existing scroll logic
}
```

**Layout Optimizations:**
- Binary search for visible row ranges instead of linear iteration
- Replace stream operations with indexed loops in hot paths
- Debounce rapid property changes (zoom, scroll, resize)

**Advanced Rendering:**
- Implement dirty region tracking for partial redraws
- Add frame rate limiting to prevent excessive updates
- Consider canvas double-buffering for complex scenes

#### 4. Overscrolling Behavior

**Current Issues:**
- No overscroll visual feedback
- Abrupt stopping at content boundaries
- No momentum-based scrolling

**Recommended Improvements:**
```java
// Implement elastic overscroll
private void handleOverscroll(double delta, double contentHeight) {
    double maxOverscroll = 50.0;  // pixels
    double resistance = 0.3;      // resistance factor
    
    if (offset < 0) {
        // Top overscroll with diminishing effect
        offset = Math.max(-maxOverscroll, offset + delta * resistance);
        scheduleSnapBack();
    } else if (offset > contentHeight - getHeight()) {
        // Bottom overscroll with diminishing effect
        offset = Math.min(contentHeight - getHeight() + maxOverscroll, 
                         offset + delta * resistance);
        scheduleSnapBack();
    }
}

private void scheduleSnapBack() {
    // Animate back to valid scroll position
    Timeline snapBack = new Timeline(
        new KeyFrame(Duration.millis(300),
            new KeyValue(scrollProperty, clampToValidRange(offset)))
    );
    snapBack.play();
}
```

**Momentum Scrolling:**
```java
// Track scroll velocity for momentum
private double scrollVelocity = 0.0;
private long lastScrollTime = 0;

private void updateScrollMomentum(double delta) {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastScrollTime < 50) {
        scrollVelocity = (scrollVelocity * 0.8) + (delta * 0.2);
    } else {
        scrollVelocity = delta;
    }
    lastScrollTime = currentTime;
}
```

#### 5. Monitoring & Metrics

**Implement Performance Tracking:**
```java
public class PerformanceMetrics {
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
    
    public double getCacheHitRatio() {
        long loads = imageLoads.get();
        return loads > 0 ? (double) cacheHits.get() / loads : 0.0;
    }
    
    public double getAverageRenderTime() {
        long calls = renderCalls.get();
        return calls > 0 ? (double) totalRenderTime.sum() / calls : 0.0;
    }
}
```

**Key Performance Indicators:**
- Cache hit ratio (target: >80%)
- Average render time (target: <16ms for 60fps)
- Memory usage trend (should be stable)
- Image load frequency (detect thrashing)

### Implementation Priority

1. **Immediate (1-2 days):**
   - Add scroll epsilon filtering
   - Implement shared placeholder images
   - Add basic performance metrics

2. **Short-term (1-2 weeks):**
   - LRU cache implementation
   - Overscroll behavior improvements
   - Property change debouncing

3. **Medium-term (1-2 months):**
   - Two-tier image loading
   - Viewport-based memory management
   - Advanced scroll momentum

4. **Long-term (3+ months):**
   - Full virtualization
   - Disk-based caching
   - Advanced rendering optimizations

## Contributing

### Development Setup
1. **Prerequisites**: Java 21+, Maven 3.8+
2. **Clone**: `git clone https://github.com/amoAHCP/CanvasImageGrid.git`
3. **Build**: `mvn clean package`
4. **IDE**: Import as Maven project with JavaFX support

### Code Style
- Follow existing naming conventions (including "Hight" spelling for compatibility)
- Use builder patterns for complex configurations
- Prefer composition over inheritance
- Document performance implications for public APIs

### Testing Guidelines
- Add unit tests for new functionality
- Performance test with large image collections (1000+ images)
- Memory leak testing with JFR/VisualVM
- Cross-platform testing (Windows, macOS, Linux)

### Pull Request Process
1. Create feature branch from `master`
2. Implement changes with tests
3. Update documentation
4. Verify performance impact
5. Submit PR with detailed description

---

**Last Updated**: September 5, 2025  
**Version**: 1.0-SNAPSHOT  
**Maintainer**: Andy Moncsek (amo.ahcp@gmail.com)  
**License**: Apache 2.0
