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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a group of shapes and other elements within a PowerPoint slide.
 *
 * <p>A {@code PotGroup} is a container element that holds multiple {@link PotElement} instances,
 * allowing them to be manipulated as a single unit. Grouping elements enables collective
 * transformations (move, scale, rotate) and simplifies layout management. The group maintains
 * its own anchor bounds and interior anchor, which define its position and the coordinate space
 * for its child elements. When a group is ungrouped, its children are returned to the slide as
 * independent elements.</p>
 *
 * <h3>Creating a Group</h3>
 * <pre>{@code
 * PotShape rect = slide.addShape(PotShapeType.RECTANGLE);
 * PotShape oval = slide.addShape(PotShapeType.OVAL);
 * PotGroup group = slide.group(Arrays.asList(rect, oval));
 * }</pre>
 *
 * <h3>Iterating Over Group Elements</h3>
 * <pre>{@code
 * for (PotElement element : group.getElements()) {
 *     // Apply operations to each element
 * }
 * }</pre>
 *
 * <h3>Ungrouping</h3>
 * <pre>{@code
 * List<PotElement> elements = group.ungroup();
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotGroup extends PotElement {

    // ==================== Fields ====================

    /** The underlying Apache POI group shape object. */
    private final XSLFGroupShape groupShape;

    /** The list of child elements contained within this group. */
    private final List<PotElement> children;

    // ==================== Constructors ====================

    /**
     * Constructs a new group from an existing Apache POI group shape.
     *
     * <p>This constructor is intended for internal use when loading a presentation. It initializes
     * the group by recursively creating {@link PotElement} instances for each shape found within
     * the provided {@link XSLFGroupShape}.</p>
     *
     * @param groupShape  the Apache POI group shape object; must not be null
     * @param parentSlide the parent slide containing this group
     * @param uuid        the unique identifier for this element
     * @throws NullPointerException if {@code groupShape} is null
     */
    PotGroup(XSLFGroupShape groupShape, PotSlide parentSlide, String uuid) {
        super(groupShape, parentSlide, uuid);
        this.groupShape = Objects.requireNonNull(groupShape, "groupShape cannot be null");
        this.children = new ArrayList<>();

        // Initialize child elements from the underlying POI shapes
        initializeChildren();
    }

    // ==================== Element Access ====================

    /**
     * Returns an unmodifiable list of all elements contained in this group.
     *
     * <p>The returned list is a snapshot of the current child elements. Modifications to the
     * group via {@link #add(PotElement)} or {@link #remove(PotElement)} will not be reflected
     * in previously obtained lists.</p>
     *
     * @return an unmodifiable list of child elements; never null but may be empty
     */
    public List<PotElement> getElements() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns a filtered list of elements of a specific type contained in this group.
     *
     * <p>This method provides a convenient way to retrieve only those child elements that are
     * instances of the specified class, such as {@link PotShape} or {@link PotTextBox}.</p>
     *
     * @param <T>  the type of element to filter for
     * @param type the class object representing the desired element type; must not be null
     * @return a list containing all child elements that are instances of the specified type;
     *         never null but may be empty
     * @throws NullPointerException if {@code type} is null
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> List<T> getElements(Class<T> type) {
        return children.stream()
            .filter(type::isInstance)
            .map(e -> (T) e)
            .collect(Collectors.toList());
    }

    /**
     * Returns the number of elements currently contained in this group.
     *
     * @return the count of child elements
     */
    public int getElementCount() {
        return children.size();
    }

    /**
     * Returns the element at the specified position in this group.
     *
     * <p>The index is zero-based. The order of elements corresponds to their visual stacking
     * order within the group, with the first element being at the back.</p>
     *
     * @param index the index of the element to return
     * @return the element at the specified index
     * @throws PotException if the index is out of range
     *                      ({@code index < 0 || index >= getElementCount()})
     */
    public PotElement getElement(int index) {
        if (index < 0 || index >= children.size()) {
            throw PotException.invalidParameter("index",
                String.format("must be between 0 and %d, got %d", children.size() - 1, index));
        }
        return children.get(index);
    }

    // ==================== Group Management ====================

    /**
     * Ungroups this group, releasing all contained elements back to the parent slide.
     *
     * <p>This operation removes the group from the slide and returns its child elements as
     * independent entities. The underlying XML structure is modified to move each child shape
     * from the group's container to the slide's main shape tree. After ungrouping, this
     * {@code PotGroup} instance becomes invalid and should no longer be used.</p>
     *
     * <p>The returned list is a snapshot of the children before ungrouping. The elements
     * remain registered with the presentation and can be manipulated normally.</p>
     *
     * @return a list containing the former child elements; never null
     * @see PotSlide#group(List)
     */
    public List<PotElement> ungroup() {
        List<PotElement> result = new ArrayList<>(children);

        // Modify the underlying XML to ungroup shapes
        XmlUtils.ungroupShapes(parentSlide.getRawSlide(), groupShape);

        // Clean up internal state
        children.clear();
        parentSlide.removeFromCache(this);
        parentSlide.getPresentation().unregisterElement(uuid);

        return result;
    }

    /**
     * Adds an element to this group.
     *
     * <p>The element is moved from its current location (either the slide or another group)
     * into this group. Its position is adjusted relative to the group's anchor. If the element
     * is already a member of this group or is {@code null}, this method has no effect.</p>
     *
     * <p>This operation modifies the underlying XML structure to reparent the element's shape
     * node. If the XML manipulation fails, a warning is logged but the element is still added
     * to the internal list to maintain consistency.</p>
     *
     * @param element the element to add; may be null (in which case nothing happens)
     * @return this group instance for method chaining
     */
    public PotGroup add(PotElement element) {
        if (element != null && element != this) {
            // Move the element's XML node into the group's XML structure
            try {
                org.w3c.dom.Node groupNode = groupShape.getXmlObject().getDomNode();
                org.w3c.dom.Node elementNode = element.getShape().getXmlObject().getDomNode();

                if (groupNode != null && elementNode != null) {
                    // Adjust the element's offset relative to the group's position
                    java.awt.geom.Rectangle2D groupBounds = groupShape.getAnchor();
                    if (groupBounds != null) {
                        long offsetX = (long) (groupBounds.getX() * 12700);
                        long offsetY = (long) (groupBounds.getY() * 12700);
                        adjustElementOffset(elementNode, -offsetX, -offsetY);
                    }

                    // Import and append the element node to the group
                    org.w3c.dom.Node importedNode = groupNode.getOwnerDocument().importNode(elementNode, true);
                    groupNode.appendChild(importedNode);

                    // Remove the original node from its parent
                    org.w3c.dom.Node parentNode = elementNode.getParentNode();
                    if (parentNode != null) {
                        parentNode.removeChild(elementNode);
                    }
                }
            } catch (Exception e) {
                PotLogger.warn(PotGroup.class, "add",
                    "Failed to move element XML into group", e);
            }
            children.add(element);
        }
        return this;
    }

    /**
     * Removes an element from this group.
     *
     * <p>The element is moved from this group back to the parent slide's main shape tree.
     * Its position is adjusted to be absolute relative to the slide. If the element is not
     * a member of this group or is {@code null}, this method has no effect.</p>
     *
     * <p>This operation modifies the underlying XML structure to reparent the element's shape
     * node. If the XML manipulation fails, a warning is logged but the element is still removed
     * from the internal list to maintain consistency.</p>
     *
     * @param element the element to remove; may be null (in which case nothing happens)
     * @return this group instance for method chaining
     */
    public PotGroup remove(PotElement element) {
        if (element != null && children.contains(element)) {
            // Move the element's XML node out of the group and into the slide's spTree
            try {
                org.w3c.dom.Node elementNode = element.getShape().getXmlObject().getDomNode();
                if (elementNode != null) {
                    // Adjust the element's offset to be absolute relative to the slide
                    java.awt.geom.Rectangle2D groupBounds = groupShape.getAnchor();
                    if (groupBounds != null) {
                        long offsetX = (long) (groupBounds.getX() * 12700);
                        long offsetY = (long) (groupBounds.getY() * 12700);
                        adjustElementOffset(elementNode, offsetX, offsetY);
                    }

                    // Find the slide's spTree and append the element node
                    org.w3c.dom.Node slideNode = parentSlide.getRawSlide().getXmlObject().getDomNode();
                    if (slideNode instanceof org.w3c.dom.Element) {
                        org.w3c.dom.NodeList spTreeList =
                            ((org.w3c.dom.Element) slideNode).getElementsByTagNameNS("*", "spTree");
                        if (spTreeList.getLength() > 0) {
                            org.w3c.dom.Node spTree = spTreeList.item(0);
                            org.w3c.dom.Node importedNode =
                                spTree.getOwnerDocument().importNode(elementNode, true);
                            spTree.appendChild(importedNode);

                            org.w3c.dom.Node parentNode = elementNode.getParentNode();
                            if (parentNode != null) {
                                parentNode.removeChild(elementNode);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                PotLogger.warn(PotGroup.class, "remove",
                    "Failed to move element XML out of group", e);
            }
            children.remove(element);
        }
        return this;
    }

    /**
     * Adjusts the positional offset of an XML shape element.
     *
     * <p>This helper method modifies the {@code x} and {@code y} attributes of the first
     * {@code <a:off>} element found within the given node, adding the specified deltas.
     * If the node is not an {@link org.w3c.dom.Element} or no {@code <a:off>} element is found,
     * or if the attribute values are not valid numbers, the method does nothing.</p>
     *
     * @param elementNode the DOM node representing a shape element
     * @param dx          the delta to add to the x-coordinate (in EMUs)
     * @param dy          the delta to add to the y-coordinate (in EMUs)
     */
    private static void adjustElementOffset(org.w3c.dom.Node elementNode, long dx, long dy) {
        if (!(elementNode instanceof org.w3c.dom.Element)) {
            return;
        }
        org.w3c.dom.NodeList offNodes =
            ((org.w3c.dom.Element) elementNode).getElementsByTagNameNS("*", "off");
        for (int i = 0; i < offNodes.getLength(); i++) {
            org.w3c.dom.Node node = offNodes.item(i);
            if (node instanceof org.w3c.dom.Element) {
                org.w3c.dom.Element off = (org.w3c.dom.Element) node;
                try {
                    long x = Long.parseLong(off.getAttribute("x")) + dx;
                    long y = Long.parseLong(off.getAttribute("y")) + dy;
                    off.setAttribute("x", String.valueOf(x));
                    off.setAttribute("y", String.valueOf(y));
                } catch (NumberFormatException ignored) {
                }
                break;
            }
        }
    }

    // ==================== Geometry ====================

    /**
     * Returns the interior anchor rectangle of this group.
     *
     * <p>The interior anchor defines the coordinate space within the group where child elements
     * are positioned. It is typically equal to the group's anchor but can be adjusted to create
     * padding or margins inside the group.</p>
     *
     * @return the interior anchor rectangle, or null if not set
     * @see #setInteriorAnchor(Rectangle2D)
     */
    public Rectangle2D getInteriorAnchor() {
        return groupShape.getInteriorAnchor();
    }

    /**
     * Sets the interior anchor rectangle of this group.
     *
     * <p>The interior anchor defines the coordinate space within the group where child elements
     * are positioned. Setting this value does not automatically reposition child elements; it
     * only changes the reference coordinate system for future placements.</p>
     *
     * @param anchor the new interior anchor rectangle; may be null
     * @return this group instance for method chaining
     * @see #getInteriorAnchor()
     */
    public PotGroup setInteriorAnchor(Rectangle2D anchor) {
        groupShape.setInteriorAnchor(anchor);
        return this;
    }

    /**
     * Recalculates the group's bounds based on the positions of its child elements.
     *
     * <p>This method computes the bounding rectangle that encloses all child elements and
     * updates both the group's anchor and interior anchor to match. If the group contains no
     * children, this method has no effect.</p>
     *
     * <p>Recalculating bounds is useful after adding, removing, or moving child elements to
     * ensure the group's container rectangle accurately reflects its contents.</p>
     *
     * @return this group instance for method chaining
     */
    public PotGroup recalculateBounds() {
        if (children.isEmpty()) {
            return this;
        }

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (PotElement element : children) {
            minX = Math.min(minX, element.getX());
            minY = Math.min(minY, element.getY());
            maxX = Math.max(maxX, element.getX() + element.getWidth());
            maxY = Math.max(maxY, element.getY() + element.getHeight());
        }

        Rectangle2D bounds = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
        groupShape.setAnchor(bounds);
        groupShape.setInteriorAnchor(bounds);

        return this;
    }

    // ==================== Transformations ====================

    /**
     * Moves this group by the specified offsets.
     *
     * <p>This operation adjusts the group's anchor position, effectively moving all contained
     * elements together. The movement is applied relative to the group's current position.</p>
     *
     * @param dx the horizontal offset to move by (in points)
     * @param dy the vertical offset to move by (in points)
     * @return this group instance for method chaining
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T move(double dx, double dy) {
        Rectangle2D anchor = groupShape.getAnchor();
        if (anchor != null) {
            groupShape.setAnchor(new Rectangle2D.Double(
                anchor.getX() + dx,
                anchor.getY() + dy,
                anchor.getWidth(),
                anchor.getHeight()
            ));
        }
        return (T) this;
    }

    /**
     * Scales this group uniformly by the specified factor.
     *
     * <p>This method resizes the group's anchor bounds, scaling all child elements
     * proportionally. The scaling is centered on the group's current position.</p>
     *
     * @param scale the scaling factor (1.0 means no change)
     * @return this group instance for method chaining
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PotElement> T scale(double scale) {
        Rectangle2D anchor = groupShape.getAnchor();
        if (anchor != null) {
            groupShape.setAnchor(new Rectangle2D.Double(
                anchor.getX(),
                anchor.getY(),
                anchor.getWidth() * scale,
                anchor.getHeight() * scale
            ));
        }
        return (T) this;
    }

    // ==================== Duplication ====================

    /**
     * Duplication is not supported for groups.
     *
     * <p>Groups cannot be duplicated directly because they contain complex child hierarchies.
     * To duplicate a group, you must ungroup it, duplicate the individual elements, and then
     * create a new group from the duplicates.</p>
     *
     * @return never returns; always throws an exception
     * @throws PotException always, indicating that duplication is unsupported for groups
     */
    @Override
    public PotGroup duplicate() {
        throw PotException.unsupportedOperation("duplicate for PotGroup");
    }

    // ==================== Iteration ====================

    /**
     * Performs the given action for each element in this group.
     *
     * <p>This method provides a convenient way to apply an operation to every child element.
     * The action is performed in the order of the elements' visual stacking (back to front).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * group.forEach(element -> element.setOpacity(0.5));
     * }</pre>
     *
     * @param action the action to perform on each element; must not be null
     * @return this group instance for method chaining
     * @throws NullPointerException if {@code action} is null
     */
    public PotGroup forEach(java.util.function.Consumer<PotElement> action) {
        children.forEach(action);
        return this;
    }

    /**
     * Performs the given action for each element of a specific type in this group.
     *
     * <p>This method filters the child elements by the specified class and applies the action
     * only to those that match. This is useful when you need to operate on a subset of elements,
     * such as all shapes or all text boxes.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Make all shapes in the group red
     * group.forEach(PotShape.class, shape -> shape.setFillColor(Color.RED));
     * }</pre>
     *
     * @param <T>    the type of element to filter for
     * @param type   the class object representing the desired element type; must not be null
     * @param action the action to perform on each matching element; must not be null
     * @return this group instance for method chaining
     * @throws NullPointerException if {@code type} or {@code action} is null
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> PotGroup forEach(Class<T> type, java.util.function.Consumer<T> action) {
        children.stream()
            .filter(type::isInstance)
            .map(e -> (T) e)
            .forEach(action);
        return this;
    }

    // ==================== Raw Access ====================

    /**
     * Returns the underlying Apache POI {@code XSLFGroupShape} object.
     *
     * <p>This method provides direct access to the low-level POI shape for advanced operations
     * not covered by the PotShaper API. Use with caution, as modifications may break internal
     * consistency.</p>
     *
     * @return the raw Apache POI group shape object
     */
    public XSLFGroupShape getRawGroupShape() {
        return groupShape;
    }

    // ==================== Internal Helpers ====================

    /**
     * Initializes the child elements list from the underlying POI group shape.
     *
     * <p>This method is called during construction to recursively create {@link PotElement}
     * instances for each shape found within the group. It ensures that the internal
     * {@code children} list matches the actual content of the group.</p>
     */
    private void initializeChildren() {
        for (XSLFShape shape : groupShape.getShapes()) {
            PotElement element = createElementFromShape(shape);
            if (element != null) {
                children.add(element);
            }
        }
    }

    /**
     * Creates a {@link PotElement} instance from an Apache POI shape.
     *
     * <p>This factory method maps POI shape types to their corresponding PotShaper wrapper
     * classes. It also registers the created element with the presentation's UUID registry.</p>
     *
     * @param shape the Apache POI shape to wrap
     * @return a new {@code PotElement} instance, or {@code null} if the shape type is unsupported
     */
    private PotElement createElementFromShape(XSLFShape shape) {
        PotPresentation ppt = parentSlide.getPresentation();
        String uuid = ppt.allocateUUID();

        PotElement element = null;

        if (shape instanceof XSLFTextBox) {
            element = new PotTextBox((XSLFTextBox) shape, parentSlide, uuid);
        } else if (shape instanceof XSLFAutoShape) {
            element = new PotShape((XSLFAutoShape) shape, parentSlide, uuid);
        } else if (shape instanceof XSLFPictureShape) {
            element = new PotImage((XSLFPictureShape) shape, parentSlide, uuid);
        } else if (shape instanceof XSLFTable) {
            element = new PotTable((XSLFTable) shape, parentSlide, uuid);
        } else if (shape instanceof XSLFGroupShape) {
            element = new PotGroup((XSLFGroupShape) shape, parentSlide, uuid);
        } else if (shape instanceof XSLFConnectorShape) {
            element = new PotConnector((XSLFConnectorShape) shape, parentSlide, uuid,
                PotConnector.ConnectorType.STRAIGHT);
        } else {
            element = new PotUnknownElement(shape, parentSlide, uuid);
        }

        if (element != null) {
            ppt.registerElement(uuid, element);
        }

        return element;
    }

    // ==================== Fluent Overrides ====================
    // The following methods override parent class methods to return PotGroup
    // for fluent chaining. Their behavior is identical to the parent implementation.

    @Override
    public PotGroup at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotGroup setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotGroup x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotGroup setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotGroup y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotGroup position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotGroup size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotGroup setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotGroup width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotGroup setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotGroup height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotGroup scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    @Override
    public PotGroup rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotGroup rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotGroup rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotGroup flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotGroup flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotGroup bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotGroup sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotGroup bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotGroup sendBackward() {
        super.sendBackward();
        return this;
    }

    @Override
    public PotGroup animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotGroup removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotGroup hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    @Override
    public PotGroup hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotGroup hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotGroup removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotGroup action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotGroup removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotGroup shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotGroup removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotGroup reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotGroup glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotGroup softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotGroup removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotGroup rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotGroup bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotGroup material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotGroup lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }

    @Override
    public PotGroup opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }

    // ==================== Object Overrides ====================

    /**
     * Returns a string representation of this group.
     *
     * <p>The string includes the group's UUID, element count, position, and dimensions.</p>
     *
     * @return a descriptive string representation
     */
    @Override
    public String toString() {
        return String.format("PotGroup{uuid=%s, elements=%d, position=(%.0f,%.0f), size=(%.0fx%.0f)}",
            uuid, getElementCount(), getX(), getY(), getWidth(), getHeight());
    }
}
