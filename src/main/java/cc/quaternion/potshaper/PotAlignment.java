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

import org.apache.poi.sl.usermodel.TextParagraph;

/**
 * Enumerates text alignment options for presentation shapes, providing mapping to and from Apache POI alignment constants.
 * <p>
 * This enum defines a set of standard text alignment values used within the library. Each constant corresponds to a specific
 * {@link TextParagraph.TextAlign} value from the Apache POI library, enabling seamless conversion between the library's
 * internal representation and POI's model for text paragraph alignment. The mapping is bidirectional, supporting both
 * conversion to POI alignments and construction from POI alignments.
 * </p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public enum PotAlignment {

    /**
     * Aligns text to the left margin of the shape.
     */
    LEFT,

    /**
     * Centers text horizontally within the shape.
     */
    CENTER,

    /**
     * Aligns text to the right margin of the shape.
     */
    RIGHT,

    /**
     * Justifies text, adding space between words so that lines reach both the left and right margins.
     */
    JUSTIFY,

    /**
     * Distributes text evenly across the width of the shape, typically adjusting character spacing.
     */
    DISTRIBUTED,

    /**
     * Justifies text using a low-resolution algorithm, often for specific typographic or compatibility purposes.
     */
    JUSTIFY_LOW;

    /**
     * Converts this alignment constant to its corresponding Apache POI {@code TextAlign} value.
     * <p>
     * This method performs a direct mapping from the enum constant to the equivalent POI alignment constant.
     * It is the primary method for obtaining the POI representation of this alignment. The fallback for any
     * unrecognized state (which should not occur) is {@link TextParagraph.TextAlign#LEFT}.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAlignment alignment = PotAlignment.CENTER;
     * TextParagraph.TextAlign poiAlign = alignment.toPoiAlign();
     * // poiAlign is now TextParagraph.TextAlign.CENTER
     * }</pre>
     *
     * @return the corresponding {@code TextParagraph.TextAlign} constant, never {@code null}.
     * @see TextParagraph.TextAlign
     */
    public TextParagraph.TextAlign toPoiAlign() {
        switch (this) {
            case LEFT:
                return TextParagraph.TextAlign.LEFT;
            case CENTER:
                return TextParagraph.TextAlign.CENTER;
            case RIGHT:
                return TextParagraph.TextAlign.RIGHT;
            case JUSTIFY:
                return TextParagraph.TextAlign.JUSTIFY;
            case DISTRIBUTED:
                return TextParagraph.TextAlign.DIST;
            case JUSTIFY_LOW:
                return TextParagraph.TextAlign.JUSTIFY_LOW;
            default:
                return TextParagraph.TextAlign.LEFT;
        }
    }

    /**
     * Converts this alignment constant to its corresponding Apache POI {@code TextAlign} value.
     * <p>
     * This method is an alias for {@link #toPoiAlign()}. It provides an alternative, more explicit name
     * for the conversion operation, returning the same result as {@code toPoiAlign()}.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAlignment alignment = PotAlignment.RIGHT;
     * TextParagraph.TextAlign poiAlign = alignment.toPoiTextAlign();
     * // poiAlign is now TextParagraph.TextAlign.RIGHT
     * }</pre>
     *
     * @return the corresponding {@code TextParagraph.TextAlign} constant, never {@code null}.
     * @see #toPoiAlign()
     */
    public TextParagraph.TextAlign toPoiTextAlign() {
        return toPoiAlign();
    }

    /**
     * Converts an Apache POI {@code TextAlign} value to the corresponding {@code PotAlignment} enum constant.
     * <p>
     * This static method performs the inverse mapping of {@link #toPoiAlign()}. It translates a POI alignment
     * constant into the library's internal alignment enum. If the provided {@code poiAlign} is {@code null},
     * the method returns {@link #LEFT} as a default value. If the POI alignment does not match any known
     * mapping, {@link #LEFT} is also returned as a fallback.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * TextParagraph.TextAlign poiAlignment = TextParagraph.TextAlign.JUSTIFY;
     * PotAlignment potAlignment = PotAlignment.fromPoiAlign(poiAlignment);
     * // potAlignment is now PotAlignment.JUSTIFY
     * }</pre>
     *
     * @param poiAlign the Apache POI text alignment constant to convert; may be {@code null}.
     * @return the corresponding {@code PotAlignment} constant, or {@link #LEFT} if the input is {@code null}
     *         or does not match a known mapping.
     * @see TextParagraph.TextAlign
     * @see #toPoiAlign()
     */
    public static PotAlignment fromPoiAlign(TextParagraph.TextAlign poiAlign) {
        if (poiAlign == null) {
            return LEFT;
        }
        switch (poiAlign) {
            case LEFT:
                return LEFT;
            case CENTER:
                return CENTER;
            case RIGHT:
                return RIGHT;
            case JUSTIFY:
                return JUSTIFY;
            case DIST:
                return DISTRIBUTED;
            case JUSTIFY_LOW:
                return JUSTIFY_LOW;
            default:
                return LEFT;
        }
    }
}