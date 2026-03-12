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

import org.apache.poi.xslf.usermodel.XSLFPictureShape;

/**
 * Represents a video element within a presentation slide.
 *
 * <p>This class provides a fluent API for configuring video properties such as playback behavior,
 * volume, poster frames, trimming, and visual effects. It wraps an underlying Apache POI
 * {@code XSLFPictureShape} that stores the video media and its presentation attributes.
 * All configuration methods ensure the parent presentation is open for modification and
 * propagate changes to the underlying shape when it exists. The class extends {@code PotElement},
 * inheriting common slide element functionality for positioning, sizing, animation, and styling.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * PotVideo video = slide.addVideo(videoBytes, "video/mp4")
 *     .at(100, 100)
 *     .size(400, 300)
 *     .autoPlay(false)
 *     .loop(false)
 *     .fullScreen(true)
 *     .posterFrame(thumbnailBytes, "image/jpeg");
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotVideo extends PotElement {

    private boolean autoPlay = false;
    private boolean loop = false;
    private double volume = 1.0;
    private boolean fullScreen = false;
    private byte[] posterFrame;
    private String posterFrameType;

    /**
     * Constructs a new video element from an existing Apache POI picture shape.
     *
     * <p>This constructor is typically used internally when loading a presentation. It associates
     * the provided POI shape with the given parent slide and unique identifier.</p>
     *
     * @param shape       the Apache POI picture shape representing the video
     * @param parentSlide the parent slide containing this video
     * @param uuid        the unique identifier for this element
     */
    public PotVideo(XSLFPictureShape shape, PotSlide parentSlide, String uuid) {
        super(shape, parentSlide, uuid);
    }

    /**
     * Enables or disables automatic playback of the video.
     *
     * <p>When set to {@code true}, the video will begin playing as soon as the slide is displayed
     * during a presentation. The setting is applied to the underlying shape if it exists.</p>
     *
     * @param autoPlay {@code true} to enable automatic playback, {@code false} to disable it
     * @return this video instance for method chaining
     */
    public PotVideo autoPlay(boolean autoPlay) {
        ensurePresentationOpen();
        this.autoPlay = autoPlay;
        if (shape != null) {
            MediaHelper.setAutoPlay((XSLFPictureShape) shape, autoPlay);
        }
        return this;
    }

    /**
     * Enables or disables looping playback of the video.
     *
     * <p>When set to {@code true}, the video will repeat continuously until the slide is exited.
     * The setting is applied to the underlying shape if it exists.</p>
     *
     * @param loop {@code true} to enable looping, {@code false} to disable it
     * @return this video instance for method chaining
     */
    public PotVideo loop(boolean loop) {
        ensurePresentationOpen();
        this.loop = loop;
        if (shape != null) {
            MediaHelper.setLoop((XSLFPictureShape) shape, loop);
        }
        return this;
    }

    /**
     * Sets the playback volume of the video.
     *
     * <p>The volume is specified as a value between 0.0 (silent) and 1.0 (maximum volume).
     * The value is validated and then applied to the underlying shape if it exists.</p>
     *
     * @param volume the volume level, between 0.0 and 1.0 inclusive
     * @return this video instance for method chaining
     * @throws IllegalArgumentException if the volume is outside the valid range
     */
    public PotVideo volume(double volume) {
        ensurePresentationOpen();
        this.volume = ValidationUtils.percentage01(volume, "volume");
        if (shape != null) {
            MediaHelper.setVolume((XSLFPictureShape) shape, volume);
        }
        return this;
    }

    /**
     * Enables or disables fullscreen playback of the video.
     *
     * <p>When set to {@code true}, the video can be played in fullscreen mode during the presentation.
     * The setting is applied to the underlying shape if it exists.</p>
     *
     * @param fullScreen {@code true} to allow fullscreen playback, {@code false} to disable it
     * @return this video instance for method chaining
     */
    public PotVideo fullScreen(boolean fullScreen) {
        ensurePresentationOpen();
        this.fullScreen = fullScreen;
        if (shape != null) {
            MediaHelper.setFullScreen((XSLFPictureShape) shape, fullScreen);
        }
        return this;
    }

    /**
     * Sets a poster frame (thumbnail) image for the video.
     *
     * <p>The poster frame is displayed before the video starts playing. The image data and its
     * content type must be provided. Both parameters are validated for nonemptiness, and the
     * frame is applied to the underlying shape if it exists.</p>
     *
     * @param imageData    the byte array containing the poster image data
     * @param contentType  the MIME type of the image, e.g., "image/jpeg"
     * @return this video instance for method chaining
     * @throws IllegalArgumentException if {@code imageData} is empty or {@code contentType} is blank
     */
    public PotVideo posterFrame(byte[] imageData, String contentType) {
        ensurePresentationOpen();
        this.posterFrame = ValidationUtils.notEmpty(imageData, "imageData");
        this.posterFrameType = ValidationUtils.notBlank(contentType, "contentType");
        if (shape != null) {
            MediaHelper.setPosterFrame((XSLFPictureShape) shape, this.posterFrame, this.posterFrameType);
        }
        return this;
    }

    /**
     * Trims the video to a specific time range.
     *
     * <p>Defines the start and end times (in seconds) for playback. The end time must be greater
     * than or equal to the start time. Both values must be nonnegative. The trim is applied to
     * the underlying shape if it exists.</p>
     *
     * @param startSeconds the start time of the trimmed segment, in seconds
     * @param endSeconds   the end time of the trimmed segment, in seconds
     * @return this video instance for method chaining
     * @throws IllegalArgumentException if either time is negative or if {@code endSeconds < startSeconds}
     */
    public PotVideo trim(double startSeconds, double endSeconds) {
        ensurePresentationOpen();
        ValidationUtils.nonNegative(startSeconds, "startSeconds");
        ValidationUtils.nonNegative(endSeconds, "endSeconds");
        if (endSeconds < startSeconds) {
            throw PotException.invalidParameter("trim", "endSeconds must be >= startSeconds");
        }
        if (shape != null) {
            MediaHelper.setTrim((XSLFPictureShape) shape, startSeconds, endSeconds);
        }
        return this;
    }

    /**
     * Applies a fadein effect to the beginning of the video.
     *
     * <p>The fadein duration is specified in seconds and must be nonnegative. The effect is
     * applied to the underlying shape if it exists.</p>
     *
     * @param seconds the duration of the fadein effect, in seconds
     * @return this video instance for method chaining
     * @throws IllegalArgumentException if {@code seconds} is negative
     */
    public PotVideo fadeIn(double seconds) {
        ensurePresentationOpen();
        ValidationUtils.nonNegative(seconds, "fadeInSeconds");
        if (shape != null) {
            MediaHelper.setFadeIn((XSLFPictureShape) shape, seconds);
        }
        return this;
    }

    /**
     * Applies a fadeout effect to the end of the video.
     *
     * <p>The fadeout duration is specified in seconds and must be nonnegative. The effect is
     * applied to the underlying shape if it exists.</p>
     *
     * @param seconds the duration of the fadeout effect, in seconds
     * @return this video instance for method chaining
     * @throws IllegalArgumentException if {@code seconds} is negative
     */
    public PotVideo fadeOut(double seconds) {
        ensurePresentationOpen();
        ValidationUtils.nonNegative(seconds, "fadeOutSeconds");
        if (shape != null) {
            MediaHelper.setFadeOut((XSLFPictureShape) shape, seconds);
        }
        return this;
    }

    /**
     * Returns whether the video is set to play automatically.
     *
     * @return {@code true} if automatic playback is enabled, {@code false} otherwise
     */
    public boolean isAutoPlay() {
        return autoPlay;
    }

    /**
     * Returns whether the video is set to loop.
     *
     * @return {@code true} if looping is enabled, {@code false} otherwise
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * Returns the current volume setting.
     *
     * @return the volume level, between 0.0 and 1.0
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Returns whether fullscreen playback is allowed.
     *
     * @return {@code true} if fullscreen playback is enabled, {@code false} otherwise
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * Returns the poster frame image data.
     *
     * @return the byte array containing the poster image, or {@code null} if not set
     */
    public byte[] getPosterFrame() {
        return posterFrame;
    }

    /**
     * Sets the position of the video on the slide.
     *
     * <p>This method overrides the parent implementation to return the concrete {@code PotVideo}
     * type for fluent chaining.</p>
     *
     * @param x the horizontal coordinate in points
     * @param y the vertical coordinate in points
     * @return this video instance for method chaining
     * @see PotElement#at(double, double)
     */
    @Override
    public PotVideo at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotVideo setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotVideo x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotVideo setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotVideo y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotVideo position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotVideo move(double dx, double dy) {
        super.move(dx, dy);
        return this;
    }

    /**
     * Sets the width and height of the video.
     *
     * <p>This method overrides the parent implementation to return the concrete {@code PotVideo}
     * type for fluent chaining.</p>
     *
     * @param width  the new width in points
     * @param height the new height in points
     * @return this video instance for method chaining
     * @see PotElement#size(double, double)
     */
    @Override
    public PotVideo size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotVideo setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotVideo width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotVideo setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotVideo height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotVideo scale(double scale) {
        super.scale(scale);
        return this;
    }

    @Override
    public PotVideo scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    /**
     * Rotates the video by the specified angle.
     *
     * <p>This method overrides the parent implementation to return the concrete {@code PotVideo}
     * type for fluent chaining.</p>
     *
     * @param angle the rotation angle in degrees
     * @return this video instance for method chaining
     * @see PotElement#rotate(double)
     */
    @Override
    public PotVideo rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotVideo rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotVideo rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotVideo flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotVideo flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotVideo bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotVideo sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotVideo bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotVideo sendBackward() {
        super.sendBackward();
        return this;
    }

    /**
     * Applies an animation to the video.
     *
     * <p>This method overrides the parent implementation to return the concrete {@code PotVideo}
     * type for fluent chaining.</p>
     *
     * @param animation the animation to apply
     * @return this video instance for method chaining
     * @see PotElement#animate(PotAnimation)
     */
    @Override
    public PotVideo animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotVideo duplicate() {
        throw PotException.unsupportedOperation("duplicate for PotVideo");
    }

    @Override
    public PotVideo removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotVideo hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    /**
     * Sets a hyperlink for the video.
     *
     * <p>This method overrides the parent implementation to return the concrete {@code PotVideo}
     * type for fluent chaining.</p>
     *
     * @param link the hyperlink to set
     * @return this video instance for method chaining
     * @see PotElement#hyperlink(PotHyperlink)
     */
    @Override
    public PotVideo hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotVideo hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotVideo removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotVideo action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotVideo removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotVideo shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotVideo removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotVideo reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotVideo glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotVideo softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotVideo removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotVideo rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotVideo bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotVideo material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotVideo lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }

    @Override
    public PotVideo opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }
}