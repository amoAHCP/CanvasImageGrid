# JavaFX GraalVM Native Image - Complete Configuration Guide

## Problem: QuantumToolkit ClassNotFoundException

This error occurs when JavaFX tries to dynamically load the `QuantumToolkit` class at runtime, but GraalVM native-image hasn't been told to include it.

### Error Message
```
java.lang.ClassNotFoundException: com.sun.javafx.tk.quantum.QuantumToolkit
        at java.lang.Class.forName(DynamicHub.java:1708)
        at com.sun.javafx.tk.Toolkit.getToolkit(Toolkit.java:241)
```

## Solution

### 1. Add to reflect-config.json

All JavaFX internal classes that are loaded via reflection must be explicitly registered:

```json
{
    "name": "com.sun.javafx.tk.quantum.QuantumToolkit",
    "allDeclaredConstructors": true,
    "allPublicConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true
}
```

### 2. Initialize at Runtime

The entire JavaFX toolkit must be initialized at runtime, not build-time:

**native-image.properties:**
```ini
--initialize-at-run-time=com.sun.javafx
--initialize-at-run-time=com.sun.glass.ui
--initialize-at-run-time=com.sun.prism
--initialize-at-run-time=javafx
```

## Complete Reflection Configuration

### Core JavaFX Toolkit Classes (CRITICAL)

These classes are loaded dynamically and MUST be in reflect-config.json:

1. **com.sun.javafx.tk.quantum.QuantumToolkit** - Main JavaFX toolkit
2. **com.sun.glass.ui.mac.MacApplication** - macOS window system (or equivalent for Linux/Windows)
3. **com.sun.glass.ui.mac.MacPlatformFactory** - Platform factory
4. **com.sun.glass.ui.mac.MacPlatform** - Platform abstraction
5. **com.sun.prism.es2.MacGLFactory** - OpenGL graphics factory
6. **com.sun.prism.es2.MacGLContext** - OpenGL rendering context
7. **com.sun.prism.es2.MacGLDrawable** - OpenGL drawable surface
8. **com.sun.prism.es2.MacGLPixelFormat** - Pixel format configuration

### Application Classes

Your application main class and any classes loaded via reflection:

```json
{
    "name": "org.jacpfx.image.canvas.ApplicationMainNative",
    "allDeclaredConstructors": true,
    "allPublicConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true
}
```

### JavaFX Public API Classes

Common JavaFX classes used in applications:

- javafx.application.Application
- javafx.stage.Stage
- javafx.scene.Scene
- javafx.scene.canvas.Canvas
- javafx.scene.image.Image
- javafx.scene.layout.* (StackPane, VBox, etc.)
- javafx.scene.control.* (Label, Button, etc.)

## Runtime Initialization Strategy

### Use Package-Level Initialization

**DO:**
```bash
--initialize-at-run-time=com.sun.javafx      # Entire package
--initialize-at-run-time=javafx              # Entire package
```

**DON'T:**
```bash
--initialize-at-run-time=com.sun.javafx.application.PlatformImpl  # Too specific
--initialize-at-build-time=com.sun.javafx.tk.quantum.QuantumToolkit  # WRONG!
```

### Why Package-Level?

1. **Avoids initialization order issues** - JavaFX classes have complex dependencies
2. **Prevents build-time instantiation** - Some classes create objects that reference others
3. **Simpler configuration** - Less chance of missing a class

## Platform-Specific Considerations

### macOS (darwin)
- Use `MacApplication`, `MacGLFactory`, etc.
- Requires native libraries from JavaFX SDK

### Linux
Replace `Mac` prefixes with `Gtk`:
- `com.sun.glass.ui.gtk.GtkApplication`
- `com.sun.glass.ui.gtk.GtkPlatformFactory`
- `com.sun.prism.es2.X11GLFactory`

### Windows
Replace `Mac` prefixes with `Win`:
- `com.sun.glass.ui.win.WinApplication`
- `com.sun.glass.ui.win.WinPlatformFactory`
- `com.sun.prism.d3d.D3DPipeline`

## Testing the Configuration

### 1. Build
```bash
./build-native-improved.sh
```

### 2. Run with Verbose Output
```bash
./target/CanvasImageGrid --verbose 2>&1 | tee run.log
```

### 3. Check for Reflection Warnings

If you see warnings like:
```
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module'
```

This usually means a class is missing from reflection config.

## Common Issues

### Issue: ClassNotFoundException for QuantumToolkit
**Solution:** Add to reflect-config.json (see above)

### Issue: NoSuchMethodException
**Solution:** Add `"allDeclaredMethods": true` to the class entry

### Issue: "No toolkit found"
**Solution:** Ensure platform-specific toolkit classes are registered (MacApplication, etc.)

### Issue: "Graphics Device initialization failed for: es2, sw"
**Error**: `Error initializing QuantumRenderer: no suitable pipeline found`
**Solution:** Add graphics pipeline classes to reflect-config.json:
- `com.sun.prism.es2.ES2Pipeline`
- `com.sun.prism.sw.SWPipeline`  
- `com.sun.prism.GraphicsPipeline`
- `com.sun.scenario.effect.impl.Renderer`

### Issue: Black window or graphical glitches
**Solution:** Check Prism graphics pipeline classes are registered (MacGLFactory, etc.)

## Debugging Tips

### Use Tracing Agent

Generate complete configuration automatically:

```bash
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
     -jar target/CanvasImageGrid-1.0-SNAPSHOT.jar ~/Pictures
```

This will capture ALL reflection, JNI, and resource accesses and update your configuration files.

### Enable Native-Image Verbose Logging

```bash
native-image --verbose \
    -H:+PrintClassInitialization \
    -H:+TraceClassInitialization \
    ...
```

## Complete Working Configuration

See the project's configuration files:
- `src/main/resources/META-INF/native-image/reflect-config.json`
- `src/main/resources/META-INF/native-image/native-image.properties`
- `build-native-improved.sh`

These have been tested and verified to work with JavaFX 25 on GraalVM 25.0.1.
