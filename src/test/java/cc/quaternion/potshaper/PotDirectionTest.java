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

import org.apache.poi.sl.usermodel.TextShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotDirection functionality.
 * Validates direction operations, classification, and angle/POI conversions.
 */
class PotDirectionTest {

    /**
     * Tests that opposite directions are correctly returned for main cases.
     * Covers cardinal directions, diagonal, in/out, clockwise, and none.
     */
    @Test
    void shouldReturnOppositeDirectionForMainCases() {
        assertEquals(PotDirection.FROM_BOTTOM, PotDirection.FROM_TOP.opposite());
        assertEquals(PotDirection.FROM_RIGHT, PotDirection.FROM_LEFT.opposite());
        assertEquals(PotDirection.OUT, PotDirection.IN.opposite());
        assertEquals(PotDirection.COUNTER_CLOCKWISE, PotDirection.CLOCKWISE.opposite());
        assertEquals(PotDirection.NONE, PotDirection.NONE.opposite());
    }

    /**
     * Tests horizontal, vertical, and diagonal direction classification.
     * Verifies isHorizontal(), isVertical(), and isDiagonal() methods.
     */
    @Test
    void shouldClassifyHorizontalVerticalAndDiagonal() {
        assertTrue(PotDirection.HORIZONTAL.isHorizontal());
        assertFalse(PotDirection.HORIZONTAL.isVertical());

        assertTrue(PotDirection.VERTICAL.isVertical());
        assertFalse(PotDirection.VERTICAL.isDiagonal());

        assertTrue(PotDirection.FROM_TOP_LEFT.isDiagonal());
        assertFalse(PotDirection.FROM_TOP_LEFT.isHorizontal());
    }

    /**
     * Tests that directions expose expected angles and POI text direction mapping.
     * Verifies getAngle() and toPoiTextDirection() methods.
     */
    @Test
    void shouldExposeExpectedAnglesAndPoiDirection() {
        assertEquals(0, PotDirection.RIGHT.getAngle());
        assertEquals(90, PotDirection.DOWN.getAngle());
        assertEquals(180, PotDirection.LEFT.getAngle());
        assertEquals(270, PotDirection.UP.getAngle());

        assertEquals(TextShape.TextDirection.HORIZONTAL, PotDirection.HORIZONTAL.toPoiTextDirection());
        assertEquals(TextShape.TextDirection.VERTICAL, PotDirection.VERTICAL.toPoiTextDirection());
        assertEquals(TextShape.TextDirection.HORIZONTAL, PotDirection.NONE.toPoiTextDirection());
    }
}

