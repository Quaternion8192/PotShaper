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
 * Represents a visual transition effect between presentation slides.
 *
 * <p>This immutable class defines the type, direction, timing, and advancement behavior
 * for a slide transition. It provides factory methods for common transition types and
 * supports fluent configuration through {@code with*} methods that return new instances.
 * All predefined constants are immutable and reusable.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * // Create a fade transition with custom duration
 * PotTransition fadeIn = PotTransition.fade().withDuration(1000);
 *
 * // Create a push transition from the right that auto-advances after 3 seconds
 * PotTransition pushRight = PotTransition.push(PotDirection.FROM_RIGHT)
 *                                        .withAdvanceAfter(3000);
 *
 * // Use a predefined constant
 * PotTransition dissolve = PotTransition.DISSOLVE;
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotTransition {

    // ==================== Predefined Constants ====================

    /** A transition representing no visual effect. */
    public static final PotTransition NONE = new PotTransition(Type.NONE, null, 0, false, 0);

    /** A standard cross-fade transition lasting 700 milliseconds. */
    public static final PotTransition FADE = new PotTransition(Type.FADE, null, 700, true, 0);

    /** A push transition where the new slide enters from the right. */
    public static final PotTransition PUSH_FROM_RIGHT = push(PotDirection.FROM_RIGHT);

    /** A push transition where the new slide enters from the bottom. */
    public static final PotTransition PUSH_FROM_BOTTOM = push(PotDirection.FROM_BOTTOM);

    /** A wipe transition where the new slide reveals from the left. */
    public static final PotTransition WIPE_FROM_LEFT = wipe(PotDirection.FROM_LEFT);

    /** A wipe transition where the new slide reveals from the top. */
    public static final PotTransition WIPE_FROM_TOP = wipe(PotDirection.FROM_TOP);

    /** A dissolve (dither) transition lasting 700 milliseconds. */
    public static final PotTransition DISSOLVE = new PotTransition(Type.DISSOLVE, null, 700, true, 0);

    /** A blinds transition with horizontal strips. */
    public static final PotTransition BLINDS_HORIZONTAL = new PotTransition(Type.BLINDS, PotDirection.HORIZONTAL, 700, true, 0);

    /** A blinds transition with vertical strips. */
    public static final PotTransition BLINDS_VERTICAL = new PotTransition(Type.BLINDS, PotDirection.VERTICAL, 700, true, 0);

    /** A checkerboard pattern transition. */
    public static final PotTransition CHECKERBOARD = new PotTransition(Type.CHECKERBOARD, null, 700, true, 0);

    /** A clockwise clock wipe transition. */
    public static final PotTransition CLOCK = new PotTransition(Type.CLOCK, PotDirection.CLOCKWISE, 700, true, 0);

    /** A zoom-in transition. */
    public static final PotTransition ZOOM = new PotTransition(Type.ZOOM, PotDirection.IN, 700, true, 0);

    // ==================== Transition Type Enumeration ====================

    /**
     * Enumerates all supported visual transition types.
     *
     * @since 1.0
     */
    public enum Type {
        /** No visual effect. */
        NONE,
        /** Cross-fade between slides. */
        FADE,
        /** New slide pushes the old slide offscreen. */
        PUSH,
        /** New slide wipes over the old slide. */
        WIPE,
        /** Old slide splits apart to reveal the new slide. */
        SPLIT,
        /** New slide is revealed behind the old slide. */
        REVEAL,
        /** New slide covers the old slide. */
        COVER,
        /** Old slide dissolves into the new slide. */
        DISSOLVE,
        /** Zoom in or out between slides. */
        ZOOM,
        /** Horizontal or vertical blinds effect. */
        BLINDS,
        /** Checkerboard pattern transition. */
        CHECKERBOARD,
        /** Clockwise or counter-clockwise radial wipe. */
        CLOCK,
        /** Comb-like stripping effect. */
        COMB,
        /** Random bars sweeping across. */
        RANDOM_BARS,
        /** Instant cut with no animation. */
        CUT,
        /** 3D fly-through effect. */
        FLY_THROUGH,
        /** Gallery-style horizontal slide. */
        GALLERY,
        /** 3D cube rotation. */
        CUBE,
        /** Doors opening or closing. */
        DOORS,
        /** Box in or out effect. */
        BOX,
        /** Vortex swirl transition. */
        VORTEX,
        /** Shredding paper effect. */
        SHRED,
        /** 3D flip effect. */
        FLIP,
        /** 2D rotation. */
        ROTATE,
        /** Orbital rotation. */
        ORBIT,
        /** Bright flash effect. */
        FLASH,
        /** Ripple distortion. */
        RIPPLE,
        /** Honeycomb pattern. */
        HONEYCOMB,
        /** Glitter sparkle effect. */
        GLITTER,
        /** Curtains opening or closing. */
        CURTAINS,
        /** Wind blowing effect. */
        WIND,
        /** Airplane flying across. */
        AIRPLANE,
        /** Origami folding. */
        ORIGAMI,
        /** Fracture and reassemble. */
        FRACTURE,
        /** Randomly selected transition type. */
        RANDOM
    }

    // ==================== Instance Fields ====================

    private final Type type;
    private final PotDirection direction;
    private final int duration;         // Total animation time in milliseconds
    private final boolean advanceOnClick;
    private final int advanceAfterTime; // Auto-advance delay in milliseconds, 0 means disabled

    // ==================== Constructors ====================

    /**
     * Constructs a fully specified transition instance.
     *
     * <p>This private constructor is used internally by factory and builder methods.
     * The {@code type} is normalized to {@link Type#NONE} if {@code null}. Durations
     * and advance times are clamped to be non-negative.</p>
     *
     * @param type             The visual effect type, or {@code null} for {@link Type#NONE}.
     * @param direction        The directional modifier for the effect, may be {@code null}.
     * @param duration         The total animation duration in milliseconds.
     * @param advanceOnClick   Whether a mouse click can advance the slide during the transition.
     * @param advanceAfterTime The delay in milliseconds before auto-advancing; 0 disables auto-advance.
     */
    private PotTransition(Type type, PotDirection direction, int duration,
                          boolean advanceOnClick, int advanceAfterTime) {
        this.type = type != null ? type : Type.NONE;
        this.direction = direction;
        this.duration = Math.max(0, duration);
        this.advanceOnClick = advanceOnClick;
        this.advanceAfterTime = Math.max(0, advanceAfterTime);
    }

    // ==================== Factory Methods ====================

    /**
     * Returns a transition representing no visual effect.
     *
     * <p>This is equivalent to the constant {@link #NONE}.</p>
     *
     * @return A {@code PotTransition} instance of type {@link Type#NONE}.
     * @see #NONE
     */
    public static PotTransition none() {
        return NONE;
    }

    /**
     * Returns a standard cross-fade transition.
     *
     * <p>This is equivalent to the constant {@link #FADE}.</p>
     *
     * @return A {@code PotTransition} instance of type {@link Type#FADE} with a 700ms duration.
     * @see #FADE
     */
    public static PotTransition fade() {
        return FADE;
    }

    /**
     * Creates a push transition in the specified direction.
     *
     * <p>The new slide enters from the given direction, pushing the old slide off the opposite edge.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition pushLeft = PotTransition.push(PotDirection.FROM_LEFT);
     * }</pre>
     *
     * @param direction The direction from which the new slide enters.
     * @return A new {@code PotTransition} configured as a push effect.
     * @see PotDirection
     */
    public static PotTransition push(PotDirection direction) {
        return new PotTransition(Type.PUSH, direction, 700, true, 0);
    }

    /**
     * Creates a wipe transition in the specified direction.
     *
     * <p>The new slide wipes over the old slide from the given edge.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition wipeDown = PotTransition.wipe(PotDirection.FROM_TOP);
     * }</pre>
     *
     * @param direction The direction from which the wipe originates.
     * @return A new {@code PotTransition} configured as a wipe effect.
     * @see PotDirection
     */
    public static PotTransition wipe(PotDirection direction) {
        return new PotTransition(Type.WIPE, direction, 700, true, 0);
    }

    /**
     * Creates a split transition in the specified direction.
     *
     * <p>The old slide splits apart along the given axis to reveal the new slide behind it.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Horizontal split
     * PotTransition splitHoriz = PotTransition.split(PotDirection.HORIZONTAL);
     * // Vertical split
     * PotTransition splitVert = PotTransition.split(PotDirection.VERTICAL);
     * }</pre>
     *
     * @param direction The split axis; typically {@link PotDirection#HORIZONTAL} or {@link PotDirection#VERTICAL}.
     * @return A new {@code PotTransition} configured as a split effect.
     * @see PotDirection
     */
    public static PotTransition split(PotDirection direction) {
        return new PotTransition(Type.SPLIT, direction, 700, true, 0);
    }

    /**
     * Creates a reveal transition in the specified direction.
     *
     * <p>The new slide is revealed behind the old slide, which moves away in the given direction.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition revealFromLeft = PotTransition.reveal(PotDirection.FROM_LEFT);
     * }</pre>
     *
     * @param direction The direction in which the old slide moves to reveal the new one.
     * @return A new {@code PotTransition} configured as a reveal effect.
     * @see PotDirection
     */
    public static PotTransition reveal(PotDirection direction) {
        return new PotTransition(Type.REVEAL, direction, 700, true, 0);
    }

    /**
     * Creates a cover transition in the specified direction.
     *
     * <p>The new slide moves in from the given direction to cover the old slide.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition coverFromBottom = PotTransition.cover(PotDirection.FROM_BOTTOM);
     * }</pre>
     *
     * @param direction The direction from which the new slide enters to cover the old.
     * @return A new {@code PotTransition} configured as a cover effect.
     * @see PotDirection
     */
    public static PotTransition cover(PotDirection direction) {
        return new PotTransition(Type.COVER, direction, 700, true, 0);
    }

    /**
     * Returns a dissolve (dither) transition.
     *
     * <p>This is equivalent to the constant {@link #DISSOLVE}.</p>
     *
     * @return A {@code PotTransition} instance of type {@link Type#DISSOLVE} with a 700ms duration.
     * @see #DISSOLVE
     */
    public static PotTransition dissolve() {
        return DISSOLVE;
    }

    /**
     * Returns a zoom-in transition.
     *
     * <p>This is equivalent to the constant {@link #ZOOM}.</p>
     *
     * @return A {@code PotTransition} instance of type {@link Type#ZOOM} with direction {@link PotDirection#IN}.
     * @see #ZOOM
     */
    public static PotTransition zoom() {
        return ZOOM;
    }

    /**
     * Creates a blinds transition with the specified orientation.
     *
     * <p>The transition uses horizontal or vertical strips that open like window blinds.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition horizontalBlinds = PotTransition.blinds(PotDirection.HORIZONTAL);
     * PotTransition verticalBlinds = PotTransition.blinds(PotDirection.VERTICAL);
     * }</pre>
     *
     * @param direction The orientation of the blinds; must be {@link PotDirection#HORIZONTAL} or {@link PotDirection#VERTICAL}.
     * @return A new {@code PotTransition} configured as a blinds effect.
     * @see PotDirection
     */
    public static PotTransition blinds(PotDirection direction) {
        return new PotTransition(Type.BLINDS, direction, 700, true, 0);
    }

    /**
     * Returns a checkerboard pattern transition.
     *
     * <p>This is equivalent to the constant {@link #CHECKERBOARD}.</p>
     *
     * @return A {@code PotTransition} instance of type {@link Type#CHECKERBOARD} with a 700ms duration.
     * @see #CHECKERBOARD
     */
    public static PotTransition checkerboard() {
        return CHECKERBOARD;
    }

    /**
     * Creates a clock wipe transition with the specified rotation.
     *
     * <p>The transition reveals the new slide with a radial wipe, like the sweep of a clock hand.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition clockwiseClock = PotTransition.clock(true);
     * PotTransition counterClockwiseClock = PotTransition.clock(false);
     * }</pre>
     *
     * @param clockwise {@code true} for a clockwise wipe, {@code false} for counter-clockwise.
     * @return A new {@code PotTransition} configured as a clock wipe effect.
     */
    public static PotTransition clock(boolean clockwise) {
        return new PotTransition(Type.CLOCK,
            clockwise ? PotDirection.CLOCKWISE : PotDirection.COUNTER_CLOCKWISE,
            700, true, 0);
    }

    /**
     * Creates a random bars transition with the specified sweep direction.
     *
     * <p>Bars of random height sweep across the screen in the given direction.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition barsHorizontal = PotTransition.randomBars(PotDirection.HORIZONTAL);
     * PotTransition barsVertical = PotTransition.randomBars(PotDirection.VERTICAL);
     * }</pre>
     *
     * @param direction The sweep direction; must be {@link PotDirection#HORIZONTAL} or {@link PotDirection#VERTICAL}.
     * @return A new {@code PotTransition} configured as a random bars effect.
     * @see PotDirection
     */
    public static PotTransition randomBars(PotDirection direction) {
        return new PotTransition(Type.RANDOM_BARS, direction, 700, true, 0);
    }

    /**
     * Creates a transition that randomly selects its visual effect at runtime.
     *
     * <p>The actual transition type is chosen randomly from the available types when the transition is executed.
     * The default duration is 700 milliseconds and click advancement is enabled.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition randomTransition = PotTransition.random();
     * }</pre>
     *
     * @return A new {@code PotTransition} configured to use a random effect type.
     */
    public static PotTransition random() {
        return new PotTransition(Type.RANDOM, null, 700, true, 0);
    }

    // ==================== Fluent Configuration Methods ====================

    /**
     * Returns a new transition with the specified total animation duration.
     *
     * <p>This method creates a copy of the current transition with the {@code duration} field replaced.
     * The duration is clamped to be non-negative.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition slowFade = PotTransition.fade().withDuration(2000);
     * }</pre>
     *
     * @param milliseconds The new total animation duration in milliseconds.
     * @return A new {@code PotTransition} instance with the updated duration.
     */
    public PotTransition withDuration(int milliseconds) {
        return new PotTransition(type, direction, milliseconds, advanceOnClick, advanceAfterTime);
    }

    /**
     * Returns a new transition with the specified click-advancement behavior.
     *
     * <p>This method creates a copy of the current transition with the click-advancement flag replaced.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition noClickFade = PotTransition.FADE.withAdvanceOnClick(false);
     * }</pre>
     *
     * @param advanceOnClick {@code true} if a mouse click can advance the slide during the transition, {@code false} otherwise.
     * @return A new {@code PotTransition} instance with the updated click-advancement behavior.
     */
    public PotTransition withAdvanceOnClick(boolean advanceOnClick) {
        return new PotTransition(type, direction, duration, advanceOnClick, advanceAfterTime);
    }

    /**
     * Returns a new transition with the specified auto-advance delay.
     *
     * <p>This method creates a copy of the current transition with the auto-advance delay replaced.
     * A delay of zero disables auto-advance. The delay is clamped to be non-negative.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition autoAdvanceFade = PotTransition.FADE.withAdvanceAfter(5000);
     * }</pre>
     *
     * @param milliseconds The auto-advance delay in milliseconds; 0 disables auto-advance.
     * @return A new {@code PotTransition} instance with the updated auto-advance delay.
     */
    public PotTransition withAdvanceAfter(int milliseconds) {
        return new PotTransition(type, direction, duration, advanceOnClick, milliseconds);
    }

    /**
     * Returns a new transition with the specified directional modifier.
     *
     * <p>This method creates a copy of the current transition with the direction replaced.
     * The direction may be {@code null} for transition types that do not require a direction.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition pushFromTop = PotTransition.PUSH_FROM_RIGHT.withDirection(PotDirection.FROM_TOP);
     * }</pre>
     *
     * @param direction The new directional modifier, or {@code null}.
     * @return A new {@code PotTransition} instance with the updated direction.
     * @see PotDirection
     */
    public PotTransition withDirection(PotDirection direction) {
        return new PotTransition(type, direction, duration, advanceOnClick, advanceAfterTime);
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the visual effect type of this transition.
     *
     * @return The transition type, never {@code null}.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the directional modifier of this transition.
     *
     * @return The direction, or {@code null} if this transition type does not use a direction.
     */
    public PotDirection getDirection() {
        return direction;
    }

    /**
     * Returns the total animation duration.
     *
     * @return The duration in milliseconds, always non-negative.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns whether a mouse click can advance the slide during this transition.
     *
     * @return {@code true} if click advancement is enabled, {@code false} otherwise.
     */
    public boolean isAdvanceOnClick() {
        return advanceOnClick;
    }

    /**
     * Returns the auto-advance delay.
     *
     * @return The delay in milliseconds before auto-advancing; 0 indicates auto-advance is disabled.
     */
    public int getAdvanceAfterTime() {
        return advanceAfterTime;
    }

    /**
     * Determines whether this transition represents no visual effect.
     *
     * <p>This is a convenience method equivalent to checking if the type is {@link Type#NONE}.</p>
     *
     * @return {@code true} if the transition type is {@link Type#NONE}, {@code false} otherwise.
     */
    public boolean isNone() {
        return type == Type.NONE;
    }

    /**
     * Determines whether this transition will auto-advance after a delay.
     *
     * <p>This is a convenience method equivalent to checking if {@link #getAdvanceAfterTime()} is greater than zero.</p>
     *
     * @return {@code true} if the auto-advance delay is positive, {@code false} otherwise.
     */
    public boolean hasAutoAdvance() {
        return advanceAfterTime > 0;
    }

    // ==================== Object Overrides ====================

    /**
     * Compares this transition to the specified object for equality.
     *
     * <p>Two transitions are considered equal if they have the same type, direction,
     * duration, click-advancement behavior, and auto-advance delay.</p>
     *
     * @param o The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotTransition that = (PotTransition) o;
        return duration == that.duration &&
               advanceOnClick == that.advanceOnClick &&
               advanceAfterTime == that.advanceAfterTime &&
               type == that.type &&
               direction == that.direction;
    }

    /**
     * Returns a hash code value for this transition.
     *
     * @return A hash code based on all significant fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, direction, duration, advanceOnClick, advanceAfterTime);
    }

    /**
     * Returns a string representation of this transition.
     *
     * <p>The string includes the type, direction (if present), duration, and flags for
     * click advancement and auto-advance. For the {@link Type#NONE} type, a simplified
     * representation is returned.</p>
     *
     * @return A descriptive string representation.
     */
    @Override
    public String toString() {
        if (isNone()) {
            return "PotTransition{NONE}";
        }
        StringBuilder sb = new StringBuilder("PotTransition{");
        sb.append("type=").append(type);
        if (direction != null) sb.append(", direction=").append(direction);
        sb.append(", duration=").append(duration).append("ms");
        if (!advanceOnClick) sb.append(", noClick");
        if (advanceAfterTime > 0) sb.append(", autoAfter=").append(advanceAfterTime).append("ms");
        sb.append('}');
        return sb.toString();
    }
}
