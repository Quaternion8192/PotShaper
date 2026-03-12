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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotGradient functionality.
 * Validates gradient building, angle normalization, and stop management.
 */
class PotGradientTest {

    /**
     * Tests gradient building with normalized angles and color stops.
     * Verifies type, angle normalization, stops list, and toString output.
     */
    @Test
    void shouldBuildGradientWithNormalizedAngleAndStops() {
        PotGradient gradient = PotGradient.linear(-45)
            .addStop(0, PotColor.BLACK)
            .addStop(1, PotColor.WHITE)
            .rotateWithShape(false);

        assertEquals(PotGradient.Type.LINEAR, gradient.getType());
        assertEquals(315.0, gradient.getAngle(), 1e-9);
        assertEquals(2, gradient.getStops().size());
        assertEquals(0, gradient.getStops().get(0).getPositionPercent());
        assertEquals(100000, gradient.getStops().get(1).getPositionPercent());
        assertTrue(gradient.toString().contains("stops=2"));
    }

    /**
     * Validates that gradient stops are clamped and unmodifiable.
     * Tests position clamping, default values, and list immutability.
     */
    @Test
    void stopsShouldBeClampedAndUnmodifiable() {
        PotGradient.GradientStop stop = new PotGradient.GradientStop(2.0, null, -0.5);
        assertEquals(1.0, stop.getPosition(), 1e-9);
        assertEquals(PotColor.BLACK, stop.getColor());
        assertEquals(0.0, stop.getOpacity(), 1e-9);

        PotGradient gradient = PotGradient.vertical(PotColor.RED, PotColor.BLUE);
        assertThrows(UnsupportedOperationException.class, () -> gradient.getStops().add(stop));
    }
}

