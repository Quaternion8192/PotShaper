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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotException functionality.
 * Validates exception creation, error codes, context handling, and factory methods.
 */
class PotExceptionTest {

    /**
     * Tests default error code and context storage in PotException.
     * Verifies defensive copy contract for context map.
     */
    @Test
    void shouldDefaultToGeneralErrorAndStoreContext() {
        PotException exception = new PotException("failure")
            .withContext("key", 123);

        assertEquals(PotException.ErrorCode.GENERAL_ERROR, exception.getErrorCode());
        assertEquals(123, exception.getContextValue("key"));

        // Defensive copy contract.
        var context = exception.getContext();
        context.put("key", 999);
        assertEquals(123, exception.getContextValue("key"));
    }

    /**
     * Validates that PotException is not double-wrapped.
     * Ensures wrap() returns the original exception when already a PotException.
     */
    @Test
    void shouldNotDoubleWrapPotException() {
        PotException original = PotException.invalidParameter("x", "must be > 0");
        PotException wrapped = PotException.wrap("ignored", original);

        assertSame(original, wrapped);
    }

    /**
     * Tests that factory methods provide consistent error codes and context.
     * Verifies ioError and slideIndexOutOfBounds factory methods.
     */
    @Test
    void shouldProvideConsistentFactoryErrors() {
        PotException io = PotException.ioError("save", new RuntimeException("disk"));
        assertEquals(PotException.ErrorCode.IO_ERROR, io.getErrorCode());
        assertTrue(io.getMessage().contains("save"));

        PotException index = PotException.slideIndexOutOfBounds(3, 2);
        assertEquals(PotException.ErrorCode.INVALID_PARAMETER, index.getErrorCode());
        assertEquals(3, index.getContextValue("index"));
        assertEquals(2, index.getContextValue("slideCount"));
    }
}

