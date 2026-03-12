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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a gradient fill that can be applied to shapes or text.
 *
 * <p>A gradient defines a smooth transition between two or more colors along a specified direction
 * or path. This class supports multiple gradient types (linear, radial, rectangular, and path)
 * and allows configuration of color stops, angle, and rotation behavior relative to the shape.
 * Instances are immutable; all modifier methods return a new instance with the applied change.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotGradient {

    // ==================== Predefined Gradients ====================

    /** A predefined linear blue gradient from dark to light blue. */
    public static final PotGradient BLUE = linear(0)
        .addStop(0, PotColor.hex("0072C6"))
        .addStop(1, PotColor.hex("00BCF2"));

    /** A predefined linear green gradient from dark green to light green. */
    public static final PotGradient GREEN = linear(0)
        .addStop(0, PotColor.hex("107C10"))
        .addStop(1, PotColor.hex("7FBA00"));

    /** A predefined linear orange gradient from dark orange to yellow. */
    public static final PotGradient ORANGE = linear(0)
        .addStop(0, PotColor.hex("D83B01"))
        .addStop(1, PotColor.hex("FFB900"));

    /** A predefined linear purple gradient from dark purple to magenta. */
    public static final PotGradient PURPLE = linear(0)
        .addStop(0, PotColor.hex("5C2D91"))
        .addStop(1, PotColor.hex("B4009E"));

    /** A predefined linear gray gradient from dark gray to light gray. */
    public static final PotGradient GRAY = linear(90)
        .addStop(0, PotColor.hex("737373"))
        .addStop(1, PotColor.hex("A6A6A6"));

    // ==================== Gradient Type Enumeration ====================

    /**
     * Defines the geometric type of the gradient fill.
     *
     * @since 1.0
     */
    public enum Type {
        /** A linear gradient that transitions colors along a straight line at a specified angle. */
        LINEAR,
        /** A radial gradient that transitions colors outward from a central point. */
        RADIAL,
        /** A rectangular (or diamond) gradient that transitions colors from the center to the corners. */
        RECTANGULAR,
        /** A gradient that follows the outline path of the shape. */
        PATH
    }

    // ==================== Instance Fields ====================

    private final Type type;
    private final double angle; // 0-360 degrees
    private final List<GradientStop> stops;
    private final boolean rotateWithShape; // Whether the gradient rotates when the shape is rotated

    // ==================== Constructors ====================

    private PotGradient(Type type, double angle, List<GradientStop> stops, boolean rotateWithShape) {
        this.type = type;
        this.angle = normalizeAngle(angle);
        this.stops = stops != null ? new ArrayList<>(stops) : new ArrayList<>();
        this.rotateWithShape = rotateWithShape;
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new linear gradient with the specified angle.
     *
     * <p>The gradient initially contains no color stops; use {@link #addStop(double, PotColor)} to define them.
     * The angle is normalized to the range [0, 360) degrees, where 0 degrees points to the right (east)
     * and 90 degrees points upward (north).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.linear(45)
     *     .addStop(0, PotColor.RED)
     *     .addStop(1, PotColor.BLUE);
     * }</pre>
     *
     * @param angle the direction of the gradient in degrees (0 = horizontal left-to-right, 90 = vertical bottom-to-top)
     * @return a new linear PotGradient instance
     * @since 1.0
     */
    public static PotGradient linear(double angle) {
        return new PotGradient(Type.LINEAR, angle, new ArrayList<>(), true);
    }

    /**
     * Creates a new horizontal left-to-right linear gradient.
     *
     * <p>This is a convenience method equivalent to {@code linear(0)}. The gradient initially contains no color stops.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.linear();
     * }</pre>
     *
     * @return a new horizontal linear PotGradient instance
     * @since 1.0
     */
    public static PotGradient linear() {
        return linear(0);
    }

    /**
     * Creates a new radial gradient.
     *
     * <p>The gradient transitions colors outward from the center of the shape. It initially contains no color stops.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.radial()
     *     .addStop(0, PotColor.WHITE)
     *     .addStop(1, PotColor.BLACK);
     * }</pre>
     *
     * @return a new radial PotGradient instance
     * @since 1.0
     */
    public static PotGradient radial() {
        return new PotGradient(Type.RADIAL, 0, new ArrayList<>(), true);
    }

    /**
     * Creates a new rectangular (diamond) gradient.
     *
     * <p>The gradient transitions colors from the center of the shape toward its corners. It initially contains no color stops.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.rectangular()
     *     .addStop(0, PotColor.YELLOW)
     *     .addStop(1, PotColor.GREEN);
     * }</pre>
     *
     * @return a new rectangular PotGradient instance
     * @since 1.0
     */
    public static PotGradient rectangular() {
        return new PotGradient(Type.RECTANGULAR, 0, new ArrayList<>(), true);
    }

    /**
     * Creates a new path gradient.
     *
     * <p>The gradient follows the outline path of the shape. It initially contains no color stops.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.path()
     *     .addStop(0, PotColor.CYAN)
     *     .addStop(1, PotColor.MAGENTA);
     * }</pre>
     *
     * @return a new path PotGradient instance
     * @since 1.0
     */
    public static PotGradient path() {
        return new PotGradient(Type.PATH, 0, new ArrayList<>(), true);
    }

    /**
     * Creates a simple two-color linear gradient with the specified angle and colors.
     *
     * <p>This is a convenience method that creates a linear gradient with two stops at positions 0.0 and 1.0.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.simple(90, PotColor.RED, PotColor.BLUE);
     * }</pre>
     *
     * @param angle the direction of the gradient in degrees
     * @param startColor the color at the start of the gradient (position 0.0)
     * @param endColor the color at the end of the gradient (position 1.0)
     * @return a new linear PotGradient with two color stops
     * @throws NullPointerException if startColor or endColor is null
     * @since 1.0
     */
    public static PotGradient simple(double angle, PotColor startColor, PotColor endColor) {
        return linear(angle)
            .addStop(0, startColor)
            .addStop(1, endColor);
    }

    /**
     * Creates a horizontal left-to-right two-color linear gradient.
     *
     * <p>This is a convenience method equivalent to {@code simple(0, startColor, endColor)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.horizontal(PotColor.RED, PotColor.BLUE);
     * }</pre>
     *
     * @param startColor the color at the left side (position 0.0)
     * @param endColor the color at the right side (position 1.0)
     * @return a new horizontal linear PotGradient with two color stops
     * @throws NullPointerException if startColor or endColor is null
     * @since 1.0
     */
    public static PotGradient horizontal(PotColor startColor, PotColor endColor) {
        return simple(0, startColor, endColor);
    }

    /**
     * Creates a vertical bottom-to-top two-color linear gradient.
     *
     * <p>This is a convenience method equivalent to {@code simple(90, startColor, endColor)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.vertical(PotColor.RED, PotColor.BLUE);
     * }</pre>
     *
     * @param startColor the color at the bottom (position 0.0)
     * @param endColor the color at the top (position 1.0)
     * @return a new vertical linear PotGradient with two color stops
     * @throws NullPointerException if startColor or endColor is null
     * @since 1.0
     */
    public static PotGradient vertical(PotColor startColor, PotColor endColor) {
        return simple(90, startColor, endColor);
    }

    /**
     * Creates a diagonal (45-degree) two-color linear gradient.
     *
     * <p>This is a convenience method equivalent to {@code simple(45, startColor, endColor)}.
     * The gradient runs from the bottom-left to the top-right.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.diagonal(PotColor.RED, PotColor.BLUE);
     * }</pre>
     *
     * @param startColor the color at the bottom-left (position 0.0)
     * @param endColor the color at the top-right (position 1.0)
     * @return a new diagonal linear PotGradient with two color stops
     * @throws NullPointerException if startColor or endColor is null
     * @since 1.0
     */
    public static PotGradient diagonal(PotColor startColor, PotColor endColor) {
        return simple(45, startColor, endColor);
    }

    // ==================== Immutable Modifier Methods ====================

    /**
     * Adds a color stop with full opacity to this gradient, returning a new gradient.
     *
     * <p>The position is clamped to the range [0.0, 1.0]. The new stop is appended to the existing list.
     * This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient newGradient = originalGradient.addStop(0.5, PotColor.GREEN);
     * }</pre>
     *
     * @param position the position along the gradient path (0.0 = start, 1.0 = end)
     * @param color the color at this position
     * @return a new PotGradient instance with the added stop
     * @throws NullPointerException if color is null
     * @since 1.0
     */
    public PotGradient addStop(double position, PotColor color) {
        List<GradientStop> newStops = new ArrayList<>(this.stops);
        newStops.add(new GradientStop(position, color));
        return new PotGradient(type, angle, newStops, rotateWithShape);
    }

    /**
     * Returns a new gradient with the specified stop added at the given position, color, and opacity.
     *
     * <p>This method creates a new immutable gradient by appending a stop to the current list of stops.
     * The stop is defined by a position along the gradient's axis (from 0.0 at the start to 1.0 at the end),
     * a color, and an opacity value. The original gradient is not modified.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = PotGradient.linear(90)
     *     .addStop(0.0, PotColor.BLUE, 1.0)
     *     .addStop(0.5, PotColor.CYAN, 0.7)
     *     .addStop(1.0, PotColor.WHITE, 0.3);
     * }</pre>
     *
     * @param position the position of the stop along the gradient axis, between 0.0 and 1.0 inclusive
     * @param color    the color of the stop at the specified position; must not be null
     * @param opacity  the opacity of the stop at the specified position, between 0.0 (transparent) and 1.0 (opaque) inclusive
     * @return a new {@code PotGradient} instance with the added stop
     * @see #addStop(double, PotColor)
     */
    public PotGradient addStop(double position, PotColor color, double opacity) {
        List<GradientStop> newStops = new ArrayList<>(this.stops);
        newStops.add(new GradientStop(position, color, opacity));
        return new PotGradient(type, angle, newStops, rotateWithShape);
    }

    /**
     * Returns a new gradient with the specified rotation angle.
     *
     * <p>This method creates a new immutable gradient with the given angle, while preserving the type,
     * stops, and rotation-with-shape property of the original gradient. The angle is normalized to the
     * range [0, 360) degrees. The original gradient is not modified.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient original = PotGradient.horizontal(PotColor.RED, PotColor.BLUE);
     * PotGradient rotated = original.withAngle(45); // Creates a 45-degree diagonal gradient
     * }</pre>
     *
     * @param angle the new rotation angle for the gradient, in degrees
     * @return a new {@code PotGradient} instance with the specified angle
     */
    public PotGradient withAngle(double angle) {
        return new PotGradient(type, angle, stops, rotateWithShape);
    }

    /**
     * Returns a new gradient with the specified rotation-with-shape behavior.
     *
     * <p>This method creates a new immutable gradient with the given {@code rotateWithShape} flag,
     * while preserving the type, angle, and stops of the original gradient. The original gradient is not modified.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient original = PotGradient.linear(30);
     * PotGradient fixed = original.rotateWithShape(false); // Gradient angle remains fixed relative to the page
     * }</pre>
     *
     * @param rotateWithShape {@code true} if the gradient should rotate with the shape it fills;
     *                        {@code false} if the gradient angle should remain fixed relative to the page
     * @return a new {@code PotGradient} instance with the specified rotation behavior
     */
    public PotGradient rotateWithShape(boolean rotateWithShape) {
        return new PotGradient(type, angle, stops, rotateWithShape);
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the type of this gradient.
     *
     * @return the gradient type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the rotation angle of this gradient.
     *
     * <p>The angle is normalized to the range [0, 360) degrees.</p>
     *
     * @return the gradient angle in degrees
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Returns an unmodifiable view of the gradient stops.
     *
     * @return a list of gradient stops; never null
     */
    public List<GradientStop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    /**
     * Returns whether this gradient rotates with the shape it fills.
     *
     * @return {@code true} if the gradient rotates with the shape; {@code false} if the angle is fixed relative to the page
     */
    public boolean isRotateWithShape() {
        return rotateWithShape;
    }

    // ==================== Utility Methods ====================

    private static double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle < 0) angle += 360;
        return angle;
    }

    // ==================== Inner Class: GradientStop ====================

    /**
     * Represents a single color stop within a gradient.
     *
     * <p>A gradient stop defines a color and opacity at a specific position along the gradient's axis.
     * Multiple stops are interpolated to create the smooth color transition of the gradient.</p>
     *
     * @author Quaternion8192
     * @since 1.0
     */
    public static class GradientStop {
        private final double position; // 0.0-1.0
        private final PotColor color;
        private final double opacity;  // 0.0-1.0

        /**
         * Constructs a fully opaque gradient stop.
         *
         * @param position the position along the gradient axis, between 0.0 and 1.0 inclusive
         * @param color    the color at this position; must not be null
         */
        public GradientStop(double position, PotColor color) {
            this(position, color, 1.0);
        }

        /**
         * Constructs a gradient stop with the specified position, color, and opacity.
         *
         * @param position the position along the gradient axis, between 0.0 and 1.0 inclusive
         * @param color    the color at this position; must not be null
         * @param opacity  the opacity at this position, between 0.0 and 1.0 inclusive
         */
        public GradientStop(double position, PotColor color, double opacity) {
            this.position = Math.max(0, Math.min(1, position));
            this.color = color != null ? color : PotColor.BLACK;
            this.opacity = Math.max(0, Math.min(1, opacity));
        }

        /**
         * Returns the position of this stop along the gradient axis.
         *
         * @return the position, between 0.0 and 1.0 inclusive
         */
        public double getPosition() {
            return position;
        }

        /**
         * Returns the color of this stop.
         *
         * @return the color; never null
         */
        public PotColor getColor() {
            return color;
        }

        /**
         * Returns the opacity of this stop.
         *
         * @return the opacity, between 0.0 and 1.0 inclusive
         */
        public double getOpacity() {
            return opacity;
        }

        /**
         * Returns the position as an integer percentage scaled for OpenXML.
         *
         * <p>This method converts the position (0.0 to 1.0) to an integer in the range 0 to 100000,
         * which is the representation used by the OpenXML specification for gradient stop positions.</p>
         *
         * @return the position as an integer percentage (0 to 100000)
         */
        public int getPositionPercent() {
            return (int) Math.round(position * 100000);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GradientStop that = (GradientStop) o;
            return Double.compare(that.position, position) == 0 &&
                   Double.compare(that.opacity, opacity) == 0 &&
                   Objects.equals(color, that.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, color, opacity);
        }

        @Override
        public String toString() {
            return String.format("Stop{pos=%.2f, color=%s, opacity=%.2f}",
                                position, color, opacity);
        }
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotGradient that = (PotGradient) o;
        return Double.compare(that.angle, angle) == 0 &&
               rotateWithShape == that.rotateWithShape &&
               type == that.type &&
               Objects.equals(stops, that.stops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, angle, stops, rotateWithShape);
    }

    @Override
    public String toString() {
        return String.format("PotGradient{type=%s, angle=%.1f, stops=%d}", 
                            type, angle, stops.size());
    }
}
