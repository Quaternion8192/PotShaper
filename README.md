# 🎨 PotShaper — Reshaping PowerPoint Automation

**PotShaper** is a high-level, fluent Java library that wraps Apache POI. It transforms the chaotic, XML-heavy experience of PowerPoint automation into a Michelin-star developer experience.

[![Java CI](https://img.shields.io/github/actions/workflow/status/quaternion8192/potshaper/build.yml?branch=main&logo=github&style=flat-square)](https://github.com/quaternion8192/potshaper/actions) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/cc.quaternion/potshaper.svg?style=flat-square)](https://central.sonatype.com/) [![Java Version](https://img.shields.io/badge/Java-17+-green.svg?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.java.net/) [![Code Size](https://img.shields.io/github/languages/code-size/quaternion8192/potshaper?style=flat-square)](https://github.com/quaternion8192/potshaper) [![Stars](https://img.shields.io/github/stars/quaternion8192/potshaper?style=social)](https://github.com/quaternion8192/potshaper)

---

## 🎯 The Mission

> Working with native Apache POI is like trying to eat a boiling **Hot Pot (火锅)** with a pair of toothpicks. It's powerful, but messy, verbose, and you'll likely get burned by low-level XML boilerplate.
> **PotShaper** is the "Shaper." It provides a **Fluent API** that lets you craft presentations with the intuitive ease of a modern UI framework.

```java
// Native POI: A maze of XML and boilerplate
XSLFSlide slide = ppt.createSlide();
XSLFTextBox tb = slide.createTextBox();
tb.setText("Hello Chaos");
tb.setAnchor(new Rectangle2D.Double(50, 50, 300, 50));

// PotShaper: Fluent, readable, and elegant
slide.addTextBox("Hello Elegance")
    .at(50, 50)
    .size(300, 50)
    .font(PotFont.of("Calibri", 24).bold().color(PotColor.BLUE))
    .animate(PotAnimation.fadeIn());

```

---

## 🔥 Why PotShaper?

### The "Hot Pot" Problem 🥢

Native OOXML manipulation is riddled with:

* ❌ **XML Swamp**: Getting lost in `CTPresetShapeProperties` instead of focusing on logic.
* ❌ **Type-Safety Nightmares**: String magic values everywhere.
* ❌ **Verbose Boilerplate**: 20 lines of code for a simple rounded rectangle.
* ❌ **Z-Order Chaos**: Managing element layers by trial and error.

### The PotShaper Solution 🍴

* ✅ **Fluent API**: Chainable methods that read like a story.
* ✅ **Compile-Time Safety**: Strong types for colors, fonts, and effects.
* ✅ **Zero XML**: Complete abstraction of underlying complexity.
* ✅ **Motion Engine**: Simplified Animations and Transitions.
* ✅ **Resource Safe**: Built-in memory monitoring and AutoCloseable support.

---

## 🚀 Key Features

| Category | Capability | Example API |
| --- | --- | --- |
| **📝 Text** | Full control | `.setFontSize()`, `.setAlignment()`, `.setBold()` |
| **🔷 Shapes** | 100+ Preset Types | `addShape(PotShapeType.HEART)`, `.fill(PotColor.RED)` |
| **📊 Tables** | High-level API | `addTable(rows, cols)`, `.cell(0,0).setText()` |
| **🎬 Motion** | 30+ Effects | `.animate(PotAnimation.bounceIn())`, `.flyIn()` |
| **🎞️ Transition** | Slide Effects | `slide.setTransition(PotTransition.push())` |
| **🧊 3D Effects** | Advanced Visuals | `.rotation3D()`, `.bevel()`, `.material()` |
| **💾 Interop** | Serialization | `JsonExporter.export(ppt)` |
| **📊 DevOps** | Reliability | `getMemoryStats()`, `90%+ Test Coverage` |

---

## 💡 "Aha!" Moment: See it in Action

Creating a professional slide with animations and interactions:

```java
try (PotPresentation ppt = PotPresentation.create(PotPageSize.WIDESCREEN_16_9)) {
    PotSlide slide = ppt.getSlide(0);
    
    // 1. Add Header with Fade-In
    slide.addTextBox("Q4 Revenue Report")
        .at(50, 50)
        .font(PotFont.of("Arial", 36).bold().color(PotColor.DARK_BLUE))
        .animate(PotAnimation.fadeIn().duration(800));
    
    // 2. Add a Gradient Shape with a Click Action
    slide.addShape(PotShapeType.ROUNDED_RECTANGLE)
        .at(100, 150).size(200, 100)
        .fill(PotFill.gradient(PotColor.BLUE, PotColor.PURPLE))
        .action(PotAction.goToNextSlide().onClick());
    
    // 3. Setup Slide Transition
    slide.setTransition(PotTransition.wipe(PotDirection.FROM_TOP));
    
    ppt.save("business_pitch.pptx");
}

```

---

## 🏆 Engineering Excellence

### CI/CD Pipeline 🏭

We don't just ship code; we ship quality.

* **Automated Testing**: Every push triggers a full test suite on Ubuntu/Windows.
* **Auto-Docs**: Javadoc is automatically deployed to GitHub Pages on every release.
* **Static Analysis**: Enforced coding standards via `.editorconfig`.

### Robust Testing 📊

With **90%+ test coverage**, PotShaper is built for production:

* `PotValidationTest`: Parameter boundary checks.
* `MemoryStatsTest`: Ensuring efficient resource usage.
* `ComponentCoverageTest`: Verifying every shape and effect.

---

## ⚡ Quick Start

### Installation (Maven) 

```xml
<dependency>
    <groupId>cc.quaternion</groupId>
    <artifactId>potshaper</artifactId>
    <version>1.0.1</version>
</dependency>

```

### Simple Example

```java
try (PotPresentation ppt = PotPresentation.create()) {
    ppt.getSlide(0).addTextBox("Hello PotShaper")
       .at(100, 100)
       .font(PotFont.of("Verdana", 24));
    ppt.save("hello.pptx");
}

```

---

## 📖 Documentation & Resources

* **[Usage Guide](https://www.google.com/search?q=docs/architecture.md)** - Deep dive into architecture and design.
* **[Examples](https://www.google.com/search?q=docs/example/)** - From basic shapes to complex motions.
* **[API Reference (Javadoc)](https://quaternion8192.github.io/potshaper/)** - Full technical documentation.

---

## 📄 License

Licensed under the **Apache License 2.0**. See [LICENSE](https://www.google.com/search?q=LICENSE) for details.

```text
Copyright © 2026 Quaternion
```

---

<div align="center">

**Made with ❤️ by Quaternion**

*Reshaping PowerPoint Automation, One Fluent API at a Time.*

> PS. We are looking for long-term contributors to shape the future of PPT automation!

</div>

