# PotShaper API Concepts

## Introduction

This document describes the core concepts and design patterns used throughout the PotShaper API. Understanding these concepts is essential for effective use of the library.

## Core Concepts

### 1. Presentation-Slide-Element Hierarchy

PotShaper follows a hierarchical model that mirrors PowerPoint's structure:

```
PotPresentation (Container)
    └─ PotSlide (Canvas)
        └─ PotElement (Visual Content)
```

**Key Relationships**:
- A `PotPresentation` contains multiple `PotSlide` instances
- Each `PotSlide` contains multiple `PotElement` instances
- Elements cannot exist independently outside a slide
- Slides cannot exist independently outside a presentation

### 2. Fluent Builder Pattern

All element operations return the element itself, enabling method chaining:

```java
// Basic chaining
slide.addTextBox("Title")
    .at(100, 100)
    .size(500, 80)
    .setFontSize(36)
    .setBold(true);

// Advanced chaining with effects
slide.addShape(PotShapeType.RECTANGLE)
    .position(50, 50)
    .size(200, 150)
    .setFillColor(PotColor.BLUE)
    .shadow(new PotShadow(PotShadow.Type.OUTER, PotColor.BLACK, 0.7, 10.0, 45.0))
    .rotate(15)
    .animate(PotAnimation.bounceIn());
```

**Design Rationale**:
- Reduces boilerplate code
- Improves readability by grouping related operations
- Enables inline configuration without intermediate variables

### 3. UUID-based Identity System

Every slide and element has a unique identifier:

```java
PotSlide slide = ppt.addSlide();
String slideId = slide.getUUID();  // e.g., "POT:00000001"

PotTextBox textBox = slide.addTextBox("Hello");
String elementId = textBox.getUUID();  // e.g., "POT:00000002"

// Retrieve by UUID later
PotElement element = ppt.findElement(elementId);
```

**Benefits**:
- Stable references across operations
- Safe serialization to JSON
- Easy integration with external systems
- No conflicts from duplicate elements

### 4. Element Creation Patterns

#### Factory Methods on PotSlide

Each element type has dedicated factory methods:

```java
// Text box
PotTextBox tb = slide.addTextBox("Initial text");
PotTextBox tb2 = slide.addTextBox("Custom", 100, 100, 400, 50);

// Shape
PotShape rect = slide.addShape(PotShapeType.RECTANGLE);
PotShape circle = slide.addShape(PotShapeType.ELLIPSE, 200, 200, 100, 100);

// Image
PotImage img = slide.addImage("/path/to/image.png");
PotImage img2 = slide.addImage(imageBytes, "image/png");

// Table
PotTable table = slide.addTable(5, 3);  // 5 rows, 3 columns
PotTable table2 = slide.addTable(5, 3, 100, 100, 500, 300);

// Connector
PotConnector conn = slide.addConnector(PotConnector.ConnectorType.ELBOW);
PotConnector conn2 = slide.addConnector(fromElement, toElement);

// Media
PotAudio audio = slide.addAudio(audioBytes, "audio/mp3");
PotVideo video = slide.addVideo(videoBytes, "video/mp4");

// Chart
PotChart chart = slide.addChart("BAR_CHART");
PotChart chart2 = slide.addChart("LINE_CHART", 100, 100, 600, 400);
```

#### Default Values

When not specified, sensible defaults are applied:

| Element | Default Position | Default Size |
|---------|-----------------|--------------|
| TextBox | (100, 100) | 400×50 |
| Shape | (100, 100) | 200×200 |
| Image | POI-determined | Original image size |
| Table | (100, 100) | 400×200 |
| Chart | (100, 100) | 500×350 |
| Audio | (50, 50) | 50×50 (icon) |
| Video | (100, 100) | 400×300 |

### 5. Coordinate System

PotShaper uses PowerPoint's coordinate system:

- **Origin**: Top-left corner of the slide
- **X-axis**: Increases from left to right
- **Y-axis**: Increases from top to bottom
- **Units**: Points (1 point = 1/72 inch)

```java
// Position at top-left
element.at(0, 0);

// Position 2 inches from left, 1 inch from top
element.at(144, 72);  // 2*72, 1*72

// Get current position
double x = element.getX();
double y = element.getY();
```

### 6. Z-Order Management

Elements have a stacking order (Z-order):

```java
// Bring to front (topmost)
element.bringToFront();

// Send to back (bottommost)
element.sendToBack();

// Move one level forward
element.bringForward();

// Move one level backward
element.sendBackward();
```

**Rendering Order**:
- Elements drawn in cache order (first = bottom, last = top)
- Overlapping elements follow Z-order
- Grouped elements maintain relative Z-order within group

### 7. Element Search and Filtering

Multiple strategies to retrieve elements:

```java
// By UUID
PotElement elem = slide.getElement("POT:00000005");

// By type
List<PotShape> shapes = slide.getElements(PotShape.class);
List<PotTextBox> texts = slide.findElementsByType(PotTextBox.class);

// By name
List<PotElement> logos = slide.findElementsByName("Company Logo");

// By predicate
List<PotElement> redElements = slide.findElements(e -> 
    e instanceof PotShape && 
    PotColor.RED.equals(((PotShape)e).getFillColor())
);

// Count elements
int count = slide.getElementCount();

// All elements
List<PotElement> all = slide.getElements();
```

### 8. Property Categories

Element properties fall into several categories:

#### Geometric Properties
```java
// Position
element.at(x, y);
element.move(dx, dy);
element.setX(100);

// Size
element.size(width, height);
element.scale(1.5);        // Uniform scaling
element.scale(2.0, 0.5);   // Non-uniform

// Rotation
element.rotate(45);        // Set absolute angle
element.rotateBy(15);      // Relative rotation
element.flipHorizontal();
element.flipVertical();
```

#### Visual Properties
```java
// Fill
shape.setFillColor(PotColor.BLUE);
shape.setFillGradient(gradient);
shape.setFillPattern(patternType, fgColor, bgColor);

// Border
shape.setBorder(new PotBorder(PotColor.RED, 2.0));

// Shadow
shape.shadow(new PotShadow(PotShadow.Type.OUTER, PotColor.BLACK, 0.8, 10.0, 45.0));
shape.removeShadow();

// Effects
shape.reflection(new PotEffect.Reflection(0.5, 10.0));
shape.glow(new PotEffect.Glow(PotColor.YELLOW, 15.0));
shape.softEdge(new PotEffect.SoftEdge(5.0));

// 3D
shape.rotation3D(rotation3D);
shape.bevel(bevel);
shape.material(material);
shape.lighting(lighting);

// Opacity
element.opacity(0.7);  // 70% opaque
```

#### Text Properties (PotTextBox, PotShape)
```java
textBox.setText("New content");
textBox.setFont(new PotFont("Arial", 24, true, false));
textBox.setFontSize(36);
textBox.setBold(true);
textBox.setItalic(true);
textBox.setTextColor(PotColor.WHITE);
textBox.setAlignment(PotAlignment.CENTER);
textBox.setVerticalAlignment(PotVerticalAlignment.MIDDLE);
```

### 9. Animation System

Animations are applied using the builder pattern:

```java
// Simple animation
element.animate(PotAnimation.fadeIn());

// Complex animation with multiple effects
PotAnimation anim = new PotAnimation()
    .effect(PotAnimation.Effect.FLY_IN)
    .trigger(PotAnimation.Trigger.ON_CLICK)
    .direction(PotAnimation.Direction.FROM_BOTTOM)
    .duration(2.0)
    .delay(0.5);

element.animate(anim);

// Remove animation
element.removeAnimation();

// Check if animated
if (element.hasAnimation()) {
    PotAnimation current = element.getAnimation();
}
```

**Animation Categories**:
- **Entrance**: fade_in, fly_in, bounce_in, zoom_in, etc.
- **Emphasis**: pulse, spin, grow_shrink, color_pulse, etc.
- **Exit**: fade_out, fly_out, bounce_out, zoom_out, etc.

### 10. Hyperlink System

Elements can have hyperlinks:

```java
// External URL
element.hyperlink("https://example.com");

// Internal slide link
element.hyperlinkToSlide(2);  // Link to slide index 2

// Using PotHyperlink object
PotHyperlink link = new PotHyperlink(PotLinkType.URL, "https://example.com");
element.hyperlink(link);

// Remove hyperlink
element.removeHyperlink();

// Get hyperlink URL
String url = element.getHyperlinkUrl();
```

### 11. Action Triggers

Interactive actions respond to user events:

```java
// Mouse click action
PotAction clickAction = new PotAction(PotActionType.MOUSE_CLICK)
    .hyperlink("https://example.com")
    .sound("/path/to/sound.wav");

element.action(clickAction);

// Mouse hover action
PotAction hoverAction = new PotAction(PotActionType.MOUSE_OVER)
    .highlight(true);

element.action(hoverAction);

// Remove action
element.removeAction(true);   // Remove hover action
element.removeAction(false);  // Remove click action
```

### 12. Grouping

Group multiple elements to manipulate them as a unit:

```java
// Create elements
PotShape rect = slide.addShape(PotShapeType.RECTANGLE, 50, 50, 100, 100);
PotTextBox text = slide.addTextBox("Label", 60, 60, 80, 30);

// Group them
List<PotElement> elements = Arrays.asList(rect, text);
PotGroup group = slide.group(elements);

// Manipulate group
group.setPosition(200, 200);  // Moves both elements together
group.scale(1.5);              // Scales both elements

// Access group members
List<PotElement> children = group.getElements();
```

**Important Notes**:
- Original elements are removed from slide's element list
- Elements can only belong to one group
- Group coordinates are relative to group origin
- Nesting groups is supported but may have limitations

### 13. Slide Operations

#### Slide Management
```java
// Add slides
PotSlide slide1 = ppt.addSlide();
PotSlide slide2 = ppt.insertSlide(1);  // Insert at index 1
PotSlide slide3 = ppt.cloneSlide(0);   // Duplicate first slide

// Remove slide
ppt.removeSlide(2);

// Reorder slides
ppt.moveSlide(3, 0);  // Move slide from index 3 to index 0

// Get slides
PotSlide slide = ppt.getSlide(0);
List<PotSlide> allSlides = ppt.getSlides();
int count = ppt.getSlideCount();
PotSlide byUuid = ppt.findSlide("POT:00000001");
```

#### Slide Properties
```java
// Background
slide.setBackground(PotColor.BLUE);
slide.setBackground("/path/to/image.png");
slide.setBackground(gradient);

// Layout
PotLayout layout = slide.getLayout();
slide.setLayout(specificLayout);

// Notes
slide.setNotes("Presenter notes here");
String notes = slide.getNotes();

// Hidden state
slide.setHidden(true);
boolean hidden = slide.isHidden();

// Transition
slide.setTransition(new PotTransition(PotTransitionType.FADE, 2.0));
```

### 14. Document Properties

Set presentation metadata:

```java
ppt.setTitle("Quarterly Report");
ppt.setAuthor("John Doe");
ppt.setSubject("Q4 2025 Results");

String title = ppt.getTitle();
String author = ppt.getAuthor();
String subject = ppt.getSubject();
```

### 15. Save Options

Advanced save configuration:

```java
SaveOptions options = new SaveOptions()
    .createBackup(true)           // Create versioned backup
    .bufferSize(8192)             // 8KB buffer
    .optimizeForWeb(true);        // Web optimization

ppt.save("output.pptx", options);

// Backup file created: output_2026-03-12_14-30-00.pptx.bak
```

### 16. Memory Management

Monitor and manage memory usage:

```java
// Get statistics
MemoryStats stats = ppt.getMemoryStats();
System.out.println("Estimated memory: " + stats.getEstimatedMemoryMB() + " MB");
System.out.println("Slides: " + stats.getSlideCount());
System.out.println("Shapes: " + stats.getShapeCount());
System.out.println("Media: " + stats.getMediaCount());

// Set warning threshold
ppt.setLargeFileWarning(500, stats -> {
    System.out.println("Warning: Large presentation detected!");
    System.out.println("Consider splitting into smaller presentations.");
});
```

### 17. Resource Cleanup

Always close presentations properly:

```java
// Try-with-resources (recommended)
try (PotPresentation ppt = PotPresentation.create()) {
    // Work with presentation
    ppt.save("output.pptx");
} // Automatically closed

// Explicit close
PotPresentation ppt = PotPresentation.open("input.pptx");
try {
    // Work with presentation
} finally {
    ppt.close();
}
```

**Consequences of Not Closing**:
- File handles remain open
- Memory not released
- Potential file corruption if application crashes

### 18. Error Handling

Use structured exception handling:

```java
try {
    PotPresentation ppt = PotPresentation.open("file.pptx");
    // ... operations
} catch (PotException e) {
    // Handle specific error types
    switch (e.getErrorCode()) {
        case FILE_NOT_FOUND:
            logger.error("File does not exist: " + e.getContext("path"));
            break;
        case IO_ERROR:
            logger.error("I/O operation failed", e.getCause());
            break;
        case SLIDE_INDEX_OUT_OF_BOUNDS:
            logger.error("Invalid slide index: " + e.getContext("index"));
            break;
        case INVALID_PARAMETER:
            logger.error("Invalid parameter: " + e.getContext("paramName"));
            break;
        default:
            logger.error("Unexpected error", e);
    }
}
```

### 19. Low-Level Access

Access underlying POI objects when needed:

```java
// Get raw POI objects
XMLSlideShow poiSlideShow = ppt.getRawSlideShow();
XSLFSlide poiSlide = slide.getRawSlide();
XSLFShape poiShape = element.getRawShape();

// Use POI APIs directly
poiShape.setShapeName("Custom Name");

// Access XML structure
XmlObject xml = poiShape.getXmlObject();
String xmlString = xml.toString();
```

**Warning**: Direct POI manipulation may bypass PotShaper's internal state management. Use with caution.

### 20. JSON Export

Export presentation structure for analysis or interchange:

```java
// Basic export
String json = ppt.exportJsonIndex();
/*
{
  "slideCount": 3,
  "pageWidth": 960,
  "pageHeight": 540,
  "slides": [
    {
      "index": 0,
      "uuid": "POT:00000001",
      "elementCount": 5,
      "elements": [...]
    }
  ]
}
*/

// Detailed export (includes masters, layouts, transitions)
String detailedJson = JsonExporter.exportDetailed(ppt);
```

## Common Patterns

### Pattern 1: Template-Based Creation

```java
PotPresentation ppt = PotPresentation.create();
PotSlide master = ppt.getSlide(0);

// Apply consistent styling
master.setBackground(PotColor.LIGHT_GRAY);
master.setTransition(new PotTransition(PotTransitionType.FADE, 1.0));

// Clone for each section
for (Section section : sections) {
    PotSlide slide = ppt.cloneSlide(0);
    slide.addTextBox(section.getTitle())
        .at(100, 50)
        .setFontSize(32)
        .setBold(true);
}
```

### Pattern 2: Batch Element Creation

```java
List<PotShape> shapes = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    shapes.add(slide.addShape(PotShapeType.CIRCLE)
        .at(i * 50, 100)
        .size(40, 40)
        .setFillColor(PotColor.PALETTE[i]));
}

// Group all shapes
PotGroup group = slide.group(shapes);
```

### Pattern 3: Conditional Styling

```java
for (PotElement element : slide.getElements()) {
    if (element instanceof PotShape) {
        PotShape shape = (PotShape) element;
        if (shape.getWidth() > 200) {
            shape.setFillColor(PotColor.RED);
        } else {
            shape.setFillColor(PotColor.GREEN);
        }
    }
}
```

### Pattern 4: Multi-Slide Consistency

```java
Consumer<PotSlide> applyConsistentStyle = s -> {
    s.setBackground(PotColor.WHITE);
    s.getElements(PotTextBox.class).forEach(tb -> {
        tb.setFont(new PotFont("Arial", 18, false, false));
    });
};

for (PotSlide slide : ppt.getSlides()) {
    applyConsistentStyle.accept(slide);
}
```

### Pattern 5: Incremental Building

```java
PotPresentation ppt = PotPresentation.create();

// Build slide 1
PotSlide titleSlide = ppt.getSlide(0);
titleSlide.addTextBox("Presentation Title")
    .at(100, 200)
    .size(600, 80)
    .setFontSize(48)
    .setBold(true);

// Build slide 2
PotSlide contentSlide = ppt.addSlide();
contentSlide.addTextBox("Agenda")
    .at(100, 50)
    .setFontSize(36);

// Continue building...
ppt.save("presentation.pptx");
ppt.close();
```

## Best Practices

### DO:
- Use try-with-resources for automatic cleanup
- Validate input parameters before expensive operations
- Use UUIDs for stable element references
- Group related elements for easier manipulation
- Apply animations sparingly for professional results
- Monitor memory usage for large presentations

### DON'T:
- Share `PotPresentation` instances across threads
- Modify elements after removing them from slides
- Forget to close presentations (resource leak)
- Mix direct POI manipulation with PotShaper APIs unnecessarily
- Assume elements persist after presentation is closed

---

*Last Updated: March 12, 2026*  
*Version: 1.0.0*
