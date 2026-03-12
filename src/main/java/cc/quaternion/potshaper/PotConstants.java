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
 * Provides constant values used throughout the PotShaper library.
 * <p>
 * This final utility class contains application-wide constants for geometric offsets,
 * numeric bounds, timing transitions, and memory estimation. These constants are used
 * to ensure consistent behavior across different components of the presentation
 * shaping system.
 * </p>
 */
final class PotConstants {

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This is a utility class that should not be instantiated.
     * </p>
     */
    private PotConstants() {
    }

    /**
     * The offset distance, in points, used to separate duplicate shapes.
     * <p>
     * When duplicate shapes are created, this constant defines the default
     * displacement applied to avoid perfect overlap.
     * </p>
     */
    static final double DUPLICATE_OFFSET_POINTS = 20.0;

    /**
     * The minimum valid value for a fraction, representing 0% or no portion.
     */
    static final double FRACTION_MIN = 0.0;

    /**
     * The maximum valid value for a fraction, representing 100% or the whole.
     */
    static final double FRACTION_MAX = 1.0;

    /**
     * The scaling factor used to convert percentages in OpenXML formats.
     * <p>
     * In OpenXML (e.g., PowerPoint .pptx files), percentages are often stored
     * as integers scaled by this factor (e.g., 50% is stored as 50000).
     * </p>
     */
    static final int OPENXML_PERCENT_SCALE = 100000;

    /**
     * Duration in milliseconds for a fast visual transition.
     */
    static final int TRANSITION_FAST_MS = 300;

    /**
     * Duration in milliseconds for a medium-speed visual transition.
     */
    static final int TRANSITION_MEDIUM_MS = 700;

    /**
     * Duration in milliseconds for a slow visual transition.
     */
    static final int TRANSITION_SLOW_MS = 1000;

    /**
     * Base memory allocation in megabytes for the application runtime.
     */
    static final long MEMORY_BASE_MB = 2;

    /**
     * Divisor used to estimate memory per slide.
     * <p>
     * The total estimated memory is calculated by adding the base memory to
     * the number of slides divided by this value.
     * </p>
     */
    static final int MEMORY_SLIDES_DIVISOR = 2;

    /**
     * Divisor used to estimate memory per shape.
     * <p>
     * The total estimated memory is calculated by adding the base memory to
     * the number of shapes divided by this value.
     * </p>
     */
    static final int MEMORY_SHAPES_DIVISOR = 20;

    /**
     * Additional memory in megabytes estimated per embedded media object.
     */
    static final int MEMORY_PER_MEDIA_MB = 5;
}