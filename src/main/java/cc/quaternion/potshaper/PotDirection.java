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
 * Represents a direction used for shaping or positioning elements, such as pots or text.
 *
 * <p>This enumeration defines a comprehensive set of directional constants, including
 * cardinal directions, diagonal directions, and abstract directional concepts like inward,
 * outward, and rotational directions. It provides utility methods to query directional
 * properties, obtain opposite directions, calculate angles, and convert to related
 * external direction representations.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public enum PotDirection {

    // ==================== Cardinal Directions (From Origin) ====================

    /** Direction originating from the top. */
    FROM_TOP,

    /** Direction originating from the bottom. */
    FROM_BOTTOM,

    /** Direction originating from the left. */
    FROM_LEFT,

    /** Direction originating from the right. */
    FROM_RIGHT,

    /** Direction pointing upward. */
    UP,

    /** Direction pointing downward. */
    DOWN,

    /** Direction pointing leftward. */
    LEFT,

    /** Direction pointing rightward. */
    RIGHT,

    // ==================== Diagonal Directions (From Origin) ====================

    /** Direction originating from the top-left corner. */
    FROM_TOP_LEFT,

    /** Direction originating from the top-right corner. */
    FROM_TOP_RIGHT,

    /** Direction originating from the bottom-left corner. */
    FROM_BOTTOM_LEFT,

    /** Direction originating from the bottom-right corner. */
    FROM_BOTTOM_RIGHT,

    // ==================== Abstract Directional Concepts ====================

    /** General horizontal direction. */
    HORIZONTAL,

    /** General vertical direction. */
    VERTICAL,

    /** Direction moving inward, towards a center or focus. */
    IN,

    /** Direction moving outward, away from a center or focus. */
    OUT,

    /** Rotational direction moving clockwise. */
    CLOCKWISE,

    /** Rotational direction moving counter-clockwise. */
    COUNTER_CLOCKWISE,

    // ==================== Combined Directions ====================

    /** Horizontal direction moving inward. */
    HORIZONTAL_IN,

    /** Horizontal direction moving outward. */
    HORIZONTAL_OUT,

    /** Vertical direction moving inward. */
    VERTICAL_IN,

    /** Vertical direction moving outward. */
    VERTICAL_OUT,

    /** Represents the absence of a specific direction. */
    NONE;

    /**
     * Returns the direction opposite to this one.
     *
     * <p>For each defined direction, this method returns its logical opposite.
     * For example, {@code FROM_TOP} returns {@code FROM_BOTTOM}, {@code LEFT} returns
     * {@code FROM_RIGHT}, {@code IN} returns {@code OUT}, and {@code CLOCKWISE} returns
     * {@code COUNTER_CLOCKWISE}. Directions that do not have a logical opposite,
     * such as {@code NONE}, return themselves.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotDirection dir = PotDirection.LEFT;
     * PotDirection oppositeDir = dir.opposite(); // Returns PotDirection.FROM_RIGHT
     * }</pre>
     *
     * @return the direction opposite to this one, or this direction itself if no opposite is defined
     */
    public PotDirection opposite() {
        switch (this) {
            case FROM_TOP:
            case UP:
                return FROM_BOTTOM;
            case FROM_BOTTOM:
            case DOWN:
                return FROM_TOP;
            case FROM_LEFT:
            case LEFT:
                return FROM_RIGHT;
            case FROM_RIGHT:
            case RIGHT:
                return FROM_LEFT;
            case FROM_TOP_LEFT:
                return FROM_BOTTOM_RIGHT;
            case FROM_TOP_RIGHT:
                return FROM_BOTTOM_LEFT;
            case FROM_BOTTOM_LEFT:
                return FROM_TOP_RIGHT;
            case FROM_BOTTOM_RIGHT:
                return FROM_TOP_LEFT;
            case IN:
                return OUT;
            case OUT:
                return IN;
            case CLOCKWISE:
                return COUNTER_CLOCKWISE;
            case COUNTER_CLOCKWISE:
                return CLOCKWISE;
            case HORIZONTAL_IN:
                return HORIZONTAL_OUT;
            case HORIZONTAL_OUT:
                return HORIZONTAL_IN;
            case VERTICAL_IN:
                return VERTICAL_OUT;
            case VERTICAL_OUT:
                return VERTICAL_IN;
            default:
                return this;
        }
    }

    /**
     * Determines if this direction is considered horizontal.
     *
     * <p>A direction is considered horizontal if it is explicitly a left or right direction,
     * or if it is a general horizontal direction (including inward/outward variants).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotDirection dir1 = PotDirection.LEFT;
     * boolean result1 = dir1.isHorizontal(); // Returns true
     *
     * PotDirection dir2 = PotDirection.UP;
     * boolean result2 = dir2.isHorizontal(); // Returns false
     * }</pre>
     *
     * @return {@code true} if this direction is horizontal, {@code false} otherwise
     */
    public boolean isHorizontal() {
        return this == FROM_LEFT || this == FROM_RIGHT ||
               this == LEFT || this == RIGHT ||
               this == HORIZONTAL || this == HORIZONTAL_IN || this == HORIZONTAL_OUT;
    }

    /**
     * Determines if this direction is considered vertical.
     *
     * <p>A direction is considered vertical if it is explicitly a top or bottom direction,
     * or if it is a general vertical direction (including inward/outward variants).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotDirection dir1 = PotDirection.UP;
     * boolean result1 = dir1.isVertical(); // Returns true
     *
     * PotDirection dir2 = PotDirection.LEFT;
     * boolean result2 = dir2.isVertical(); // Returns false
     * }</pre>
     *
     * @return {@code true} if this direction is vertical, {@code false} otherwise
     */
    public boolean isVertical() {
        return this == FROM_TOP || this == FROM_BOTTOM ||
               this == UP || this == DOWN ||
               this == VERTICAL || this == VERTICAL_IN || this == VERTICAL_OUT;
    }

    /**
     * Determines if this direction is considered diagonal.
     *
     * <p>A direction is considered diagonal if it originates from a corner
     * (top-left, top-right, bottom-left, or bottom-right).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotDirection dir1 = PotDirection.FROM_TOP_LEFT;
     * boolean result1 = dir1.isDiagonal(); // Returns true
     *
     * PotDirection dir2 = PotDirection.UP;
     * boolean result2 = dir2.isDiagonal(); // Returns false
     * }</pre>
     *
     * @return {@code true} if this direction is diagonal, {@code false} otherwise
     */
    public boolean isDiagonal() {
        return this == FROM_TOP_LEFT || this == FROM_TOP_RIGHT ||
               this == FROM_BOTTOM_LEFT || this == FROM_BOTTOM_RIGHT;
    }

    /**
     * Returns the angle in degrees corresponding to this direction, assuming a standard
     * mathematical coordinate system (0 degrees pointing right, increasing counter-clockwise).
     *
     * <p>Only cardinal and diagonal directions have defined angles. For other directions
     * (e.g., {@code HORIZONTAL}, {@code IN}, {@code NONE}), this method returns 0.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotDirection dir = PotDirection.DOWN;
     * int angle = dir.getAngle(); // Returns 90
     * }</pre>
     *
     * @return the angle in degrees for this direction, or 0 if not applicable
     */
    public int getAngle() {
        switch (this) {
            case FROM_RIGHT:
            case RIGHT:
                return 0;
            case FROM_BOTTOM_RIGHT:
                return 45;
            case FROM_BOTTOM:
            case DOWN:
                return 90;
            case FROM_BOTTOM_LEFT:
                return 135;
            case FROM_LEFT:
            case LEFT:
                return 180;
            case FROM_TOP_LEFT:
                return 225;
            case FROM_TOP:
            case UP:
                return 270;
            case FROM_TOP_RIGHT:
                return 315;
            default:
                return 0;
        }
    }

    /**
     * Converts this direction to an Apache POI TextDirection constant.
     *
     * <p>This method maps general horizontal and vertical directions to their
     * corresponding POI text direction constants. All other directions default to
     * {@code HORIZONTAL}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotDirection dir = PotDirection.VERTICAL;
     * TextShape.TextDirection poiDir = dir.toPoiTextDirection();
     * // poiDir is TextShape.TextDirection.VERTICAL
     * }</pre>
     *
     * @return the corresponding Apache POI TextDirection, defaulting to HORIZONTAL
     * @see org.apache.poi.sl.usermodel.TextShape.TextDirection
     */
    public org.apache.poi.sl.usermodel.TextShape.TextDirection toPoiTextDirection() {
        switch (this) {
            case HORIZONTAL:
                return org.apache.poi.sl.usermodel.TextShape.TextDirection.HORIZONTAL;
            case VERTICAL:
                return org.apache.poi.sl.usermodel.TextShape.TextDirection.VERTICAL;
            default:
                return org.apache.poi.sl.usermodel.TextShape.TextDirection.HORIZONTAL;
        }
    }
}