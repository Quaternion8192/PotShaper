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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test cases for PotAction functionality.
 * Validates action creation, argument validation, trigger mode switching, and type resolution.
 */
class PotActionTest {

    /**
     * Tests building navigation actions with expected type and target values.
     * Verifies goToSlide and endShow action construction.
     */
    @Test
    void shouldBuildNavigationActionsWithExpectedTypeAndTarget() {
        PotAction goToSlide = PotAction.goToSlide(3);
        assertEquals(PotActionType.GO_TO_SLIDE, goToSlide.getType());
        assertEquals("3", goToSlide.getTarget());
        assertTrue(goToSlide.isOnClick());

        PotAction endShow = PotAction.endShow();
        assertEquals(PotActionType.END_SHOW, endShow.getType());
        assertNull(endShow.getTarget());
    }

    /**
     * Validates that action arguments are properly checked.
     * Tests for IllegalArgumentException and NullPointerException on invalid inputs.
     */
    @Test
    void shouldValidateActionArguments() {
        assertThrows(IllegalArgumentException.class, () -> PotAction.goToSlide(-1));
        assertThrows(NullPointerException.class, () -> PotAction.hyperlink(null));
        assertThrows(NullPointerException.class, () -> PotAction.runProgram(null));
        assertThrows(NullPointerException.class, () -> PotAction.runMacro(null));
        assertThrows(NullPointerException.class, () -> PotAction.playSound(null));
    }

    /**
     * Tests that trigger mode switching works correctly and idempotently.
     * Verifies conversion between onClick and onHover modes.
     */
    @Test
    void shouldSwitchTriggerModeIdempotently() {
        PotAction click = PotAction.goToNextSlide();
        PotAction hover = click.onHover();

        assertFalse(click.isOnHover());
        assertTrue(hover.isOnHover());
        assertSame(hover, hover.onHover());
        assertSame(click, click.onClick());
        assertTrue(hover.onClick().isOnClick());
    }

    /**
     * Tests action type resolution from string values.
     * Verifies valid type lookup and exception on invalid values.
     */
    @Test
    void shouldResolveActionTypeFromValue() {
        assertEquals(PotActionType.HYPERLINK, PotActionType.fromValue("hyperlink"));
        assertThrows(IllegalArgumentException.class, () -> PotActionType.fromValue("invalid"));
    }
}
