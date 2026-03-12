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

import java.awt.Color;
import java.util.Objects;

/**
 * Represents an immutable color with red, green, blue, and alpha components.
 *
 * <p>This class provides a comprehensive set of operations for color creation, conversion,
 * and manipulation. It supports construction from RGB, RGBA, hexadecimal strings, and HSL values.
 * Instances can be converted to AWT {@link Color} objects, hexadecimal representations, and
 * integer packed formats. The class also includes utilities for adjusting opacity, lightening,
 * darkening, mixing, and computing complementary or grayscale versions of a color.
 *
 * <p>All color component values are clamped to the valid range of 0-255 upon construction.
 * The class defines a set of commonly used color constants for convenience.
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotColor {

    // ==================== Color Constants ====================

    /** A constant representing the color black (RGB 0,0,0). */
    public static final PotColor BLACK = new PotColor(0, 0, 0);
    /** A constant representing the color white (RGB 255,255,255). */
    public static final PotColor WHITE = new PotColor(255, 255, 255);
    /** A constant representing the color red (RGB 255,0,0). */
    public static final PotColor RED = new PotColor(255, 0, 0);
    /** A constant representing the color green (RGB 0,128,0). */
    public static final PotColor GREEN = new PotColor(0, 128, 0);
    /** A constant representing the color blue (RGB 0,0,255). */
    public static final PotColor BLUE = new PotColor(0, 0, 255);
    /** A constant representing the color yellow (RGB 255,255,0). */
    public static final PotColor YELLOW = new PotColor(255, 255, 0);
    /** A constant representing the color cyan (RGB 0,255,255). */
    public static final PotColor CYAN = new PotColor(0, 255, 255);
    /** A constant representing the color magenta (RGB 255,0,255). */
    public static final PotColor MAGENTA = new PotColor(255, 0, 255);
    /** A constant representing the color gray (RGB 128,128,128). */
    public static final PotColor GRAY = new PotColor(128, 128, 128);
    /** A constant representing the color dark gray (RGB 64,64,64). */
    public static final PotColor DARK_GRAY = new PotColor(64, 64, 64);
    /** A constant representing the color light gray (RGB 192,192,192). */
    public static final PotColor LIGHT_GRAY = new PotColor(192, 192, 192);
    /** A constant representing the color orange (RGB 255,165,0). */
    public static final PotColor ORANGE = new PotColor(255, 165, 0);
    /** A constant representing the color pink (RGB 255,192,203). */
    public static final PotColor PINK = new PotColor(255, 192, 203);
    /** A constant representing the color purple (RGB 128,0,128). */
    public static final PotColor PURPLE = new PotColor(128, 0, 128);
    /** A constant representing the color brown (RGB 139,69,19). */
    public static final PotColor BROWN = new PotColor(139, 69, 19);
    /** A constant representing the color dark blue (RGB 0,0,139). */
    public static final PotColor DARK_BLUE = new PotColor(0, 0, 139);
    /** A constant representing the color dark green (RGB 0,100,0). */
    public static final PotColor DARK_GREEN = new PotColor(0, 100, 0);
    /** A constant representing the color dark red (RGB 139,0,0). */
    public static final PotColor DARK_RED = new PotColor(139, 0, 0);
    /** A constant representing the color light blue (RGB 173,216,230). */
    public static final PotColor LIGHT_BLUE = new PotColor(173, 216, 230);
    /** A constant representing the color light green (RGB 144,238,144). */
    public static final PotColor LIGHT_GREEN = new PotColor(144, 238, 144);
    /** A constant representing the color light yellow (RGB 255,255,224). */
    public static final PotColor LIGHT_YELLOW = new PotColor(255, 255, 224);
    /** A constant representing a fully transparent color (RGBA 0,0,0,0). */
    public static final PotColor TRANSPARENT = new PotColor(0, 0, 0, 0);

    // ==================== Instance Fields ====================

    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    // ==================== Constructors ====================

    /**
     * Constructs an opaque color from red, green, and blue components.
     *
     * <p>The alpha component is set to 255 (fully opaque). Each component value is clamped
     * to the range 0-255 inclusive.
     *
     * @param red   the red component (0-255)
     * @param green the green component (0-255)
     * @param blue  the blue component (0-255)
     */
    public PotColor(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    /**
     * Constructs a color from red, green, blue, and alpha components.
     *
     * <p>Each component value is clamped to the range 0-255 inclusive.
     *
     * @param red   the red component (0-255)
     * @param green the green component (0-255)
     * @param blue  the blue component (0-255)
     * @param alpha the alpha (transparency) component (0-255)
     */
    public PotColor(int red, int green, int blue, int alpha) {
        this.red = clamp(red, 0, 255);
        this.green = clamp(green, 0, 255);
        this.blue = clamp(blue, 0, 255);
        this.alpha = clamp(alpha, 0, 255);
    }

    // ==================== Static Factory Methods ====================

    /**
     * Creates an opaque PotColor from red, green, and blue components.
     *
     * <p>This is a convenience static factory method equivalent to the constructor
     * {@link #PotColor(int, int, int)}.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotColor brightRed = PotColor.rgb(255, 50, 50);
     * }</pre>
     *
     * @param red   the red component (0-255)
     * @param green the green component (0-255)
     * @param blue  the blue component (0-255)
     * @return a new opaque PotColor instance
     * @see #PotColor(int, int, int)
     */
    public static PotColor rgb(int red, int green, int blue) {
        return new PotColor(red, green, blue);
    }

    /**
     * Creates a PotColor from red, green, blue, and alpha components.
     *
     * <p>This is a convenience static factory method equivalent to the constructor
     * {@link #PotColor(int, int, int, int)}.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotColor semiTransparentBlue = PotColor.rgba(0, 0, 255, 128);
     * }</pre>
     *
     * @param red   the red component (0-255)
     * @param green the green component (0-255)
     * @param blue  the blue component (0-255)
     * @param alpha the alpha component (0-255)
     * @return a new PotColor instance with the specified transparency
     * @see #PotColor(int, int, int, int)
     */
    public static PotColor rgba(int red, int green, int blue, int alpha) {
        return new PotColor(red, green, blue, alpha);
    }

    /**
     * Creates a PotColor from a hexadecimal color string.
     *
     * <p>The input string can be in the following formats:
     * <ul>
     *   <li>6-digit RGB: "FF0000" or "#FF0000"</li>
     *   <li>8-digit RGBA: "FF000080" or "#FF000080"</li>
     *   <li>3-digit shorthand RGB: "F00" or "#F00" (expanded to "FF0000")</li>
     * </ul>
     * If the string is null, empty, or cannot be parsed, the method returns {@link #BLACK}.
     * The parsing is case-insensitive.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotColor color1 = PotColor.hex("#FF5733");
     * PotColor color2 = PotColor.hex("00FF00");
     * PotColor color3 = PotColor.hex("#C8F"); // Expands to "#CC88FF"
     * }</pre>
     *
     * @param hex the hexadecimal color string, optionally prefixed with '#'
     * @return a PotColor corresponding to the hex string, or {@link #BLACK} on parse failure
     */
    public static PotColor hex(String hex) {
        if (hex == null || hex.isEmpty()) {
            return BLACK;
        }

        // Remove leading '#' if present
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        // Expand 3-digit shorthand to 6 digits
        if (hex.length() == 3) {
            char r = hex.charAt(0);
            char g = hex.charAt(1);
            char b = hex.charAt(2);
            hex = "" + r + r + g + g + b + b;
        }

        try {
            if (hex.length() == 6) {
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                return new PotColor(r, g, b);
            } else if (hex.length() == 8) {
                int r = Integer.parseInt(hex.substring(0, 2), 16);
                int g = Integer.parseInt(hex.substring(2, 4), 16);
                int b = Integer.parseInt(hex.substring(4, 6), 16);
                int a = Integer.parseInt(hex.substring(6, 8), 16);
                return new PotColor(r, g, b, a);
            }
        } catch (NumberFormatException e) {
            // Fall through to return BLACK
        }
        return BLACK;
    }

    /**
     * Creates a PotColor from HSL (Hue, Saturation, Lightness) values.
     *
     * <p>The resulting color is fully opaque (alpha = 255). The conversion follows the
     * standard HSL to RGB algorithm.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // A pure red hue, fully saturated, medium lightness
     * PotColor redHsl = PotColor.hsl(0, 100, 50);
     * }</pre>
     *
     * @param hue        the hue angle in degrees (0-360)
     * @param saturation the saturation percentage (0-100)
     * @param lightness  the lightness percentage (0-100)
     * @return a new opaque PotColor derived from the HSL values
     */
    public static PotColor hsl(double hue, double saturation, double lightness) {
        double h = hue / 360.0;
        double s = saturation / 100.0;
        double l = lightness / 100.0;

        double r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;
            r = hueToRgb(p, q, h + 1.0 / 3.0);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0 / 3.0);
        }

        return new PotColor(
            (int) Math.round(r * 255),
            (int) Math.round(g * 255),
            (int) Math.round(b * 255)
        );
    }

    /**
     * Creates a PotColor from an AWT {@link Color} object.
     *
     * <p>If the provided color is {@code null}, the method returns {@link #BLACK}.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Color awtColor = new Color(255, 0, 0, 128);
     * PotColor potColor = PotColor.fromAwtColor(awtColor);
     * }</pre>
     *
     * @param color the source AWT Color, may be {@code null}
     * @return a PotColor with the same components as the AWT Color, or {@link #BLACK} if input is null
     * @see #toAwtColor()
     */
    public static PotColor fromAwtColor(Color color) {
        if (color == null) {
            return BLACK;
        }
        return new PotColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the red component of this color.
     *
     * @return the red component value (0-255)
     */
    public int getRed() {
        return red;
    }

    /**
     * Returns the green component of this color.
     *
     * @return the green component, in the range 0-255.
     */
    public int getGreen() {
        return green;
    }

    /**
     * Returns the blue component of this color.
     *
     * @return the blue component, in the range 0-255.
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Returns the alpha (transparency) component of this color.
     *
     * @return the alpha component, in the range 0-255.
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Returns the opacity of this color as a floating-point value.
     * <p>
     * The opacity is derived from the alpha component, where 0.0 represents
     * full transparency and 1.0 represents full opacity.
     *
     * @return the opacity value, between 0.0 and 1.0 inclusive.
     */
    public double getOpacity() {
        return alpha / 255.0;
    }

    // ==================== Conversion Methods ====================

    /**
     * Converts this color to a standard AWT {@link Color} object.
     *
     * @return a new {@code Color} instance with the same RGBA components.
     */
    public Color toAwtColor() {
        return new Color(red, green, blue, alpha);
    }

    /**
     * Returns a hexadecimal string representation of the RGB components.
     * <p>
     * The format is six uppercase hexadecimal digits (RRGGBB), without a leading hash.
     *
     * @return the RGB hex string, e.g., "FF0000" for red.
     */
    public String toHex() {
        return String.format("%02X%02X%02X", red, green, blue);
    }

    /**
     * Returns a hexadecimal string representation of the RGB components with a leading hash.
     * <p>
     * The format is a hash followed by six uppercase hexadecimal digits (#RRGGBB).
     *
     * @return the RGB hex string with a hash prefix, e.g., "#FF0000".
     */
    public String toHexWithHash() {
        return "#" + toHex();
    }

    /**
     * Returns a hexadecimal string representation of the RGBA components.
     * <p>
     * The format is eight uppercase hexadecimal digits (RRGGBBAA).
     *
     * @return the RGBA hex string, e.g., "FF0000FF" for opaque red.
     */
    public String toHexRgba() {
        return String.format("%02X%02X%02X%02X", red, green, blue, alpha);
    }

    /**
     * Returns an integer representation of the RGB components.
     * <p>
     * The integer is packed as 0xRRGGBB, where the most significant byte is red.
     *
     * @return the RGB integer value.
     */
    public int toRgbInt() {
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Returns an integer representation of the ARGB components.
     * <p>
     * The integer is packed as 0xAARRGGBB, where the most significant byte is alpha.
     *
     * @return the ARGB integer value.
     */
    public int toArgbInt() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    // ==================== Derivation Methods ====================

    /**
     * Creates a new color with a different alpha component.
     *
     * @param alpha the new alpha value, in the range 0-255.
     * @return a new {@code PotColor} instance with the specified alpha.
     */
    public PotColor withAlpha(int alpha) {
        return new PotColor(red, green, blue, alpha);
    }

    /**
     * Creates a new color with a different opacity.
     *
     * @param opacity the new opacity value, in the range 0.0 (transparent) to 1.0 (opaque).
     * @return a new {@code PotColor} instance with the specified opacity.
     */
    public PotColor withOpacity(double opacity) {
        return new PotColor(red, green, blue, (int) Math.round(opacity * 255));
    }

    /**
     * Creates a lighter version of this color.
     * <p>
     * Each RGB component is moved linearly towards 255 (white) by the specified factor.
     * The alpha component remains unchanged.
     *
     * @param factor the lightening factor, between 0.0 (no change) and 1.0 (pure white).
     * @return a new, lighter {@code PotColor} instance.
     */
    public PotColor lighter(double factor) {
        factor = clamp(factor, 0.0, 1.0);
        return new PotColor(
            (int) (red + (255 - red) * factor),
            (int) (green + (255 - green) * factor),
            (int) (blue + (255 - blue) * factor),
            alpha
        );
    }

    /**
     * Creates a darker version of this color.
     * <p>
     * Each RGB component is scaled down (multiplied by (1 - factor)).
     * The alpha component remains unchanged.
     *
     * @param factor the darkening factor, between 0.0 (no change) and 1.0 (pure black).
     * @return a new, darker {@code PotColor} instance.
     */
    public PotColor darker(double factor) {
        factor = clamp(factor, 0.0, 1.0);
        return new PotColor(
            (int) (red * (1 - factor)),
            (int) (green * (1 - factor)),
            (int) (blue * (1 - factor)),
            alpha
        );
    }

    /**
     * Linearly interpolates between this color and another color.
     * <p>
     * The resulting color is calculated as: this + (other - this) * factor.
     * All components (RGBA) are interpolated independently.
     *
     * @param other  the target color to mix with.
     * @param factor the interpolation factor, where 0.0 yields this color and 1.0 yields the other color.
     * @return a new {@code PotColor} representing the mixture.
     */
    public PotColor mix(PotColor other, double factor) {
        factor = clamp(factor, 0.0, 1.0);
        return new PotColor(
            (int) (red + (other.red - red) * factor),
            (int) (green + (other.green - green) * factor),
            (int) (blue + (other.blue - blue) * factor),
            (int) (alpha + (other.alpha - alpha) * factor)
        );
    }

    /**
     * Returns the complementary color.
     * <p>
     * The complement is calculated by subtracting each RGB component from 255.
     * The alpha component remains unchanged.
     *
     * @return a new {@code PotColor} that is the complement of this color.
     */
    public PotColor complement() {
        return new PotColor(255 - red, 255 - green, 255 - blue, alpha);
    }

    /**
     * Converts this color to grayscale using a standard luminance formula.
     * <p>
     * The grayscale value is calculated as: 0.299*R + 0.587*G + 0.114*B.
     * The alpha component remains unchanged.
     *
     * @return a new grayscale {@code PotColor} instance.
     */
    public PotColor toGrayscale() {
        int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
        return new PotColor(gray, gray, gray, alpha);
    }

    // ==================== Private Helper Methods ====================

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double hueToRgb(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0 / 6.0) return p + (q - p) * 6 * t;
        if (t < 1.0 / 2.0) return q;
        if (t < 2.0 / 3.0) return p + (q - p) * (2.0 / 3.0 - t) * 6;
        return p;
    }

    // ==================== Object Overrides ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotColor potColor = (PotColor) o;
        return red == potColor.red && green == potColor.green &&
               blue == potColor.blue && alpha == potColor.alpha;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        if (alpha == 255) {
            return String.format("PotColor(#%s)", toHex());
        } else {
            return String.format("PotColor(#%s, alpha=%d)", toHex(), alpha);
        }
    }
}
