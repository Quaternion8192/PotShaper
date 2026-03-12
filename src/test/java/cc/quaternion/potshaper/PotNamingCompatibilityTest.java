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

/**
 * Test cases for API naming compatibility and method aliases.
 * Validates that legacy setter methods and modern builder-style aliases produce equivalent results.
 */
class PotNamingCompatibilityTest {

    /**
     * Tests equivalence between legacy setX/setY methods and modern x/y builders.
     * Verifies geometry properties match regardless of naming convention used.
     */
    @Test
    void shouldKeepLegacyAndBuilderGeometryNamesEquivalent() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotShape legacy = presentation.getSlide(0).addShape(PotShapeType.RECTANGLE)
                .setX(10)
                .setY(20)
                .setWidth(200)
                .setHeight(100)
                .rotate(30);

            PotShape modern = presentation.getSlide(0).addShape(PotShapeType.RECTANGLE)
                .x(10)
                .y(20)
                .width(200)
                .height(100)
                .rotation(30);

            assertEquals(legacy.getX(), modern.getX(), 1e-6);
            assertEquals(legacy.getY(), modern.getY(), 1e-6);
            assertEquals(legacy.getWidth(), modern.getWidth(), 1e-6);
            assertEquals(legacy.getHeight(), modern.getHeight(), 1e-6);
            assertEquals(legacy.getRotation(), modern.getRotation(), 1e-6);
        }
    }

    /**
     * Tests position() alias method in fluent chain.
     * Verifies x, y, width, height, and rotation can be set using position().
     */
    @Test
    void shouldSupportPositionAliasInChain() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotTextBox textBox = presentation.getSlide(0).addTextBox("name")
                .position(50, 60)
                .width(180)
                .height(40)
                .rotation(45);

            assertEquals(50, textBox.getX(), 1e-6);
            assertEquals(60, textBox.getY(), 1e-6);
            assertEquals(180, textBox.getWidth(), 1e-6);
            assertEquals(40, textBox.getHeight(), 1e-6);
            assertEquals(45, textBox.getRotation(), 1e-6);
        }
    }

    /**
     * Tests builder aliases for chart elements.
     * Verifies position(), width(), height(), and rotation() work on charts.
     */
    @Test
    void shouldSupportBuilderAliasesForChart() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotChart chart = presentation.getSlide(0)
                .addChart("BAR_CHART")
                .position(120, 130)
                .width(300)
                .height(200)
                .rotation(15);

            assertEquals(120, chart.getX(), 1e-6);
            assertEquals(130, chart.getY(), 1e-6);
            assertEquals(300, chart.getWidth(), 1e-6);
            assertEquals(200, chart.getHeight(), 1e-6);
        }
    }

    /**
     * Tests that position/size aliases work across all element types.
     * Covers image, table, connector, group, audio, video, and unknown elements.
     */
    @Test
    void shouldSupportAliasesAcrossOtherElementTypes() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotImage image = slide.addImage(new byte[] {1, 2, 3}, "image/png")
                .position(15, 25)
                .width(110)
                .height(70);
            assertEquals(15, image.getX(), 1e-6);
            assertEquals(25, image.getY(), 1e-6);

            PotTable table = slide.addTable(2, 2)
                .x(30)
                .y(40)
                .width(260)
                .height(120);
            assertEquals(30, table.getX(), 1e-6);
            assertEquals(40, table.getY(), 1e-6);

            PotConnector connector = slide.addConnector(PotConnector.ConnectorType.STRAIGHT)
                .line(10, 10, 40, 30)
                .position(50, 60);
            assertEquals(50, connector.getX(), 1e-6);
            assertEquals(60, connector.getY(), 1e-6);

            PotShape s1 = slide.addShape(PotShapeType.RECTANGLE).at(10, 10).size(30, 30);
            PotShape s2 = slide.addShape(PotShapeType.DIAMOND).at(50, 10).size(30, 30);
            PotGroup group = slide.group(java.util.List.of(s1, s2)).position(120, 80);
            assertEquals(120, group.getX(), 1e-6);
            assertEquals(80, group.getY(), 1e-6);

            PotAudio audio = slide.addAudio(new byte[] {9}, "audio/mpeg")
                .x(70)
                .y(90)
                .width(40)
                .height(40);
            assertEquals(70, audio.getX(), 1e-6);
            assertEquals(90, audio.getY(), 1e-6);

            PotVideo video = slide.addVideo(new byte[] {8, 7}, "video/mp4")
                .position(150, 95)
                .width(160)
                .height(90);
            assertEquals(150, video.getX(), 1e-6);
            assertEquals(95, video.getY(), 1e-6);

            PotShape raw = slide.addShape(PotShapeType.ELLIPSE);
            PotUnknownElement unknown = new PotUnknownElement(raw.getRawShape(), slide, "unknown-alias-test")
                .position(33, 44)
                .width(77)
                .height(55);
            assertEquals(33, unknown.getX(), 1e-6);
            assertEquals(44, unknown.getY(), 1e-6);
        }
    }
}


