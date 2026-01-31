# CanvasImageGrid Performance Benchmarks

## Overview
This directory contains JMH (Java Microbenchmark Harness) benchmarks for measuring and analyzing performance bottlenecks in the CanvasImageGrid project.

## Quick Start

### Run All Benchmarks
```bash
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.AllBenchmarksRunner"
```

### Run Individual Benchmarks
```bash
# ImageMetadata parsing
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.ImageMetadataBenchmark"

# ImageContainer operations
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.ImageContainerBenchmark"

# CanvasPanel layout
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.CanvasPanelLayoutBenchmark"

# ImageFactory comparison
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.ImageFactoryBenchmark"
```

## Results Location
All benchmark results are saved to: `target/benchmark-results/`

Files include:
- `all_benchmarks_YYYYMMDD_HHMMSS.json` - Detailed JSON results
- `summary_YYYYMMDD_HHMMSS.txt` - Human-readable summary with analysis guidance
- Individual benchmark result files (when run separately)

## Analyzing Results

### JSON Results Structure
The JSON files contain detailed metrics for each benchmark:
```json
{
  "benchmark": "org.jacpfx.image.benchmark.ImageMetadataBenchmark.parsePngMetadata",
  "mode": "avgt",
  "threads": 1,
  "forks": 1,
  "warmupIterations": 3,
  "measurementIterations": 5,
  "primaryMetric": {
    "score": 1.234,              // Average time in milliseconds
    "scoreError": 0.056,         // Confidence interval (95%)
    "scoreUnit": "ms/op",
    "scorePercentiles": {
      "0.0": 1.180,
      "50.0": 1.230,
      "90.0": 1.290,
      "100.0": 1.310
    }
  }
}
```

### Key Metrics to Analyze
1. **Score (Average Time)**: Lower is better - identifies slow operations
2. **Score Error**: Lower indicates more consistent performance
3. **Percentiles**: Shows distribution - high p90/p100 indicates occasional slowdowns
4. **Throughput**: Operations per second (higher is better)

### Identifying Bottlenecks
Look for:
- **Highest average times** → Primary bottlenecks requiring optimization
- **High variability (score error)** → Inconsistent performance, possible GC issues
- **Large p90-p50 gaps** → Occasional performance spikes
- **Comparison between methods** → Relative efficiency of different approaches

## Benchmark Categories

### 1. ImageMetadata Benchmarks
**File**: `ImageMetadataBenchmark.java`

**Tests**:
- `parsePngMetadata` - PNG header parsing
- `parseJpgMetadata` - JPEG header parsing
- `parseMultipleFormats` - Batch processing of mixed formats

**Purpose**: Measure metadata extraction efficiency without full image decode

**Expected Performance**:
- Fast operations: < 1ms per image
- If slower: Optimize header parsing logic or add format-specific fast paths

### 2. ImageContainer Benchmarks
**File**: `ImageContainerBenchmark.java`

**Tests**:
- `imageContainerCreation` - Container instantiation overhead
- `imageLoadingWithDefaultFactory` - Standard image loading
- `imageLoadingWithSquareFactory` - Square crop image loading
- `repeatedDrawingSameImage` - Cache effectiveness measurement

**Purpose**: Measure image loading, caching, and drawing performance

**Expected Performance**:
- Container creation: < 1ms
- First load: 5-20ms (depending on image size)
- Cached drawing: < 2ms
- If slower: Improve caching strategy or optimize pixel operations

### 3. CanvasPanel Benchmarks
**File**: `CanvasPanelLayoutBenchmark.java`

**Tests**:
- `createCanvasPanelSmallSet` - 10 images layout
- `createCanvasPanelMediumSet` - 50 images layout
- `createCanvasPanelLargeSet` - 100 images layout
- `zoomOperation` - Zoom with re-layout

**Purpose**: Measure layout computation and rendering pipeline performance

**Expected Performance**:
- Small set (10 images): < 50ms
- Medium set (50 images): < 150ms
- Large set (100 images): < 300ms
- Zoom operation: < 100ms
- If slower: Optimize row creation algorithm or add caching

### 4. ImageFactory Benchmarks
**File**: `ImageFactoryBenchmark.java`

**Tests**:
- `defaultFactoryImageCreation` - Standard factory loading
- `squareFactoryImageCreation` - Square factory loading
- `defaultFactoryWithPostProcessing` - With post-processing step
- `squareFactoryWithPostProcessing` - Pixel-level crop operations

**Purpose**: Compare factory implementation efficiency

**Expected Performance**:
- DefaultFactory: 5-15ms per image
- SquareFactory (no post-process): Similar to DefaultFactory
- SquareFactory (with post-process): 10-30ms (pixel operations overhead)
- If SquareFactory is much slower: Optimize pixel manipulation or add caching

## Performance Targets

### Fast Operations (< 1ms)
- ImageMetadata parsing
- ImageContainer creation
- Cached image retrieval

### Medium Operations (1-10ms)
- Initial image loading
- Default factory image creation
- Small set layout (10 images)

### Acceptable Operations (10-50ms)
- Square factory with post-processing
- Medium set layout (50 images)
- Zoom operations

### Slow Operations (> 50ms)
- Large set layout (100+ images)
- Batch image loading without cache
- Selection effect with snapshot

## Optimization Recommendations

### High Priority Optimizations

**If SquareImageFactory post-processing is slow (>20ms)**:
```java
// Current: Pixel-by-pixel operations
// Optimize: Use WritablePixelFormat and bulk operations
// Consider: Native libraries or GPU acceleration
```

**If Large set layout is slow (>300ms for 100 images)**:
```java
// Current: Full row recalculation on every change
// Optimize: Binary search for visible rows
// Optimize: Object pooling for RowContainer
// Optimize: Dirty region tracking
```

### Medium Priority Optimizations

**If Image loading is slow (>20ms per image)**:
```java
// Current: SoftReference-only caching
// Add: LRU cache with strong references for visible images
// Add: Two-tier loading (thumbnail → full resolution)
// Add: Predictive pre-loading for adjacent images
```

**If Zoom operation is slow (>100ms)**:
```java
// Current: Immediate full re-layout
// Add: Debouncing for continuous zoom gestures
// Add: Layout caching at common zoom levels
// Add: Progressive rendering during zoom
```

### Low Priority Optimizations

**If Metadata parsing is slow (>1ms per image)**:
```java
// Current: Stream-based parsing
// Optimize: Memory-mapped file access for large batches
// Optimize: Parallel parsing with work-stealing
```

## Continuous Monitoring

### Recommended Schedule
- **Before/after major optimizations**: Measure impact
- **Weekly during active development**: Track performance trends
- **Before releases**: Ensure no performance regressions
- **When investigating issues**: Identify root causes

### Regression Detection
Compare benchmark results over time:
```bash
# Save baseline
cp target/benchmark-results/all_benchmarks_*.json baseline.json

# After changes, compare
diff baseline.json target/benchmark-results/all_benchmarks_*.json
```

### CI/CD Integration
Add to your pipeline:
```yaml
# Example GitHub Actions step
- name: Run Performance Benchmarks
  run: |
    mvn clean test-compile
    mvn exec:java -Dexec.classpathScope=test \
      -Dexec.mainClass="org.jacpfx.image.benchmark.AllBenchmarksRunner"
    
- name: Upload Results
  uses: actions/upload-artifact@v3
  with:
    name: benchmark-results
    path: target/benchmark-results/
```

## Troubleshooting

### JavaFX Initialization Errors
If benchmarks fail with JavaFX toolkit errors:
```bash
# Ensure proper JavaFX exports
export JAVA_OPTS="--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
```

### Memory Issues
If benchmarks run out of memory:
```bash
# Increase heap size in pom.xml or command line
-Xms4g -Xmx8g
```

### Inconsistent Results
If results vary significantly between runs:
- Ensure no other applications are running
- Increase warmup iterations
- Check for background GC activity
- Run on dedicated hardware if possible

## Advanced Usage

### Custom Benchmark Parameters
Modify `BenchmarkBase.java` to adjust:
- `@Warmup(iterations = 3)` - Warmup iterations
- `@Measurement(iterations = 5)` - Measurement iterations
- `@Fork(value = 1)` - Number of JVM forks
- `jvmArgs` - JVM options

### Profiling Integration
Combine JMH with profilers:
```bash
# With Java Flight Recorder
-XX:+UnlockCommercialFeatures -XX:+FlightRecorder

# With Async Profiler
-agentpath:/path/to/libasyncProfiler.so=start,event=cpu,file=profile.html
```

## Notes

- **Test Images**: Currently uses 3 PNG images from `src/test/resources/images/`
- **Realistic Workloads**: Benchmarks duplicate images to simulate larger datasets
- **JavaFX Threading**: All UI operations properly executed on JavaFX Application Thread
- **Results Stability**: First run may be slower due to JIT compilation and class loading

## References

- JMH Documentation: https://github.com/openjdk/jmh
- JavaFX Performance Guide: https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/perfGuide.html
- Canvas Optimization: https://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm
