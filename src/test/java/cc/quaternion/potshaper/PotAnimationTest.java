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
 * Test cases for PotAnimation functionality.
 * Validates animation building with triggers, directions, and equality contract.
 */
class PotAnimationTest {

    /**
     * Tests animation building with trigger mode and direction settings.
     * Verifies all animation properties including duration, delay, repeat count, and auto-reverse.
     */
    @Test
    void shouldBuildAnimationWithTriggerAndDirection() {
        PotAnimation animation = PotAnimation.flyIn(PotDirection.FROM_TOP)
            .duration(1200)
            .delay(300)
            .withPrevious()
            .repeat(2)
            .autoReverse(true);

        assertEquals(PotAnimation.Type.ENTRANCE, animation.getType());
        assertEquals(PotAnimation.Effect.FLY, animation.getEffect());
        assertEquals(PotAnimation.Trigger.WITH_PREVIOUS, animation.getTrigger());
        assertEquals(1200, animation.getDuration());
        assertEquals(300, animation.getDelay());
        assertEquals(PotDirection.FROM_TOP, animation.getDirection());
        assertEquals(2, animation.getRepeatCount());
        assertTrue(animation.isAutoReverse());
        assertTrue(animation.isEntrance());
    }

    /**
     * Tests that animations with identical properties satisfy the equality contract.
     * Verifies equals() and hashCode() consistency for animations with same configuration.
     */
    @Test
    void shouldSupportEqualityContract() {
        PotAnimation a = PotAnimation.fadeIn().duration(1000).afterPrevious();
        PotAnimation b = PotAnimation.fadeIn().duration(1000).afterPrevious();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}

