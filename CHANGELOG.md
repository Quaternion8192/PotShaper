# Changelog

All notable changes to PotShaper will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-03-12

### Added
- Initial release of PotShaper library
- Core presentation management with `PotPresentation` class
  - Create new presentations with default or custom page sizes
  - Open existing presentations from files or streams
  - Save presentations to files or output streams
  - Slide management (add, insert, clone, remove, move)
  - Master slide and layout access
  - Memory usage statistics and warnings
  
- Slide element support
  - Text boxes (`PotTextBox`)
  - Shapes (`PotShape`) with various shape types
  - Images (`PotImage`)
  - Videos (`PotVideo`)
  - Audio (`PotAudio`)
  - Tables (`PotTable`)
  - Charts (`PotChart`)
  - Groups (`PotGroup`)
  
- Styling and formatting
  - Borders (`PotBorder`)
  - Colors (`PotColor`)
  - Fonts (`PotFont`)
  - Gradients (`PotGradient`)
  - Shadows (`PotShadow`)
  - Fill patterns (`PotFill`)
  
- Animations and transitions
  - Slide animations (`PotAnimation`)
  - Slide transitions (`PotTransition`)
  - Animation helper utilities
  - Transition helper utilities
  
- Interactive elements
  - Hyperlinks (`PotHyperlink`)
  - Actions (`PotAction`)
  - Link types support
  
- Utilities and helpers
  - JSON exporter for presentation metadata
  - XML manipulation utilities
  - Unit converter for measurements
  - Validation utilities
  - UUID manager for element identification
  
- Configuration options
  - Page size presets (`PotPageSize`)
  - Save options with backup and buffering support
  - Media options for audio/video
  
- Error handling
  - Custom exception type (`PotException`)
  - Comprehensive validation and error messages
  
- Logging
  - Built-in logger (`PotLogger`)
  
- Rendering
  - Slide rendering to BufferedImage
  - Configurable scale factors

### Changed
- None (initial release)

### Deprecated
- None (initial release)

### Removed
- None (initial release)

### Fixed
- None (initial release)

### Security
- None (initial release)

---

## Migration Guide

### From Apache POI directly to PotShaper

PotShaper provides a fluent API that simplifies PowerPoint manipulation:

**Before (Apache POI):**
```java
XMLSlideShow ppt = new XMLSlideShow();
XSLFSlide slide = ppt.createSlide();
XSLFTextBox textBox = slide.createTextBox();
textBox.setText("Hello World");
```

**After (PotShaper):**
```java
PotPresentation ppt = PotPresentation.create();
PotSlide slide = ppt.addSlide();
slide.addTextBox("Hello World");
```

---

## Known Issues

- None reported in initial release

---

## Credits

**Author:** Quaternion8192  
**Email:** mail@quaternion.cc  
**GitHub:** https://github.com/quaternion8192/potshaper

Special thanks to:
- Apache POI team for the excellent PowerPoint library
- All contributors and early adopters

---

*For more information, visit the project documentation at:*
https://github.com/quaternion8192/potshaper
