#!/bin/bash

# Native Image Build Script for CanvasImageGrid
echo "Building native executable for CanvasImageGrid..."

# First, build the JAR
echo "Step 1: Building JAR..."
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "Maven build failed!"
    exit 1
fi

echo "Step 2: Creating native image with GraalVM..."

# Get the classpath
CLASSPATH="target/CanvasImageGrid-1.0-SNAPSHOT.jar"
CLASSPATH="${CLASSPATH}:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)"

# Create native image
native-image \
    --no-fallback \
    --install-exit-handlers \
    -H:+ReportExceptionStackTraces \
    -H:+AddAllCharsets \
    -H:EnableURLProtocols=http,https,file,jar \
    -H:+UnlockExperimentalVMOptions \
    -H:+JNI \
    -H:+UseServiceLoaderFeature \
    --initialize-at-build-time=com.sun.javafx.application.PlatformImpl \
    --initialize-at-run-time=com.sun.glass.ui.Application \
    --initialize-at-run-time=javafx.application.Platform \
    --initialize-at-run-time=javafx.scene.media.MediaPlayer \
    --initialize-at-run-time=javafx.scene.text.Font \
    -H:ConfigurationFileDirectories=src/main/resources/META-INF/native-image/ \
    -cp "${CLASSPATH}" \
    org.jacpfx.image.canvas.ApplicationMainSingleWindow \
    -o target/CanvasImageGrid

echo "Native executable created at: target/CanvasImageGrid"
echo "To run: ./target/CanvasImageGrid"
