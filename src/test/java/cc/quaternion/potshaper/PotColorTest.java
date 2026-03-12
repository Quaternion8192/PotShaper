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

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotColor functionality.
 * Validates color parsing, conversion, and transformation operations.
 */
class PotColorTest {

    /**
     * Tests parsing of RGB and RGBA hex color formats.
     * Verifies standard (#RRGGBB), short (#RGB), and 8-digit (AARRGGBB) formats.
     */
    @Test
    void shouldParseHexRgbAndRgbaFormats() {
        PotColor rgb = PotColor.hex("#FF00AA");
        assertEquals(255, rgb.getRed());
        assertEquals(0, rgb.getGreen());
        assertEquals(170, rgb.getBlue());
        assertEquals(255, rgb.getAlpha());

        PotColor rgba = PotColor.hex("11223344");
        assertEquals(17, rgba.getRed());
        assertEquals(34, rgba.getGreen());
        assertEquals(51, rgba.getBlue());
        assertEquals(68, rgba.getAlpha());

        PotColor shortHex = PotColor.hex("0f8");
        assertEquals("00FF88", shortHex.toHex());
    }

    /**
     * Validates that invalid hex inputs fall back to BLACK color.
     * Tests null, empty, and malformed strings.
     */
    @Test
    void shouldFallbackToBlackOnInvalidHexInput() {
        assertSame(PotColor.BLACK, PotColor.hex(null));
        assertSame(PotColor.BLACK, PotColor.hex(""));
        assertSame(PotColor.BLACK, PotColor.hex("not-a-color"));
    }

    /**
     * Tests bidirectional conversion between PotColor and AWT Color.
     * Verifies proper handling of alpha channel and null inputs.
     */
    @Test
    void shouldConvertToAndFromAwtColor() {
        PotColor potColor = PotColor.rgba(10, 20, 30, 40);
        Color awt = potColor.toAwtColor();

        assertEquals(new Color(10, 20, 30, 40), awt);
        assertEquals(potColor, PotColor.fromAwtColor(awt));
        assertSame(PotColor.BLACK, PotColor.fromAwtColor(null));
    }

    /**
     * Tests common color transformation operations.
     * Verifies lighter, darker, mix, and opacity modifications.
     */
    @Test
    void shouldSupportCommonColorTransformations() {
        PotColor base = PotColor.rgb(100, 120, 140);

        PotColor lighter = base.lighter(0.2);
        PotColor darker = base.darker(0.2);
        PotColor mixed = base.mix(PotColor.WHITE, 0.5);

        assertNotEquals(base, lighter);
        assertNotEquals(base, darker);
        assertEquals(255, base.withOpacity(1.0).getAlpha());
        assertEquals(128, base.withOpacity(0.5).getAlpha());

        // Mixed-with-white should become brighter than the original channels.
        assertTrue(mixed.getRed() >= base.getRed());
        assertTrue(mixed.getGreen() >= base.getGreen());
        assertTrue(mixed.getBlue() >= base.getBlue());
    }
}
