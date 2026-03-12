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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for ShapeTypeMapper functionality.
 * Validates bidirectional shape type mapping and shape family classification.
 */
class ShapeTypeMapperTest {

    /**
     * Tests bidirectional mapping between PotShapeType and Apache POI ShapeType.
     * Verifies null handling and round-trip conversion for key shape types.
     */
    @Test
    void shouldMapKeyShapeTypesBothWays() {
        assertEquals(ShapeType.RECT, ShapeTypeMapper.toPoiType(PotShapeType.RECTANGLE));
        assertEquals(ShapeType.RIGHT_ARROW, ShapeTypeMapper.toPoiType(PotShapeType.RIGHT_ARROW));

        PotShapeType rectMapped = ShapeTypeMapper.toPotType(ShapeType.RECT);
        assertEquals(ShapeType.RECT, ShapeTypeMapper.toPoiType(rectMapped));
        assertEquals(PotShapeType.RIGHT_ARROW, ShapeTypeMapper.toPotType(ShapeType.RIGHT_ARROW));

        assertEquals(ShapeType.RECT, ShapeTypeMapper.toPoiType(null));
        assertEquals(PotShapeType.RECTANGLE, ShapeTypeMapper.toPotType(null));
    }

    /**
     * Tests shape family classification methods.
     * Verifies flowchart, callout, arrow, star, and action button detection.
     */
    @Test
    void shouldClassifyShapeFamiliesCorrectly() {
        assertTrue(ShapeTypeMapper.isFlowchart(PotShapeType.FLOWCHART_PROCESS));
        assertTrue(ShapeTypeMapper.isCallout(PotShapeType.CALLOUT_CLOUD));
        assertTrue(ShapeTypeMapper.isArrow(PotShapeType.RIGHT_ARROW));
        assertTrue(ShapeTypeMapper.isStar(PotShapeType.STAR_5));
        assertTrue(ShapeTypeMapper.isActionButton(PotShapeType.ACTION_BUTTON_HOME));

        assertFalse(ShapeTypeMapper.isArrow(PotShapeType.CALLOUT_CLOUD));
        assertFalse(ShapeTypeMapper.isActionButton(PotShapeType.RECTANGLE));
    }
}
