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
import java.util.Objects;

/**
 * Represents a single cell within a {@link PotTable}, providing a fluent API for styling and content manipulation.
 *
 * <p>This class wraps an Apache POI {@link XSLFTableCell} and offers a higher-level, chainable interface for
 * common table cell operations such as setting text, applying fonts, colors, borders, alignment, and padding.
 * All style modifications are applied directly to the underlying POI object. Methods that return {@code this}
 * enable method chaining for convenient configuration.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * PotTableCell cell = table.cell(0, 0);
 * cell.setText("Header")
 *     .fill(PotColor.BLUE)
 *     .fontColor(PotColor.WHITE)
 *     .bold()
 *     .align(PotAlignment.CENTER)
 *     .verticalAlign(PotVerticalAlignment.MIDDLE);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotTableCell {

    // ==================== Fields ====================

    /** The underlying Apache POI table cell object. */
    private final XSLFTableCell cell;

    /** The parent {@link PotTable} containing this cell. */
    private final PotTable table;

    /** The zero-based row index of this cell within the table. */
    private final int row;

    /** The zero-based column index of this cell within the table. */
    private final int col;

    // ==================== Constructors ====================

    /**
     * Constructs a new wrapper for an Apache POI table cell.
     *
     * <p>This constructor is intended for internal use by the {@link PotTable} class. It associates the
     * POI cell with its logical position and parent table.</p>
     *
     * @param cell  the underlying Apache POI {@link XSLFTableCell}; must not be {@code null}
     * @param table the parent {@link PotTable} containing this cell; must not be {@code null}
     * @param row   the zero-based row index of this cell
     * @param col   the zero-based column index of this cell
     * @throws NullPointerException if {@code cell} or {@code table} is {@code null}
     */
    PotTableCell(XSLFTableCell cell, PotTable table, int row, int col) {
        this.cell = Objects.requireNonNull(cell, "cell cannot be null");
        this.table = Objects.requireNonNull(table, "table cannot be null");
        this.row = row;
        this.col = col;
    }

    // ==================== Position & Context ====================

    /**
     * Returns the zero-based row index of this cell within its table.
     *
     * @return the zero-based row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the zero-based column index of this cell within its table.
     *
     * @return the zero-based column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns the parent table that contains this cell.
     *
     * @return the parent {@link PotTable}
     */
    public PotTable getTable() {
        return table;
    }

    // ==================== Text Content ====================

    /**
     * Sets the plain text content of the cell, replacing any existing text.
     *
     * <p>If the provided text is {@code null}, the cell's text is set to an empty string.</p>
     *
     * @param text the new text content for the cell; may be {@code null}
     * @return this cell for method chaining
     */
    public PotTableCell setText(String text) {
        cell.setText(text != null ? text : "");
        return this;
    }

    /**
     * Returns the current plain text content of the cell.
     *
     * @return the cell's text content; may be an empty string but never {@code null}
     */
    public String getText() {
        return cell.getText();
    }

    /**
     * Appends text to the end of the cell's current content.
     *
     * <p>If the provided text is {@code null}, this method has no effect. The {@code newLine} parameter
     * controls whether the appended text starts on a new line (paragraph).</p>
     *
     * @param text    the text to append; may be {@code null}
     * @param newLine {@code true} to start the appended text on a new line, {@code false} to continue on the same line
     * @return this cell for method chaining
     */
    public PotTableCell appendText(String text, boolean newLine) {
        if (text != null) {
            cell.appendText(text, newLine);
        }
        return this;
    }

    /**
     * Removes all text content from the cell.
     *
     * @return this cell for method chaining
     */
    public PotTableCell clearText() {
        cell.clearText();
        return this;
    }

    // ==================== Font Styling ====================

    /**
     * Sets the font family for all text runs within the cell.
     *
     * @param family the font family name (e.g., "Arial"); {@code null} values are ignored
     * @return this cell for method chaining
     */
    public PotTableCell fontFamily(String family) {
        applyToAllRuns(run -> run.setFontFamily(family));
        return this;
    }

    /**
     * Sets the font size for all text runs within the cell.
     *
     * @param size the font size in points
     * @return this cell for method chaining
     */
    public PotTableCell fontSize(double size) {
        applyToAllRuns(run -> run.setFontSize(size));
        return this;
    }

    /**
     * Sets the font color for all text runs within the cell using a {@link PotColor}.
     *
     * @param color the desired font color; {@code null} values are ignored
     * @return this cell for method chaining
     */
    public PotTableCell fontColor(PotColor color) {
        if (color != null) {
            applyToAllRuns(run -> run.setFontColor(color.toAwtColor()));
        }
        return this;
    }

    /**
     * Sets the font color for all text runs within the cell using a standard AWT {@link Color}.
     *
     * @param color the desired AWT font color; {@code null} values are ignored
     * @return this cell for method chaining
     */
    public PotTableCell fontColor(Color color) {
        if (color != null) {
            applyToAllRuns(run -> run.setFontColor(color));
        }
        return this;
    }

    /**
     * Applies bold styling to all text runs within the cell.
     *
     * <p>This is a convenience method equivalent to {@code bold(true)}.</p>
     *
     * @return this cell for method chaining
     * @see #bold(boolean)
     */
    public PotTableCell bold() {
        return bold(true);
    }

    /**
     * Enables or disables bold styling for all text runs within the cell.
     *
     * @param bold {@code true} to apply bold styling, {@code false} to remove it
     * @return this cell for method chaining
     */
    public PotTableCell bold(boolean bold) {
        applyToAllRuns(run -> run.setBold(bold));
        return this;
    }

    /**
     * Applies italic styling to all text runs within the cell.
     *
     * <p>This is a convenience method equivalent to {@code italic(true)}.</p>
     *
     * @return this cell for method chaining
     * @see #italic(boolean)
     */
    public PotTableCell italic() {
        return italic(true);
    }

    /**
     * Enables or disables italic styling for all text runs within the cell.
     *
     * @param italic {@code true} to apply italic styling, {@code false} to remove it
     * @return this cell for method chaining
     */
    public PotTableCell italic(boolean italic) {
        applyToAllRuns(run -> run.setItalic(italic));
        return this;
    }

    /**
     * Enables or disables underline styling for all text runs within the cell.
     *
     * @param underline {@code true} to apply underline styling, {@code false} to remove it
     * @return this cell for method chaining
     */
    public PotTableCell underline(boolean underline) {
        applyToAllRuns(run -> run.setUnderlined(underline));
        return this;
    }

    /**
     * Applies multiple font properties from a {@link PotFont} object to all text runs within the cell.
     *
     * <p>Only the non-null and applicable properties of the provided font are applied. This includes
     * font family, size, color, bold, italic, and underline styles.</p>
     *
     * @param font the font specification containing the properties to apply; {@code null} values are ignored
     * @return this cell for method chaining
     */
    public PotTableCell font(PotFont font) {
        if (font == null) return this;

        if (font.getFamily() != null) fontFamily(font.getFamily());
        if (font.getSize() > 0) fontSize(font.getSize());
        if (font.getColor() != null) fontColor(font.getColor());
        if (font.isBold()) bold(true);
        if (font.isItalic()) italic(true);
        if (font.isUnderline()) underline(true);

        return this;
    }

    // ==================== Text Alignment ====================

    /**
     * Sets the horizontal text alignment for all paragraphs within the cell.
     *
     * @param alignment the desired horizontal alignment; {@code null} values are ignored
     * @return this cell for method chaining
     */
    public PotTableCell align(PotAlignment alignment) {
        if (alignment != null) {
            TextParagraph.TextAlign textAlign = alignment.toPoiTextAlign();
            for (XSLFTextParagraph paragraph : cell.getTextParagraphs()) {
                paragraph.setTextAlign(textAlign);
            }
        }
        return this;
    }

    /**
     * Sets the vertical alignment of text within the cell.
     *
     * @param alignment the desired vertical alignment; {@code null} values are ignored
     * @return this cell for method chaining
     */
    public PotTableCell verticalAlign(PotVerticalAlignment alignment) {
        if (alignment != null) {
            cell.setVerticalAlignment(alignment.toPoiAlignment());
        }
        return this;
    }

    // ==================== Cell Fill ====================

    /**
     * Sets the background fill color of the cell using a {@link PotColor}.
     *
     * <p>If the provided color is {@code null}, the fill is effectively removed (set to transparent).</p>
     *
     * @param color the desired fill color; {@code null} removes the fill
     * @return this cell for method chaining
     */
    public PotTableCell fill(PotColor color) {
        if (color != null) {
            cell.setFillColor(color.toAwtColor());
        } else {
            cell.setFillColor(null);
        }
        return this;
    }

    /**
     * Sets the background fill color of the cell using a standard AWT {@link Color}.
     *
     * @param color the desired AWT fill color; may be {@code null} for no fill
     * @return this cell for method chaining
     */
    public PotTableCell fill(Color color) {
        cell.setFillColor(color);
        return this;
    }

    /**
     * Removes the background fill from the cell, making it transparent.
     *
     * @return this cell for method chaining
     */
    public PotTableCell noFill() {
        cell.setFillColor(null);
        return this;
    }

    /**
     * Returns the current background fill color of the cell as a {@link PotColor}.
     *
     * @return the fill color, or {@code null} if the cell has no fill
     */
    public PotColor getFillColor() {
        Color color = cell.getFillColor();
        return color != null ? PotColor.fromAwtColor(color) : null;
    }

    // ==================== Borders ====================

    /**
     * Applies the same border style to all four sides of the cell.
     *
     * <p>If the provided border is {@code null}, or its color is {@code null}, or its width is not positive,
     * the border on all sides is removed (width set to zero).</p>
     *
     * @param border the border specification to apply uniformly
     * @return this cell for method chaining
     */
    public PotTableCell border(PotBorder border) {
        borderTop(border);
        borderRight(border);
        borderBottom(border);
        borderLeft(border);
        return this;
    }

    /**
     * Applies a border style to the top edge of the cell.
     *
     * <p>If the provided border is {@code null}, or its color is {@code null}, or its width is not positive,
     * the top border is removed (width set to zero).</p>
     *
     * @param border the border specification for the top edge
     * @return this cell for method chaining
     */
    public PotTableCell borderTop(PotBorder border) {
        if (border != null && border.getColor() != null && border.getWidth() > 0) {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.top, border.getWidth());
            cell.setBorderColor(XSLFTableCell.BorderEdge.top, border.getColor().toAwtColor());
            // Note: setBorderStyle not available in this POI version
        } else {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.top, 0);
        }
        return this;
    }

    /**
     * Applies a border style to the right edge of the cell.
     *
     * <p>If the provided border is {@code null}, or its color is {@code null}, or its width is not positive,
     * the right border is removed (width set to zero).</p>
     *
     * @param border the border specification for the right edge
     * @return this cell for method chaining
     */
    public PotTableCell borderRight(PotBorder border) {
        if (border != null && border.getColor() != null && border.getWidth() > 0) {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.right, border.getWidth());
            cell.setBorderColor(XSLFTableCell.BorderEdge.right, border.getColor().toAwtColor());
            // Note: setBorderStyle not available in this POI version
        } else {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.right, 0);
        }
        return this;
    }

    /**
     * Applies a border style to the bottom edge of the cell.
     *
     * <p>If the provided border is {@code null}, or its color is {@code null}, or its width is not positive,
     * the bottom border is removed (width set to zero).</p>
     *
     * @param border the border specification for the bottom edge
     * @return this cell for method chaining
     */
    public PotTableCell borderBottom(PotBorder border) {
        if (border != null && border.getColor() != null && border.getWidth() > 0) {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.bottom, border.getWidth());
            cell.setBorderColor(XSLFTableCell.BorderEdge.bottom, border.getColor().toAwtColor());
            // Note: setBorderStyle not available in this POI version
        } else {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.bottom, 0);
        }
        return this;
    }

    /**
     * Applies a border style to the left edge of the cell.
     *
     * <p>If the provided border is {@code null}, or its color is {@code null}, or its width is not positive,
     * the left border is removed (width set to zero).</p>
     *
     * @param border the border specification for the left edge
     * @return this cell for method chaining
     */
    public PotTableCell borderLeft(PotBorder border) {
        if (border != null && border.getColor() != null && border.getWidth() > 0) {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.left, border.getWidth());
            cell.setBorderColor(XSLFTableCell.BorderEdge.left, border.getColor().toAwtColor());
            // Note: setBorderStyle not available in this POI version
        } else {
            cell.setBorderWidth(XSLFTableCell.BorderEdge.left, 0);
        }
        return this;
    }

    /**
     * Removes all borders from the cell by setting their widths to zero.
     *
     * @return this cell for method chaining
     */
    public PotTableCell noBorder() {
        cell.setBorderWidth(XSLFTableCell.BorderEdge.top, 0);
        cell.setBorderWidth(XSLFTableCell.BorderEdge.right, 0);
        cell.setBorderWidth(XSLFTableCell.BorderEdge.bottom, 0);
        cell.setBorderWidth(XSLFTableCell.BorderEdge.left, 0);
        return this;
    }

    // ==================== Padding ====================

    /**
     * Applies uniform padding to all four sides of the cell's content area.
     *
     * @param padding the padding value (in points) to apply to top, right, bottom, and left
     * @return this cell for method chaining
     */
    public PotTableCell padding(double padding) {
        return padding(padding, padding, padding, padding);
    }

    /**
     * Sets individual padding values for each side of the cell.
     *
     * @param top    the top padding in points
     * @param right  the right padding in points
     * @param bottom the bottom padding in points
     * @param left   the left padding in points
     * @return this cell for method chaining
     */
    public PotTableCell padding(double top, double right, double bottom, double left) {
        cell.setTopInset(top);
        cell.setRightInset(right);
        cell.setBottomInset(bottom);
        cell.setLeftInset(left);
        return this;
    }

    // ==================== Cell Merging ====================

    /**
     * Indicates whether this cell is part of a merged cell region.
     *
     * <p>A cell is considered merged if it is not the top-left cell of a merged region.
     * In such cases, its content and styling are typically controlled by the top-left cell.</p>
     *
     * @return {@code true} if this cell is merged (i.e., not the primary cell of a merge), {@code false} otherwise
     */
    public boolean isMerged() {
        return cell.isMerged();
    }

    /**
     * Returns the number of rows spanned by this cell.
     *
     * <p>For a non-merged cell, this returns 1. For the top-left cell of a merged region,
     * it returns the total number of rows covered by the merge.</p>
     *
     * @return the row span (always at least 1)
     */
    public int getRowSpan() {
        return cell.getRowSpan();
    }

    /**
     * Returns the number of columns spanned by this cell.
     *
     * <p>For a non-merged cell, this returns 1. For the top-left cell of a merged region,
     * it returns the total number of columns covered by the merge. Note: The underlying POI API
     * may not directly expose column span; this implementation returns 1 as a default.</p>
     *
     * @return the column span (always at least 1)
     */
    public int getColSpan() {
        // POI may not expose getColSpan directly, return 1 as default
        return 1;
    }

    // ==================== Text Direction ====================

    /**
     * Sets the text direction (writing mode) for the cell.
     *
     * <p>This controls whether text flows horizontally (left-to-right or right-to-left) or vertically.
     * If the provided direction is {@code null}, the method does nothing and returns immediately.</p>
     *
     * @param direction the desired text direction; {@code null} values are ignored
     * @return this cell for method chaining
     */
    public PotTableCell textDirection(PotDirection direction) {
        if (direction != null) {
            cell.setTextDirection(direction.toPoiTextDirection());
        }
        return this;
    }

    // ==================== Low-Level Access ====================

    /**
     * Returns the underlying Apache POI {@link XSLFTableCell} object.
     *
     * <p>This method provides direct access to the POI object for advanced operations not covered
     * by the wrapper API. Modifications made directly to the returned object will affect this cell.</p>
     *
     * @return the underlying {@link XSLFTableCell}
     */
    public XSLFTableCell getRawCell() {
        return cell;
    }

    // ==================== Internal Helper ====================

    /**
     * Applies a given action to every text run within the cell.
     *
     * <p>This helper method iterates through all paragraphs and their text runs, executing the
     * provided consumer on each run. It is used internally to apply font styling uniformly.</p>
     *
     * @param action the operation to perform on each {@link XSLFTextRun}
     */
    private void applyToAllRuns(java.util.function.Consumer<XSLFTextRun> action) {
        for (XSLFTextParagraph paragraph : cell.getTextParagraphs()) {
            for (XSLFTextRun run : paragraph.getTextRuns()) {
                action.accept(run);
            }
        }
    }

    // ==================== Object Overrides ====================

    /**
     * Returns a string representation of the cell for debugging purposes.
     *
     * <p>The representation includes the cell's row and column indices and a truncated version
     * of its text content (if longer than 20 characters).</p>
     *
     * @return a concise, informative string representation of the cell
     */
    @Override
    public String toString() {
        String text = getText();
        if (text != null && text.length() > 20) {
            text = text.substring(0, 17) + "...";
        }
        return String.format("PotTableCell{row=%d, col=%d, text='%s'}",
            row, col, text);
    }
}
