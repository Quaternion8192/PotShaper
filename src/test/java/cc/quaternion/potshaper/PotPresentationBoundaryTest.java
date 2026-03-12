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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for PotPresentation boundary conditions and error handling.
 * Validates slide index bounds, save constraints, closed state rejection, and read-only collections.
 */
class PotPresentationBoundaryTest {

    /**
     * Tests that out-of-range slide indices throw domain-specific exceptions.
     * Verifies negative and too-large indices are properly rejected.
     */
    @Test
    void shouldThrowDomainExceptionWhenSlideIndexIsOutOfRange() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotException ex1 = assertThrows(PotException.class, () -> presentation.getSlide(-1));
            assertEquals(PotException.ErrorCode.INVALID_PARAMETER, ex1.getErrorCode());
            assertEquals(-1, ex1.getContextValue("index"));

            PotException ex2 = assertThrows(PotException.class, () -> presentation.getSlide(presentation.getSlideCount()));
            assertEquals(PotException.ErrorCode.INVALID_PARAMETER, ex2.getErrorCode());
        }
    }

    /**
     * Validates that save operations require a source file.
     * Ensures presentations created in memory cannot be saved without explicit path.
     */
    @Test
    void shouldRejectSaveWithoutSourceFile() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotException ex = assertThrows(PotException.class, presentation::save);
            assertTrue(ex.getMessage().contains("No source file"));
        }
    }

    /**
     * Tests that all operations are rejected after presentation is closed.
     * Verifies presentation, slide, and element mutations are blocked.
     */
    @Test
    void shouldRejectOperationsAfterClose() {
        PotPresentation presentation = PotPresentation.create();
        PotSlide slide = presentation.getSlide(0);
        PotTextBox textBox = slide.addTextBox("closed-check");
        presentation.close();

        PotException ex = assertThrows(PotException.class, presentation::getSlideCount);
        assertTrue(ex.getMessage().toLowerCase().contains("closed"));

        assertThrows(PotException.class, presentation::addSlide);
        assertThrows(PotException.class, slide::getElementCount);
        assertThrows(PotException.class, () -> slide.addTextBox("x"));
        assertThrows(PotException.class, () -> textBox.at(20, 20));
        assertThrows(PotException.class, textBox::getShapeId);
    }

    /**
     * Validates that slide query methods return read-only collections.
     * Ensures findElements results cannot be modified.
     */
    @Test
    void shouldReturnReadOnlyCollectionsForSlideQueries() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);
            slide.addTextBox("a");
            slide.addShape(PotShapeType.RECTANGLE);
            String name = slide.getElements().get(0).getName();

            assertThrows(UnsupportedOperationException.class,
                () -> slide.findElementsByName(name).add(slide.getElements().get(0)));
            assertThrows(UnsupportedOperationException.class,
                () -> slide.findElementsByType(PotElement.class).clear());
            assertThrows(UnsupportedOperationException.class,
                () -> slide.findElements(e -> true).remove(0));
        }
    }
}

