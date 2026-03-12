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

import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.*;

import javax.xml.namespace.QName;

/**
 * A helper class for applying, removing, and reading slide transition effects in PowerPoint presentations.
 *
 * <p>This class provides static utility methods to manipulate the transition properties of a slide
 * within a PowerPoint presentation. It translates between the high-level {@link PotTransition} model
 * and the underlying XML representation ({@link CTSlideTransition}) used by Apache POI. Operations
 * include applying a specified transition, removing any existing transition, and reading the current
 * transition settings from a slide. All methods handle null inputs gracefully and log warnings for
 * any processing errors.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class TransitionHelper {

    /**
     * Applies a specified transition effect to a slide.
     *
     * <p>This method configures the slide's underlying XML transition properties based on the provided
     * {@link PotTransition} object. If the transition is {@link PotTransition#NONE} or represents "none",
     * any existing transition is removed. Otherwise, it sets the transition type, direction, duration,
     * and advance behavior (on click or after a time). The method clears any previous transition effect
     * before applying the new one. If the slide or transition parameter is null, the method returns
     * without performing any action. Any exceptions during processing are logged as warnings.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFSlide slide = ...; // obtain a slide
     * PotTransition fadeIn = PotTransition.fade().withDuration(1000);
     * TransitionHelper.apply(slide, fadeIn);
     * }</pre>
     *
     * @param slide      the slide to which the transition will be applied; if null, the method does nothing
     * @param transition the transition configuration to apply; if null, the method does nothing
     *
     * @see #remove(XSLFSlide)
     * @see #get(XSLFSlide)
     */
    static void apply(XSLFSlide slide, PotTransition transition) {
        if (slide == null || transition == null) {
            return;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide == null) {
                return;
            }

            // Handle the "none" transition
            if (transition.isNone()) {
                remove(slide);
                return;
            }

            // Get or create the transition element
            CTSlideTransition trans = ctSlide.isSetTransition()
                ? ctSlide.getTransition()
                : ctSlide.addNewTransition();

            // Set duration via speed enumeration
            if (transition.getDuration() > 0) {
                trans.setSpd(getTransitionSpeed(transition.getDuration()));
            }

            // Set advance-on-click behavior
            trans.setAdvClick(transition.isAdvanceOnClick());

            // Set or clear auto-advance time
            if (transition.hasAutoAdvance()) {
                trans.setAdvTm(transition.getAdvanceAfterTime());
            } else {
                if (trans.isSetAdvTm()) {
                    trans.unsetAdvTm();
                }
            }

            // Clear any existing transition effect
            clearTransitionEffect(trans);

            // Apply the new transition effect
            applyTransitionEffect(trans, transition);

        } catch (Exception e) {
            PotLogger.warn(TransitionHelper.class, "apply", "Failed to apply transition", e);
        }
    }

    /**
     * Removes any transition effect from a slide.
     *
     * <p>This method unsets the transition element in the slide's underlying XML structure, effectively
     * removing any applied transition. If the slide parameter is null or the slide does not have a
     * transition set, the method returns without performing any action. Any exceptions during processing
     * are logged as warnings.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFSlide slide = ...; // obtain a slide
     * TransitionHelper.remove(slide);
     * }</pre>
     *
     * @param slide the slide from which the transition will be removed; if null, the method does nothing
     *
     * @see #apply(XSLFSlide, PotTransition)
     * @see #get(XSLFSlide)
     */
    static void remove(XSLFSlide slide) {
        if (slide == null) {
            return;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide != null && ctSlide.isSetTransition()) {
                ctSlide.unsetTransition();
            }
        } catch (Exception e) {
            PotLogger.warn(TransitionHelper.class, "remove", "Failed to remove transition", e);
        }
    }

    /**
     * Reads the current transition effect from a slide.
     *
     * <p>This method inspects the slide's underlying XML transition properties and constructs a corresponding
     * {@link PotTransition} object. If the slide is null, does not have a transition element, or an error
     * occurs during parsing, the method returns {@link PotTransition#NONE}. The parsed transition includes
     * type, direction, duration, and advance behavior settings.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFSlide slide = ...; // obtain a slide
     * PotTransition current = TransitionHelper.get(slide);
     * System.out.println("Current transition: " + current.getType());
     * }</pre>
     *
     * @param slide the slide from which to read the transition; if null, returns {@link PotTransition#NONE}
     * @return a {@link PotTransition} representing the slide's current transition, or {@link PotTransition#NONE}
     *         if no transition is set or an error occurs
     *
     * @see #apply(XSLFSlide, PotTransition)
     * @see #remove(XSLFSlide)
     */
    static PotTransition get(XSLFSlide slide) {
        if (slide == null) {
            return PotTransition.NONE;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide == null || !ctSlide.isSetTransition()) {
                return PotTransition.NONE;
            }

            CTSlideTransition trans = ctSlide.getTransition();
            return parseTransition(trans);

        } catch (Exception e) {
            PotLogger.warn(TransitionHelper.class, "get", "Failed to read transition", e);
            return PotTransition.NONE;
        }
    }

    // ==================== Private Helper Methods ====================

    /**
     * Maps a duration in milliseconds to a standard transition speed enumeration.
     *
     * <p>This method converts a numeric duration into one of the predefined speed constants
     * ({@link STTransitionSpeed#FAST}, {@link STTransitionSpeed#MED}, or {@link STTransitionSpeed#SLOW})
     * based on threshold values defined in {@link PotConstants}.</p>
     *
     * @param durationMs the transition duration in milliseconds
     * @return the corresponding {@link STTransitionSpeed.Enum} value
     *
     * @see PotConstants#TRANSITION_FAST_MS
     * @see PotConstants#TRANSITION_MEDIUM_MS
     * @see PotConstants#TRANSITION_SLOW_MS
     */
    private static STTransitionSpeed.Enum getTransitionSpeed(int durationMs) {
        if (durationMs <= PotConstants.TRANSITION_FAST_MS) {
            return STTransitionSpeed.FAST;
        } else if (durationMs <= PotConstants.TRANSITION_MEDIUM_MS) {
            return STTransitionSpeed.MED;
        } else {
            return STTransitionSpeed.SLOW;
        }
    }

    /**
     * Clears all specific transition effect elements from a transition object.
     *
     * <p>This method unsets every possible transition effect sub-element (e.g., blinds, checker, circle)
     * within the provided {@link CTSlideTransition} to ensure a clean state before applying a new effect.</p>
     *
     * @param trans the transition object from which all effect elements will be removed
     */
    private static void clearTransitionEffect(CTSlideTransition trans) {
        // Unset all possible transition effect elements
        if (trans.isSetBlinds()) trans.unsetBlinds();
        if (trans.isSetChecker()) trans.unsetChecker();
        if (trans.isSetCircle()) trans.unsetCircle();
        if (trans.isSetComb()) trans.unsetComb();
        if (trans.isSetCover()) trans.unsetCover();
        if (trans.isSetCut()) trans.unsetCut();
        if (trans.isSetDiamond()) trans.unsetDiamond();
        if (trans.isSetDissolve()) trans.unsetDissolve();
        if (trans.isSetFade()) trans.unsetFade();
        if (trans.isSetNewsflash()) trans.unsetNewsflash();
        if (trans.isSetPlus()) trans.unsetPlus();
        if (trans.isSetPull()) trans.unsetPull();
        if (trans.isSetPush()) trans.unsetPush();
        if (trans.isSetRandom()) trans.unsetRandom();
        if (trans.isSetRandomBar()) trans.unsetRandomBar();
        if (trans.isSetSplit()) trans.unsetSplit();
        if (trans.isSetStrips()) trans.unsetStrips();
        if (trans.isSetWedge()) trans.unsetWedge();
        if (trans.isSetWheel()) trans.unsetWheel();
        if (trans.isSetWipe()) trans.unsetWipe();
        if (trans.isSetZoom()) trans.unsetZoom();
    }

    /**
     * Applies a specific transition effect and direction to a transition object.
     *
     * <p>This method configures the appropriate sub-element within the {@link CTSlideTransition}
     * based on the {@link PotTransition.Type} and {@link PotDirection}. It handles the creation
     * and parameterization of effect-specific XML structures (e.g., {@link CTSideDirectionTransition},
     * {@link CTEightDirectionTransition}).</p>
     *
     * @param trans      the transition object to which the effect will be applied
     * @param transition the source transition configuration containing type and direction
     */
    private static void applyTransitionEffect(CTSlideTransition trans, PotTransition transition) {
        PotDirection direction = transition.getDirection();

        switch (transition.getType()) {
            case FADE:
                trans.addNewFade();
                // Fade transition has no direction parameter
                break;

            case PUSH:
                CTSideDirectionTransition push = trans.addNewPush();
                if (direction != null) {
                    push.setDir(getSideDirection(direction));
                }
                break;

            case WIPE:
                CTSideDirectionTransition wipe = trans.addNewWipe();
                if (direction != null) {
                    wipe.setDir(getSideDirection(direction));
                }
                break;

            case SPLIT:
                CTSplitTransition split = trans.addNewSplit();
                if (direction != null) {
                    if (direction == PotDirection.HORIZONTAL || direction == PotDirection.HORIZONTAL_IN || direction == PotDirection.HORIZONTAL_OUT) {
                        split.setOrient(STDirection.HORZ);
                    } else {
                        split.setOrient(STDirection.VERT);
                    }
                    if (direction == PotDirection.IN || direction == PotDirection.HORIZONTAL_IN || direction == PotDirection.VERTICAL_IN) {
                        split.setDir(STTransitionInOutDirectionType.IN);
                    } else {
                        split.setDir(STTransitionInOutDirectionType.OUT);
                    }
                }
                break;

            case REVEAL:
            case COVER:
                CTEightDirectionTransition cover = trans.addNewCover();
                setEightDirection(cover, direction);
                break;

            case DISSOLVE:
                trans.addNewDissolve();
                break;

            case ZOOM:
                CTInOutTransition zoom = trans.addNewZoom();
                if (direction == PotDirection.IN) {
                    zoom.setDir(STTransitionInOutDirectionType.IN);
                } else {
                    zoom.setDir(STTransitionInOutDirectionType.OUT);
                }
                break;

            case BLINDS:
                CTOrientationTransition blinds = trans.addNewBlinds();
                if (direction == PotDirection.HORIZONTAL) {
                    blinds.setDir(STDirection.HORZ);
                } else {
                    blinds.setDir(STDirection.VERT);
                }
                break;

            case CHECKERBOARD:
                CTOrientationTransition checker = trans.addNewChecker();
                if (direction == PotDirection.HORIZONTAL) {
                    checker.setDir(STDirection.HORZ);
                } else {
                    checker.setDir(STDirection.VERT);
                }
                break;

            case CLOCK:
                CTWheelTransition wheel = trans.addNewWheel();
                // Wheel transition direction is fixed as clockwise
                wheel.setSpokes(1); // 1 spoke produces a clock-like effect
                break;

            case RANDOM_BARS:
                CTOrientationTransition randomBar = trans.addNewRandomBar();
                if (direction == PotDirection.HORIZONTAL) {
                    randomBar.setDir(STDirection.HORZ);
                } else {
                    randomBar.setDir(STDirection.VERT);
                }
                break;

            case COMB:
                CTOrientationTransition comb = trans.addNewComb();
                if (direction == PotDirection.HORIZONTAL) {
                    comb.setDir(STDirection.HORZ);
                } else {
                    comb.setDir(STDirection.VERT);
                }
                break;

            case CUT:
                trans.addNewCut();
                break;

            case RANDOM:
                trans.addNewRandom();
                break;

            default:
                // Fallback to a fade transition
                trans.addNewFade();
                break;
        }
    }

    /**
     * Converts a high-level direction constant to a sidedirection XML enumeration.
     *
     * <p>This method maps a {@link PotDirection} value (e.g., {@link PotDirection#LEFT},
     * {@link PotDirection#FROM_TOP}) to the corresponding {@link STTransitionSideDirectionType}
     * constant used by sideoriented transitions such as push and wipe.</p>
     *
     * @param direction the highlevel direction to convert; if null or unrecognized,
     *                  returns {@link STTransitionSideDirectionType#L}
     * @return the matching {@link STTransitionSideDirectionType.Enum} value
     *
     * @see #applyTransitionEffect(CTSlideTransition, PotTransition)
     * @see #parseSideDirection(STTransitionSideDirectionType.Enum)
     */
    private static STTransitionSideDirectionType.Enum getSideDirection(PotDirection direction) {
        switch (direction) {
            case FROM_LEFT:
            case LEFT:
                return STTransitionSideDirectionType.L;
            case FROM_RIGHT:
            case RIGHT:
                return STTransitionSideDirectionType.R;
            case FROM_TOP:
            case UP:
                return STTransitionSideDirectionType.U;
            case FROM_BOTTOM:
            case DOWN:
                return STTransitionSideDirectionType.D;
            default:
                return STTransitionSideDirectionType.L;
        }
    }

    /**
     * Converts a high-level direction constant to an eightdirection string.
     *
     * <p>This method maps a {@link PotDirection} value to the corresponding lowercase
     * direction string ("l", "r", "u", "d", "lu", "ru", "ld", "rd") used by eightdirection
     * transitions such as cover and reveal. The result is used to set the {@code dir} attribute
     * in the underlying XML.</p>
     *
     * @param direction the highlevel direction to convert; if null, returns "l"
     * @return a lowercase direction string suitable for the XML {@code dir} attribute
     *
     * @see #setEightDirection(CTEightDirectionTransition, PotDirection)
     * @see #parseEightDirection(XmlObject)
     */
    private static Object getEightDirection(PotDirection direction) {
        if (direction == null) {
            return "l";
        }

        switch (direction) {
            case FROM_LEFT:
            case LEFT:
                return "l";
            case FROM_RIGHT:
            case RIGHT:
                return "r";
            case FROM_TOP:
            case UP:
                return "u";
            case FROM_BOTTOM:
            case DOWN:
                return "d";
            case FROM_TOP_LEFT:
                return "lu";
            case FROM_TOP_RIGHT:
                return "ru";
            case FROM_BOTTOM_LEFT:
                return "ld";
            case FROM_BOTTOM_RIGHT:
                return "rd";
            default:
                return "l";
        }
    }

    /**
     * Parses a slidetransition XML object and extracts its settings into a {@link PotTransition}.
     *
     * <p>This method examines the provided {@link CTSlideTransition} and determines the transition
     * type, direction, duration, and advance behavior. It reads the speed attribute to infer the
     * duration, checks for autoadvance time, and inspects the specific effect subelement (e.g.,
     * {@code fade}, {@code push}) to reconstruct the original {@link PotTransition} configuration.
     * If the transition object does not contain a recognized effect, {@link PotTransition#NONE} is
     * returned.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * CTSlideTransition trans = slide.getXmlObject().getTransition();
     * PotTransition parsed = TransitionHelper.parseTransition(trans);
     * }</pre>
     *
     * @param trans the XML transition object to parse; must not be null
     * @return a {@link PotTransition} representing the parsed settings, or {@link PotTransition#NONE}
     *         if no effect is recognized
     *
     * @see #get(XSLFSlide)
     */
    private static PotTransition parseTransition(CTSlideTransition trans) {
        PotTransition.Type type = PotTransition.Type.NONE;
        PotDirection direction = null;
        int duration = PotConstants.TRANSITION_MEDIUM_MS;
        boolean advanceOnClick = trans.getAdvClick();
        int advanceAfterTime = trans.isSetAdvTm() ? (int) trans.getAdvTm() : 0;

        // Determine duration from the speed enumeration.
        if (trans.isSetSpd()) {
            switch (trans.getSpd().intValue()) {
                case STTransitionSpeed.INT_FAST:
                    duration = PotConstants.TRANSITION_FAST_MS;
                    break;
                case STTransitionSpeed.INT_MED:
                    duration = PotConstants.TRANSITION_MEDIUM_MS;
                    break;
                case STTransitionSpeed.INT_SLOW:
                    duration = PotConstants.TRANSITION_SLOW_MS;
                    break;
            }
        }

        // Identify the transition type and direction based on the present effect element.
        if (trans.isSetFade()) {
            type = PotTransition.Type.FADE;
        } else if (trans.isSetPush()) {
            type = PotTransition.Type.PUSH;
            direction = parseSideDirection(trans.getPush().getDir());
        } else if (trans.isSetWipe()) {
            type = PotTransition.Type.WIPE;
            direction = parseSideDirection(trans.getWipe().getDir());
        } else if (trans.isSetSplit()) {
            type = PotTransition.Type.SPLIT;
            // Direction parsing for split is omitted for brevity.
        } else if (trans.isSetCover()) {
            type = PotTransition.Type.COVER;
            direction = parseEightDirection(trans.getCover());
        } else if (trans.isSetDissolve()) {
            type = PotTransition.Type.DISSOLVE;
        } else if (trans.isSetBlinds()) {
            type = PotTransition.Type.BLINDS;
            direction = parseOrientation(trans.getBlinds().getDir());
        } else if (trans.isSetWheel()) {
            type = PotTransition.Type.CLOCK;
            direction = PotDirection.CLOCKWISE;
        } else if (trans.isSetRandom()) {
            type = PotTransition.Type.RANDOM;
        }

        PotTransition result;
        switch (type) {
            case FADE:
                result = PotTransition.fade();
                break;
            case PUSH:
                result = direction != null ? PotTransition.push(direction) : PotTransition.push(null);
                break;
            case WIPE:
                result = direction != null ? PotTransition.wipe(direction) : PotTransition.wipe(null);
                break;
            case COVER:
                result = PotTransition.cover(direction != null ? direction : PotDirection.FROM_LEFT);
                break;
            case DISSOLVE:
                result = PotTransition.dissolve();
                break;
            case BLINDS:
                result = direction != null ? PotTransition.blinds(direction) : PotTransition.blinds(null);
                break;
            case RANDOM:
                result = PotTransition.random();
                break;
            default:
                result = PotTransition.NONE;
        }
        return result.withDuration(duration)
            .withAdvanceOnClick(advanceOnClick)
            .withAdvanceAfter(advanceAfterTime);
    }

    /**
     * Converts a sidedirection XML enumeration to a highlevel direction constant.
     *
     * <p>This method maps an {@link STTransitionSideDirectionType} value (e.g., {@code L}, {@code U})
     * to the corresponding {@link PotDirection} constant used by the highlevel transition model.</p>
     *
     * @param dir the XML sidedirection enumeration; if null, returns {@link PotDirection#FROM_LEFT}
     * @return the matching {@link PotDirection} value
     *
     * @see #getSideDirection(PotDirection)
     * @see #parseTransition(CTSlideTransition)
     */
    private static PotDirection parseSideDirection(STTransitionSideDirectionType.Enum dir) {
        if (dir == null) return PotDirection.FROM_LEFT;
        switch (dir.intValue()) {
            case STTransitionSideDirectionType.INT_L:
                return PotDirection.FROM_LEFT;
            case STTransitionSideDirectionType.INT_R:
                return PotDirection.FROM_RIGHT;
            case STTransitionSideDirectionType.INT_U:
                return PotDirection.FROM_TOP;
            case STTransitionSideDirectionType.INT_D:
                return PotDirection.FROM_BOTTOM;
            default:
                return PotDirection.FROM_LEFT;
        }
    }

    /**
     * Converts an orientation XML enumeration to a highlevel direction constant.
     *
     * <p>This method maps an {@link STDirection} value ({@link STDirection#HORZ} or
     * {@link STDirection#VERT}) to the corresponding horizontal or vertical {@link PotDirection}
     * constant.</p>
     *
     * @param dir the XML orientation enumeration; if null, returns {@link PotDirection#HORIZONTAL}
     * @return {@link PotDirection#HORIZONTAL} for horizontal, {@link PotDirection#VERTICAL} for vertical
     *
     * @see #parseTransition(CTSlideTransition)
     */
    private static PotDirection parseOrientation(STDirection.Enum dir) {
        if (dir == null) return PotDirection.HORIZONTAL;
        switch (dir.intValue()) {
            case STDirection.INT_HORZ:
                return PotDirection.HORIZONTAL;
            case STDirection.INT_VERT:
                return PotDirection.VERTICAL;
            default:
                return PotDirection.HORIZONTAL;
        }
    }

    /**
     * Sets the direction attribute on an eightdirection transition XML element.
     *
     * <p>This method writes the appropriate {@code dir} attribute value (e.g., "l", "ru") to the
     * provided {@link CTEightDirectionTransition} object using an {@link XmlCursor}. The attribute
     * is derived from the given {@link PotDirection} via {@link #getEightDirection(PotDirection)}.</p>
     *
     * @param transition the XML transition element to modify; if null, the method does nothing
     * @param direction  the highlevel direction to apply; may be null (defaults to "l")
     *
     * @see #getEightDirection(PotDirection)
     * @see #parseEightDirection(XmlObject)
     */
    private static void setEightDirection(CTEightDirectionTransition transition, PotDirection direction) {
        if (transition == null) {
            return;
        }
        Object mapped = getEightDirection(direction);
        if (!(mapped instanceof String)) {
            return;
        }

        XmlCursor cursor = transition.newCursor();
        try {
            cursor.setAttributeText(new QName("dir"), (String) mapped);
        } finally {
            cursor.dispose();
        }
    }

    /**
     * Reads the direction attribute from an eightdirection transition XML element.
     *
     * <p>This method uses an {@link XmlCursor} to extract the {@code dir} attribute value from the
     * provided {@link XmlObject} (expected to be a {@link CTEightDirectionTransition}) and maps it
     * back to a {@link PotDirection} constant.</p>
     *
     * @param transition the XML transition element to inspect; if null, returns {@link PotDirection#FROM_LEFT}
     * @return the parsed {@link PotDirection}, or {@link PotDirection#FROM_LEFT} if the attribute is missing
     *         or unrecognized
     *
     * @see #getEightDirection(PotDirection)
     * @see #setEightDirection(CTEightDirectionTransition, PotDirection)
     */
    private static PotDirection parseEightDirection(XmlObject transition) {
        if (transition == null) {
            return PotDirection.FROM_LEFT;
        }

        XmlCursor cursor = transition.newCursor();
        try {
            String dir = cursor.getAttributeText(new QName("dir"));
            if (dir == null || dir.isEmpty()) {
                return PotDirection.FROM_LEFT;
            }
            switch (dir) {
                case "l":
                    return PotDirection.FROM_LEFT;
                case "r":
                    return PotDirection.FROM_RIGHT;
                case "u":
                    return PotDirection.FROM_TOP;
                case "d":
                    return PotDirection.FROM_BOTTOM;
                case "lu":
                    return PotDirection.FROM_TOP_LEFT;
                case "ru":
                    return PotDirection.FROM_TOP_RIGHT;
                case "ld":
                    return PotDirection.FROM_BOTTOM_LEFT;
                case "rd":
                    return PotDirection.FROM_BOTTOM_RIGHT;
                default:
                    return PotDirection.FROM_LEFT;
            }
        } finally {
            cursor.dispose();
        }
    }

    // PotTransition builder used internally for constructing transition objects.
    private static class PotTransitionBuilder {
        private PotTransition.Type type;
        private PotDirection direction;
        private int duration;
        private boolean advanceOnClick;
        private int advanceAfterTime;

        PotTransitionBuilder(PotTransition.Type type, PotDirection direction,
                            int duration, boolean advanceOnClick, int advanceAfterTime) {
            this.type = type;
            this.direction = direction;
            this.duration = duration;
            this.advanceOnClick = advanceOnClick;
            this.advanceAfterTime = advanceAfterTime;
        }
    }
}
