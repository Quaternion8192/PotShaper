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

/**
 * A mutable builder for configuring playback and presentation options of a media element.
 *
 * <p>This class provides a fluent API to set various media properties such as autoplay, looping,
 * volume, fade effects, trimming, poster frames, and bookmarks. Each configuration method returns
 * the instance itself, allowing for method chaining. Values are validated using {@link ValidationUtils}
 * where applicable.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * PotMediaOptions options = new PotMediaOptions()
 *     .autoPlay(true)
 *     .volume(0.8)
 *     .fadeIn(2.0)
 *     .fadeOut(1.5)
 *     .trim(5.0, 30.0)
 *     .posterFrame(imageBytes, "image/jpeg")
 *     .bookmark("Chapter1");
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotMediaOptions {

    // ==================== Playback Options ====================

    private boolean autoPlay = false;
    private boolean loop = false;
    private double volume = 1.0;
    private boolean hideIcon = false;
    private boolean fullScreen = false;
    private double fadeInSeconds = 0;
    private double fadeOutSeconds = 0;
    private double trimStartSeconds = 0;
    private double trimEndSeconds = 0;

    // ==================== Poster Frame ====================

    private byte[] posterFrameData;
    private String posterFrameType;

    // ==================== Bookmark ====================

    private String bookmarkName;

    // ==================== Constructors ====================

    /**
     * Constructs a new {@code PotMediaOptions} instance with default values.
     *
     * <p>Default values are: autoplay {@code false}, loop {@code false}, volume {@code 1.0},
     * hide icon {@code false}, full screen {@code false}, all fade and trim durations {@code 0},
     * and no poster frame or bookmark set.</p>
     */
    public PotMediaOptions() {
    }

    // ==================== Playback Configuration Methods ====================

    /**
     * Enables or disables automatic playback of the media.
     *
     * <p>When set to {@code true}, the media will begin playing as soon as it is loaded and ready,
     * without requiring explicit user interaction.</p>
     *
     * @param autoPlay {@code true} to enable autoplay, {@code false} to disable it
     * @return this {@code PotMediaOptions} instance for method chaining
     */
    public PotMediaOptions autoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
        return this;
    }

    /**
     * Enables or disables looping of the media.
     *
     * <p>When set to {@code true}, the media will restart from the beginning upon reaching its end,
     * creating a continuous playback loop.</p>
     *
     * @param loop {@code true} to enable looping, {@code false} to disable it
     * @return this {@code PotMediaOptions} instance for method chaining
     */
    public PotMediaOptions loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    /**
     * Sets the playback volume level.
     *
     * <p>The volume is a value between 0.0 (silent) and 1.0 (maximum volume). The value is validated
     * to be within this range using {@link ValidationUtils#percentage01(double, String)}.</p>
     *
     * @param volume the volume level, between 0.0 and 1.0 inclusive
     * @return this {@code PotMediaOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code volume} is not between 0.0 and 1.0
     * @see ValidationUtils#percentage01(double, String)
     */
    public PotMediaOptions volume(double volume) {
        this.volume = ValidationUtils.percentage01(volume, "volume");
        return this;
    }

    /**
     * Controls the visibility of the media control icon.
     *
     * <p>When set to {@code true}, the default media control icon (such as a play button overlay)
     * will be hidden from view.</p>
     *
     * @param hideIcon {@code true} to hide the icon, {@code false} to show it
     * @return this {@code PotMediaOptions} instance for method chaining
     */
    public PotMediaOptions hideIcon(boolean hideIcon) {
        this.hideIcon = hideIcon;
        return this;
    }

    /**
     * Enables or disables full-screen presentation of the media.
     *
     * <p>When set to {@code true}, the media is intended to be presented in full-screen mode,
     * typically occupying the entire display.</p>
     *
     * @param fullScreen {@code true} to enable full-screen mode, {@code false} to disable it
     * @return this {@code PotMediaOptions} instance for method chaining
     */
    public PotMediaOptions fullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    // ==================== Fade Effect Methods ====================

    /**
     * Sets the duration of the fade-in effect at the beginning of playback.
     *
     * <p>The fade-in effect gradually increases the audio volume from silence to the set volume level
     * over the specified duration. The value is validated to be non-negative using
     * {@link ValidationUtils#nonNegative(double, String)}.</p>
     *
     * @param seconds the fade-in duration in seconds; must be {@code >= 0}
     * @return this {@code PotMediaOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code seconds} is negative
     * @see ValidationUtils#nonNegative(double, String)
     */
    public PotMediaOptions fadeIn(double seconds) {
        this.fadeInSeconds = ValidationUtils.nonNegative(seconds, "fadeInSeconds");
        return this;
    }

    /**
     * Sets the duration of the fade-out effect at the end of playback.
     *
     * <p>The fade-out effect gradually decreases the audio volume from the set volume level to silence
     * over the specified duration. The value is validated to be non-negative using
     * {@link ValidationUtils#nonNegative(double, String)}.</p>
     *
     * @param seconds the fade-out duration in seconds; must be {@code >= 0}
     * @return this {@code PotMediaOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code seconds} is negative
     * @see ValidationUtils#nonNegative(double, String)
     */
    public PotMediaOptions fadeOut(double seconds) {
        this.fadeOutSeconds = ValidationUtils.nonNegative(seconds, "fadeOutSeconds");
        return this;
    }

    // ==================== Trimming Method ====================

    /**
     * Defines a segment of the media to be played by specifying start and end trim points.
     *
     * <p>Only the portion of the media between {@code startSeconds} (inclusive) and {@code endSeconds}
     * (exclusive) will be played. Both values are validated to be non-negative, and {@code endSeconds}
     * must be greater than or equal to {@code startSeconds}.</p>
     *
     * @param startSeconds the start time of the trim segment in seconds; must be {@code >= 0}
     * @param endSeconds the end time of the trim segment in seconds; must be {@code >= startSeconds}
     * @return this {@code PotMediaOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code startSeconds} or {@code endSeconds} is negative,
     *         or if {@code endSeconds < startSeconds}
     * @see ValidationUtils#nonNegative(double, String)
     * @see PotException#invalidParameter(String, String)
     */
    public PotMediaOptions trim(double startSeconds, double endSeconds) {
        ValidationUtils.nonNegative(startSeconds, "startSeconds");
        ValidationUtils.nonNegative(endSeconds, "endSeconds");
        if (endSeconds < startSeconds) {
            throw PotException.invalidParameter("trim", "endSeconds must be >= startSeconds");
        }
        this.trimStartSeconds = startSeconds;
        this.trimEndSeconds = endSeconds;
        return this;
    }

    // ==================== Poster Frame Method ====================

    /**
     * Sets a custom poster frame (thumbnail) image for the media.
     *
     * <p>The poster frame is a static image displayed before the media begins playback or while it is loading.
     * The image data and its MIME content type must be provided. Both parameters are validated for
     * non-emptiness using {@link ValidationUtils}.</p>
     *
     * @param imageData the byte array containing the image data; must not be null or empty
     * @param contentType the MIME type of the image (e.g., "image/jpeg", "image/png"); must not be blank
     * @return this {@code PotMediaOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code imageData} is null or empty, or if {@code contentType} is blank
     * @see ValidationUtils#notEmpty(byte[], String)
     * @see ValidationUtils#notBlank(String, String)
     */
    public PotMediaOptions posterFrame(byte[] imageData, String contentType) {
        this.posterFrameData = ValidationUtils.notEmpty(imageData, "imageData");
        this.posterFrameType = ValidationUtils.notBlank(contentType, "contentType");
        return this;
    }

    // ==================== Bookmark Method ====================

    /**
     * Associates a bookmark name with the media.
     *
     * <p>The bookmark can be used to identify or reference this specific media configuration within
     * the application. The name is validated to be non-blank using {@link ValidationUtils#notBlank(String, String)}.</p>
     *
     * @param bookmarkName the name of the bookmark; must not be blank
     * @return this {@code PotMediaOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code bookmarkName} is blank
     * @see ValidationUtils#notBlank(String, String)
     */
    public PotMediaOptions bookmark(String bookmarkName) {
        this.bookmarkName = ValidationUtils.notBlank(bookmarkName, "bookmarkName");
        return this;
    }

    // ==================== Getter Methods ====================

    /**
     * Returns whether autoplay is enabled.
     *
     * @return {@code true} if autoplay is enabled, {@code false} otherwise
     */
    public boolean isAutoPlay() {
        return autoPlay;
    }

    /**
     * Returns whether looping is enabled.
     *
     * @return {@code true} if looping is enabled, {@code false} otherwise
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * Returns the current volume level.
     *
     * @return the volume level, between 0.0 and 1.0 inclusive
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Returns whether the media control icon is hidden.
     *
     * @return {@code true} if the icon is hidden, {@code false} otherwise
     */
    public boolean isIconHidden() {
        return hideIcon;
    }

    /**
     * Returns whether full-screen mode is enabled.
     *
     * @return {@code true} if full-screen mode is enabled, {@code false} otherwise
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * Returns the fade-in duration.
     *
     * @return the fade-in duration in seconds
     */
    public double getFadeInSeconds() {
        return fadeInSeconds;
    }

    /**
     * Returns the fade-out duration.
     *
     * @return the fade-out duration in seconds
     */
    public double getFadeOutSeconds() {
        return fadeOutSeconds;
    }

    /**
     * Returns the start time of the trim segment.
     *
     * @return the trim start time in seconds
     */
    public double getTrimStartSeconds() {
        return trimStartSeconds;
    }

    /**
     * Returns the end time of the trim segment.
     *
     * @return the trim end time in seconds
     */
    public double getTrimEndSeconds() {
        return trimEndSeconds;
    }

    /**
     * Returns the poster frame image data.
     *
     * @return the byte array containing the poster frame image data, or {@code null} if not set
     */
    public byte[] getPosterFrameData() {
        return posterFrameData;
    }

    /**
     * Returns the MIME content type of the poster frame.
     *
     * @return the content type of the poster frame (e.g., "image/jpeg"), or {@code null} if not set
     */
    public String getPosterFrameType() {
        return posterFrameType;
    }

    /**
     * Returns the associated bookmark name.
     *
     * @return the bookmark name, or {@code null} if not set
     */
    public String getBookmarkName() {
        return bookmarkName;
    }

    /**
     * Checks whether a poster frame has been set.
     *
     * <p>A poster frame is considered set if both {@code posterFrameData} and {@code posterFrameType}
     * are non-null.</p>
     *
     * @return {@code true} if a poster frame is set, {@code false} otherwise
     */
    public boolean hasPosterFrame() {
        return posterFrameData != null && posterFrameType != null;
    }

    /**
     * Checks whether a bookmark has been set.
     *
     * @return {@code true} if a bookmark name is set (non-null), {@code false} otherwise
     */
    public boolean hasBookmark() {
        return bookmarkName != null;
    }

    // ==================== Object Methods ====================

    /**
     * Returns a string representation of this {@code PotMediaOptions} instance.
     *
     * <p>The string includes the values of all playback-related fields (autoplay, loop, volume, etc.),
     * but excludes poster frame data and bookmark name for brevity.</p>
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "PotMediaOptions{" +
                "autoPlay=" + autoPlay +
                ", loop=" + loop +
                ", volume=" + volume +
                ", hideIcon=" + hideIcon +
                ", fullScreen=" + fullScreen +
                ", fadeIn=" + fadeInSeconds +
                ", fadeOut=" + fadeOutSeconds +
                ", trimStart=" + trimStartSeconds +
                ", trimEnd=" + trimEndSeconds +
                '}';
    }
}