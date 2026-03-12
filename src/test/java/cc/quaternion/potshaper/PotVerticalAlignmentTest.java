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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for PotVerticalAlignment functionality.
 * Validates conversion between PotVerticalAlignment and Apache POI VerticalAlignment.
 */
class PotVerticalAlignmentTest {

    /**
     * Tests bidirectional conversion between PotVerticalAlignment and POI alignment.
     * Verifies all vertical alignment types convert correctly.
     */
    @Test
    void shouldConvertToAndFromPoiVerticalAlignment() {
        assertEquals(VerticalAlignment.TOP, PotVerticalAlignment.TOP.toPoiAlign());
        assertEquals(VerticalAlignment.MIDDLE, PotVerticalAlignment.MIDDLE.toPoiAlign());
        assertEquals(VerticalAlignment.BOTTOM, PotVerticalAlignment.BOTTOM.toPoiAlign());
        assertEquals(VerticalAlignment.DISTRIBUTED, PotVerticalAlignment.JUSTIFY.toPoiAlign());

        assertEquals(PotVerticalAlignment.TOP, PotVerticalAlignment.fromPoiAlign(null));
        assertEquals(PotVerticalAlignment.BOTTOM, PotVerticalAlignment.fromPoiAlign(VerticalAlignment.BOTTOM));
        assertEquals(PotVerticalAlignment.DISTRIBUTED, PotVerticalAlignment.fromPoiAlign(VerticalAlignment.DISTRIBUTED));
    }
}

