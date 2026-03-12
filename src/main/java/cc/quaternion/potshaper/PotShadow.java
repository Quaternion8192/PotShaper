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
 * Represents a visual shadow effect for a shape or text element.
 *
 * <p>This immutable class defines the properties of a shadow, including its type (outer, inner,
 * perspective, or none), color, blur radius, offset distance, angle, and scaling factors.
 * It provides factory methods for creating common shadow presets and fluent builders for
 * customization. All measurements are in points unless otherwise specified.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotShadow {

    // ==================== Predefined Instances ====================

    /**
     * A shadow representing no visual effect.
     */
    public static final PotShadow NONE = new PotShadow(Type.NONE, null, 0, 0, 0, 0, 0);

    /**
     * A standard outer shadow offset to the bottom-right.
     */
    public static final PotShadow OFFSET_DIAGONAL_BOTTOM_RIGHT = outer()
        .withColor(PotColor.BLACK.withOpacity(0.4))
        .withBlur(4)
        .withDistance(3)
        .withAngle(45);

    /**
     * A standard outer shadow offset directly to the bottom.
     */
    public static final PotShadow OFFSET_BOTTOM = outer()
        .withColor(PotColor.BLACK.withOpacity(0.4))
        .withBlur(4)
        .withDistance(3)
        .withAngle(90);

    /**
     * A standard outer shadow centered behind the element.
     */
    public static final PotShadow OFFSET_CENTER = outer()
        .withColor(PotColor.BLACK.withOpacity(0.4))
        .withBlur(8)
        .withDistance(0)
        .withAngle(0);

    /**
     * A perspective shadow offset to the bottom-right with vertical scaling.
     */
    public static final PotShadow PERSPECTIVE_DIAGONAL_BOTTOM_RIGHT = perspective()
        .withColor(PotColor.BLACK.withOpacity(0.5))
        .withBlur(6)
        .withDistance(5)
        .withAngle(45);

    /**
     * A standard inner shadow offset to the bottom-right.
     */
    public static final PotShadow INSIDE_DIAGONAL_BOTTOM_RIGHT = inner()
        .withColor(PotColor.BLACK.withOpacity(0.4))
        .withBlur(4)
        .withDistance(2)
        .withAngle(45);

    // ==================== Shadow Type Enumeration ====================

    /**
     * Defines the visual type of a shadow effect.
     *
     * @since 1.0
     */
    public enum Type {
        /**
         * Represents the absence of a shadow effect.
         */
        NONE,
        /**
         * A shadow cast outside the boundaries of the element.
         */
        OUTER,
        /**
         * A shadow cast inside the boundaries of the element.
         */
        INNER,
        /**
         * A shadow that simulates a three-dimensional perspective, often with scaling.
         */
        PERSPECTIVE
    }

    // ==================== Instance Fields ====================

    private final Type type;
    private final PotColor color;
    private final double blur;      // Blur radius in points
    private final double distance;  // Offset distance in points
    private final double angle;     // Offset angle in degrees
    private final double scaleX;    // Horizontal scale factor
    private final double scaleY;    // Vertical scale factor

    // ==================== Constructors ====================

    /**
     * Constructs a new shadow with the specified properties.
     *
     * <p>This constructor is private; use the static factory methods ({@link #outer()},
     * {@link #inner()}, {@link #perspective()}, {@link #none()}) and builder methods
     * (e.g., {@link #withColor(PotColor)}) to create instances. Negative blur and distance
     * values are clamped to zero. The angle is normalized to the range [0, 360). Scale
     * factors must be positive; non-positive values default to 1.0.</p>
     *
     * @param type     the visual type of the shadow
     * @param color    the color of the shadow; if null, defaults to black with 40% opacity
     * @param blur     the blur radius in points
     * @param distance the offset distance in points
     * @param angle    the offset angle in degrees
     * @param scaleX   the horizontal scale factor
     * @param scaleY   the vertical scale factor
     */
    private PotShadow(Type type, PotColor color, double blur, double distance,
                      double angle, double scaleX, double scaleY) {
        this.type = type;
        this.color = color != null ? color : PotColor.BLACK.withOpacity(0.4);
        this.blur = Math.max(0, blur);
        this.distance = Math.max(0, distance);
        this.angle = normalizeAngle(angle);
        this.scaleX = scaleX > 0 ? scaleX : 1.0;
        this.scaleY = scaleY > 0 ? scaleY : 1.0;
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a new outer shadow with default properties.
     *
     * <p>The default outer shadow is black with 40% opacity, has a blur radius of 4 points,
     * is offset by 3 points at a 45-degree angle, and has no scaling.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.outer()
     *     .withColor(PotColor.RED)
     *     .withDistance(10);
     * }</pre>
     *
     * @return a new mutable outer shadow builder with default properties
     * @since 1.0
     */
    public static PotShadow outer() {
        return new PotShadow(Type.OUTER, PotColor.BLACK.withOpacity(0.4), 4, 3, 45, 1, 1);
    }

    /**
     * Creates a new inner shadow with default properties.
     *
     * <p>The default inner shadow is black with 40% opacity, has a blur radius of 4 points,
     * is offset by 2 points at a 45-degree angle, and has no scaling.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.inner()
     *     .withBlur(8)
     *     .withOpacity(0.6);
     * }</pre>
     *
     * @return a new mutable inner shadow builder with default properties
     * @since 1.0
     */
    public static PotShadow inner() {
        return new PotShadow(Type.INNER, PotColor.BLACK.withOpacity(0.4), 4, 2, 45, 1, 1);
    }

    /**
     * Creates a new perspective shadow with default properties.
     *
     * <p>The default perspective shadow is black with 50% opacity, has a blur radius of 6 points,
     * is offset by 5 points at a 45-degree angle, and is scaled to 100% horizontally and 50%
     * vertically.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.perspective()
     *     .withScale(1.2, 0.3)
     *     .withAngle(30);
     * }</pre>
     *
     * @return a new mutable perspective shadow builder with default properties
     * @since 1.0
     */
    public static PotShadow perspective() {
        return new PotShadow(Type.PERSPECTIVE, PotColor.BLACK.withOpacity(0.5), 6, 5, 45, 1, 0.5);
    }

    /**
     * Returns the singleton instance representing no shadow.
     *
     * <p>This is functionally equivalent to the {@link #NONE} constant.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.none();
     * }</pre>
     *
     * @return the predefined {@link #NONE} shadow instance
     * @see #NONE
     * @since 1.0
     */
    public static PotShadow none() {
        return NONE;
    }

    // ==================== Fluent Builder Methods ====================

    /**
     * Returns a new shadow with the specified color, leaving all other properties unchanged.
     *
     * <p>This method creates a new immutable instance; the original shadow is not modified.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow blueShadow = originalShadow.withColor(PotColor.BLUE);
     * }</pre>
     *
     * @param color the new color for the shadow
     * @return a new shadow instance with the updated color
     * @since 1.0
     */
    public PotShadow withColor(PotColor color) {
        return new PotShadow(type, color, blur, distance, angle, scaleX, scaleY);
    }

    /**
     * Returns a new shadow with the specified color, leaving all other properties unchanged.
     *
     * <p>This is a fluent alias for {@link #withColor(PotColor)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.outer().color(PotColor.GREEN);
     * }</pre>
     *
     * @param color the new color for the shadow
     * @return a new shadow instance with the updated color
     * @see #withColor(PotColor)
     * @since 1.0
     */
    public PotShadow color(PotColor color) {
        return withColor(color);
    }

    /**
     * Returns a new shadow with the specified blur radius, leaving all other properties unchanged.
     *
     * <p>This method creates a new immutable instance; the original shadow is not modified.
     * The blur radius is clamped to a minimum of zero.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow softShadow = originalShadow.withBlur(10.0);
     * }</pre>
     *
     * @param blur the new blur radius in points
     * @return a new shadow instance with the updated blur radius
     * @since 1.0
     */
    public PotShadow withBlur(double blur) {
        return new PotShadow(type, color, blur, distance, angle, scaleX, scaleY);
    }

    /**
     * Returns a new shadow with the specified offset distance, leaving all other properties unchanged.
     *
     * <p>This method creates a new immutable instance; the original shadow is not modified.
     * The distance is clamped to a minimum of zero.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow distantShadow = originalShadow.withDistance(15.0);
     * }</pre>
     *
     * @param distance the new offset distance in points
     * @return a new shadow instance with the updated distance
     * @since 1.0
     */
    public PotShadow withDistance(double distance) {
        return new PotShadow(type, color, blur, distance, angle, scaleX, scaleY);
    }

    /**
     * Returns a new shadow with the specified offset distance, leaving all other properties unchanged.
     *
     * <p>This is a fluent alias for {@link #withDistance(double)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.inner().distance(5.0);
     * }</pre>
     *
     * @param distance the new offset distance in points
     * @return a new shadow instance with the updated distance
     * @see #withDistance(double)
     * @since 1.0
     */
    public PotShadow distance(double distance) {
        return withDistance(distance);
    }

    /**
     * Returns a new shadow with the specified offset angle, leaving all other properties unchanged.
     *
     * <p>This method creates a new immutable instance; the original shadow is not modified.
     * The angle is normalized to the range [0, 360) degrees.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow leftShadow = originalShadow.withAngle(180.0);
     * }</pre>
     *
     * @param angle the new offset angle in degrees
     * @return a new shadow instance with the updated angle
     * @since 1.0
     */
    public PotShadow withAngle(double angle) {
        return new PotShadow(type, color, blur, distance, angle, scaleX, scaleY);
    }

    /**
     * Returns a new shadow with the specified offset angle, leaving all other properties unchanged.
     *
     * <p>This is a fluent alias for {@link #withAngle(double)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.outer().angle(90.0);
     * }</pre>
     *
     * @param angle the new offset angle in degrees
     * @return a new shadow instance with the updated angle
     * @see #withAngle(double)
     * @since 1.0
     */
    public PotShadow angle(double angle) {
        return withAngle(angle);
    }

    /**
     * Returns a new shadow with the specified blur radius, leaving all other properties unchanged.
     *
     * <p>This is a fluent alias for {@link #withBlur(double)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = PotShadow.perspective().blur(12.0);
     * }</pre>
     *
     * @param blur the new blur radius in points
     * @return a new shadow instance with the updated blur radius
     * @see #withBlur(double)
     * @since 1.0
     */
    public PotShadow blur(double blur) {
        return withBlur(blur);
    }

    /**
     * Returns a new shadow with the specified opacity, leaving all other properties unchanged.
     *
     * <p>This method creates a new immutable instance; the original shadow is not modified.
     * The opacity is applied to the current shadow color.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow faintShadow = originalShadow.withOpacity(0.2);
     * }</pre>
     *
     * @param opacity the new opacity value, typically between 0.0 (transparent) and 1.0 (opaque)
     * @return a new shadow instance with the updated opacity
     * @since 1.0
     */
    public PotShadow withOpacity(double opacity) {
        return new PotShadow(type, color.withOpacity(opacity), blur, distance, angle, scaleX, scaleY);
    }

    /**
     * Returns a new shadow with the specified scaling factors, leaving all other properties unchanged.
     *
     * <p>This method creates a new immutable instance; the original shadow is not modified.
     * Scaling is primarily relevant for perspective shadows. Scale factors must be positive;
     * non-positive values default to 1.0 in the constructor.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow scaledShadow = originalShadow.withScale(1.5, 0.8);
     * }</pre>
     *
     * @param scaleX the new horizontal scale factor
     * @param scaleY the new vertical scale factor
     * @return a new shadow instance with the updated scaling factors
     * @since 1.0
     */
    public PotShadow withScale(double scaleX, double scaleY) {
        return new PotShadow(type, color, blur, distance, angle, scaleX, scaleY);
    }

    /**
     * Returns a new shadow with the offset angle derived from the specified direction.
     *
     * <p>This method creates a new immutable instance; the original shadow is not modified.
     * If the provided direction is null, the angle defaults to 45 degrees.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow topShadow = originalShadow.withDirection(PotDirection.TOP);
     * }</pre>
     *
     * @param direction the cardinal or intercardinal direction defining the shadow's angle
     * @return a new shadow instance with the updated angle
     * @since 1.0
     */
    public PotShadow withDirection(PotDirection direction) {
        double newAngle = direction != null ? direction.getAngle() : 45;
        return new PotShadow(type, color, blur, distance, newAngle, scaleX, scaleY);
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the type of this shadow.
     *
     * @return the shadow type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the color of this shadow.
     *
     * @return the shadow color
     */
    public PotColor getColor() {
        return color;
    }

    /**
     * Returns the blur radius of this shadow in points.
     *
     * @return the blur radius in points
     */
    public double getBlur() {
        return blur;
    }

    /**
     * Returns the distance of this shadow.
     *
     * @return the distance in points
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the angle of this shadow.
     *
     * @return the angle in degrees, normalized to the range [0, 360)
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Returns the X-axis scaling factor of this shadow.
     *
     * @return the scaling factor along the X-axis
     */
    public double getScaleX() {
        return scaleX;
    }

    /**
     * Returns the Y-axis scaling factor of this shadow.
     *
     * @return the scaling factor along the Y-axis
     */
    public double getScaleY() {
        return scaleY;
    }

    /**
     * Checks if this shadow is of type {@code NONE}.
     *
     * <p>A {@code NONE} shadow typically indicates no visual effect should be applied.</p>
     *
     * @return {@code true} if the shadow type is {@code Type.NONE}; {@code false} otherwise
     */
    public boolean isNone() {
        return type == Type.NONE;
    }

    /**
     * Returns the blur radius of this shadow in English Metric Units (EMU).
     *
     * <p>This is a convenience method for contexts requiring EMU values, such as certain document formats.</p>
     *
     * @return the blur radius converted to EMU
     * @see UnitConverter#pointsToEmu(double)
     */
    public long getBlurEmu() {
        return UnitConverter.pointsToEmu(blur);
    }

    /**
     * Returns the distance of this shadow in English Metric Units (EMU).
     *
     * <p>This is a convenience method for contexts requiring EMU values, such as certain document formats.</p>
     *
     * @return the distance converted to EMU
     * @see UnitConverter#pointsToEmu(double)
     */
    public long getDistanceEmu() {
        return UnitConverter.pointsToEmu(distance);
    }

    /**
     * Returns the angle of this shadow in EMU angle units.
     *
     * <p>In the EMU angle system, 60000 units represent 1 degree. This is a convenience method for
     * contexts requiring EMU values, such as certain document formats.</p>
     *
     * @return the angle converted to EMU angle units
     * @see UnitConverter#degreesToEmuAngle(double)
     */
    public int getAngleEmu() {
        return UnitConverter.degreesToEmuAngle(angle);
    }

    /**
     * Calculates the horizontal offset of the shadow based on its distance and angle.
     *
     * <p>The offset is calculated as {@code distance * cos(angle)}.</p>
     *
     * @return the horizontal offset in points
     */
    public double getOffsetX() {
        return distance * Math.cos(Math.toRadians(angle));
    }

    /**
     * Calculates the vertical offset of the shadow based on its distance and angle.
     *
     * <p>The offset is calculated as {@code distance * sin(angle)}.</p>
     *
     * @return the vertical offset in points
     */
    public double getOffsetY() {
        return distance * Math.sin(Math.toRadians(angle));
    }

    // ==================== Utility Methods ====================

    private static double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle < 0) angle += 360;
        return angle;
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotShadow potShadow = (PotShadow) o;
        return Double.compare(potShadow.blur, blur) == 0 &&
               Double.compare(potShadow.distance, distance) == 0 &&
               Double.compare(potShadow.angle, angle) == 0 &&
               Double.compare(potShadow.scaleX, scaleX) == 0 &&
               Double.compare(potShadow.scaleY, scaleY) == 0 &&
               type == potShadow.type &&
               Objects.equals(color, potShadow.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color, blur, distance, angle, scaleX, scaleY);
    }

    @Override
    public String toString() {
        if (isNone()) {
            return "PotShadow{NONE}";
        }
        return String.format("PotShadow{type=%s, color=%s, blur=%.1f, dist=%.1f, angle=%.1f}",
                            type, color, blur, distance, angle);
    }
}
