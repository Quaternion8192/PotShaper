import cc.quaternion.potshaper.PotAction;
import cc.quaternion.potshaper.PotAlignment;
import cc.quaternion.potshaper.PotAnimation;
import cc.quaternion.potshaper.PotBorder;
import cc.quaternion.potshaper.PotColor;
import cc.quaternion.potshaper.PotDirection;
import cc.quaternion.potshaper.PotFont;
import cc.quaternion.potshaper.PotHyperlink;
import cc.quaternion.potshaper.PotPresentation;
import cc.quaternion.potshaper.PotShape;
import cc.quaternion.potshaper.PotShapeType;
import cc.quaternion.potshaper.PotSlide;
import cc.quaternion.potshaper.PotTextBox;
import cc.quaternion.potshaper.PotTransition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Program 3: Interaction and motion example.
 * Demonstrates transitions, element animations, hyperlinks, and actions.
 */
public class Example03InteractionAndMotion {

    public static void main(String[] args) throws IOException {
        Path outputDir = Path.of("draft", "output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve("example03-interaction-and-motion.pptx");

        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide intro = presentation.getSlide(0);
            PotSlide details = presentation.addSlide();

            intro.setTransition(PotTransition.push(PotDirection.FROM_RIGHT).withDuration(900));
            details.setTransition(PotTransition.fade().withAdvanceAfter(2500).withAdvanceOnClick(true));

            PotTextBox title = intro.addTextBox("Interactive Slide")
                .at(120, 45)
                .size(520, 70)
                .font(PotFont.of("Calibri", 32).bold().color(PotColor.DARK_BLUE))
                .align(PotAlignment.CENTER)
                .animate(PotAnimation.fadeIn().duration(800).withPrevious());

            PotShape button = intro.addShape(PotShapeType.ROUNDED_RECTANGLE)
                .at(230, 170)
                .size(300, 90)
                .fill(PotColor.hex("#2F80ED"))
                .border(PotBorder.solid(1.5, PotColor.DARK_BLUE))
                .animate(PotAnimation.zoomIn().duration(700));

            intro.addTextBox("Go to Details")
                .at(280, 195)
                .size(200, 40)
                .font(PotFont.of("Calibri", 20).bold().color(PotColor.WHITE))
                .align(PotAlignment.CENTER)
                .hyperlink(PotHyperlink.slide(1));

            button.action(PotAction.goToNextSlide().onClick());
            title.hyperlink("https://github.com/quaternion8192/potshaper");

            details.addTextBox("Second Slide: Action + Hyperlink + Animation")
                .at(90, 80)
                .size(650, 80)
                .font(PotFont.of("Calibri", 26).bold())
                .animate(PotAnimation.flyIn(PotDirection.FROM_BOTTOM).duration(900));

            details.addTextBox("Click to return")
                .at(300, 220)
                .size(180, 40)
                .align(PotAlignment.CENTER)
                .font(PotFont.of("Calibri", 18))
                .hyperlinkToSlide(0);

            presentation.save(outputFile.toFile());
        }

        System.out.println("Generated: " + outputFile.toAbsolutePath());
    }
}

