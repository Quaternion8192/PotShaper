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

import org.apache.poi.sl.usermodel.ShapeType;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps between the library's internal {@link PotShapeType} enumeration and Apache POI's {@link ShapeType}.
 * <p>
 * This class provides bidirectional conversion and categorization for shape types used in presentation documents.
 * It maintains static mappings for all supported shapes, including basic geometries, arrows, flowchart symbols,
 * callouts, stars, mathematical operators, action buttons, and connectors. The mapping is initialized once when
 * the class is loaded and is used to ensure compatibility between the internal representation and the POI library's
 * shape model.
 * </p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class ShapeTypeMapper {

    /**
     * Maps a {@link PotShapeType} to its corresponding Apache POI {@link ShapeType}.
     */
    private static final Map<PotShapeType, ShapeType> POT_TO_POI = new HashMap<>();

    /**
     * Maps an Apache POI {@link ShapeType} to its corresponding {@link PotShapeType}.
     */
    private static final Map<ShapeType, PotShapeType> POI_TO_POT = new HashMap<>();

    static {
        // Basic geometric shapes
        register(PotShapeType.RECTANGLE, ShapeType.RECT);
        register(PotShapeType.ROUNDED_RECTANGLE, ShapeType.ROUND_RECT);
        register(PotShapeType.SNIP_ROUND_RECTANGLE, ShapeType.SNIP_ROUND_RECT);
        register(PotShapeType.SNIP_1_RECTANGLE, ShapeType.SNIP_1_RECT);
        register(PotShapeType.SNIP_2_SAME_RECTANGLE, ShapeType.SNIP_2_SAME_RECT);
        register(PotShapeType.SNIP_2_DIAGONAL_RECTANGLE, ShapeType.SNIP_2_DIAG_RECT);
        register(PotShapeType.ROUND_1_RECTANGLE, ShapeType.ROUND_1_RECT);
        register(PotShapeType.ROUND_2_SAME_RECTANGLE, ShapeType.ROUND_2_SAME_RECT);
        register(PotShapeType.ROUND_2_DIAGONAL_RECTANGLE, ShapeType.ROUND_2_DIAG_RECT);
        register(PotShapeType.ELLIPSE, ShapeType.ELLIPSE);
        register(PotShapeType.TRIANGLE, ShapeType.TRIANGLE);
        register(PotShapeType.RIGHT_TRIANGLE, ShapeType.RT_TRIANGLE);
        register(PotShapeType.PARALLELOGRAM, ShapeType.PARALLELOGRAM);
        register(PotShapeType.TRAPEZOID, ShapeType.TRAPEZOID);
        register(PotShapeType.DIAMOND, ShapeType.DIAMOND);
        register(PotShapeType.PENTAGON, ShapeType.PENTAGON);
        register(PotShapeType.HEXAGON, ShapeType.HEXAGON);
        register(PotShapeType.HEPTAGON, ShapeType.HEPTAGON);
        register(PotShapeType.OCTAGON, ShapeType.OCTAGON);
        register(PotShapeType.DECAGON, ShapeType.DECAGON);
        register(PotShapeType.DODECAGON, ShapeType.DODECAGON);
        register(PotShapeType.PIE, ShapeType.PIE);
        register(PotShapeType.CHORD, ShapeType.CHORD);
        register(PotShapeType.TEARDROP, ShapeType.TEARDROP);
        register(PotShapeType.FRAME, ShapeType.FRAME);
        register(PotShapeType.HALF_FRAME, ShapeType.HALF_FRAME);
        register(PotShapeType.CORNER, ShapeType.CORNER);
        register(PotShapeType.DIAGONAL_STRIPE, ShapeType.DIAG_STRIPE);
        register(PotShapeType.PLUS, ShapeType.PLUS);
        register(PotShapeType.PLAQUE, ShapeType.PLAQUE);
        register(PotShapeType.CAN, ShapeType.CAN);
        register(PotShapeType.CUBE, ShapeType.CUBE);
        register(PotShapeType.BEVEL, ShapeType.BEVEL);
        register(PotShapeType.DONUT, ShapeType.DONUT);
        register(PotShapeType.NO_SMOKING, ShapeType.NO_SMOKING);
        register(PotShapeType.BLOCK_ARC, ShapeType.BLOCK_ARC);
        register(PotShapeType.FOLDED_CORNER, ShapeType.FOLDED_CORNER);
        register(PotShapeType.SMILEY_FACE, ShapeType.SMILEY_FACE);
        register(PotShapeType.HEART, ShapeType.HEART);
        register(PotShapeType.LIGHTNING_BOLT, ShapeType.LIGHTNING_BOLT);
        register(PotShapeType.SUN, ShapeType.SUN);
        register(PotShapeType.MOON, ShapeType.MOON);
        register(PotShapeType.CLOUD, ShapeType.CLOUD);
        register(PotShapeType.ARC, ShapeType.ARC);
        register(PotShapeType.BRACKET_PAIR, ShapeType.BRACKET_PAIR);
        register(PotShapeType.BRACE_PAIR, ShapeType.BRACE_PAIR);

        // Arrow shapes
        register(PotShapeType.RIGHT_ARROW, ShapeType.RIGHT_ARROW);
        register(PotShapeType.LEFT_ARROW, ShapeType.LEFT_ARROW);
        register(PotShapeType.UP_ARROW, ShapeType.UP_ARROW);
        register(PotShapeType.DOWN_ARROW, ShapeType.DOWN_ARROW);
        register(PotShapeType.LEFT_RIGHT_ARROW, ShapeType.LEFT_RIGHT_ARROW);
        register(PotShapeType.UP_DOWN_ARROW, ShapeType.UP_DOWN_ARROW);
        register(PotShapeType.QUAD_ARROW, ShapeType.QUAD_ARROW);
        register(PotShapeType.LEFT_RIGHT_UP_ARROW, ShapeType.LEFT_RIGHT_UP_ARROW);
        register(PotShapeType.BENT_ARROW, ShapeType.BENT_ARROW);
        register(PotShapeType.U_TURN_ARROW, ShapeType.UTURN_ARROW);
        register(PotShapeType.LEFT_UP_ARROW, ShapeType.LEFT_UP_ARROW);
        register(PotShapeType.BENT_UP_ARROW, ShapeType.BENT_UP_ARROW);
        register(PotShapeType.CURVED_RIGHT_ARROW, ShapeType.CURVED_RIGHT_ARROW);
        register(PotShapeType.CURVED_LEFT_ARROW, ShapeType.CURVED_LEFT_ARROW);
        register(PotShapeType.CURVED_UP_ARROW, ShapeType.CURVED_UP_ARROW);
        register(PotShapeType.CURVED_DOWN_ARROW, ShapeType.CURVED_DOWN_ARROW);
        register(PotShapeType.STRIPED_RIGHT_ARROW, ShapeType.STRIPED_RIGHT_ARROW);
        register(PotShapeType.NOTCHED_RIGHT_ARROW, ShapeType.NOTCHED_RIGHT_ARROW);
        register(PotShapeType.HOME_PLATE, ShapeType.HOME_PLATE);
        register(PotShapeType.CHEVRON, ShapeType.CHEVRON);
        register(PotShapeType.RIGHT_ARROW_CALLOUT, ShapeType.RIGHT_ARROW_CALLOUT);
        register(PotShapeType.LEFT_ARROW_CALLOUT, ShapeType.LEFT_ARROW_CALLOUT);
        register(PotShapeType.UP_ARROW_CALLOUT, ShapeType.UP_ARROW_CALLOUT);
        register(PotShapeType.DOWN_ARROW_CALLOUT, ShapeType.DOWN_ARROW_CALLOUT);
        register(PotShapeType.LEFT_RIGHT_ARROW_CALLOUT, ShapeType.LEFT_RIGHT_ARROW_CALLOUT);
        register(PotShapeType.UP_DOWN_ARROW_CALLOUT, ShapeType.UP_DOWN_ARROW_CALLOUT);
        register(PotShapeType.QUAD_ARROW_CALLOUT, ShapeType.QUAD_ARROW_CALLOUT);
        register(PotShapeType.CIRCULAR_ARROW, ShapeType.CIRCULAR_ARROW);

        // Flowchart shapes - mapped to closest available POI shapes (POI 5.2.3 lacks native flowchart types)
        // Note: PotXml retains the original XML type for fidelity.
        register(PotShapeType.FLOWCHART_PROCESS, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_ALTERNATE_PROCESS, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_DECISION, ShapeType.DIAMOND);
        register(PotShapeType.FLOWCHART_INPUT_OUTPUT, ShapeType.PARALLELOGRAM);
        register(PotShapeType.FLOWCHART_PREDEFINED_PROCESS, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_INTERNAL_STORAGE, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_DOCUMENT, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_MULTIDOCUMENT, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_TERMINATOR, ShapeType.ROUND_RECT);
        register(PotShapeType.FLOWCHART_PREPARATION, ShapeType.HEXAGON);
        register(PotShapeType.FLOWCHART_MANUAL_INPUT, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_MANUAL_OPERATION, ShapeType.TRAPEZOID);
        register(PotShapeType.FLOWCHART_CONNECTOR, ShapeType.ELLIPSE);
        register(PotShapeType.FLOWCHART_OFF_PAGE_CONNECTOR, ShapeType.HOME_PLATE);
        register(PotShapeType.FLOWCHART_CARD, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_PUNCHED_TAPE, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_SUMMING_JUNCTION, ShapeType.ELLIPSE);
        register(PotShapeType.FLOWCHART_OR, ShapeType.ELLIPSE);
        register(PotShapeType.FLOWCHART_COLLATE, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_SORT, ShapeType.DIAMOND);
        register(PotShapeType.FLOWCHART_EXTRACT, ShapeType.TRIANGLE);
        register(PotShapeType.FLOWCHART_MERGE, ShapeType.TRIANGLE);
        register(PotShapeType.FLOWCHART_STORED_DATA, ShapeType.CAN);
        register(PotShapeType.FLOWCHART_DELAY, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_SEQUENTIAL_ACCESS_STORAGE, ShapeType.RECT);
        register(PotShapeType.FLOWCHART_MAGNETIC_DISK, ShapeType.CAN);
        register(PotShapeType.FLOWCHART_DIRECT_ACCESS_STORAGE, ShapeType.CAN);
        register(PotShapeType.FLOWCHART_DISPLAY, ShapeType.RECT);

        // Callout shapes
        register(PotShapeType.CALLOUT_RECTANGULAR, ShapeType.WEDGE_RECT_CALLOUT);
        register(PotShapeType.CALLOUT_ROUNDED_RECTANGULAR, ShapeType.WEDGE_ROUND_RECT_CALLOUT);
        register(PotShapeType.CALLOUT_ELLIPSE, ShapeType.WEDGE_ELLIPSE_CALLOUT);
        register(PotShapeType.CALLOUT_CLOUD, ShapeType.CLOUD_CALLOUT);
        register(PotShapeType.CALLOUT_LINE_1, ShapeType.BORDER_CALLOUT_1);
        register(PotShapeType.CALLOUT_LINE_2, ShapeType.BORDER_CALLOUT_2);
        register(PotShapeType.CALLOUT_LINE_3, ShapeType.BORDER_CALLOUT_3);
        register(PotShapeType.CALLOUT_LINE_1_NO_BORDER, ShapeType.CALLOUT_1);
        register(PotShapeType.CALLOUT_LINE_2_NO_BORDER, ShapeType.CALLOUT_2);
        register(PotShapeType.CALLOUT_LINE_3_NO_BORDER, ShapeType.CALLOUT_3);
        register(PotShapeType.CALLOUT_LINE_1_ACCENT_BAR, ShapeType.ACCENT_CALLOUT_1);
        register(PotShapeType.CALLOUT_LINE_2_ACCENT_BAR, ShapeType.ACCENT_CALLOUT_2);
        register(PotShapeType.CALLOUT_LINE_3_ACCENT_BAR, ShapeType.ACCENT_CALLOUT_3);

        // Star and decorative shapes
        register(PotShapeType.STAR_4, ShapeType.STAR_4);
        register(PotShapeType.STAR_5, ShapeType.STAR_5);
        register(PotShapeType.STAR_6, ShapeType.STAR_6);
        register(PotShapeType.STAR_7, ShapeType.STAR_7);
        register(PotShapeType.STAR_8, ShapeType.STAR_8);
        register(PotShapeType.STAR_10, ShapeType.STAR_10);
        register(PotShapeType.STAR_12, ShapeType.STAR_12);
        register(PotShapeType.STAR_16, ShapeType.STAR_16);
        register(PotShapeType.STAR_24, ShapeType.STAR_24);
        register(PotShapeType.STAR_32, ShapeType.STAR_32);
        register(PotShapeType.RIBBON, ShapeType.RIBBON);
        register(PotShapeType.RIBBON_2, ShapeType.RIBBON_2);
        register(PotShapeType.ELLIPSE_RIBBON, ShapeType.ELLIPSE_RIBBON);
        register(PotShapeType.ELLIPSE_RIBBON_2, ShapeType.ELLIPSE_RIBBON_2);
        register(PotShapeType.VERTICAL_SCROLL, ShapeType.VERTICAL_SCROLL);
        register(PotShapeType.HORIZONTAL_SCROLL, ShapeType.HORIZONTAL_SCROLL);
        register(PotShapeType.WAVE, ShapeType.WAVE);
        register(PotShapeType.DOUBLE_WAVE, ShapeType.DOUBLE_WAVE);

        // Mathematical operator shapes
        register(PotShapeType.MATH_PLUS, ShapeType.MATH_PLUS);
        register(PotShapeType.MATH_MINUS, ShapeType.MATH_MINUS);
        register(PotShapeType.MATH_MULTIPLY, ShapeType.MATH_MULTIPLY);
        register(PotShapeType.MATH_DIVIDE, ShapeType.MATH_DIVIDE);
        register(PotShapeType.MATH_EQUAL, ShapeType.MATH_EQUAL);
        register(PotShapeType.MATH_NOT_EQUAL, ShapeType.MATH_NOT_EQUAL);

        // Action button shapes
        register(PotShapeType.ACTION_BUTTON_BACK_PREVIOUS, ShapeType.ACTION_BUTTON_BACK_PREVIOUS);
        register(PotShapeType.ACTION_BUTTON_FORWARD_NEXT, ShapeType.ACTION_BUTTON_FORWARD_NEXT);
        register(PotShapeType.ACTION_BUTTON_BEGINNING, ShapeType.ACTION_BUTTON_BEGINNING);
        register(PotShapeType.ACTION_BUTTON_END, ShapeType.ACTION_BUTTON_END);
        register(PotShapeType.ACTION_BUTTON_HOME, ShapeType.ACTION_BUTTON_HOME);
        register(PotShapeType.ACTION_BUTTON_INFORMATION, ShapeType.ACTION_BUTTON_INFORMATION);
        register(PotShapeType.ACTION_BUTTON_RETURN, ShapeType.ACTION_BUTTON_RETURN);
        register(PotShapeType.ACTION_BUTTON_DOCUMENT, ShapeType.ACTION_BUTTON_DOCUMENT);
        register(PotShapeType.ACTION_BUTTON_SOUND, ShapeType.ACTION_BUTTON_SOUND);
        register(PotShapeType.ACTION_BUTTON_MOVIE, ShapeType.ACTION_BUTTON_MOVIE);
        register(PotShapeType.ACTION_BUTTON_HELP, ShapeType.ACTION_BUTTON_HELP);
        register(PotShapeType.ACTION_BUTTON_BLANK, ShapeType.ACTION_BUTTON_BLANK);

        // Connector and line shapes
        register(PotShapeType.LINE, ShapeType.LINE);
        register(PotShapeType.STRAIGHT_CONNECTOR, ShapeType.STRAIGHT_CONNECTOR_1);
        register(PotShapeType.BENT_CONNECTOR, ShapeType.BENT_CONNECTOR_3);
        register(PotShapeType.CURVED_CONNECTOR, ShapeType.CURVED_CONNECTOR_3);
    }

    /**
     * Registers a bidirectional mapping between a {@link PotShapeType} and an Apache POI {@link ShapeType}.
     * <p>
     * This private utility method populates the internal lookup maps during static initialization.
     * It ensures that each mapping is entered in both directions for consistent conversion.
     * </p>
     *
     * @param potType the library's internal shape type
     * @param poiType the corresponding Apache POI shape type
     */
    private static void register(PotShapeType potType, ShapeType poiType) {
        POT_TO_POI.put(potType, poiType);
        POI_TO_POT.put(poiType, potType);
    }

    /**
     * Converts a {@link PotShapeType} to the corresponding
Apache POI {@link ShapeType}.
     * <p>
     * This method performs a lookup in the internal mapping. If the provided {@code potType} is {@code null},
     * or if no explicit mapping exists, the method returns {@link ShapeType#RECT} as a safe default.
     * This ensures that shape conversion never fails and always produces a valid POI shape type.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType internalShape = PotShapeType.HEXAGON;
     * ShapeType poiShape = ShapeTypeMapper.toPoiType(internalShape);
     * // poiShape will be ShapeType.HEXAGON
     * }</pre>
     *
     * @param potType the library's internal shape type, may be {@code null}
     * @return the corresponding Apache POI shape type, or {@link ShapeType#RECT} if the input is {@code null} or unmapped
     * @see #toPotType(ShapeType)
     */
    static ShapeType toPoiType(PotShapeType potType) {
        if (potType == null) {
            return ShapeType.RECT;
        }
        return POT_TO_POI.getOrDefault(potType, ShapeType.RECT);
    }

    /**
     * Converts an Apache POI {@link ShapeType} to the corresponding library {@link PotShapeType}.
     * <p>
     * This method performs a reverse lookup in the internal mapping. If the provided {@code poiType} is {@code null},
     * or if no explicit mapping exists, the method returns {@link PotShapeType#RECTANGLE} as a safe default.
     * This ensures that reverse conversion never fails and always produces a valid internal shape type.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * ShapeType poiShape = ShapeType.DIAMOND;
     * PotShapeType internalShape = ShapeTypeMapper.toPotType(poiShape);
     * // internalShape will be PotShapeType.DIAMOND
     * }</pre>
     *
     * @param poiType the Apache POI shape type, may be {@code null}
     * @return the corresponding library shape type, or {@link PotShapeType#RECTANGLE} if the input is {@code null} or unmapped
     * @see #toPoiType(PotShapeType)
     */
    static PotShapeType toPotType(ShapeType poiType) {
        if (poiType == null) {
            return PotShapeType.RECTANGLE;
        }
        return POI_TO_POT.getOrDefault(poiType, PotShapeType.RECTANGLE);
    }

    /**
     * Determines whether a given {@link PotShapeType} represents a connector or line shape.
     * <p>
     * Connector shapes are used to link other shapes in diagrams and typically have special
     * handling for endpoints and routing. This method checks against a fixed set of known
     * connector types: {@link PotShapeType#LINE}, {@link PotShapeType#STRAIGHT_CONNECTOR},
     * {@link PotShapeType#BENT_CONNECTOR}, and {@link PotShapeType#CURVED_CONNECTOR}.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.CURVED_CONNECTOR;
     * boolean isConnector = ShapeTypeMapper.isConnector(shape);
     * // isConnector will be true
     * }</pre>
     *
     * @param potType the library's internal shape type, may be {@code null}
     * @return {@code true} if the shape is a connector, {@code false} otherwise or if the input is {@code null}
     */
    static boolean isConnector(PotShapeType potType) {
        if (potType == null) {
            return false;
        }
        return potType == PotShapeType.LINE ||
               potType == PotShapeType.STRAIGHT_CONNECTOR ||
               potType == PotShapeType.BENT_CONNECTOR ||
               potType == PotShapeType.CURVED_CONNECTOR;
    }

    /**
     * Determines whether a given {@link PotShapeType} represents a flowchart shape.
     * <p>
     * Flowchart shapes are identified by their enum name starting with the prefix "FLOWCHART_".
     * This categorization is useful for applying flowchart-specific rendering or behavior.
     * Note that the mapping to POI shapes may use geometric approximations (e.g., rectangles,
     * diamonds) as POI lacks native flowchart types.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.FLOWCHART_DECISION;
     * boolean isFlowchart = ShapeTypeMapper.isFlowchart(shape);
     * // isFlowchart will be true
     * }</pre>
     *
     * @param potType the library's internal shape type, may be {@code null}
     * @return {@code true} if the shape is a flowchart shape, {@code false} otherwise or if the input is {@code null}
     */
    static boolean isFlowchart(PotShapeType potType) {
        if (potType == null) {
            return false;
        }
        return potType.name().startsWith("FLOWCHART_");
    }

    /**
     * Determines whether a given {@link PotShapeType} represents a callout shape.
     * <p>
     * Callout shapes are identified by their enum name starting with the prefix "CALLOUT_".
     * These shapes typically include a leader line pointing to an annotation or comment.
     * This categorization helps in handling callout-specific properties like leader line style.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.CALLOUT_CLOUD;
     * boolean isCallout = ShapeTypeMapper.isCallout(shape);
     * // isCallout will be true
     * }</pre>
     *
     * @param potType the library's internal shape type, may be {@code null}
     * @return {@code true} if the shape is a callout, {@code false} otherwise or if the input is {@code null}
     */
    static boolean isCallout(PotShapeType potType) {
        if (potType == null) {
            return false;
        }
        return potType.name().startsWith("CALLOUT_");
    }

    /**
     * Determines whether a given {@link PotShapeType} represents an arrow shape.
     * <p>
     * Arrow shapes are identified by their enum name containing "ARROW" but not starting with
     * "CALLOUT_" (since callout arrows are categorized separately). This includes directional
     * arrows, bent arrows, curved arrows, and arrow callouts (when not filtered out).
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.RIGHT_ARROW;
     * boolean isArrow = ShapeTypeMapper.isArrow(shape);
     * // isArrow will be true
     * }</pre>
     *
     * @param potType the library's internal shape type, may be {@code null}
     * @return {@code true} if the shape is an arrow, {@code false} otherwise or if the input is {@code null}
     */
    static boolean isArrow(PotShapeType potType) {
        if (potType == null) {
            return false;
        }
        String name = potType.name();
        return name.contains("ARROW") && !name.startsWith("CALLOUT_");
    }

    /**
     * Determines whether a given {@link PotShapeType} represents a star shape.
     * <p>
     * Star shapes are identified by their enum name starting with the prefix "STAR_".
     * This includes stars with various point counts (4, 5, 6, 7, 8, 10, 12, 16, 24, 32).
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.STAR_8;
     * boolean isStar = ShapeTypeMapper.isStar(shape);
     * // isStar will be true
     * }</pre>
     *
     * @param potType the library's internal shape type, may be {@code null}
     * @return {@code true} if the shape is a star, {@code false} otherwise or if the input is {@code null}
     */
    static boolean isStar(PotShapeType potType) {
        if (potType == null) {
            return false;
        }
        return potType.name().startsWith("STAR_");
    }

    /**
     * Determines whether a given {@link PotShapeType} represents an action button shape.
     * <p>
     * Action button shapes are identified by their enum name starting with the prefix "ACTION_BUTTON_".
     * These shapes are typically used in interactive presentations and may have associated
     * hyperlink or macro behaviors.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShapeType shape = PotShapeType.ACTION_BUTTON_HELP;
     * boolean isActionButton = ShapeTypeMapper.isActionButton(shape);
     * // isActionButton will be true
     * }</pre>
     *
     * @param potType the library's internal shape type, may be {@code null}
     * @return {@code true} if the shape is an action button, {@code false} otherwise or if the input is {@code null}
     */
    static boolean isActionButton(PotShapeType potType) {
        if (potType == null) {
            return false;
        }
        return potType.name().startsWith("ACTION_BUTTON_");
    }
}
