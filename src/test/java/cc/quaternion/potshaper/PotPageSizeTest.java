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
 * Test cases for PotPageSize functionality.
 * Validates page size dimensions, orientation, and custom size creation.
 */
class PotPageSizeTest {

    /**
     * Tests predefined page size dimensions and orientation properties.
     * Verifies width, height, aspect ratio, and isLandscape() methods.
     */
    @Test
    void shouldExposeExpectedDimensionsAndOrientation() {
        PotPageSize size = PotPageSize.WIDESCREEN_16_9;

        assertEquals(960.0, size.getWidth(), 1e-9);
        assertEquals(540.0, size.getHeight(), 1e-9);
        assertTrue(size.isLandscape());
        assertEquals(16.0 / 9.0, size.getAspectRatio(), 1e-9);
        assertEquals(960, size.toDimension().width);
    }

    /**
     * Tests custom page size creation using different units.
     * Verifies points, inches, and centimeters produce equivalent results.
     */
    @Test
    void shouldBuildCustomSizesFromDifferentUnits() {
        PotPageSize.CustomPageSize byPoints = PotPageSize.custom(720, 540);
        PotPageSize.CustomPageSize byInches = PotPageSize.customInches(10, 7.5);
        PotPageSize.CustomPageSize byCm = PotPageSize.customCm(25.4, 19.05);

        assertEquals(720.0, byPoints.getWidth(), 1e-9);
        assertEquals(720.0, byInches.getWidth(), 1e-9);
        assertEquals(720.0, byCm.getWidth(), 1e-6);
        assertEquals(byPoints.getWidthEmu(), byInches.getWidthEmu());
    }
}

