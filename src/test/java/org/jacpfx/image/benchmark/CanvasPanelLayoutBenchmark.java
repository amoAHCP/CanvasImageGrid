package org.jacpfx.image.benchmark;

import javafx.embed.swing.JFXPanel;
import org.jacpfx.image.canvas.ImageContainer;
import org.jacpfx.image.canvas.DefaultImageFactory;
import org.jacpfx.image.canvas.ImageFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark for layout operations
 * Measures batch image container creation and metadata extraction
 * Note: CanvasPanel builder classes are package-private, so we benchmark
 * the underlying components (ImageContainer creation) directly
 */
public class CanvasPanelLayoutBenchmark extends BenchmarkBase {
    
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        List<java.nio.file.Path> smallImageSet;
        List<java.nio.file.Path> mediumImageSet;
        List<java.nio.file.Path> largeImageSet;
        
        @Setup(Level.Trial)
        public void setup() {
            // Initialize JavaFX toolkit
            new JFXPanel();
            
            // Create different sized image sets
            smallImageSet = new ArrayList<>();
            mediumImageSet = new ArrayList<>();
            largeImageSet = new ArrayList<>();
            
            // Small set: 10 images
            for (int i = 0; i < Math.min(10, testImagePaths.size() * 3); i++) {
                smallImageSet.add(testImagePaths.get(i % testImagePaths.size()));
            }
            
            // Medium set: 50 images
            for (int i = 0; i < Math.min(50, testImagePaths.size() * 17); i++) {
                mediumImageSet.add(testImagePaths.get(i % testImagePaths.size()));
            }
            
            // Large set: 100 images
            for (int i = 0; i < Math.min(100, testImagePaths.size() * 34); i++) {
                largeImageSet.add(testImagePaths.get(i % testImagePaths.size()));
            }
        }
    }
    
    @Benchmark
    public void batchImageContainerCreationSmallSet(BenchmarkState state, Blackhole bh) {
        ImageFactory factory = new DefaultImageFactory();
        List<ImageContainer> containers = new ArrayList<>();
        
        for (java.nio.file.Path path : state.smallImageSet) {
            ImageContainer container = new ImageContainer(path, factory, 200, 200);
            containers.add(container);
        }
        
        bh.consume(containers);
    }
    
    @Benchmark
    public void batchImageContainerCreationMediumSet(BenchmarkState state, Blackhole bh) {
        ImageFactory factory = new DefaultImageFactory();
        List<ImageContainer> containers = new ArrayList<>();
        
        for (java.nio.file.Path path : state.mediumImageSet) {
            ImageContainer container = new ImageContainer(path, factory, 200, 200);
            containers.add(container);
        }
        
        bh.consume(containers);
    }
    
    @Benchmark
    public void batchImageContainerCreationLargeSet(BenchmarkState state, Blackhole bh) {
        ImageFactory factory = new DefaultImageFactory();
        List<ImageContainer> containers = new ArrayList<>();
        
        for (java.nio.file.Path path : state.largeImageSet) {
            ImageContainer container = new ImageContainer(path, factory, 200, 200);
            containers.add(container);
        }
        
        bh.consume(containers);
    }
    
    @Benchmark
    public void parallelImageContainerCreation(BenchmarkState state, Blackhole bh) {
        ImageFactory factory = new DefaultImageFactory();
        
        List<ImageContainer> containers = state.mediumImageSet.parallelStream()
                .map(path -> new ImageContainer(path, factory, 200, 200))
                .toList();
        
        bh.consume(containers);
    }
    
    public static void main(String[] args) throws RunnerException {
        runBenchmark(CanvasPanelLayoutBenchmark.class);
    }
}
