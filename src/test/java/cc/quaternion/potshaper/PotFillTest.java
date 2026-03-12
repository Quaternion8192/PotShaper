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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotFill functionality.
 * Validates different fill types creation and opacity clamping.
 */
class PotFillTest {

    /**
     * Tests creation of different fill kinds: solid, gradient, picture, and pattern.
     * Verifies type detection, opacity, and color properties.
     */
    @Test
    void shouldCreateDifferentFillKinds() {
        PotFill solid = PotFill.solid(PotColor.GREEN, 0.5);
        PotFill gradient = PotFill.gradient(PotGradient.horizontal(PotColor.BLACK, PotColor.WHITE));
        PotFill picture = PotFill.picture(new byte[] {1, 2, 3});
        PotFill pattern = PotFill.pattern(PotFill.PatternType.CROSS, PotColor.RED, PotColor.WHITE);

        assertTrue(solid.isSolid());
        assertEquals(50000, solid.getOpacityPercent());
        assertTrue(gradient.isGradient());
        assertTrue(picture.isPicture());
        assertTrue(pattern.isPattern());
        assertEquals(PotColor.WHITE, pattern.getBackgroundColor());
        assertNotNull(picture.getImageData());
    }

    /**
     * Validates that opacity values are properly clamped to [0, 1] range.
     * Tests that values exceeding 1.0 are capped at 1.0.
     */
    @Test
    void shouldClampOpacity() {
        PotFill fill = PotFill.solid(PotColor.BLUE).withOpacity(2.0);
        assertEquals(1.0, fill.getOpacity(), 1e-9);
    }
}

