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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a table element within a presentation slide.
 *
 * <p>This class provides a fluent API for creating, formatting, and manipulating tables in PowerPoint slides.
 * It wraps the underlying Apache POI {@link XSLFTable} object and offers methods for setting data, adjusting
 * borders, applying fills, formatting text, and managing rows and columns. All operations return the table
 * instance for method chaining.</p>
 *
 * <h3>Basic Usage Example</h3>
 * <pre>{@code
 * PotTable table = slide.addTable(3, 4)
 *     .at(100, 100)
 *     .size(600, 200);
 *
 * // Set table data
 * table.setData(new String[][] {
 *     {"Name", "Age", "City", "Country"},
 *     {"Alice", "25", "Beijing", "China"},
 *     {"Bob", "30", "London", "UK"}
 * });
 *
 * // Format the header row
 * table.row(0).fill(PotColor.BLUE).fontColor(PotColor.WHITE);
 * }</pre>
 *
 * <h3>Cell Merging Example</h3>
 * <pre>{@code
 * table.mergeCells(0, 0, 0, 3);  // Merge first row across all columns
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotTable extends PotElement {

    // ==================== Fields ====================

    /** The underlying Apache POI table object. */
    private final XSLFTable table;

    // ==================== Constructors ====================

    /**
     * Constructs a new PotTable wrapper for the specified Apache POI table.
     *
     * <p>This constructor is intended for internal use by the library. Applications should create tables
     * using {@link PotSlide#addTable(int, int)}.</p>
     *
     * @param table       the Apache POI XSLFTable object to wrap, must not be null
     * @param parentSlide the parent slide containing this table
     * @param uuid        the unique identifier for this element
     * @throws NullPointerException if the table parameter is null
     */
    PotTable(XSLFTable table, PotSlide parentSlide, String uuid) {
        super(table, parentSlide, uuid);
        this.table = Objects.requireNonNull(table, "table cannot be null");
    }

    // ==================== Basic Properties ====================

    /**
     * Returns the number of rows in the table.
     *
     * @return the current row count
     */
    public int getRowCount() {
        return table.getNumberOfRows();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the current column count
     */
    public int getColumnCount() {
        return table.getNumberOfColumns();
    }

    // ==================== Cell, Row, and Column Access ====================

    /**
     * Returns a wrapper for the cell at the specified row and column indices.
     *
     * <p>The returned {@link PotTableCell} object provides methods for formatting and manipulating
     * the individual cell.</p>
     *
     * @param row the zero-based row index
     * @param col the zero-based column index
     * @return a PotTableCell wrapper for the specified cell
     * @throws IndexOutOfBoundsException if either index is out of range
     */
    public PotTableCell cell(int row, int col) {
        validateRowIndex(row);
        validateColumnIndex(col);
        XSLFTableCell poiCell = table.getCell(row, col);
        return new PotTableCell(poiCell, this, row, col);
    }

    /**
     * Returns a wrapper for the row at the specified index.
     *
     * <p>The returned {@link PotTableRow} object provides methods for applying formatting
     * operations to all cells in the row.</p>
     *
     * @param row the zero-based row index
     * @return a PotTableRow wrapper for the specified row
     * @throws IndexOutOfBoundsException if the row index is out of range
     */
    public PotTableRow row(int row) {
        validateRowIndex(row);
        return new PotTableRow(this, row);
    }

    /**
     * Returns a wrapper for the column at the specified index.
     *
     * <p>The returned {@link PotTableColumn} object provides methods for applying formatting
     * operations to all cells in the column.</p>
     *
     * @param col the zero-based column index
     * @return a PotTableColumn wrapper for the specified column
     * @throws IndexOutOfBoundsException if the column index is out of range
     */
    public PotTableColumn column(int col) {
        validateColumnIndex(col);
        return new PotTableColumn(this, col);
    }

    // ==================== Data Management ====================

    /**
     * Populates the table with data from a two-dimensional string array.
     *
     * <p>This method sets the text content of table cells using the provided data array.
     * If the data array has fewer rows or columns than the table, only the overlapping
     * cells are updated. Extra rows or columns in the data array are ignored.</p>
     *
     * @param data a two-dimensional string array containing the cell values
     * @return this table for method chaining
     */
    public PotTable setData(String[][] data) {
        if (data == null) return this;

        for (int r = 0; r < data.length && r < getRowCount(); r++) {
            for (int c = 0; c < data[r].length && c < getColumnCount(); c++) {
                cell(r, c).setText(data[r][c]);
            }
        }
        return this;
    }

    /**
     * Retrieves all text content from the table as a two-dimensional string array.
     *
     * <p>The returned array has dimensions matching the current row and column counts.
     * Empty cells are represented as empty strings or null, depending on the underlying
     * POI implementation.</p>
     *
     * @return a two-dimensional string array containing all cell text values
     */
    public String[][] getData() {
        int rows = getRowCount();
        int cols = getColumnCount();
        String[][] data = new String[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r][c] = cell(r, c).getText();
            }
        }
        return data;
    }

    // ==================== Row Operations ====================

    /**
     * Appends a new row to the bottom of the table.
     *
     * <p>The new row will have the same number of cells as existing rows.
     * All cells in the new row are initially empty.</p>
     *
     * @return this table for method chaining
     */
    public PotTable addRow() {
        XSLFTableRow newRow = table.addRow();
        // Ensure the new row has the correct number of cells
        for (int c = 0; c < getColumnCount(); c++) {
            newRow.addCell();
        }
        return this;
    }

    /**
     * Inserts a new row at the specified position.
     *
     * <p>Existing rows at or after the insertion index are shifted down.
     * The new row will have the same number of cells as existing rows.</p>
     *
     * @param index the zero-based insertion position
     * @return this table for method chaining
     * @throws IndexOutOfBoundsException if the index is less than 0 or greater than the row count
     */
    public PotTable insertRow(int index) {
        ValidationUtils.validInsertIndex(index, getRowCount(), "index");
        // POI doesn't provide insertRow, so we manipulate XML directly
        XmlUtils.insertTableRow(table, index);
        return this;
    }

    /**
     * Removes the row at the specified index.
     *
     * @param index the zero-based index of the row to remove
     * @return this table for method chaining
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public PotTable removeRow(int index) {
        validateRowIndex(index);
        // POI doesn't provide removeRow, so we manipulate XML directly
        XmlUtils.removeTableRow(table, index);
        return this;
    }

    /**
     * Sets the height of a specific row.
     *
     * @param index  the zero-based row index
     * @param height the new height in points
     * @return this table for method chaining
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws IllegalArgumentException  if the height is not positive
     */
    public PotTable setRowHeight(int index, double height) {
        validateRowIndex(index);
        ValidationUtils.positive(height, "height");
        XSLFTableRow row = table.getRows().get(index);
        row.setHeight(height);
        return this;
    }

    /**
     * Returns the height of the row at the specified index.
     *
     * @param index the zero-based row index
     * @return the row height in points
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public double getRowHeight(int index) {
        validateRowIndex(index);
        XSLFTableRow row = table.getRows().get(index);
        return row.getHeight();
    }

    // ==================== Column Operations ====================

    /**
     * Appends a new column to the right side of the table.
     *
     * <p>A new cell is added to every existing row in the table.</p>
     *
     * @return this table for method chaining
     */
    public PotTable addColumn() {
        for (XSLFTableRow row : table.getRows()) {
            row.addCell();
        }
        return this;
    }

    /**
     * Inserts a new column at the specified position.
     *
     * <p>Existing columns at or after the insertion index are shifted right.
     * A new cell is inserted at the specified position in every row.</p>
     *
     * @param index the zero-based insertion position
     * @return this table for method chaining
     * @throws IndexOutOfBoundsException if the index is less than 0 or greater than the column count
     */
    public PotTable insertColumn(int index) {
        ValidationUtils.validInsertIndex(index, getColumnCount(), "index");
        // POI doesn't provide insertColumn, so we manipulate XML directly
        XmlUtils.insertTableColumn(table, index);
        return this;
    }

    /**
     * Removes the column at the specified index.
     *
     * @param index the zero-based index of the column to remove
     * @return this table for method chaining
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public PotTable removeColumn(int index) {
        validateColumnIndex(index);
        // POI doesn't provide removeColumn, so we manipulate XML directly
        XmlUtils.removeTableColumn(table, index);
        return this;
    }

    /**
     * Sets the width of a specific column.
     *
     * @param index the zero-based column index
     * @param width the new width in points
     * @return this table for method chaining
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws IllegalArgumentException  if the width is not positive
     */
    public PotTable setColumnWidth(int index, double width) {
        validateColumnIndex(index);
        ValidationUtils.positive(width, "width");
        table.setColumnWidth(index, width);
        return this;
    }

    /**
     * Returns the width of the column at the specified index.
     *
     * @param index the zero-based column index
     * @return the column width in points
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public double getColumnWidth(int index) {
        validateColumnIndex(index);
        return table.getColumnWidth(index);
    }

    // ==================== Cell Merging ====================

    /**
     * Merges a rectangular range of cells into a single cell.
     *
     * <p>The merged cell will span from the start row/column to the end row/column inclusive.
     * The content of the top-left cell in the range is preserved; other cell contents are discarded.</p>
     *
     * @param startRow the zero-based starting row index
     * @param startCol the zero-based starting column index
     * @param endRow   the zero-based ending row index
     * @param endCol   the zero-based ending column index
     * @return this table for method chaining
     * @throws IndexOutOfBoundsException if any index is out of range
     * @throws IllegalArgumentException  if startRow > endRow or startCol > endCol
     */
    public PotTable mergeCells(int startRow, int startCol, int endRow, int endCol) {
        validateRowIndex(startRow);
        validateRowIndex(endRow);
        validateColumnIndex(startCol);
        validateColumnIndex(endCol);

        if (startRow > endRow || startCol > endCol) {
            throw PotException.invalidParameter("cell range", "start must be <= end");
        }

        table.mergeCells(startRow, endRow, startCol, endCol);
        return this;
    }

    // ==================== Border Operations ====================

    /**
     * Applies the specified border style to all cells in the table.
     *
     * <p>This method sets all four borders (top, bottom, left, right) of every cell
     * to the specified border style.</p>
     *
     * @param border the border style to apply
     * @return this table for method chaining
     */
    public PotTable border(PotBorder border) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).border(border);
            }
        }
        return this;
    }

    /**
     * Applies the specified border style to all interior cell borders.
     *
     * <p>This method sets borders between adjacent cells while leaving the outer
     * perimeter of the table unchanged. Specifically, it applies:
     * <ul>
     *   <li>Top border to all cells except those in the first row</li>
     *   <li>Left border to all cells except those in the first column</li>
     *   <li>Bottom border to all cells except those in the last row</li>
     *   <li>Right border to all cells except those in the last column</li>
     * </ul></p>
     *
     * @param border the border style to apply
     * @return this table for method chaining
     */
    public PotTable innerBorder(PotBorder border) {
        int rows = getRowCount();
        int cols = getColumnCount();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                PotTableCell tableCell = cell(r, c);
                // Apply top border if not in first row
                if (r > 0) {
                    tableCell.borderTop(border);
                }
                // Apply left border if not in first column
                if (c > 0) {
                    tableCell.borderLeft(border);
                }
                // Apply bottom border if not in last row
                if (r < rows - 1) {
                    tableCell.borderBottom(border);
                }
                // Apply right border if not in last column
                if (c < cols - 1) {
                    tableCell.borderRight(border);
                }
            }
        }
        return this;
    }

    /**
     * Applies the specified border style to the outer perimeter of the table.
     *
     * <p>This method sets borders only on the outermost cells of the table:
     * <ul>
     *   <li>Top border to all cells in the first row</li>
     *   <li>Left border to all cells in the first column</li>
     *   <li>Bottom border to all cells in the last row</li>
     *   <li>Right border to all cells in the last column</li>
     * </ul></p>
     *
     * @param border the border style to apply
     * @return this table for method chaining
     */
    public PotTable outerBorder(PotBorder border) {
        int rows = getRowCount();
        int cols = getColumnCount();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                PotTableCell tableCell = cell(r, c);
                // Apply top border to first row
                if (r == 0) {
                    tableCell.borderTop(border);
                }
                // Apply left border to first column
                if (c == 0) {
                    tableCell.borderLeft(border);
                }
                // Apply bottom border to last row
                if (r == rows - 1) {
                    tableCell.borderBottom(border);
                }
                // Apply right border to last column
                if (c == cols - 1) {
                    tableCell.borderRight(border);
                }
            }
        }
        return this;
    }

    /**
     * Removes all borders from every cell in the table.
     *
     * <p>This method clears any border style applied to all cells, making the table appear borderless.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.noBorder();
     * }</pre>
     *
     * @return this {@code PotTable} instance for method chaining
     * @see PotTableCell#noBorder()
     */
    public PotTable noBorder() {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).noBorder();
            }
        }
        return this;
    }

    // ==================== Fill Operations ====================

    /**
     * Sets the background fill color for all cells in the table.
     *
     * <p>This method applies the specified color to the background of every cell, overriding any previous fill settings.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.fill(PotColor.LIGHT_GRAY);
     * }</pre>
     *
     * @param color the color to use for the cell background fill
     * @return this {@code PotTable} instance for method chaining
     * @see PotTableCell#fill(PotColor)
     */
    public PotTable fill(PotColor color) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).fill(color);
            }
        }
        return this;
    }

    /**
     * Sets the background fill color for all cells in a specific row.
     *
     * <p>This method applies the specified color to the background of every cell in the given row.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Fill the header row with blue
     * table.fillRow(0, PotColor.BLUE);
     * }</pre>
     *
     * @param row   the zero-based index of the row to fill
     * @param color the color to use for the cell background fill
     * @return this {@code PotTable} instance for method chaining
     * @throws IndexOutOfBoundsException if the row index is invalid
     * @see #fillColumn(int, PotColor)
     * @see #fillRange(int, int, int, int, PotColor)
     */
    public PotTable fillRow(int row, PotColor color) {
        validateRowIndex(row);
        int cols = getColumnCount();
        for (int c = 0; c < cols; c++) {
            cell(row, c).fill(color);
        }
        return this;
    }

    /**
     * Sets the background fill color for all cells in a specific column.
     *
     * <p>This method applies the specified color to the background of every cell in the given column.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Highlight the first column
     * table.fillColumn(0, PotColor.YELLOW);
     * }</pre>
     *
     * @param col   the zero-based index of the column to fill
     * @param color the color to use for the cell background fill
     * @return this {@code PotTable} instance for method chaining
     * @throws IndexOutOfBoundsException if the column index is invalid
     * @see #fillRow(int, PotColor)
     * @see #fillRange(int, int, int, int, PotColor)
     */
    public PotTable fillColumn(int col, PotColor color) {
        validateColumnIndex(col);
        int rows = getRowCount();
        for (int r = 0; r < rows; r++) {
            cell(r, col).fill(color);
        }
        return this;
    }

    /**
     * Sets the background fill color for a rectangular range of cells.
     *
     * <p>This method applies the specified color to the background of all cells within the inclusive range
     * defined by the start and end row/column indices.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Fill a 2x2 block starting at cell (1,1)
     * table.fillRange(1, 1, 2, 2, PotColor.GREEN);
     * }</pre>
     *
     * @param startRow the zero-based starting row index (inclusive)
     * @param startCol the zero-based starting column index (inclusive)
     * @param endRow   the zero-based ending row index (inclusive)
     * @param endCol   the zero-based ending column index (inclusive)
     * @param color    the color to use for the cell background fill
     * @return this {@code PotTable} instance for method chaining
     * @throws IndexOutOfBoundsException if any row or column index is invalid
     * @throws IllegalArgumentException  if {@code startRow > endRow} or {@code startCol > endCol}
     * @see #fillRow(int, PotColor)
     * @see #fillColumn(int, PotColor)
     */
    public PotTable fillRange(int startRow, int startCol, int endRow, int endCol, PotColor color) {
        validateRowIndex(startRow);
        validateRowIndex(endRow);
        validateColumnIndex(startCol);
        validateColumnIndex(endCol);
        if (startRow > endRow || startCol > endCol) {
            throw PotException.invalidParameter("range", "start must be <= end");
        }

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                cell(r, c).fill(color);
            }
        }
        return this;
    }

    /**
     * Applies alternating background colors to rows.
     *
     * <p>This method sets the background fill for rows in a striped pattern. Even-indexed rows (0, 2, 4, ...)
     * receive {@code color1}, and odd-indexed rows receive {@code color2}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.alternateRowColors(PotColor.WHITE, PotColor.LIGHT_BLUE);
     * }</pre>
     *
     * @param color1 the color for even-indexed rows (starting from row 0)
     * @param color2 the color for odd-indexed rows
     * @return this {@code PotTable} instance for method chaining
     * @see #alternateColumnColors(PotColor, PotColor)
     */
    public PotTable alternateRowColors(PotColor color1, PotColor color2) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            PotColor color = (r % 2 == 0) ? color1 : color2;
            for (int c = 0; c < cols; c++) {
                cell(r, c).fill(color);
            }
        }
        return this;
    }

    /**
     * Applies alternating background colors to columns.
     *
     * <p>This method sets the background fill for columns in a striped pattern. Even-indexed columns (0, 2, 4, ...)
     * receive {@code color1}, and odd-indexed columns receive {@code color2}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.alternateColumnColors(PotColor.WHITE, PotColor.LIGHT_GRAY);
     * }</pre>
     *
     * @param color1 the color for even-indexed columns (starting from column 0)
     * @param color2 the color for odd-indexed columns
     * @return this {@code PotTable} instance for method chaining
     * @see #alternateRowColors(PotColor, PotColor)
     */
    public PotTable alternateColumnColors(PotColor color1, PotColor color2) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int c = 0; c < cols; c++) {
            PotColor color = (c % 2 == 0) ? color1 : color2;
            for (int r = 0; r < rows; r++) {
                cell(r, c).fill(color);
            }
        }
        return this;
    }

    // ==================== Font Operations ====================

    /**
     * Sets the font family for text in all cells.
     *
     * <p>This method applies the specified font family to the text content of every cell in the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.fontFamily("Arial");
     * }</pre>
     *
     * @param family the name of the font family (e.g., "Calibri", "Times New Roman")
     * @return this {@code PotTable} instance for method chaining
     * @see PotTableCell#fontFamily(String)
     */
    public PotTable fontFamily(String family) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).fontFamily(family);
            }
        }
        return this;
    }

    /**
     * Sets the font size for text in all cells.
     *
     * <p>This method applies the specified font size (in points) to the text content of every cell in the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.fontSize(12.0);
     * }</pre>
     *
     * @param size the font size in points
     * @return this {@code PotTable} instance for method chaining
     * @throws IllegalArgumentException if {@code size} is not positive
     * @see PotTableCell#fontSize(double)
     */
    public PotTable fontSize(double size) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).fontSize(size);
            }
        }
        return this;
    }

    /**
     * Sets the font color for text in all cells.
     *
     * <p>This method applies the specified color to the text content of every cell in the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.fontColor(PotColor.BLACK);
     * }</pre>
     *
     * @param color the color to use for the text
     * @return this {@code PotTable} instance for method chaining
     * @see PotTableCell#fontColor(PotColor)
     */
    public PotTable fontColor(PotColor color) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).fontColor(color);
            }
        }
        return this;
    }

    // ==================== Alignment Operations ====================

    /**
     * Sets the horizontal text alignment for all cells.
     *
     * <p>This method applies the specified horizontal alignment to the text content of every cell in the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.align(PotAlignment.CENTER);
     * }</pre>
     *
     * @param alignment the horizontal alignment to apply
     * @return this {@code PotTable} instance for method chaining
     * @see PotTableCell#align(PotAlignment)
     */
    public PotTable align(PotAlignment alignment) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).align(alignment);
            }
        }
        return this;
    }

    /**
     * Sets the vertical text alignment for all cells.
     *
     * <p>This method applies the specified vertical alignment to the text content of every cell in the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * table.verticalAlign(PotVerticalAlignment.MIDDLE);
     * }</pre>
     *
     * @param alignment the vertical alignment to apply
     * @return this {@code PotTable} instance for method chaining
     * @see PotTableCell#verticalAlign(PotVerticalAlignment)
     */
    public PotTable verticalAlign(PotVerticalAlignment alignment) {
        int rows = getRowCount();
        int cols = getColumnCount();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cell(r, c).verticalAlign(alignment);
            }
        }
        return this;
    }

    // ==================== Element Overrides ====================

    @Override
    public PotTable duplicate() {
        PotTable copy = parentSlide.addTable(getRowCount(), getColumnCount());
        copy.at(getX() + PotConstants.DUPLICATE_OFFSET_POINTS, getY() + PotConstants.DUPLICATE_OFFSET_POINTS);
        copy.size(getWidth(), getHeight());
        copy.setData(getData());
        return copy;
    }

    // ==================== Internal Access =================

    /**
     * Returns the underlying Apache POI XSLFTable object.
     *
     * <p>This method provides direct access to the internal POI object for advanced operations not covered by the PotTable API.
     * Modifications made directly to the returned object may affect the behavior of this PotTable instance.</p>
     *
     * @return the underlying XSLFTable object.
     */
    public XSLFTable getRawTable() {
        return table;
    }

    // ==================== Validation ====================

    private void validateRowIndex(int row) {
        ValidationUtils.validIndex(row, getRowCount(), "row");
    }

    private void validateColumnIndex(int col) {
        ValidationUtils.validIndex(col, getColumnCount(), "col");
    }

    // ==================== Fluent Overrides from PotElement ====================
    // These methods override the parent class methods to return PotTable for fluent chaining.

    @Override
    public PotTable at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotTable setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotTable x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotTable setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotTable y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotTable position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotTable move(double dx, double dy) {
        super.move(dx, dy);
        return this;
    }

    @Override
    public PotTable size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotTable setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotTable width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotTable setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotTable height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotTable scale(double scale) {
        super.scale(scale);
        return this;
    }

    @Override
    public PotTable scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    @Override
    public PotTable rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotTable rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotTable rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotTable flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotTable flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotTable bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotTable sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotTable bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotTable sendBackward() {
        super.sendBackward();
        return this;
    }

    @Override
    public PotTable animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotTable removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotTable hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    @Override
    public PotTable hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotTable hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotTable removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotTable action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotTable removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotTable shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotTable removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotTable reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotTable glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotTable softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotTable removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotTable rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotTable bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotTable material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotTable lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }


    @Override
    public PotTable opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        return String.format("PotTable{uuid=%s, rows=%d, cols=%d, position=(%.0f,%.0f)}",
            uuid, getRowCount(), getColumnCount(), getX(), getY());
    }

    // ==================== Row Wrapper Class ====================

    /**
     * Provides a fluent interface for formatting a single row in a table.
     *
     * <p>This class allows operations to be applied to all cells in a specific row. All methods return the
     * same {@code PotTableRow} instance for method chaining.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Format the header row
     * table.row(0)
     *     .fill(PotColor.BLUE)
     *     .fontColor(PotColor.WHITE)
     *     .bold();
     * }</pre>
     *
     * @since 1.0
     */
    public static class PotTableRow {
        private final PotTable table;
        private final int rowIndex;

        /**
         * Constructs a new row wrapper for the specified table and row index.
         *
         * <p>This constructor is intended for internal use. Applications should obtain row wrappers via
         * {@link PotTable#row(int)}.</p>
         *
         * @param table    the parent table
         * @param rowIndex the zero-based row index
         */
        PotTableRow(PotTable table, int rowIndex) {
            this.table = table;
            this.rowIndex = rowIndex;
        }

        /**
         * Sets the background fill color for all cells in this row.
         *
         * @param color the color to use for the cell background fill
         * @return this {@code PotTableRow} instance for method chaining
         */
        public PotTableRow fill(PotColor color) {
            for (int c = 0; c < table.getColumnCount(); c++) {
                table.cell(rowIndex, c).fill(color);
            }
            return this;
        }

        /**
         * Sets the font color for text in all cells in this row.
         *
         * @param color the color to use for the text
         * @return this {@code PotTableRow} instance for method chaining
         */
        public PotTableRow fontColor(PotColor color) {
            for (int c = 0; c < table.getColumnCount(); c++) {
                table.cell(rowIndex, c).fontColor(color);
            }
            return this;
        }

        /**
         * Sets the font size for text in all cells in this row.
         *
         * @param size the font size in points
         * @return this {@code PotTableRow} instance for method chaining
         * @throws IllegalArgumentException if {@code size} is not positive
         */
        public PotTableRow fontSize(double size) {
            for (int c = 0; c < table.getColumnCount(); c++) {
                table.cell(rowIndex, c).fontSize(size);
            }
            return this;
        }

        /**
         * Applies bold formatting to text in all cells in this row.
         *
         * <p>This is equivalent to {@code bold(true)}.</p>
         *
         * @return this {@code PotTableRow} instance for method chaining
         * @see #bold(boolean)
         */
        public PotTableRow bold() {
            return bold(true);
        }

        /**
         * Sets the bold formatting state for text in all cells in this row.
         *
         * @param bold {@code true} to apply bold formatting, {@code false} to remove it
         * @return this {@code PotTableRow} instance for method chaining
         */
        public PotTableRow bold(boolean bold) {
            for (int c = 0; c < table.getColumnCount(); c++) {
                table.cell(rowIndex, c).bold(bold);
            }
            return this;
        }

        /**
         * Sets the horizontal text alignment for all cells in this row.
         *
         * @param alignment the horizontal alignment to apply
         * @return this {@code PotTableRow} instance for method chaining
         */
        public PotTableRow align(PotAlignment alignment) {
            for (int c = 0; c < table.getColumnCount(); c++) {
                table.cell(rowIndex, c).align(alignment);
            }
            return this;
        }

        /**
         * Sets the height of this row.
         *
         * @param height the new row height in points
         * @return this {@code PotTableRow} instance for method chaining
         * @throws IllegalArgumentException if {@code height} is not positive
         */
        public PotTableRow height(double height) {
            table.setRowHeight(rowIndex, height);
            return this;
        }

        /**
         * Returns a list of all cells in this row.
         *
         * <p>The returned list contains {@link PotTableCell} wrappers for each cell in the row,
         * in column order (left to right).</p>
         *
         * @return a list of all cells in this row
         */
        public List<PotTableCell> cells() {
            List<PotTableCell> cells = new ArrayList<>();
            for (int c = 0; c < table.getColumnCount(); c++) {
                cells.add(table.cell(rowIndex, c));
            }
            return cells;
        }
    }

    // ==================== Column Wrapper Class ====================

    /**
     * Provides a fluent interface for formatting a single column in a table.
     *
     * <p>This class allows operations to be applied to all cells in a specific column. All methods return the
     * same {@code PotTableColumn} instance for method chaining.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Format the first column
     * table.column(0)
     *     .fill(PotColor.YELLOW)
     *     .align(PotAlignment.RIGHT);
     * }</pre>
     *
     * @since 1.0
     */
    public static class PotTableColumn {
        private final PotTable table;
        private final int colIndex;

        /**
         * Constructs a new column wrapper for the specified table and column index.
         *
         * <p>This constructor is intended for internal use. Applications should obtain column wrappers via
         * {@link PotTable#column(int)}.</p>
         *
         * @param table    the parent table
         * @param colIndex the zero-based column index
         */
        PotTableColumn(PotTable table, int colIndex) {
            this.table = table;
            this.colIndex = colIndex;
        }

        /**
         * Sets the background fill color for all cells in this column.
         *
         * @param color the color to use for the cell background fill
         * @return this {@code PotTableColumn} instance for method chaining
         */
        public PotTableColumn fill(PotColor color) {
            for (int r = 0; r < table.getRowCount(); r++) {
                table.cell(r, colIndex).fill(color);
            }
            return this;
        }

        /**
         * Sets the font color for text in all cells in this column.
         *
         * @param color the color to use for the text
         * @return this {@code PotTableColumn} instance for method chaining
         */
        public PotTableColumn fontColor(PotColor color) {
            for (int r = 0; r < table.getRowCount(); r++) {
                table.cell(r, colIndex).fontColor(color);
            }
            return this;
        }

        /**
         * Sets the font size for text in all cells in this column.
         *
         * @param size the font size in points
         * @return this {@code PotTableColumn} instance for method chaining
         * @throws IllegalArgumentException if {@code size} is not positive
         */
        public PotTableColumn fontSize(double size) {
            for (int r = 0; r < table.getRowCount(); r++) {
                table.cell(r, colIndex).fontSize(size);
            }
            return this;
        }

        /**
         * Applies bold formatting to text in all cells in this column.
         *
         * <p>This is equivalent to {@code bold(true)}.</p>
         *
         * @return this {@code PotTableColumn} instance for method chaining
         * @see #bold(boolean)
         */
        public PotTableColumn bold() {
            return bold(true);
        }

        /**
         * Sets the bold formatting state for text in all cells in this column.
         *
         * @param bold {@code true} to apply bold formatting, {@code false} to remove it
         * @return this {@code PotTableColumn} instance for method chaining
         */
        public PotTableColumn bold(boolean bold) {
            for (int r = 0; r < table.getRowCount(); r++) {
                table.cell(r, colIndex).bold(bold);
            }
            return this;
        }

        /**
         * Sets the horizontal text alignment for all cells in this column.
         *
         * @param alignment the horizontal alignment to apply
         * @return this {@code PotTableColumn} instance for method chaining
         */
        public PotTableColumn align(PotAlignment alignment) {
            for (int r = 0; r < table.getRowCount(); r++) {
                table.cell(r, colIndex).align(alignment);
            }
            return this;
        }

        /**
         * Sets the width of this column.
         *
         * @param width the new column width in points
         * @return this {@code PotTableColumn} instance for method chaining
         * @throws IllegalArgumentException if {@code width} is not positive
         */
        public PotTableColumn width(double width) {
            table.setColumnWidth(colIndex, width);
            return this;
        }

        /**
         * Returns a list of all cells in this column.
         *
         * <p>The returned list contains {@link PotTableCell} wrappers for each cell in the column,
         * in row order (top to bottom).</p>
         *
         * @return a list of all cells in this column
         */
        public List<PotTableCell> cells() {
            List<PotTableCell> cells = new ArrayList<>();
            for (int r = 0; r < table.getRowCount(); r++) {
                cells.add(table.cell(r, colIndex));
            }
            return cells;
        }
    }
}
