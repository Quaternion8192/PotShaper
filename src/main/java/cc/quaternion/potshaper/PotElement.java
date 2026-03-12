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

import org.apache.poi.xslf.usermodel.*;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

/**
 * The abstract base class for all visual elements within a presentation slide.
 * <p>This class provides a common interface for manipulating the position, size, rotation, animation,
 * and other visual properties of slide elements such as shapes, text boxes, images, and tables.
 * It wraps an underlying Apache POI {@link XSLFShape} and manages its lifecycle within a {@link PotSlide}.
 * Concrete subclasses implement type-specific behavior.</p>
 *
 * <h3>Usage Example: Accessing and Casting</h3>
 * <pre>{@code
 * PotElement element = slide.getElements().get(0);
 * if (element instanceof PotTextBox) {
 *     PotTextBox textBox = (PotTextBox) element;
 *     textBox.setText("Hello");
 * }
 * }</pre>
 *
 * <h3>Usage Example: Fluent Builder Pattern</h3>
 * <pre>{@code
 * slide.addTextBox("Hello")
 *     .at(100, 100)
 *     .size(400, 50)
 *     .rotate(45)
 *     .animate(PotAnimation.fadeIn());
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public abstract class PotElement implements IPotElement {

    // ==================== Fields ====================

    /** The underlying Apache POI shape object managed by this element. */
    protected final XSLFShape shape;

    /** The parent slide that contains this element. */
    protected final PotSlide parentSlide;

    /** The unique identifier for this element. */
    protected final String uuid;

    // ==================== Constructors ====================

    /**
     * Constructs a new PotElement with the specified underlying shape, parent slide, and unique identifier.
     *
     * @param shape        the Apache POI shape object to wrap; must not be null
     * @param parentSlide  the slide that contains this element; must not be null
     * @param uuid         the unique identifier for this element
     * @throws NullPointerException if {@code shape} or {@code parentSlide} is null
     */
    protected PotElement(XSLFShape shape, PotSlide parentSlide, String uuid) {
        this.shape = ValidationUtils.notNull(shape, "shape");
        this.parentSlide = ValidationUtils.notNull(parentSlide, "parentSlide");
        this.uuid = uuid;
    }

    // ==================== Basic Properties ====================

    /**
     * Returns the unique identifier of this element.
     *
     * @return the element's UUID
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Returns the parent slide containing this element.
     *
     * @return the parent PotSlide
     */
    public PotSlide getSlide() {
        return parentSlide;
    }

    /**
     * Returns the X-coordinate of the element's top-left corner, in points.
     *
     * @return the X-coordinate
     */
    public double getX() {
        ensurePresentationOpen();
        Rectangle2D anchor = shape.getAnchor();
        return anchor != null ? anchor.getX() : 0;
    }

    /**
     * Returns the Y-coordinate of the element's top-left corner, in points.
     *
     * @return the Y-coordinate
     */
    public double getY() {
        ensurePresentationOpen();
        Rectangle2D anchor = shape.getAnchor();
        return anchor != null ? anchor.getY() : 0;
    }

    /**
     * Returns the width of the element, in points.
     *
     * @return the width
     */
    public double getWidth() {
        ensurePresentationOpen();
        Rectangle2D anchor = shape.getAnchor();
        return anchor != null ? anchor.getWidth() : 0;
    }

    /**
     * Returns the height of the element, in points.
     *
     * @return the height
     */
    public double getHeight() {
        ensurePresentationOpen();
        Rectangle2D anchor = shape.getAnchor();
        return anchor != null ? anchor.getHeight() : 0;
    }

    /**
     * Returns the rotation angle of the element, in degrees.
     * <p>Rotation is only applicable to simple shapes; other shape types return 0.</p>
     *
     * @return the rotation angle in degrees
     */
    public double getRotation() {
        ensurePresentationOpen();
        if (shape instanceof XSLFSimpleShape) {
            return ((XSLFSimpleShape) shape).getRotation();
        }
        return 0;
    }

    /**
     * Returns the name of the underlying shape.
     *
     * @return the shape name
     */
    public String getName() {
        ensurePresentationOpen();
        return shape.getShapeName();
    }

    // ==================== Position Manipulation ====================

    /**
     * Sets the position of the element to the specified coordinates.
     *
     * @param x the new X-coordinate, in points
     * @param y the new Y-coordinate, in points
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code x} or {@code y} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T at(double x, double y) {
        ensurePresentationOpen();
        ValidationUtils.finite(x, "x");
        ValidationUtils.finite(y, "y");
        Rectangle2D anchor = shape.getAnchor();
        if (anchor == null) {
            anchor = new Rectangle2D.Double(x, y, 100, 100);
        } else {
            anchor = new Rectangle2D.Double(x, y, anchor.getWidth(), anchor.getHeight());
        }
        if (shape instanceof XSLFSimpleShape) {
            ((XSLFSimpleShape) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFGroupShape) {
            ((XSLFGroupShape) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFGraphicFrame) {
            ((XSLFGraphicFrame) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFTable) {
            ((XSLFTable) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFConnectorShape) {
            ((XSLFConnectorShape) shape).setAnchor(anchor);
        }
        return (T) this;
    }

    /**
     * Sets the X-coordinate of the element.
     *
     * @param x the new X-coordinate, in points
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code x} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T setX(double x) {
        return at(x, getY());
    }

    /**
     * Sets the X-coordinate of the element (fluent builder alias).
     *
     * @param x the new X-coordinate, in points
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code x} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T x(double x) {
        return setX(x);
    }

    /**
     * Sets the Y-coordinate of the element.
     *
     * @param y the new Y-coordinate, in points
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code y} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T setY(double y) {
        return at(getX(), y);
    }

    /**
     * Sets the Y-coordinate of the element (fluent builder alias).
     *
     * @param y the new Y-coordinate, in points
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code y} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T y(double y) {
        return setY(y);
    }

    /**
     * Sets the position of the element (fluent builder alias for {@link #at(double, double)}).
     *
     * @param x the new X-coordinate, in points
     * @param y the new Y-coordinate, in points
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code x} or {@code y} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T position(double x, double y) {
        return at(x, y);
    }

    /**
     * Moves the element by the specified offsets.
     *
     * @param dx the horizontal offset, in points
     * @param dy the vertical offset, in points
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code dx} or {@code dy} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T move(double dx, double dy) {
        return at(getX() + dx, getY() + dy);
    }

    // ==================== Size Manipulation ====================

    /**
     * Sets the size of the element.
     *
     * @param width  the new width, in points; must be positive
     * @param height the new height, in points; must be positive
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code width} or {@code height} is not positive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T size(double width, double height) {
        ensurePresentationOpen();
        ValidationUtils.positive(width, "width");
        ValidationUtils.positive(height, "height");
        Rectangle2D anchor = shape.getAnchor();
        if (anchor == null) {
            anchor = new Rectangle2D.Double(0, 0, width, height);
        } else {
            anchor = new Rectangle2D.Double(anchor.getX(), anchor.getY(), width, height);
        }
        if (shape instanceof XSLFSimpleShape) {
            ((XSLFSimpleShape) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFGroupShape) {
            ((XSLFGroupShape) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFGraphicFrame) {
            ((XSLFGraphicFrame) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFTable) {
            ((XSLFTable) shape).setAnchor(anchor);
        } else if (shape instanceof XSLFConnectorShape) {
            ((XSLFConnectorShape) shape).setAnchor(anchor);
        }
        return (T) this;
    }

    /**
     * Sets the width of the element.
     *
     * @param width the new width, in points; must be positive
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code width} is not positive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T setWidth(double width) {
        return size(width, getHeight());
    }

    /**
     * Sets the width of the element (fluent builder alias).
     *
     * @param width the new width, in points; must be positive
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code width} is not positive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T width(double width) {
        return setWidth(width);
    }

    /**
     * Sets the height of the element.
     *
     * @param height the new height, in points; must be positive
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code height} is not positive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T setHeight(double height) {
        return size(getWidth(), height);
    }

    /**
     * Sets the height of the element (fluent builder alias).
     *
     * @param height the new height, in points; must be positive
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code height} is not positive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T height(double height) {
        return setHeight(height);
    }

    /**
     * Scales the element uniformly by the specified factor.
     *
     * @param scale the scaling factor; 1.0 represents 100%, must be positive
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code scale} is not positive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T scale(double scale) {
        ValidationUtils.positive(scale, "scale");
        return size(getWidth() * scale, getHeight() * scale);
    }

    /**
     * Scales the element independently along the X and Y axes.
     *
     * @param scaleX the horizontal scaling factor; must be positive
     * @param scaleY the vertical scaling factor; must be positive
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code scaleX} or {@code scaleY} is not positive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T scale(double scaleX, double scaleY) {
        ValidationUtils.positive(scaleX, "scaleX");
        ValidationUtils.positive(scaleY, "scaleY");
        return size(getWidth() * scaleX, getHeight() * scaleY);
    }

    // ==================== Rotation and Flipping ====================

    /**
     * Sets the rotation angle of the element.
     * <p>Rotation is only applied to simple shapes; other shape types are unaffected.</p>
     *
     * @param angle the rotation angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T rotate(double angle) {
        ensurePresentationOpen();
        if (shape instanceof XSLFSimpleShape) {
            ((XSLFSimpleShape) shape).setRotation(ValidationUtils.normalizeAngle(angle));
        }
        return (T) this;
    }

    /**
     * Sets the rotation angle of the element (fluent builder alias).
     *
     * @param angle the rotation angle in degrees
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T rotation(double angle) {
        return rotate(angle);
    }

    /**
     * Rotates the element by the specified angle delta.
     * <p>Rotation is only applied to simple shapes; other shape types are unaffected.</p>
     *
     * @param deltaAngle the angle to add to the current rotation, in degrees
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code deltaAngle} is not a finite number
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T rotateBy(double deltaAngle) {
        ValidationUtils.finite(deltaAngle, "deltaAngle");
        if (shape instanceof XSLFSimpleShape) {
            ((XSLFSimpleShape) shape).setRotation(ValidationUtils.normalizeAngle(getRotation() + deltaAngle));
        }
        return (T) this;
    }

    /**
     * Toggles the horizontal flip state of the element.
     * <p>Flipping is only applied to simple shapes; other shape types are unaffected.</p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T flipHorizontal() {
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape ss = (XSLFSimpleShape) shape;
            ss.setFlipHorizontal(!ss.getFlipHorizontal());
        }
        return (T) this;
    }

    /**
     * Toggles the vertical flip state of the element.
     * <p>Flipping is only applied to simple shapes; other shape types are unaffected.</p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T flipVertical() {
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape ss = (XSLFSimpleShape) shape;
            ss.setFlipVertical(!ss.getFlipVertical());
        }
        return (T) this;
    }

    // ==================== Z-Order Manipulation ====================

    /**
     * Brings this element to the front of the slide's drawing order.
     * <p>This operation manipulates the underlying XML structure of the slide.</p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T bringToFront() {
        ensurePresentationOpen();
        XSLFSlide slide = parentSlide.getRawSlide();
        List<XSLFShape> shapes = slide.getShapes();
        if (shapes.contains(shape) && shapes.indexOf(shape) < shapes.size() - 1) {
            // POI doesn't have a direct API, need to use removeShape and createShape
            // For now, we use a workaround via XML manipulation
            XmlUtils.bringToFront(slide, shape);
        }
        return (T) this;
    }

    /**
     * Sends this element to the back of the slide's drawing order.
     * <p>This method uses internal XML manipulation because Apache POI does not provide a direct API for zorder changes.</p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T sendToBack() {
        XSLFSlide slide = parentSlide.getRawSlide();
        List<XSLFShape> shapes = slide.getShapes();
        if (shapes.contains(shape) && shapes.indexOf(shape) > 0) {
            XmlUtils.sendToBack(slide, shape);
        }
        return (T) this;
    }

    /**
     * Brings this element one step forward in the slide's drawing order.
     * <p>This method uses internal XML manipulation because Apache POI does not provide a direct API for zorder changes.</p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T bringForward() {
        XSLFSlide slide = parentSlide.getRawSlide();
        List<XSLFShape> shapes = slide.getShapes();
        int index = shapes.indexOf(shape);
        if (index >= 0 && index < shapes.size() - 1) {
            XmlUtils.bringForward(slide, shape);
        }
        return (T) this;
    }

    /**
     * Sends this element one step backward in the slide's drawing order.
     * <p>This method uses internal XML manipulation because Apache POI does not provide a direct API for zorder changes.</p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T sendBackward() {
        XSLFSlide slide = parentSlide.getRawSlide();
        List<XSLFShape> shapes = slide.getShapes();
        int index = shapes.indexOf(shape);
        if (index > 0) {
            XmlUtils.sendBackward(slide, shape);
        }
        return (T) this;
    }

    // ==================== Animation ====================

    /**
     * Applies an animation to this element.
     *
     * @param animation the animation to apply; must not be null
     * @return this element for method chaining
     * @throws NullPointerException if {@code animation} is null
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T animate(PotAnimation animation) {
        ensurePresentationOpen();
        AnimationHelper.apply(parentSlide.getRawSlide(), shape, animation);
        return (T) this;
    }

    /**
     * Removes any animation attached to this element.
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T removeAnimation() {
        AnimationHelper.remove(parentSlide.getRawSlide(), shape);
        return (T) this;
    }

    /**
     * Returns the animation currently attached to this element.
     *
     * @return the attached animation, or {@code null} if none exists
     */
    public PotAnimation getAnimation() {
        return AnimationHelper.get(parentSlide.getRawSlide(), shape);
    }

    /**
     * Checks whether this element has an animation attached.
     *
     * @return {@code true} if an animation is attached, {@code false} otherwise
     */
    public boolean hasAnimation() {
        return AnimationHelper.hasAnimation(parentSlide.getRawSlide(), shape);
    }

    // ==================== Hyperlinks ====================

    /**
     * Attaches a hyperlink to this element that points to an external URL.
     * <p>Hyperlinks are only applicable to simple shapes; other shape types are unaffected.</p>
     *
     * @param url the destination URL; must not be null
     * @return this element for method chaining
     * @throws NullPointerException if {@code url} is null
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T hyperlink(String url) {
        ensurePresentationOpen();
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape simpleShape = (XSLFSimpleShape) shape;
            XSLFHyperlink link = simpleShape.getHyperlink();
            if (link == null) {
                link = simpleShape.createHyperlink();
            }
            link.setAddress(url);
        }
        return (T) this;
    }

    /**
     * Creates a hyperlink that jumps to a specific slide within the same presentation.
     * <p>This method sets both the internal slide reference and a fallback address string.
     * Hyperlinks are only applicable to simple shapes; other shape types are unaffected.</p>
     *
     * @param slideIndex the zerobased index of the target slide
     * @return this element for method chaining
     * @throws IndexOutOfBoundsException if {@code slideIndex} is out of the presentation's slide range
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T hyperlinkToSlide(int slideIndex) {
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape simpleShape = (XSLFSimpleShape) shape;
            XSLFHyperlink link = simpleShape.getHyperlink();
            if (link == null) {
                link = simpleShape.createHyperlink();
            }
            // Set a fallback address for compatibility
            link.setAddress("slide" + (slideIndex + 1) + ".xml");
            link.linkToSlide(parentSlide.getPresentation().getSlide(slideIndex).getRawSlide());
        }
        return (T) this;
    }

    /**
     * Removes any hyperlink attached to this element.
     * <p>This method manipulates the underlying XML because Apache POI does not provide a direct removal API.
     * Hyperlinks are only applicable to simple shapes; other shape types are unaffected.</p>
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T removeHyperlink() {
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape simpleShape = (XSLFSimpleShape) shape;
            // POI doesn't have a direct removeHyperlink method
            // We need to manipulate the underlying XML
            XmlUtils.removeHyperlink(simpleShape);
        }
        return (T) this;
    }

    /**
     * Returns the URL of the hyperlink attached to this element.
     *
     * @return the hyperlink URL, or {@code null} if no hyperlink exists or the shape does not support hyperlinks
     */
    public String getHyperlinkUrl() {
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape simpleShape = (XSLFSimpleShape) shape;
            XSLFHyperlink link = simpleShape.getHyperlink();
            if (link != null) {
                return link.getAddress();
            }
        }
        return null;
    }

    /**
     * Attaches a hyperlink defined by a {@link PotHyperlink} object.
     * <p>If the provided link is {@code null}, any existing hyperlink is removed.
     * Hyperlinks are only applicable to simple shapes; other shape types are unaffected.</p>
     *
     * @param link the hyperlink configuration, or {@code null} to remove the hyperlink
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T hyperlink(PotHyperlink link) {
        if (link == null) {
            return removeHyperlink();
        }

        if (shape instanceof XSLFSimpleShape) {
            // Delegate to PotHyperlink's applyTo method
            link.applyTo((XSLFSimpleShape) shape, parentSlide.getPresentation());
        }

        return (T) this;
    }

    // ==================== Duplication and Removal ====================

    /**
     * Creates a copy of this element on the same slide.
     *
     * <p><b>Supported element types</b>
     * <ul>
     *   <li>{@link PotShape}  geometric shapes</li>
     *   <li>{@link PotTextBox}  text boxes</li>
     *   <li>{@link PotImage}  images</li>
     *   <li>{@link PotTable}  tables</li>
     *   <li>{@link PotConnector}  connector lines</li>
     * </ul>
     *
     * <p>Other element types such as {@code PotAudio} or {@code PotVideo} are not supported and will throw a
     * {@link PotException}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTextBox original = slide.addTextBox("Original");
     * PotTextBox copy = original.duplicate();
     * copy.setText("Copy").move(100, 0);
     * }</pre>
     *
     * @return a new element that is a duplicate of this one
     * @throws PotException if duplication is not supported for this element type
     */
    public PotElement duplicate() {
        ensurePresentationOpen();
        // Subclasses must override this method
        String elementType = this.getClass().getSimpleName();
        PotLogger.warn(PotElement.class, "duplicate",
            "Duplicate not supported for element type: " + elementType);
        throw PotException.unsupportedOperation(
            "Duplicate not supported for " + elementType +
            ". Only PotShape, PotTextBox, PotImage, PotTable, and PotConnector support duplication.");
    }

    /**
     * Removes this element from the slide and cleans up internal references.
     * <p>After removal, the element should no longer be used.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotElement element = slide.getElements().get(0);
     * element.remove();
     * // element is now invalid; further operations may throw exceptions
     * }</pre>
     */
    public void remove() {
        ensurePresentationOpen();
        parentSlide.getRawSlide().removeShape(shape);
        parentSlide.removeFromCache(this);
        parentSlide.getPresentation().unregisterElement(uuid);
    }

    // ==================== Shadow Effects ====================

    /**
     * Applies a shadow effect to the element.
     * <p>Shadows are only applicable to simple shapes; other shape types are unaffected.
     * Passing {@code null} or a shadow of type {@link PotShadow.Type#NONE} removes any existing shadow.</p>
     *
     * @param shadow the shadow configuration, or {@code null} to remove the shadow
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T shadow(PotShadow shadow) {
        if (shape instanceof XSLFSimpleShape) {
            XSLFSimpleShape simpleShape = (XSLFSimpleShape) shape;
            if (shadow == null || shadow.getType() == PotShadow.Type.NONE) {
                // Remove shadow
                XmlUtils.removeShadow(simpleShape);
            } else {
                // Apply shadow
                XmlUtils.applyShadow(simpleShape, shadow);
            }
        }
        return (T) this;
    }

    /**
     * Removes any shadow effect from the element.
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T removeShadow() {
        return shadow(null);
    }

    // ==================== Visual Effects ====================

    /**
     * Applies a reflection effect to the element.
     *
     * @param reflection the reflection configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T reflection(PotEffect.Reflection reflection) {
        EffectHelper.applyReflection(shape, reflection);
        return (T) this;
    }

    /**
     * Applies a glow effect to the element.
     *
     * @param glow the glow configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T glow(PotEffect.Glow glow) {
        EffectHelper.applyGlow(shape, glow);
        return (T) this;
    }

    /**
     * Applies a softedge effect to the element.
     *
     * @param softEdge the softedge configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T softEdge(PotEffect.SoftEdge softEdge) {
        EffectHelper.applySoftEdge(shape, softEdge);
        return (T) this;
    }

    // ==================== 3D Formatting ====================

    /**
     * Applies a 3D rotation effect to the element.
     *
     * @param rotation the 3D rotation configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T rotation3D(Pot3DFormat.Rotation rotation) {
        EffectHelper.apply3DRotation(shape, rotation);
        return (T) this;
    }

    /**
     * Applies a bevel effect to the element.
     *
     * @param bevel the bevel configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T bevel(Pot3DFormat.Bevel bevel) {
        EffectHelper.applyBevel(shape, bevel);
        return (T) this;
    }

    /**
     * Applies a material effect to the element.
     *
     * @param material the material configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T material(Pot3DFormat.Material material) {
        EffectHelper.applyMaterial(shape, material);
        return (T) this;
    }

    /**
     * Applies a lighting effect to the element.
     *
     * @param lighting the lighting configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T lighting(Pot3DFormat.Lighting lighting) {
        EffectHelper.applyLighting(shape, lighting);
        return (T) this;
    }

    /**
     * Removes all visual effects (shadow, reflection, glow, softedge, 3D) from the element.
     *
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T removeEffects() {
        EffectHelper.removeAllEffects(shape);
        return (T) this;
    }

    // ==================== Action Triggers ====================

    /**
     * Attaches an action (e.g., mouseclick or mouseover) to the element.
     *
     * @param action the action configuration
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T action(PotAction action) {
        ActionHelper.applyAction(shape, action);
        return (T) this;
    }

    /**
     * Removes an action from the element.
     *
     * @param isHover {@code true} to remove a mouseover action, {@code false} to remove a mouseclick action
     * @return this element for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T removeAction(boolean isHover) {
        ActionHelper.removeAction(shape, isHover);
        return (T) this;
    }

    // ==================== Opacity ====================

    /**
     * Sets the opacity (transparency) of the element.
     * <p>Opacity is applied by manipulating the underlying XML, as Apache POI does not provide a direct API.
     * The value must be between 0.0 (fully transparent) and 1.0 (fully opaque).</p>
     *
     * @param alpha the opacity value between 0.0 (transparent) and 1.0 (opaque)
     * @return this element for method chaining
     * @throws IllegalArgumentException if {@code alpha} is not between 0.0 and 1.0 inclusive
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T opacity(double alpha) {
        ValidationUtils.percentage01(alpha, "alpha");
        // Apply via XML manipulation
        XmlUtils.setOpacity(shape, alpha);
        return (T) this;
    }

    // ==================== Raw Access ====================

    /**
     * Returns the underlying Apache POI {@code XSLFShape} object.
     * <p>This method provides lowlevel access for advanced operations not covered by the PotShaper API.
     * Direct manipulation of the raw shape may break internal invariants; use with caution.</p>
     *
     * @return the underlying Apache POI {@code XSLFShape}
     */
    public XSLFShape getRawShape() {
        ensurePresentationOpen();
        return shape;
    }

    /**
     * Returns the shape identifier assigned by PowerPoint.
     *
     * @return the shape ID as an integer
     */
    public int getShapeId() {
        ensurePresentationOpen();
        return (int) shape.getShapeId();
    }

    // ==================== Internal Access ====================

    /**
     * Internal method to retrieve the underlying shape.
     *
     * @return the underlying {@code XSLFShape}
     */
    XSLFShape getShape() {
        ensurePresentationOpen();
        return shape;
    }

    protected final void ensurePresentationOpen() {
        parentSlide.getPresentation().ensureNotClosed();
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        return String.format("%s{uuid=%s, position=(%.0f,%.0f), size=(%.0fx%.0f)}",
            getClass().getSimpleName(), uuid, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotElement that = (PotElement) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
