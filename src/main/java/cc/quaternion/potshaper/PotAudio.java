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
 * Represents an audio element embedded within a presentation slide.
 *
 * <p>This class provides a fluent API to configure audio playback properties such as auto-play,
 * looping, volume, and icon visibility. It supports common audio formats like MP3, WAV, and AAC.
 * All configuration methods ensure the underlying presentation is open for modification and
 * update the associated Apache POI shape accordingly. Instances of this class are typically
 * created by {@link PotSlide#addAudio} and are not intended to be instantiated directly.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * PotAudio audio = slide.addAudio(audioBytes, "audio/mp3")
 *     .at(50, 50)
 *     .size(50, 50)
 *     .autoPlay(true)
 *     .loop(true)
 *     .volume(0.8);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotAudio extends PotElement {

    private boolean autoPlay = false;
    private boolean loop = false;
    private double volume = 1.0;
    private boolean hideIcon = false;

    /**
     * Constructs a new audio element wrapper.
     *
     * <p>This constructor is intended for internal use by the library. Audio elements should
     * be created via {@link PotSlide#addAudio}.</p>
     *
     * @param shape       the underlying Apache POI picture shape representing the audio
     * @param parentSlide the parent slide containing this audio element
     * @param uuid        the unique identifier for this element
     */
    public PotAudio(XSLFPictureShape shape, PotSlide parentSlide, String uuid) {
        super(shape, parentSlide, uuid);
    }

    /**
     * Enables or disables automatic playback of the audio when the slide is shown.
     *
     * <p>When set to {@code true}, the audio will start playing as soon as the slide appears
     * during a slideshow. This setting is applied to the underlying presentation shape.</p>
     *
     * @param autoPlay {@code true} to enable auto-play, {@code false} to disable it
     * @return this audio element for method chaining
     */
    public PotAudio autoPlay(boolean autoPlay) {
        ensurePresentationOpen();
        this.autoPlay = autoPlay;
        if (shape != null) {
            MediaHelper.setAutoPlay((XSLFPictureShape) shape, autoPlay);
        }
        return this;
    }

    /**
     * Enables or disables continuous looping of the audio.
     *
     * <p>When looping is enabled, the audio will restart from the beginning each time it
     * finishes playing. This setting is applied to the underlying presentation shape.</p>
     *
     * @param loop {@code true} to enable looping, {@code false} to disable it
     * @return this audio element for method chaining
     */
    public PotAudio loop(boolean loop) {
        ensurePresentationOpen();
        this.loop = loop;
        if (shape != null) {
            MediaHelper.setLoop((XSLFPictureShape) shape, loop);
        }
        return this;
    }

    /**
     * Sets the playback volume for the audio.
     *
     * <p>The volume is specified as a value between 0.0 (silent) and 1.0 (maximum volume).
     * Values outside this range will be clamped. This setting is applied to the underlying
     * presentation shape.</p>
     *
     * @param volume the playback volume, between 0.0 and 1.0 inclusive
     * @return this audio element for method chaining
     * @throws IllegalArgumentException if the volume is not a valid percentage
     */
    public PotAudio volume(double volume) {
        ensurePresentationOpen();
        this.volume = ValidationUtils.percentage01(volume, "volume");
        if (shape != null) {
            MediaHelper.setVolume((XSLFPictureShape) shape, volume);
        }
        return this;
    }

    /**
     * Shows or hides the audio icon on the slide.
     *
     * <p>When the icon is hidden, the audio control interface is not visible on the slide,
     * though the audio remains functional if triggered by auto-play or other actions.
     * This setting is applied to the underlying presentation shape.</p>
     *
     * @param hideIcon {@code true} to hide the audio icon, {@code false} to show it
     * @return this audio element for method chaining
     */
    public PotAudio hideIcon(boolean hideIcon) {
        ensurePresentationOpen();
        this.hideIcon = hideIcon;
        if (shape != null) {
            MediaHelper.setHideIcon((XSLFPictureShape) shape, hideIcon);
        }
        return this;
    }

    /**
     * Returns whether the audio is set to play automatically.
     *
     * @return {@code true} if auto-play is enabled, {@code false} otherwise
     */
    public boolean isAutoPlay() {
        return autoPlay;
    }

    /**
     * Returns whether the audio is set to loop continuously.
     *
     * @return {@code true} if looping is enabled, {@code false} otherwise
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * Returns the current playback volume.
     *
     * @return the volume value, between 0.0 and 1.0 inclusive
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Returns whether the audio icon is hidden.
     *
     * @return {@code true} if the icon is hidden, {@code false} otherwise
     */
    public boolean isIconHidden() {
        return hideIcon;
    }

    /**
     * Sets the position of this audio element on the slide.
     *
     * <p>The coordinates are specified in points relative to the top-left corner of the slide.
     * This method updates the underlying shape and returns this audio element for chaining.</p>
     *
     * @param x the horizontal position in points
     * @param y the vertical position in points
     * @return this audio element for method chaining
     * @see PotElement#at(double, double)
     */
    @Override
    public PotAudio at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotAudio setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotAudio x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotAudio setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotAudio y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotAudio position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotAudio move(double dx, double dy) {
        super.move(dx, dy);
        return this;
    }

    /**
     * Sets the size of this audio element.
     *
     * <p>The dimensions are specified in points. This method updates the underlying shape
     * and returns this audio element for chaining.</p>
     *
     * @param width  the width in points
     * @param height the height in points
     * @return this audio element for method chaining
     * @see PotElement#size(double, double)
     */
    @Override
    public PotAudio size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotAudio setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotAudio width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotAudio setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotAudio height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotAudio scale(double scale) {
        super.scale(scale);
        return this;
    }

    @Override
    public PotAudio scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    /**
     * Sets the rotation angle of this audio element.
     *
     * <p>The angle is specified in degrees clockwise. This method updates the underlying shape
     * and returns this audio element for chaining.</p>
     *
     * @param angle the rotation angle in degrees
     * @return this audio element for method chaining
     * @see PotElement#rotate(double)
     */
    @Override
    public PotAudio rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotAudio rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotAudio rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotAudio flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotAudio flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotAudio bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotAudio sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotAudio bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotAudio sendBackward() {
        super.sendBackward();
        return this;
    }

    /**
     * Applies an animation to this audio element.
     *
     * <p>The animation is defined by a {@link PotAnimation} object and will be played
     * during the slideshow. This method returns this audio element for chaining.</p>
     *
     * @param animation the animation to apply
     * @return this audio element for method chaining
     * @see PotElement#animate(PotAnimation)
     */
    @Override
    public PotAudio animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotAudio duplicate() {
        throw PotException.unsupportedOperation("duplicate for PotAudio");
    }

    @Override
    public PotAudio removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotAudio hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    /**
     * Applies a hyperlink to this audio element.
     *
     * <p>The hyperlink is defined by a {@link PotHyperlink} object. Clicking the audio
     * during the slideshow will trigger the hyperlink. This method returns this audio
     * element for chaining.</p>
     *
     * @param link the hyperlink to apply
     * @return this audio element for method chaining
     * @see PotElement#hyperlink(PotHyperlink)
     */
    @Override
    public PotAudio hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotAudio hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotAudio removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotAudio action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotAudio removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotAudio shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotAudio removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotAudio reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotAudio glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotAudio softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotAudio removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotAudio rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotAudio bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotAudio material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotAudio lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }

    @Override
    public PotAudio opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }
}