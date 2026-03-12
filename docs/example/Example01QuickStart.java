import cc.quaternion.potshaper.PotAlignment;
import cc.quaternion.potshaper.PotBorder;
import cc.quaternion.potshaper.PotColor;
import cc.quaternion.potshaper.PotFont;
import cc.quaternion.potshaper.PotPageSize;
import cc.quaternion.potshaper.PotPresentation;
import cc.quaternion.potshaper.PotShape;
import cc.quaternion.potshaper.PotShapeType;
import cc.quaternion.potshaper.PotSlide;
import cc.quaternion.potshaper.PotTextBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Program 1: Quick start example.
 * Demonstrates creating a presentation, adding common elements, and saving to disk.
 */
public class Example01QuickStart {

    public static void main(String[] args) throws IOException {
        Path outputDir = Path.of("draft", "output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("example01-quick-start.pptx");

        try (PotPresentation presentation = PotPresentation.create(PotPageSize.WIDESCREEN_16_9)) {
            PotSlide slide = presentation.getSlide(0);

            PotTextBox title = slide.addTextBox("PotShaper Quick Start")
                .at(80, 50)
                .size(640, 70)
                .font(PotFont.of("Calibri", 34).bold().color(PotColor.DARK_BLUE))
                .align(PotAlignment.CENTER);

            PotShape banner = slide.addShape(PotShapeType.ROUNDED_RECTANGLE)
                .at(120, 150)
                .size(560, 130)
                .fill(PotColor.LIGHT_BLUE)
                .border(PotBorder.solid(2.0, PotColor.DARK_BLUE));

            slide.addTextBox("Create -> Edit -> Save")
                .at(180, 190)
                .size(440, 50)
                .font(PotFont.of("Calibri", 22).bold())
                .align(PotAlignment.CENTER);

            // Avoid z-order XML rewrites in this basic sample to keep saving robust.
            presentation.save(outputFile.toFile());
        }

        System.out.println("Generated: " + outputFile.toAbsolutePath());
    }
}
