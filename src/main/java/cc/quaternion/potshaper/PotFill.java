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

import java.util.Objects;

/**
 * Represents a fill style for a shape or drawing area, supporting solid colors, gradients, images, and patterns.
 *
 * <p>This class is immutable and provides factory methods for creating different types of fills.
 * Each fill type is defined by a {@link Type} and carries associated data such as color, gradient,
 * image bytes, or pattern specification. The fill can also have an opacity value applied uniformly.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * // Create a solid red fill with 50% opacity
 * PotFill redFill = PotFill.solid(PotColor.RED, 0.5);
 *
 * // Create a gradient fill
 * PotGradient grad = PotGradient.linear(...);
 * PotFill gradientFill = PotFill.gradient(grad);
 *
 * // Create a pattern fill
 * PotFill patternFill = PotFill.pattern(PatternType.CROSS, PotColor.BLACK, PotColor.WHITE);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotFill {

    // ==================== Constants ====================

    /** A fill representing no fill (transparent). */
    public static final PotFill NONE = new PotFill(Type.NONE, null, null, null, null, 1.0);

    /** A solid white fill. */
    public static final PotFill WHITE = solid(PotColor.WHITE);

    /** A solid black fill. */
    public static final PotFill BLACK = solid(PotColor.BLACK);

    /** A solid transparent fill. */
    public static final PotFill TRANSPARENT = solid(PotColor.TRANSPARENT);

    // ==================== Enumerations ====================

    /**
     * Defines the type of fill.
     *
     * @since 1.0
     */
    public enum Type {
        /** No fill; the area is transparent. */
        NONE,
        /** A uniform color fill. */
        SOLID,
        /** A gradient fill. */
        GRADIENT,
        /** An image fill. */
        PICTURE,
        /** A predefined pattern fill. */
        PATTERN,
        /** A texture fill (reserved for future use). */
        TEXTURE
    }

    /**
     * Defines the pattern type for a pattern fill, corresponding to OpenXML preset patterns.
     *
     * @since 1.0
     */
    public enum PatternType {
        PERCENT_5, PERCENT_10, PERCENT_20, PERCENT_25, PERCENT_30,
        PERCENT_40, PERCENT_50, PERCENT_60, PERCENT_70, PERCENT_75,
        PERCENT_80, PERCENT_90,
        HORIZONTAL, VERTICAL, LIGHT_HORIZONTAL, LIGHT_VERTICAL,
        DARK_HORIZONTAL, DARK_VERTICAL,
        NARROW_HORIZONTAL, NARROW_VERTICAL,
        DASHED_HORIZONTAL, DASHED_VERTICAL,
        CROSS, DOWNWARD_DIAGONAL, UPWARD_DIAGONAL,
        LIGHT_DOWNWARD_DIAGONAL, LIGHT_UPWARD_DIAGONAL,
        DARK_DOWNWARD_DIAGONAL, DARK_UPWARD_DIAGONAL,
        WIDE_DOWNWARD_DIAGONAL, WIDE_UPWARD_DIAGONAL,
        DASHED_DOWNWARD_DIAGONAL, DASHED_UPWARD_DIAGONAL,
        DIAGONAL_CROSS, SMALL_CHECKER_BOARD, LARGE_CHECKER_BOARD,
        SMALL_GRID, LARGE_GRID, DOTTED_GRID,
        SMALL_CONFETTI, LARGE_CONFETTI,
        HORIZONTAL_BRICK, DIAGONAL_BRICK,
        SOLID_DIAMOND, OPEN_DIAMOND,
        DOTTED_DIAMOND, PLAID, SPHERE,
        WEAVE, DIVOT, SHINGLE, WAVE, TRELLIS, ZIG_ZAG
    }

    // ==================== Fields ====================

    private final Type type;
    private final PotColor color;
    private final PotGradient gradient;
    private final byte[] imageData;
    private final PatternType patternType;
    private final double opacity; // 0.0-1.0

    // ==================== Constructors ====================

    /**
     * Private constructor for creating a fill instance.
     *
     * @param type the type of fill
     * @param color the color for solid fills, or foreground color for patterns
     * @param gradient the gradient definition
     * @param imageData the raw image bytes for picture fills
     * @param patternType the pattern type for pattern fills
     * @param opacity the opacity value, clamped to [0.0, 1.0]
     */
    private PotFill(Type type, PotColor color, PotGradient gradient,
                    byte[] imageData, PatternType patternType, double opacity) {
        this.type = type;
        this.color = color;
        this.gradient = gradient;
        this.imageData = imageData;
        this.patternType = patternType;
        this.opacity = Math.max(0, Math.min(1, opacity));
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a solid fill with the specified color and full opacity.
     *
     * <p>The resulting fill will have {@link Type#SOLID} and an opacity of 1.0.</p>
     *
     * @param color the color of the fill
     * @return a solid fill with the given color
     * @see #solid(PotColor, double)
     */
    public static PotFill solid(PotColor color) {
        return new PotFill(Type.SOLID, color, null, null, null, 1.0);
    }

    /**
     * Creates a solid fill with the specified color and opacity.
     *
     * <p>The resulting fill will have {@link Type#SOLID}. The opacity is clamped to the range [0.0, 1.0].</p>
     *
     * @param color the color of the fill
     * @param opacity the opacity value between 0.0 (transparent) and 1.0 (opaque)
     * @return a solid fill with the given color and opacity
     * @see #solid(PotColor)
     */
    public static PotFill solid(PotColor color, double opacity) {
        return new PotFill(Type.SOLID, color, null, null, null, opacity);
    }

    /**
     * Creates a solid fill from a hexadecimal color string.
     *
     * <p>The string is parsed by {@link PotColor#hex(String)}. The resulting fill has full opacity.</p>
     *
     * @param hex the hexadecimal color string (e.g., "#FF0000" or "FF0000")
     * @return a solid fill with the parsed color
     * @throws IllegalArgumentException if the hex string is invalid
     * @see PotColor#hex(String)
     */
    public static PotFill hex(String hex) {
        return solid(PotColor.hex(hex));
    }

    /**
     * Creates a gradient fill.
     *
     * <p>The resulting fill will have {@link Type#GRADIENT} and full opacity.</p>
     *
     * @param gradient the gradient definition
     * @return a gradient fill
     */
    public static PotFill gradient(PotGradient gradient) {
        return new PotFill(Type.GRADIENT, null, gradient, null, null, 1.0);
    }

    /**
     * Creates a picture fill from raw image data.
     *
     * <p>The resulting fill will have {@link Type#PICTURE} and full opacity.
     * The image data is stored as-is; no validation or decoding is performed.</p>
     *
     * @param imageData the raw bytes of the image
     * @return a picture fill
     */
    public static PotFill picture(byte[] imageData) {
        return new PotFill(Type.PICTURE, null, null, imageData, null, 1.0);
    }

    /**
     * Creates a pattern fill with the specified pattern type and colors.
     *
     * <p>The resulting fill will have {@link Type#PATTERN} and full opacity.
     * The pattern uses the foreground color for the pattern elements and the background color for the base.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFill crossPattern = PotFill.pattern(PatternType.CROSS, PotColor.BLACK, PotColor.WHITE);
     * }</pre>
     *
     * @param patternType the preset pattern type, corresponding to OpenXML's prst attribute
     * @param foregroundColor the color used for the pattern elements
     * @param backgroundColor the color used for the pattern background
     * @return a pattern fill
     */
    public static PotFill pattern(PatternType patternType, PotColor foregroundColor, PotColor backgroundColor) {
        return new PotFill(Type.PATTERN, foregroundColor, null, null, patternType, 1.0) {
            /** The background color for this pattern. */
            private final PotColor bgColor = backgroundColor;

            @Override
            public PotColor getBackgroundColor() {
                return bgColor;
            }
        };
    }

    /**
     * Returns a fill representing no fill (transparent).
     *
     * <p>This is equivalent to the constant {@link #NONE}.</p>
     *
     * @return a fill of type {@link Type#NONE}
     * @see #NONE
     */
    public static PotFill none() {
        return NONE;
    }

    // ==================== Operations ====================

    /**
     * Returns a new fill with the same properties but a different opacity.
     *
     * <p>This method creates a copy of the current fill with the specified opacity value,
     * which is clamped to the range [0.0, 1.0]. All other properties remain unchanged.</p>
     *
     * @param opacity the new opacity value between 0.0 (transparent) and 1.0 (opaque)
     * @return a new PotFill instance with the updated opacity
     */
    public PotFill withOpacity(double opacity) {
        return new PotFill(type, color, gradient, imageData, patternType, opacity);
    }

    // ==================== Type Checks ====================

    /**
     * Returns whether this fill is of type {@link Type#NONE}.
     *
     * @return true if the fill type is NONE
     */
    public boolean isNone() {
        return type == Type.NONE;
    }

    /**
     * Returns whether this fill is of type {@link Type#SOLID}.
     *
     * @return true if the fill type is SOLID
     */
    public boolean isSolid() {
        return type == Type.SOLID;
    }

    /**
     * Returns whether this fill is of type {@link Type#GRADIENT}.
     *
     * @return true if the fill type is GRADIENT
     */
    public boolean isGradient() {
        return type == Type.GRADIENT;
    }

    /**
     * Returns whether this fill is of type {@link Type#PICTURE}.
     *
     * @return true if the fill type is PICTURE
     */
    public boolean isPicture() {
        return type == Type.PICTURE;
    }

    /**
     * Returns whether this fill is an image-based fill.
     *
     * <p>This is equivalent to {@link #isPicture()} and is provided for semantic clarity.</p>
     *
     * @return true if the fill type is PICTURE
     * @see #isPicture()
     */
    public boolean isImage() {
        return isPicture();
    }

    /**
     * Returns whether this fill is of type {@link Type#PATTERN}.
     *
     * @return true if the fill type is PATTERN
     */
    public boolean isPattern() {
        return type == Type.PATTERN;
    }

    // ==================== Getters ====================

    /**
     * Returns the type of this fill.
     *
     * @return the fill type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the color associated with this fill.
     *
     * <p>For solid fills, this is the fill color. For pattern fills, this is the foreground color.
     * For other types, this returns null.</p>
     *
     * @return the color, or null if not applicable
     */
    public PotColor getColor() {
        return color;
    }

    /**
     * Returns the gradient definition for this fill.
     *
     * @return the gradient, or null if the fill is not a gradient
     */
    public PotGradient getGradient() {
        return gradient;
    }

    /**
     * Returns the raw image data for this fill.
     *
     * @return the image bytes, or null if the fill is not a picture
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * Returns the pattern type for this fill.
     *
     * @return the pattern type, or null if the fill is not a pattern
     */
    public PatternType getPatternType() {
        return patternType;
    }

    /**
     * Returns the background color for a pattern fill.
     *
     * <p>For pattern fills created via {@link #pattern(PatternType, PotColor, PotColor)},
     * this returns the specified background color. For all other fill types, this returns null.</p>
     *
     * @return the background color for pattern fills, or null otherwise
     */
    public PotColor getBackgroundColor() {
        return null;
    }

    /**
     * Returns the opacity of this fill as a double between 0.0 and 1.0.
     *
     * @return the opacity value
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * Returns the opacity as an integer percentage scaled by 1000 (OpenXML format).
     *
     * <p>This converts the 0.0-1.0 opacity to an integer in the range 0-100000,
     * as used by OpenXML's alpha value representation.</p>
     *
     * @return the opacity as an integer between 0 and 100000
     */
    public int getOpacityPercent() {
        return (int) Math.round(opacity * 100000);
    }

    // ==================== Object Methods ====================

    /**
     * Compares this fill to another object for equality.
     *
     * <p>Two fills are considered equal if they have the same type, opacity, and
     * relevant data (color, gradient, pattern type). Image data is not compared directly.</p>
     *
     * @param o the object to compare with
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotFill potFill = (PotFill) o;
        return Double.compare(potFill.opacity, opacity) == 0 &&
               type == potFill.type &&
               Objects.equals(color, potFill.color) &&
               Objects.equals(gradient, potFill.gradient) &&
               patternType == potFill.patternType;
    }

    /**
     * Returns a hash code value for this fill.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, color, gradient, patternType, opacity);
    }

    /**
     * Returns a string representation of this fill.
     *
     * <p>The format is descriptive and varies by fill type, showing key properties.</p>
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        switch (type) {
            case NONE:
                return "PotFill{NONE}";
            case SOLID:
                return String.format("PotFill{SOLID, color=%s, opacity=%.2f}", color, opacity);
            case GRADIENT:
                return String.format("PotFill{GRADIENT, %s}", gradient);
            case PICTURE:
                return String.format("PotFill{PICTURE, size=%d bytes}",
                    imageData != null ? imageData.length : 0);
            case PATTERN:
                return String.format("PotFill{PATTERN, type=%s}", patternType);
            default:
                return "PotFill{UNKNOWN}";
        }
    }
}