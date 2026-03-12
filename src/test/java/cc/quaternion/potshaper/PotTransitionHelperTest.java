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
 * Test cases for PotTransitionHelper functionality.
 * Validates slide transition application, configuration, and removal.
 */
class PotTransitionHelperTest {

    /**
     * Tests complete transition lifecycle through the slide API.
     * Verifies setting, reading, and removing transitions with all configuration options.
     */
    @Test
    void shouldRoundTripTransitionThroughSlideApi() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotTransition transition = PotTransition.push(PotDirection.FROM_RIGHT)
                .withDuration(900)
                .withAdvanceOnClick(false)
                .withAdvanceAfter(2500);

            slide.setTransition(transition);
            PotTransition restored = slide.getTransition();

            assertEquals(PotTransition.Type.PUSH, restored.getType());
            assertEquals(PotDirection.FROM_RIGHT, restored.getDirection());
            assertFalse(restored.isAdvanceOnClick());
            assertTrue(restored.hasAutoAdvance());
            assertEquals(2500, restored.getAdvanceAfterTime());
            assertTrue(restored.getDuration() == 300 || restored.getDuration() == 700 || restored.getDuration() == 1000);

            slide.removeTransition();
            assertTrue(slide.getTransition().isNone());
        }
    }
}

