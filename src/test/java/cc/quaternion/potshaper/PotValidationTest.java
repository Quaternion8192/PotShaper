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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test cases for validation logic across all element types.
 * Validates geometry arguments, slide construction, table operations, and text style constraints.
 */
class PotValidationTest {

    /**
     * Tests that IPotElement exposes stable UUID, slide reference, and geometry properties.
     * Verifies basic element contract is maintained.
     */
    @Test
    void shouldExposeStableElementContract() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);
            IPotElement element = slide.addShape(PotShapeType.RECTANGLE).at(50, 60).size(200, 100);

            assertNotNull(element.getUUID());
            assertEquals(slide, element.getSlide());
            assertEquals(50.0, element.getX(), 1e-6);
            assertEquals(60.0, element.getY(), 1e-6);
        }
    }

    /**
     * Validates that element geometry methods reject invalid arguments.
     * Tests NaN, zero, negative, and out-of-range values.
     */
    @Test
    void shouldValidateElementGeometryArguments() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotShape shape = presentation.getSlide(0).addShape(PotShapeType.RECTANGLE);

            assertThrows(PotException.class, () -> shape.at(Double.NaN, 10));
            assertThrows(PotException.class, () -> shape.size(0, 10));
            assertThrows(PotException.class, () -> shape.scale(0));
            assertThrows(PotException.class, () -> shape.rotate(Double.POSITIVE_INFINITY));
            assertThrows(PotException.class, () -> shape.opacity(1.1));
        }
    }

    /**
     * Tests that rotation angles are normalized to [0, 360) range.
     * Verifies negative angles wrap correctly.
     */
    @Test
    void shouldNormalizeRotationAngle() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotShape shape = presentation.getSlide(0).addShape(PotShapeType.RECTANGLE);
            shape.rotate(-30);
            assertEquals(330.0, shape.getRotation(), 1e-6);
        }
    }

    /**
     * Validates slide construction and element addition argument checking.
     * Tests null, blank, empty, and invalid inputs for all element types.
     */
    @Test
    void shouldValidateSlideConstructionArguments() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            assertThrows(PotException.class, () -> slide.addShape(null));
            assertThrows(PotException.class, () -> slide.addTable(0, 2));
            assertThrows(PotException.class, () -> slide.addTextBox("txt", 0, 0, -1, 20));
            assertThrows(PotException.class, () -> slide.addImage(" "));
            assertThrows(PotException.class, () -> slide.addImage((byte[]) null, "image/png"));
            assertThrows(PotException.class, () -> slide.addAudio(new byte[0], "audio/mpeg"));
            assertThrows(PotException.class, () -> slide.addChart(" "));
            assertThrows(PotException.class, () -> slide.getElements(null));
            assertThrows(PotException.class, () -> slide.group(Arrays.asList(slide.addTextBox("a"), null)));
            assertThrows(PotException.class, () -> slide.getElement(" "));
        }
    }

    /**
     * Validates table cell access and structural modification arguments.
     * Tests out-of-bounds indices, invalid dimensions, and merge constraints.
     */
    @Test
    void shouldValidateTableArguments() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotTable table = presentation.getSlide(0).addTable(2, 2);

            assertThrows(PotException.class, () -> table.cell(5, 0));
            assertThrows(PotException.class, () -> table.insertRow(-1));
            assertThrows(PotException.class, () -> table.insertRow(3));
            assertThrows(PotException.class, () -> table.insertColumn(3));
            assertThrows(PotException.class, () -> table.setColumnWidth(0, 0));
            assertThrows(PotException.class, () -> table.setRowHeight(0, -1));
            assertThrows(PotException.class, () -> table.mergeCells(1, 1, 0, 0));
        }
    }

    /**
     * Tests that insertions at tail boundary (rowCount/columnCount) do not throw validation errors.
     * Verifies boundary condition is handled as valid operation.
     */
    @Test
    void shouldAcceptInsertAtTableTailBoundaryWithoutValidationError() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotTable table = presentation.getSlide(0).addTable(2, 2);

            assertDoesNotThrow(() -> table.insertRow(table.getRowCount()));
            assertDoesNotThrow(() -> table.insertColumn(table.getColumnCount()));
        }
    }

    /**
     * Validates text style property arguments.
     * Tests font size, family, and line spacing constraints.
     */
    @Test
    void shouldValidateTextStyleArguments() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotTextBox textBox = presentation.getSlide(0).addTextBox("hello");

            assertThrows(PotException.class, () -> textBox.fontSize(0));
            assertThrows(PotException.class, () -> textBox.fontFamily(" "));
            assertThrows(PotException.class, () -> textBox.lineSpacing(0));
        }
    }
}

