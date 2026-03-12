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
 * Enumerates the types of shapes available for use in presentation slides.
 *
 * <p>This enum defines a comprehensive set of shape types, categorized into groups such as
 * basic geometric shapes, arrows, flowchart symbols, callouts, stars, action buttons, and connectors.
 * Each constant represents a distinct visual shape that can be instantiated and manipulated
 * within a slide. The enum also provides utility methods to determine the category of a given shape.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public enum PotShapeType {

    // ==================== Basic Shapes ====================

    /** A standard rectangle with four right angles. */
    RECTANGLE,
    /** A rectangle with rounded corners. */
    ROUNDED_RECTANGLE,
    /** A rectangle with one pair of opposite corners snipped off. */
    SNIP_ROUND_RECTANGLE,
    /** A rectangle with a single corner snipped. */
    SNIP_1_RECTANGLE,
    /** A rectangle with two adjacent corners snipped identically. */
    SNIP_2_SAME_RECTANGLE,
    /** A rectangle with two opposite corners snipped diagonally. */
    SNIP_2_DIAGONAL_RECTANGLE,
    /** A rectangle with a single rounded corner. */
    ROUND_1_RECTANGLE,
    /** A rectangle with two adjacent corners rounded identically. */
    ROUND_2_SAME_RECTANGLE,
    /** A rectangle with two opposite corners rounded diagonally. */
    ROUND_2_DIAGONAL_RECTANGLE,
    /** A standard ellipse or circle. */
    ELLIPSE,
    /** An equilateral triangle. */
    TRIANGLE,
    /** A right-angled triangle. */
    RIGHT_TRIANGLE,
    /** A parallelogram with slanted sides. */
    PARALLELOGRAM,
    /** A trapezoid with one pair of parallel sides. */
    TRAPEZOID,
    /** A diamond shape, typically a rotated square. */
    DIAMOND,
    /** A five-sided polygon. */
    PENTAGON,
    /** A six-sided polygon. */
    HEXAGON,
    /** A seven-sided polygon. */
    HEPTAGON,
    /** An eight-sided polygon. */
    OCTAGON,
    /** A ten-sided polygon. */
    DECAGON,
    /** A twelve-sided polygon. */
    DODECAGON,
    /** A pie wedge shape, a sector of a circle. */
    PIE,
    /** A chord shape, a segment of a circle cut by a straight line. */
    CHORD,
    /** A teardrop or droplet shape. */
    TEARDROP,
    /** A rectangular frame, hollow in the center. */
    FRAME,
    /** A half-frame, open on one side. */
    HALF_FRAME,
    /** A single corner shape, often used for callouts or highlights. */
    CORNER,
    /** A diagonal stripe shape. */
    DIAGONAL_STRIPE,
    /** A plus sign shape. */
    PLUS,
    /** A plaque or label shape. */
    PLAQUE,
    /** A cylindrical can shape. */
    CAN,
    /** A cube shape. */
    CUBE,
    /** A shape with beveled edges. */
    BEVEL,
    /** A donut shape, a ring with a hollow center. */
    DONUT,
    /** The international "no smoking" symbol. */
    NO_SMOKING,
    /** A block arc shape, a thick arc segment. */
    BLOCK_ARC,
    /** A shape with a folded corner effect. */
    FOLDED_CORNER,
    /** A smiley face emoticon shape. */
    SMILEY_FACE,
    /** A heart shape. */
    HEART,
    /** A lightning bolt shape. */
    LIGHTNING_BOLT,
    /** A sun shape with rays. */
    SUN,
    /** A crescent moon shape. */
    MOON,
    /** A cloud shape. */
    CLOUD,
    /** A simple arc curve. */
    ARC,
    /** A pair of bracket symbols. */
    BRACKET_PAIR,
    /** A pair of brace symbols. */
    BRACE_PAIR,

    // ==================== Arrow Shapes ====================

    /** An arrow pointing to the right. */
    RIGHT_ARROW,
    /** An arrow pointing to the left. */
    LEFT_ARROW,
    /** An arrow pointing upwards. */
    UP_ARROW,
    /** An arrow pointing downwards. */
    DOWN_ARROW,
    /** A double-ended arrow pointing left and right. */
    LEFT_RIGHT_ARROW,
    /** A double-ended arrow pointing up and down. */
    UP_DOWN_ARROW,
    /** A four-way arrow pointing in all cardinal directions. */
    QUAD_ARROW,
    /** An arrow pointing left, right, and up. */
    LEFT_RIGHT_UP_ARROW,
    /** A bent arrow, typically with a 90-degree angle. */
    BENT_ARROW,
    /** A U-turn arrow shape. */
    U_TURN_ARROW,
    /** An arrow pointing left and up. */
    LEFT_UP_ARROW,
    /** A bent arrow pointing upwards. */
    BENT_UP_ARROW,
    /** A curved arrow pointing to the right. */
    CURVED_RIGHT_ARROW,
    /** A curved arrow pointing to the left. */
    CURVED_LEFT_ARROW,
    /** A curved arrow pointing upwards. */
    CURVED_UP_ARROW,
    /** A curved arrow pointing downwards. */
    CURVED_DOWN_ARROW,
    /** A right arrow with a striped pattern. */
    STRIPED_RIGHT_ARROW,
    /** A right arrow with a notched tail. */
    NOTCHED_RIGHT_ARROW,
    /** A home plate shape, as used in baseball. */
    HOME_PLATE,
    /** A chevron (V-shaped) arrow. */
    CHEVRON,
    /** A right arrow with a callout bubble. */
    RIGHT_ARROW_CALLOUT,
    /** A left arrow with a callout bubble. */
    LEFT_ARROW_CALLOUT,
    /** An up arrow with a callout bubble. */
    UP_ARROW_CALLOUT,
    /** A down arrow with a callout bubble. */
    DOWN_ARROW_CALLOUT,
    /** A left-right arrow with a callout bubble. */
    LEFT_RIGHT_ARROW_CALLOUT,
    /** An up-down arrow with a callout bubble. */
    UP_DOWN_ARROW_CALLOUT,
    /** A quad arrow with a callout bubble. */
    QUAD_ARROW_CALLOUT,
    /** A circular or curved arrow shape. */
    CIRCULAR_ARROW,

    // ==================== Flowchart Shapes ====================

    /** Flowchart symbol: Process (rectangle). */
    FLOWCHART_PROCESS,
    /** Flowchart symbol: Alternate Process (rounded rectangle). */
    FLOWCHART_ALTERNATE_PROCESS,
    /** Flowchart symbol: Decision (diamond). */
    FLOWCHART_DECISION,
    /** Flowchart symbol: Input/Output (parallelogram). */
    FLOWCHART_INPUT_OUTPUT,
    /** Flowchart symbol: Predefined Process (rectangle with double vertical lines). */
    FLOWCHART_PREDEFINED_PROCESS,
    /** Flowchart symbol: Internal Storage. */
    FLOWCHART_INTERNAL_STORAGE,
    /** Flowchart symbol: Document. */
    FLOWCHART_DOCUMENT,
    /** Flowchart symbol: Multi-document. */
    FLOWCHART_MULTIDOCUMENT,
    /** Flowchart symbol: Terminator (oval). */
    FLOWCHART_TERMINATOR,
    /** Flowchart symbol: Preparation (hexagon). */
    FLOWCHART_PREPARATION,
    /** Flowchart symbol: Manual Input (trapezoid). */
    FLOWCHART_MANUAL_INPUT,
    /** Flowchart symbol: Manual Operation. */
    FLOWCHART_MANUAL_OPERATION,
    /** Flowchart symbol: Connector (circle). */
    FLOWCHART_CONNECTOR,
    /** Flowchart symbol: Off-page Connector. */
    FLOWCHART_OFF_PAGE_CONNECTOR,
    /** Flowchart symbol: Card. */
    FLOWCHART_CARD,
    /** Flowchart symbol: Punched Tape. */
    FLOWCHART_PUNCHED_TAPE,
    /** Flowchart symbol: Summing Junction. */
    FLOWCHART_SUMMING_JUNCTION,
    /** Flowchart symbol: Or (circle with plus inside). */
    FLOWCHART_OR,
    /** Flowchart symbol: Collate. */
    FLOWCHART_COLLATE,
    /** Flowchart symbol: Sort. */
    FLOWCHART_SORT,
    /** Flowchart symbol: Extract. */
    FLOWCHART_EXTRACT,
    /** Flowchart symbol: Merge. */
    FLOWCHART_MERGE,
    /** Flowchart symbol: Stored Data. */
    FLOWCHART_STORED_DATA,
    /** Flowchart symbol: Delay. */
    FLOWCHART_DELAY,
    /** Flowchart symbol: Sequential Access Storage. */
    FLOWCHART_SEQUENTIAL_ACCESS_STORAGE,
    /** Flowchart symbol: Magnetic Disk. */
    FLOWCHART_MAGNETIC_DISK,
    /** Flowchart symbol: Direct Access Storage. */
    FLOWCHART_DIRECT_ACCESS_STORAGE,
    /** Flowchart symbol: Display. */
    FLOWCHART_DISPLAY,

    // ==================== Callout Shapes ====================

    /** A rectangular callout bubble with a leader line. */
    CALLOUT_RECTANGULAR,
    /** A rounded rectangular callout bubble with a leader line. */
    CALLOUT_ROUNDED_RECTANGULAR,
    /** An elliptical callout bubble with a leader line. */
    CALLOUT_ELLIPSE,
    /** A cloud-shaped callout bubble with a leader line. */
    CALLOUT_CLOUD,
    /** A callout with a single-segment leader line. */
    CALLOUT_LINE_1,
    /** A callout with a two-segment leader line. */
    CALLOUT_LINE_2,
    /** A callout with a three-segment leader line. */
    CALLOUT_LINE_3,
    /** A callout with a single-segment leader line and no border on the bubble. */
    CALLOUT_LINE_1_NO_BORDER,
    /** A callout with a two-segment leader line and no border on the bubble. */
    CALLOUT_LINE_2_NO_BORDER,
    /** A callout with a three-segment leader line and no border on the bubble. */
    CALLOUT_LINE_3_NO_BORDER,
    /** A callout with a single-segment leader line and an accent bar on the bubble. */
    CALLOUT_LINE_1_ACCENT_BAR,
    /** A callout with a two-segment leader line and an accent bar on the bubble. */
    CALLOUT_LINE_2_ACCENT_BAR,
    /** A callout with a three-segment leader line and an accent bar on the bubble. */
    CALLOUT_LINE_3_ACCENT_BAR,

    // ==================== Star and Banner Shapes ====================

    /** A four-pointed star. */
    STAR_4,
    /** A five-pointed star. */
    STAR_5,
    /** A six-pointed star. */
    STAR_6,
    /** A seven-pointed star. */
    STAR_7,
    /** An eight-pointed star. */
    STAR_8,
    /** A ten-pointed star. */
    STAR_10,
    /** A twelve-pointed star. */
    STAR_12,
    /** A sixteen-pointed star. */
    STAR_16,
    /** A twenty-four-pointed star. */
    STAR_24,
    /** A thirty-two-pointed star. */
    STAR_32,
    /** A ribbon banner shape. */
    RIBBON,
    /** A ribbon banner shape, variant 2. */
    RIBBON_2,
    /** An elliptical ribbon shape. */
    ELLIPSE_RIBBON,
    /** An elliptical ribbon shape, variant 2. */
    ELLIPSE_RIBBON_2,
    /** A vertical scroll shape. */
    VERTICAL_SCROLL,
    /** A horizontal scroll shape. */
    HORIZONTAL_SCROLL,
    /** A single wave shape. */
    WAVE,
    /** A double wave shape. */
    DOUBLE_WAVE,

    // ==================== Mathematical Symbol Shapes ====================

    /** A plus sign shape for mathematical notation. */
    MATH_PLUS,
    /** A minus sign shape for mathematical notation. */
    MATH_MINUS,
    /** A multiplication sign (cross) shape for mathematical notation. */
    MATH_MULTIPLY,
    /** A division sign (obelus) shape for mathematical notation. */
    MATH_DIVIDE,
    /** An equals sign shape for mathematical notation. */
    MATH_EQUAL,
    /** A not-equals sign shape for mathematical notation. */
    MATH_NOT_EQUAL,

    // ==================== Action Button Shapes ====================

    /** An action button representing back or previous. */
    ACTION_BUTTON_BACK_PREVIOUS,
    /** An action button representing forward or next. */
    ACTION_BUTTON_FORWARD_NEXT,
    /** An action button representing beginning. */
    ACTION_BUTTON_BEGINNING,
    /** An action button representing end. */
    ACTION_BUTTON_END,
    /** An action button representing home. */
    ACTION_BUTTON_HOME,
    /** An action button representing information. */
    ACTION_BUTTON_INFORMATION,
    /** An action button representing return. */
    ACTION_BUTTON_RETURN,
    /** An action button representing a document. */
    ACTION_BUTTON_DOCUMENT,
    /** An action button representing sound. */
    ACTION_BUTTON_SOUND,
    /** An action button representing a movie. */
    ACTION_BUTTON_MOVIE,
    /** An action button representing help. */
    ACTION_BUTTON_HELP,
    /** A blank action button. */
    ACTION_BUTTON_BLANK,

    // ==================== Connector Shapes ====================

    /** A simple straight line. */
    LINE,
    /** A straight connector line with endpoints. */
    STRAIGHT_CONNECTOR,
    /** A bent connector line with a 90-degree angle. */
    BENT_CONNECTOR,
    /** A curved connector line. */
    CURVED_CONNECTOR;

    /**
     * Determines if this shape type belongs to the basic geometric shapes category.
     *
     * <p>Basic shapes include standard polygons, rounded variants, frames, and simple symbols
     * like hearts and clouds. This category encompasses shapes from {@link #RECTANGLE} through
     * {@link #BRACE_PAIR}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.HEXAGON;
     * if (shape.isBasicShape()) {
     *     System.out.println("This is a basic shape.");
     * }
     * }</pre>
     *
     * @return {@code true} if this shape is a basic geometric shape, {@code false} otherwise.
     * @since 1.0
     */
    public boolean isBasicShape() {
        int ordinal = this.ordinal();
        return ordinal >= RECTANGLE.ordinal() && ordinal <= BRACE_PAIR.ordinal();
    }

    /**
     * Determines if this shape type belongs to the arrow shapes category.
     *
     * <p>Arrow shapes include directional arrows, bent arrows, curved arrows, and callout arrows.
     * This category encompasses shapes from {@link #RIGHT_ARROW} through {@link #CIRCULAR_ARROW}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.CHEVRON;
     * if (shape.isArrowShape()) {
     *     System.out.println("This is an arrow shape.");
     * }
     * }</pre>
     *
     * @return {@code true} if this shape is an arrow shape, {@code false} otherwise.
     * @since 1.0
     */
    public boolean isArrowShape() {
        int ordinal = this.ordinal();
        return ordinal >= RIGHT_ARROW.ordinal() && ordinal <= CIRCULAR_ARROW.ordinal();
    }

    /**
     * Determines if this shape type belongs to the flowchart shapes category.
     *
     * <p>Flowchart shapes are standardized symbols used in process flow diagrams, such as
     * process rectangles, decision diamonds, and terminators. This category encompasses shapes
     * from {@link #FLOWCHART_PROCESS} through {@link #FLOWCHART_DISPLAY}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.FLOWCHART_DECISION;
     * if (shape.isFlowchartShape()) {
     *     System.out.println("This is a flowchart shape.");
     * }
     * }</pre>
     *
     * @return {@code true} if this shape is a flowchart shape, {@code false} otherwise.
     * @since 1.0
     */
    public boolean isFlowchartShape() {
        int ordinal = this.ordinal();
        return ordinal >= FLOWCHART_PROCESS.ordinal() && ordinal <= FLOWCHART_DISPLAY.ordinal();
    }

    /**
     * Determines if this shape type belongs to the category of callout shapes.
     * <p>
     * This method checks if the ordinal value of this enum constant falls within the
     * contiguous range defined by the {@link #CALLOUT_RECTANGULAR} and {@link #CALLOUT_LINE_3_ACCENT_BAR} constants.
     * Callout shapes are typically used for annotations and have a leader line pointing to a subject.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.CALLOUT_CLOUD;
     * if (shape.isCalloutShape()) {
     *     System.out.println(shape + " is a callout shape.");
     * }
     * }</pre>
     *
     * @return {@code true} if this shape is a callout shape, {@code false} otherwise.
     * @see #CALLOUT_RECTANGULAR
     * @see #CALLOUT_LINE_3_ACCENT_BAR
     */
    public boolean isCalloutShape() {
        int ordinal = this.ordinal();
        return ordinal >= CALLOUT_RECTANGULAR.ordinal() && ordinal <= CALLOUT_LINE_3_ACCENT_BAR.ordinal();
    }

    /**
     * Determines if this shape type belongs to the category of star shapes.
     * <p>
     * This method checks if the ordinal value of this enum constant falls within the
     * contiguous range defined by the {@link #STAR_4} and {@link #STAR_32} constants.
     * This category includes star polygons with varying numbers of points.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.STAR_5;
     * if (shape.isStarShape()) {
     *     System.out.println(shape + " is a star shape.");
     * }
     * }</pre>
     *
     * @return {@code true} if this shape is a star shape, {@code false} otherwise.
     * @see #STAR_4
     * @see #STAR_32
     */
    public boolean isStarShape() {
        int ordinal = this.ordinal();
        return ordinal >= STAR_4.ordinal() && ordinal <= STAR_32.ordinal();
    }

    /**
     * Determines if this shape type belongs to the category of action button shapes.
     * <p>
     * This method checks if the ordinal value of this enum constant falls within the
     * contiguous range defined by the {@link #ACTION_BUTTON_BACK_PREVIOUS} and {@link #ACTION_BUTTON_BLANK} constants.
     * Action button shapes are commonly used in user interfaces to represent interactive commands.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.ACTION_BUTTON_HELP;
     * if (shape.isActionButton()) {
     *     System.out.println(shape + " is an action button shape.");
     * }
     * }</pre>
     *
     * @return {@code true} if this shape is an action button shape, {@code false} otherwise.
     * @see #ACTION_BUTTON_BACK_PREVIOUS
     * @see #ACTION_BUTTON_BLANK
     */
    public boolean isActionButton() {
        int ordinal = this.ordinal();
        return ordinal >= ACTION_BUTTON_BACK_PREVIOUS.ordinal() && ordinal <= ACTION_BUTTON_BLANK.ordinal();
    }

    /**
     * Determines if this shape type is a line or connector shape.
     * <p>
     * This method checks if this enum constant is one of the specific line-based shapes:
     * {@link #LINE}, {@link #STRAIGHT_CONNECTOR}, {@link #BENT_CONNECTOR}, or {@link #CURVED_CONNECTOR}.
     * These shapes are typically used for drawing connections between other shapes.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.BENT_CONNECTOR;
     * if (shape.isConnector()) {
     *     System.out.println(shape + " is a connector shape.");
     * }
     * }</pre>
     *
     * @return {@code true} if this shape is a line or connector shape, {@code false} otherwise.
     * @see #LINE
     * @see #STRAIGHT_CONNECTOR
     * @see #BENT_CONNECTOR
     * @see #CURVED_CONNECTOR
     */
    public boolean isConnector() {
        return this == LINE || this == STRAIGHT_CONNECTOR ||
               this == BENT_CONNECTOR || this == CURVED_CONNECTOR;
    }
}
