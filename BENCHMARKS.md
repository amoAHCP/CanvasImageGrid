# JMH Performance-Benchmarks für CanvasImageGrid

## ✅ Implementierungsstatus

Die JMH-Benchmark-Infrastruktur wurde erfolgreich implementiert:

### Erstellte Komponenten

1. **Maven-Konfiguration** (`pom.xml`)
   - JMH-Core 1.37 dependency
   - JMH-Annotation-Processor 1.37 dependency
   - JavaFX-Swing für JFXPanel-Initialisierung

2. **Module-Konfiguration** (`module-info.java`)
   - `javafx.swing` Module-Requirement hinzugefügt

3. **Benchmark-Klassen** (`src/test/java/org/jacpfx/image/benchmark/`)
   - `BenchmarkBase.java` - Basis-Konfiguration mit File-Output
   - `ImageMetadataBenchmark.java` - PNG/JPEG/GIF Header-Parsing-Performance
   - `ImageContainerBenchmark.java` - Image-Loading, Caching, Drawing-Performance
   - `CanvasPanelLayoutBenchmark.java` - Batch-Container-Erstellung (10/50/100 Bilder)
   - `ImageFactoryBenchmark.java` - DefaultFactory vs. SquareFactory Vergleich
   - `AllBenchmarksRunner.java` - Master-Runner für alle Benchmarks
   - `README.md` - Umfassende Dokumentation

## 🚀 Benchmarks ausführen

### Methode 1: Mit Maven Exec Plugin (Empfohlen)

```bash
# Alle Benchmarks ausführen
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.AllBenchmarksRunner"
```

### Methode 2: Einzelne Benchmarks

```bash
# ImageMetadata Parsing
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.ImageMetadataBenchmark"

# ImageContainer Operations
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.ImageContainerBenchmark"

# Layout Benchmarks
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.CanvasPanelLayoutBenchmark"

# Factory Comparison
mvn clean test-compile exec:java -Dexec.classpathScope=test \
  -Dexec.mainClass="org.jacpfx.image.benchmark.ImageFactoryBenchmark"
```

### Methode 3: Mit Shell-Skript

**Hinweis**: Aktuell funktioniert das Shell-Skript nicht, da JMH einen Annotation-Processor benötigt.
Verwenden Sie stattdessen Maven Exec Plugin (Methode 1 oder 2).

## 📊 Ergebnisse analysieren

### Ausgabe-Dateien

Alle Benchmark-Ergebnisse werden in `target/benchmark-results/` gespeichert:

```
target/benchmark-results/
├── all_benchmarks_YYYYMMDD_HHMMSS.json  # Detaillierte JSON-Ergebnisse
├── summary_YYYYMMDD_HHMMSS.txt          # Lesbarer Summary-Report
└── [einzelne Benchmark-JSON-Dateien]     # Bei einzelner Ausführung
```

### JSON-Struktur

```json
{
  "benchmark": "org.jacpfx.image.benchmark.ImageMetadataBenchmark.parsePngMetadata",
  "mode": "avgt",
  "primaryMetric": {
    "score": 1.234,        // Durchschnittszeit in ms
    "scoreError": 0.056,   // Konfidenzintervall (±)
    "scoreUnit": "ms/op"
  }
}
```

### Wichtige Metriken

1. **Score (Durchschnittszeit)** - Niedriger = besser → Hauptbottleneck-Indikator
2. **Score Error** - Niedriger = konsistentere Performance
3. **Percentiles** - Verteilung der Messwerte (p50, p90, p99)

## 🎯 Benchmark-Kategorien

### 1. ImageMetadata Parsing
**Datei**: `ImageMetadataBenchmark.java`

**Tests**:
- `parsePngMetadata` - PNG-Header-Parsing
- `parseJpgMetadata` - JPEG-Header-Parsing  
- `parseMultipleFormats` - Batch-Verarbeitung gemischter Formate

**Erwartete Performance**: < 1ms pro Bild

### 2. ImageContainer Operations
**Datei**: `ImageContainerBenchmark.java`

**Tests**:
- `imageContainerCreation` - Container-Instantiierung
- `imageLoadingWithDefaultFactory` - Standard-Image-Loading
- `imageLoadingWithSquareFactory` - Square-Crop-Loading
- `repeatedDrawingSameImage` - Cache-Effektivität

**Erwartete Performance**:
- Container-Erstellung: < 1ms
- Erstes Laden: 5-20ms
- Cached Drawing: < 2ms

### 3. Layout/Container-Batch-Operationen
**Datei**: `CanvasPanelLayoutBenchmark.java`

**Tests**:
- `batchImageContainerCreationSmallSet` - 10 Bilder
- `batchImageContainerCreationMediumSet` - 50 Bilder
- `batchImageContainerCreationLargeSet` - 100 Bilder
- `parallelImageContainerCreation` - Parallel vs. Sequential

**Erwartete Performance**:
- Small (10): < 10ms
- Medium (50): < 50ms
- Large (100): < 100ms

### 4. ImageFactory Comparison
**Datei**: `ImageFactoryBenchmark.java`

**Tests**:
- `defaultFactoryImageCreation` - Standard-Factory
- `squareFactoryImageCreation` - Square-Factory
- `defaultFactoryWithPostProcessing` - Mit Post-Processing
- `squareFactoryWithPostProcessing` - Pixel-Level-Crop

**Erwartete Performance**:
- DefaultFactory: 5-15ms
- SquareFactory (kein Post-Process): ähnlich
- SquareFactory (mit Post-Process): 10-30ms

## 🔍 Bottleneck-Analyse

Nach Ausführung der Benchmarks:

1. **JSON-Datei öffnen** in `target/benchmark-results/`

2. **Höchste Scores (Average Time) finden** → Primäre Bottlenecks

3. **Hohe Score-Error-Werte** → Inkonsistente Performance (mögliche GC-Probleme)

4. **Erwartete Bottlenecks** (laut Dokumentation):
   - SquareImageFactory Post-Processing (Pixel-Operationen)
   - Große Bildmengen (100+) Layout-Berechnung
   - SoftReference-Cache-Thrashing

## ⚙️ Benchmark-Konfiguration

### Standardeinstellungen (BenchmarkBase.java)

```java
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xms2g", "-Xmx4g"})
```

### JVM-Optionen

```bash
-Xms2g -Xmx4g              # Heap-Size
-XX:+UseG1GC               # G1 Garbage Collector
-XX:+ParallelRefProcEnabled # Parallel Reference Processing
--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
```

## 📝 Anmerkungen

### Aktuelle Einschränkungen

1. **CanvasPanel Builder ist package-private** 
   - Direkte CanvasPanel-Layout-Benchmarks nicht möglich
   - Stattdessen: Batch-ImageContainer-Erstellung als Proxy

2. **Test-Bilder begrenzt**
   - Nur 3 PNG-Bilder in `src/test/resources/images/`
   - Benchmarks duplizieren Bilder für größere Sets

3. **JavaFX-Threading**
   - Alle UI-Operationen auf JavaFX Application Thread
   - JFXPanel initialisiert JavaFX-Toolkit

### Troubleshooting

**Problem**: "ERROR: Unable to find /META-INF/BenchmarkList"
**Lösung**: JMH Annotation-Processor wurde nicht ausgeführt. 
→ Verwende `mvn exec:java` statt direkten `java`-Aufruf

**Problem**: JavaFX-Toolkit-Initialisierungsfehler
**Lösung**: Stelle sicher, dass `--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED` gesetzt ist

**Problem**: Out of Memory
**Lösung**: Erhöhe Heap-Size: `-Xms4g -Xmx8g`

## 🎓 Nächste Schritte

1. **Benchmarks ausführen**:
   ```bash
   mvn clean test-compile exec:java -Dexec.classpathScope=test \
     -Dexec.mainClass="org.jacpfx.image.benchmark.AllBenchmarksRunner"
   ```

2. **Ergebnisse reviewen**:
   - JSON-Datei öffnen
   - Summary-Report lesen
   - Top 3 Bottlenecks identifizieren

3. **Optimierungen priorisieren**:
   - Basierend auf gemessenen Bottlenecks
   - Dokumentierte Empfehlungen in `summary_*.txt` folgen

4. **Re-Benchmarking**:
   - Nach Optimierungen erneut ausführen
   - Performance-Verbesserungen messen
   - Dokumentieren in Projekt-Docs

## 📚 Weitere Dokumentation

- Detaillierte Benchmark-Dokumentation: `src/test/java/org/jacpfx/image/benchmark/README.md`
- JMH-Dokumentation: https://github.com/openjdk/jmh
- JavaFX-Performance-Guide: https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/perfGuide.html
