package org.jacpfx.image.benchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Master runner for all CanvasImageGrid benchmarks
 * Runs all benchmarks and saves comprehensive results to files
 */
public class AllBenchmarksRunner {
    
    public static void main(String[] args) throws RunnerException, IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String resultsDir = "target/benchmark-results";
        Path resultsDirPath = Paths.get(resultsDir);
        
        Files.createDirectories(resultsDirPath);
        
        String resultFile = resultsDir + "/all_benchmarks_" + timestamp;
        
        System.out.println("==========================================");
        System.out.println("CanvasImageGrid Performance Benchmark Suite");
        System.out.println("==========================================");
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Results will be saved to: " + resultFile);
        System.out.println();
        
        Options opt = new OptionsBuilder()
                // Include all benchmark classes in the package
                .include("org.jacpfx.image.benchmark.*Benchmark")
                .result(resultFile + ".json")
                .resultFormat(ResultFormatType.JSON)
                .shouldDoGC(true)
                .jvmArgs("-Xms2g", "-Xmx4g", 
                        "-XX:+UseG1GC",
                        "-XX:InitiatingHeapOccupancyPercent=30",
                        "-XX:+ParallelRefProcEnabled",
                        "-Dprism.verbose=false",
                        "--add-exports", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED")
                .build();
        
        new Runner(opt).run();
        
        // Create summary report
        createSummaryReport(resultsDirPath, timestamp);
        
        System.out.println();
        System.out.println("==========================================");
        System.out.println("Benchmark Complete!");
        System.out.println("==========================================");
        System.out.println("JSON Results: " + resultFile + ".json");
        System.out.println("Summary Report: " + resultsDir + "/summary_" + timestamp + ".txt");
        System.out.println("Results Directory: " + resultsDir);
        System.out.println();
        System.out.println("To analyze bottlenecks, review the JSON file for:");
        System.out.println("  - Operations with highest average time (ms)");
        System.out.println("  - High score error (indicates inconsistent performance)");
        System.out.println("  - Memory allocation rates in detailed logs");
        System.out.println("==========================================");
    }
    
    private static void createSummaryReport(Path resultsDir, String timestamp) throws IOException {
        Path summaryFile = resultsDir.resolve("summary_" + timestamp + ".txt");
        
        StringBuilder summary = new StringBuilder();
        summary.append("CanvasImageGrid Performance Benchmark Summary\n");
        summary.append("==============================================\n\n");
        summary.append("Timestamp: ").append(timestamp).append("\n\n");
        
        summary.append("Benchmark Categories:\n");
        summary.append("---------------------\n");
        summary.append("1. ImageMetadata Parsing - Measures header parsing performance\n");
        summary.append("   - PNG, JPEG, GIF format detection and dimension extraction\n");
        summary.append("   - Lightweight parsing without full image decode\n\n");
        
        summary.append("2. ImageContainer Operations - Measures loading and drawing\n");
        summary.append("   - Image container creation overhead\n");
        summary.append("   - Image loading with DefaultFactory vs SquareFactory\n");
        summary.append("   - Repeated drawing to measure cache effectiveness\n\n");
        
        summary.append("3. CanvasPanel Layout - Measures row creation and rendering\n");
        summary.append("   - Small set (10 images), Medium set (50 images), Large set (100 images)\n");
        summary.append("   - Zoom operation performance\n");
        summary.append("   - Layout computation and normalization\n\n");
        
        summary.append("4. ImageFactory Comparison - Compares factory implementations\n");
        summary.append("   - DefaultFactory: aspect ratio preservation\n");
        summary.append("   - SquareFactory: pixel-level center crop operations\n");
        summary.append("   - Post-processing overhead measurement\n\n");
        
        summary.append("Key Performance Indicators:\n");
        summary.append("---------------------------\n");
        summary.append("- Average time per operation (milliseconds)\n");
        summary.append("- Throughput (operations per second)\n");
        summary.append("- Memory allocation rates\n");
        summary.append("- Garbage collection impact\n");
        summary.append("- Score error (lower = more consistent performance)\n\n");
        
        summary.append("Analysis Instructions:\n");
        summary.append("----------------------\n");
        summary.append("1. Open the JSON results file in a text editor or JSON viewer\n");
        summary.append("2. For each benchmark, look at the 'primaryMetric' section:\n");
        summary.append("   - 'score': Average time in milliseconds (lower is better)\n");
        summary.append("   - 'scoreError': Confidence interval (lower is better)\n");
        summary.append("   - 'scorePercentiles': Distribution of measurements\n");
        summary.append("3. Compare scores across different operations\n");
        summary.append("4. Identify bottlenecks (operations with highest avg time)\n");
        summary.append("5. Check 'scoreError' for performance consistency\n\n");
        
        summary.append("Expected Bottlenecks to Investigate:\n");
        summary.append("------------------------------------\n");
        summary.append("HIGH PRIORITY (likely slowest operations):\n");
        summary.append("- SquareImageFactory post-processing (pixel-level operations)\n");
        summary.append("- ImageContainer repeated drawing with selection effect\n");
        summary.append("- Large set layout creation (100+ images)\n\n");
        
        summary.append("MEDIUM PRIORITY:\n");
        summary.append("- Initial image loading (no cache)\n");
        summary.append("- Row normalization for justified layout\n");
        summary.append("- Zoom operations triggering full re-layout\n\n");
        
        summary.append("LOW PRIORITY (likely fast operations):\n");
        summary.append("- ImageMetadata header parsing\n");
        summary.append("- ImageContainer creation\n");
        summary.append("- Cached image drawing\n\n");
        
        summary.append("Optimization Recommendations:\n");
        summary.append("-----------------------------\n");
        summary.append("Based on benchmark results, prioritize optimizations in this order:\n\n");
        
        summary.append("IF SquareFactory post-processing is slow (>10ms):\n");
        summary.append("  → Cache processed images or use GPU acceleration\n");
        summary.append("  → Consider native image libraries for pixel operations\n\n");
        
        summary.append("IF Large set layout is slow (>50ms for 100 images):\n");
        summary.append("  → Implement binary search for visible row detection\n");
        summary.append("  → Optimize row creation algorithm\n");
        summary.append("  → Add object pooling for RowContainer instances\n\n");
        
        summary.append("IF Image loading is slow (>20ms per image):\n");
        summary.append("  → Implement two-tier loading (thumbnail → full resolution)\n");
        summary.append("  → Add stronger cache layer with LRU eviction\n");
        summary.append("  → Consider pre-loading adjacent images\n\n");
        
        summary.append("IF Zoom operation is slow (>100ms):\n");
        summary.append("  → Add debouncing to avoid recalculation during zoom\n");
        summary.append("  → Implement dirty region tracking\n");
        summary.append("  → Cache layout calculations at different zoom levels\n\n");
        
        summary.append("Next Steps:\n");
        summary.append("-----------\n");
        summary.append("1. Review JSON results to identify top 3 bottlenecks\n");
        summary.append("2. Implement targeted optimizations for slowest operations\n");
        summary.append("3. Re-run benchmarks to measure improvement\n");
        summary.append("4. Document performance gains in project documentation\n");
        summary.append("5. Set up automated benchmark runs in CI/CD pipeline\n\n");
        
        summary.append("For detailed results, see: all_benchmarks_").append(timestamp).append(".json\n");
        
        Files.writeString(summaryFile, summary.toString());
    }
}
