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

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test cases for ActionHelper functionality.
 * Validates action application and removal operations on shapes.
 */
class ActionHelperTest {

    /**
     * Verifies that actions can be applied and removed without breaking the slide workflow.
     * Tests the complete lifecycle: create shape, apply action, remove action, and save.
     *
     * @throws Exception if any operation fails during the test
     */
    @Test
    void shouldExecuteActionApplyAndRemoveWithoutBreakingSlideWorkflow() throws Exception {
        File tempFile = Files.createTempFile("potshaper-action-", ".pptx").toFile();
        tempFile.deleteOnExit();

        try (PotPresentation presentation = PotPresentation.create()) {
            PotShape shape = presentation.getSlide(0).addShape(PotShapeType.RECTANGLE);

            shape.action(PotAction.goToNextSlide());
            shape.removeAction(false);

            assertNotNull(shape.getRawShape());
            presentation.save(tempFile);
        }
    }
}
