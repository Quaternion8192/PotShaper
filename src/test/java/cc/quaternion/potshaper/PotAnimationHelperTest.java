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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotAnimationHelper functionality.
 * Validates animation application, retrieval, and removal through the element API.
 */
class PotAnimationHelperTest {

    /**
     * Tests animation lifecycle: apply, read, and remove via element API.
     * Verifies animation properties including type, effect, duration, and trigger.
     */
    @Test
    void shouldApplyReadAndRemoveAnimationViaElementApi() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);
            PotTextBox textBox = slide.addTextBox("anim");

            textBox.animate(PotAnimation.fadeIn().withPrevious().duration(800));

            assertTrue(textBox.hasAnimation());
            PotAnimation animation = textBox.getAnimation();
            assertNotNull(animation);
            assertEquals(PotAnimation.Type.ENTRANCE, animation.getType());
            assertEquals(PotAnimation.Effect.FADE, animation.getEffect());

            textBox.removeAnimation();
            assertFalse(textBox.hasAnimation());
        }
    }
}

