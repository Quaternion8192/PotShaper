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
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import javax.xml.namespace.QName;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

/**
 * Represents a connector shape that can be drawn between points or shapes on a slide.
 *
 * <p>A connector is a line that can be used to link elements visually, such as in flowcharts
 * or diagrams. It supports various line styles, arrowheads, and connection points to other shapes.
 * The connector's geometry is defined by a bounding rectangle where the start and end points
 * are determined by the rectangle's corners and flip properties.</p>
 *
 * <h3>Usage Example: Creating a Simple Connector</h3>
 * <pre>{@code
 * // Create a straight connector from (100,100) to (300,200)
 * PotConnector line = slide.addConnector(PotConnector.ConnectorType.STRAIGHT)
 *     .from(100, 100)
 *     .to(300, 200)
 *     .lineColor(PotColor.BLACK)
 *     .lineWidth(2);
 *
 * // Add arrowheads to both ends
 * line.startArrow(PotConnector.ArrowType.TRIANGLE)
 *     .endArrow(PotConnector.ArrowType.TRIANGLE);
 * }</pre>
 *
 * <h3>Usage Example: Connecting Two Shapes</h3>
 * <pre>{@code
 * // Create a connector that automatically links shape1 to shape2
 * PotConnector connector = slide.addConnector(shape1, shape2);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotConnector extends PotElement {

    // ==================== Fields ====================

    /** The underlying Apache POI connector shape object. */
    private final XSLFConnectorShape connectorShape;

    /** The visual type of this connector (straight, elbow, or curved). */
    private ConnectorType connectorType;

    // ==================== Enumerations ====================

    /**
     * Defines the visual style of the connector line.
     *
     * @since 1.0
     */
    public enum ConnectorType {
        /** A straight line segment. */
        STRAIGHT,
        /** A line with a single right-angle elbow. */
        ELBOW,
        /** A smooth curved line. */
        CURVED
    }

    /**
     * Defines the style of arrowheads that can be applied to the connector ends.
     *
     * @since 1.0
     */
    public enum ArrowType {
        /** No arrowhead. */
        NONE,
        /** A filled triangular arrowhead. */
        TRIANGLE,
        /** A standard arrow shape. */
        ARROW,
        /** A stealth (tapered) arrowhead. */
        STEALTH,
        /** A diamond-shaped arrowhead. */
        DIAMOND,
        /** An oval-shaped arrowhead. */
        OVAL,
        /** An open (unfilled) arrowhead. */
        OPEN
    }

    /**
     * Defines the relative size of an arrowhead.
     *
     * @since 1.0
     */
    public enum ArrowSize {
        /** Small arrowhead size. */
        SMALL,
        /** Medium arrowhead size. */
        MEDIUM,
        /** Large arrowhead size. */
        LARGE
    }

    // ==================== Constructors ====================

    /**
     * Constructs a new PotConnector wrapping an Apache POI connector shape.
     *
     * <p>This constructor is intended for internal use by the library. The connector type
     * defaults to {@link ConnectorType#STRAIGHT} if the provided type is {@code null}.</p>
     *
     * @param connectorShape the underlying Apache POI connector shape object
     * @param parentSlide the parent slide that contains this connector
     * @param uuid the unique identifier for this element
     * @param connectorType the visual type of the connector, or {@code null} for default
     * @throws NullPointerException if {@code connectorShape} is {@code null}
     * @since 1.0
     */
    PotConnector(XSLFConnectorShape connectorShape, PotSlide parentSlide, String uuid,
                 ConnectorType connectorType) {
        super(connectorShape, parentSlide, uuid);
        this.connectorShape = Objects.requireNonNull(connectorShape, "connectorShape cannot be null");
        this.connectorType = connectorType != null ? connectorType : ConnectorType.STRAIGHT;
    }

    // ==================== Connector Type ====================

    /**
     * Returns the visual type of this connector.
     *
     * @return the current connector type, never {@code null}
     * @since 1.0
     */
    public ConnectorType getConnectorType() {
        return connectorType;
    }

    /**
     * Sets the visual type of this connector and updates the underlying XML.
     *
     * <p>The connector type determines the line geometry: straight, elbow, or curved.
     * If the provided type is {@code null}, it defaults to {@link ConnectorType#STRAIGHT}.</p>
     *
     * @param type the new connector type, or {@code null} to use the default
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector setConnectorType(ConnectorType type) {
        this.connectorType = type != null ? type : ConnectorType.STRAIGHT;
        applyConnectorTypeToXml(this.connectorType);
        return this;
    }

    /**
     * Applies the specified connector type to the underlying XML shape definition.
     *
     * <p>This method updates the {@code prstGeom/@prst} attribute in the shape's XML
     * to reflect the chosen connector geometry.</p>
     *
     * @param type the connector type to apply
     * @since 1.0
     */
    private void applyConnectorTypeToXml(ConnectorType type) {
        XmlObject xml = connectorShape.getXmlObject();
        if (xml == null) {
            return;
        }
        String prst;
        switch (type) {
            case ELBOW:
                prst = "bentConnector3";
                break;
            case CURVED:
                prst = "curvedConnector3";
                break;
            case STRAIGHT:
            default:
                prst = "straightConnector1";
                break;
        }
        try (XmlCursor cursor = xml.newCursor()) {
            String ns = "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; ";
            cursor.selectPath(ns + ".//a:prstGeom");
            if (cursor.toNextSelection()) {
                cursor.setAttributeText(new QName("prst"), prst);
            }
        }
    }

    // ==================== Geometry ====================

    /**
     * Sets the starting point of the connector, preserving the current end point.
     *
     * <p>This method adjusts the connector's anchor rectangle so that its effective start point
     * becomes the specified coordinates. The end point remains at its current location relative
     * to the new anchor.</p>
     *
     * @param x the X-coordinate of the start point, in points
     * @param y the Y-coordinate of the start point, in points
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector from(double x, double y) {
        Rectangle2D anchor = connectorShape.getAnchor();
        if (anchor == null) {
            anchor = new Rectangle2D.Double(x, y, 100, 100);
        } else {
            double width = anchor.getX() + anchor.getWidth() - x;
            double height = anchor.getY() + anchor.getHeight() - y;
            anchor = new Rectangle2D.Double(x, y, width, height);
        }
        connectorShape.setAnchor(anchor);
        return this;
    }

    /**
     * Sets the ending point of the connector, preserving the current start point.
     *
     * <p>This method adjusts the connector's anchor rectangle so that its effective end point
     * becomes the specified coordinates. The start point remains at its current location relative
     * to the new anchor.</p>
     *
     * @param x the X-coordinate of the end point, in points
     * @param y the Y-coordinate of the end point, in points
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector to(double x, double y) {
        Rectangle2D anchor = connectorShape.getAnchor();
        if (anchor == null) {
            anchor = new Rectangle2D.Double(0, 0, x, y);
        } else {
            double width = x - anchor.getX();
            double height = y - anchor.getY();
            anchor = new Rectangle2D.Double(anchor.getX(), anchor.getY(), width, height);
        }
        connectorShape.setAnchor(anchor);
        return this;
    }

    /**
     * Sets both the start and end points of the connector in a single call.
     *
     * <p>This method defines the connector's bounding rectangle using the minimum and maximum
     * coordinates. The start point is {@code (x1, y1)} and the end point is {@code (x2, y2)}.
     * The connector's flip properties are automatically set if the end point coordinates
     * are less than the start point coordinates.</p>
     *
     * @param x1 the X-coordinate of the start point, in points
     * @param y1 the Y-coordinate of the start point, in points
     * @param x2 the X-coordinate of the end point, in points
     * @param y2 the Y-coordinate of the end point, in points
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector line(double x1, double y1, double x2, double y2) {
        connectorShape.setAnchor(new Rectangle2D.Double(
            Math.min(x1, x2),
            Math.min(y1, y2),
            Math.abs(x2 - x1),
            Math.abs(y2 - y1)
        ));
        // Set flip properties if the line direction is reversed
        connectorShape.setFlipHorizontal(x2 < x1);
        connectorShape.setFlipVertical(y2 < y1);
        return this;
    }

    /**
     * Returns the X-coordinate of the connector's start point.
     *
     * <p>The start point is determined by the anchor rectangle and the horizontal flip state.
     * If the connector is flipped horizontally, the start point is at the right edge;
     * otherwise, it is at the left edge.</p>
     *
     * @return the X-coordinate of the start point, in points, or 0 if the anchor is not set
     * @since 1.0
     */
    public double getStartX() {
        Rectangle2D anchor = connectorShape.getAnchor();
        if (anchor == null) return 0;
        return connectorShape.getFlipHorizontal() ?
            anchor.getX() + anchor.getWidth() : anchor.getX();
    }

    /**
     * Returns the Y-coordinate of the connector's start point.
     *
     * <p>The start point is determined by the anchor rectangle and the vertical flip state.
     * If the connector is flipped vertically, the start point is at the bottom edge;
     * otherwise, it is at the top edge.</p>
     *
     * @return the Y-coordinate of the start point, in points, or 0 if the anchor is not set
     * @since 1.0
     */
    public double getStartY() {
        Rectangle2D anchor = connectorShape.getAnchor();
        if (anchor == null) return 0;
        return connectorShape.getFlipVertical() ?
            anchor.getY() + anchor.getHeight() : anchor.getY();
    }

    /**
     * Returns the X-coordinate of the connector's end point.
     *
     * <p>The end point is determined by the anchor rectangle and the horizontal flip state.
     * If the connector is flipped horizontally, the end point is at the left edge;
     * otherwise, it is at the right edge.</p>
     *
     * @return the X-coordinate of the end point, in points, or 0 if the anchor is not set
     * @since 1.0
     */
    public double getEndX() {
        Rectangle2D anchor = connectorShape.getAnchor();
        if (anchor == null) return 0;
        return connectorShape.getFlipHorizontal() ?
            anchor.getX() : anchor.getX() + anchor.getWidth();
    }

    /**
     * Returns the Y-coordinate of the connector's end point.
     *
     * <p>The end point is determined by the anchor rectangle and the vertical flip state.
     * If the connector is flipped vertically, the end point is at the top edge;
     * otherwise, it is at the bottom edge.</p>
     *
     * @return the Y-coordinate of the end point, in points, or 0 if the anchor is not set
     * @since 1.0
     */
    public double getEndY() {
        Rectangle2D anchor = connectorShape.getAnchor();
        if (anchor == null) return 0;
        return connectorShape.getFlipVertical() ?
            anchor.getY() : anchor.getY() + anchor.getHeight();
    }

    // ==================== Line Style ====================

    /**
     * Sets the line color using a {@link PotColor}.
     *
     * <p>If the provided color is {@code null}, no change is made to the current line color.</p>
     *
     * @param color the new line color, or {@code null} to leave unchanged
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector lineColor(PotColor color) {
        if (color != null) {
            connectorShape.setLineColor(color.toAwtColor());
        }
        return this;
    }

    /**
     * Sets the line color using a standard AWT {@link Color}.
     *
     * @param color the new line color
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector lineColor(Color color) {
        connectorShape.setLineColor(color);
        return this;
    }

    /**
     * Sets the line width.
     *
     * @param width the new line width, in points
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector lineWidth(double width) {
        connectorShape.setLineWidth(width);
        return this;
    }

    /**
     * Applies a complete border style to the connector line.
     *
     * <p>This method sets the line color, width, dash style, and cap style from the provided
     * {@link PotBorder} object. Any {@code null} properties in the border are ignored.</p>
     *
     * @param border the border style to apply
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector lineStyle(PotBorder border) {
        if (border != null) {
            if (border.getColor() != null) {
                connectorShape.setLineColor(border.getColor().toAwtColor());
            }
            if (border.getWidth() > 0) {
                connectorShape.setLineWidth(border.getWidth());
            }
            if (border.getDashStyle() != null) {
                connectorShape.setLineDash(border.getDashStyle().toPoiDash());
            }
            if (border.getCapStyle() != null) {
                connectorShape.setLineCap(border.getCapStyle().toPoiCap());
            }
        }
        return this;
    }

    /**
     * Sets the dash style of the connector line.
     *
     * <p>If the provided dash style is {@code null}, no change is made to the current dash style.</p>
     *
     * @param dashStyle the new dash style, or {@code null} to leave unchanged
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector dash(PotBorder.DashStyle dashStyle) {
        if (dashStyle != null) {
            connectorShape.setLineDash(dashStyle.toPoiDash());
        }
        return this;
    }

    /**
     * Returns the current line color as a {@link PotColor}.
     *
     * @return the line color, or {@code null} if no color is set
     * @since 1.0
     */
    public PotColor getLineColor() {
        Color color = connectorShape.getLineColor();
        return color != null ? PotColor.fromAwtColor(color) : null;
    }

    /**
     * Returns the current line width.
     *
     * @return the line width, in points
     * @since 1.0
     */
    public double getLineWidth() {
        return connectorShape.getLineWidth();
    }

    // ==================== Arrowheads ====================

    /**
     * Sets the arrowhead style at the start of the connector.
     *
     * <p>If the provided arrow type is {@code null}, the start arrow remains unchanged.
     * The arrowhead is drawn at the line's start point, which is determined by the
     * connector's geometry and flip state.</p>
     *
     * @param arrowType the arrowhead style for the start of the connector
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector startArrow(ArrowType arrowType) {
        if (arrowType != null) {
            connectorShape.setLineHeadDecoration(mapArrowType(arrowType));
        }
        return this;
    }

    /**
     * Sets the arrowhead style and dimensions at the start of the connector.
     *
     * <p>This method allows specifying both the arrowhead shape and its size.
     * If the provided arrow type is {@code null}, the start arrow remains unchanged.
     * The width and length parameters control the arrowhead's proportions.</p>
     *
     * @param arrowType the arrowhead style for the start of the connector
     * @param width the width (crosssection) of the arrowhead
     * @param length the length (tiptobase) of the arrowhead
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector startArrow(ArrowType arrowType, ArrowSize width, ArrowSize length) {
        if (arrowType != null) {
            connectorShape.setLineHeadDecoration(mapArrowType(arrowType));
            connectorShape.setLineHeadWidth(mapArrowSize(width));
            connectorShape.setLineHeadLength(mapArrowSize(length));
        }
        return this;
    }

    /**
     * Sets the arrowhead style at the end of the connector.
     *
     * <p>If the provided arrow type is {@code null}, the end arrow remains unchanged.
     * The arrowhead is drawn at the line's end point, which is determined by the
     * connector's geometry and flip state.</p>
     *
     * @param arrowType the arrowhead style for the end of the connector
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector endArrow(ArrowType arrowType) {
        if (arrowType != null) {
            connectorShape.setLineTailDecoration(mapArrowType(arrowType));
        }
        return this;
    }

    /**
     * Sets the arrowhead style and dimensions at the end of the connector.
     *
     * <p>This method allows specifying both the arrowhead shape and its size.
     * If the provided arrow type is {@code null}, the end arrow remains unchanged.
     * The width and length parameters control the arrowhead's proportions.</p>
     *
     * @param arrowType the arrowhead style for the end of the connector
     * @param width the width (crosssection) of the arrowhead
     * @param length the length (tiptobase) of the arrowhead
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector endArrow(ArrowType arrowType, ArrowSize width, ArrowSize length) {
        if (arrowType != null) {
            connectorShape.setLineTailDecoration(mapArrowType(arrowType));
            connectorShape.setLineTailWidth(mapArrowSize(width));
            connectorShape.setLineTailLength(mapArrowSize(length));
        }
        return this;
    }

    /**
     * Removes arrowheads from both ends of the connector.
     *
     * <p>This method sets both the start and end arrowheads to {@link ArrowType#NONE}.</p>
     *
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector noArrow() {
        connectorShape.setLineHeadDecoration(org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.NONE);
        connectorShape.setLineTailDecoration(org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.NONE);
        return this;
    }

    // ==================== Shape Connections ====================

    /**
     * Connects the start of this connector to another shape element.
     *
     * <p>This method establishes a visual link from the start point of this connector
     * to the specified shape. The connection is stored in the underlying XML as a
     * {@code stCxn} element. If the element is {@code null}, no connection is made.</p>
     *
     * @param element the shape to connect from
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector connectFrom(PotElement element) {
        if (element != null) {
            bindConnection("stCxn", element.getShapeId());
        }
        return this;
    }

    /**
     * Connects the end of this connector to another shape element.
     *
     * <p>This method establishes a visual link from the end point of this connector
     * to the specified shape. The connection is stored in the underlying XML as an
     * {@code endCxn} element. If the element is {@code null}, no connection is made.</p>
     *
     * @param element the shape to connect to
     * @return this connector instance for method chaining
     * @since 1.0
     */
    public PotConnector connectTo(PotElement element) {
        if (element != null) {
            bindConnection("endCxn", element.getShapeId());
        }
        return this;
    }

    // ==================== Duplication ====================

    /**
     * Creates a duplicate of this connector on the same slide.
     *
     * <p>The duplicate is a new connector with the same geometry, line style, and arrowheads.
     * The copy is placed at the same position and retains the same visual appearance.
     * The underlying shape ID and UUID are different from the original.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotConnector original = slide.addConnector(ConnectorType.ELBOW)
     *     .from(50, 50).to(200, 150)
     *     .lineColor(PotColor.RED);
     * PotConnector copy = original.duplicate();
     * }</pre>
     *
     * @return a new {@code PotConnector} instance that is a copy of this one
     * @since 1.0
     */
    @Override
    public PotConnector duplicate() {
        PotConnector copy = parentSlide.addConnector(connectorType);
        copy.line(getStartX(), getStartY(), getEndX(), getEndY());

        // Copy line style
        PotColor lineColor = getLineColor();
        if (lineColor != null) {
            copy.lineColor(lineColor);
        }
        copy.lineWidth(getLineWidth());

        return copy;
    }

    // ==================== Internal Access ====================

    /**
     * Returns the underlying Apache POI connector shape.
     *
     * <p>This method provides direct access to the internal {@code XSLFConnectorShape}
     * for advanced operations not covered by the PotShaper API. Use with caution,
     * as modifications may conflict with PotShaper's internal state.</p>
     *
     * @return the raw Apache POI {@code XSLFConnectorShape}
     * @since 1.0
     */
    public XSLFConnectorShape getRawConnectorShape() {
        return connectorShape;
    }

    // ==================== Internal Mapping Utilities ====================

    /**
     * Maps a PotShaper arrow type to the corresponding Apache POI decoration shape.
     *
     * <p>This internal method converts the public {@link ArrowType} enum to the
     * POI-specific {@code LineDecoration.DecorationShape} used for arrowhead rendering.</p>
     *
     * @param type the PotShaper arrow type
     * @return the equivalent POI decoration shape
     * @since 1.0
     */
    private org.apache.poi.sl.usermodel.LineDecoration.DecorationShape mapArrowType(ArrowType type) {
        switch (type) {
            case TRIANGLE:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.TRIANGLE;
            case ARROW:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.ARROW;
            case STEALTH:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.STEALTH;
            case DIAMOND:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.DIAMOND;
            case OVAL:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.OVAL;
            case OPEN:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.ARROW;
            case NONE:
            default:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationShape.NONE;
        }
    }

    /**
     * Maps a PotShaper arrow size to the corresponding Apache POI decoration size.
     *
     * <p>This internal method converts the public {@link ArrowSize} enum to the
     * POI-specific {@code LineDecoration.DecorationSize} used for arrowhead dimensions.</p>
     *
     * @param size the PotShaper arrow size
     * @return the equivalent POI decoration size
     * @since 1.0
     */
    private org.apache.poi.sl.usermodel.LineDecoration.DecorationSize mapArrowSize(ArrowSize size) {
        switch (size) {
            case SMALL:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationSize.SMALL;
            case LARGE:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationSize.LARGE;
            case MEDIUM:
            default:
                return org.apache.poi.sl.usermodel.LineDecoration.DecorationSize.MEDIUM;
        }
    }

    /**
     * Binds a connection point to another shape in the underlying XML.
     *
     * <p>This internal method writes a {@code stCxn} or {@code endCxn} element into the
     * connector's XML structure, linking it to the specified shape ID. If the connection
     * node already exists, its attributes are updated; otherwise, a new node is created.</p>
     *
     * @param nodeName the XML node name, either "stCxn" or "endCxn"
     * @param shapeId  the target shape's numeric ID
     * @since 1.0
     */
    private void bindConnection(String nodeName, int shapeId) {
        XmlObject xml = connectorShape.getXmlObject();
        if (xml == null) {
            return;
        }

        String ns = "declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main'; ";
        try (XmlCursor cursor = xml.newCursor()) {
            cursor.selectPath(ns + ".//p:cNvCxnSpPr");
            if (!cursor.toNextSelection()) {
                return;
            }

            try (XmlCursor child = cursor.newCursor()) {
                child.selectPath(ns + "./p:" + nodeName);
                if (child.toNextSelection()) {
                    child.setAttributeText(new QName("id"), String.valueOf(shapeId));
                    child.setAttributeText(new QName("idx"), "0");
                    return;
                }
            }

            cursor.toEndToken();
            cursor.beginElement(new QName("http://schemas.openxmlformats.org/presentationml/2006/main", nodeName, "p"));
            cursor.insertAttributeWithValue(new QName("id"), String.valueOf(shapeId));
            cursor.insertAttributeWithValue(new QName("idx"), "0");
        }
    }

    // ==================== Inherited Fluent Methods ====================
    // These methods override PotElement methods to return PotConnector for chaining.

    @Override
    public PotConnector at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotConnector setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotConnector x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotConnector setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotConnector y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotConnector position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotConnector move(double dx, double dy) {
        super.move(dx, dy);
        return this;
    }

    @Override
    public PotConnector size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotConnector setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotConnector width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotConnector setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotConnector height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotConnector scale(double scale) {
        super.scale(scale);
        return this;
    }

    @Override
    public PotConnector scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    @Override
    public PotConnector rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotConnector rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotConnector rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotConnector flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotConnector flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotConnector bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotConnector sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotConnector bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotConnector sendBackward() {
        super.sendBackward();
        return this;
    }

    @Override
    public PotConnector animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotConnector removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotConnector hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    @Override
    public PotConnector hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotConnector hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotConnector removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotConnector action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotConnector removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotConnector shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotConnector removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotConnector reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotConnector glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotConnector softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotConnector removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotConnector rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotConnector bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotConnector material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotConnector lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }

    @Override
    public PotConnector opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }

    // ==================== Object Overrides ====================

    /**
     * Returns a string representation of this connector.
     *
     * <p>The string includes the connector's UUID, type, and start/end coordinates.</p>
     *
     * @return a descriptive string representation
     * @since 1.0
     */
    @Override
    public String toString() {
        return String.format("PotConnector{uuid=%s, type=%s, from=(%.0f,%.0f), to=(%.0f,%.0f)}",
            uuid, connectorType, getStartX(), getStartY(), getEndX(), getEndY());
    }
}
