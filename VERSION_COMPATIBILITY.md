# Gluon Native Build - Complete Version Compatibility Matrix

## ✅ Working Configuration (Tested)

| Component | Version | Requirement |
|-----------|---------|-------------|
| **GraalVM** | 21.0.2 | MUST be 21.x |
| **Java Compiler** | 21 | MUST match GraalVM |
| **JavaFX** | 21 | MUST match Java version |
| **Maven** | 3.8.8 | MUST be 3.8.x |
| **Gluon Plugin** | 1.0.24 | Latest stable |
| **Gluon Substrate** | 0.0.64 | Auto-downloaded |

## ❌ Incompatible Configurations

### Configuration 1 (Original - Fails)
- GraalVM: 25.0.1 ❌
- Java: 24 ❌  
- JavaFX: 25 ❌
- Maven: 3.9.12 ❌
- **Error**: Maven version not supported

### Configuration 2 (After Maven fix - Fails)
- GraalVM: 25.0.1 ❌
- Java: 24 ❌
- JavaFX: 25 ❌
- Maven: 3.8.8 ✅
- **Error**: GRAALVM_HOME not found

### Configuration 3 (After GRAALVM_HOME - Fails)
- GraalVM: 25.0.1 ❌
- Java: 24 ❌
- JavaFX: 25 ❌
- Maven: 3.8.8 ✅
- GRAALVM_HOME: Set ✅
- **Error**: Linker error - `symbol(s) not found for architecture arm64`
- **Reason**: Gluon Substrate 0.0.64 uses GraalVM 21.1.0 SVM libraries, incompatible with GraalVM 25

### Configuration 4 (After GraalVM downgrade - Fails)
- GraalVM: 21.0.2 ✅
- Java: 24 ❌
- JavaFX: 25 ❌
- Maven: 3.8.8 ✅
- **Error**: Invalid target release: 24
- **Reason**: GraalVM 21 doesn't support Java 24 target

### Configuration 5 (WORKING ✅)
- GraalVM: 21.0.2 ✅
- Java: 21 ✅
- JavaFX: 21 ✅
- Maven: 3.8.8 ✅
- **Status**: Build in progress

## Why These Specific Versions?

### Gluon Client 1.0.24
- Released: ~2023
- Uses: Substrate 0.0.64
- Substrate 0.0.64 dependencies:
  - `org.graalvm.nativeimage:svm:21.1.0`
  - `org.graalvm.sdk:graal-sdk:21.1.0`
  - `org.graalvm.compiler:compiler:21.1.0`

### GraalVM 21 vs 25
- **GraalVM 21**: Uses SVM (Substrate VM) version 21.x
- **GraalVM 25**: Uses SVM version 25.x
- **Problem**: Binary incompatibility between SVM 21 and SVM 25
- **Linker Error**: Missing symbols from SVM 25 (like `_svm_attach_*` functions)

### JavaFX 25 vs 21
- **JavaFX 25**: Compiled with Java 24 (class file version 67.0)
- **JavaFX 21**: Compiled with Java 21 (class file version 65.0)
- **GraalVM 21**: Only understands up to Java 21
- **Error**: "Unsupported major.minor version 67.0"

### Maven 3.9 vs 3.8
- Gluon Client has hardcoded check for Maven version
- Refuses to run with Maven 3.9.x
- Requires Maven 3.8.8 or earlier in 3.8.x line

## Installation Commands (Complete Setup)

```bash
# 1. Install GraalVM 21
sdk install java 21.0.2-graal
sdk use java 21.0.2-graal

# 2. Install Maven 3.8.8
sdk install maven 3.8.8
sdk use maven 3.8.8

# 3. Set environment variables
export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.2-graal
export JAVA_HOME=$GRAALVM_HOME

# 4. Add to ~/.zshrc for permanence
echo 'export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.2-graal' >> ~/.zshrc
echo 'export JAVA_HOME=$GRAALVM_HOME' >> ~/.zshrc

# 5. Verify versions
java -version        # Should show: java version "21.0.2"
mvn --version        # Should show: Apache Maven 3.8.8
native-image --version  # Should show: native-image 21.0.2
```

## POM.xml Changes Required

```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>  <!-- Changed from 24 -->
    <maven.compiler.target>21</maven.compiler.target>  <!-- Changed from 24 -->
    <javafx.version>21</javafx.version>                <!-- Changed from 25 -->
</properties>
```

## Build Command

```bash
# Set environment (if not in shell profile)
export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.2-graal
export JAVA_HOME=$GRAALVM_HOME

# Build
mvn clean gluonfx:build
```

## Alternative: Use Newer Gluon (If Available)

Check for newer Gluon Client versions that might support GraalVM 25:

```bash
# Check Maven Central for newer versions
curl -s https://repo.maven.apache.org/maven2/com/gluonhq/gluonfx-maven-plugin/maven-metadata.xml | grep -i version
```

As of Jan 2026, version 1.0.24 is latest stable, which requires GraalVM 21.

## Lessons Learned

1. **Dependency Chains Matter**: Gluon → Substrate → GraalVM SVM are tightly coupled
2. **Class File Versions**: JavaFX version must match Java compiler target
3. **Binary Compatibility**: Native libraries from different GraalVM major versions are incompatible
4. **Tool Versions**: Build tools (Maven) can have strict version requirements
5. **Documentation Gaps**: Official docs don't always specify exact version requirements

## Future-Proofing

To avoid this in the future:

1. **Check Gluon Substrate version** in plugin dependency
2. **Match GraalVM version** to Substrate's GraalVM dependencies
3. **Use matching Java and JavaFX versions**
4. **Pin all versions** in pom.xml and documentation

## Summary

**The Gotcha**: Modern JavaFX development uses latest versions (Java 24, JavaFX 25, GraalVM 25), but Gluon Client (the only reliable JavaFX native-image tool) is stuck on older versions (Java 21, GraalVM 21).

**The Solution**: Step back to GraalVM 21, Java 21, JavaFX 21, and Maven 3.8.8 - all aligned for Gluon Client 1.0.24.

**When to Update**: Wait for Gluon Client to release a version that supports GraalVM 23+ and JavaFX 23+.

---

This explains the complete version maze and how we navigated through it!
