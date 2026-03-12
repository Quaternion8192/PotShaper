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

import java.awt.Dimension;

/**
 * Defines standard and custom page sizes for presentation and document layouts.
 *
 * <p>This enumeration provides a comprehensive set of predefined page sizes commonly used in
 * presentations, documents, and screen displays. All dimensions are stored in points (1/72 inch),
 * which is the standard unit in many graphics and document systems. The enum includes common
 * aspect ratios (4:3, 16:9, 16:10), standard office screen sizes, paper formats (Letter, A4, A3,
 * B5, B4), and special formats like 35mm slides and banners.</p>
 *
 * <p>Each constant provides methods to retrieve dimensions in various units (points, inches,
 * centimeters, EMUs) and determine orientation. For custom sizes not covered by the predefined
 * constants, factory methods are available to create {@link CustomPageSize} instances with
 * specified dimensions.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public enum PotPageSize {

    // ==================== Common Aspect Ratios ====================

    /** Standard 4:3 aspect ratio presentation size (960 x 720 points). */
    STANDARD_4_3(960, 720),

    /** Widescreen 16:9 aspect ratio presentation size (960 x 540 points). */
    WIDESCREEN_16_9(960, 540),

    /** Widescreen 16:10 aspect ratio presentation size (960 x 600 points). */
    WIDESCREEN_16_10(960, 600),

    // ==================== Office Screen Sizes ====================

    /** 4:3 aspect ratio screen size at 10" diagonal (720 x 540 points). */
    SCREEN_4_3(720, 540),

    /** 16:9 aspect ratio screen size at 10" diagonal (720 x 405 points). */
    SCREEN_16_9(720, 405),

    /** 16:10 aspect ratio screen size at 10" diagonal (720 x 450 points). */
    SCREEN_16_10(720, 450),

    // ==================== Paper Formats ====================

    /** Letter paper in landscape orientation (8.5" x 11", 792 x 612 points). */
    LETTER_LANDSCAPE(792, 612),

    /** Letter paper in portrait orientation (8.5" x 11", 612 x 792 points). */
    LETTER_PORTRAIT(612, 792),

    /** A4 paper in landscape orientation (210mm x 297mm, 842 x 595 points). */
    A4_LANDSCAPE(842, 595),

    /** A4 paper in portrait orientation (210mm x 297mm, 595 x 842 points). */
    A4_PORTRAIT(595, 842),

    /** A3 paper in landscape orientation (297mm x 420mm, 1191 x 842 points). */
    A3_LANDSCAPE(1191, 842),

    /** A3 paper in portrait orientation (297mm x 420mm, 842 x 1191 points). */
    A3_PORTRAIT(842, 1191),

    /** B5 paper in landscape orientation (176mm x 250mm, 709 x 499 points). */
    B5_LANDSCAPE(709, 499),

    /** B4 paper in landscape orientation (250mm x 353mm, 1001 x 709 points). */
    B4_LANDSCAPE(1001, 709),

    // ==================== Special Formats ====================

    /** 35mm slide format (720 x 480 points). */
    SLIDE_35MM(720, 480),

    /** Standard banner size (720 x 540 points). */
    BANNER(720, 540),

    /** Overhead projector transparency size (720 x 540 points). */
    OVERHEAD(720, 540),

    /** Placeholder for custom sizes; actual dimensions must be specified via factory methods. */
    CUSTOM(0, 0);

    /** The width of the page in points. */
    private final double width;

    /** The height of the page in points. */
    private final double height;

    /**
     * Constructs a page size constant with the specified dimensions.
     *
     * <p>Dimensions are stored internally in points (1/72 inch). All calculations and conversions
     * are based on these point values.</p>
     *
     * @param width  the width in points
     * @param height the height in points
     */
    PotPageSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width of the page in points.
     *
     * <p>Points are the standard unit used throughout this class (1 point = 1/72 inch).</p>
     *
     * @return the width in points
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the page in points.
     *
     * <p>Points are the standard unit used throughout this class (1 point = 1/72 inch).</p>
     *
     * @return the height in points
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the width of the page in inches.
     *
     * <p>Conversion is performed using the standard ratio: 1 inch = 72 points.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * double widthInches = PotPageSize.A4_LANDSCAPE.getWidthInches();
     * // Returns approximately 11.694 inches
     * }</pre>
     *
     * @return the width in inches
     */
    public double getWidthInches() {
        return width / 72.0;
    }

    /**
     * Returns the height of the page in inches.
     *
     * <p>Conversion is performed using the standard ratio: 1 inch = 72 points.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * double heightInches = PotPageSize.A4_LANDSCAPE.getHeightInches();
     * // Returns approximately 8.264 inches
     * }</pre>
     *
     * @return the height in inches
     */
    public double getHeightInches() {
        return height / 72.0;
    }

    /**
     * Returns the width of the page in centimeters.
     *
     * <p>Conversion is performed using the standard ratios: 1 inch = 72 points and 1 inch = 2.54 cm.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * double widthCm = PotPageSize.A4_PORTRAIT.getWidthCm();
     * // Returns approximately 21.0 cm
     * }</pre>
     *
     * @return the width in centimeters
     */
    public double getWidthCm() {
        return width / 72.0 * 2.54;
    }

    /**
     * Returns the height of the page in centimeters.
     *
     * <p>Conversion is performed using the standard ratios: 1 inch = 72 points and 1 inch = 2.54 cm.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * double heightCm = PotPageSize.A4_PORTRAIT.getHeightCm();
     * // Returns approximately 29.7 cm
     * }</pre>
     *
     * @return the height in centimeters
     */
    public double getHeightCm() {
        return height / 72.0 * 2.54;
    }

    /**
     * Returns the width of the page in English Metric Units (EMUs).
     *
     * <p>EMUs are commonly used in office document formats like Microsoft Office. Conversion is
     * performed using the ratio: 1 point = 12,700 EMUs (914400 EMUs per inch / 72 points per inch).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * long widthEmu = PotPageSize.LETTER_LANDSCAPE.getWidthEmu();
     * // Returns 10,058,400 EMUs
     * }</pre>
     *
     * @return the width in EMUs
     */
    public long getWidthEmu() {
        return Math.round(width * 914400.0 / 72.0);
    }

    /**
     * Returns the height of the page in English Metric Units (EMUs).
     *
     * <p>EMUs are commonly used in office document formats like Microsoft Office. Conversion is
     * performed using the ratio: 1 point = 12,700 EMUs (914400 EMUs per inch / 72 points per inch).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * long heightEmu = PotPageSize.LETTER_LANDSCAPE.getHeightEmu();
     * // Returns 7,772,400 EMUs
     * }</pre>
     *
     * @return the height in EMUs
     */
    public long getHeightEmu() {
        return Math.round(height * 914400.0 / 72.0);
    }

    /**
     * Converts the page size to an AWT {@link Dimension} object.
     *
     * <p>The returned dimension has integer width and height values, which are obtained by
     * casting the internal point values to integers. This is suitable for use with AWT/Swing
     * components that expect integer dimensions.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Dimension dim = PotPageSize.STANDARD_4_3.toDimension();
     * // Returns Dimension(960, 720)
     * }</pre>
     *
     * @return a Dimension object representing the page size
     * @see Dimension
     */
    public Dimension toDimension() {
        return new Dimension((int) width, (int) height);
    }

    /**
     * Calculates the aspect ratio (width divided by height) of the page.
     *
     * <p>Returns 0 if the height is 0 to avoid division by zero. For square pages, the ratio is 1.0.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * double ratio = PotPageSize.WIDESCREEN_16_9.getAspectRatio();
     * // Returns approximately 1.777...
     * }</pre>
     *
     * @return the aspect ratio, or 0 if height is 0
     */
    public double getAspectRatio() {
        if (height == 0) {
            return 0;
        }
        return width / height;
    }

    /**
     * Determines whether the page orientation is landscape.
     *
     * <p>A page is considered landscape if its width is greater than its height. Square pages
     * (width equal to height) return {@code false}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * boolean landscape = PotPageSize.A4_LANDSCAPE.isLandscape();
     * // Returns true
     * }</pre>
     *
     * @return {@code true} if width > height, {@code false} otherwise
     * @see #isPortrait()
     */
    public boolean isLandscape() {
        return width > height;
    }

    /**
     * Determines whether the page orientation is portrait.
     *
     * <p>A page is considered portrait if its height is greater than its width. Square pages
     * (width equal to height) return {@code false}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * boolean portrait = PotPageSize.A4_PORTRAIT.isPortrait();
     * // Returns true
     * }</pre>
     *
     * @return {@code true} if height > width, {@code false} otherwise
     * @see #isLandscape()
     */
    public boolean isPortrait() {
        return height > width;
    }

    /**
     * Creates a custom page size with dimensions specified in points.
     *
     * <p>This factory method returns a {@link CustomPageSize} instance with the given dimensions.
     * Use this method when you need a page size not covered by the predefined constants.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * CustomPageSize custom = PotPageSize.custom(500, 300);
     * double width = custom.getWidth(); // 500.0 points
     * }</pre>
     *
     * @param width  the width in points
     * @param height the height in points
     * @return a CustomPageSize instance with the specified dimensions
     * @see CustomPageSize
     */
    public static CustomPageSize custom(double width, double height) {
        return new CustomPageSize(width, height);
    }

    /**
     * Creates a custom page size with dimensions specified in inches.
     *
     * <p>This factory method converts the inch measurements to points (1 inch = 72 points) and
     * returns a {@link CustomPageSize} instance with the calculated dimensions.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * CustomPageSize custom = PotPageSize.customInches(8.5, 11.0);
     * // Creates a page size equivalent to Letter paper
     * }</pre>
     *
     * @param widthInches  the width in inches
     * @param heightInches the height in inches
     * @return a CustomPageSize instance with dimensions converted to points
     * @see CustomPageSize
     */
    public static CustomPageSize customInches(double widthInches, double heightInches) {
        return new CustomPageSize(widthInches * 72.0, heightInches * 72.0);
    }

    /**
     * Creates a custom page size with dimensions specified in centimeters.
     *
     * <p>This factory method converts the centimeter measurements to points (1 cm = 72/2.54 points)
     * and returns a {@link CustomPageSize} instance with the calculated dimensions.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * CustomPageSize custom = PotPageSize.customCm(21.0, 29.7);
     * // Creates a page size equivalent to A4 paper
     * }</pre>
     *
     * @param widthCm  the width in centimeters
     * @param heightCm the height in centimeters
     * @return a CustomPageSize instance with dimensions converted to points
     * @see CustomPageSize
     */
    public static CustomPageSize customCm(double widthCm, double heightCm) {
        return new CustomPageSize(widthCm / 2.54 * 72.0, heightCm / 2.54 * 72.0);
    }

    /**
     * Represents a custom page size with arbitrary dimensions.
     *
     * <p>This nested class provides the same dimensional access methods as the {@link PotPageSize}
     * enum, but for user-defined sizes. Instances are created through the static factory methods
     * in the enclosing class.</p>
     *
     * @see PotPageSize#custom(double, double)
     * @see PotPageSize#customInches(double, double)
     * @see PotPageSize#customCm(double, double)
     * @since 1.0
     */
    public static class CustomPageSize {
        /** The width of the custom page in points. */
        private final double width;
        /** The height of the custom page in points. */
        private final double height;

        /**
         * Constructs a custom page size with the specified dimensions.
         *
         * @param width  the width in points
         * @param height the height in points
         */
        CustomPageSize(double width, double height) {
            this.width = width;
            this.height = height;
        }

        /**
         * Returns the width of the custom page in points.
         *
         * @return the width in points
         */
        public double getWidth() {
            return width;
        }

        /**
         * Returns the height of the custom page in points.
         *
         * @return the height in points
         */
        public double getHeight() {
            return height;
        }

        /**
         * Returns the width of the custom page in English Metric Units (EMUs).
         *
         * @return the width in EMUs
         * @see PotPageSize#getWidthEmu()
         */
        public long getWidthEmu() {
            return Math.round(width * 914400.0 / 72.0);
        }

        /**
         * Returns the height of the custom page in English Metric Units (EMUs).
         *
         * @return the height in EMUs
         * @see PotPageSize#getHeightEmu()
         */
        public long getHeightEmu() {
            return Math.round(height * 914400.0 / 72.0);
        }

        /**
         * Converts the custom page size to an AWT {@link Dimension} object.
         *
         * @return a Dimension object representing the custom page size
         * @see PotPageSize#toDimension()
         */
        public Dimension toDimension() {
            return new Dimension((int) width, (int) height);
        }
    }
}