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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for SaveOptions functionality.
 * Validates factory presets and chained configuration with validation.
 */
class SaveOptionsTest {

    /**
     * Tests factory preset methods provide useful default configurations.
     * Verifies web optimization, high compression, and safe save options.
     */
    @Test
    void shouldProvideUsefulFactoryPresets() {
        SaveOptions web = SaveOptions.forWeb();
        assertTrue(web.isOptimizeForWeb());
        assertEquals(65536, web.getBufferSize());

        SaveOptions compressed = SaveOptions.highCompression();
        assertTrue(compressed.isCompressMedia());
        assertTrue(compressed.isCleanMetadata());

        SaveOptions safe = SaveOptions.safe();
        assertTrue(safe.isValidate());
        assertTrue(safe.isCreateBackup());
    }

    /**
     * Tests fluent configuration chaining and argument validation.
     * Verifies buffer size, backup path, and macro removal settings.
     */
    @Test
    void shouldSupportChainedConfigurationAndValidation() {
        SaveOptions options = new SaveOptions()
            .optimizeForWeb(true)
            .bufferSize(4096)
            .compressMedia(true)
            .removeMacros(true)
            .cleanMetadata(true)
            .validate(true)
            .createBackup(true)
            .backupPath("backup.pptx");

        assertEquals(4096, options.getBufferSize());
        assertTrue(options.hasBackupPath());
        assertTrue(options.isRemoveMacros());
        assertFalse(new SaveOptions().hasBackupPath());

        assertThrows(PotException.class, () -> new SaveOptions().bufferSize(0));
        assertThrows(PotException.class, () -> new SaveOptions().backupPath("  "));
    }
}

