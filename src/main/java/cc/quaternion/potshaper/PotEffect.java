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
 * Provides factory methods and nested configuration classes for creating visual effects
 * that can be applied to graphical elements.
 * <p>
 * This class serves as a central point for constructing effect configurations such as
 * reflections, glows, and soft edges. Each effect is represented by a nested builder-style
 * class that allows for fluent configuration of its parameters. The effects are designed
 * to be applied to shapes or images to enhance their visual appearance.
 * </p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotEffect {

    // ==================== Reflection Effect ====================

    /**
     * Configures a reflection effect that simulates a mirrored image below the original element.
     * <p>
     * This effect creates a visual reflection with configurable blur, opacity gradient,
     * distance from the original element, and angular direction. The reflection typically
     * fades out towards its end, creating a realistic diminishing effect.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotEffect.Reflection reflection = PotEffect.reflection()
     *     .blurRadius(5.0)
     *     .opacity(0.7, 0.1)
     *     .distance(10.0)
     *     .direction(90.0);
     * }</pre>
     *
     * @author Quaternion8192
     * @since 1.0
     */
    public static class Reflection {
        private double blurRadius = 0;
        private double startOpacity = 0.5;
        private double endOpacity = 0;
        private double distance = 0;
        private double direction = 90; // degrees, default downward

        /**
         * Returns the current blur radius applied to the reflection.
         *
         * @return the blur radius as a non-negative double value.
         */
        public double getBlurRadius() {
            return blurRadius;
        }

        /**
         * Sets the blur radius for the reflection effect.
         * <p>
         * A larger radius produces a more diffused, softer reflection edge.
         * The value is clamped to be non-negative.
         * </p>
         *
         * @param radius the desired blur radius; values less than zero are treated as zero.
         * @return this {@code Reflection} instance for method chaining.
         */
        public Reflection blurRadius(double radius) {
            this.blurRadius = Math.max(0, radius);
            return this;
        }

        /**
         * Sets the opacity gradient for the reflection effect.
         * <p>
         * The reflection fades from the starting opacity at its top (nearest the original element)
         * to the ending opacity at its bottom. Both values are clamped to the range [0, 1].
         * </p>
         *
         * @param start the starting opacity, between 0.0 (fully transparent) and 1.0 (fully opaque).
         * @param end   the ending opacity, between 0.0 (fully transparent) and 1.0 (fully opaque).
         * @return this {@code Reflection} instance for method chaining.
         */
        public Reflection opacity(double start, double end) {
            this.startOpacity = Math.max(0, Math.min(1, start));
            this.endOpacity = Math.max(0, Math.min(1, end));
            return this;
        }

        /**
         * Returns the starting opacity of the reflection gradient.
         *
         * @return the starting opacity, a value between 0.0 and 1.0 inclusive.
         */
        public double getStartOpacity() {
            return startOpacity;
        }

        /**
         * Returns the ending opacity of the reflection gradient.
         *
         * @return the ending opacity, a value between 0.0 and 1.0 inclusive.
         */
        public double getEndOpacity() {
            return endOpacity;
        }

        /**
         * Sets the vertical distance between the original element and the start of its reflection.
         * <p>
         * This defines how far below (or offset according to direction) the reflection begins.
         * The value is clamped to be non-negative.
         * </p>
         *
         * @param d the desired distance; values less than zero are treated as zero.
         * @return this {@code Reflection} instance for method chaining.
         */
        public Reflection distance(double d) {
            this.distance = Math.max(0, d);
            return this;
        }

        /**
         * Returns the current distance between the element and its reflection.
         *
         * @return the distance as a non-negative double value.
         */
        public double getDistance() {
            return distance;
        }

        /**
         * Sets the angular direction of the reflection relative to the original element.
         * <p>
         * The direction is specified in degrees, where 0 degrees points to the right,
         * and angles increase clockwise. The value is normalized to the range [0, 360).
         * A typical downward reflection uses 90 degrees.
         * </p>
         *
         * @param deg the direction angle in degrees.
         * @return this {@code Reflection} instance for method chaining.
         */
        public Reflection direction(double deg) {
            this.direction = deg % 360;
            return this;
        }

        /**
         * Returns the current direction of the reflection.
         *
         * @return the direction angle in degrees, normalized to the range [0, 360).
         */
        public double getDirection() {
            return direction;
        }
    }

    // ==================== Glow Effect ====================

    /**
     * Configures a glow effect that creates a colored halo around the edges of an element.
     * <p>
     * This effect simulates a luminous aura by applying a blurred color outline.
     * The glow's size, color, and transparency can be customized.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotEffect.Glow glow = PotEffect.glow()
     *     .radius(15.0)
     *     .color(PotColor.CYAN)
     *     .opacity(0.8);
     * }</pre>
     *
     * @author Quaternion8192
     * @since 1.0
     */
    public static class Glow {
        private double radius = 0;
        private PotColor color = PotColor.YELLOW;
        private double opacity = 1.0;

        /**
         * Returns the current radius of the glow effect.
         *
         * @return the glow radius as a non-negative double value.
         */
        public double getRadius() {
            return radius;
        }

        /**
         * Sets the radius of the glow effect.
         * <p>
         * A larger radius produces a wider, more diffuse glow around the element.
         * The value is clamped to be non-negative.
         * </p>
         *
         * @param radius the desired glow radius; values less than zero are treated as zero.
         * @return this {@code Glow} instance for method chaining.
         */
        public Glow radius(double radius) {
            this.radius = Math.max(0, radius);
            return this;
        }

        /**
         * Sets the color of the glow effect.
         * <p>
         * If the provided color is {@code null}, the default color {@code PotColor.YELLOW} is used.
         * </p>
         *
         * @param color the desired {@code PotColor} for the glow.
         * @return this {@code Glow} instance for method chaining.
         */
        public Glow color(PotColor color) {
            this.color = color != null ? color : PotColor.YELLOW;
            return this;
        }

        /**
         * Returns the current color of the glow effect.
         *
         * @return the {@code PotColor} instance representing the glow color.
         */
        public PotColor getColor() {
            return color;
        }

        /**
         * Sets the overall opacity of the glow effect.
         * <p>
         * The opacity controls the transparency of the entire glow.
         * The value is clamped to the range [0, 1].
         * </p>
         *
         * @param opacity the desired opacity, between 0.0 (fully transparent) and 1.0 (fully opaque).
         * @return this {@code Glow} instance for method chaining.
         */
        public Glow opacity(double opacity) {
            this.opacity = Math.max(0, Math.min(1, opacity));
            return this;
        }

        /**
         * Returns the current opacity of the glow effect.
         *
         * @return the opacity as a value between 0.0 and 1.0 inclusive.
         */
        public double getOpacity() {
            return opacity;
        }
    }

    // ==================== Soft Edge Effect ====================

    /**
     * Configures a soft edge effect that blurs the boundaries of an element.
     * <p>
     * This effect applies a uniform blur to the edges of a shape or image,
     * creating a feathered, anti-aliased appearance.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotEffect.SoftEdge softEdge = PotEffect.softEdge()
     *     .radius(3.0);
     * }</pre>
     *
     * @author Quaternion8192
     * @since 1.0
     */
    public static class SoftEdge {
        private double radius = 0;

        /**
         * Returns the current blur radius applied to the edges.
         *
         * @return the soft edge radius as a non-negative double value.
         */
        public double getRadius() {
            return radius;
        }

        /**
         * Sets the blur radius for softening the edges of an element.
         * <p>
         * A larger radius produces a more pronounced feathering effect.
         * The value is clamped to be non-negative.
         * </p>
         *
         * @param radius the desired soft edge radius; values less than zero are treated as zero.
         * @return this {@code SoftEdge} instance for method chaining.
         */
        public SoftEdge radius(double radius) {
            this.radius = Math.max(0, radius);
            return this;
        }
    }

    // ==================== Factory Methods ====================

    /**
     * Creates and returns a new, default configuration for a reflection effect.
     * <p>
     * The returned instance has default values: zero blur, 50% starting opacity,
     * zero ending opacity, zero distance, and a 90-degree (downward) direction.
     * </p>
     *
     * @return a new {@code Reflection} configuration object with default settings.
     * @see Reflection
     */
    public static Reflection reflection() {
        return new Reflection();
    }

    /**
     * Creates and returns a new, default configuration for a glow effect.
     * <p>
     * The returned instance has default values: zero radius, yellow color, and full opacity.
     * </p>
     *
     * @return a new {@code Glow} configuration object with default settings.
     * @see Glow
     */
    public static Glow glow() {
        return new Glow();
    }

    /**
     * Creates and returns a new, default configuration for a soft edge effect.
     * <p>
     * The returned instance has a default radius of zero.
     * </p>
     *
     * @return a new {@code SoftEdge} configuration object with default settings.
     * @see SoftEdge
     */
    public static SoftEdge softEdge() {
        return new SoftEdge();
    }
}