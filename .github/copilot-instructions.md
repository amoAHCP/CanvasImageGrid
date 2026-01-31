# CanvasImageGrid - Copilot Agent Instructions

## Project Overview

**CanvasImageGrid** is a high-performance JavaFX component that displays large collections of images in a responsive, scrollable, and zoomable justified mosaic layout. It efficiently handles thousands of images using canvas-based rendering with memory-conscious loading optimizations.

**Key Technologies:**
- **Language:** Java 24 (configured in pom.xml, actual runtime may vary)
- **Framework:** JavaFX 25
- **Module System:** JPMS (Java Platform Module System)
- **Build Tool:** Maven 3.9+
- **Testing:** JUnit 5 (Jupiter 5.11.3)
- **Benchmarking:** JMH 1.37
- **Main Class:** `org.jacpfx.image.canvas.ApplicationMainSingleWindow`

**Repository Size:** ~18 Java source files in main, ~12 test files, ~5 shell scripts

## Build Commands - CRITICAL INFORMATION

**ALWAYS use these exact command sequences. Trust these instructions - only search if they fail.**

### Clean Build from Scratch
```bash
mvn clean compile
```
- Takes ~1-2 seconds
- **Always run this after making code changes** to ensure proper recompilation
- Warnings about "Systemmodulpfad" and unchecked operations are normal and safe to ignore

### Run Tests
```bash
mvn test
```
- Takes ~10 seconds (includes JavaFX initialization)
- All 12 tests should pass (6 test classes)
- Tests require JavaFX runtime - warnings about restricted methods are normal
- Test configuration includes: `--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED`

### Package Application
```bash
mvn package -DskipTests
```
- Creates shaded JAR at `target/CanvasImageGrid-1.0-SNAPSHOT.jar`
- Takes ~1-2 seconds (without tests)
- Warnings about module-info.class and overlapping resources are expected and safe

### Full Build with Tests
```bash
mvn clean compile test
```
- Takes ~11 seconds total
- This is the recommended validation command after making changes

### Run Application (Three Methods)

**Method 1 - Maven JavaFX Plugin (Recommended):**
```bash
mvn javafx:run
```

**Method 2 - Shell Script (macOS/Linux):**
```bash
./run.sh
```
- Automatically detects JavaFX location
- Requires Java 17+ (checks version)
- Works with platform-specific native libraries

**Method 3 - Direct JAR Execution:**
```bash
# First package
mvn package -DskipTests
# Then run (platform-specific module-path required)
java --module-path <javafx-lib-path> --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.media -jar target/CanvasImageGrid-1.0-SNAPSHOT.jar
```

### Run Benchmarks
```bash
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.AllBenchmarksRunner"
```
- Results saved to `target/benchmark-results/`
- Takes several minutes (JMH warmup + iterations)
- **Note:** The `run-benchmarks.sh` script currently does NOT work due to annotation processor requirements

### Build Native Executable (GraalVM)
```bash
./build-native-improved.sh
```
- Requires GraalVM 21+ with native-image installed
- Takes 5-10 minutes to build
- Creates native executable at `target/CanvasImageGrid`
- Run with: `./target/CanvasImageGrid <image-directory>`
- See NATIVE_BUILD.md for detailed instructions
- **Note**: Hits JavaFX native library limitations, see NATIVE_IMAGE_STATUS.md

### Build Native Executable with Gluon (Recommended for JavaFX)
```bash
./build-gluon.sh
```
- Requires GraalVM 21+ with native-image installed
- Takes 10-20 minutes on first build (downloads JavaFX native libraries)
- Creates platform-specific executable in `target/gluonfx/<platform>/`
- Properly handles all JavaFX native dependencies
- See GLUON_BUILD_GUIDE.md for detailed instructions

## Project Structure

### Module Configuration
**File:** `src/main/java/module-info.java`
```java
module canvasImageGrid {
  requires transitive javafx.base;
  requires transitive javafx.controls;
  requires javafx.graphics;
  requires javafx.media;
  requires javafx.swing;
  exports org.jacpfx.image.canvas;
}
```
**Critical:** When adding dependencies, update this file to maintain JPMS compatibility.

### Source Organization
```
src/main/java/org/jacpfx/image/
├── canvas/                          # Main implementation (exported)
│   ├── CanvasPanel.java            # Core UI component (extends Canvas)
│   ├── ImageContainer.java         # Individual image management
│   ├── RowContainer.java           # Row layout container
│   ├── ImageFactory.java           # Strategy interface
│   ├── DefaultImageFactory.java    # Standard image loader
│   ├── SquareImageFactory.java     # Square crop factory
│   ├── ImageMetadata.java          # Lightweight header parser
│   ├── SelectionListener.java      # Selection callback interface
│   ├── ApplicationMainSingleWindow.java        # Main entry point
│   ├── ApplicationMainNative.java              # Native-image compatible entry point
│   ├── ApplicationMainSingleWindowFPS.java     # FPS demo variant
│   ├── ApplicationMainWithSelectionWindow.java # Selection demo
│   └── WritableImageDemo.java      # Specialized demo
└── node/                           # Alternative FlowPane implementation (partial)
    ├── NodePanel.java
    └── ImageNodeContainer.java

src/test/java/org/jacpfx/image/
├── canvas/                         # Unit tests (6 test classes)
│   ├── CanvasPanelTest.java
│   ├── ImageContainerTest.java
│   ├── RowContainerTest.java
│   ├── DefaultImageFactoryTest.java
│   ├── SquareImageFactoryTest.java
│   └── ImageMetadataTest.java
└── benchmark/                      # JMH benchmarks (5 benchmark classes)
    ├── AllBenchmarksRunner.java
    ├── BenchmarkBase.java
    ├── CanvasPanelLayoutBenchmark.java
    ├── ImageContainerBenchmark.java
    ├── ImageFactoryBenchmark.java
    └── ImageMetadataBenchmark.java

src/main/resources/META-INF/native-image/
    # GraalVM native-image configuration (jni, proxy, reflect, resource, serialization configs)

src/test/resources/images/
    # Test fixtures: blue.png, green.png, red.png, dummy.jpg, dummy.txt
```

### Configuration Files
- **pom.xml** - Maven build configuration (primary build file)
- **module-info.java** - JPMS module descriptor
- **dependency-reduced-pom.xml** - Auto-generated by maven-shade-plugin (do not edit)

### Build Scripts
- **run.sh** - Universal launcher (detects JavaFX, checks Java version)
- **run-jar.sh** - Direct JAR launcher with hardcoded paths (may need adjustment)
- **run-benchmarks.sh** - Benchmark runner (currently non-functional, use Maven instead)
- **build-native.sh** - Original GraalVM native-image build script
- **build-native-improved.sh** - Enhanced native-image build script (has limitations)
- **build-gluon.sh** - Gluon Substrate build script (recommended for native builds)

### Documentation Files
- **PROJECT_DOCUMENTATION.md** - Architecture overview and usage examples
- **COMPREHENSIVE_DOCUMENTATION.md** - Detailed agent architecture explanation
- **AGENTS.md** - Multi-agent design pattern documentation
- **BENCHMARKS.md** - Performance benchmarking guide
- **NATIVE_BUILD.md** - GraalVM native-image build instructions (vanilla GraalVM)
- **NATIVE_IMAGE_STATUS.md** - Analysis of vanilla GraalVM limitations with JavaFX
- **GLUON_BUILD_GUIDE.md** - Gluon Substrate build guide (recommended approach)
- **JAVAFX_NATIVE_CONFIG_GUIDE.md** - JavaFX native configuration troubleshooting
- **docs/ARCHITEKTUR.md** - German architecture summary
- **docs/MEMORY_OPTIMIZATION.md** - Memory management strategies
- **docs/CANVAS_CLEAR_ANALYSE.md** - Canvas clearing analysis

## Architecture Key Points

### Design Patterns
1. **Builder Pattern** - CanvasPanel construction via fluent API
2. **Strategy Pattern** - ImageFactory interface for pluggable loading strategies
3. **Observer Pattern** - JavaFX properties for reactive updates
4. **Soft Reference Pattern** - Memory-aware image caching

### Core Components
- **CanvasPanel** - Main component extending javafx.scene.canvas.Canvas
  - Manages layout computation, viewport clipping, scroll/zoom
  - Uses justified layout algorithm to fill rows optimally
  - Only renders visible images (viewport-based filtering)
  
- **ImageContainer** - Represents individual images
  - Lazy loading with SoftReference<Image> for memory management
  - Placeholder rendering until image loads asynchronously
  - Position/scaling calculations and selection state
  
- **RowContainer** - Groups images into horizontal rows
  - Width normalization for justified alignment
  - Height coordination across row

### Critical Implementation Details
1. **Viewport Clipping:** Only images intersecting visible area are rendered (`filterImagesVisible()`)
2. **Scroll Optimization:** Uses SCROLL_EPSILON (0.9) to reduce micro-scroll redraws
3. **Memory Strategy:** SoftReference allows GC to reclaim images under memory pressure
4. **Parallel Processing:** Image metadata extraction uses parallel streams
5. **TODOs in Code:** 5 TODO comments exist (see ImageContainer.java, NodePanel.java) - these are future optimizations, not blockers

## Common Development Tasks

### Adding a New Test
1. Create test class in `src/test/java/org/jacpfx/image/canvas/`
2. Extend test with `@Test` annotations (JUnit 5)
3. Use TestFX for JavaFX component testing if needed
4. Run with `mvn test`

### Modifying Core Components
1. Main code in `src/main/java/org/jacpfx/image/canvas/`
2. **Always run `mvn clean compile test` after changes**
3. Check compilation warnings (unchecked operations are known)
4. Test fixtures available in `src/test/resources/images/`

### Adding Dependencies
1. Add to `<dependencies>` in pom.xml
2. Update `module-info.java` with `requires` statement
3. For test-only deps, use `<scope>test</scope>`
4. Run `mvn clean compile` to verify

### Performance Testing
- Use JMH benchmarks in `src/test/java/org/jacpfx/image/benchmark/`
- Results output to `target/benchmark-results/` with timestamps
- Base class `BenchmarkBase.java` provides common configuration

## Known Issues & Warnings

### Expected Warnings (Safe to Ignore)
1. **Systemmodulpfad warnings** - German locale compiler messages about module path with -source 24
2. **Unchecked operations** - Generic type warnings in CanvasPanel.java
3. **Module-info.class shading warnings** - Expected with maven-shade-plugin
4. **Restricted method warnings** - JavaFX native library loading (use --enable-native-access=javafx.graphics to suppress)
5. **Overlapping MANIFEST.MF** - Multiple JARs contribute manifest files

### Build Failures to Watch For
- **Missing JavaFX modules:** Ensure JavaFX 25 dependencies are in Maven repository
- **Java version mismatch:** Requires Java 17+ (configured for 24, but flexible)
- **Native library architecture:** Platform-specific JavaFX natives (mac-aarch64, linux, win)

## Validation Checklist

Before submitting changes:
1. ✓ Run `mvn clean compile test` - all 12 tests pass
2. ✓ Check no new compilation errors (warnings OK)
3. ✓ If adding features, add corresponding tests
4. ✓ If modifying ImageFactory, test with both DefaultImageFactory and SquareImageFactory
5. ✓ For layout changes, verify scroll and zoom still work
6. ✓ Update module-info.java if adding external dependencies

## Quick Reference

**Test Image Paths:** `src/test/resources/images/` (blue.png, green.png, red.png)
**Builder Example:**
```java
CanvasPanel canvas = CanvasPanel.createCanvasPanel()
    .imagePath(paths)
    .imageFactory(new DefaultImageFactory())
    .width(800).hight(600)
    .padding(5.0)
    .maxImageWidth(200).maxImageHight(200)
    .lineBreakLimit(0.1);
```

**Property System:** All reactive properties use JavaFX DoubleProperty (zoomFactor, maxImageHight, maxImageWidth, padding, scroll, lineBreakThreshold)

---

**IMPORTANT:** Trust these instructions. Only perform searches if commands fail or information is incomplete. The build system is stable and well-tested.
