package org.jacpfx.image.benchmark;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.jacpfx.image.canvas.DefaultImageFactory;
import org.jacpfx.image.canvas.ImageContainer;
import org.jacpfx.image.canvas.ImageFactory;
import org.jacpfx.image.canvas.SquareImageFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

/**
 * Benchmark for ImageContainer operations
 * Measures image loading, caching, and drawing performance
 */
public class ImageContainerBenchmark extends BenchmarkBase {
    
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ImageFactory defaultFactory;
        ImageFactory squareFactory;
        Path testImage;
        GraphicsContext gc;
        Canvas canvas;
        
        @Setup(Level.Trial)
        public void setup() throws InterruptedException {
            // Initialize JavaFX toolkit
            new JFXPanel();
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                canvas = new Canvas(800, 600);
                gc = canvas.getGraphicsContext2D();
                latch.countDown();
            });
            latch.await();
            
            defaultFactory = new DefaultImageFactory();
            squareFactory = new SquareImageFactory();
            testImage = testImagePaths.isEmpty() ? null : testImagePaths.get(0);
        }
    }
    
    @Benchmark
    public void imageContainerCreation(BenchmarkState state, Blackhole bh) {
        ImageContainer container = new ImageContainer(
            state.testImage, 
            state.defaultFactory, 
            200, 
            200
        );
        bh.consume(container);
    }
    
    @Benchmark
    public void imageLoadingWithDefaultFactory(BenchmarkState state, Blackhole bh) throws InterruptedException {
        ImageContainer container = new ImageContainer(
            state.testImage, 
            state.defaultFactory, 
            200, 
            200
        );
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            container.drawImageToCanvas(state.gc, 0);
            latch.countDown();
        });
        latch.await();
        bh.consume(container);
    }
    
    @Benchmark
    public void imageLoadingWithSquareFactory(BenchmarkState state, Blackhole bh) throws InterruptedException {
        ImageContainer container = new ImageContainer(
            state.testImage, 
            state.squareFactory, 
            200, 
            200
        );
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            container.drawImageToCanvas(state.gc, 0);
            latch.countDown();
        });
        latch.await();
        bh.consume(container);
    }
    
    @Benchmark
    public void repeatedDrawingSameImage(BenchmarkState state, Blackhole bh) throws InterruptedException {
        ImageContainer container = new ImageContainer(
            state.testImage, 
            state.defaultFactory, 
            200, 
            200
        );
        
        // First draw to load image
        CountDownLatch firstDraw = new CountDownLatch(1);
        Platform.runLater(() -> {
            container.drawImageToCanvas(state.gc, 0);
            firstDraw.countDown();
        });
        firstDraw.await();
        
        // Measure repeated draws (should use cache)
        CountDownLatch repeatedDraw = new CountDownLatch(1);
        Platform.runLater(() -> {
            for (int i = 0; i < 10; i++) {
                container.drawImageToCanvas(state.gc, i * 10);
            }
            repeatedDraw.countDown();
        });
        repeatedDraw.await();
        bh.consume(container);
    }
    
    public static void main(String[] args) throws RunnerException {
        runBenchmark(ImageContainerBenchmark.class);
    }
}
