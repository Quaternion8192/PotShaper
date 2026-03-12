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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for UuidManager functionality.
 * Validates UUID allocation, format validation, and parsing.
 */
class UuidManagerTest {

    /**
     * Tests that allocated UUIDs are monotonically increasing and uppercase.
     * Verifies sequential allocation produces unique identifiers.
     */
    @Test
    void shouldAllocateMonotonicUppercaseUuid() {
        UuidManager manager = new UuidManager();

        assertEquals("POT:00000001", manager.allocate());
        assertEquals("POT:00000002", manager.allocate());
    }

    /**
     * Tests UUID format validation and value parsing.
     * Verifies case-insensitive validation and proper error handling.
     */
    @Test
    void shouldValidateAndParseUuid() {
        assertTrue(UuidManager.isValidUuid("POT:00ABCDEF"));
        assertTrue(UuidManager.isValidUuid("POT:00abcdef"));
        assertFalse(UuidManager.isValidUuid("pot:00ABCDEF"));
        assertFalse(UuidManager.isValidUuid("POT:123"));
        assertFalse(UuidManager.isValidUuid(null));

        assertEquals(0x00ABCDEF, UuidManager.parseUuidValue("POT:00ABCDEF"));
        assertEquals(-1, UuidManager.parseUuidValue("bad"));
        assertEquals(-1, UuidManager.parseUuidValue(null));
    }
}

