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

import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for managing PowerPoint slide animations via the underlying OpenXML structure.
 *
 * <p>This class provides static methods to apply, remove, query, and clear animations on shapes within a slide.
 * It operates directly on the XML representation of the presentation, translating high-level {@link PotAnimation}
 * objects into the corresponding OpenXML elements and attributes within the slide's timing tree.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * XSLFSlide slide = ...;
 * XSLFShape shape = ...;
 * PotAnimation animation = PotAnimation.fadeIn().duration(1.5);
 * AnimationHelper.apply(slide, shape, animation);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class AnimationHelper {

    /**
     * A mapping from {@link PotAnimation.Effect} to its corresponding OpenXML preset string for entrance animations.
     */
    private static final Map<PotAnimation.Effect, String> ENTRANCE_EFFECT_MAP = new HashMap<>();
    /**
     * A mapping from {@link PotAnimation.Effect} to its corresponding OpenXML preset string for exit animations.
     */
    private static final Map<PotAnimation.Effect, String> EXIT_EFFECT_MAP = new HashMap<>();
    /**
     * A mapping from {@link PotAnimation.Effect} to its corresponding OpenXML preset string for emphasis animations.
     */
    private static final Map<PotAnimation.Effect, String> EMPHASIS_EFFECT_MAP = new HashMap<>();

    static {
        // Initialize entrance effect mappings
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.APPEAR, "appear");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.FADE, "fade");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.FLY, "fly");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.ZOOM, "zoom");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.SPIN, "spin");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.BOUNCE, "bounce");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.WIPE, "wipe");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.SPLIT, "split");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.WHEEL, "wheel");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.RANDOM_BARS, "randomBars");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.GROW_AND_TURN, "growAndTurn");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.FLOAT, "float");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.PINWHEEL, "pinwheel");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.EXPAND, "expand");
        ENTRANCE_EFFECT_MAP.put(PotAnimation.Effect.RISE_UP, "riseUp");

        // Initialize exit effect mappings
        EXIT_EFFECT_MAP.put(PotAnimation.Effect.DISAPPEAR, "disappear");
        EXIT_EFFECT_MAP.put(PotAnimation.Effect.FADE, "fade");
        EXIT_EFFECT_MAP.put(PotAnimation.Effect.FLY, "fly");
        EXIT_EFFECT_MAP.put(PotAnimation.Effect.ZOOM, "zoom");
        EXIT_EFFECT_MAP.put(PotAnimation.Effect.SPIN, "spin");
        EXIT_EFFECT_MAP.put(PotAnimation.Effect.WIPE, "wipe");

        // Initialize emphasis effect mappings
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.PULSE, "pulse");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.GROW_SHRINK, "growShrink");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.TEETER, "teeter");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.SPIN_EMPHASIS, "spin");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.DARKEN, "darken");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.LIGHTEN, "lighten");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.TRANSPARENCY, "transparency");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.FLASH_ONCE, "flashOnce");
        EMPHASIS_EFFECT_MAP.put(PotAnimation.Effect.SHIMMER, "shimmer");
    }

    /**
     * Applies a specified animation to a shape on a slide.
     *
     * <p>This method modifies the slide's underlying OpenXML timing structure to add an animation node
     * for the given shape. It ensures the main sequence exists, creates a new parallel time node for the
     * animation, configures its trigger, duration, and effect, and binds it to the target shape. If any
     * required XML object is null, the method returns silently. If an error occurs during the process,
     * a {@link PotException} with error code {@link PotException.ErrorCode#ANIMATION_ERROR} is thrown.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * AnimationHelper.apply(slide, shape, PotAnimation.flyIn().direction(PotDirection.FROM_LEFT));
     * }</pre>
     *
     * @param slide     The slide containing the shape. If null, the method returns without action.
     * @param shape     The shape to animate. If null, the method returns without action.
     * @param animation The animation configuration to apply. If null, the method returns without action.
     *
     * @throws PotException if an error occurs while applying the animation, with context including the effect,
     *                      type, and duration.
     * @see PotAnimation
     * @see PotException.ErrorCode#ANIMATION_ERROR
     * @since 1.0
     */
    static void apply(XSLFSlide slide, XSLFShape shape, PotAnimation animation) {
        if (slide == null || shape == null || animation == null) {
            return;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide == null) {
                return;
            }

            // Ensure the slide timing element exists
            CTSlideTiming timing = ctSlide.isSetTiming() ? ctSlide.getTiming() : ctSlide.addNewTiming();

            // Ensure the top-level time node list exists
            CTTimeNodeList tnLst = timing.isSetTnLst() ? timing.getTnLst() : timing.addNewTnLst();

            // Ensure the main animation sequence exists
            CTTLCommonTimeNodeData mainSeq = ensureMainSequence(tnLst);

            // Get the shape's ID and add the animation node
            int shapeId = getShapeId(shape);
            addAnimationNode(mainSeq, shapeId, animation);

        } catch (Exception e) {
            // Log the warning and wrap the exception
            PotLogger.warn(AnimationHelper.class, "apply",
                "Failed to apply animation: " + animation.getEffect(), e);
            throw new PotException("Failed to apply animation: " + animation.getEffect(), e,
                PotException.ErrorCode.ANIMATION_ERROR)
                .withContext("effect", animation.getEffect())
                .withContext("type", animation.getType())
                .withContext("duration", animation.getDuration());
        }
    }

    /**
     * Removes all animations associated with a specific shape from a slide.
     *
     * <p>This method searches the slide's timing tree for animation nodes that target the given shape
     * and removes them. If the slide, shape, or underlying XML objects are null, or if no timing
     * structure exists, the method returns silently. Errors during removal are silently ignored.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * AnimationHelper.remove(slide, shape);
     * }</pre>
     *
     * @param slide The slide from which to remove animations. If null, the method returns without action.
     * @param shape The shape whose animations should be removed. If null, the method returns without action.
     *
     * @since 1.0
     */
    static void remove(XSLFSlide slide, XSLFShape shape) {
        if (slide == null || shape == null) {
            return;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide == null || !ctSlide.isSetTiming()) {
                return;
            }

            CTSlideTiming timing = ctSlide.getTiming();
            if (!timing.isSetTnLst()) {
                return;
            }

            int shapeId = getShapeId(shape);
            removeAnimationForShape(timing.getTnLst(), shapeId);

        } catch (Exception e) {
            // Silently ignore errors during removal
        }
    }

    /**
     * Clears all animations from a slide by removing its entire timing structure.
     *
     * <p>This method unsets the {@code timing} element of the slide's underlying XML object,
     * effectively removing all animations defined for the slide. If the slide or its XML object
     * is null, the method returns silently. If an error occurs, a {@link PotException} with error
     * code {@link PotException.ErrorCode#ANIMATION_ERROR} is thrown.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * AnimationHelper.clear(slide);
     * }</pre>
     *
     * @param slide The slide from which to clear all animations. If null, the method returns without action.
     *
     * @throws PotException if an error occurs while clearing animations.
     * @see PotException.ErrorCode#ANIMATION_ERROR
     * @since 1.0
     */
    static void clear(XSLFSlide slide) {
        if (slide == null) {
            return;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide != null && ctSlide.isSetTiming()) {
                ctSlide.unsetTiming();
            }
        } catch (Exception e) {
            // Log the warning and wrap the exception
            PotLogger.warn(AnimationHelper.class, "clear",
                "Failed to clear all animations", e);
            throw new PotException("Failed to clear all animations", e,
                PotException.ErrorCode.ANIMATION_ERROR);
        }
    }

    /**
     * Checks whether a specific shape on a slide has any animations applied.
     *
     * <p>This method examines the slide's timing tree for any animation nodes that target the given shape.
     * It returns {@code true} if at least one such node is found. If the slide, shape, or required XML
     * objects are null, or if no timing structure exists, the method returns {@code false}. If an error
     * occurs during the check, a {@link PotException} with error code {@link PotException.ErrorCode#ANIMATION_ERROR}
     * is thrown.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * boolean animated = AnimationHelper.hasAnimation(slide, shape);
     * }</pre>
     *
     * @param slide The slide to check. If null, returns {@code false}.
     * @param shape The shape to check for animations. If null, returns {@code false}.
     *
     * @return {@code true} if the shape has at least one animation applied; {@code false} otherwise.
     *
     * @throws PotException if an error occurs while checking for animations.
     * @see PotException.ErrorCode#ANIMATION_ERROR
     * @since 1.0
     */
    static boolean hasAnimation(XSLFSlide slide, XSLFShape shape) {
        if (slide == null || shape == null) {
            return false;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide == null || !ctSlide.isSetTiming()) {
                return false;
            }

            CTSlideTiming timing = ctSlide.getTiming();
            if (!timing.isSetTnLst()) {
                return false;
            }

            int shapeId = getShapeId(shape);
            return hasAnimationForShape(timing.getTnLst(), shapeId);

        } catch (Exception e) {
            // Log the warning and wrap the exception
            PotLogger.warn(AnimationHelper.class, "hasAnimation",
                "Failed to check animation existence", e);
            throw new PotException("Failed to check animation existence", e,
                PotException.ErrorCode.ANIMATION_ERROR);
        }
    }

    /**
     * Retrieves the animation configuration applied to a specific shape on a slide.
     *
     * <p>This method searches the slide's timing tree for the first animation node targeting the given shape,
     * parses its attributes, and attempts to reconstruct a corresponding {@link PotAnimation} object.
     * If multiple animations exist for the shape, only the first one is returned. If no animation is found,
     * or if the slide, shape, or required XML objects are null, the method returns {@code null}. If an error
     * occurs during retrieval, a {@link PotException} with error code {@link PotException.ErrorCode#ANIMATION_ERROR}
     * is thrown.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotAnimation anim = AnimationHelper.get(slide, shape);
     * if (anim != null) {
     *     System.out.println("Effect: " + anim.getEffect());
     * }
     * }</pre>
     *
     * @param slide The slide containing the shape. If null, returns {@code null}.
     * @param shape The shape whose animation to retrieve. If null, returns {@code null}.
     *
     * @return The {@link PotAnimation} object representing the shape's animation, or {@code null} if none exists.
     *
     * @throws PotException if an error occurs while reading the animation configuration.
     * @see PotException.ErrorCode#ANIMATION_ERROR
     * @since 1.0
     */
    static PotAnimation get(XSLFSlide slide, XSLFShape shape) {
        if (slide == null || shape == null) {
            return null;
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide == null || !ctSlide.isSetTiming() || !ctSlide.getTiming().isSetTnLst()) {
                return null;
            }

            int shapeId = getShapeId(shape);
            Element ctn = findFirstAnimationNodeForShape(ctSlide.getTiming().getTnLst(), shapeId);
            if (ctn == null) {
                return null;
            }

            PotAnimation animation = mapAnimationFromNode(ctn);
            String dur = ctn.getAttribute("dur");
            if (dur != null && !dur.isEmpty()) {
                try {
                    animation = animation.duration(Double.parseDouble(dur));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed duration values
                }
            }
            return animation;
        } catch (Exception e) {
            PotLogger.warn(AnimationHelper.class, "get",
                "Failed to read animation config", e);
            throw new PotException("Failed to read animation config", e,
                PotException.ErrorCode.ANIMATION_ERROR);
        }
    }

    // ==================== Internal Helper Methods ====================

    /**
     * Retrieves the numeric ID of a shape.
     *
     * <p>This method attempts to get the shape's ID via {@link XSLFShape#getShapeId()}. If an exception occurs,
     * it returns 0 as a fallback.</p>
     *
     * @param shape The shape whose ID to retrieve.
     *
     * @return The shape's ID as an integer, or 0 if retrieval fails.
     *
     * @since 1.0
     */
    private static int getShapeId(XSLFShape shape) {
        try {
            return (int) shape.getShapeId();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Ensures the main animation sequence exists within the time node list.
     *
     * <p>This method searches for an existing main sequence node within the provided time node list.
     * If found, it returns the common time node data for that sequence. If not found, it creates
     * a new root parallel node containing a main sequence node with default properties and returns it.</p>
     *
     * @param tnLst the parent time node list to search or modify
     * @return the common time node data for the main sequence, never null
     */
    private static CTTLCommonTimeNodeData ensureMainSequence(CTTimeNodeList tnLst) {
        // Search for an existing main sequence
        for (CTTLTimeNodeParallel par : tnLst.getParArray()) {
            CTTLCommonTimeNodeData ctn = par.getCTn();
            if (ctn != null) {
                // Check child sequences
                CTTimeNodeList childTnLst = ctn.getChildTnLst();
                if (childTnLst != null) {
                    for (CTTLTimeNodeSequence seq : childTnLst.getSeqArray()) {
                        CTTLCommonTimeNodeData seqCtn = seq.getCTn();
                        if (seqCtn != null) {
                            return seqCtn;
                        }
                    }
                }
            }
        }

        // Create a new main sequence structure
        CTTLTimeNodeParallel rootPar = tnLst.addNewPar();
        CTTLCommonTimeNodeData rootCtn = rootPar.addNewCTn();
        rootCtn.setId(1);
        rootCtn.setDur(STTLTimeIndefinite.INDEFINITE);
        rootCtn.setRestart(STTLTimeNodeRestartType.NEVER);
        rootCtn.setNodeType(STTLTimeNodeType.TM_ROOT);

        CTTimeNodeList childTnLst = rootCtn.addNewChildTnLst();
        CTTLTimeNodeSequence seq = childTnLst.addNewSeq();
        seq.setConcurrent(true);
        seq.setNextAc(STTLNextActionType.SEEK);

        CTTLCommonTimeNodeData seqCtn = seq.addNewCTn();
        seqCtn.setId(2);
        seqCtn.setDur(STTLTimeIndefinite.INDEFINITE);
        seqCtn.setNodeType(STTLTimeNodeType.MAIN_SEQ);

        return seqCtn;
    }

    /**
     * Adds an animation node for a specific shape to the main sequence.
     *
     * <p>This method creates a new parallel time node under the main sequence to represent the animation.
     * It configures the node's identifier, fill behavior, trigger condition, repeat count, and child effect node.</p>
     *
     * @param mainSeq the common time node data of the main sequence
     * @param shapeId the identifier of the target shape
     * @param animation the animation configuration to apply
     */
    private static void addAnimationNode(CTTLCommonTimeNodeData mainSeq, int shapeId, PotAnimation animation) {
        CTTimeNodeList childTnLst = mainSeq.isSetChildTnLst() ? mainSeq.getChildTnLst() : mainSeq.addNewChildTnLst();

        // Create a parallel node for the animation
        CTTLTimeNodeParallel animPar = childTnLst.addNewPar();
        CTTLCommonTimeNodeData animCtn = animPar.addNewCTn();

        // Set basic properties
        int nextId = getNextId(mainSeq);
        animCtn.setId(nextId);
        animCtn.setFill(STTLTimeNodeFillType.HOLD);

        // Set start condition via DOM manipulation
        PotAnimation.Trigger trigger = animation.getTrigger();
        if (trigger == null) {
            trigger = PotAnimation.Trigger.ON_CLICK;
        }
        buildTriggerCondition(animCtn, trigger, animation.getDelay());

        // Configure repeat count
        if (animation.getRepeatCount() != 0) {
            if (animation.getRepeatCount() < 0) {
                animCtn.setRepeatCount(STTLTimeIndefinite.INDEFINITE.toString());
            } else {
                animCtn.setRepeatCount(String.valueOf(animation.getRepeatCount() * 1000));
            }
        }

        // Add the effect node as a child
        CTTimeNodeList animChildTnLst = animCtn.addNewChildTnLst();
        addEffectNode(animChildTnLst, shapeId, animation, nextId + 1);
    }

    /**
     * Builds the trigger condition element for an animation node using DOM manipulation.
     *
     * <p>This method creates the appropriate start condition list based on the specified trigger type and delay.
     * The condition is appended directly to the provided common time node data's DOM node.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Build a trigger condition for an animation that starts on click with a 500ms delay
     * buildTriggerCondition(animCtn, PotAnimation.Trigger.ON_CLICK, 500);
     * }</pre>
     *
     * @param animCtn the common time node data to receive the condition
     * @param trigger the trigger type for the animation
     * @param delayMs the delay in milliseconds before the animation starts
     */
    private static void buildTriggerCondition(CTTLCommonTimeNodeData animCtn,
                                              PotAnimation.Trigger trigger, int delayMs) {
        Node root = animCtn.getDomNode();
        if (root == null) {
            return;
        }
        String pNs = "http://schemas.openxmlformats.org/presentationml/2006/main";
        String delay = delayMs > 0 ? String.valueOf(delayMs) : "0";

        Element stCondLst = root.getOwnerDocument().createElementNS(pNs, "p:stCondLst");

        switch (trigger) {
            case WITH_PREVIOUS: {
                // Start with previous animation, delay=0, no explicit event
                Element cond = root.getOwnerDocument().createElementNS(pNs, "p:cond");
                cond.setAttribute("delay", delay);
                stCondLst.appendChild(cond);
                break;
            }
            case AFTER_PREVIOUS: {
                // Start after previous animation, delay=0
                Element cond = root.getOwnerDocument().createElementNS(pNs, "p:cond");
                cond.setAttribute("delay", delay.equals("0") ? "0" : delay);
                stCondLst.appendChild(cond);
                break;
            }
            case ON_CLICK:
            default: {
                // Default: evt=onBegin, group=endParent, delay=indefinite
                Element cond = root.getOwnerDocument().createElementNS(pNs, "p:cond");
                cond.setAttribute("delay", "indefinite");
                Element tn = root.getOwnerDocument().createElementNS(pNs, "p:tn");
                tn.setAttribute("val", "0");
                Element rtn = root.getOwnerDocument().createElementNS(pNs, "p:rtn");
                rtn.setAttribute("val", "PPTtime://&slidenum=slide");
                // Add onclick trigger condition
                cond = root.getOwnerDocument().createElementNS(pNs, "p:cond");
                cond.setAttribute("evt", "onBegin");
                cond.setAttribute("delay", delayMs > 0 ? delay : "indefinite");
                Element tgtEl = root.getOwnerDocument().createElementNS(pNs, "p:tgtEl");
                Element sldTgt = root.getOwnerDocument().createElementNS(pNs, "p:sldTgt");
                tgtEl.appendChild(sldTgt);
                cond.appendChild(tgtEl);
                stCondLst.appendChild(cond);
                break;
            }
        }

        root.appendChild(stCondLst);
    }

    /**
     * Adds an effect node to the parent time node list.
     *
     * <p>This method creates a parallel node representing the visual effect of the animation.
     * It configures the preset identifier, class, subtype, duration, and node type based on the
     * animation configuration. Depending on the animation type, it delegates to specific behavior
     * creation methods.</p>
     *
     * @param parentTnLst the parent time node list to add the effect to
     * @param shapeId the identifier of the target shape
     * @param animation the animation configuration
     * @param id the identifier to assign to the effect node
     */
    private static void addEffectNode(CTTimeNodeList parentTnLst, int shapeId, PotAnimation animation, int id) {
        CTTLTimeNodeParallel effectPar = parentTnLst.addNewPar();
        CTTLCommonTimeNodeData effectCtn = effectPar.addNewCTn();

        effectCtn.setId(id);
        effectCtn.setPresetID(getPresetId(animation));
        effectCtn.setPresetClass(getPresetClass(animation));
        effectCtn.setPresetSubtype(getPresetSubtype(animation));
        effectCtn.setFill(STTLTimeNodeFillType.HOLD);
        effectCtn.setNodeType(STTLTimeNodeType.CLICK_EFFECT);
        effectCtn.setDur(animation.getDuration());

        // Add behavior node as child
        CTTimeNodeList effectChildTnLst = effectCtn.addNewChildTnLst();

        switch (animation.getType()) {
            case EMPHASIS:
                addEmphasisBehavior(effectChildTnLst, shapeId, animation, id + 1);
                break;
            case MOTION_PATH:
                addMotionBehavior(effectChildTnLst, shapeId, animation, id + 1);
                break;
            case ENTRANCE:
            case EXIT:
            default:
                addAnimateBehavior(effectChildTnLst, shapeId, animation, id + 1);
                break;
        }
    }

    /**
     * Adds an animate behavior node for entrance or exit animations.
     *
     * <p>This method creates an {@code anim} behavior node within the specified time node list.
     * It configures the common behavior data with the provided identifier, duration, and fill type,
     * and binds it to the target shape.</p>
     *
     * @param tnLst the time node list to add the behavior to
     * @param shapeId the identifier of the target shape
     * @param animation the animation configuration (duration is used)
     * @param id the identifier to assign to the behavior node
     */
    private static void addAnimateBehavior(CTTimeNodeList tnLst, int shapeId,
                                           PotAnimation animation, int id) {
        CTTLAnimateBehavior animBehavior = tnLst.addNewAnim();
        CTTLCommonBehaviorData cBhvr = animBehavior.addNewCBhvr();
        CTTLCommonTimeNodeData bhvrCtn = cBhvr.addNewCTn();
        bhvrCtn.setId(id);
        bhvrCtn.setDur(animation.getDuration());
        bhvrCtn.setFill(STTLTimeNodeFillType.HOLD);
        bindTargetShape(cBhvr, shapeId);
    }

    /**
     * Adds an animate effect behavior node for emphasis animations.
     *
     * <p>This method creates an {@code animEffect} behavior node within the specified time node list.
     * It configures the common behavior data with the provided identifier, duration, and fill type,
     * and binds it to the target shape.</p>
     *
     * @param tnLst the time node list to add the behavior to
     * @param shapeId the identifier of the target shape
     * @param animation the animation configuration (duration is used)
     * @param id the identifier to assign to the behavior node
     */
    private static void addEmphasisBehavior(CTTimeNodeList tnLst, int shapeId,
                                            PotAnimation animation, int id) {
        CTTLAnimateEffectBehavior animEffect = tnLst.addNewAnimEffect();
        CTTLCommonBehaviorData cBhvr = animEffect.addNewCBhvr();
        CTTLCommonTimeNodeData bhvrCtn = cBhvr.addNewCTn();
        bhvrCtn.setId(id);
        bhvrCtn.setDur(animation.getDuration());
        bhvrCtn.setFill(STTLTimeNodeFillType.HOLD);
        bindTargetShape(cBhvr, shapeId);
    }

    /**
     * Adds an animate motion behavior node for motion path animations.
     *
     * <p>This method creates an {@code animMotion} behavior node within the specified time node list.
     * It configures the common behavior data with the provided identifier, duration, and fill type,
     * and binds it to the target shape.</p>
     *
     * @param tnLst the time node list to add the behavior to
     * @param shapeId the identifier of the target shape
     * @param animation the animation configuration (duration is used)
     * @param id the identifier to assign to the behavior node
     */
    private static void addMotionBehavior(CTTimeNodeList tnLst, int shapeId,
                                          PotAnimation animation, int id) {
        CTTLAnimateMotionBehavior animMotion = tnLst.addNewAnimMotion();
        CTTLCommonBehaviorData cBhvr = animMotion.addNewCBhvr();
        CTTLCommonTimeNodeData bhvrCtn = cBhvr.addNewCTn();
        bhvrCtn.setId(id);
        bhvrCtn.setDur(animation.getDuration());
        bhvrCtn.setFill(STTLTimeNodeFillType.HOLD);

        bindTargetShape(cBhvr, shapeId);
    }

    /**
     * Calculates the next available identifier for a time node within the main sequence.
     *
     * <p>This method scans all parallel nodes under the main sequence's child time node list
     * to find the maximum identifier currently in use. It returns that maximum value plus one.</p>
     *
     * @param mainSeq the common time node data of the main sequence
     * @return the next available identifier, guaranteed to be unique within the scanned scope
     */
    private static int getNextId(CTTLCommonTimeNodeData mainSeq) {
        int maxId = 2;
        CTTimeNodeList childTnLst = mainSeq.getChildTnLst();
        if (childTnLst != null) {
            for (CTTLTimeNodeParallel par : childTnLst.getParArray()) {
                CTTLCommonTimeNodeData ctn = par.getCTn();
                if (ctn != null && ctn.isSetId()) {
                    maxId = Math.max(maxId, (int) ctn.getId());
                }
            }
        }
        return maxId + 1;
    }

    /**
     * Maps a PotAnimation effect to its corresponding OpenXML preset identifier.
     *
     * <p>This method returns the numeric preset identifier as defined by the ECMA-376 standard
     * (Part 1, 19.5.63, Table 1). The identifier is used within the presentation XML to specify
     * the built-in animation effect. The mapping covers entrance, exit, emphasis, and motion path effects.
     * For effects without a specific mapping, the default identifier 1 (typically "appear") is returned.
     *
     * <h3>Common Preset ID Mappings</h3>
     * <pre>
     * 1 = appear, 2 = fly, 10 = fade, 15 = randomBars, 16 = wheel, 17 = split,
     * 22 = wipe, 23 = zoom, 24 = expand, 25 = float, 26 = bounce, 27 = pinwheel,
     * 28 = riseUp, 29 = growAndTurn, 30 = centerRevolve
     * Emphasis: 1 = pulse, 6 = darken, 10 = growShrink, 11 = lighten,
     * 13 = teeter, 14 = shimmer, 15 = spin, 16 = transparency, 20 = flashOnce
     * </pre>
     *
     * @param animation The animation configuration containing the effect to map.
     * @return The integer preset identifier corresponding to the animation effect.
     */
    private static int getPresetId(PotAnimation animation) {
        switch (animation.getEffect()) {
            // Entrance/Exit effects
            case APPEAR:
            case DISAPPEAR:
                return 1;
            case FLY:
                return 2;
            case RANDOM_BARS:
                return 15;
            case FADE:
                return 10;
            case SPLIT:
                return 17;
            case WIPE:
                return 22;
            case ZOOM:
                return 23;
            case EXPAND:
                return 24;
            case FLOAT:
            case FLOAT_DOWN:
            case FLOAT_UP:
                return 25;
            case BOUNCE:
                return 26;
            case PINWHEEL:
                return 27;
            case RISE_UP:
                return 28;
            case GROW_AND_TURN:
                return 29;
            case CENTER_REVOLVE:
                return 30;
            case WHEEL:
                return 16;

            // Emphasis effects
            case PULSE:
                return 1;
            case GROW_SHRINK:
                return 10;
            case SPIN:
            case SPIN_EMPHASIS:
                return 15;
            case TRANSPARENCY:
                return 16;
            case DARKEN:
                return 6;
            case LIGHTEN:
                return 11;
            case FLASH_ONCE:
                return 20;
            case TEETER:
                return 13;
            case SHIMMER:
                return 14;
            case WAVE:
                return 17;

            // Motion path effects
            case PATH_LINE:
            case PATH_ARC:
            case PATH_TURN:
            case PATH_CIRCLE:
            case PATH_SQUARE:
            case PATH_DIAMOND:
            case PATH_HEXAGON:
            case PATH_CUSTOM:
                return 1;

            default:
                return 1;
        }
    }

    /**
     * Determines the OpenXML preset class enumeration for a given animation.
     *
     * <p>Based on the animation type (entrance, exit, emphasis, or motion path), this method returns
     * the corresponding standard preset class type constant. This class is used in the presentation XML
     * to categorize the animation effect.
     *
     * @param animation The animation configuration containing the type to map.
     * @return The STTLTimeNodePresetClassType enumeration value for the animation's type.
     */
    private static STTLTimeNodePresetClassType.Enum getPresetClass(PotAnimation animation) {
        switch (animation.getType()) {
            case ENTRANCE:
                return STTLTimeNodePresetClassType.ENTR;
            case EXIT:
                return STTLTimeNodePresetClassType.EXIT;
            case EMPHASIS:
                return STTLTimeNodePresetClassType.EMPH;
            case MOTION_PATH:
                return STTLTimeNodePresetClassType.PATH;
            default:
                return STTLTimeNodePresetClassType.ENTR;
        }
    }

    /**
     * Maps a PotDirection to its corresponding OpenXML preset subtype identifier.
     *
     * <p>This method converts the logical direction defined in the animation configuration into a
     * numeric subtype code used by the OpenXML format to specify the direction or orientation of
     * an animation effect (e.g., from left, from top, in, out). If no direction is set or the direction
     * does not map to a known subtype, 0 is returned.
     *
     * @param animation The animation configuration containing the direction to map.
     * @return The integer preset subtype code, or 0 if no mapping exists.
     */
    private static int getPresetSubtype(PotAnimation animation) {
        PotDirection direction = animation.getDirection();
        if (direction == null) {
            return 0;
        }

        switch (direction) {
            case FROM_LEFT:
            case LEFT:
                return 8;
            case FROM_RIGHT:
            case RIGHT:
                return 2;
            case FROM_TOP:
            case UP:
                return 1;
            case FROM_BOTTOM:
            case DOWN:
                return 4;
            case FROM_TOP_LEFT:
                return 9;
            case FROM_TOP_RIGHT:
                return 3;
            case FROM_BOTTOM_LEFT:
                return 12;
            case FROM_BOTTOM_RIGHT:
                return 6;
            case HORIZONTAL:
                return 10;
            case VERTICAL:
                return 5;
            case IN:
                return 16;
            case OUT:
                return 32;
            default:
                return 0;
        }
    }

    /**
     * Removes all animation nodes associated with a specific shape from the slide's timing list.
     *
     * <p>This method traverses the DOM tree of the provided time node list, identifies all parallel
     * animation nodes (`<p:par>`) that target the given shape ID, and removes them from their parent nodes.
     * The operation modifies the XML structure in-place.
     *
     * @param tnLst  The root time node list containing animation definitions.
     * @param shapeId The unique identifier of the shape whose animations should be removed.
     */
    private static void removeAnimationForShape(CTTimeNodeList tnLst, int shapeId) {
        Node root = tnLst.getDomNode();
        if (root == null) {
            return;
        }

        List<Node> toDelete = new ArrayList<>();
        collectAnimationContainersForShape(root, shapeId, toDelete);
        for (Node node : toDelete) {
            if (node.getParentNode() != null) {
                node.getParentNode().removeChild(node);
            }
        }
    }

    /**
     * Checks whether any animation targeting a specific shape exists within the slide's timing list.
     *
     * <p>This method performs a depth-first search through the DOM tree of the time node list,
     * looking for any element that contains a shape target (`<p:spTgt>`) with the specified ID.
     *
     * @param tnLst  The root time node list to search.
     * @param shapeId The unique identifier of the shape to check for.
     * @return {@code true} if at least one animation node targets the shape, {@code false} otherwise.
     */
    private static boolean hasAnimationForShape(CTTimeNodeList tnLst, int shapeId) {
        Node root = tnLst.getDomNode();
        return root != null && containsShapeTarget(root, shapeId);
    }

    /**
     * Binds a behavior node to a specific shape by adding a target element to its DOM.
     *
     * <p>This helper method creates a {@code <p:tgtEl>} element containing a {@code <p:spTgt>} element
     * with the given shape ID and appends it to the DOM node of the provided behavior data object.
     * This establishes the link between the animation behavior and the shape it should affect.
     *
     * @param behaviorData The common behavior data object to which the target will be attached.
     * @param shapeId      The unique identifier of the target shape.
     */
    private static void bindTargetShape(CTTLCommonBehaviorData behaviorData, int shapeId) {
        Node root = behaviorData.getDomNode();
        if (root == null) {
            return;
        }

        Element tgtEl = root.getOwnerDocument().createElementNS(
            "http://schemas.openxmlformats.org/presentationml/2006/main", "p:tgtEl");
        Element spTgt = root.getOwnerDocument().createElementNS(
            "http://schemas.openxmlformats.org/presentationml/2006/main", "p:spTgt");
        spTgt.setAttribute("spid", String.valueOf(shapeId));
        tgtEl.appendChild(spTgt);
        root.appendChild(tgtEl);
    }

    /**
     * Recursively searches a DOM node and its descendants for a shape target with a specific ID.
     *
     * <p>This method checks if the current node is an element with local name "spTgt" and if its
     * "spid" attribute matches the given shape ID. If not, it recursively searches all child nodes.
     *
     * @param root    The root DOM node to start the search from.
     * @param shapeId The shape identifier to match against "spid" attributes.
     * @return {@code true} if a matching {@code <p:spTgt>} element is found, {@code false} otherwise.
     */
    private static boolean containsShapeTarget(Node root, int shapeId) {
        if (root.getNodeType() == Node.ELEMENT_NODE && "spTgt".equals(root.getLocalName())) {
            String spid = ((Element) root).getAttribute("spid");
            if (String.valueOf(shapeId).equals(spid)) {
                return true;
            }
        }

        Node child = root.getFirstChild();
        while (child != null) {
            if (containsShapeTarget(child, shapeId)) {
                return true;
            }
            child = child.getNextSibling();
        }
        return false;
    }

    /**
     * Collects all parallel animation container nodes that target a specific shape.
     *
     * <p>This method traverses the DOM tree starting from the given root node. When it encounters
     * an element node with local name "par" that contains a shape target matching the specified ID,
     * it adds that node to the provided collector list. The search continues through all descendants.
     *
     * @param root     The root DOM node to start the traversal from.
     * @param shapeId  The shape identifier to match.
     * @param collector A list to which matching "par" nodes will be added.
     */
    private static void collectAnimationContainersForShape(Node root, int shapeId, List<Node> collector) {
        if (root.getNodeType() == Node.ELEMENT_NODE && "par".equals(root.getLocalName())
            && containsShapeTarget(root, shapeId)) {
            collector.add(root);
            return;
        }

        Node child = root.getFirstChild();
        while (child != null) {
            collectAnimationContainersForShape(child, shapeId, collector);
            child = child.getNextSibling();
        }
    }

    /**
     * Finds the first animation time node element associated with a specific shape.
     *
     * <p>This method searches the provided time node list for the first common time node element
     * ({@code <p:cTn>}) that contains a target for the given shape ID. It returns the corresponding
     * DOM Element if found, or {@code null} if no such node exists.
     *
     * @param tnLst   The root time node list to search.
     * @param shapeId The shape identifier to match.
     * @return The first matching {@code <p:cTn>} Element, or {@code null}.
     */
    private static Element findFirstAnimationNodeForShape(CTTimeNodeList tnLst, int shapeId) {
        Node root = tnLst.getDomNode();
        if (root == null) {
            return null;
        }
        return findFirstAnimationNodeForShape(root, shapeId);
    }

    /**
     * Recursively searches for the first common time node element targeting a specific shape.
     *
     * <p>This helper method traverses the DOM tree starting from the given root node. It returns
     * the first element with local name "cTn" that contains a shape target matching the specified ID.
     *
     * @param root    The root DOM node to start the search from.
     * @param shapeId The shape identifier to match.
     * @return The first matching {@code <p:cTn>} Element, or {@code null}.
     */
    private static Element findFirstAnimationNodeForShape(Node root, int shapeId) {
        if (root.getNodeType() == Node.ELEMENT_NODE && "cTn".equals(root.getLocalName())
            && containsShapeTarget(root, shapeId)) {
            return (Element) root;
        }

        Node child = root.getFirstChild();
        while (child != null) {
            Element found = findFirstAnimationNodeForShape(child, shapeId);
            if (found != null) {
                return found;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * Reconstructs a PotAnimation object from an OpenXML common time node element.
     *
     * <p>This method parses the "presetClass" and "presetID" attributes of the given DOM element
     * to infer the original animation type and effect. It returns a corresponding PotAnimation
     * instance with default parameters. The mapping is basic and intended for common built-in effects;
     * complex custom animations may not be fully reconstructed.
     *
     * @param ctn The DOM Element representing a {@code <p:cTn>} node from the presentation XML.
     * @return A PotAnimation instance approximating the animation defined in the node, or a default fade-in animation if mapping fails.
     */
    private static PotAnimation mapAnimationFromNode(Element ctn) {
        String presetClass = ctn.getAttribute("presetClass");
        String presetId = ctn.getAttribute("presetID");

        if ("entr".equalsIgnoreCase(presetClass)) {
            if ("10".equals(presetId)) return PotAnimation.fadeIn();
            if ("2".equals(presetId)) return PotAnimation.flyIn();
            if ("23".equals(presetId)) return PotAnimation.zoomIn();
            return PotAnimation.appear();
        }
        if ("exit".equalsIgnoreCase(presetClass)) {
            if ("10".equals(presetId)) return PotAnimation.fadeOut();
            return PotAnimation.exit(PotAnimation.Effect.DISAPPEAR);
        }
        if ("emph".equalsIgnoreCase(presetClass)) {
            return PotAnimation.pulse();
        }

        return PotAnimation.fadeIn();
    }
}
