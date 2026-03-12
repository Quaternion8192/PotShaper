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

import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

/**
 * Represents a text box shape within a PowerPoint slide, providing a fluent API for text and style manipulation.
 *
 * <p>This class encapsulates an Apache POI {@link XSLFTextBox} and offers methods to set text content,
 * configure font properties (family, size, color, weight, style), adjust paragraph formatting (alignment,
 * spacing, indentation, bullets), apply fills and borders, and control layout properties such as padding,
 * word wrap, and auto-fitting. All style methods return the instance itself, enabling method chaining.
 * The underlying POI object can be accessed via {@link #getRawTextBox()} for advanced operations.</p>
 *
 * <h3>Basic Usage Example</h3>
 * <pre>{@code
 * PotTextBox textBox = slide.addTextBox("Hello World")
 *     .at(100, 100)
 *     .size(400, 50)
 *     .fontFamily("Arial")
 *     .fontSize(24)
 *     .fontColor(PotColor.RED)
 *     .bold()
 *     .align(PotAlignment.CENTER);
 * }</pre>
 *
 * <h3>Paragraph Formatting Example</h3>
 * <pre>{@code
 * textBox.lineSpacing(1.5)
 *     .paragraphSpacing(12)
 *     .bullet(true)
 *     .indent(20);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotTextBox extends PotElement {

    // ==================== Fields ====================

    /** The underlying Apache POI text box shape. */
    private final XSLFTextBox textBox;

    // ==================== Constructors ====================

    /**
     * Constructs a new {@code PotTextBox} wrapper for the specified POI text box.
     *
     * <p>This constructor is intended for internal use by the library. The text box, parent slide,
     * and UUID must not be null.</p>
     *
     * @param textBox      the Apache POI {@link XSLFTextBox} to wrap
     * @param parentSlide  the parent slide containing this text box
     * @param uuid         the unique identifier for this element
     * @throws NullPointerException if {@code textBox} is {@code null}
     */
    PotTextBox(XSLFTextBox textBox, PotSlide parentSlide, String uuid) {
        super(textBox, parentSlide, uuid);
        this.textBox = Objects.requireNonNull(textBox, "textBox cannot be null");
    }

    // ==================== Text Content Methods ====================

    /**
     * Sets the plain text content of the entire text box, replacing any existing text.
     *
     * <p>If the provided text is {@code null}, the text box content is set to an empty string.
     * This operation affects all paragraphs and text runs within the box.</p>
     *
     * @param text  the new text content; may be {@code null} which is treated as an empty string
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox setText(String text) {
        textBox.setText(text != null ? text : "");
        return this;
    }

    /**
     * Returns the plain text content of the entire text box.
     *
     * <p>The returned string is the concatenation of all text runs across all paragraphs,
     * as provided by the underlying POI object.</p>
     *
     * @return the current text content of the text box; never {@code null} but may be empty
     */
    public String getText() {
        return textBox.getText();
    }

    /**
     * Appends the specified text to the end of the text box content, adding a new line after the appended text.
     *
     * <p>This is a convenience method equivalent to {@code appendText(text, true)}. If the text is {@code null},
     * no operation is performed.</p>
     *
     * @param text  the text to append; may be {@code null} in which case no change occurs
     * @return this {@code PotTextBox} instance for method chaining
     * @see #appendText(String, boolean)
     */
    public PotTextBox appendText(String text) {
        return appendText(text, true);
    }

    /**
     * Appends the specified text to the end of the text box content, optionally starting a new line.
     *
     * <p>The text is appended to the last paragraph, or a new paragraph is created if necessary.
     * If {@code newLine} is {@code true}, a line break is added after the appended text, effectively
     * starting a new line within the same paragraph. If the text is {@code null}, no operation is performed.</p>
     *
     * @param text     the text to append; may be {@code null} in which case no change occurs
     * @param newLine  if {@code true}, a line break is inserted after the appended text
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox appendText(String text, boolean newLine) {
        if (text != null) {
            textBox.appendText(text, newLine);
        }
        return this;
    }

    /**
     * Clears all text content from the text box.
     *
     * <p>This method removes all text runs and paragraphs, leaving the text box empty.
     * Formatting properties (font, alignment, etc.) are not reset.</p>
     *
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox clearText() {
        textBox.clearText();
        return this;
    }

    // ==================== Font Style Methods ====================

    /**
     * Sets the font family for all text runs within the text box.
     *
     * <p>The family name must be a non-blank string (e.g., "Arial", "Calibri"). The change is applied
     * uniformly to every text run in every paragraph.</p>
     *
     * @param family  the font family name; must not be {@code null} or blank
     * @return this {@code PotTextBox} instance for method chaining
     * @throws IllegalArgumentException if {@code family} is {@code null} or blank
     */
    public PotTextBox fontFamily(String family) {
        ValidationUtils.notBlank(family, "family");
        applyToAllRuns(run -> run.setFontFamily(family));
        return this;
    }

    /**
     * Sets the font size in points for all text runs within the text box.
     *
     * <p>The size must be a positive number. The change is applied uniformly to every text run
     * in every paragraph.</p>
     *
     * @param size  the font size in points; must be greater than zero
     * @return this {@code PotTextBox} instance for method chaining
     * @throws IllegalArgumentException if {@code size} is not positive
     */
    public PotTextBox fontSize(double size) {
        ValidationUtils.positive(size, "size");
        applyToAllRuns(run -> run.setFontSize(size));
        return this;
    }

    /**
     * Sets the font color for all text runs within the text box using a {@link PotColor}.
     *
     * <p>If the provided color is {@code null}, no change is made. The change is applied uniformly
     * to every text run in every paragraph.</p>
     *
     * @param color  the font color; may be {@code null} to leave the color unchanged
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox fontColor(PotColor color) {
        if (color != null) {
            applyToAllRuns(run -> run.setFontColor(color.toAwtColor()));
        }
        return this;
    }

    /**
     * Sets the font color for all text runs within the text box using a standard AWT {@link Color}.
     *
     * <p>If the provided color is {@code null}, no change is made. The change is applied uniformly
     * to every text run in every paragraph.</p>
     *
     * @param color  the AWT color; may be {@code null} to leave the color unchanged
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox fontColor(Color color) {
        if (color != null) {
            applyToAllRuns(run -> run.setFontColor(color));
        }
        return this;
    }

    /**
     * Enables bold font weight for all text runs within the text box.
     *
     * <p>This is a convenience method equivalent to {@code bold(true)}.</p>
     *
     * @return this {@code PotTextBox} instance for method chaining
     * @see #bold(boolean)
     */
    public PotTextBox bold() {
        return bold(true);
    }

    /**
     * Sets the bold font weight for all text runs within the text box.
     *
     * <p>The change is applied uniformly to every text run in every paragraph.</p>
     *
     * @param bold  {@code true} to enable bold, {@code false} to disable
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox bold(boolean bold) {
        applyToAllRuns(run -> run.setBold(bold));
        return this;
    }

    /**
     * Enables italic font style for all text runs within the text box.
     *
     * <p>This is a convenience method equivalent to {@code italic(true)}.</p>
     *
     * @return this {@code PotTextBox} instance for method chaining
     * @see #italic(boolean)
     */
    public PotTextBox italic() {
        return italic(true);
    }

    /**
     * Sets the italic font style for all text runs within the text box.
     *
     * <p>The change is applied uniformly to every text run in every paragraph.</p>
     *
     * @param italic  {@code true} to enable italic, {@code false} to disable
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox italic(boolean italic) {
        applyToAllRuns(run -> run.setItalic(italic));
        return this;
    }

    /**
     * Enables underline for all text runs within the text box.
     *
     * <p>This is a convenience method equivalent to {@code underline(true)}.</p>
     *
     * @return this {@code PotTextBox} instance for method chaining
     * @see #underline(boolean)
     */
    public PotTextBox underline() {
        return underline(true);
    }

    /**
     * Sets the underline style for all text runs within the text box.
     *
     * <p>The change is applied uniformly to every text run in every paragraph.</p>
     *
     * @param underline  {@code true} to enable underline, {@code false} to disable
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox underline(boolean underline) {
        applyToAllRuns(run -> run.setUnderlined(underline));
        return this;
    }

    /**
     * Enables strikethrough for all text runs within the text box.
     *
     * <p>This is a convenience method equivalent to {@code strikethrough(true)}.</p>
     *
     * @return this {@code PotTextBox} instance for method chaining
     * @see #strikethrough(boolean)
     */
    public PotTextBox strikethrough() {
        return strikethrough(true);
    }

    /**
     * Sets the strikethrough style for all text runs within the text box.
     *
     * <p>The change is applied uniformly to every text run in every paragraph.</p>
     *
     * @param strikethrough  {@code true} to enable strikethrough, {@code false} to disable
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox strikethrough(boolean strikethrough) {
        applyToAllRuns(run -> run.setStrikethrough(strikethrough));
        return this;
    }

    /**
     * Applies a comprehensive font configuration from a {@link PotFont} object.
     *
     * <p>This method sets all font properties (family, size, color, bold, italic, underline, strikethrough)
     * that are defined (non-null or non-default) in the provided {@code PotFont}. Properties that are
     * {@code null} or zero in the font object are skipped, leaving the existing settings unchanged.</p>
     *
     * @param font  the font configuration object; may be {@code null}, in which case no changes are made
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox font(PotFont font) {
        if (font == null) return this;

        if (font.getFamily() != null) fontFamily(font.getFamily());
        if (font.getSize() > 0) fontSize(font.getSize());
        if (font.getColor() != null) fontColor(font.getColor());
        if (font.isBold()) bold(true);
        if (font.isItalic()) italic(true);
        if (font.isUnderline()) underline(true);
        if (font.isStrikethrough()) strikethrough(true);

        return this;
    }

    // ==================== Paragraph Alignment Methods ====================

    /**
     * Sets the horizontal text alignment for all paragraphs within the text box.
     *
     * <p>If the provided alignment is {@code null}, no change is made. The alignment is applied
     * to every paragraph in the text box.</p>
     *
     * @param alignment  the desired horizontal alignment; may be {@code null}
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox align(PotAlignment alignment) {
        if (alignment != null) {
            TextParagraph.TextAlign textAlign = alignment.toPoiTextAlign();
            for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
                paragraph.setTextAlign(textAlign);
            }
        }
        return this;
    }

    /**
     * Sets the vertical alignment of text within the text box shape.
     *
     * <p>This controls how text is positioned vertically (top, middle, bottom) inside the shape's bounds.
     * If the provided alignment is {@code null}, no change is made.</p>
     *
     * @param alignment  the desired vertical alignment; may be {@code null}
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox verticalAlign(PotVerticalAlignment alignment) {
        if (alignment != null) {
            textBox.setVerticalAlignment(alignment.toPoiAlignment());
        }
        return this;
    }

    // ==================== Paragraph Spacing and Indentation Methods ====================

    /**
     * Sets the line spacing (leading) for all paragraphs within the text box.
     *
     * <p>The spacing is specified as a multiplier of the normal line height. For example, a value of 1.5
     * results in one-and-a-half line spacing. The value must be positive. The change is applied to every
     * paragraph in the text box.</p>
     *
     * @param spacing  the line spacing multiplier; must be greater than zero
     * @return this {@code PotTextBox} instance for method chaining
     * @throws IllegalArgumentException if {@code spacing} is not positive
     */
    public PotTextBox lineSpacing(double spacing) {
        ValidationUtils.positive(spacing, "spacing");
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setLineSpacing(spacing * 100); // POI expects percentage
        }
        return this;
    }

    /**
     * Sets the space before each paragraph for all paragraphs within the text box.
     *
     * <p>The spacing is specified in points. The change is applied to every paragraph in the text box.</p>
     *
     * @param spacing  the space before each paragraph in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox spaceBefore(double spacing) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setSpaceBefore(spacing);
        }
        return this;
    }

    /**
     * Sets the space after each paragraph for all paragraphs within the text box.
     *
     * <p>The spacing is specified in points. The change is applied to every paragraph in the text box.</p>
     *
     * @param spacing  the space after each paragraph in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox spaceAfter(double spacing) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setSpaceAfter(spacing);
        }
        return this;
    }

    /**
     * Sets the left indent for all paragraphs within the text box.
     *
     * <p>The indent is specified in points. The change is applied to every paragraph in the text box.</p>
     *
     * @param indent  the left indent in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox indent(double indent) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setIndent(indent);
        }
        return this;
    }

    /**
     * Sets the left margin for all paragraphs within the text box.
     *
     * <p>The margin is specified in points. The change is applied to every paragraph in the text box.</p>
     *
     * @param margin  the left margin in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox leftMargin(double margin) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setLeftMargin(margin);
        }
        return this;
    }

    /**
     * Sets the right margin for all paragraphs within the text box.
     *
     * <p>The margin is specified in points. The change is applied to every paragraph in the text box.</p>
     *
     * @param margin  the right margin in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox rightMargin(double margin) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setRightMargin(margin);
        }
        return this;
    }

    /**
     * Enables or disables bullet points for all paragraphs within the text box.
     *
     * <p>The change is applied to every paragraph in the text box.</p>
     *
     * @param bullet  {@code true} to enable bullets, {@code false} to disable
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox bullet(boolean bullet) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setBullet(bullet);
        }
        return this;
    }

    /**
     * Sets a custom bullet character for all paragraphs within the text box and enables bullets.
     *
     * <p>This method enables bullets and sets the specified character as the bullet symbol for every
     * paragraph. If the character is {@code null} or empty, the default bullet character is used.</p>
     *
     * @param character  the custom bullet character (e.g., "", ""); may be {@code null}
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox bulletCharacter(String character) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            paragraph.setBullet(true);
            paragraph.setBulletCharacter(character);
        }
        return this;
    }

    /**
     * Sets the bullet color for all paragraphs within the text box.
     *
     * <p>If the provided color is {@code null}, no change is made. The change is applied to every
     * paragraph in the text box.</p>
     *
     * @param color  the bullet color; may be {@code null}
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox bulletColor(PotColor color) {
        if (color != null) {
            for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
                paragraph.setBulletFontColor(color.toAwtColor());
            }
        }
        return this;
    }

    // ==================== Fill Methods ====================

    /**
     * Sets a solid fill color for the text box shape.
     *
     * <p>If the provided color is {@code null}, the fill is removed (set to transparent).</p>
     *
     * @param color  the solid fill color; {@code null} removes the fill
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox fill(PotColor color) {
        if (color != null) {
            textBox.setFillColor(color.toAwtColor());
        } else {
            textBox.setFillColor(null);
        }
        return this;
    }

    /**
     * Applies a complex fill (gradient, image, pattern, or solid) to the text box shape.
     *
     * <p>The fill is configured according to the properties of the provided {@link PotFill} object.
     * If the object is {@code null}, the fill is removed (equivalent to {@link #noFill()}).
     * Supported fill types are gradient, image, pattern, and solid color.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Solid fill
     * textBox.fill(PotFill.solid(PotColor.BLUE));
     *
     * // Gradient fill
     * textBox.fill(PotFill.gradient(PotGradient.linear(45, PotColor.RED, PotColor.YELLOW)));
     *
     * // Image fill (from byte array)
     * byte[] imageBytes = ...;
     * textBox.fill(PotFill.image(imageBytes));
     * }</pre>
     *
     * @param fill  the fill configuration object; may be {@code null} to remove the fill
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox fill(PotFill fill) {
        if (fill == null) {
            return noFill();
        }

        if (fill.isGradient() && fill.getGradient() != null) {
            XmlUtils.applyGradientFill(textBox, fill.getGradient());
        } else if (fill.isImage() && fill.getImageData() != null) {
            try {
                XSLFPictureData picData = parentSlide.getPresentation()
                    .addPictureData(fill.getImageData(), org.apache.poi.sl.usermodel.PictureData.PictureType.PNG);
                String rId = textBox.getSheet().getPackagePart()
                    .addRelationship(
                        picData.getPackagePart().getPartName(),
                        org.apache.poi.openxml4j.opc.TargetMode.INTERNAL,
                        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"
                    ).getId();
                XmlUtils.applyPictureFill(textBox, rId);
            } catch (Exception e) {
                PotLogger.warn(PotTextBox.class, "fill", "Failed to apply picture fill", e);
            }
        } else if (fill.isPattern()) {
            XmlUtils.applyPatternFill(textBox, fill.getPatternType(), fill.getColor(),
                fill.getBackgroundColor());
        } else if (fill.getColor() != null) {
            textBox.setFillColor(fill.getColor().toAwtColor());
        }
        return this;
    }

    /**
     * Removes any fill from the text box shape, making it transparent.
     *
     * <p>This is equivalent to {@code fill((PotColor) null)}.</p>
     *
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox noFill() {
        textBox.setFillColor(null);
        return this;
    }

    // ==================== Border Methods ====================

    /**
     * Applies a border configuration to the text box shape.
     *
     * <p>This method sets the border color, width, dash style, and cap style according to the properties
     * of the provided {@link PotBorder} object. If the object is {@code null}, the border is removed
     * (equivalent to {@link #noBorder()}).</p>
     *
     * @param border  the border configuration object; may be {@code null} to remove the border
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox border(PotBorder border) {
        if (border == null) {
            return noBorder();
        }

        if (border.getColor() != null) {
            textBox.setLineColor(border.getColor().toAwtColor());
        }
        if (border.getWidth() > 0) {
            textBox.setLineWidth(border.getWidth());
        }
        if (border.getDashStyle() != null) {
            textBox.setLineDash(border.getDashStyle().toPoiDash());
        }
        if (border.getCapStyle() != null) {
            textBox.setLineCap(border.getCapStyle().toPoiCap());
        }

        return this;
    }

    /**
     * Sets the border color of the text box shape.
     *
     * <p>If the provided color is {@code null}, no change is made.</p>
     *
     * @param color  the border color; may be {@code null}
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox borderColor(PotColor color) {
        if (color != null) {
            textBox.setLineColor(color.toAwtColor());
        }
        return this;
    }

    /**
     * Sets the border width (line width) of the text box shape.
     *
     * <p>The width is specified in points. A width of zero effectively removes the border.</p>
     *
     * @param width  the border width in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox borderWidth(double width) {
        textBox.setLineWidth(width);
        return this;
    }

    /**
     * Removes the border from the text box shape by setting its width to zero.
     *
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox noBorder() {
        textBox.setLineWidth(0);
        return this;
    }

    // ==================== Padding Methods ====================

    /**
     * Sets uniform padding (inset) on all four sides of the text box.
     *
     * <p>The padding is the space between the text content and the inner edges of the shape.
     * The value is specified in points.</p>
     *
     * @param padding  the padding value applied to all sides, in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox padding(double padding) {
        return padding(padding, padding, padding, padding);
    }

    /**
     * Sets individual padding (inset) values for each side of the text box.
     *
     * <p>The padding is the space between the text content and the inner edges of the shape.
     * Values are specified in points.</p>
     *
     * @param top     the top padding in points
     * @param right   the right padding in points
     * @param bottom  the bottom padding in points
     * @param left    the left padding in points
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox padding(double top, double right, double bottom, double left) {
        textBox.setTopInset(top);
        textBox.setRightInset(right);
        textBox.setBottomInset(bottom);
        textBox.setLeftInset(left);
        return this;
    }

    /**
     * Returns the top padding (inset) of the text box.
     *
     * @return the top padding in points
     */
    public double getTopInset() {
        return textBox.getTopInset();
    }

    /**
     * Returns the right padding (inset) of the text box.
     *
     * @return the right padding in points
     */
    public double getRightInset() {
        return textBox.getRightInset();
    }

    /**
     * Returns the bottom padding (inset) of the text box.
     *
     * @return the bottom padding in points
     */
    public double getBottomInset() {
        return textBox.getBottomInset();
    }

    /**
     * Returns the left padding (inset) of the text box.
     *
     * @return the left padding in points
     */
    public double getLeftInset() {
        return textBox.getLeftInset();
    }

    // ==================== Text Layout Methods ====================

    /**
     * Enables or disables word wrapping within the text box.
     *
     * <p>When enabled, text that exceeds the width of the shape wraps to the next line.
     * When disabled, text may be clipped or overflow the shape.</p>
     *
     * @param wrap  {@code true} to enable word wrapping, {@code false} to disable
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox wordWrap(boolean wrap) {
        textBox.setWordWrap(wrap);
        return this;
    }

    /**
     * Sets the text direction (writing direction) for the text box.
     *
     * <p>This controls the flow of text, such as left-to-right, right-to-left, or vertical.
     * If the provided direction is {@code null}, no change is made.</p>
     *
     * @param direction  the desired text direction; may be {@code null}
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox textDirection(PotDirection direction) {
        if (direction != null) {
            textBox.setTextDirection(direction.toPoiTextDirection());
        }
        return this;
    }

    /**
     * Enables or disables automatic text fitting within the text box.
     *
     * <p>When enabled, the font size may be automatically adjusted to fit the text within the shape's bounds.
     * When disabled, the font size remains as set.</p>
     *
     * @param autoFit  {@code true} to enable automatic fitting, {@code false} to disable
     * @return this {@code PotTextBox} instance for method chaining
     */
    public PotTextBox autoFit(boolean autoFit) {
        if (autoFit) {
            textBox.setTextAutofit(XSLFTextShape.TextAutofit.NORMAL);
        } else {
            textBox.setTextAutofit(XSLFTextShape.TextAutofit.NONE);
        }
        return this;
    }

    // ==================== Paragraph Access Methods ====================

    /**
     * Returns the number of paragraphs in the text box.
     *
     * <p>A paragraph is a block of text separated by line breaks or new paragraph markers.</p>
     *
     * @return the count of paragraphs
     */
    public int getParagraphCount() {
        return textBox.getTextParagraphs().size();
    }

    /**
     * Returns the underlying POI paragraph objects for advanced manipulation.
     *
     * <p>This method provides direct access to the Apache POI {@code XSLFTextParagraph} list.
     * Use with caution, as modifications may affect the integrity of the wrapper.</p>
     *
     * @return a list of POI text paragraph objects
     */
    public List<XSLFTextParagraph> getParagraphs() {
        return textBox.getTextParagraphs();
    }

    /**
     * Adds a new empty paragraph to the end of the text box.
     *
     * <p>The new paragraph is created with default formatting. To add text to it, use
     * {@link #appendText(String, boolean)} or manipulate the returned paragraph directly.</p>
     *
     * @return the newly created POI text paragraph
     */
    public XSLFTextParagraph addParagraph() {
        return textBox.addNewTextParagraph();
    }

    // ==================== Duplication Method ====================

    @Override
    public PotTextBox duplicate() {
        PotTextBox copy = parentSlide.addTextBox(getText());
        copy.at(getX() + PotConstants.DUPLICATE_OFFSET_POINTS, getY() + PotConstants.DUPLICATE_OFFSET_POINTS)
            .size(getWidth(), getHeight())
            .rotate(getRotation());

        // Copy fill
        java.awt.Color fillColor = textBox.getFillColor();
        if (fillColor != null) {
            copy.textBox.setFillColor(fillColor);
        }

        // Copy border
        java.awt.Color lineColor = textBox.getLineColor();
        if (lineColor != null) {
            copy.textBox.setLineColor(lineColor);
            copy.textBox.setLineWidth(textBox.getLineWidth());
        }

        // Copy padding
        copy.textBox.setTopInset(textBox.getTopInset());
        copy.textBox.setRightInset(textBox.getRightInset());
        copy.textBox.setBottomInset(textBox.getBottomInset());
        copy.textBox.setLeftInset(textBox.getLeftInset());

        // Copy vertical alignment
        copy.textBox.setVerticalAlignment(textBox.getVerticalAlignment());

        // Copy paragraph and run formatting
        List<XSLFTextParagraph> srcParas = textBox.getTextParagraphs();
        List<XSLFTextParagraph> dstParas = copy.textBox.getTextParagraphs();
        for (int i = 0; i < srcParas.size() && i < dstParas.size(); i++) {
            XSLFTextParagraph srcPara = srcParas.get(i);
            XSLFTextParagraph dstPara = dstParas.get(i);
            dstPara.setTextAlign(srcPara.getTextAlign());
            dstPara.setLineSpacing(srcPara.getLineSpacing());
            dstPara.setSpaceBefore(srcPara.getSpaceBefore());
            dstPara.setSpaceAfter(srcPara.getSpaceAfter());

            // Copy text run properties
            List<XSLFTextRun> srcRuns = srcPara.getTextRuns();
            List<XSLFTextRun> dstRuns = dstPara.getTextRuns();
            for (int j = 0; j < srcRuns.size() && j < dstRuns.size(); j++) {
                XSLFTextRun srcRun = srcRuns.get(j);
                XSLFTextRun dstRun = dstRuns.get(j);
                if (srcRun.getFontSize() != null) {
                    dstRun.setFontSize(srcRun.getFontSize());
                }
                dstRun.setBold(srcRun.isBold());
                dstRun.setItalic(srcRun.isItalic());
                dstRun.setUnderlined(srcRun.isUnderlined());
                dstRun.setStrikethrough(srcRun.isStrikethrough());
                if (srcRun.getFontColor() != null) {
                    dstRun.setFontColor(srcRun.getFontColor());
                }
                if (srcRun.getFontFamily() != null) {
                    dstRun.setFontFamily(srcRun.getFontFamily());
                }
            }
        }

        return copy;
    }

    // ==================== Raw Access Method ====================

    /**
     * Returns the underlying Apache POI {@code XSLFTextBox} object for advanced operations.
     *
     * <p>This method provides direct access to the POI text box. Use with caution, as modifications
     * may affect the integrity of the wrapper and are not guaranteed to be compatible with future versions.</p>
     *
     * @return the underlying POI text box object
     */
    public XSLFTextBox getRawTextBox() {
        return textBox;
    }

    // ==================== Position and Size Overrides ====================

    @Override
    public PotTextBox at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotTextBox setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotTextBox x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotTextBox setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotTextBox y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotTextBox position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotTextBox move(double dx, double dy) {
        super.move(dx, dy);
        return this;
    }

    @Override
    public PotTextBox size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotTextBox setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotTextBox width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotTextBox setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotTextBox height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotTextBox scale(double scale) {
        super.scale(scale);
        return this;
    }

    @Override
    public PotTextBox scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    @Override
    public PotTextBox rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotTextBox rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotTextBox rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotTextBox flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotTextBox flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotTextBox bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotTextBox sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotTextBox bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotTextBox sendBackward() {
        super.sendBackward();
        return this;
    }

    @Override
    public PotTextBox animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotTextBox removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotTextBox hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    @Override
    public PotTextBox hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotTextBox hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotTextBox removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotTextBox action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotTextBox removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotTextBox shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotTextBox removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotTextBox reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotTextBox glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotTextBox softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotTextBox removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotTextBox rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotTextBox bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotTextBox material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotTextBox lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }

    @Override
    public PotTextBox opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }

    // ==================== Internal Helper Methods ====================

    /**
     * Applies a given action to every text run in the text box.
     *
     * <p>This internal helper iterates through all paragraphs and their text runs,
     * applying the specified consumer action to each run. It is used to batch-apply
     * font formatting changes.</p>
     *
     * @param action  the action to apply to each text run
     */
    private void applyToAllRuns(java.util.function.Consumer<XSLFTextRun> action) {
        for (XSLFTextParagraph paragraph : textBox.getTextParagraphs()) {
            for (XSLFTextRun run : paragraph.getTextRuns()) {
                action.accept(run);
            }
        }
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        String text = getText();
        if (text != null && text.length() > 20) {
            text = text.substring(0, 17) + "...";
        }
        return String.format("PotTextBox{uuid=%s, text='%s', position=(%.0f,%.0f)}",
            uuid, text, getX(), getY());
    }
}
