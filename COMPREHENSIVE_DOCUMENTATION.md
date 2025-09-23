# CanvasImageGrid - Comprehensive Documentation

## Table of Contents
1.  [Project Overview](#project-overview)
2.  [Core Agent Architecture](#core-agent-architecture)
3.  [Canvas-Based Agents](#canvas-based-agents)
4.  [Image Processing Agents](#image-processing-agents)
5.  [Layout and Rendering](#layout-and-rendering)
6.  [Selection and Interaction](#selection-and-interaction)
7.  [Memory Management](#memory-management)
8.  [Performance](#performance)
9.  [Build and Deployment](#build-and-deployment)
10. [Suggested Optimizations](#suggested-optimizations)
11. [Future Enhancements](#future-enhancements)

## Project Overview

**CanvasImageGrid** is a high-performance JavaFX component that implements a sophisticated agent-based architecture for displaying large collections of images in a responsive, scrollable, and zoomable justified mosaic layout. The project uses a combination of canvas-based and node-based approaches to efficiently handle thousands of images with memory-conscious loading and rendering optimizations.

### Key Statistics
-   **Language**: Java 21 (JPMS Module System)
-   **Framework**: JavaFX 21.0.4
-   **Build Tool**: Maven 3.x
-   **Architecture**: Multi-agent modular design
-   **Performance Target**: Handle 10,000+ images efficiently
-   **Memory Strategy**: SoftReference-based caching with viewport clipping

## Core Agent Architecture

The system follows a **multi-agent architectural pattern** where each component acts as an autonomous agent with specific responsibilities.

### Agent Classification
```
Primary Agents (Core System)
├── CanvasPanel Agent (Main UI Controller)
├── ImageContainer Agent (Individual Image Management)
└── RowContainer Agent (Row Layout Management)

Secondary Agents (Supporting Services)
├── ImageFactory Agent (Image Creation Strategy)
├── SelectionListener Agent (User Interaction)
└── ImageMetadata Agent (Lightweight Parsing)
```

### Agent Communication Protocol
-   **Property-Based Messaging**: JavaFX properties for reactive updates.
-   **Event-Driven Architecture**: Scroll, zoom, and selection events.
-   **Builder Pattern Commands**: Fluent API for configuration.
-   **Observable Collections**: Dynamic child management.

## Canvas-Based Agents

### 1. CanvasPanel Agent
The main UI component extending `javafx.scene.canvas.Canvas`.

**Responsibilities:**
-   **Layout Computation**: Dynamic row organization and justified alignment.
-   **Viewport Management**: Efficient clipping and visible range calculation.
-   **User Interaction**: Mouse clicks, scroll events, zoom handling.
-   **Rendering Pipeline**: Orchestrates drawing operations.

### 2. ImageContainer Agent
Represents individual images with metadata and rendering logic.

**Responsibilities:**
-   **Lazy Loading**: On-demand image loading with placeholder fallback.
-   **Soft Caching**: Memory-pressure-aware image retention.
-   **Coordinate Management**: Position, scaling, and viewport calculations.
-   **Draw Operations**: Canvas rendering with optimization.

### 3. RowContainer Agent
Manages groups of images organized in horizontal rows.

**Responsibilities:**
-   **Width Normalization**: Proportional scaling for justified layout.
-   **Height Coordination**: Consistent row heights and vertical positioning.
-   **Image Grouping**: Logical organization of images into rows.

## Image Processing Agents

### ImageFactory Agent Interface
A strategy interface for image loading and processing.

**Implementations:**
-   `DefaultImageFactory`: Standard image loading with aspect ratio preservation.
-   `SquareImageFactory`: Center-crop square image generation.

### ImageMetadata Agent
A lightweight image header parser supporting GIF, JPEG, PNG, BMP, and TIFF formats. It extracts dimensions without a full decode, enabling fast layout computation.

## Layout and Rendering

The layout is calculated by organizing images into `RowContainer`s. The `CanvasPanel` then renders only the visible rows and images based on the current scroll offset and viewport height. A justified layout is achieved by scaling images in each row to fit the available width.

## Selection and Interaction

User interactions like clicks, scrolling, and zooming are handled by the `CanvasPanel`. A `SelectionListener` can be registered to respond to image selection events. The component supports single and multi-selection with visual feedback.

## Memory Management

The primary memory optimization is the use of `SoftReference` for caching `Image` objects within each `ImageContainer`. This allows the garbage collector to reclaim image data under memory pressure, which are then reloaded on demand.

## Performance

-   **Viewport Clipping**: Only images currently visible in the viewport are rendered.
-   **Lazy Loading**: Images are not loaded into memory until they are about to be displayed.
-   **Metadata Parsing**: Image dimensions are read from headers first to avoid loading full image data for layout calculations.
-   **Parallel Processing**: Image metadata is extracted in parallel to speed up initial layout.

## Build and Deployment

The project is built with Maven and uses the Java Platform Module System (JPMS).

-   **Dependencies**: `org.openjfx:javafx-controls`.
-   **Build**: `mvn clean package`.
-   **Run**: `mvn javafx:run`.

## Suggested Optimizations

This section provides recommendations for further improving the performance, usability, and robustness of the `CanvasImageGrid` component.

### 1. Advanced Caching Strategies

The current `SoftReference`-based caching is effective but can lead to "cache thrashing" under memory pressure, where images are frequently garbage-collected and reloaded.

-   **Implement a Multi-Level Cache:**
    -   **L1 Cache (Strong References):** An LRU (Least Recently Used) cache holding a small number of images that are currently visible or near the viewport. This guarantees that visible images are not prematurely collected.
    -   **L2 Cache (Soft References):** The existing mechanism for images outside the immediate viewport.
    -   **L3 Cache (Disk Cache):** A disk-based cache for pre-scaled thumbnails. This would reduce CPU usage from repeated scaling operations and provide faster reloads.

### 2. Enhanced Rendering Performance

-   **Dirty Region Rendering:** Instead of redrawing the entire canvas on every change, track which regions of the canvas are "dirty" (e.g., a newly selected image) and only redraw those parts.
-   **Frame Rate Limiting:** During rapid scrolling or zooming, the component might attempt to render more frames than necessary. Implementing a frame rate limiter (e.g., using an `AnimationTimer`) can smooth the experience and reduce CPU load.
-   **Scroll Event Filtering:** Ignore very small scroll deltas (micro-scrolls) to prevent unnecessary redraws. An epsilon-based filter can be effective here.

### 3. Improved User Experience

-   **Predictive Loading:** Analyze the scroll velocity and direction to predict which images will soon enter the viewport. Preload these images in the background to make scrolling appear seamless.
-   **Elastic Overscrolling:** When the user scrolls past the beginning or end of the content, provide visual feedback (e.g., by showing a bouncing effect). This is a standard feature in modern UI frameworks and improves the perceived quality of the component.
-   **Graceful Error Handling:** If an image file is corrupt or cannot be loaded, display a distinct and informative placeholder icon instead of just failing to render it.

### 4. API and Code Quality Enhancements

-   **Correct API Naming:** The property and method names using "hight" should be deprecated and replaced with "height". Provide a clear migration path for users of the library.
    -   Example: Deprecate `setMaxImageHight()` and introduce `setMaxImageHeight()`.
-   **Modernize Testing:** Migrate from JUnit 3.8.1 to JUnit 5. This will enable modern testing features and better integration with current development tools.
-   **Introduce Concurrency Controls:** For thread-safe operations on shared collections (like the list of images), consider using concurrent collections from `java.util.concurrent` or implementing more fine-grained synchronization to prevent potential race conditions.

## Future Enhancements

The `AGENTS.md` document outlines several forward-looking ideas that build upon the suggested optimizations:

-   **TieredCacheAgent**: A formal agent for managing a multi-level cache.
-   **PredictiveLoadAgent**: An agent dedicated to predictive image loading.
-   **MemoryPressureAgent**: An agent that monitors JVM memory usage and can trigger more aggressive cleanup operations if memory pressure becomes high.
