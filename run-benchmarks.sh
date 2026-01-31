#!/bin/bash
#
# Run JMH benchmarks for CanvasImageGrid
# This script compiles and runs all JMH benchmarks, saving results to target/benchmark-results/
#

echo "=========================================="
echo "CanvasImageGrid JMH Benchmarks"
echo "=========================================="
echo ""

# Compile the project
echo "Step 1: Compiling main classes..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "ERROR: Main compilation failed"
    exit 1
fi

echo ""
echo "Step 2: Compiling benchmark classes..."
mkdir -p target/test-classes

# Get classpath
CP=$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout):target/classes

# Compile benchmarks
javac -cp "$CP" \
      -d target/test-classes \
      --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
      src/test/java/org/jacpfx/image/benchmark/*.java

if [ $? -ne 0 ]; then
    echo "ERROR: Benchmark compilation failed"
    exit 1
fi

echo ""
echo "Step 3: Running benchmarks..."
echo "This may take several minutes..."
echo ""

# Run benchmarks
java -cp "$CP:target/test-classes" \
     --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
     -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:+ParallelRefProcEnabled \
     org.jacpfx.image.benchmark.AllBenchmarksRunner

echo ""
echo "=========================================="
echo "Benchmarks Complete!"
echo "=========================================="
echo ""
echo "Results saved to: target/benchmark-results/"
ls -lh target/benchmark-results/
