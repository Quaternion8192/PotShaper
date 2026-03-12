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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for complete presentation workflow from creation to save.
 * Validates creation and manipulation of all common slide components in a real-world scenario.
 */
class PotPresentationWorkflowTest {

    private static final byte[] ONE_PIXEL_PNG = Base64.getDecoder().decode(
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII="
    );

    /**
     * Tests end-to-end workflow creating and saving a presentation with all component types.
     * Verifies text boxes, shapes, tables, connectors, images, audio, video, charts, and groups.
     *
     * @throws Exception if file operations fail
     */
    @Test
    void shouldCreateAndManipulateCommonSlideComponents() throws Exception {
        File tempFile = Files.createTempFile("potshaper-workflow-", ".pptx").toFile();
        tempFile.deleteOnExit();

        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotTextBox text = slide.addTextBox("hello").at(40, 30).size(200, 50);
            PotShape shape = slide.addShape(PotShapeType.RECTANGLE).at(80, 120).size(180, 90);
            PotTable table = slide.addTable(2, 2).at(300, 60).size(200, 120);
            PotConnector connector = slide.addConnector(PotConnector.ConnectorType.STRAIGHT)
                .from(100, 200)
                .to(350, 200);
            PotImage image = slide.addImage(ONE_PIXEL_PNG, "image/png").at(520, 40).size(40, 40);
            PotAudio audio = slide.addAudio(new byte[] {1, 2, 3}, "audio/mpeg");
            PotVideo video = slide.addVideo(new byte[] {4, 5, 6}, "video/mp4");
            PotChart chart = slide.addChart("bar").at(60, 260).size(320, 180);
            PotGroup group = slide.group(java.util.List.of(text, shape));

            assertNotNull(text);
            assertNotNull(shape);
            assertNotNull(table);
            assertNotNull(connector);
            assertNotNull(image);
            assertNotNull(audio);
            assertNotNull(video);
            assertNotNull(chart);
            assertNotNull(group);
            assertEquals(0, slide.getIndex());

            presentation.save(tempFile);
        }

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }

    @Test
    void shouldBindAudioVideoReferencesUnderNvPr() throws Exception {
        File tempFile = Files.createTempFile("potshaper-media-xml-", ".pptx").toFile();
        tempFile.deleteOnExit();

        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);
            slide.addAudio(new byte[] {1, 2, 3}, "audio/mpeg");
            slide.addVideo(new byte[] {4, 5, 6}, "video/mp4");
            presentation.save(tempFile);
        }

        String slideXml = readZipEntry(tempFile, "ppt/slides/slide1.xml");
        assertTrue(slideXml.contains("<a:audioFile"));
        assertTrue(slideXml.contains("<a:videoFile"));

        Pattern mediaInCNvPr = Pattern.compile("<p:cNvPr[^>]*>\\s*<a:(audioFile|videoFile)");
        assertTrue(
            !mediaInCNvPr.matcher(slideXml).find(),
            "audioFile/videoFile must not be attached directly under cNvPr"
        );
    }

    @Test
    void shouldGenerateNonEmptyChartDefinition() throws Exception {
        File tempFile = Files.createTempFile("potshaper-chart-xml-", ".pptx").toFile();
        tempFile.deleteOnExit();

        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);
            slide.addChart("bar");
            presentation.save(tempFile);
        }

        String chartXml = readZipEntry(tempFile, "ppt/charts/chart1.xml");
        assertTrue(
            chartXml.contains("<c:barChart") || chartXml.contains("<c:lineChart") || chartXml.contains("<c:pieChart"),
            "chart XML should include a concrete chart type"
        );
        assertTrue(chartXml.contains("<c:ser"), "chart XML should include at least one series");
    }

    private static String readZipEntry(File file, String entryName) throws IOException {
        try (ZipFile zipFile = new ZipFile(file)) {
            ZipEntry entry = zipFile.getEntry(entryName);
            assertNotNull(entry, "Missing entry: " + entryName);
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
    }
}
