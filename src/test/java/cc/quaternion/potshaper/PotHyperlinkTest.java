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
 * Test cases for PotHyperlink functionality.
 * Validates creation of URL, slide, and email hyperlinks with proper typing.
 */
class PotHyperlinkTest {

    /**
     * Tests creation of typed hyperlinks: URL, slide, and email.
     * Verifies type detection, address format, and helper methods.
     */
    @Test
    void shouldCreateTypedHyperlinks() {
        PotHyperlink url = PotHyperlink.url("https://example.com").tooltip("go");
        PotHyperlink slide = PotHyperlink.slide(2);
        PotHyperlink email = PotHyperlink.email("a@b.com", "hello world");

        assertEquals(PotLinkType.URL, url.getType());
        assertEquals("https://example.com", url.getAddress());
        assertEquals("go", url.getTooltip());
        assertTrue(url.isUrl());

        assertEquals(PotLinkType.SLIDE, slide.getType());
        assertEquals(2, slide.getTargetSlideIndex());
        assertTrue(slide.isSlide());

        assertEquals(PotLinkType.EMAIL, email.getType());
        assertTrue(email.getAddress().startsWith("mailto:"));
        assertEquals("hello world", email.getEmailSubject());
        assertTrue(email.isEmail());
    }
}

