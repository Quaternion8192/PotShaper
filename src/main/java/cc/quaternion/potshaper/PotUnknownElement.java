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

import org.apache.poi.xslf.usermodel.XSLFShape;

/**
 * Represents a PowerPoint slide element whose specific type is not recognized by the PotShaper library.
 *
 * <p>This class encapsulates an Apache POI {@code XSLFShape} that does not correspond to any of the
 * known {@code PotElement} subclasses (such as {@code PotTextBox} or {@code PotPicture}). It provides
 * basic geometric manipulation and type introspection, but advanced operations like duplication are
 * unsupported. Use this class to handle shapes from newer PowerPoint versions or custom extensions
 * that are not yet directly modeled.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * for (PotElement element : slide.getElements()) {
 *     if (element instanceof PotUnknownElement) {
 *         PotUnknownElement unknown = (PotUnknownElement) element;
 *         System.out.println("Unknown element type: " + unknown.getTypeName());
 *     }
 * }
 * }</pre>
 *
 * <p>Key capabilities include:</p>
 * <ul>
 *     <li>Positioning and resizing via fluent setters inherited from {@code PotElement}.</li>
 *     <li>Identifying the underlying POI shape class name.</li>
 *     <li>Conditional text access if the shape is actually a text shape.</li>
 *     <li>Direct access to the raw POI {@code XSLFShape} via {@code getRawShape()}.</li>
 * </ul>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotUnknownElement extends PotElement {

    // ==================== Constructor ====================

    /**
     * Constructs a new unknown element wrapper.
     *
     * @param shape       the underlying Apache POI shape object
     * @param parentSlide the parent slide containing this element
     * @param uuid        the unique identifier for this element
     */
    PotUnknownElement(XSLFShape shape, PotSlide parentSlide, String uuid) {
        super(shape, parentSlide, uuid);
    }

    // ==================== Fluent Geometry Setters ====================

    @Override
    public PotUnknownElement at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotUnknownElement setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotUnknownElement x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotUnknownElement setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotUnknownElement y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotUnknownElement position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotUnknownElement size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotUnknownElement setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotUnknownElement width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotUnknownElement setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotUnknownElement height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotUnknownElement rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotUnknownElement rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    // ==================== Type Introspection ====================

    /**
     * Returns the simple class name of the underlying POI shape.
     *
     * @return the simple class name (e.g., "XSLFGraphicFrame")
     */
    public String getTypeName() {
        return shape.getClass().getSimpleName();
    }

    /**
     * Returns the fully qualified class name of the underlying POI shape.
     *
     * @return the fully qualified class name (e.g., "org.apache.poi.xslf.usermodel.XSLFGraphicFrame")
     */
    public String getFullTypeName() {
        return shape.getClass().getName();
    }

    /**
     * Checks whether the underlying shape's class name matches the given string.
     *
     * <p>The comparison is performed against both the simple name and the fully qualified name.</p>
     *
     * @param className the class name to compare against (may be simple or fully qualified)
     * @return {@code true} if the shape's simple or fully qualified class name equals {@code className},
     *         {@code false} otherwise or if {@code className} is {@code null}
     */
    public boolean isType(String className) {
        if (className == null) return false;
        return getTypeName().equals(className) ||
               getFullTypeName().equals(className);
    }

    /**
     * Determines if the underlying POI shape is an instance of the specified class.
     *
     * @param clazz the class to test against
     * @return {@code true} if the underlying shape is an instance of {@code clazz},
     *         {@code false} otherwise or if {@code clazz} is {@code null}
     */
    public boolean isInstanceOf(Class<?> clazz) {
        return clazz != null && clazz.isInstance(shape);
    }

    // ==================== Conditional Text Access ====================

    /**
     * Attempts to retrieve text content if the underlying shape is a text shape.
     *
     * <p>This method safely checks whether the wrapped {@code XSLFShape} is actually an
     * {@code XSLFTextShape}. If so, it returns the shape's text; otherwise it returns {@code null}.</p>
     *
     * @return the text content if the shape is a text shape, {@code null} otherwise
     * @see org.apache.poi.xslf.usermodel.XSLFTextShape#getText()
     */
    public String tryGetText() {
        if (shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
            return ((org.apache.poi.xslf.usermodel.XSLFTextShape) shape).getText();
        }
        return null;
    }

    /**
     * Attempts to set text content if the underlying shape is a text shape.
     *
     * <p>This method safely checks whether the wrapped {@code XSLFShape} is actually an
     * {@code XSLFTextShape}. If so, it sets the shape's text and returns {@code true};
     * otherwise it returns {@code false} and does nothing.</p>
     *
     * @param text the new text to set
     * @return {@code true} if the text was successfully set, {@code false} if the shape is not a text shape
     * @see org.apache.poi.xslf.usermodel.XSLFTextShape#setText(String)
     */
    public boolean trySetText(String text) {
        if (shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
            ((org.apache.poi.xslf.usermodel.XSLFTextShape) shape).setText(text);
            return true;
        }
        return false;
    }

    // ==================== Unsupported Operations ====================

    /**
     * Duplication is not supported for unknown element types.
     *
     * @throws PotException always, with an "unsupported operation" message
     * @see PotElement#duplicate()
     */
    @Override
    public PotElement duplicate() {
        throw PotException.unsupportedOperation("duplicate for PotUnknownElement");
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        return String.format("PotUnknownElement{uuid=%s, type=%s, position=(%.0f,%.0f), size=(%.0fx%.0f)}",
            uuid, getTypeName(), getX(), getY(), getWidth(), getHeight());
    }
}