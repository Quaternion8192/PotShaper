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

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for JsonExporter functionality.
 * Validates JSON export with proper text escaping and detailed presentation data.
 */
class JsonExporterTest {

    /**
     * Verifies that index and detailed JSON exports handle escaped text correctly.
     * Tests proper escaping of quotes and newlines in text content.
     */
    @Test
    void shouldExportIndexAndDetailedJsonWithEscapedText() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);
            slide.addTextBox("hello \"json\"\nline2");
            slide.addShape(PotShapeType.RECTANGLE).setText("shape text");

            String indexJson = presentation.exportJsonIndex();
            String detailedJson = JsonExporter.exportDetailed(presentation);

            assertTrue(indexJson.contains("\"slideCount\""));
            assertTrue(indexJson.contains("\\\"json\\\""));
            assertTrue(indexJson.contains("\"type\": \"textbox\""));

            assertTrue(detailedJson.contains("\"masters\""));
            assertTrue(detailedJson.contains("\"transition\""));
        }
    }
}
