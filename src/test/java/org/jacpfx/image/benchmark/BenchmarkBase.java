package org.jacpfx.image.benchmark;

import org.openjdk.jmh.annotations.*;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Base class for JMH benchmarks with common configuration and utilities
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(value = 0) // No forking - run in same JVM to avoid classpath issues with Maven
public abstract class BenchmarkBase {
    
    protected static final String BENCHMARK_RESULTS_DIR = "target/benchmark-results";
    protected static List<Path> testImagePaths;
    
    static {
        try {
            // Initialize test images
            Path testImagesDir = Paths.get("src/test/resources/images");
            if (Files.exists(testImagesDir)) {
                testImagePaths = Files.list(testImagesDir)
                    .filter(p -> p.toString().toLowerCase().matches(".*\\.(png|jpg|jpeg|gif|bmp)$"))
                    .collect(Collectors.toList());
            }
            
            // Ensure results directory exists
            Files.createDirectories(Paths.get(BENCHMARK_RESULTS_DIR));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize benchmark base", e);
        }
    }
    
    /**
     * Helper method to create benchmark runner with file output
     */
    protected static void runBenchmark(Class<?> benchmarkClass) throws RunnerException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String resultFile = BENCHMARK_RESULTS_DIR + "/" + 
                           benchmarkClass.getSimpleName() + "_" + timestamp;
        
        Options opt = new OptionsBuilder()
                .include(benchmarkClass.getSimpleName())
                .result(resultFile + ".json")
                .resultFormat(ResultFormatType.JSON)
                .shouldDoGC(true)
                .jvmArgs("-Xms2g", "-Xmx4g", 
                        "-XX:+UseG1GC",
                        "-XX:+ParallelRefProcEnabled",
                        "--add-exports", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED")
                .build();
        
        new Runner(opt).run();
        
        // Also create a human-readable summary
        System.out.println("\n==========================================");
        System.out.println("Benchmark results saved to: " + resultFile + ".json");
        System.out.println("Results directory: " + BENCHMARK_RESULTS_DIR);
        System.out.println("==========================================\n");
    }
}
