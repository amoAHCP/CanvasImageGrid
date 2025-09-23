# CanvasImageGrid – Architecture & Usage

## Overview
CanvasImageGrid is a JavaFX based component for efficiently showing a large number of images in a scrollable & zoomable justified mosaic (grid). The core logic dynamically computes rows with scaled images to optimally fill the available width. Lazy/on‑demand drawing plus `SoftReference` based caching reduce memory footprint.

## Module / Package
`module canvasImageGrid` (see `module-info.java`) exports `org.jacpfx.image.canvas`.

## Core Classes
| Class | Responsibility |
|-------|----------------|
| `CanvasPanel` | Core UI component (extends `Canvas`): layout, scroll, zoom, dynamic repaint, selection handling. |
| `ImageContainer` | Holds metadata & drawing logic for one image including scaling, soft cache, selection effect. |
| `RowContainer` | Groups `ImageContainer`s of one row with start/end heights and max width. |
| `ImageFactory` | Strategy interface for loading, (optional) post‑processing and size probing without full decode. |
| `DefaultImageFactory` | Loads images with target height (2 * maxHight) maintaining aspect ratio. |
| `SquareImageFactory` | Creates square crops from the source image (center crop using pixel operations). |
| `ImageMetadata` | Lightweight header parser (GIF, JPG, PNG, BMP, TIFF) to extract dimensions & MIME. |
| `SelectionListener` | Callback invoked when the user selects an image (mouse click). |
| `ApplicationMain*` | Demo / launcher classes illustrating usage variants. |

## Data Flow & Rendering Pipeline
1. Builder invocation: `CanvasPanel.createCanvasPanel()...` collects parameters (paths, factory, dimensions, padding etc.) and produces the `CanvasPanel`.
2. Construction: `addImages()` maps paths → `ImageContainer` (parallel stream) using `ImageFactory#getImageSize` for quick dimension lookup and sets initial scale factors.
3. Row layout: `createRows()` accumulates images until width limit reached. A configurable `lineBreakThreshold` decides if the last image still fits into the current row or starts a new one.
4. Normalization: `normalizeWidth()` proportionally rescales all images of a row so that (including padding) they exactly fill the canvas width (justified layout). `normalizeHight()` assigns vertical start coordinates for subsequent rows.
5. Scrolling: `canvasScroll()` updates a vertical offset, calculates visible interval (`start`, `end`) and triggers `renderCanvas()` only for images intersecting the viewport (clipping).
6. Drawing: `ImageContainer#drawImageToCanvas()` loads the image lazily. First a placeholder (rectangle snapshot), then an asynchronous repaint when loading completes. Optional post‑processing (e.g. square crop) via `ImageFactory#postProcess`.
7. Zooming: `zoomFactorProperty` influences the effective max image height and triggers full re‑layout & repaint.
8. Selection: On mouse click, coordinates are checked against image bounds; a drop shadow snapshot effect is applied or reverted.

## Key Properties / Parameters
- `maxImageHightProperty`, `maxImageWidthProperty`: Base sizing inputs (height drives layout; width is computed).
- `paddingProperty`: Horizontal & vertical gap (symmetric) between images.
- `lineBreakThresholdProperty`: Percentage of the last image width that must still fit to stay in the current row. Higher → denser rows.
- `zoomFactorProperty`: Scales target height (`maxHeight * zoom`). Clamped by `inRange()` (0.2–1.5).
- `scrollProperty`: Exposed hook for external scroll triggering (currently not feeding back into layout directly).

## Performance & Memory
- `SoftReference<Image>` allows GC eviction under memory pressure. Re‑loading trades I/O for memory savings.
- Partial rendering: only visible images pass the `filterImagesVisible()` predicate, reducing draw cost.
- Parallel creation of `ImageContainer` accelerates startup with large image sets.
- Header‑only metadata parsing avoids full decode during initial layout.

## Extensibility
1. Implement custom `ImageFactory` (e.g. persistent cache, DB backed images, transformations, lazy cropping).
2. Customize selection visualization (`drawSelectedImageOnConvas()`) to use borders, overlays, badges, etc.
3. Virtual paging / on-demand path streaming instead of eager full list.
4. Keyboard navigation & accessibility focus traversal.
5. Multi‑selection model managing a collection instead of simple toggle.

## Limitations / Known Issues
- Spelling uses `Hight` instead of `Height` in multiple APIs (fix would be breaking; consider deprecation bridge).
- No cancellation for long running I/O; relies on JavaFX internal image loader threads.
- No centralized thumbnail cache (soft references may trigger frequent reloads).
- Legacy JUnit 3.8.1 dependency; no automated tests.
- Hardcoded demo paths (`/Users/amo/Pictures/...`) reduce portability.

## Stability & Safety Considerations
- Missing validation of invalid / unreadable paths (exceptions only logged).
- Potential short OOM spikes with very large individual images before GC clears soft refs.
- No throttling of rapid scroll + zoom interplay (simple boolean toggle only).

## API Usage (Builder)
Example:
```java
CanvasPanel panel = CanvasPanel.createCanvasPanel()
    .imagePath(imagePaths)
    .imageFactory(new DefaultImageFactory())
    .width(800)
    .hight(600) // note: "hight" spelling kept for compatibility
    .padding(5)
    .lineBreakLimit(0.1d)
    .maxImageWidth(150)
    .maxImageHight(150)
    .selectionListener((x,y,images) -> { /* handle selection */ });
```
Bind width/height to parent container after creation and add to scene graph.

## Events & Reactive Properties
Property changes (padding, zoom, max height, line break threshold, canvas size, children list) invalidate layout and trigger a full recomputation of rows plus repaint.

## Recommendations / Future Improvements
- Introduce properly spelled `Height` APIs with deprecation of old ones.
- Add `ThumbnailCache` (LRU + optional disk persistence).
- Batch invalidations (e.g. via `AnimationTimer`) to coalesce multiple property changes per frame.
- Speculative prefetch: background load of next/previous viewport range for smoother scroll.
- Consider `VirtualFlow` style virtualization for extremely large collections.
- Modernize Maven: add JavaFX plugin & modular build, update to JUnit 5.
- Add tests for: narrow width edge cases, zoom range bounds, `lineBreakThreshold` behavior.

## Build / Run (Java 17+ Example)
Suggested POM updates:
- Set compiler release & encoding.
- Add JavaFX dependencies or use `javafx-maven-plugin` (preferred).

Run (adjust image path constants as needed):
```bash
mvn clean package
mvn javafx:run -Dprism.verbose=true
```

## License
Apache 2.0 (see `LICENSE`).

---
Updated: 2025-09-05
