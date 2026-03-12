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

import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

/**
 * Represents a shape element within a PowerPoint slide.
 *
 * <p>This class provides a fluent API for creating and configuring shapes such as rectangles,
 * circles, and arrows. It encapsulates an Apache POI {@code XSLFAutoShape} and offers methods
 * to set its geometry, fill, border, text content, font properties, and various visual effects.
 * All methods that modify the shape return {@code this} to enable method chaining.</p>
 *
 * <h3>Basic Shape Creation</h3>
 * <pre>{@code
 * PotShape shape = slide.addShape(PotShapeType.RECTANGLE)
 *     .at(100, 100)
 *     .size(200, 100)
 *     .fill(PotColor.BLUE)
 *     .border(PotBorder.of(PotColor.DARK_BLUE, 2));
 * }</pre>
 *
 * <h3>Text Shape Creation</h3>
 * <pre>{@code
 * slide.addShape(PotShapeType.ROUNDED_RECTANGLE)
 *     .at(100, 100)
 *     .size(200, 100)
 *     .fill(PotColor.LIGHT_BLUE)
 *     .setText("Hello")
 *     .fontColor(PotColor.WHITE)
 *     .align(PotAlignment.CENTER)
 *     .verticalAlign(PotVerticalAlignment.MIDDLE);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotShape extends PotElement {

    // ==================== Instance Fields ====================

    /** The underlying Apache POI auto shape object. */
    private final XSLFAutoShape autoShape;

    // ==================== Constructors ====================

    /**
     * Constructs a new {@code PotShape} wrapper for the specified POI auto shape.
     *
     * <p>This constructor is intended for internal use by the library. The provided
     * auto shape must not be {@code null}.</p>
     *
     * @param autoShape   the Apache POI {@code XSLFAutoShape} to wrap
     * @param parentSlide the parent slide containing this shape
     * @param uuid        the unique identifier for this shape
     * @throws NullPointerException if {@code autoShape} is {@code null}
     */
    PotShape(XSLFAutoShape autoShape, PotSlide parentSlide, String uuid) {
        super(autoShape, parentSlide, uuid);
        this.autoShape = Objects.requireNonNull(autoShape, "autoShape cannot be null");
    }

    // ==================== Shape Type Methods ====================

    /**
     * Returns the geometric type of this shape.
     *
     * @return the shape type, such as {@code RECTANGLE} or {@code ELLIPSE}
     */
    public PotShapeType getShapeType() {
        ShapeType poiType = autoShape.getShapeType();
        return ShapeTypeMapper.toPotType(poiType);
    }

    /**
     * Changes the geometric type of this shape.
     *
     * <p>This method modifies the underlying shape's geometry while preserving its
     * position, size, and other visual properties.</p>
     *
     * @param shapeType the new shape type to apply
     * @return this shape for method chaining
     * @throws NullPointerException if {@code shapeType} is {@code null}
     */
    public PotShape setShapeType(PotShapeType shapeType) {
        autoShape.setShapeType(ShapeTypeMapper.toPoiType(ValidationUtils.notNull(shapeType, "shapeType")));
        return this;
    }

    // ==================== Fill Methods ====================

    /**
     * Sets a solid color fill for this shape.
     *
     * <p>If the provided color is {@code null}, the fill is cleared, making the shape transparent.</p>
     *
     * @param color the color to fill the shape with
     * @return this shape for method chaining
     */
    public PotShape fill(PotColor color) {
        if (color != null) {
            autoShape.setFillColor(color.toAwtColor());
        } else {
            autoShape.setFillColor(null);
        }
        return this;
    }

    /**
     * Sets a solid color fill for this shape using a standard AWT {@code Color}.
     *
     * @param color the AWT color to fill the shape with
     * @return this shape for method chaining
     */
    public PotShape fill(Color color) {
        autoShape.setFillColor(color);
        return this;
    }

    /**
     * Applies a complex fill to this shape.
     *
     * <p>The fill can be a solid color, gradient, image, or pattern, as defined by the
     * {@link PotFill} object. If the fill is {@code null}, the shape's fill is cleared.</p>
     *
     * @param fill the fill definition to apply
     * @return this shape for method chaining
     */
    public PotShape fill(PotFill fill) {
        if (fill == null) {
            return noFill();
        }

        if (fill.isGradient() && fill.getGradient() != null) {
            XmlUtils.applyGradientFill(autoShape, fill.getGradient());
        } else if (fill.isImage() && fill.getImageData() != null) {
            try {
                XSLFPictureData picData = parentSlide.getPresentation()
                    .addPictureData(fill.getImageData(), org.apache.poi.sl.usermodel.PictureData.PictureType.PNG);
                String rId = autoShape.getSheet().getPackagePart()
                    .addRelationship(
                        picData.getPackagePart().getPartName(),
                        org.apache.poi.openxml4j.opc.TargetMode.INTERNAL,
                        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"
                    ).getId();
                XmlUtils.applyPictureFill(autoShape, rId);
            } catch (Exception e) {
                PotLogger.warn(PotShape.class, "fill", "Failed to apply picture fill", e);
            }
        } else if (fill.isPattern()) {
            XmlUtils.applyPatternFill(autoShape, fill.getPatternType(), fill.getColor(),
                fill.getBackgroundColor());
        } else if (fill.getColor() != null) {
            autoShape.setFillColor(fill.getColor().toAwtColor());
        }
        return this;
    }

    /**
     * Applies a gradient fill to this shape.
     *
     * <p>If the gradient is {@code null}, this method has no effect.</p>
     *
     * @param gradient the gradient definition to apply
     * @return this shape for method chaining
     */
    public PotShape fill(PotGradient gradient) {
        if (gradient != null) {
            XmlUtils.applyGradientFill(autoShape, gradient);
        }
        return this;
    }

    /**
     * Removes any fill from this shape, making it transparent.
     *
     * @return this shape for method chaining
     */
    public PotShape noFill() {
        autoShape.setFillColor(null);
        return this;
    }

    /**
     * Returns the current solid fill color of this shape.
     *
     * <p>If the shape has no fill, a gradient fill, an image fill, or a pattern fill,
     * this method returns {@code null}.</p>
     *
     * @return the current fill color, or {@code null} if not applicable
     */
    public PotColor getFillColor() {
        Color color = autoShape.getFillColor();
        return color != null ? PotColor.fromAwtColor(color) : null;
    }

    // ==================== Border Methods ====================

    /**
     * Applies a border to this shape.
     *
     * <p>The border definition includes color, width, dash style, and cap style.
     * If the border is {@code null}, the shape's border is removed.</p>
     *
     * @param border the border definition to apply
     * @return this shape for method chaining
     */
    public PotShape border(PotBorder border) {
        if (border == null) {
            return noBorder();
        }

        if (border.getColor() != null) {
            autoShape.setLineColor(border.getColor().toAwtColor());
        }
        if (border.getWidth() > 0) {
            autoShape.setLineWidth(border.getWidth());
        }
        if (border.getDashStyle() != null) {
            autoShape.setLineDash(border.getDashStyle().toPoiDash());
        }
        if (border.getCapStyle() != null) {
            autoShape.setLineCap(border.getCapStyle().toPoiCap());
        }

        return this;
    }

    /**
     * Sets the border color of this shape.
     *
     * <p>If the color is {@code null}, the border color is not changed.</p>
     *
     * @param color the color for the shape's border
     * @return this shape for method chaining
     */
    public PotShape borderColor(PotColor color) {
        if (color != null) {
            autoShape.setLineColor(color.toAwtColor());
        }
        return this;
    }

    /**
     * Sets the border color of this shape using a standard AWT {@code Color}.
     *
     * @param color the AWT color for the shape's border
     * @return this shape for method chaining
     */
    public PotShape borderColor(Color color) {
        autoShape.setLineColor(color);
        return this;
    }

    /**
     * Sets the border width of this shape.
     *
     * <p>The width must be a non-negative value. A width of zero effectively removes the border.</p>
     *
     * @param width the border width in points
     * @return this shape for method chaining
     * @throws IllegalArgumentException if {@code width} is negative
     */
    public PotShape borderWidth(double width) {
        ValidationUtils.nonNegative(width, "width");
        autoShape.setLineWidth(width);
        return this;
    }

    /**
     * Removes the border from this shape by setting its width to zero.
     *
     * @return this shape for method chaining
     */
    public PotShape noBorder() {
        autoShape.setLineWidth(0);
        return this;
    }

    /**
     * Returns the current border color of this shape.
     *
     * <p>If the shape has no border, this method returns {@code null}.</p>
     *
     * @return the current border color, or {@code null} if no border is set
     */
    public PotColor getLineColor() {
        Color color = autoShape.getLineColor();
        return color != null ? PotColor.fromAwtColor(color) : null;
    }

    /**
     * Returns the current border width of this shape.
     *
     * @return the border width in points
     */
    public double getLineWidth() {
        return autoShape.getLineWidth();
    }

    // ==================== Text Content Methods ====================

    /**
     * Sets the text content of this shape.
     *
     * <p>If the text is {@code null}, an empty string is used. This method replaces any
     * existing text in the shape.</p>
     *
     * @param text the text to display within the shape
     * @return this shape for method chaining
     */
    public PotShape setText(String text) {
        autoShape.setText(text != null ? text : "");
        return this;
    }

    /**
     * Returns the text content of this shape.
     *
     * @return the shape's text, which may be an empty string
     */
    public String getText() {
        return autoShape.getText();
    }

    /**
     * Appends text to the existing content of this shape.
     *
     * <p>If the text is {@code null}, this method has no effect.</p>
     *
     * @param text    the text to append
     * @param newLine if {@code true}, the appended text starts on a new line
     * @return this shape for method chaining
     */
    public PotShape appendText(String text, boolean newLine) {
        if (text != null) {
            autoShape.appendText(text, newLine);
        }
        return this;
    }

    /**
     * Clears all text content from this shape.
     *
     * @return this shape for method chaining
     */
    public PotShape clearText() {
        autoShape.clearText();
        return this;
    }

    // ==================== Font Style Methods ====================

    /**
     * Sets the font family for all text runs within this shape.
     *
     * @param family the font family name (e.g., "Arial")
     * @return this shape for method chaining
     * @throws IllegalArgumentException if {@code family} is {@code null} or blank
     */
    public PotShape fontFamily(String family) {
        ValidationUtils.notBlank(family, "family");
        applyToAllRuns(run -> run.setFontFamily(family));
        return this;
    }

    /**
     * Sets the font size for all text runs within this shape.
     *
     * @param size the font size in points
     * @return this shape for method chaining
     * @throws IllegalArgumentException if {@code size} is not positive
     */
    public PotShape fontSize(double size) {
        ValidationUtils.positive(size, "size");
        applyToAllRuns(run -> run.setFontSize(size));
        return this;
    }

    /**
     * Sets the font color for all text runs within this shape.
     *
     * <p>If the color is {@code null}, the font color is not changed.</p>
     *
     * @param color the color for the text
     * @return this shape for method chaining
     */
    public PotShape fontColor(PotColor color) {
        if (color != null) {
            applyToAllRuns(run -> run.setFontColor(color.toAwtColor()));
        }
        return this;
    }

    /**
     * Applies bold styling to all text runs within this shape.
     *
     * <p>This is a convenience method equivalent to {@code bold(true)}.</p>
     *
     * @return this shape for method chaining
     */
    public PotShape bold() {
        return bold(true);
    }

    /**
     * Enables or disables bold styling for all text runs within this shape.
     *
     * @param bold {@code true} to make the text bold, {@code false} to remove bold styling
     * @return this shape for method chaining
     */
    public PotShape bold(boolean bold) {
        applyToAllRuns(run -> run.setBold(bold));
        return this;
    }

    /**
     * Applies italic styling to all text runs within this shape.
     *
     * <p>This is a convenience method equivalent to {@code italic(true)}.</p>
     *
     * @return this shape for method chaining
     */
    public PotShape italic() {
        return italic(true);
    }

    /**
     * Enables or disables italic styling for all text runs within this shape.
     *
     * @param italic {@code true} to make the text italic, {@code false} to remove italic styling
     * @return this shape for method chaining
     */
    public PotShape italic(boolean italic) {
        applyToAllRuns(run -> run.setItalic(italic));
        return this;
    }

    /**
     * Enables or disables underline styling for all text runs within this shape.
     *
     * @param underline {@code true} to underline the text, {@code false} to remove underlining
     * @return this shape for method chaining
     */
    public PotShape underline(boolean underline) {
        applyToAllRuns(run -> run.setUnderlined(underline));
        return this;
    }

    /**
     * Applies multiple font properties from a {@link PotFont} object to all text runs.
     *
     * <p>Only the non-null properties of the font object are applied. If the font is
     * {@code null}, this method has no effect.</p>
     *
     * @param font the font definition containing family, size, color, and style properties
     * @return this shape for method chaining
     */
    public PotShape font(PotFont font) {
        if (font == null) return this;

        if (font.getFamily() != null) fontFamily(font.getFamily());
        if (font.getSize() > 0) fontSize(font.getSize());
        if (font.getColor() != null) fontColor(font.getColor());
        if (font.isBold()) bold(true);
        if (font.isItalic()) italic(true);
        if (font.isUnderline()) underline(true);

        return this;
    }

    // ==================== Text Alignment Methods ====================

    /**
     * Sets the horizontal text alignment for all paragraphs within this shape.
     *
     * <p>If the alignment is {@code null}, the alignment is not changed.</p>
     *
     * @param alignment the desired horizontal alignment
     * @return this shape for method chaining
     */
    public PotShape align(PotAlignment alignment) {
        if (alignment != null) {
            TextParagraph.TextAlign textAlign = alignment.toPoiTextAlign();
            for (XSLFTextParagraph paragraph : autoShape.getTextParagraphs()) {
                paragraph.setTextAlign(textAlign);
            }
        }
        return this;
    }

    /**
     * Sets the vertical text alignment for this shape.
     *
     * @param alignment the desired vertical alignment
     * @return this shape for method chaining
     * @see #align(PotAlignment)
     */
    public PotShape verticalAlign(PotVerticalAlignment alignment) {
        if (alignment != null) {
            autoShape.setVerticalAlignment(alignment.toPoiAlignment());
        }
        return this;
    }

    // ==================== Text Padding ====================

    /**
     * Sets uniform padding around the text content within this shape.
     *
     * <p>The padding is specified in points and applies equally to all four sides.
     *
     * @param padding the padding value in points for all sides
     * @return this shape for method chaining
     * @see #textPadding(double, double, double, double)
     */
    public PotShape textPadding(double padding) {
        return textPadding(padding, padding, padding, padding);
    }

    /**
     * Sets individual padding values for each side around the text content within this shape.
     *
     * <p>Padding values are specified in points.
     *
     * @param top    the top padding in points
     * @param right  the right padding in points
     * @param bottom the bottom padding in points
     * @param left   the left padding in points
     * @return this shape for method chaining
     * @see #textPadding(double)
     */
    public PotShape textPadding(double top, double right, double bottom, double left) {
        autoShape.setTopInset(top);
        autoShape.setRightInset(right);
        autoShape.setBottomInset(bottom);
        autoShape.setLeftInset(left);
        return this;
    }

    // ==================== Shape Geometry Adjustments ====================

    /**
     * Sets the corner radius for rounded rectangle shapes.
     *
     * <p>This method is primarily intended for shapes of type {@link PotShapeType#ROUNDED_RECTANGLE}.
     * The radius is expressed as a percentage of the shape's half-dimension, typically ranging from 0 to 100.
     * Internally, the value is converted to the underlying XML adjustment value.
     *
     * @param radius the corner radius percentage (typically 0-100)
     * @return this shape for method chaining
     * @see #adjust(String, int)
     */
    public PotShape cornerRadius(double radius) {
        // The adjustment value 'adj' for rounded rectangles is calculated as radius * 50000 / 100.
        XmlUtils.setAdjustValue(autoShape, "adj", (int)(radius * 50000 / 100));
        return this;
    }

    /**
     * Sets a raw adjustment value for this shape's geometry.
     *
     * <p>This is a low-level method for directly manipulating the shape's geometry adjustment handles
     * (e.g., "adj", "adj1", "adj2"). The meaning of the value depends on the shape type and adjustment name.
     *
     * @param name  the adjustment handle name (e.g., "adj", "adj1", "adj2")
     * @param value the adjustment value (typically in the range 0-100000)
     * @return this shape for method chaining
     * @see #cornerRadius(double)
     */
    public PotShape adjust(String name, int value) {
        XmlUtils.setAdjustValue(autoShape, name, value);
        return this;
    }

    // ==================== Duplication ====================

    @Override
    public PotShape duplicate() {
        PotShape copy = parentSlide.addShape(getShapeType());
        copy.at(getX() + PotConstants.DUPLICATE_OFFSET_POINTS, getY() + PotConstants.DUPLICATE_OFFSET_POINTS)
            .size(getWidth(), getHeight())
            .rotate(getRotation());

        // Copy fill
        PotColor fillColor = getFillColor();
        if (fillColor != null) {
            copy.fill(fillColor);
        }

        // Copy border
        PotColor lineColor = getLineColor();
        if (lineColor != null) {
            copy.borderColor(lineColor);
            copy.borderWidth(getLineWidth());
        }

        // Copy text
        String text = getText();
        if (text != null && !text.isEmpty()) {
            copy.setText(text);
        }

        return copy;
    }

    // ==================== Paragraph Access ====================

    /**
     * Returns the number of text paragraphs inside this shape.
     *
     * <p>This method counts the paragraphs defined in the underlying Apache POI shape.
     * A paragraph is a block of text that can have its own alignment and formatting.
     * An empty shape or one with no explicit text may still contain a default empty paragraph.</p>
     *
     * @return the number of text paragraphs, which is always at least zero.
     */
    public int getParagraphCount() {
        return autoShape.getTextParagraphs().size();
    }

    /**
     * Returns the list of raw Apache POI text paragraphs for this shape.
     *
     * <p>This provides direct access to the underlying POI paragraph objects for advanced
     * manipulation not covered by the PotShape API. Modifications to these objects directly
     * affect the shape's content and formatting.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShape shape = slide.addShape(PotShapeType.RECTANGLE).setText("Line 1\nLine 2");
     * List<XSLFTextParagraph> paragraphs = shape.getParagraphs();
     * for (XSLFTextParagraph p : paragraphs) {
     *     p.setTextAlign(TextParagraph.TextAlign.CENTER);
     * }
     * }</pre>
     *
     * @return an unmodifiable list of {@code XSLFTextParagraph} objects.
     * @see XSLFTextParagraph
     */
    public List<XSLFTextParagraph> getParagraphs() {
        return autoShape.getTextParagraphs();
    }

    // ==================== Raw Access ====================

    /**
     * Returns the underlying Apache POI {@code XSLFAutoShape} object.
     *
     * <p>This method provides access to the raw POI object for operations that are not
     * yet supported by the PotShape wrapper. Use with caution, as direct modifications
     * may conflict with the wrapper's state.</p>
     *
     * @return the raw {@code XSLFAutoShape} instance managed by this PotShape.
     */
    public XSLFAutoShape getRawAutoShape() {
        return autoShape;
    }

    // ==================== Overridden Position/Size Methods ====================
    // These methods override the parent class methods to return the more specific
    // PotShape type, enabling fluent chaining.

    @Override
    public PotShape at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotShape setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotShape x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotShape setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotShape y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotShape position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotShape move(double dx, double dy) {
        super.move(dx, dy);
        return this;
    }

    @Override
    public PotShape size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotShape setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotShape width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotShape setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotShape height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotShape scale(double scale) {
        super.scale(scale);
        return this;
    }

    @Override
    public PotShape scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    @Override
    public PotShape rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotShape rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotShape rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotShape flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotShape flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotShape bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotShape sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotShape bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotShape sendBackward() {
        super.sendBackward();
        return this;
    }

    @Override
    public PotShape animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotShape removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotShape hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    @Override
    public PotShape hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotShape hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotShape removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotShape action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotShape removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotShape shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotShape removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotShape reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotShape glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotShape softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotShape removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotShape rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotShape bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotShape material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotShape lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }


    @Override
    public PotShape opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }

    // ==================== Private Helper ====================

    /**
     * Applies a given action to every text run in every paragraph of this shape.
     *
     * <p>This is a convenience method used internally to apply font formatting
     * consistently across all text content.</p>
     *
     * @param action the consumer to apply to each {@code XSLFTextRun}.
     */
    private void applyToAllRuns(java.util.function.Consumer<XSLFTextRun> action) {
        for (XSLFTextParagraph paragraph : autoShape.getTextParagraphs()) {
            for (XSLFTextRun run : paragraph.getTextRuns()) {
                action.accept(run);
            }
        }
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        return String.format("PotShape{uuid=%s, type=%s, position=(%.0f,%.0f), size=(%.0fx%.0f)}",
            uuid, getShapeType(), getX(), getY(), getWidth(), getHeight());
    }
}
