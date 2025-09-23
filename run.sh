#!/bin/bash

# Universal launcher script for CanvasImageGrid
echo "Starting CanvasImageGrid..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17+ and try again"
    exit 1
fi

# Get Java version
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed 's/^1\.//' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "17" ]; then
    echo "Error: Java 17 or higher is required. Current version: $(java -version 2>&1 | head -1)"
    exit 1
fi

# Check if JavaFX is available in classpath by trying to run
echo "Testing JavaFX availability..."

# Method 1: Try with module path (for newer Java versions)
if java --list-modules | grep -q javafx; then
    echo "Using system JavaFX modules..."
    java --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.media \
         -jar target/CanvasImageGrid-1.0-SNAPSHOT.jar
elif [ -d "$HOME/.m2/repository/org/openjfx" ]; then
    echo "Using Maven local repository JavaFX..."
    # Find JavaFX JARs in Maven repository
    JAVAFX_PATH=""
    for module in javafx-base javafx-graphics javafx-controls javafx-media; do
        JAR_PATH=$(find "$HOME/.m2/repository/org/openjfx/$module" -name "*mac-aarch64*.jar" 2>/dev/null | head -1)
        if [ -z "$JAR_PATH" ]; then
            JAR_PATH=$(find "$HOME/.m2/repository/org/openjfx/$module" -name "*linux*.jar" 2>/dev/null | head -1)
        fi
        if [ -z "$JAR_PATH" ]; then
            JAR_PATH=$(find "$HOME/.m2/repository/org/openjfx/$module" -name "*win*.jar" 2>/dev/null | head -1)
        fi
        if [ -n "$JAR_PATH" ]; then
            if [ -z "$JAVAFX_PATH" ]; then
                JAVAFX_PATH="$JAR_PATH"
            else
                JAVAFX_PATH="$JAVAFX_PATH:$JAR_PATH"
            fi
        fi
    done
    
    if [ -n "$JAVAFX_PATH" ]; then
        java --module-path "$JAVAFX_PATH" \
             --add-modules javafx.controls,javafx.graphics,javafx.base,javafx.media \
             -jar target/CanvasImageGrid-1.0-SNAPSHOT.jar
    else
        echo "Error: JavaFX not found in Maven repository"
        echo "Please run 'mvn dependency:copy-dependencies' first"
        exit 1
    fi
else
    echo "Error: JavaFX not found"
    echo "Please install JavaFX or run 'mvn dependency:copy-dependencies' first"
    echo ""
    echo "Alternatively, you can download JavaFX from: https://openjfx.io/"
    exit 1
fi
