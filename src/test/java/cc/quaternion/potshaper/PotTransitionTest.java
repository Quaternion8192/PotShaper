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
 * Test cases for PotTransition functionality.
 * Validates transition building, configuration chaining, and equality semantics.
 */
class PotTransitionTest {

    /**
     * Tests transition building with fluent option chaining.
     * Verifies type, direction, duration, and advance settings.
     */
    @Test
    void shouldBuildAndChainTransitionOptions() {
        PotTransition transition = PotTransition.push(PotDirection.FROM_RIGHT)
            .withDuration(1200)
            .withAdvanceOnClick(false)
            .withAdvanceAfter(3000)
            .withDirection(PotDirection.FROM_LEFT);

        assertEquals(PotTransition.Type.PUSH, transition.getType());
        assertEquals(PotDirection.FROM_LEFT, transition.getDirection());
        assertEquals(1200, transition.getDuration());
        assertFalse(transition.isAdvanceOnClick());
        assertTrue(transition.hasAutoAdvance());
        assertEquals(3000, transition.getAdvanceAfterTime());
    }

    /**
     * Tests transition equality contract and none semantics.
     * Verifies equals(), hashCode(), and isNone() behavior.
     */
    @Test
    void shouldSupportEqualityAndNoneSemantics() {
        PotTransition a = PotTransition.fade().withDuration(800);
        PotTransition b = PotTransition.fade().withDuration(800);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(PotTransition.none().isNone());
    }
}

