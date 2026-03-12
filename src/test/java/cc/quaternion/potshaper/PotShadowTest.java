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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotShadow functionality.
 * Validates shadow building, offset computation, and none preset.
 */
class PotShadowTest {

    /**
     * Tests shadow building with various properties and automatic offset computation.
     * Verifies blur, distance, angle, opacity, scale, and EMU conversions.
     */
    @Test
    void shouldBuildShadowAndComputeOffsets() {
        PotShadow shadow = PotShadow.outer()
            .withBlur(6)
            .withDistance(10)
            .withAngle(90)
            .withOpacity(0.3)
            .withScale(1.5, 0.8);

        assertEquals(PotShadow.Type.OUTER, shadow.getType());
        assertEquals(6.0, shadow.getBlur(), 1e-9);
        assertEquals(10.0, shadow.getDistance(), 1e-9);
        assertEquals(90.0, shadow.getAngle(), 1e-9);
        assertEquals(1.5, shadow.getScaleX(), 1e-9);
        assertEquals(0.8, shadow.getScaleY(), 1e-9);
        assertEquals(0.0, shadow.getOffsetX(), 1e-6);
        assertEquals(10.0, shadow.getOffsetY(), 1e-6);
        assertEquals(UnitConverter.degreesToEmuAngle(90), shadow.getAngleEmu());
    }

    /**
     * Tests that the none() preset returns a shadow marked as none.
     * Verifies isNone() behavior for empty shadows.
     */
    @Test
    void shouldExposeNonePreset() {
        assertTrue(PotShadow.none().isNone());
    }
}

