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

import java.util.Objects;

/**
 * Represents an executable action that can be triggered by user interaction on a presentation element.
 *
 * <p>An action defines a specific operation (like navigating slides, playing sounds, or running programs)
 * and can be configured to trigger either on a mouse click or on a mouse hover event. Instances are
 * immutable; methods like {@link #onClick()} and {@link #onHover()} return new instances with the
 * modified trigger setting.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * // Navigate to the next slide when a button is clicked.
 * button.action(PotAction.goToNextSlide().onClick());
 *
 * // Play a sound when the mouse hovers over a shape.
 * shape.action(PotAction.playSound("click.wav").onHover());
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotAction {

    private final PotActionType type;
    private final String target;
    private final boolean onHover; // true=trigger on hover, false=trigger on click

    /**
     * Constructs a new PotAction with the specified properties.
     *
     * @param type    the type of action to perform; must not be null
     * @param target  the optional target parameter for the action (e.g., slide index, file path)
     * @param onHover true if the action triggers on mouse hover, false if it triggers on mouse click
     */
    private PotAction(PotActionType type, String target, boolean onHover) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.target = target;
        this.onHover = onHover;
    }

    // ==================== Slide Navigation Actions ====================

    /**
     * Creates an action to navigate to a specific slide by its index.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @param slideIndex the zero-based index of the slide to navigate to
     * @return a new PotAction configured to go to the specified slide
     * @throws IllegalArgumentException if {@code slideIndex} is negative
     */
    public static PotAction goToSlide(int slideIndex) {
        if (slideIndex < 0) {
            throw new IllegalArgumentException("Slide index cannot be negative");
        }
        return new PotAction(PotActionType.GO_TO_SLIDE, String.valueOf(slideIndex), false);
    }

    /**
     * Creates an action to navigate to the first slide of the presentation.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to go to the first slide
     */
    public static PotAction goToFirstSlide() {
        return new PotAction(PotActionType.GO_TO_FIRST, null, false);
    }

    /**
     * Creates an action to navigate to the last slide of the presentation.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to go to the last slide
     */
    public static PotAction goToLastSlide() {
        return new PotAction(PotActionType.GO_TO_LAST, null, false);
    }

    /**
     * Creates an action to navigate to the next slide in sequence.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to go to the next slide
     */
    public static PotAction goToNextSlide() {
        return new PotAction(PotActionType.GO_TO_NEXT, null, false);
    }

    /**
     * Creates an action to navigate to the previous slide in sequence.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to go to the previous slide
     */
    public static PotAction goToPreviousSlide() {
        return new PotAction(PotActionType.GO_TO_PREVIOUS, null, false);
    }

    /**
     * Creates an action to end the presentation show.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to end the show
     */
    public static PotAction endShow() {
        return new PotAction(PotActionType.END_SHOW, null, false);
    }

    // ==================== Extended Navigation API ====================

    /**
     * Creates an action to open a hyperlink to the specified URL.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @param url the URL to open; must not be null
     * @return a new PotAction configured to open the hyperlink
     * @throws NullPointerException if {@code url} is null
     */
    public static PotAction hyperlink(String url) {
        return new PotAction(PotActionType.HYPERLINK,
            Objects.requireNonNull(url, "url cannot be null"), false);
    }

    /**
     * Creates an action to navigate to a specific slide object.
     *
     * <p>This is a convenience method that extracts the index from the provided slide.
     * The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @param slide the slide to navigate to; must not be null
     * @return a new PotAction configured to go to the specified slide
     * @throws IllegalArgumentException if {@code slide} is null
     * @see #goToSlide(int)
     */
    public static PotAction jumpToSlide(PotSlide slide) {
        if (slide == null) {
            throw new IllegalArgumentException("slide cannot be null");
        }
        return goToSlide(slide.getIndex());
    }

    // ==================== Program and Macro Actions ====================

    /**
     * Creates an action to run an external program.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @param programPath the file system path to the executable program; must not be null
     * @return a new PotAction configured to run the specified program
     * @throws NullPointerException if {@code programPath} is null
     */
    public static PotAction runProgram(String programPath) {
        return new PotAction(PotActionType.RUN_PROGRAM,
            Objects.requireNonNull(programPath, "programPath cannot be null"), false);
    }

    /**
     * Creates an action to run a named macro.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @param macroName the name of the macro to execute; must not be null
     * @return a new PotAction configured to run the specified macro
     * @throws NullPointerException if {@code macroName} is null
     */
    public static PotAction runMacro(String macroName) {
        return new PotAction(PotActionType.RUN_MACRO,
            Objects.requireNonNull(macroName, "macroName cannot be null"), false);
    }

    // ==================== Media Actions ====================

    /**
     * Creates an action to play an audio file.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @param soundFile the path to the audio file to play; must not be null
     * @return a new PotAction configured to play the specified sound
     * @throws NullPointerException if {@code soundFile} is null
     */
    public static PotAction playSound(String soundFile) {
        return new PotAction(PotActionType.PLAY_SOUND,
            Objects.requireNonNull(soundFile, "soundFile cannot be null"), false);
    }

    /**
     * Creates an action to stop any currently playing sound.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to stop sound playback
     */
    public static PotAction stopSound() {
        return new PotAction(PotActionType.STOP_SOUND, null, false);
    }

    // ==================== OLE Object Actions ====================

    /**
     * Creates an action to activate an embedded OLE object.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to activate an OLE object
     */
    public static PotAction oleActivate() {
        return new PotAction(PotActionType.OLE_ACTIVATE, null, false);
    }

    /**
     * Creates an action to edit an embedded OLE object.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to edit an OLE object
     */
    public static PotAction oleEdit() {
        return new PotAction(PotActionType.OLE_EDIT, null, false);
    }

    /**
     * Creates an action to open an embedded OLE object.
     *
     * <p>The resulting action is configured to trigger on a click by default.
     * Use {@link #onHover()} to change the trigger to hover.</p>
     *
     * @return a new PotAction configured to open an OLE object
     */
    public static PotAction oleOpen() {
        return new PotAction(PotActionType.OLE_OPEN, null, false);
    }

    // ==================== Trigger Configuration ====================

    /**
     * Returns an action configured to trigger on a mouse click.
     *
     * <p>If this action is already configured for click, the same instance is returned.
     * Otherwise, a new instance with the click trigger is created.</p>
     *
     * @return a PotAction configured to trigger on click (may be {@code this})
     */
    public PotAction onClick() {
        if (!onHover) {
            return this; // Already configured for click
        }
        return new PotAction(this.type, this.target, false);
    }

    /**
     * Returns an action configured to trigger on a mouse hover.
     *
     * <p>If this action is already configured for hover, the same instance is returned.
     * Otherwise, a new instance with the hover trigger is created.</p>
     *
     * @return a PotAction configured to trigger on hover (may be {@code this})
     */
    public PotAction onHover() {
        if (onHover) {
            return this; // Already configured for hover
        }
        return new PotAction(this.type, this.target, true);
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the type of this action.
     *
     * @return the action type; never null
     */
    public PotActionType getType() {
        return type;
    }

    /**
     * Returns the target parameter of this action.
     *
     * <p>The meaning of the target depends on the action type. For example, it may be
     * a slide index, a file path, or a URL. May be {@code null} if the action type
     * does not require a target.</p>
     *
     * @return the target string, or {@code null} if not applicable
     */
    public String getTarget() {
        return target;
    }

    /**
     * Indicates whether this action is configured to trigger on mouse hover.
     *
     * @return true if the action triggers on hover, false if it triggers on click
     */
    public boolean isOnHover() {
        return onHover;
    }

    /**
     * Indicates whether this action is configured to trigger on mouse click.
     *
     * @return true if the action triggers on click, false if it triggers on hover
     */
    public boolean isOnClick() {
        return !onHover;
    }

    @Override
    public String toString() {
        return "PotAction{" +
                "type=" + type +
                ", target='" + target + '\'' +
                ", trigger=" + (onHover ? "onHover" : "onClick") +
                '}';
    }
}