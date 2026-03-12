import cc.quaternion.potshaper.PotAlignment;
import cc.quaternion.potshaper.PotBorder;
import cc.quaternion.potshaper.PotColor;
import cc.quaternion.potshaper.PotFill;
import cc.quaternion.potshaper.PotFont;
import cc.quaternion.potshaper.PotGradient;
import cc.quaternion.potshaper.PotPresentation;
import cc.quaternion.potshaper.PotShape;
import cc.quaternion.potshaper.PotShapeType;
import cc.quaternion.potshaper.PotSlide;
import cc.quaternion.potshaper.PotTable;
import cc.quaternion.potshaper.PotTextBox;
import cc.quaternion.potshaper.PotVerticalAlignment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Program 2: Content and style example.
 * Demonstrates text, shapes, and table styling with fluent APIs.
 */
public class Example02ContentAndStyle {

    public static void main(String[] args) throws IOException {
        Path outputDir = Path.of("draft", "output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("example02-content-and-style.pptx");

        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotTextBox heading = slide.addTextBox("Team KPI Snapshot")
                .at(50, 30)
                .size(420, 55)
                .font(PotFont.of("Segoe UI", 30).bold().color(PotColor.DARK_BLUE));

            PotGradient gradient = PotGradient.linear(25)
                .addStop(0.0, PotColor.hex("#E6F2FF"))
                .addStop(1.0, PotColor.hex("#BBD9FF"));

            PotShape card = slide.addShape(PotShapeType.ROUNDED_RECTANGLE)
                .at(40, 100)
                .size(360, 220)
                .fill(PotFill.gradient(gradient))
                .border(PotBorder.solid(1.5, PotColor.DARK_BLUE))
                .cornerRadius(0.15);

            slide.addTextBox("Q1 Growth: +18%\nNPS: 61\nOn-time Delivery: 97%")
                .at(65, 135)
                .size(300, 145)
                .font(PotFont.of("Segoe UI", 18).color(PotColor.DARK_BLUE))
                .verticalAlign(PotVerticalAlignment.MIDDLE);

            PotTable table = slide.addTable(4, 3)
                .at(430, 95)
                .size(460, 250)
                .border(PotBorder.solid(1.0, PotColor.GRAY));

            table.cell(0, 0).setText("Metric").font(PotFont.of("Segoe UI", 14).bold()).fill(PotColor.DARK_BLUE)
                .align(PotAlignment.CENTER).verticalAlign(PotVerticalAlignment.MIDDLE);
            table.cell(0, 1).setText("Target").font(PotFont.of("Segoe UI", 14).bold()).fill(PotColor.DARK_BLUE)
                .align(PotAlignment.CENTER).verticalAlign(PotVerticalAlignment.MIDDLE);
            table.cell(0, 2).setText("Actual").font(PotFont.of("Segoe UI", 14).bold()).fill(PotColor.DARK_BLUE)
                .align(PotAlignment.CENTER).verticalAlign(PotVerticalAlignment.MIDDLE);

            table.cell(1, 0).setText("Revenue");
            table.cell(1, 1).setText("$1.2M");
            table.cell(1, 2).setText("$1.34M");
            table.cell(2, 0).setText("New Leads");
            table.cell(2, 1).setText("850");
            table.cell(2, 2).setText("902");
            table.cell(3, 0).setText("Retention");
            table.cell(3, 1).setText("92%");
            table.cell(3, 2).setText("94%");

            table.fillRange(1, 0, 3, 2, PotColor.hex("#F7FBFF"));

            // Keep this example focused on content/styling APIs without z-order operations.
            presentation.save(outputFile.toFile());
        }

        System.out.println("Generated: " + outputFile.toAbsolutePath());
    }
}
