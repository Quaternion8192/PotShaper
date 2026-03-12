# PotShaper Architecture

## Overview

PotShaper is a fluent Java wrapper library for Apache POI that provides an intuitive API for manipulating PowerPoint presentations. The architecture is designed to abstract the complexity of the underlying POI library while maintaining full functionality and performance.

## Design Principles

### 1. Fluent Interface Pattern
All element manipulation methods return `this` for method chaining:
```java
slide.addTextBox("Hello")
    .at(100, 100)
    .size(400, 50)
    .rotate(45)
    .animate(PotAnimation.fadeIn());
```

### 2. UUID-based Element Identification
Every slide and element has a unique identifier managed by `UuidManager`, enabling:
- Reliable element tracking across operations
- Safe serialization/deserialization
- External system integration via JSON export

### 3. Encapsulation of Apache POI
The library wraps POI objects (`XMLSlideShow`, `XSLFSlide`, `XSLFShape`) without exposing their complexity:
- `PotPresentation` wraps `XMLSlideShow`
- `PotSlide` wraps `XSLFSlide`
- `PotElement` subclasses wrap various `XSLFShape` implementations

### 4. Layered Architecture

```
┌─────────────────────────────────────────┐
│         User Application Code           │
├─────────────────────────────────────────┤
│ PotPresentation / PotSlide / PotElement │  ← Public API Layer
├─────────────────────────────────────────┤
│    Helper Classes (AnimationHelper,     │  ← Business Logic Layer
│         EffectHelper, etc.)             │
├─────────────────────────────────────────┤
│      XmlUtils / ValidationUtils         │  ← Utility Layer
├─────────────────────────────────────────┤
│        Apache POI (XMLSlideShow,        │  ← Foundation Layer
│             XSLFSlide, etc.)            │
└─────────────────────────────────────────┘
```

## Core Components

### PotPresentation

**Responsibility**: Central entry point for presentation manipulation.

**Key Features**:
- Lifecycle management (create/open/save/close)
- Slide registry with UUID tracking
- Master slides and layouts access
- Memory statistics and warnings
- Document properties (title, author, subject)
- JSON export capability

**Internal State**:
- `XMLSlideShow slideShow` - Underlying POI object
- `UuidManager uuidManager` - UUID generator
- `Map<String, PotSlide> slideRegistry` - Slide lookup by UUID
- `List<PotSlide> slideCache` - Ordered slide list

### PotSlide

**Responsibility**: Represents a single slide and manages its elements.

**Key Features**:
- Element creation (text boxes, shapes, images, tables, media, charts)
- Element retrieval and filtering (by type, name, UUID, predicate)
- Grouping support with XML manipulation
- Background styling (solid color, image, gradient)
- Notes and transitions
- Layout assignment

**Element Cache**:
- Maintains `List<PotElement> elementCache` for Z-order preservation
- All elements are registered with presentation-level registry
- Supports dynamic addition/removal during slide manipulation

### PotElement Hierarchy

```
IPotElement (interface)
    ↑
PotElement (abstract base)
    ├─ PotTextBox
    ├─ PotShape
    ├─ PotImage
    ├─ PotTable
    ├─ PotConnector
    ├─ PotGroup
    ├─ PotChart
    ├─ PotAudio
    └─ PotVideo
```

**Common Properties** (from `PotElement`):
- Position (x, y) and size (width, height)
- Rotation and flipping
- Z-order control (bringToFront, sendToBack)
- Animation attachment
- Hyperlink support
- Shadow and visual effects
- 3D formatting
- Opacity control

### Helper Classes

#### AnimationHelper
Manages slide animations using Apache POI's animation APIs:
- Apply entrance, emphasis, exit animations
- Configure timing, triggers, and effects
- Remove existing animations

#### EffectHelper
Handles visual effects:
- Reflection, glow, soft edges
- 3D rotation, bevel, material, lighting
- Effect removal and cleanup

#### ActionHelper
Implements interactive actions:
- Mouse click and hover triggers
- Hyperlink navigation
- Sound playback
- Custom action handlers

#### TransitionHelper
Manages slide transitions:
- Transition types (fade, push, wipe, etc.)
- Duration and speed control
- Auto-advance timing

#### MediaHelper
Handles audio/video embedding:
- Media placeholder creation
- Binary data attachment
- Icon rendering for media elements

#### JsonExporter
Serializes presentations to JSON:
- Basic export (slides, elements, properties)
- Detailed export (masters, layouts, transitions)
- Type-specific element serialization

### Utility Classes

#### XmlUtils
Low-level XML manipulation utilities:
- XPath queries on XML structures
- XML node insertion/deletion
- Shape Z-order reordering
- Shadow, opacity, gradient, pattern fill application
- Hyperlink removal via XML manipulation

**Key Operations**:
```java
XmlUtils.applyShadow(shape, shadow);
XmlUtils.setOpacity(shape, alpha);
XmlUtils.bringToFront(slide, shape);
XmlUtils.removeHyperlink(shape);
```

#### ValidationUtils
Parameter validation with descriptive exceptions:
- Null/empty checks (`notNull`, `notBlank`, `notEmpty`)
- Numeric validation (`positive`, `finite`, `inRange`, `percentage01`)
- Index validation (`validIndex`, `validInsertIndex`)
- Angle normalization (`normalizeAngle`)

#### UnitConverter
Unit conversion between measurement systems:
- Points to EMU (English Metric Units)
- Percentage to OpenXML scale (×100000)
- Fraction to EMU percent

#### UuidManager
Generates and manages unique identifiers:
- Sequential UUID allocation per presentation
- Format: `POT:00000001`, `POT:00000002`, etc.

## Data Flow

### Creating a Presentation

```
User Code
    ↓
PotPresentation.create()
    ↓
new XMLSlideShow()                    [POI initialization]
    ↓
initializeCache()                     [Wrap existing slides]
    ↓
addSlide() → createAndRegisterSlide() [UUID allocation + registration]
    ↓
PotSlide returned to user
```

### Adding an Element

```
User calls: slide.addTextBox(text, x, y, width, height)
    ↓
Create XSLFTextBox via POI
    ↓
Set anchor rectangle (position + size)
    ↓
presentation.allocateUUID()           [Generate UUID]
    ↓
new PotTextBox(poiTextBox, slide, uuid)
    ↓
elementCache.add(textBox)             [Add to slide cache]
    ↓
presentation.registerElement(uuid, textBox) [Register globally]
    ↓
Return PotTextBox for fluent chaining
```

### Saving a Presentation

```
User calls: ppt.save("output.pptx")
    ↓
ensureNotClosed()                     [Validate state]
    ↓
save(file, options)                   [Apply SaveOptions if provided]
    ↓
createVersionedBackup()               [If backup enabled]
    ↓
slideShow.write(bos)                  [POI writes to BufferedOutputStream]
    ↓
Flush and close stream
```

### Element Grouping

```
User calls: slide.group(elements)
    ↓
calculateBounds(elements)             [Compute union of all bounds]
    ↓
createGroup()                         [XSLFGroupShape via POI]
    ↓
setAnchor(bounds)                     [Set group position/size]
    ↓
moveElementsIntoGroup(groupShape, elements, bounds)
    ├─ For each element:
    │   ├─ Get XML node
    │   ├─ Adjust coordinates relative to group origin (convert to EMU)
    │   ├─ Import node into group XML tree
    │   └─ Remove from slide XML tree
    └─ Rollback on error
    ↓
Remove original elements from cache
    ↓
Return PotGroup
```

## Memory Management

### Estimation Formula
```java
estimatedMemoryMB = MEMORY_BASE_MB 
                  + (slideCount / MEMORY_SLIDES_DIVISOR)
                  + (shapeCount / MEMORY_SHAPES_DIVISOR)
                  + (mediaCount * MEMORY_PER_MEDIA_MB);
```

Constants:
- `MEMORY_BASE_MB = 2`
- `MEMORY_SLIDES_DIVISOR = 2`
- `MEMORY_SHAPES_DIVISOR = 20`
- `MEMORY_PER_MEDIA_MB = 5`

### Warning System
```java
ppt.setLargeFileWarning(500, stats -> {
    logger.warn("Large presentation: " + stats.getEstimatedMemoryMB() + " MB");
});
```

Triggers when estimated memory exceeds threshold.

## Error Handling

### PotException Types

| Exception Type | Description |
|---------------|-------------|
| `fileNotFound` | File does not exist or cannot be accessed |
| `ioError` | I/O operation failed |
| `slideIndexOutOfBounds` | Invalid slide index |
| `invalidParameter` | Method parameter validation failed |
| `unsupportedOperation` | Operation not supported for element type |
| `xmlManipulationError` | Low-level XML manipulation failed |

### Validation Chain
All public methods validate parameters using `ValidationUtils`:
```java
public PotShape addShape(PotShapeType shapeType, double x, double y, 
                         double width, double height) {
    ValidationUtils.notNull(shapeType, "shapeType");
    ValidationUtils.finite(x, "x");
    ValidationUtils.finite(y, "y");
    ValidationUtils.positive(width, "width");
    ValidationUtils.positive(height, "height");
    // ... implementation
}
```

## Thread Safety

**Current Implementation**: Not thread-safe.

- `PotPresentation` instances should not be shared across threads
- Each thread should create and manage its own presentation instance
- Internal collections (`HashMap`, `ArrayList`) are not synchronized

**Recommended Usage**:
```java
// Per-thread instance
ThreadLocal<PotPresentation> pptHolder = ThreadLocal.withInitial(() -> 
    PotPresentation.create()
);
```

## Extension Points

### Custom Element Types
Extend `PotElement` and override:
- `duplicate()` - Provide duplication logic
- Type-specific property accessors

### Custom Animations
Use `PotAnimation` builder pattern:
```java
PotAnimation custom = new PotAnimation()
    .effect(PotAnimation.Effect.FADE)
    .trigger(PotAnimation.Trigger.ON_CLICK)
    .duration(2.0)
    .delay(0.5);
```

### Custom Transitions
Extend `PotTransition` with new transition types via `PotTransitionType` enum.

## Performance Considerations

### Slide Caching
- Slides are wrapped once during initialization
- Subsequent access uses cached `PotSlide` instances
- No repeated POI object wrapping overhead

### Element Lookup
- O(1) UUID-based lookup via `elementRegistry`
- O(n) type filtering via streams (acceptable for typical slide sizes)

### XML Manipulation
- Direct XML operations bypass POI's higher-level APIs for performance
- Used for Z-order, shadows, opacity, hyperlinks
- Includes rollback mechanisms for error recovery

### Resource Cleanup
- Always use try-with-resources or explicit `close()` calls
- `PotPresentation.close()` releases POI resources and clears registries
- Prevents memory leaks in long-running applications

## Compatibility

### Java Version
- Requires JDK 17+
- Uses `var` inference and modern Java features

### Apache POI Version
- Tested with POI 5.4.0
- Depends on:
  - `poi`
  - `poi-ooxml`
  - `poi-ooxml-lite`
  - `poi-ooxml-full`
  - `xmlbeans`

### File Format Support
- **Supported**: Office Open XML (.pptx)
- **Not Supported**: Binary PowerPoint format (.ppt)

## Future Architecture Considerations

### Potential Improvements
1. **Async Operations**: Background saving/loading for large presentations
2. **Template Engine**: Predefined slide templates with placeholder substitution
3. **Batch Operations**: Transactional element updates with rollback
4. **Plugin System**: Extensible effect/animation providers
5. **Streaming API**: Memory-efficient processing for very large presentations

### Known Limitations
- Group nesting depth limited by POI's XML structure handling
- Chart customization depends on POI's chart API completeness
- Some advanced PPTX features may require direct POI access

---

*Last Updated: March 12, 2026*  
*Version: 1.0.1*
