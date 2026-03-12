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

/**
 * Provides configuration formats for 3D pot rendering, including rotation, bevel, material, and lighting.
 *
 * <p>This class serves as a factory and container for various style attributes used to define the visual
 * appearance of a 3D pot. It offers fluent builders for rotation and bevel settings, and enumerations
 * for material and lighting presets. All methods are static, and the nested classes are designed for
 * chained configuration.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class Pot3DFormat {

    // ==================== Rotation Configuration ====================

    /**
     * Represents a 3D rotation configuration for a pot.
     *
     * <p>This class defines rotation angles around the X, Y, and Z axes. It provides a fluent API
     * to set these angles individually or together. The Z-axis rotation is automatically normalized
     * to a value between 0 and 360 degrees.</p>
     */
    public static class Rotation {
        private double rotationX = 0;   // Rotation around the X-axis in degrees
        private double rotationY = 0;   // Rotation around the Y-axis in degrees
        private double rotationZ = 0;   // Rotation around the Z-axis in degrees

        private Rotation() {
        }

        /**
         * Sets the rotation angle around the X-axis.
         *
         * <p>This method updates the X-axis rotation and returns the same {@code Rotation} instance
         * to allow for method chaining.</p>
         *
         * @param degrees the rotation angle in degrees
         * @return this {@code Rotation} instance for chaining
         *
         * <h3>Usage Example</h3>
         * <pre>{@code
         * Rotation rot = Pot3DFormat.rotation().x(45.0).y(30.0);
         * }</pre>
         */
        public Rotation x(double degrees) {
            this.rotationX = degrees;
            return this;
        }

        /**
         * Sets the rotation angle around the Y-axis.
         *
         * <p>This method updates the Y-axis rotation and returns the same {@code Rotation} instance
         * to allow for method chaining.</p>
         *
         * @param degrees the rotation angle in degrees
         * @return this {@code Rotation} instance for chaining
         *
         * <h3>Usage Example</h3>
         * <pre>{@code
         * Rotation rot = Pot3DFormat.rotation().y(20.0).z(90.0);
         * }</pre>
         */
        public Rotation y(double degrees) {
            this.rotationY = degrees;
            return this;
        }

        /**
         * Sets the rotation angle around the Z-axis.
         *
         * <p>This method updates the Z-axis rotation and returns the same {@code Rotation} instance
         * to allow for method chaining. The input angle is taken modulo 360 degrees, ensuring the
         * result is within a standard rotational range.</p>
         *
         * @param degrees the rotation angle in degrees
         * @return this {@code Rotation} instance for chaining
         *
         * <h3>Usage Example</h3>
         * <pre>{@code
         * Rotation rot = Pot3DFormat.rotation().z(450.0); // Result is 90.0 degrees
         * }</pre>
         */
        public Rotation z(double degrees) {
            this.rotationZ = degrees % 360;
            return this;
        }

        /**
         * Sets both the X and Y rotation angles simultaneously, typically used for perspective adjustments.
         *
         * <p>This is a convenience method that sets the X and Y rotations in one call, returning the
         * same instance for chaining. It is equivalent to calling {@code x(x).y(y)}.</p>
         *
         * @param x the rotation angle around the X-axis in degrees
         * @param y the rotation angle around the Y-axis in degrees
         * @return this {@code Rotation} instance for chaining
         *
         * <h3>Usage Example</h3>
         * <pre>{@code
         * Rotation rot = Pot3DFormat.rotation().perspective(30.0, 15.0);
         * }</pre>
         */
        public Rotation perspective(double x, double y) {
            this.rotationX = x;
            this.rotationY = y;
            return this;
        }

        /**
         * Returns the current rotation angle around the X-axis.
         *
         * @return the X-axis rotation in degrees
         */
        public double getRotationX() {
            return rotationX;
        }

        /**
         * Returns the current rotation angle around the Y-axis.
         *
         * @return the Y-axis rotation in degrees
         */
        public double getRotationY() {
            return rotationY;
        }

        /**
         * Returns the current rotation angle around the Z-axis.
         *
         * @return the Z-axis rotation in degrees, normalized to [0, 360)
         */
        public double getRotationZ() {
            return rotationZ;
        }
    }

    // ==================== Bevel Configuration ====================

    /**
     * Represents a bevel (edge) style configuration for a pot.
     *
     * <p>This class defines the shape and dimensions of a bevel applied to the pot's edges.
     * It supports different preset shapes and allows setting custom width and height dimensions.
     * The width and height are constrained to be non-negative.</p>
     */
    public static class Bevel {
        private String preset = "circle";  // Preset shape: circle, square, relaxedInset, slant
        private double width = 0;
        private double height = 0;

        private Bevel(String preset) {
            this.preset = preset;
        }

        /**
         * Sets the width and height dimensions of the bevel.
         *
         * <p>This method updates the bevel's width and height, clamping both values to be
         * non-negative. It returns the same {@code Bevel} instance for chaining.</p>
         *
         * @param w the bevel width; if negative, it is set to zero
         * @param h the bevel height; if negative, it is set to zero
         * @return this {@code Bevel} instance for chaining
         *
         * <h3>Usage Example</h3>
         * <pre>{@code
         * Bevel bev = Pot3DFormat.bevel().size(5.0, 2.0);
         * }</pre>
         */
        public Bevel size(double w, double h) {
            this.width = Math.max(0, w);
            this.height = Math.max(0, h);
            return this;
        }

        /**
         * Returns the preset shape identifier of this bevel.
         *
         * @return the preset name (e.g., "circle", "square")
         */
        public String getPreset() {
            return preset;
        }

        /**
         * Returns the current width of the bevel.
         *
         * @return the bevel width, always non-negative
         */
        public double getWidth() {
            return width;
        }

        /**
         * Returns the current height of the bevel.
         *
         * @return the bevel height, always non-negative
         */
        public double getHeight() {
            return height;
        }
    }

    // ==================== Material Enumeration ====================

    /**
     * Enumerates available material surface types for pot rendering.
     *
     * <p>Each material constant corresponds to a specific visual surface property,
     * influencing how light interacts with the pot's geometry.</p>
     */
    public enum Material {
        /**
         * Represents a plastic-like surface material.
         */
        PLASTIC("plastic"),
        /**
         * Represents a metallic surface material.
         */
        METAL("metal"),
        /**
         * Represents a non-glossy, matte surface material.
         */
        MATTE("matte"),
        /**
         * Represents a warm-toned matte surface material.
         */
        WARM_MATTE("warmMatte");

        private final String value;

        Material(String value) {
            this.value = value;
        }

        /**
         * Returns the string identifier for this material.
         *
         * @return the material's identifier string
         */
        public String getValue() {
            return value;
        }
    }

    // ==================== Lighting Enumeration ====================

    /**
     * Enumerates available lighting environment presets for pot rendering.
     *
     * <p>Each lighting constant defines a distinct set of light sources and intensities
     * to create different ambient and directional lighting effects on the pot.</p>
     */
    public enum Lighting {
        /**
         * Represents a neutral, evenly distributed lighting environment.
         */
        BALANCED("balanced"),
        /**
         * Represents a diffused, low-contrast lighting environment.
         */
        SOFT("soft"),
        /**
         * Represents a high-contrast, directional lighting environment.
         */
        HARSH("harsh"),
        /**
         * Represents a high-intensity, overall bright lighting environment.
         */
        BRIGHT("bright"),
        /**
         * Represents a lighting environment simulating morning light.
         */
        MORNING("morning"),
        /**
         * Represents a lighting environment simulating sunset light.
         */
        SUNSET("sunset");

        private final String value;

        Lighting(String value) {
            this.value = value;
        }

        /**
         * Returns the string identifier for this lighting preset.
         *
         * @return the lighting preset's identifier string
         */
        public String getValue() {
            return value;
        }
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new, default rotation configuration builder.
     *
     * <p>Returns a new {@code Rotation} instance with all angles initialized to zero degrees.
     * The returned builder can be used to set rotation angles fluently.</p>
     *
     * @return a new {@code Rotation} builder instance
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Pot3DFormat.Rotation rot = Pot3DFormat.rotation().x(10).y(20).z(5);
     * }</pre>
     * @see Rotation
     */
    public static Rotation rotation() {
        return new Rotation();
    }

    /**
     * Creates a new bevel configuration builder with the "circle" preset.
     *
     * <p>Returns a new {@code Bevel} instance preset to a circular shape. The initial width
     * and height are zero.</p>
     *
     * @return a new {@code Bevel} builder instance with the "circle" preset
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Pot3DFormat.Bevel bev = Pot3DFormat.bevel().size(3.0, 1.5);
     * }</pre>
     * @see Bevel
     */
    public static Bevel bevel() {
        return new Bevel("circle");
    }

    /**
     * Creates a new bevel configuration builder with the "square" preset.
     *
     * <p>Returns a new {@code Bevel} instance preset to a square shape. The initial width
     * and height are zero.</p>
     *
     * @return a new {@code Bevel} builder instance with the "square" preset
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Pot3DFormat.Bevel bev = Pot3DFormat.square().size(4.0, 4.0);
     * }</pre>
     * @see Bevel
     */
    public static Bevel square() {
        return new Bevel("square");
    }

    /**
     * Creates a new bevel configuration builder with the "relaxedInset" preset.
     *
     * <p>Returns a new {@code Bevel} instance preset to a relaxed inset shape. The initial width
     * and height are zero.</p>
     *
     * @return a new {@code Bevel} builder instance with the "relaxedInset" preset
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Pot3DFormat.Bevel bev = Pot3DFormat.relaxedInset().size(2.5, 1.0);
     * }</pre>
     * @see Bevel
     */
    public static Bevel relaxedInset() {
        return new Bevel("relaxedInset");
    }

    /**
     * Creates a new bevel configuration builder with the "slant" preset.
     *
     * <p>Returns a new {@code Bevel} instance preset to a slanted shape. The initial width
     * and height are zero.</p>
     *
     * @return a new {@code Bevel} builder instance with the "slant" preset
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Pot3DFormat.Bevel bev = Pot3DFormat.slant().size(6.0, 3.0);
     * }</pre>
     * @see Bevel
     */
    public static Bevel slant() {
        return new Bevel("slant");
    }
}