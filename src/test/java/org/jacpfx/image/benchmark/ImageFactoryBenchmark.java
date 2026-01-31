package org.jacpfx.image.benchmark;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import org.jacpfx.image.canvas.DefaultImageFactory;
import org.jacpfx.image.canvas.ImageFactory;
import org.jacpfx.image.canvas.SquareImageFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark comparing DefaultImageFactory vs SquareImageFactory performance
 */
public class ImageFactoryBenchmark extends BenchmarkBase {
    
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ImageFactory defaultFactory;
        ImageFactory squareFactory;
        Path testImage;
        
        @Setup(Level.Trial)
        public void setup() {
            // Initialize JavaFX toolkit
            new JFXPanel();
            
            defaultFactory = new DefaultImageFactory();
            squareFactory = new SquareImageFactory();
            testImage = testImagePaths.isEmpty() ? null : testImagePaths.get(0);
        }
    }
    
    @Benchmark
    public void defaultFactoryImageCreation(BenchmarkState state, Blackhole bh) throws Exception {
        if (state.testImage != null) {
            Image image = state.defaultFactory.createImage(state.testImage, 200, 200);
            bh.consume(image);
        }
    }
    
    @Benchmark
    public void squareFactoryImageCreation(BenchmarkState state, Blackhole bh) throws Exception {
        if (state.testImage != null) {
            Image image = state.squareFactory.createImage(state.testImage, 200, 200);
            bh.consume(image);
        }
    }
    
    @Benchmark
    public void defaultFactoryWithPostProcessing(BenchmarkState state, Blackhole bh) throws Exception {
        if (state.testImage != null) {
            Image image = state.defaultFactory.createImage(state.testImage, 200, 200);
            
            // Wait for image to load
            waitForImageLoad(image);
            
            Image processed = state.defaultFactory.postProcess(image, 200, 200);
            bh.consume(processed);
        }
    }
    
    @Benchmark
    public void squareFactoryWithPostProcessing(BenchmarkState state, Blackhole bh) throws Exception {
        if (state.testImage != null) {
            Image image = state.squareFactory.createImage(state.testImage, 200, 200);
            
            // Wait for image to load before post-processing
            waitForImageLoad(image);
            
            Image processed = state.squareFactory.postProcess(image, 200, 200);
            bh.consume(processed);
        }
    }
    
    /**
     * Helper method to wait for image loading completion
     */
    private void waitForImageLoad(Image image) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            long startTime = System.currentTimeMillis();
            while (image.getProgress() < 1.0) {
                try {
                    Thread.sleep(10);
                    // Timeout after 5 seconds
                    if (System.currentTimeMillis() - startTime > 5000) {
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            latch.countDown();
        });
        latch.await(6, TimeUnit.SECONDS);
    }
    
    public static void main(String[] args) throws RunnerException {
        runBenchmark(ImageFactoryBenchmark.class);
    }
}
