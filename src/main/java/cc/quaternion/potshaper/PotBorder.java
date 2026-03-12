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

import org.apache.poi.sl.usermodel.StrokeStyle;

import java.util.Objects;

/**
 * Represents a border style for shapes, including color, width, dash pattern, and line caps.
 *
 * <p>This class is immutable; all modification methods return a new instance. It provides
 * factory methods for common border styles and constants for predefined borders. The border
 * width is specified in points, and opacity is a value between 0.0 (transparent) and 1.0 (opaque).
 * A border with a width of zero or less is considered "none" and will not be rendered.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * // Create a solid black border
 * PotBorder border = PotBorder.solid(2.0, PotColor.BLACK);
 *
 * // Create a dashed red border
 * PotBorder dashedRed = PotBorder.dashed(1.5, PotColor.RED);
 *
 * // Modify an existing border
 * PotBorder modified = border.withColor(PotColor.BLUE).withWidth(3.0);
 *
 * // Use a predefined border
 * PotBorder thinGray = PotBorder.GRAY_THIN;
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotBorder {

    // ==================== Predefined Borders ====================

    /** A border with zero width, representing no visible border. */
    public static final PotBorder NONE = new PotBorder(PotColor.BLACK, 0, DashStyle.SOLID, CapStyle.FLAT, JoinStyle.ROUND, 1.0);

    /** A solid black border with a width of 1 point. */
    public static final PotBorder BLACK_THIN = solid(1, PotColor.BLACK);

    /** A solid black border with a width of 2 points. */
    public static final PotBorder BLACK_MEDIUM = solid(2, PotColor.BLACK);

    /** A solid black border with a width of 3 points. */
    public static final PotBorder BLACK_THICK = solid(3, PotColor.BLACK);

    /** A solid gray border with a width of 1 point. */
    public static final PotBorder GRAY_THIN = solid(1, PotColor.GRAY);

    // ==================== Enumerations ====================

    /**
     * Defines the dash pattern for a border line.
     *
     * <p>These styles correspond to common line dash patterns used in graphics and presentations.
     * The {@code SYSTEM_*} variants are intended to match the host system's default rendering.</p>
     *
     * @since 1.0
     */
    public enum DashStyle {
        /** A solid, unbroken line. */
        SOLID,
        /** A line consisting of dots. */
        DOT,
        /** A line consisting of short dashes. */
        DASH,
        /** A line consisting of long dashes. */
        LONG_DASH,
        /** A repeating pattern of dash followed by dot. */
        DASH_DOT,
        /** A repeating pattern of long dash followed by dot. */
        LONG_DASH_DOT,
        /** A repeating pattern of long dash followed by two dots. */
        LONG_DASH_DOT_DOT,
        /** A system-default dot pattern. */
        SYSTEM_DOT,
        /** A system-default dash pattern. */
        SYSTEM_DASH,
        /** A system-default dash-dot pattern. */
        SYSTEM_DASH_DOT,
        /** A system-default dash-dot-dot pattern. */
        SYSTEM_DASH_DOT_DOT;

        /**
         * Converts this dash style to the corresponding Apache POI {@code LineDash} constant.
         *
         * <p>This method is used internally for interoperability with the Apache POI library.</p>
         *
         * @return the equivalent {@code StrokeStyle.LineDash} value.
         */
        public StrokeStyle.LineDash toPoiDash() {
            switch (this) {
                case SOLID:
                    return StrokeStyle.LineDash.SOLID;
                case DOT:
                    return StrokeStyle.LineDash.DOT;
                case DASH:
                    return StrokeStyle.LineDash.DASH;
                case LONG_DASH:
                    return StrokeStyle.LineDash.LG_DASH;
                case DASH_DOT:
                    return StrokeStyle.LineDash.DASH_DOT;
                case LONG_DASH_DOT:
                    return StrokeStyle.LineDash.LG_DASH_DOT;
                case LONG_DASH_DOT_DOT:
                    return StrokeStyle.LineDash.LG_DASH_DOT_DOT;
                case SYSTEM_DOT:
                    return StrokeStyle.LineDash.SYS_DOT;
                case SYSTEM_DASH:
                    return StrokeStyle.LineDash.SYS_DASH;
                case SYSTEM_DASH_DOT:
                    return StrokeStyle.LineDash.SYS_DASH_DOT;
                case SYSTEM_DASH_DOT_DOT:
                    return StrokeStyle.LineDash.SYS_DASH_DOT_DOT;
                default:
                    return StrokeStyle.LineDash.SOLID;
            }
        }
    }

    /**
     * Defines the shape of the ends of a border line.
     *
     * <p>This style affects how the line terminates at its endpoints.</p>
     *
     * @since 1.0
     */
    public enum CapStyle {
        /** The line ends squarely at the endpoint. */
        FLAT,
        /** The line ends with a semicircular arc extending beyond the endpoint. */
        ROUND,
        /** The line ends squarely but extends beyond the endpoint by half the line width. */
        SQUARE;

        /**
         * Converts this cap style to the corresponding Apache POI {@code LineCap} constant.
         *
         * <p>This method is used internally for interoperability with the Apache POI library.</p>
         *
         * @return the equivalent {@code StrokeStyle.LineCap} value.
         */
        public StrokeStyle.LineCap toPoiCap() {
            switch (this) {
                case FLAT:
                    return StrokeStyle.LineCap.FLAT;
                case ROUND:
                    return StrokeStyle.LineCap.ROUND;
                case SQUARE:
                    return StrokeStyle.LineCap.SQUARE;
                default:
                    return StrokeStyle.LineCap.FLAT;
            }
        }
    }

    /**
     * Defines the shape of the junction where two border lines meet.
     *
     * <p>This style is relevant for shapes with corners, such as rectangles and polygons.</p>
     *
     * @since 1.0
     */
    public enum JoinStyle {
        /** The junction is rounded. */
        ROUND,
        /** The junction is beveled (flattened). */
        BEVEL,
        /** The junction is mitered (pointed). */
        MITER
    }

    // ==================== Instance Fields ====================

    private final PotColor color;
    private final double width; // in points
    private final DashStyle dashStyle;
    private final CapStyle capStyle;
    private final JoinStyle joinStyle;
    private final double opacity;

    // ==================== Constructors ====================

    /**
     * Constructs a new border with the specified properties.
     *
     * <p>This constructor is private; use the factory methods or builder-style {@code with} methods
     * to create instances. Null parameters are replaced with sensible defaults: {@code PotColor.BLACK}
     * for color, {@code DashStyle.SOLID} for dash style, {@code CapStyle.FLAT} for cap style,
     * and {@code JoinStyle.ROUND} for join style. The width is clamped to be non-negative,
     * and opacity is clamped to the range [0.0, 1.0].</p>
     *
     * @param color the border color; if null, defaults to black.
     * @param width the border width in points; negative values are treated as zero.
     * @param dashStyle the dash pattern; if null, defaults to solid.
     * @param capStyle the line cap style; if null, defaults to flat.
     * @param joinStyle the line join style; if null, defaults to round.
     * @param opacity the opacity (0.0 transparent to 1.0 opaque); clamped to [0.0, 1.0].
     */
    private PotBorder(PotColor color, double width, DashStyle dashStyle,
                      CapStyle capStyle, JoinStyle joinStyle, double opacity) {
        this.color = color != null ? color : PotColor.BLACK;
        this.width = Math.max(0, width);
        this.dashStyle = dashStyle != null ? dashStyle : DashStyle.SOLID;
        this.capStyle = capStyle != null ? capStyle : CapStyle.FLAT;
        this.joinStyle = joinStyle != null ? joinStyle : JoinStyle.ROUND;
        this.opacity = Math.max(0, Math.min(1, opacity));
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a solid border with the specified width and color.
     *
     * <p>The border will have solid dash style, flat cap style, round join style, and full opacity.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(2.5, PotColor.RED);
     * }</pre>
     *
     * @param width the border width in points.
     * @param color the border color.
     * @return a new solid border instance.
     * @throws NullPointerException if {@code color} is null.
     */
    public static PotBorder solid(double width, PotColor color) {
        return new PotBorder(color, width, DashStyle.SOLID, CapStyle.FLAT, JoinStyle.ROUND, 1.0);
    }

    /**
     * Creates a solid black border with the specified width.
     *
     * <p>The border will have solid dash style, flat cap style, round join style, and full opacity.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(1.0);
     * }</pre>
     *
     * @param width the border width in points.
     * @return a new solid black border instance.
     */
    public static PotBorder solid(double width) {
        return solid(width, PotColor.BLACK);
    }

    /**
     * Creates a solid black border with a default width of 1 point.
     *
     * <p>This is a convenience method for the most common border style.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder defaultBorder = PotBorder.solid();
     * }</pre>
     *
     * @return a solid black border with a width of 1 point.
     * @since 1.0
     */
    public static PotBorder solid() {
        return solid(1.0, PotColor.BLACK);
    }

    /**
     * Creates a dashed border with the specified width and color.
     *
     * <p>The border will have dash dash style, flat cap style, round join style, and full opacity.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.dashed(1.0, PotColor.GREEN);
     * }</pre>
     *
     * @param width the border width in points.
     * @param color the border color.
     * @return a new dashed border instance.
     * @throws NullPointerException if {@code color} is null.
     */
    public static PotBorder dashed(double width, PotColor color) {
        return new PotBorder(color, width, DashStyle.DASH, CapStyle.FLAT, JoinStyle.ROUND, 1.0);
    }

    /**
     * Creates a dotted border with the specified width and color.
     *
     * <p>The border will have dot dash style, round cap style, round join style, and full opacity.
     * Round caps are used to ensure dots appear circular.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.dotted(0.5, PotColor.BLUE);
     * }</pre>
     *
     * @param width the border width in points.
     * @param color the border color.
     * @return a new dotted border instance.
     * @throws NullPointerException if {@code color} is null.
     */
    public static PotBorder dotted(double width, PotColor color) {
        return new PotBorder(color, width, DashStyle.DOT, CapStyle.ROUND, JoinStyle.ROUND, 1.0);
    }

    /**
     * Creates a dash-dot border with the specified width and color.
     *
     * <p>The border will have dash-dot dash style, flat cap style, round join style, and full opacity.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.dashDot(1.5, PotColor.ORANGE);
     * }</pre>
     *
     * @param width the border width in points.
     * @param color the border color.
     * @return a new dash-dot border instance.
     * @throws NullPointerException if {@code color} is null.
     */
    public static PotBorder dashDot(double width, PotColor color) {
        return new PotBorder(color, width, DashStyle.DASH_DOT, CapStyle.FLAT, JoinStyle.ROUND, 1.0);
    }

    /**
     * Returns a border instance representing no visible border.
     *
     * <p>This is equivalent to the {@link #NONE} constant. The border has zero width,
     * so it will not be rendered.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder invisible = PotBorder.none();
     * }</pre>
     *
     * @return the {@code PotBorder.NONE} instance.
     */
    public static PotBorder none() {
        return NONE;
    }

    // ==================== Modification Methods ====================

    /**
     * Creates a new border with the specified color.
     *
     * <p>Returns a new {@code PotBorder} instance that is identical to this one except for its color.
     * The original instance remains unchanged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(2.0, PotColor.RED);
     * PotBorder blueBorder = border.withColor(PotColor.BLUE);
     * }</pre>
     *
     * @param color the new color for the border; if {@code null}, {@link PotColor#BLACK} is used.
     * @return a new {@code PotBorder} with the specified color.
     * @see #color(PotColor)
     */
    public PotBorder withColor(PotColor color) {
        return new PotBorder(color, width, dashStyle, capStyle, joinStyle, opacity);
    }

    /**
     * Creates a new border with the specified color.
     *
     * <p>This is a fluent alias for {@link #withColor(PotColor)}. Returns a new {@code PotBorder}
     * instance that is identical to this one except for its color. The original instance remains unchanged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(2.0).color(PotColor.GREEN);
     * }</pre>
     *
     * @param color the new color for the border; if {@code null}, {@link PotColor#BLACK} is used.
     * @return a new {@code PotBorder} with the specified color.
     * @see #withColor(PotColor)
     */
    public PotBorder color(PotColor color) {
        return withColor(color);
    }

    /**
     * Creates a new border with the specified width.
     *
     * <p>Returns a new {@code PotBorder} instance that is identical to this one except for its width.
     * The original instance remains unchanged. The width is clamped to be non-negative.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(1.0);
     * PotBorder thickBorder = border.withWidth(3.0);
     * }</pre>
     *
     * @param width the new width for the border in points; negative values are treated as zero.
     * @return a new {@code PotBorder} with the specified width.
     * @see #width(double)
     */
    public PotBorder withWidth(double width) {
        return new PotBorder(color, width, dashStyle, capStyle, joinStyle, opacity);
    }

    /**
     * Creates a new border with the specified width.
     *
     * <p>This is a fluent alias for {@link #withWidth(double)}. Returns a new {@code PotBorder}
     * instance that is identical to this one except for its width. The original instance remains unchanged.
     * The width is clamped to be non-negative.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.dashed(1.0).width(5.0);
     * }</pre>
     *
     * @param width the new width for the border in points; negative values are treated as zero.
     * @return a new {@code PotBorder} with the specified width.
     * @see #withWidth(double)
     */
    public PotBorder width(double width) {
        return withWidth(width);
    }

    /**
     * Creates a new border with the specified dash style.
     *
     * <p>Returns a new {@code PotBorder} instance that is identical to this one except for its dash style.
     * The original instance remains unchanged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(2.0);
     * PotBorder dashedBorder = border.withDashStyle(DashStyle.DASH);
     * }</pre>
     *
     * @param dashStyle the new dash style for the border; if {@code null}, {@link DashStyle#SOLID} is used.
     * @return a new {@code PotBorder} with the specified dash style.
     */
    public PotBorder withDashStyle(DashStyle dashStyle) {
        return new PotBorder(color, width, dashStyle, capStyle, joinStyle, opacity);
    }

    /**
     * Creates a new border with the specified line cap style.
     *
     * <p>Returns a new {@code PotBorder} instance that is identical to this one except for its line cap style.
     * The original instance remains unchanged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.dotted(1.0);
     * PotBorder roundedBorder = border.withCapStyle(CapStyle.ROUND);
     * }</pre>
     *
     * @param capStyle the new line cap style for the border; if {@code null}, {@link CapStyle#FLAT} is used.
     * @return a new {@code PotBorder} with the specified cap style.
     */
    public PotBorder withCapStyle(CapStyle capStyle) {
        return new PotBorder(color, width, dashStyle, capStyle, joinStyle, opacity);
    }

    /**
     * Creates a new border with the specified line join style.
     *
     * <p>Returns a new {@code PotBorder} instance that is identical to this one except for its line join style.
     * The original instance remains unchanged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(2.0);
     * PotBorder beveledBorder = border.withJoinStyle(JoinStyle.BEVEL);
     * }</pre>
     *
     * @param joinStyle the new line join style for the border; if {@code null}, {@link JoinStyle#ROUND} is used.
     * @return a new {@code PotBorder} with the specified join style.
     */
    public PotBorder withJoinStyle(JoinStyle joinStyle) {
        return new PotBorder(color, width, dashStyle, capStyle, joinStyle, opacity);
    }

    /**
     * Creates a new border with the specified opacity.
     *
     * <p>Returns a new {@code PotBorder} instance that is identical to this one except for its opacity.
     * The original instance remains unchanged. The opacity value is clamped to the range [0.0, 1.0].</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.solid(2.0);
     * PotBorder semiTransparentBorder = border.withOpacity(0.5);
     * }</pre>
     *
     * @param opacity the new opacity for the border, where 0.0 is fully transparent and 1.0 is fully opaque.
     * @return a new {@code PotBorder} with the specified opacity.
     */
    public PotBorder withOpacity(double opacity) {
        return new PotBorder(color, width, dashStyle, capStyle, joinStyle, opacity);
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the color of this border.
     *
     * @return the border color.
     */
    public PotColor getColor() {
        return color;
    }

    /**
     * Returns the width of this border.
     *
     * @return the border width in points.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the dash style of this border.
     *
     * @return the border dash style.
     */
    public DashStyle getDashStyle() {
        return dashStyle;
    }

    /**
     * Returns the line cap style of this border.
     *
     * @return the border line cap style.
     */
    public CapStyle getCapStyle() {
        return capStyle;
    }

    /**
     * Returns the line join style of this border.
     *
     * @return the border line join style.
     */
    public JoinStyle getJoinStyle() {
        return joinStyle;
    }

    /**
     * Returns the opacity of this border.
     *
     * @return the border opacity in the range [0.0, 1.0].
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * Determines whether this border is effectively invisible.
     *
     * <p>A border is considered "none" if its width is less than or equal to zero.</p>
     *
     * @return {@code true} if the border width is zero or negative, {@code false} otherwise.
     */
    public boolean isNone() {
        return width <= 0;
    }

    /**
     * Returns the width of this border in English Metric Units (EMU).
     *
     * <p>This is a convenience method for converting the point-based width to the EMU unit
     * commonly used in presentation file formats like PPTX.</p>
     *
     * @return the border width in EMU.
     * @see UnitConverter#pointsToEmu(double)
     */
    public long getWidthEmu() {
        return UnitConverter.pointsToEmu(width);
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotBorder potBorder = (PotBorder) o;
        return Double.compare(potBorder.width, width) == 0 &&
               Double.compare(potBorder.opacity, opacity) == 0 &&
               Objects.equals(color, potBorder.color) &&
               dashStyle == potBorder.dashStyle &&
               capStyle == potBorder.capStyle &&
               joinStyle == potBorder.joinStyle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, width, dashStyle, capStyle, joinStyle, opacity);
    }

    @Override
    public String toString() {
        if (isNone()) {
            return "PotBorder{NONE}";
        }
        return String.format("PotBorder{color=%s, width=%.1fpt, dash=%s}", 
                            color, width, dashStyle);
    }
}
