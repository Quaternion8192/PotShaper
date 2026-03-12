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

import org.apache.poi.sl.usermodel.VerticalAlignment;

/**
 * Enumerates vertical alignment options for content within a shape or text box.
 * <p>
 * This enumeration provides a set of standard vertical alignment constants and serves as a bridge
 * to the Apache POI {@link VerticalAlignment} type. It defines mappings for conversion between
 * this library's representation and the underlying POI representation. The {@code JUSTIFY} member
 * is provided for API compatibility but is internally mapped to POI's {@code DISTRIBUTED} alignment,
 * as POI does not have a distinct {@code JUSTIFY} constant for vertical alignment.
 * </p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public enum PotVerticalAlignment {

    /**
     * Aligns content to the top of the container.
     */
    TOP,

    /**
     * Centers content vertically within the container.
     */
    MIDDLE,

    /**
     * Aligns content to the bottom of the container.
     */
    BOTTOM,

    /**
     * Justifies content vertically; maps to {@code DISTRIBUTED} in Apache POI.
     * <p>
     * Apache POI's vertical alignment does not include a separate {@code JUSTIFY} constant.
     * This value is provided for API consistency and is converted to {@link VerticalAlignment#DISTRIBUTED}
     * when calling conversion methods.
     * </p>
     */
    JUSTIFY,

    /**
     * Distributes content evenly across the vertical space of the container.
     */
    DISTRIBUTED;

    /**
     * Converts this alignment to the equivalent Apache POI {@code VerticalAlignment}.
     * <p>
     * Returns the corresponding POI vertical alignment constant. The {@link #JUSTIFY} member
     * is mapped to {@link VerticalAlignment#DISTRIBUTED}, as POI does not have a vertical
     * {@code JUSTIFY} alignment. All other members have direct counterparts.
     * </p>
     *
     * @return the corresponding Apache POI {@code VerticalAlignment} constant, never {@code null}
     *
     * @see VerticalAlignment
     */
    public VerticalAlignment toPoiAlign() {
        switch (this) {
            case TOP:
                return VerticalAlignment.TOP;
            case MIDDLE:
                return VerticalAlignment.MIDDLE;
            case BOTTOM:
                return VerticalAlignment.BOTTOM;
            case JUSTIFY:
                return VerticalAlignment.DISTRIBUTED; // No JUSTIFY in POI, map to DISTRIBUTED
            case DISTRIBUTED:
                return VerticalAlignment.DISTRIBUTED;
            default:
                return VerticalAlignment.TOP;
        }
    }

    /**
     * Converts this alignment to the equivalent Apache POI {@code VerticalAlignment}.
     * <p>
     * This method is an alias for {@link #toPoiAlign()}. It provides an alternative
     * method name for consistency with other conversion patterns in the API.
     * </p>
     *
     * @return the corresponding Apache POI {@code VerticalAlignment} constant, never {@code null}
     *
     * @see #toPoiAlign()
     * @see VerticalAlignment
     */
    public VerticalAlignment toPoiAlignment() {
        return toPoiAlign();
    }

    /**
     * Converts an Apache POI {@code VerticalAlignment} to the corresponding {@code PotVerticalAlignment}.
     * <p>
     * Maps the given POI alignment constant to its equivalent in this enumeration.
     * If the provided alignment is {@code null}, this method returns {@link #TOP} as a default.
     * Since POI does not have a {@code JUSTIFY} vertical alignment, only {@link #TOP},
     * {@link #MIDDLE}, {@link #BOTTOM}, and {@link #DISTRIBUTED} can be produced from a POI constant.
     * Any unrecognized POI alignment value also defaults to {@link #TOP}.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * VerticalAlignment poiAlign = shape.getVerticalAlignment();
     * PotVerticalAlignment potAlign = PotVerticalAlignment.fromPoiAlign(poiAlign);
     * }</pre>
     *
     * @param poiAlign the Apache POI vertical alignment to convert, may be {@code null}
     * @return the corresponding {@code PotVerticalAlignment}, never {@code null}
     *
     * @see VerticalAlignment
     */
    public static PotVerticalAlignment fromPoiAlign(VerticalAlignment poiAlign) {
        if (poiAlign == null) {
            return TOP;
        }
        switch (poiAlign) {
            case TOP:
                return TOP;
            case MIDDLE:
                return MIDDLE;
            case BOTTOM:
                return BOTTOM;
            case DISTRIBUTED:
                return DISTRIBUTED;
            default:
                return TOP;
        }
    }
}