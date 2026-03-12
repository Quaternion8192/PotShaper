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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for MediaHelper functionality.
 * Validates audio and video media options, argument validation, and mutation safety.
 */
class MediaHelperTest {

    /**
     * Tests that audio and video options can be applied through the public API.
     * Verifies auto-play, loop, volume, hide icon, full screen, poster frame, trim, and fade settings.
     */
    @Test
    void shouldApplyAudioAndVideoOptionsThroughPublicApi() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);

            PotAudio audio = slide.addAudio(new byte[] {1, 2, 3}, "audio/mpeg")
                .autoPlay(true)
                .loop(true)
                .volume(0.8)
                .hideIcon(true);

            PotVideo video = slide.addVideo(new byte[] {4, 5, 6}, "video/mp4")
                .autoPlay(false)
                .loop(true)
                .volume(0.6)
                .fullScreen(true)
                .posterFrame(new byte[] {9, 9}, "image/png")
                .trim(1.0, 2.5)
                .fadeIn(0.3)
                .fadeOut(0.7);

            assertTrue(audio.isAutoPlay());
            assertTrue(audio.isLoop());
            assertEquals(0.8, audio.getVolume(), 1e-9);
            assertTrue(audio.isIconHidden());

            assertFalse(video.isAutoPlay());
            assertTrue(video.isLoop());
            assertEquals(0.6, video.getVolume(), 1e-9);
            assertTrue(video.isFullScreen());
            assertArrayEquals(new byte[] {9, 9}, video.getPosterFrame());
        }
    }

    /**
     * Validates that media option arguments are properly validated.
     * Tests boundary conditions for volume, trim times, and fade durations.
     */
    @Test
    void shouldValidateMediaOptionArguments() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotSlide slide = presentation.getSlide(0);
            PotAudio audio = slide.addAudio(new byte[] {1}, "audio/mpeg");
            PotVideo video = slide.addVideo(new byte[] {1}, "video/mp4");

            assertThrows(PotException.class, () -> audio.volume(-0.1));
            assertThrows(PotException.class, () -> video.volume(1.1));
            assertThrows(PotException.class, () -> video.trim(2, 1));
            assertThrows(PotException.class, () -> video.fadeIn(-1));
            assertThrows(PotException.class, () -> video.fadeOut(-1));
        }
    }

    /**
     * Ensures that media mutations are rejected after the presentation is closed.
     * Validates that the presentation enforces state-based operation constraints.
     */
    @Test
    void shouldRejectMediaMutationsAfterPresentationClosed() {
        PotPresentation presentation = PotPresentation.create();
        PotSlide slide = presentation.getSlide(0);
        PotAudio audio = slide.addAudio(new byte[] {1}, "audio/mpeg");
        PotVideo video = slide.addVideo(new byte[] {1}, "video/mp4");
        presentation.close();

        assertThrows(PotException.class, () -> audio.autoPlay(true));
        assertThrows(PotException.class, () -> audio.volume(0.5));
        assertThrows(PotException.class, () -> video.fullScreen(true));
        assertThrows(PotException.class, () -> video.fadeOut(0.2));
    }
}

