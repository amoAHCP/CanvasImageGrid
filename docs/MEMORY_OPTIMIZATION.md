# CanvasImageGrid Memory Optimization Guide

This document summarizes concrete optimization opportunities for memory usage in the current code base (`org.jacpfx.image.canvas`). Items are grouped by complexity / risk.

## 1. Quick Low-Risk Wins

| Area | Issue | Recommendation | Expected Impact |
|------|-------|---------------|-----------------|
| Placeholder image | Each unloaded image creates a unique rectangle snapshot placeholder. | Use a single shared static placeholder `Image`, or defer drawing until real image available. | Fewer temporary `Image` objects. |
| SoftReference fields | `imageRefOrig` always allocated. | Allocate only when selection starts; set to `null` otherwise. | Slight object reduction. |
| Redundant state | `scaledX` & `scaledY` stored plus `endX/endY` & `scaleFactor`. | Compute on demand (`endX * scaleFactor`) or cache lazily. | Saves two doubles per image (negligible individually, large sets add up). |
| clone/reset usage | `clone()` mutates the same instance; `resetStart()` duplicates logic. | Remove `clone()` or implement real copy; consolidate reset logic. | Avoid confusion & accidental retention. |

## 2. Medium Complexity Improvements

| Topic | Description | Action |
|-------|-------------|--------|
| Thumbnail caching | SoftReferences alone cause re-decoding thrash under GC pressure. | Add explicit LRU (e.g., `LinkedHashMap` or caffeine) keyed by path + size tier. |
| Two-level images | Full-resolution (2 * maxHight) maybe overkill initially. | Load a small thumbnail first; upgrade to full only when selected / zoomed. |
| Row object churn | Each re-layout creates fresh `RowContainer` & lists. | Reuse row objects: maintain a pool, `clear()` and refill. |
| Parallel stream cost | `parallelStream()` for image path mapping can blow up threads & memory for metadata. | Switch to sequential or fixed-size `ExecutorService`. |
| Selection effect snapshot | Snapshot with DropShadow duplicates full bitmap. | Apply `DropShadow` as a runtime effect or draw an outline via `strokeRect`. |

## 3. Larger Architectural Steps

| Topic | Goal | Approach |
|-------|------|----------|
| Virtualization beyond clipping | Currently only y-range clipping; all `ImageContainer`s live simultaneously. | Introduce paging: keep only metadata for distant images, lazily instantiate containers when entering buffer zone. |
| Progressive decoding | Avoid loading full-size until needed. | Add strategy: first read headers (already done), then load scaled-down variant (JavaFX `Image` width/height constraints), final load only on demand. |
| Off-heap / pooled buffers | Reduce heap pressure for cropping logic (`SquareImageFactory`). | Byte buffer pool (e.g., Agrona or custom) for pixel copies. |
| Multi-tier cache | Memory + disk fallback for large sets. | Use hash of path & params; store pre-scaled thumbnails on disk (e.g., simple directory cache). |

## 4. Zoom & Repaint Efficiency

| Issue | Current Behavior | Optimization |
|-------|------------------|-------------|
| Multiple immediate invalidations | Every property listener triggers full recompute. | Introduce a debounce (schedule layout after ~16 ms or next pulse). |
| Zoom recalculation cost | Full row rebuild each zoom event in quick succession. | Accumulate zoom deltas until user stops (e.g., after 120 ms inactivity) then relayout. |

## 5. Selection Handling

| Issue | Current Behavior | Improvement |
|-------|------------------|------------|
| Memory duplication | Effect snapshot duplicates underlying pixel data. | Use effect layering (Canvas overlay) or minimal highlight rectangle. |
| State toggling | Replaces `imageRef` each click. | Maintain original reference; apply effect at draw time only. |

## 6. Lifecycle & Eviction Strategy

Add explicit hooks:
```java
public void evictOutOfRange(double topY, double bottomY) {
    children.stream()
        .filter(img -> img.getStartY() + img.getScaledY() < topY - buffer
                     || img.getStartY() > bottomY + buffer)
        .forEach(ImageContainer::clearImageRef); // hint GC
}
```
Call after scroll settles.

## 7. Instrumentation & Metrics

| Metric | Why | Implementation |
|--------|-----|--------------|
| Image load count | Detect thrash vs. cache hit | Increment counter in `ImageFactory#createImage()` |
| Average decode time | Spot slow sources | Measure start/end around creation |
| Cache hit ratio | Validate LRU tuning | Wrap LRU with stats proxy |

Example lightweight counters:
```java
public final class ImageStats {  
  public static final AtomicLong loads = new AtomicLong();
  public static final AtomicLong postProcess = new AtomicLong();
}
```
Increment inside factory, log every N seconds.

## 8. GC & JVM Tuning

| Flag | Purpose |
|------|---------|
| `-XX:+UseG1GC` | Balanced GC for mixed allocation sizes. |
| `-XX:InitiatingHeapOccupancyPercent=30` | Earlier concurrent cycle to free soft refs sooner. |
| `-XX:+ParallelRefProcEnabled` | Faster reference processing (Soft/Weak). |

## 9. Prioritized Action Plan

1. Shared placeholder + optional deferred drawing.
2. LRU thumbnail cache (in-memory) + two-step load.
3. Debounce layout & zoom invalidations.
4. Row reuse pool.
5. Lightweight selection overlay (no snapshot duplication).
6. Eviction for off-screen images after scroll settle.
7. Metrics to validate improvements.
8. Disk-backed cache (optional, after verifying need).

## 10. Sample LRU Cache Skeleton
```java
public class ThumbnailCache {
    private final int maxEntries;
    private final Map<Path, Image> cache;
    public ThumbnailCache(int maxEntries) {
        this.maxEntries = maxEntries;
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Path, Image> eldest) {
                return size() > ThumbnailCache.this.maxEntries;
            }
        };
    }
    public synchronized Image get(Path p, Supplier<Image> loader) {
        return cache.computeIfAbsent(p, k -> loader.get());
    }
}
```
Integrate in `ImageFactory` implementation.

## 11. Risks & Trade-offs

| Change | Risk | Mitigation |
|--------|------|-----------|
| Aggressive eviction | Frequent reload stutter | Tune thresholds, measure. |
| Placeholder removal | Empty gaps while loading | Keep minimal 1x1 or blurred placeholder. |
| Debounce | Slight latency before update | Keep under a frame (~16 ms). |
| Cache | Higher base memory | Parameterize max entries. |

## 12. Validation Checklist
- [ ] Measure baseline memory (RSS / heap histogram)
- [ ] Implement shared placeholder + counters
- [ ] Add LRU & record hit ratio > 70%
- [ ] Ensure scroll remains smooth (FPS stable)
- [ ] Confirm GC frequency decreases (GC logs)

---
_Last updated: 2025-09-05_
