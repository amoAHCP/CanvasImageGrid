# CanvasImageGrid - Agent Guidelines

## Build, Lint, and Test Commands
- **Build**: `mvn clean package`
- **Run Tests**: `mvn test` or `mvn surefire:test`
- **Run Single Test**: `mvn surefire:test -Dtest=CanvasPanelTest`
- **Run with JavaFX**: `mvn javafx:run`
- **Native Build**: `mvn clean package -Pnative`

## Code Style Guidelines

### Imports
- Group imports in order: Java, JavaFX, third-party, project-specific
- Use wildcard imports only for test classes

### Formatting
- 4 spaces for indentation (no tabs)
- No trailing whitespace
- Line length: 120 characters maximum
- One statement per line

### Types and Naming Conventions
- Use `double` for all floating-point values (no `float`)
- Use `camelCase` for variables and methods
- Use `PascalCase` for classes and interfaces
- Constants in `UPPER_SNAKE_CASE`
- Method names should be verbs (e.g., `calculateWidth()`)
- Avoid abbreviations in variable names

### Error Handling
- Wrap image loading in try-catch blocks for graceful fallback
- Log errors with meaningful messages to console
- Handle `IOException` when reading image metadata
- Use `SoftReference` for memory-aware caching

### Performance Considerations
- Avoid creating new objects in hot paths (e.g., rendering loops)
- Use `AtomicLong` and `LongAdder` for thread-safe counters
- Minimize property change notifications to reduce layout thrashing
- Filter micro-scrolls with epsilon values (e.g., `SCROLL_EPSILON = 0.9`)
- Use stream operations judiciously - consider indexed loops for performance-critical code

### Documentation
- Javadoc on all public classes and methods
- Include performance implications in API documentation
- Document memory management strategies in class comments