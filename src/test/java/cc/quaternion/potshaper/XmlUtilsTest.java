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
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for XmlUtils functionality.
 * Validates XML parsing, XPath queries, serialization, and text escaping.
 */
class XmlUtilsTest {

    /**
     * Tests XML parsing, XPath query execution, and serialization.
     * Verifies document creation and attribute extraction.
     */
    @Test
    void shouldParseQueryAndSerializeSimpleXml() {
        String xml = "<root><item id='42'>hello</item></root>";

        Document doc = XmlUtils.parseXml(xml);
        assertNotNull(doc);
        assertEquals("42", XmlUtils.queryString(xml, "string(//item/@id)"));

        String serialized = XmlUtils.serializeXml(doc);
        assertNotNull(serialized);
        assertTrue(serialized.contains("item"));
    }

    /**
     * Tests XML text escaping and unescaping for special characters.
     * Verifies proper handling of <, >, &, and quotes.
     */
    @Test
    void shouldEscapeAndUnescapeXmlText() {
        String raw = "<tag a=\"1\">Tom & Jerry</tag>";

        String escaped = XmlUtils.escapeXml(raw);
        assertEquals("&lt;tag a=&quot;1&quot;&gt;Tom &amp; Jerry&lt;/tag&gt;", escaped);

        String unescaped = XmlUtils.unescapeXml(escaped);
        assertEquals(raw, unescaped);
    }

    /**
     * Tests that invalid XML input returns null instead of throwing exception.
     * Verifies graceful error handling for malformed XML.
     */
    @Test
    void shouldReturnNullForInvalidXml() {
        assertNull(XmlUtils.parseXml("<root>"));
    }
}

