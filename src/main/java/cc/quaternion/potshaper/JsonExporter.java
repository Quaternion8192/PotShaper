/*
 * Copyright 2026 Quaternion8192
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.quaternion.potshaper;

import java.util.*;

/**
 * Exports a presentation and its components to JSON format.
 *
 * <p>This utility class provides static methods to serialize a {@link PotPresentation} object
 * and its constituent slides, elements, and metadata into a structured JSON string.
 * Two export modes are available: a basic summary and a detailed version including
 * master slides and layout information. The JSON output is designed for interoperability,
 * data exchange, or debugging purposes.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * PotPresentation presentation = ...;
 * String jsonSummary = JsonExporter.export(presentation);
 * String jsonDetailed = JsonExporter.exportDetailed(presentation);
 * }</pre>
 *
 * <h3>Basic JSON Structure</h3>
 * <pre>
 * {
 *   "slideCount": 3,
 *   "pageWidth": 960,
 *   "pageHeight": 540,
 *   "slides": [
 *     {
 *       "index": 0,
 *       "uuid": "POT:00000001",
 *       "elementCount": 2,
 *       "elements": [
 *         {
 *           "type": "textbox",
 *           "uuid": "POT:00000002",
 *           "text": "Hello",
 *           "x": 100, "y": 100, "width": 400, "height": 50
 *         }
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class JsonExporter {

    // ==================== Constructor ====================

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This is a utility class with only static methods; it should not be instantiated.</p>
     */
    private JsonExporter() {
        // Utility class
    }

    // ==================== Public API ====================

    /**
     * Exports a presentation to a basic JSON summary.
     *
     * <p>Serializes the core properties of the presentation, including slide count, page dimensions,
     * and a list of slides. Each slide includes its index, UUID, hidden status, notes (if present),
     * and a list of its graphical elements. Element serialization is type-specific, adding relevant
     * fields such as text content for text boxes or shape type for shapes.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotPresentation ppt = getPresentation();
     * String json = JsonExporter.export(ppt);
     * System.out.println(json);
     * }</pre>
     *
     * @param ppt the presentation to export; may be {@code null}
     * @return a valid JSON string representing the presentation; returns "{}" if {@code ppt} is {@code null}
     */
    static String export(PotPresentation ppt) {
        if (ppt == null) {
            return "{}";
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n");

        // Presentation metadata
        json.append("  \"slideCount\": ").append(ppt.getSlideCount()).append(",\n");
        json.append("  \"pageWidth\": ").append((int) ppt.getPageWidth()).append(",\n");
        json.append("  \"pageHeight\": ").append((int) ppt.getPageHeight()).append(",\n");

        // Slides array
        json.append("  \"slides\": [\n");
        List<PotSlide> slides = ppt.getSlides();
        for (int i = 0; i < slides.size(); i++) {
            PotSlide slide = slides.get(i);
            json.append(exportSlide(slide, i));
            if (i < slides.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ]\n");

        json.append("}");
        return json.toString();
    }

    /**
     * Serializes a single slide to its JSON representation.
     *
     * <p>Builds a JSON object containing the slide's index, UUID, hidden flag, optional notes,
     * element count, and an array of its elements. This method is called internally by
     * {@link #export(PotPresentation)}.</p>
     *
     * @param slide the slide to serialize
     * @param index the positional index of the slide within the presentation
     * @return a JSON object string for the slide
     */
    private static String exportSlide(PotSlide slide, int index) {
        StringBuilder json = new StringBuilder();
        json.append("    {\n");
        json.append("      \"index\": ").append(index).append(",\n");
        json.append("      \"uuid\": \"").append(escapeJson(slide.getUUID())).append("\",\n");
        json.append("      \"hidden\": ").append(slide.isHidden()).append(",\n");

        // Optional notes
        String notes = slide.getNotes();
        if (notes != null && !notes.isEmpty()) {
            json.append("      \"notes\": \"").append(escapeJson(notes)).append("\",\n");
        }

        // Elements array
        List<PotElement> elements = slide.getElements();
        json.append("      \"elementCount\": ").append(elements.size()).append(",\n");
        json.append("      \"elements\": [\n");
        
        for (int i = 0; i < elements.size(); i++) {
            PotElement element = elements.get(i);
            json.append(exportElement(element));
            if (i < elements.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("      ]\n");
        json.append("    }");
        
        return json.toString();
    }

    /**
     * Serializes a presentation element to its JSON representation.
     *
     * <p>Creates a JSON object with common properties (type, UUID, position, dimensions, rotation)
     * and adds type-specific fields. Supported element types include text boxes, shapes, images,
     * tables, groups, connectors, and unknown elements.</p>
     *
     * @param element the graphical element to serialize
     * @return a JSON object string for the element
     */
    private static String exportElement(PotElement element) {
        StringBuilder json = new StringBuilder();
        json.append("        {\n");

        // Common properties
        String type = getElementType(element);
        json.append("          \"type\": \"").append(type).append("\",\n");
        json.append("          \"uuid\": \"").append(escapeJson(element.getUUID())).append("\",\n");

        // Geometry
        json.append("          \"x\": ").append((int) element.getX()).append(",\n");
        json.append("          \"y\": ").append((int) element.getY()).append(",\n");
        json.append("          \"width\": ").append((int) element.getWidth()).append(",\n");
        json.append("          \"height\": ").append((int) element.getHeight()).append(",\n");
        json.append("          \"rotation\": ").append(element.getRotation());

        // Type-specific properties
        if (element instanceof PotTextBox) {
            PotTextBox textBox = (PotTextBox) element;
            String text = textBox.getText();
            if (text != null) {
                json.append(",\n");
                json.append("          \"text\": \"").append(escapeJson(text)).append("\"");
            }
        } else if (element instanceof PotShape) {
            PotShape shape = (PotShape) element;
            json.append(",\n");
            json.append("          \"shapeType\": \"").append(shape.getShapeType()).append("\"");
            String text = shape.getText();
            if (text != null && !text.isEmpty()) {
                json.append(",\n");
                json.append("          \"text\": \"").append(escapeJson(text)).append("\"");
            }
        } else if (element instanceof PotImage) {
            PotImage image = (PotImage) element;
            json.append(",\n");
            json.append("          \"imageType\": \"").append(image.getPictureType()).append("\",\n");
            json.append("          \"originalWidth\": ").append(image.getOriginalWidth()).append(",\n");
            json.append("          \"originalHeight\": ").append(image.getOriginalHeight());
        } else if (element instanceof PotTable) {
            PotTable table = (PotTable) element;
            json.append(",\n");
            json.append("          \"rows\": ").append(table.getRowCount()).append(",\n");
            json.append("          \"cols\": ").append(table.getColumnCount());
        } else if (element instanceof PotGroup) {
            PotGroup group = (PotGroup) element;
            json.append(",\n");
            json.append("          \"childCount\": ").append(group.getElementCount());
        } else if (element instanceof PotConnector) {
            PotConnector connector = (PotConnector) element;
            json.append(",\n");
            json.append("          \"connectorType\": \"").append(connector.getConnectorType()).append("\",\n");
            json.append("          \"startX\": ").append((int) connector.getStartX()).append(",\n");
            json.append("          \"startY\": ").append((int) connector.getStartY()).append(",\n");
            json.append("          \"endX\": ").append((int) connector.getEndX()).append(",\n");
            json.append("          \"endY\": ").append((int) connector.getEndY());
        }

        json.append("\n        }");
        return json.toString();
    }

    /**
     * Determines the JSON type string for a given element instance.
     *
     * <p>Maps the concrete class of the element to a simple lowercase type identifier
     * used in the JSON output.</p>
     *
     * @param element the element to classify
     * @return the type string, such as "textbox", "shape", "image", "table", "group",
     *         "connector", "unknown", or the fallback "element"
     */
    private static String getElementType(PotElement element) {
        if (element instanceof PotTextBox) {
            return "textbox";
        } else if (element instanceof PotShape) {
            return "shape";
        } else if (element instanceof PotImage) {
            return "image";
        } else if (element instanceof PotTable) {
            return "table";
        } else if (element instanceof PotGroup) {
            return "group";
        } else if (element instanceof PotConnector) {
            return "connector";
        } else if (element instanceof PotUnknownElement) {
            return "unknown";
        } else {
            return "element";
        }
    }

    /**
     * Escapes a string for safe inclusion in a JSON value.
     *
     * <p>Performs standard JSON escaping: quotes, backslashes, and control characters
     * are replaced with their escape sequences. Characters below 0x20 are encoded as
     * Unicode escape sequences (e.g., \u0000).</p>
     *
     * @param s the input string; may be {@code null}
     * @return the escaped string, or an empty string if {@code s} is {@code null}
     */
    private static String escapeJson(String s) {
        if (s == null) return "";
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    if (c < 32) {
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
                    break;
            }
        }
        return result.toString();
    }

    // ==================== Detailed Export ====================

    /**
     * Exports a presentation to a detailed JSON structure including master slides and layout information.
     *
     * <p>Provides an extended serialization that includes the presentation's master slides,
     * each slide's associated layout, and transition effects (if any). This output is more
     * comprehensive than {@link #export(PotPresentation)} and is suitable for applications
     * requiring full template and structural metadata.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotPresentation ppt = getPresentation();
     * String detailedJson = JsonExporter.exportDetailed(ppt);
     * saveToFile(detailedJson);
     * }</pre>
     *
     * @param ppt the presentation to export; may be {@code null}
     * @return a detailed JSON string representing the presentation; returns "{}" if {@code ppt} is {@code null}
     * @see #export(PotPresentation)
     */
    static String exportDetailed(PotPresentation ppt) {
        if (ppt == null) {
            return "{}";
        }

        StringBuilder json = new StringBuilder();
        json.append("{\n");

        // Presentation metadata
        json.append("  \"slideCount\": ").append(ppt.getSlideCount()).append(",\n");
        json.append("  \"pageWidth\": ").append((int) ppt.getPageWidth()).append(",\n");
        json.append("  \"pageHeight\": ").append((int) ppt.getPageHeight()).append(",\n");

        // Masters array
        json.append("  \"masters\": [\n");
        List<PotMaster> masters = ppt.getMasters();
        for (int i = 0; i < masters.size(); i++) {
            PotMaster master = masters.get(i);
            json.append("    {\n");
            json.append("      \"name\": \"").append(escapeJson(master.getName())).append("\",\n");
            json.append("      \"layoutCount\": ").append(master.getLayoutCount()).append("\n");
            json.append("    }");
            if (i < masters.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ],\n");

        // Slides array (detailed)
        json.append("  \"slides\": [\n");
        List<PotSlide> slides = ppt.getSlides();
        for (int i = 0; i < slides.size(); i++) {
            PotSlide slide = slides.get(i);
            json.append(exportSlideDetailed(slide, i));
            if (i < slides.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("  ]\n");

        json.append("}");
        return json.toString();
    }

    /**
     * Serializes a single slide to a detailed JSON representation including layout and transition.
     *
     * <p>Extends the basic slide serialization by adding optional "layout" and "transition"
     * objects. This method is called internally by {@link #exportDetailed(PotPresentation)}.</p>
     *
     * @param slide the slide to serialize
     * @param index the positional index of the slide within the presentation
     * @return a detailed JSON object string for the slide
     */
    private static String exportSlideDetailed(PotSlide slide, int index) {
        StringBuilder json = new StringBuilder();
        json.append("    {\n");
        json.append("      \"index\": ").append(index).append(",\n");
        json.append("      \"uuid\": \"").append(escapeJson(slide.getUUID())).append("\",\n");
        json.append("      \"hidden\": ").append(slide.isHidden()).append(",\n");

        // Optional layout
        PotLayout layout = slide.getLayout();
        if (layout != null) {
            json.append("      \"layout\": \"").append(escapeJson(layout.getName())).append("\",\n");
        }

        // Optional transition
        PotTransition transition = slide.getTransition();
        if (transition != null) {
            json.append("      \"transition\": {\n");
            json.append("        \"type\": \"").append(transition.getType()).append("\",\n");
            json.append("        \"duration\": ").append(transition.getDuration()).append("\n");
            json.append("      },\n");
        }

        // Elements array
        List<PotElement> elements = slide.getElements();
        json.append("      \"elementCount\": ").append(elements.size()).append(",\n");
        json.append("      \"elements\": [\n");
        
        for (int i = 0; i < elements.size(); i++) {
            PotElement element = elements.get(i);
            json.append(exportElement(element));
            if (i < elements.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("      ]\n");
        json.append("    }");
        
        return json.toString();
    }
}