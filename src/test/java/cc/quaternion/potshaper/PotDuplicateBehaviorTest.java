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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test cases for duplicate behavior across different element types.
 * Validates that shape, text box, table, connector, and image duplication works correctly,
 * while audio, video, chart, group, and unknown elements throw UnsupportedOperationException.
 */
class PotDuplicateBehaviorTest {

    /**
     * Tests duplication of shape, text box, and table with core properties preserved.
     * Verifies UUID uniqueness and property equality after duplication.
     */
    @Test
    void shouldDuplicateShapeTextBoxAndTableWithCoreProperties() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotShape shape = slide.addShape(PotShapeType.RECTANGLE)
                .at(10, 20)
                .size(120, 80)
                .fill(PotColor.RED)
                .setText("shape")
                .rotate(25);
            PotShape shapeCopy = shape.duplicate();
            assertNotEquals(shape.getUUID(), shapeCopy.getUUID());
            assertEquals(shape.getWidth(), shapeCopy.getWidth(), 1e-6);
            assertEquals(shape.getHeight(), shapeCopy.getHeight(), 1e-6);
            assertEquals(shape.getRotation(), shapeCopy.getRotation(), 1e-6);
            assertEquals(shape.getText(), shapeCopy.getText());

            PotTextBox textBox = slide.addTextBox("hello")
                .at(40, 60)
                .size(200, 50)
                .fontSize(18)
                .rotate(15);
            PotTextBox textCopy = textBox.duplicate();
            assertNotEquals(textBox.getUUID(), textCopy.getUUID());
            assertEquals(textBox.getText(), textCopy.getText());
            assertEquals(textBox.getWidth(), textCopy.getWidth(), 1e-6);
            assertEquals(textBox.getHeight(), textCopy.getHeight(), 1e-6);
            assertEquals(textBox.getRotation(), textCopy.getRotation(), 1e-6);

            PotTable table = slide.addTable(2, 2)
                .at(80, 100)
                .size(240, 120)
                .setData(new String[][] {{"A", "B"}, {"C", "D"}});
            PotTable tableCopy = table.duplicate();
            assertNotEquals(table.getUUID(), tableCopy.getUUID());
            assertEquals(table.getRowCount(), tableCopy.getRowCount());
            assertEquals(table.getColumnCount(), tableCopy.getColumnCount());
            assertEquals("A", tableCopy.cell(0, 0).getText());
            assertEquals("D", tableCopy.cell(1, 1).getText());
        }
    }

    /**
     * Validates that unsupported element types throw UnsupportedOperationException on duplicate().
     * Tests audio, video, chart, group, and unknown elements.
     */
    @Test
    void shouldThrowUnsupportedOperationForAudioVideoChartGroupAndUnknown() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotAudio audio = slide.addAudio(new byte[] {1, 2, 3}, "audio/mpeg");
            PotVideo video = slide.addVideo(new byte[] {4, 5, 6}, "video/mp4");
            PotChart chart = slide.addChart("BAR_CHART");

            PotShape shapeA = slide.addShape(PotShapeType.RECTANGLE).at(20, 20).size(60, 30);
            PotShape shapeB = slide.addShape(PotShapeType.ELLIPSE).at(100, 20).size(60, 30);
            PotGroup group = slide.group(List.of(shapeA, shapeB));

            PotShape wrapped = slide.addShape(PotShapeType.DIAMOND);
            PotUnknownElement unknown = new PotUnknownElement(wrapped.getRawShape(), slide, "unknown-test-id");

            assertUnsupported(audio::duplicate);
            assertUnsupported(video::duplicate);
            assertUnsupported(chart::duplicate);
            assertUnsupported(group::duplicate);
            assertUnsupported(unknown::duplicate);
        }
    }

    /**
     * Tests connector duplication preserving geometry and style properties.
     * Verifies type, coordinates, line color, and line width are copied correctly.
     */
    @Test
    void shouldDuplicateConnectorWithCoreGeometryAndStyle() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotConnector connector = slide.addConnector(PotConnector.ConnectorType.ELBOW)
                .line(10, 20, 180, 120)
                .lineColor(PotColor.BLUE)
                .lineWidth(2.5);

            PotConnector copy = connector.duplicate();

            assertNotEquals(connector.getUUID(), copy.getUUID());
            assertEquals(connector.getConnectorType(), copy.getConnectorType());
            assertEquals(connector.getStartX(), copy.getStartX(), 1e-6);
            assertEquals(connector.getStartY(), copy.getStartY(), 1e-6);
            assertEquals(connector.getEndX(), copy.getEndX(), 1e-6);
            assertEquals(connector.getEndY(), copy.getEndY(), 1e-6);
            assertEquals(connector.getLineColor(), copy.getLineColor());
            assertEquals(connector.getLineWidth(), copy.getLineWidth(), 1e-6);
        }
    }

    /**
     * Tests image duplication without returning null.
     * Verifies UUID uniqueness and dimension/rotation preservation.
     */
    @Test
    void shouldDuplicateImageWithoutReturningNull() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotImage image = slide.addImage(new byte[] {1, 2, 3, 4}, "image/png")
                .at(30, 40)
                .size(120, 80)
                .rotate(15);

            PotImage copy = image.duplicate();

            assertNotEquals(image.getUUID(), copy.getUUID());
            assertEquals(image.getWidth(), copy.getWidth(), 1e-6);
            assertEquals(image.getHeight(), copy.getHeight(), 1e-6);
            assertEquals(image.getRotation(), copy.getRotation(), 1e-6);
        }
    }

    private void assertUnsupported(Runnable action) {
        PotException ex = assertThrows(PotException.class, action::run);
        assertEquals(PotException.ErrorCode.UNSUPPORTED_OPERATION, ex.getErrorCode());
    }
}



