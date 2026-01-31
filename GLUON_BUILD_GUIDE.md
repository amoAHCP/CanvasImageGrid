# Gluon Substrate Native Build Guide

This guide explains how to build a native executable using Gluon Client (Substrate), which is specifically designed for JavaFX applications.

## Why Gluon Instead of Vanilla GraalVM?

Gluon Client handles all the JavaFX-specific complexity:
- ✅ Automatically manages JavaFX native libraries (.dylib, .so, .dll)
- ✅ Handles JNI bindings for graphics pipeline (Prism, Glass)
- ✅ Includes proper reflection configuration for JavaFX internals
- ✅ Supports multiple platforms (macOS, Linux, Windows, iOS, Android)
- ✅ Battle-tested with many JavaFX applications in production

## Prerequisites

1. **GraalVM 21.x** with native-image installed (**CRITICAL: NOT 25.x!**)
2. **Maven 3.8.8** (IMPORTANT: Gluon requires exactly Maven 3.8.x, NOT 3.9.x)
3. **macOS, Linux, or Windows**
4. **8GB+ RAM** (native build is memory-intensive)
5. **15-20 minutes** for first build (downloads native libraries)

**⚠️ CRITICAL VERSION REQUIREMENTS:**
- **GraalVM**: MUST be 21.x (NOT 22.x, NOT 23.x, NOT 25.x)
- **Maven**: MUST be 3.8.x (NOT 3.9.x)
- **Reason**: Gluon Client 1.0.24 uses Substrate 0.0.64 which requires GraalVM 21

### Installing GraalVM 21

**CRITICAL**: Gluon Client requires GraalVM 21.x. It does NOT work with GraalVM 25.x.

```bash
# Using SDKMAN (recommended)
sdk install java 21.0.2-graal
sdk use java 21.0.2-graal

# Install native-image component (if not included)
gu install native-image

# Set GRAALVM_HOME (required by Gluon)
export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.2-graal
export JAVA_HOME=$GRAALVM_HOME

# Add to your ~/.zshrc or ~/.bashrc to make permanent:
echo 'export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.2-graal' >> ~/.zshrc
echo 'export JAVA_HOME=$GRAALVM_HOME' >> ~/.zshrc

# Verify installation
java -version  # Should show "21.0.2" and "GraalVM"
native-image --version  # Should show "21.0.2"
echo $GRAALVM_HOME  # Should show GraalVM 21 path
```

## Quick Start

### Build Native Executable

```bash
./build-gluon.sh
```

This script will:
1. Clean previous builds
2. Run `mvn gluonfx:build`
3. Create native executable in `target/gluonfx/<platform>/`

**First build**: 10-20 minutes (downloads JavaFX native libraries)
**Subsequent builds**: 5-10 minutes

### Run the Native Executable

```bash
# macOS ARM64 (Apple Silicon)
./target/gluonfx/aarch64-darwin/CanvasImageGrid ~/Pictures

# macOS x86_64 (Intel)
./target/gluonfx/x86_64-darwin/CanvasImageGrid ~/Pictures

# Linux x86_64
./target/gluonfx/x86_64-linux/CanvasImageGrid ~/Pictures

# Linux ARM64
./target/gluonfx/aarch64-linux/CanvasImageGrid ~/Pictures
```

## Manual Build Steps

### Option 1: Build for Current Platform (host)

```bash
mvn clean
mvn gluonfx:build
```

The executable will be in `target/gluonfx/<your-platform>/`

### Option 2: Build for Specific Platform

```bash
# For macOS ARM64
mvn clean gluonfx:build -Dgluonfx.target=mac-aarch64

# For macOS x86_64
mvn clean gluonfx:build -Dgluonfx.target=mac

# For Linux x86_64
mvn clean gluonfx:build -Dgluonfx.target=linux

# For Linux ARM64
mvn clean gluonfx:build -Dgluonfx.target=linux-aarch64

# For Windows
mvn clean gluonfx:build -Dgluonfx.target=windows
```

### Option 3: Build and Run

```bash
mvn gluonfx:build gluonfx:nativerun
```

This builds and immediately runs the native executable.

## Configuration

### POM.xml Configuration

The Gluon Client plugin is configured in `pom.xml`:

```xml
<plugin>
    <groupId>com.gluonhq</groupId>
    <artifactId>gluonfx-maven-plugin</artifactId>
    <version>1.0.22</version>
    <configuration>
        <target>${gluonfx.target}</target>
        <mainClass>org.jacpfx.image.canvas.ApplicationMainNative</mainClass>
        <verbose>true</verbose>
        <attachList>
            <list>display</list>
            <list>lifecycle</list>
            <list>statusbar</list>
            <list>storage</list>
        </attachList>
        <reflectionList>
            <list>org.jacpfx.image.canvas.ApplicationMainNative</list>
            <list>org.jacpfx.image.canvas.CanvasPanel</list>
            <!-- Add more classes as needed -->
        </reflectionList>
    </configuration>
</plugin>
```

### Key Configuration Options

- **target**: Platform to build for (`host`, `mac`, `linux`, `windows`, `ios`, `android`)
- **mainClass**: Your main application class
- **verbose**: Enable detailed build output
- **attachList**: Gluon Attach services to include
- **reflectionList**: Classes that need reflection support

## Troubleshooting

### Build Fails: "native-image not found"

**Solution**: Ensure you're using GraalVM with native-image installed:

```bash
java -version  # Must show GraalVM
gu install native-image
```

### Build Fails: Out of Memory

**Solution**: Increase Maven memory:

```bash
export MAVEN_OPTS="-Xmx4G"
mvn gluonfx:build
```

### Build is Extremely Slow

**First build**: Gluon downloads platform-specific JavaFX native libraries (~500MB). This is normal.

**Subsequent builds**: Should be faster (5-10 minutes).

### Runtime Error: "Cannot find native library"

**Solution**: Ensure you're running the executable on the same platform it was built for. Cross-compilation requires additional setup.

### Application Window Doesn't Appear

**macOS**: You may need to allow the executable in System Preferences > Security & Privacy

**Linux**: Ensure you have required libraries:
```bash
sudo apt-get install libgtk-3-0 libgl1-mesa-glx libasound2
```

## Gluon Attach Services

Gluon Attach provides native features:

- **display**: Display properties (brightness, notch, etc.)
- **lifecycle**: Application lifecycle events
- **statusbar**: Status bar customization
- **storage**: File storage access
- **pictures**: Camera and photo library access
- **settings**: Platform settings
- And more...

Enable services in `<attachList>` as needed.

## Platform-Specific Notes

### macOS

- **Code Signing**: For distribution, sign with:
  ```bash
  codesign --force --deep --sign - target/gluonfx/aarch64-darwin/CanvasImageGrid
  ```

- **Notarization**: Required for Gatekeeper (distributing outside App Store)

### Linux

- **AppImage**: Create distributable package:
  ```bash
  mvn gluonfx:build gluonfx:package
  ```

### Windows

- **Visual Studio**: Requires Visual Studio with C++ build tools
- **MSVC**: Must be in PATH

## Advanced Usage

### Custom Native Build Options

Add to plugin configuration:

```xml
<nativeImageArgs>
    <arg>-H:+ReportExceptionStackTraces</arg>
    <arg>--verbose</arg>
</nativeImageArgs>
```

### Include Resources

Gluon automatically includes:
- Images (png, jpg, gif, etc.)
- CSS files
- FXML files
- Fonts

For custom resources, add:

```xml
<resourcesList>
    <list>.*\\.custom$</list>
</resourcesList>
```

### JNI Configuration

Gluon handles JavaFX JNI automatically. For custom JNI:

```xml
<jniList>
    <list>com.mycompany.MyNativeClass</list>
</jniList>
```

## Performance

### Build Time
- **First build**: 10-20 minutes (downloads native libs)
- **Clean build**: 5-10 minutes
- **Incremental**: 3-5 minutes

### Executable Size
- **Typical**: 60-100 MB
- **With media**: 100-150 MB
- **Stripped**: Can reduce by 20-30% with stripping

### Runtime Performance
- **Startup**: ~100-500ms (much faster than JVM)
- **Memory**: Lower than JVM (no JIT overhead)
- **Graphics**: Native performance

## Comparison: Gluon vs Vanilla GraalVM

| Feature | Gluon Client | Vanilla GraalVM |
|---------|--------------|-----------------|
| JavaFX Support | ✅ Automatic | ❌ Manual/Complex |
| Native Libraries | ✅ Handled | ❌ Manual |
| Mobile Support | ✅ iOS/Android | ❌ No |
| Configuration | ✅ Simple | ❌ Complex |
| Build Time | ⚠️ Longer | ✅ Faster |
| Documentation | ✅ Excellent | ⚠️ Limited for JavaFX |

## Resources

- [Gluon Client Documentation](https://docs.gluonhq.com/client/)
- [Gluon GitHub](https://github.com/gluonhq/client-maven-plugin)
- [JavaFX + Gluon Samples](https://github.com/gluonhq/gluon-samples)
- [Gluon Community](https://gluonhq.com/community/)

## Support

- **Community**: [Gluon Slack](https://gluonhq.com/slack-signup/)
- **Documentation**: [docs.gluonhq.com](https://docs.gluonhq.com/)
- **Issues**: [GitHub Issues](https://github.com/gluonhq/client-maven-plugin/issues)

## Next Steps

1. **Build**: `./build-gluon.sh`
2. **Test**: Run the executable with your image directory
3. **Optimize**: Adjust configuration for your needs
4. **Distribute**: Package for your target platform

---

**Note**: Gluon Client is the recommended approach for JavaFX native images. It handles all the complexity that vanilla GraalVM struggles with.
