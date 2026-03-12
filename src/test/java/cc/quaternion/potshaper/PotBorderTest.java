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

import org.apache.poi.sl.usermodel.StrokeStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotBorder functionality.
 * Validates border style creation, mutation, and POI enum mapping.
 */
class PotBorderTest {

    /**
     * Tests creation and fluent mutation of border styles.
     * Verifies width, dash style, cap style, join style, and opacity settings.
     */
    @Test
    void shouldCreateAndMutateBorderStyles() {
        PotBorder border = PotBorder.solid(2, PotColor.BLACK)
            .withDashStyle(PotBorder.DashStyle.DASH_DOT)
            .withCapStyle(PotBorder.CapStyle.ROUND)
            .withJoinStyle(PotBorder.JoinStyle.BEVEL)
            .withOpacity(0.4);

        assertEquals(2.0, border.getWidth(), 1e-9);
        assertEquals(PotBorder.DashStyle.DASH_DOT, border.getDashStyle());
        assertEquals(PotBorder.CapStyle.ROUND, border.getCapStyle());
        assertEquals(PotBorder.JoinStyle.BEVEL, border.getJoinStyle());
        assertEquals(0.4, border.getOpacity(), 1e-9);
        assertEquals(UnitConverter.pointsToEmu(2.0), border.getWidthEmu());
    }

    /**
     * Tests mapping of dash and cap style enums to Apache POI equivalents.
     * Also verifies the none() border behavior.
     */
    @Test
    void shouldMapDashAndCapEnumsToPoi() {
        assertEquals(StrokeStyle.LineDash.DASH_DOT, PotBorder.DashStyle.DASH_DOT.toPoiDash());
        assertEquals(StrokeStyle.LineCap.SQUARE, PotBorder.CapStyle.SQUARE.toPoiCap());
        assertTrue(PotBorder.none().isNone());
    }
}

