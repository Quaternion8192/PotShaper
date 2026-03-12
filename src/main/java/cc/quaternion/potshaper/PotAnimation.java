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
 * Represents a configurable animation for a presentation object.
 *
 * <p>This class is immutable and provides a fluent builder API for constructing animation instances.
 * It defines standard animation types (entrance, emphasis, exit) and effects (fade, fly, spin, etc.),
 * along with properties for timing, direction, and repetition. Predefined constants for common
 * animations are provided for convenience.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotAnimation {

    // ==================== Predefined Entrance Animations ====================

    /**
     * A predefined entrance animation that makes an object appear instantly.
     */
    public static final PotAnimation APPEAR = entrance(Effect.APPEAR);
    /**
     * A predefined entrance animation that fades an object in.
     */
    public static final PotAnimation FADE_IN = entrance(Effect.FADE);
    /**
     * A predefined entrance animation that flies an object in from the left.
     */
    public static final PotAnimation FLY_IN_FROM_LEFT = entrance(Effect.FLY).from(PotDirection.FROM_LEFT);
    /**
     * A predefined entrance animation that flies an object in from the right.
     */
    public static final PotAnimation FLY_IN_FROM_RIGHT = entrance(Effect.FLY).from(PotDirection.FROM_RIGHT);
    /**
     * A predefined entrance animation that flies an object in from the top.
     */
    public static final PotAnimation FLY_IN_FROM_TOP = entrance(Effect.FLY).from(PotDirection.FROM_TOP);
    /**
     * A predefined entrance animation that flies an object in from the bottom.
     */
    public static final PotAnimation FLY_IN_FROM_BOTTOM = entrance(Effect.FLY).from(PotDirection.FROM_BOTTOM);
    /**
     * A predefined entrance animation that zooms an object in.
     */
    public static final PotAnimation ZOOM_IN = entrance(Effect.ZOOM);
    /**
     * A predefined entrance animation that spins an object in.
     */
    public static final PotAnimation SPIN_IN = entrance(Effect.SPIN);
    /**
     * A predefined entrance animation that bounces an object in.
     */
    public static final PotAnimation BOUNCE_IN = entrance(Effect.BOUNCE);

    // ==================== Predefined Emphasis Animations ====================

    /**
     * A predefined emphasis animation that pulses an object.
     */
    public static final PotAnimation PULSE = emphasis(Effect.PULSE);
    /**
     * A predefined emphasis animation that spins an object.
     */
    public static final PotAnimation SPIN = emphasis(Effect.SPIN);
    /**
     * A predefined emphasis animation that grows an object.
     */
    public static final PotAnimation GROW = emphasis(Effect.GROW_SHRINK);
    /**
     * A predefined emphasis animation that teeters (rocks) an object.
     */
    public static final PotAnimation TEETER = emphasis(Effect.TEETER);
    /**
     * A predefined emphasis animation that blinks (flashes once) an object.
     */
    public static final PotAnimation BLINK = emphasis(Effect.FLASH_ONCE);

    // ==================== Predefined Exit Animations ====================

    /**
     * A predefined exit animation that makes an object disappear instantly.
     */
    public static final PotAnimation DISAPPEAR = exit(Effect.DISAPPEAR);
    /**
     * A predefined exit animation that fades an object out.
     */
    public static final PotAnimation FADE_OUT = exit(Effect.FADE);
    /**
     * A predefined exit animation that flies an object out.
     */
    public static final PotAnimation FLY_OUT = exit(Effect.FLY);
    /**
     * A predefined exit animation that zooms an object out.
     */
    public static final PotAnimation ZOOM_OUT = exit(Effect.ZOOM);

    // ==================== Enumerations ====================

    /**
     * The primary category of an animation.
     *
     * @since 1.0
     */
    public enum Type {
        /**
         * Animation that introduces an object onto the slide.
         */
        ENTRANCE,
        /**
         * Animation that highlights or modifies an already visible object.
         */
        EMPHASIS,
        /**
         * Animation that removes an object from the slide.
         */
        EXIT,
        /**
         * Animation that moves an object along a defined path.
         */
        MOTION_PATH
    }

    /**
     * The specific visual effect of an animation.
     *
     * @since 1.0
     */
    public enum Effect {
        // Entrance/Exit Effects
        APPEAR, DISAPPEAR, FADE, FLY, ZOOM, SPIN, BOUNCE,
        WIPE, SPLIT, WHEEL, RANDOM_BARS, GROW_AND_TURN,
        FLOAT, PINWHEEL, EXPAND, COMPRESS, RISE_UP,
        CENTER_REVOLVE, FLOAT_DOWN, FLOAT_UP, STRETCHY,

        // Emphasis Effects
        PULSE, GROW_SHRINK, TEETER, SPIN_EMPHASIS, DARKEN,
        LIGHTEN, TRANSPARENCY, OBJECT_COLOR, COMPLEMENTARY_COLOR,
        LINE_COLOR, FILL_COLOR, FLASH_ONCE, FLASH_BULB,
        BOLD_FLASH, BOLD_REVEAL, WAVE, SHIMMER,

        // Motion Path Effects
        PATH_LINE, PATH_ARC, PATH_TURN, PATH_CIRCLE,
        PATH_SQUARE, PATH_DIAMOND, PATH_HEXAGON, PATH_CUSTOM
    }

    /**
     * The event that starts the animation.
     *
     * @since 1.0
     */
    public enum Trigger {
        /**
         * Animation starts when the object is clicked.
         */
        ON_CLICK,
        /**
         * Animation starts simultaneously with the previous animation.
         */
        WITH_PREVIOUS,
        /**
         * Animation starts after the previous animation finishes.
         */
        AFTER_PREVIOUS
    }

    // ==================== Instance Fields ====================

    private final Type type;
    private final Effect effect;
    private final Trigger trigger;
    private final int duration;      // in milliseconds
    private final int delay;         // in milliseconds
    private final PotDirection direction;
    private final int repeatCount;   // 0 = no repeat, -1 = repeat indefinitely
    private final boolean autoReverse;

    // ==================== Constructors ====================

    /**
     * Constructs a new PotAnimation with the specified properties.
     *
     * <p>This constructor is private; use the static factory methods ({@link #entrance(Effect)},
     * {@link #emphasis(Effect)}, {@link #exit(Effect)}) and the fluent builder methods to create instances.</p>
     *
     * @param type         the category of the animation
     * @param effect       the visual effect
     * @param trigger      the start trigger; defaults to {@link Trigger#ON_CLICK} if null
     * @param duration     the animation duration in milliseconds; will be clamped to a minimum of 0
     * @param delay        the delay before the animation starts in milliseconds; will be clamped to a minimum of 0
     * @param direction    the direction for directional effects (e.g., fly in from left), may be null
     * @param repeatCount  the number of times to repeat the animation (0 for none, -1 for infinite)
     * @param autoReverse  whether the animation should automatically reverse on every other cycle
     */
    private PotAnimation(Type type, Effect effect, Trigger trigger, int duration,
                         int delay, PotDirection direction, int repeatCount, boolean autoReverse) {
        this.type = type;
        this.effect = effect;
        this.trigger = trigger != null ? trigger : Trigger.ON_CLICK;
        this.duration = Math.max(0, duration);
        this.delay = Math.max(0, delay);
        this.direction = direction;
        this.repeatCount = repeatCount;
        this.autoReverse = autoReverse;
    }

    // ==================== Static Factory Methods (Core) ====================

    /**
     * Creates a new entrance animation with the specified effect.
     *
     * <p>The animation will have default properties: triggered on click, 500ms duration,
     * no delay, no repeat, and no auto-reverse.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Create a fade-in entrance animation
     * PotAnimation myFadeIn = PotAnimation.entrance(Effect.FADE);
     * }</pre>
     *
     * @param effect the visual effect for the entrance animation
     * @return a new PotAnimation configured as an entrance
     * @see #emphasis(Effect)
     * @see #exit(Effect)
     */
    public static PotAnimation entrance(Effect effect) {
        return new PotAnimation(Type.ENTRANCE, effect, Trigger.ON_CLICK, 500, 0, null, 0, false);
    }

    /**
     * Creates a new emphasis animation with the specified effect.
     *
     * <p>The animation will have default properties: triggered on click, 500ms duration,
     * no delay, no repeat, and no auto-reverse.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Create a pulse emphasis animation
     * PotAnimation myPulse = PotAnimation.emphasis(Effect.PULSE);
     * }</pre>
     *
     * @param effect the visual effect for the emphasis animation
     * @return a new PotAnimation configured as an emphasis
     * @see #entrance(Effect)
     * @see #exit(Effect)
     */
    public static PotAnimation emphasis(Effect effect) {
        return new PotAnimation(Type.EMPHASIS, effect, Trigger.ON_CLICK, 500, 0, null, 0, false);
    }

    /**
     * Creates a new exit animation with the specified effect.
     *
     * <p>The animation will have default properties: triggered on click, 500ms duration,
     * no delay, no repeat, and no auto-reverse.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Create a fade-out exit animation
     * PotAnimation myFadeOut = PotAnimation.exit(Effect.FADE);
     * }</pre>
     *
     * @param effect the visual effect for the exit animation
     * @return a new PotAnimation configured as an exit
     * @see #entrance(Effect)
     * @see #emphasis(Effect)
     */
    public static PotAnimation exit(Effect effect) {
        return new PotAnimation(Type.EXIT, effect, Trigger.ON_CLICK, 500, 0, null, 0, false);
    }

    // ==================== Convenience Static Methods ====================

    /**
     * Returns a predefined animation that makes an object appear instantly.
     *
     * @return the {@link #APPEAR} animation constant
     * @see #APPEAR
     */
    public static PotAnimation appear() {
        return APPEAR;
    }

    /**
     * Returns a predefined animation that fades an object in.
     *
     * @return the {@link #FADE_IN} animation constant
     * @see #FADE_IN
     */
    public static PotAnimation fadeIn() {
        return FADE_IN;
    }

    /**
     * Returns a predefined animation that fades an object out.
     *
     * @return the {@link #FADE_OUT} animation constant
     * @see #FADE_OUT
     */
    public static PotAnimation fadeOut() {
        return FADE_OUT;
    }

    /**
     * Creates a fly-in entrance animation from the specified direction.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Create a fly-in animation from the top
     * PotAnimation flyDown = PotAnimation.flyIn(PotDirection.FROM_TOP);
     * }</pre>
     *
     * @param direction the direction from which the object flies in
     * @return a new fly-in entrance PotAnimation
     * @see #flyIn()
     */
    public static PotAnimation flyIn(PotDirection direction) {
        return entrance(Effect.FLY).from(direction);
    }

    /**
     * Creates a fly-in entrance animation from the left (default direction).
     *
     * <p>This is a convenience method equivalent to {@code flyIn(PotDirection.FROM_LEFT)}.</p>
     *
     * @return a new fly-in entrance PotAnimation from the left
     * @see #flyIn(PotDirection)
     */
    public static PotAnimation flyIn() {
        return flyIn(PotDirection.FROM_LEFT);
    }

    /**
     * Creates a fly-out exit animation to the specified direction.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Create a fly-out animation to the right
     * PotAnimation flyRight = PotAnimation.flyOut(PotDirection.FROM_RIGHT);
     * }</pre>
     *
     * @param direction the direction to which the object flies out
     * @return a new fly-out exit PotAnimation
     */
    public static PotAnimation flyOut(PotDirection direction) {
        return exit(Effect.FLY).to(direction);
    }

    /**
     * Returns a predefined animation that zooms an object in.
     *
     * @return the {@link #ZOOM_IN} animation constant
     * @see #ZOOM_IN
     */
    public static PotAnimation zoomIn() {
        return ZOOM_IN;
    }

    /**
     * Returns a predefined animation that zooms an object out.
     *
     * @return the {@link #ZOOM_OUT} animation constant
     * @see #ZOOM_OUT
     */
    public static PotAnimation zoomOut() {
        return ZOOM_OUT;
    }

    /**
     * Creates a wipe-in entrance animation from the specified direction.
     *
     * @param direction the direction from which the wipe effect originates
     * @return a new wipe-in entrance PotAnimation
     */
    public static PotAnimation wipeIn(PotDirection direction) {
        return entrance(Effect.WIPE).from(direction);
    }

    /**
     * Returns a predefined animation that bounces an object in.
     *
     * @return the {@link #BOUNCE_IN} animation constant
     * @see #BOUNCE_IN
     */
    public static PotAnimation bounceIn() {
        return BOUNCE_IN;
    }

    /**
     * Returns a predefined animation that spins an object in.
     *
     * @return the {@link #SPIN_IN} animation constant
     * @see #SPIN_IN
     */
    public static PotAnimation spinIn() {
        return SPIN_IN;
    }

    /**
     * Returns a predefined emphasis animation that pulses an object.
     *
     * @return the {@link #PULSE} animation constant
     * @see #PULSE
     */
    public static PotAnimation pulse() {
        return PULSE;
    }

    /**
     * Returns a predefined emphasis animation that spins an object.
     *
     * @return the {@link #SPIN} animation constant
     * @see #SPIN
     */
    public static PotAnimation spin() {
        return SPIN;
    }

    /**
     * Returns a predefined emphasis animation that grows an object.
     *
     * @return the {@link #GROW} animation constant
     * @see #GROW
     */
    public static PotAnimation grow() {
        return GROW;
    }

    /**
     * Returns a predefined emphasis animation that teeters (rocks) an object.
     *
     * @return the {@link #TEETER} animation constant
     * @see #TEETER
     */
    public static PotAnimation teeter() {
        return TEETER;
    }

    /**
     * Returns a predefined emphasis animation that blinks (flashes once) an object.
     *
     * @return the {@link #BLINK} animation constant
     * @see #BLINK
     */
    public static PotAnimation blink() {
        return BLINK;
    }

    /**
     * Creates an entrance animation with a zoom effect.
     *
     * <p>This method is an alias for {@link #entrance(Effect)} with {@link Effect#ZOOM}.
     * It is provided as a convenience for API consistency.</p>
     *
     * @return a new zoom-in entrance PotAnimation
     * @see #entrance(Effect)
     * @see #ZOOM_IN
     */
    public static PotAnimation scale() {
        return entrance(Effect.ZOOM);
    }

    // ==================== Fluent Builder Methods ====================

    /**
     * Returns a copy of this animation with the specified duration in milliseconds.
     *
     * <p>If the provided value is negative, it will be treated as 0.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Create a 2-second long fade-in
     * PotAnimation longFadeIn = PotAnimation.fadeIn().duration(2000);
     * }</pre>
     *
     * @param milliseconds the new duration in milliseconds
     * @return a new PotAnimation instance with the updated duration
     * @see #duration(double)
     */
    public PotAnimation duration(int milliseconds) {
        return new PotAnimation(type, effect, trigger, milliseconds, delay, direction, repeatCount, autoReverse);
    }

    /**
     * Creates a new animation with the specified duration in seconds.
     *
     * <p>This is a convenience method that converts the duration from seconds to milliseconds.
     * Returns a new {@code PotAnimation} instance that is identical to this one except for its duration.
     * The duration must be non-negative; a negative value will be treated as zero.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.spin().duration(2.5); // 2.5 second spin
     * }</pre>
     *
     * @param seconds the new duration for the animation in seconds
     * @return a new {@code PotAnimation} with the specified duration
     */
    public PotAnimation duration(double seconds) {
        return duration((int) (seconds * 1000));
    }

    /**
     * Creates a new animation with the specified delay before starting.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except for its start delay.
     * The delay must be non-negative; a negative value will be treated as zero.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.flyIn().delay(500); // Wait 500ms before flying in
     * }</pre>
     *
     * @param milliseconds the delay before the animation starts, in milliseconds
     * @return a new {@code PotAnimation} with the specified delay
     */
    public PotAnimation delay(int milliseconds) {
        return new PotAnimation(type, effect, trigger, duration, milliseconds, direction, repeatCount, autoReverse);
    }

    /**
     * Creates a new animation configured to be triggered by a click.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except its trigger is set to {@link Trigger#ON_CLICK}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.pulse().onClick(); // Pulse when clicked
     * }</pre>
     *
     * @return a new {@code PotAnimation} configured to trigger on click
     * @see Trigger#ON_CLICK
     */
    public PotAnimation onClick() {
        return new PotAnimation(type, effect, Trigger.ON_CLICK, duration, delay, direction, repeatCount, autoReverse);
    }

    /**
     * Creates a new animation configured to start simultaneously with the previous animation.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except its trigger is set to {@link Trigger#WITH_PREVIOUS}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.fadeIn().withPrevious(); // Start fading in with the prior animation
     * }</pre>
     *
     * @return a new {@code PotAnimation} configured to trigger with the previous animation
     * @see Trigger#WITH_PREVIOUS
     */
    public PotAnimation withPrevious() {
        return new PotAnimation(type, effect, Trigger.WITH_PREVIOUS, duration, delay, direction, repeatCount, autoReverse);
    }

    /**
     * Creates a new animation configured to start after the previous animation finishes.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except its trigger is set to {@link Trigger#AFTER_PREVIOUS}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.zoomOut().afterPrevious(); // Zoom out after the prior animation ends
     * }</pre>
     *
     * @return a new {@code PotAnimation} configured to trigger after the previous animation
     * @see Trigger#AFTER_PREVIOUS
     */
    public PotAnimation afterPrevious() {
        return new PotAnimation(type, effect, Trigger.AFTER_PREVIOUS, duration, delay, direction, repeatCount, autoReverse);
    }

    /**
     * Creates a new animation with the specified origin direction.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except for its direction property.
     * This is typically used with entrance effects like {@link Effect#FLY} or {@link Effect#WIPE} to specify where the motion originates.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.flyIn().from(PotDirection.FROM_TOP); // Fly in from the top
     * }</pre>
     *
     * @param direction the origin direction for the animation
     * @return a new {@code PotAnimation} with the specified origin direction
     * @see PotDirection
     */
    public PotAnimation from(PotDirection direction) {
        return new PotAnimation(type, effect, trigger, duration, delay, direction, repeatCount, autoReverse);
    }

    /**
     * Creates a new animation with the specified direction.
     *
     * <p>This is an alias for {@link #from(PotDirection)}. Returns a new {@code PotAnimation} instance that is identical to this one except for its direction property.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.wipeIn().direction(PotDirection.FROM_LEFT);
     * }</pre>
     *
     * @param direction the direction for the animation
     * @return a new {@code PotAnimation} with the specified direction
     * @see #from(PotDirection)
     */
    public PotAnimation direction(PotDirection direction) {
        return from(direction);
    }

    /**
     * Creates a new animation with the specified target direction.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except for its direction property.
     * This is typically used with exit effects like {@link Effect#FLY} to specify where the motion ends.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.flyOut().to(PotDirection.FROM_RIGHT); // Fly out to the right
     * }</pre>
     *
     * @param direction the target direction for the animation
     * @return a new {@code PotAnimation} with the specified target direction
     * @see PotDirection
     */
    public PotAnimation to(PotDirection direction) {
        return new PotAnimation(type, effect, trigger, duration, delay, direction, repeatCount, autoReverse);
    }

    /**
     * Creates a new animation with the specified repeat count.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except for its repeat count.
     * A count of zero means the animation plays once (no repeat). A count of -1 indicates infinite repetition.
     * Positive values specify the exact number of additional repetitions.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.pulse().repeat(3); // Pulse four times total
     * }</pre>
     *
     * @param count the number of times to repeat the animation (0 for once, -1 for infinite)
     * @return a new {@code PotAnimation} with the specified repeat count
     */
    public PotAnimation repeat(int count) {
        return new PotAnimation(type, effect, trigger, duration, delay, direction, count, autoReverse);
    }

    /**
     * Creates a new animation configured to repeat indefinitely.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except its repeat count is set to -1.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.spin().repeatForever(); // Spin continuously
     * }</pre>
     *
     * @return a new {@code PotAnimation} configured to repeat forever
     * @see #repeat(int)
     */
    public PotAnimation repeatForever() {
        return repeat(-1);
    }

    /**
     * Creates a new animation with the specified auto-reverse behavior.
     *
     * <p>Returns a new {@code PotAnimation} instance that is identical to this one except for its auto-reverse flag.
     * When auto-reverse is enabled, the animation will play forward and then in reverse for each cycle.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation animation = PotAnimation.grow().autoReverse(true); // Grow then shrink on each repeat
     * }</pre>
     *
     * @param autoReverse {@code true} to enable auto-reverse playback, {@code false} otherwise
     * @return a new {@code PotAnimation} with the specified auto-reverse behavior
     */
    public PotAnimation autoReverse(boolean autoReverse) {
        return new PotAnimation(type, effect, trigger, duration, delay, direction, repeatCount, autoReverse);
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the type of this animation.
     *
     * @return the animation {@link Type}
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the visual effect of this animation.
     *
     * @return the animation {@link Effect}
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * Returns the trigger condition for this animation.
     *
     * @return the animation {@link Trigger}
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Returns the duration of this animation in milliseconds.
     *
     * @return the animation duration in milliseconds
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns the delay before this animation starts, in milliseconds.
     *
     * @return the start delay in milliseconds
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Returns the direction associated with this animation.
     *
     * @return the animation direction, or {@code null} if not applicable
     */
    public PotDirection getDirection() {
        return direction;
    }

    /**
     * Returns the repeat count for this animation.
     *
     * @return the repeat count (0 for once, -1 for infinite, positive for specific count)
     */
    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * Returns whether this animation uses auto-reverse playback.
     *
     * @return {@code true} if auto-reverse is enabled, {@code false} otherwise
     */
    public boolean isAutoReverse() {
        return autoReverse;
    }

    /**
     * Checks if this animation is an entrance type.
     *
     * @return {@code true} if the animation type is {@link Type#ENTRANCE}, {@code false} otherwise
     */
    public boolean isEntrance() {
        return type == Type.ENTRANCE;
    }

    /**
     * Checks if this animation is an emphasis type.
     *
     * @return {@code true} if the animation type is {@link Type#EMPHASIS}, {@code false} otherwise
     */
    public boolean isEmphasis() {
        return type == Type.EMPHASIS;
    }

    /**
     * Checks if this animation is an exit type.
     *
     * @return {@code true} if the animation type is {@link Type#EXIT}, {@code false} otherwise
     */
    public boolean isExit() {
        return type == Type.EXIT;
    }

    // ==================== Object Methods ====================

    /**
     * Compares this animation to the specified object for equality.
     *
     * <p>Two {@code PotAnimation} instances are considered equal if all their configurable properties
     * (type, effect, trigger, duration, delay, direction, repeat count, and auto-reverse) are equal.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotAnimation that = (PotAnimation) o;
        return duration == that.duration &&
               delay == that.delay &&
               repeatCount == that.repeatCount &&
               autoReverse == that.autoReverse &&
               type == that.type &&
               effect == that.effect &&
               trigger == that.trigger &&
               direction == that.direction;
    }

    /**
     * Returns a hash code value for this animation.
     *
     * @return a hash code based on the animation's properties
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, effect, trigger, duration, delay, direction, repeatCount, autoReverse);
    }

    /**
     * Returns a string representation of this animation.
     *
     * <p>The string includes the animation's type, effect, trigger, duration, and any non-default properties
     * such as delay, direction, and repeat count.</p>
     *
     * @return a descriptive string representation of this animation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PotAnimation{");
        sb.append("type=").append(type);
        sb.append(", effect=").append(effect);
        sb.append(", trigger=").append(trigger);
        sb.append(", duration=").append(duration).append("ms");
        if (delay > 0) sb.append(", delay=").append(delay).append("ms");
        if (direction != null) sb.append(", direction=").append(direction);
        if (repeatCount != 0) sb.append(", repeat=").append(repeatCount == -1 ? "forever" : repeatCount);
        sb.append('}');
        return sb.toString();
    }
}
