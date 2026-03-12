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
 * Test cases for PotFont functionality.
 * Validates font creation, fluent mutations, and default fallback behavior.
 */
class PotFontTest {

    /**
     * Tests immutable fluent font modifications including style, color, and spacing.
     * Verifies all font properties are correctly applied.
     */
    @Test
    void shouldApplyImmutableFontFluentChanges() {
        PotFont font = PotFont.of("Arial", 20)
            .bold()
            .italic()
            .underline()
            .strikethrough()
            .withColor(PotColor.BLUE)
            .withCharacterSpacing(1.2)
            .withLanguage("en-US");

        assertEquals("Arial", font.getFamily());
        assertEquals(20.0, font.getSize(), 1e-9);
        assertTrue(font.isBold());
        assertTrue(font.isItalic());
        assertTrue(font.isUnderline());
        assertTrue(font.isStrikethrough());
        assertEquals(PotColor.BLUE, font.getColor());
        assertEquals(1.2, font.getCharacterSpacing(), 1e-9);
        assertEquals("en-US", font.getLanguage());
    }

    /**
     * Validates that invalid constructor inputs fall back to default values.
     * Tests null family and negative size handling.
     */
    @Test
    void shouldFallbackToDefaultsForInvalidCtorInput() {
        PotFont font = new PotFont(null, -1);

        assertEquals("Calibri", font.getFamily());
        assertEquals(18.0, font.getSize(), 1e-9);
    }
}

