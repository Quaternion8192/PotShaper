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

import org.apache.poi.sl.usermodel.TextParagraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for PotAlignment functionality.
 * Validates conversion between PotAlignment and Apache POI TextParagraph.TextAlign.
 */
class PotAlignmentTest {

    /**
     * Tests bidirectional conversion between PotAlignment and POI alignment.
     * Verifies all alignment types (LEFT, CENTER, RIGHT, JUSTIFY, DISTRIBUTED) convert correctly.
     */
    @Test
    void shouldConvertToAndFromPoiAlignment() {
        assertEquals(TextParagraph.TextAlign.LEFT, PotAlignment.LEFT.toPoiAlign());
        assertEquals(TextParagraph.TextAlign.CENTER, PotAlignment.CENTER.toPoiAlign());
        assertEquals(TextParagraph.TextAlign.RIGHT, PotAlignment.RIGHT.toPoiAlign());
        assertEquals(TextParagraph.TextAlign.JUSTIFY, PotAlignment.JUSTIFY.toPoiAlign());
        assertEquals(TextParagraph.TextAlign.DIST, PotAlignment.DISTRIBUTED.toPoiAlign());

        assertEquals(PotAlignment.LEFT, PotAlignment.fromPoiAlign(null));
        assertEquals(PotAlignment.CENTER, PotAlignment.fromPoiAlign(TextParagraph.TextAlign.CENTER));
        assertEquals(PotAlignment.DISTRIBUTED, PotAlignment.fromPoiAlign(TextParagraph.TextAlign.DIST));
    }
}

