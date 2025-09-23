# Canvas Clear Strategy Analysis

## Current State
- In `CanvasPanel.renderCanvas(...)` each render pass performs a full clear: `gc.clearRect(0,0,width,height)`.
- Render passes are triggered by: scroll events, resize events, zoom changes, padding / threshold changes and container (row) recomputation.
- All visible images are fully redrawn afterwards (no partial blitting / reuse of prior pixels).
- There is no frame throttling for very small scroll deltas (trackpad micro‑deltas cause full clears).

## Why a full clear is currently acceptable
- Number of visible images per viewport is moderate (depends on row normalization and max height).
- JavaFX canvas pipeline (Prism / Metal / OpenGL) batches clear/fill efficiently.
- No extra state (offscreen buffer) required → simple and maintainable.

## Potential scaling issues
| Risk | Description | Impact |
|------|-------------|--------|
| High image count | Many images in view simultaneously | FPS drop every scroll tick |
| Very small deltas | Frequent clears for < 0.5px motion | CPU overhead without visual gain |
| Layout overhead | Rebuilding all RowContainers per scroll | UI thread blocking |
| GPU fill limits | Very large canvas (e.g. 4k+) | Noticeable fill cost |

## Optimization options (ordered by effort vs. gain)
### 1. Early exit / epsilon filter (LOW)
- Idea: If |deltaY| < `epsilon` (e.g. 0.25–0.35) skip repaint.
- Benefit: Less clears for micro scroll.
- Effort: Minimal (condition in scroll handler before `prepareAndRender`).

### 2. Visible row range restriction (LOW–MID)
- Binary search for first / last visible row → iterate only those.
- Benefit: Fewer loops; full clear cost unchanged but less per‑frame work.
- Synergizes with future blit strategies.

### 3. Replace streams with indexed loops (LOW)
- Removes lambda / iterator overhead in hot path.
- Benefit: Small CPU reduction, more predictable.

### 4. Conservative dirty region approach (MID–HIGH)
- For small scroll deltas: clear & redraw only the newly exposed strip (top or bottom).
- Requires: Shifting existing pixels or a blit step; otherwise stale pixels remain.

### 5. Blitting (snapshot + partial redraw) (HIGH)
- Flow: Take snapshot → drawImage shifted by delta → clear freed strip → draw new images there.
- Benefit: Avoid full redraw for minor scroll increments.
- Risks: Snapshot cost may outweigh savings at high event frequency.

### 6. Offscreen double buffering (HIGH)
- Render to offscreen canvas, then single drawImage to visible canvas.
- Useful if layout+draw contain hundreds of operations or to reduce flicker.

### 7. Adaptive strategy (HIGH)
- If |delta| > VIEWPORT_HEIGHT * 0.25 → full redraw.
- Else → blit path.
- Added complexity: Maintain & test two code paths.

## Why not partial clear without blitting?
- Positions are recomputed relative to a global offset (`rowStart + offset`).
- Clearing only a portion (e.g. bottom strip) without shifting existing pixels leaves misaligned content.

## Measurable entry level steps
| Action | Estimated Benefit | Risk | Effort |
|--------|-------------------|------|--------|
| Epsilon threshold | 5–20% fewer unnecessary repaints | Very low | ~5 lines |
| Row binary search | Fewer iterations with many rows | Very low | 20–30 lines |
| Indexed loops | Small CPU gain | None | 10–15 lines |

## Short-term recommendation (no architecture change)
1. Add epsilon filter in scroll handler (e.g. 0.3): `if (Math.abs(delta) < EPS) return;`
2. Binary search visible row range + indexed loops instead of streams.
3. Optional: Frame skip if time since last render < 8 ms (mitigate event storms).

## Long-term recommendation (only if profiling shows a bottleneck)
- Prototype blitting and profile with JFR / VisualVM (frame time distribution before/after).
- Validate snapshot vs. full redraw cost under typical image counts.
- If no clear win → keep simple full redraw for maintainability.

## Example epsilon filter (pseudo code)
```java
private static final double SCROLL_EPS = 0.30;
...
void canvasScroll(...){
    double delta = handler.getDeltaY();
    if (Math.abs(delta) < SCROLL_EPS) return; // skip tiny motion
    // remaining logic
}
```

## Potential KPIs for later validation
- Avg. render time (ms) per scroll event.
- Render calls per 100 scroll events (before/after epsilon).
- GPU vs. CPU time (indirect via sampling profile).

## Conclusion
The current full-clear approach is simple and adequate for moderate data sizes. First optimizations should target avoiding needless repaints (epsilon) and reducing iteration cost (row localization). Complex blitting / dirty region strategies should only be considered after profiling confirms a full redraw bottleneck; otherwise complexity grows without guaranteed payoff.

---
*Generated for the current project state. Add real measurements once profiling data is available.*
