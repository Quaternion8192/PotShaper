import cc.quaternion.potshaper.PotBorder;
import cc.quaternion.potshaper.PotColor;
import cc.quaternion.potshaper.PotFont;
import cc.quaternion.potshaper.PotGroup;
import cc.quaternion.potshaper.PotImage;
import cc.quaternion.potshaper.PotPresentation;
import cc.quaternion.potshaper.PotShape;
import cc.quaternion.potshaper.PotShapeType;
import cc.quaternion.potshaper.PotSlide;
import cc.quaternion.potshaper.PotTextBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 * Program 4: Media and grouping example.
 * Demonstrates image/audio/video/chart creation and grouping elements.
 */
public class Example04MediaAndGrouping {

    private static final byte[] ONE_PIXEL_PNG = Base64.getDecoder().decode(
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII="
    );

    public static void main(String[] args) throws IOException {
        Path outputDir = Path.of("draft", "output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("example04-media-and-grouping.pptx");
        Path mediaDir = Path.of("draft", "media");

        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotShape panel = slide.addShape(PotShapeType.ROUNDED_RECTANGLE)
                .at(40, 35)
                .size(880, 460)
                .fill(PotColor.hex("#F8FAFD"))
                .border(PotBorder.solid(1.0, PotColor.LIGHT_GRAY));

            PotTextBox label = slide.addTextBox("Media + Grouping Demo")
                .at(75, 55)
                .size(350, 45)
                .font(PotFont.of("Calibri", 24).bold().color(PotColor.DARK_BLUE));

            PotImage image = slide.addImage(ONE_PIXEL_PNG, "image/png")
                .at(90, 130)
                .size(180, 120);

            // Use real media files when available; random placeholder bytes can produce unreadable PPTX in Office.
            Path audioPath = mediaDir.resolve("example-audio.mp3");
            if (Files.exists(audioPath)) {
                slide.addAudio(Files.readAllBytes(audioPath), "audio/mpeg").at(320, 130).size(50, 50);
            } else {
                System.out.println("Skipped audio: missing " + audioPath.toAbsolutePath());
            }

            Path videoPath = mediaDir.resolve("example-video.mp4");
            if (Files.exists(videoPath)) {
                slide.addVideo(Files.readAllBytes(videoPath), "video/mp4").at(400, 120).size(260, 160);
            } else {
                System.out.println("Skipped video: missing " + videoPath.toAbsolutePath());
            }

            PotGroup group = slide.group(List.of(panel, label, image));
            group.move(0, 0);

            // Add chart after grouping so it remains visible above the grouped background panel.
            slide.addChart("bar").at(90, 290).size(520, 180);

            presentation.save(outputFile.toFile());
        }

        System.out.println("Generated: " + outputFile.toAbsolutePath());
    }
}

