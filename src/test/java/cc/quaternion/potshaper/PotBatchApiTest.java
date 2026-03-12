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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for batch API operations.
 * Validates element finding, table filling, and bulk operation functionality.
 */
class PotBatchApiTest {

    /**
     * Tests finding elements by type, name, and predicate on a slide.
     * Verifies that shapes, text boxes, and images can be located using different criteria.
     */
    @Test
    void shouldFindElementsByTypeNameAndPredicate() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotShape shape = slide.addShape(PotShapeType.RECTANGLE);
            PotTextBox textBox = slide.addTextBox("hello");
            PotImage image = slide.addImage(new byte[] {1, 2, 3}, "image/png");

            List<PotShape> shapes = slide.findElementsByType(PotShape.class);
            List<PotElement> named = slide.findElementsByName(shape.getName());
            List<PotElement> largeOnes = slide.findElements(e -> e.getWidth() >= 100);

            assertEquals(1, shapes.size());
            assertEquals(shape.getUUID(), shapes.get(0).getUUID());
            assertTrue(named.stream().anyMatch(e -> e.getUUID().equals(shape.getUUID())));
            assertTrue(largeOnes.stream().anyMatch(e -> e.getUUID().equals(textBox.getUUID())));
            assertTrue(largeOnes.stream().anyMatch(e -> e.getUUID().equals(image.getUUID())));
        }
    }

    /**
     * Validates that findElements methods properly check their arguments.
     * Tests for exceptions on null or blank inputs.
     */
    @Test
    void shouldValidateFindElementsArguments() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            assertThrows(PotException.class, () -> slide.findElementsByType(null));
            assertThrows(PotException.class, () -> slide.findElementsByName("  "));
            assertThrows(PotException.class, () -> slide.findElements(null));
        }
    }

    /**
     * Tests table fill operations by row, column, and range.
     * Verifies that cells receive the correct fill colors.
     */
    @Test
    void shouldFillTableByRowColumnAndRange() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotTable table = presentation.getSlide(0).addTable(3, 3);

            table.fillRow(0, PotColor.RED)
                .fillColumn(2, PotColor.BLUE)
                .fillRange(1, 0, 2, 1, PotColor.GREEN);

            assertEquals(PotColor.RED, table.cell(0, 0).getFillColor());
            assertEquals(PotColor.BLUE, table.cell(0, 2).getFillColor());
            assertEquals(PotColor.GREEN, table.cell(1, 0).getFillColor());
            assertEquals(PotColor.GREEN, table.cell(2, 1).getFillColor());
        }
    }

    /**
     * Validates that fill range arguments are properly checked.
     * Tests for exceptions on out-of-bounds indices.
     */
    @Test
    void shouldValidateFillRangeArguments() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotTable table = presentation.getSlide(0).addTable(2, 2);

            assertThrows(PotException.class, () -> table.fillRow(2, PotColor.RED));
            assertThrows(PotException.class, () -> table.fillColumn(-1, PotColor.RED));
            assertThrows(PotException.class, () -> table.fillRange(1, 1, 0, 0, PotColor.RED));
        }
    }
}

