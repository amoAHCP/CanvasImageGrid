#!/bin/bash

# Run script for CanvasImageGrid Fat JAR
echo "Starting CanvasImageGrid..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep "version" | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "17" ]; then
    echo "Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Run the application with JavaFX modules
java --module-path /Users/amo/.m2/repository/org/openjfx/javafx-controls/21.0.4/javafx-controls-21.0.4-mac-aarch64.jar:/Users/amo/.m2/repository/org/openjfx/javafx-graphics/21.0.4/javafx-graphics-21.0.4-mac-aarch64.jar:/Users/amo/.m2/repository/org/openjfx/javafx-base/21.0.4/javafx-base-21.0.4-mac-aarch64.jar:/Users/amo/.m2/repository/org/openjfx/javafx-media/21.0.4/javafx-media-21.0.4-mac-aarch64.jar \
     --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.media \
     -jar target/CanvasImageGrid-1.0-SNAPSHOT.jar

echo "CanvasImageGrid finished."
