package org.jacpfx.image.benchmark;

import org.jacpfx.image.canvas.ImageMetadata;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Benchmark for ImageMetadata parsing performance
 * Measures the lightweight header parsing for different image formats
 */
public class ImageMetadataBenchmark extends BenchmarkBase {
    
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        Path pngImage;
        Path jpgImage;
        Path gifImage;
        
        @Setup(Level.Trial)
        public void setup() {
            // Find test images of different formats
            for (Path path : testImagePaths) {
                String name = path.toString().toLowerCase();
                if (name.endsWith(".png") && pngImage == null) {
                    pngImage = path;
                } else if ((name.endsWith(".jpg") || name.endsWith(".jpeg")) && jpgImage == null) {
                    jpgImage = path;
                } else if (name.endsWith(".gif") && gifImage == null) {
                    gifImage = path;
                }
            }
        }
    }
    
    @Benchmark
    public void parsePngMetadata(BenchmarkState state, Blackhole bh) throws IOException {
        if (state.pngImage != null) {
            ImageMetadata metadata = new ImageMetadata(state.pngImage.toFile());
            bh.consume(metadata.getWidth());
            bh.consume(metadata.getHeight());
            bh.consume(metadata.getMimeType());
        }
    }
    
    @Benchmark
    public void parseJpgMetadata(BenchmarkState state, Blackhole bh) throws IOException {
        if (state.jpgImage != null) {
            ImageMetadata metadata = new ImageMetadata(state.jpgImage.toFile());
            bh.consume(metadata.getWidth());
            bh.consume(metadata.getHeight());
            bh.consume(metadata.getMimeType());
        }
    }
    
    @Benchmark
    public void parseMultipleFormats(BenchmarkState state, Blackhole bh) throws IOException {
        for (Path path : testImagePaths) {
            ImageMetadata metadata = new ImageMetadata(path.toFile());
            bh.consume(metadata);
        }
    }
    
    public static void main(String[] args) throws RunnerException {
        runBenchmark(ImageMetadataBenchmark.class);
    }
}
