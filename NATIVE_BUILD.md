# GraalVM Native Image Build Guide

This guide explains how to build and run CanvasImageGrid as a native executable using GraalVM.

## Prerequisites

1. **GraalVM 21+** installed (currently tested with GraalVM CE 25.0.1)
2. **native-image** component installed
3. **Maven 3.9+**
4. **macOS, Linux, or Windows** (build scripts may need adjustments for Windows)

### Installing GraalVM and native-image

```bash
# If using SDKMAN (recommended for macOS/Linux)
sdk install java 25.0.1-graalce
sdk use java 25.0.1-graalce

# Install native-image component
gu install native-image

# Verify installation
java -version  # Should show GraalVM
native-image --version
```

## Quick Start

### Option 1: Using the Build Script (Recommended)

```bash
./build-native-improved.sh
```

This script will:
1. Clean and package the application as a shaded JAR
2. Run GraalVM native-image with optimized settings
3. Create the executable at `target/CanvasImageGrid`

Expected build time: **5-10 minutes** (depending on your machine)

### Option 2: Manual Build

```bash
# Step 1: Build the JAR
mvn clean package -DskipTests

# Step 2: Run native-image
native-image \
    --no-fallback \
    --verbose \
    -H:+ReportExceptionStackTraces \
    -H:+AddAllCharsets \
    -H:EnableURLProtocols=http,https,file,jar \
    -H:+JNI \
    -H:+UseServiceLoaderFeature \
    -H:IncludeResources='.*\.png$|.*\.jpg$|.*\.jpeg$|.*\.gif$|.*\.bmp$|.*\.properties$' \
    -H:ConfigurationFileDirectories=src/main/resources/META-INF/native-image/ \
    --initialize-at-build-time=org.jacpfx.image.canvas \
    --initialize-at-run-time=com.sun.glass.ui \
    --initialize-at-run-time=com.sun.prism \
    --initialize-at-run-time=javafx.application.Platform \
    --initialize-at-run-time=javafx.scene.text.Font \
    --initialize-at-run-time=javafx.scene.media \
    -jar target/CanvasImageGrid-1.0-SNAPSHOT.jar \
    -H:Name=target/CanvasImageGrid \
    -H:Class=org.jacpfx.image.canvas.ApplicationMainNative
```

## Running the Native Executable

### With Custom Image Directory

```bash
./target/CanvasImageGrid ~/Pictures/MyPhotos
```

### Using Default (~/Pictures)

```bash
./target/CanvasImageGrid
```

## What's Different in the Native Version?

The native version (`ApplicationMainNative`) differs from the standard version:

1. **Flexible Image Path**: Accepts image directory as command-line argument
2. **No Hardcoded Paths**: Works on any system without modification
3. **Fallback Handling**: Shows helpful message if no images found
4. **Optimized for AOT**: Configured for Ahead-of-Time compilation

## Native Image Configuration Files

All configuration files are in `src/main/resources/META-INF/native-image/`:

- **reflect-config.json** - Reflection configuration for JavaFX classes
- **resource-config.json** - Resource patterns (images, fonts, etc.)
- **jni-config.json** - JNI configuration for native library access
- **native-image.properties** - Additional native-image settings

## Troubleshooting

### Build Fails with "native-image not found"

Make sure you're using GraalVM and have installed native-image:

```bash
java -version  # Must show GraalVM
gu install native-image
```

### Build Fails with Memory Errors

Increase heap size for the native-image build:

```bash
export NATIVE_IMAGE_OPTS="-J-Xmx8G"
./build-native-improved.sh
```

### Runtime Error: "Image cannot be loaded"

The native executable needs absolute paths for images. Relative paths should work from the directory where you run it.

### Build Error: PlatformPreferences Initialization

**Error**: `com.sun.javafx.application.preferences.PlatformPreferences was found in the image heap`

**Cause**: Conflicting initialization settings between `native-image.properties` and command-line arguments.

**Solution**: Ensure both files use package-level runtime initialization:
- In `src/main/resources/META-INF/native-image/native-image.properties`: use `--initialize-at-run-time=com.sun.javafx.application`
- In `build-native-improved.sh`: use `--initialize-at-run-time=com.sun.javafx.application`

**Do NOT** try to initialize individual JavaFX classes like `PlatformImpl` at build-time, as this creates objects that must be runtime-initialized.

### Application Shows Black Window

This usually means JavaFX native libraries aren't properly initialized. Ensure:
1. You're running on the same OS where you built the native image
2. The `--initialize-at-run-time` settings are correct for JavaFX classes

### Platform-Specific Issues

**macOS**: You may need to allow the executable in System Preferences > Security & Privacy

**Linux**: Ensure you have the required libraries:
```bash
sudo apt-get install libgtk-3-0 libgl1-mesa-glx libasound2
```

## Performance Benefits

Native executables offer:
- **Faster Startup**: ~50ms vs ~2000ms for JVM
- **Lower Memory Footprint**: No JIT compiler overhead
- **No JVM Required**: Standalone executable
- **Instant-On**: No warmup time needed

## Size Comparison

- **JAR (shaded)**: ~15-20 MB
- **Native Executable**: ~80-120 MB (includes JavaFX runtime)

The larger size is due to bundling all required libraries, but startup and runtime performance are significantly improved.

## Advanced: Generating Configuration with Tracing Agent

If you encounter missing reflection/resource errors, use the tracing agent:

```bash
# Run the JAR with the agent
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
     -jar target/CanvasImageGrid-1.0-SNAPSHOT.jar ~/Pictures

# This will update the configuration files
# Then rebuild the native image
./build-native-improved.sh
```

## Known Limitations

1. **Platform-Specific**: Native executable only runs on the platform where it was built
2. **No JMX**: Java Management Extensions not available in native-image
3. **Limited Reflection**: Only explicitly configured reflection works
4. **Static Analysis**: All code paths must be reachable at build time

## See Also

- [GraalVM Native Image Documentation](https://www.graalvm.org/latest/reference-manual/native-image/)
- [JavaFX on GraalVM](https://github.com/gluonhq/substrate)
- [Main Project Documentation](PROJECT_DOCUMENTATION.md)
