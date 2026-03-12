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
import org.apache.xmlbeans.XmlObject;

/**
 * Provides utility methods for applying and removing hyperlink actions on PowerPoint shapes.
 *
 * <p>This helper class encapsulates the logic for manipulating hyperlink actions (both click and hover)
 * within PowerPoint presentation shapes. It handles the generation of appropriate XML fragments
 * according to the DrawingML and PresentationML namespaces and integrates them into the shape's
 * underlying XML structure. The class supports various action types including slide navigation,
 * program execution, macro invocation, and sound control.</p>
 *
 * <p>All methods in this class are static and the constructor is private to prevent instantiation,
 * following the utility class pattern. Operations that modify the XML structure may throw
 * {@code PotException} if the underlying XML manipulation fails.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class ActionHelper {

    private static final String DRAWINGML_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    private static final String P_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    private static final String A_DECL = "declare namespace a='" + DRAWINGML_NS + "'; ";
    private static final String P_DECL = "declare namespace p='" + P_NS + "'; ";

    /**
     * Prevents instantiation of this utility class.
     *
     * @throws AssertionError always thrown when attempted instantiation occurs
     */
    private ActionHelper() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Applies a specified action to a PowerPoint shape.
     *
     * <p>This method inserts the XML representation of the given action into the shape's
     * underlying XML structure. The action is inserted as a child of the {@code p:cNvSpPr}
     * element within the shape's XML. The method handles both click and hover actions
     * based on the configuration in the {@code PotAction} object. If either the shape
     * or action parameter is null, the method returns without performing any operation.</p>
     *
     * <p>The method uses XPath to locate the insertion point and delegates XML construction
     * to {@link #buildActionXml(PotAction)}. Any exception during XML manipulation is caught,
     * logged as a warning, and rethrown as a {@code PotException} with appropriate context
     * information.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.createAutoShape();
     * PotAction action = new PotAction(PotActionType.GO_TO_SLIDE, "3", false);
     * ActionHelper.applyAction(shape, action);
     * }</pre>
     *
     * @param shape the PowerPoint shape to which the action will be applied
     * @param action the action configuration containing type, target, and trigger information
     * @throws PotException if the XML manipulation fails during action application
     * @see #buildActionXml(PotAction)
     * @see XmlUtils#insertXml(XmlObject, String, String, XmlUtils.InsertPosition)
     */
    static void applyAction(XSLFShape shape, PotAction action) {
        if (shape == null || action == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            String xpath = P_DECL + "$this//p:cNvSpPr";
            String actionXml = buildActionXml(action);
            if (!actionXml.isEmpty()) {
                XmlUtils.insertXml(xmlObject, xpath, actionXml, XmlUtils.InsertPosition.CHILD);
            }
        } catch (Exception e) {
            PotLogger.warn(ActionHelper.class, "applyAction",
                "Failed to apply action: " + action.getType(), e);
            throw new PotException("Failed to apply action: " + action.getType(), e,
                PotException.ErrorCode.ACTION_ERROR)
                .withContext("actionType", action.getType())
                .withContext("target", action.getTarget());
        }
    }

    /**
     * Constructs the XML fragment for a given action.
     *
     * <p>This method generates the appropriate XML string representation of a hyperlink action
     * based on the action type, target, and trigger configuration. The XML conforms to the
     * DrawingML namespace and uses either {@code a:hlinkClick} or {@code a:hlinkHover} as the
     * root element depending on whether the action is triggered on hover. The action value
     * is constructed according to PowerPoint's internal action URI scheme.</p>
     *
     * <p>For action types that require a target (such as {@code GO_TO_SLIDE}, {@code RUN_PROGRAM},
     * etc.), the method returns an empty string if the target is null. OLE-related actions
     * ({@code OLE_ACTIVATE}, {@code OLE_EDIT}, {@code OLE_OPEN}) are currently not implemented
     * and return an empty string.</p>
     *
     * @param action the action configuration for which to generate XML
     * @return the XML fragment as a string, or an empty string if no XML should be inserted
     * @see #buildActionTag(String, String)
     */
    private static String buildActionXml(PotAction action) {
        PotActionType type = action.getType();
        String target = action.getTarget();
        boolean isHover = action.isOnHover();

        String triggerTag = isHover ? "a:hlinkHover" : "a:hlinkClick";

        switch (type) {
            case GO_TO_SLIDE:
                if (target != null) {
                    return buildActionTag(triggerTag,
                        "ppaction://hlinkshowjump?jump=slide" + target);
                }
                break;

            case GO_TO_FIRST:
                return buildActionTag(triggerTag, "ppaction://hlinkshowjump?jump=firstslide");

            case GO_TO_LAST:
                return buildActionTag(triggerTag, "ppaction://hlinkshowjump?jump=lastslide");

            case GO_TO_NEXT:
                return buildActionTag(triggerTag, "ppaction://hlinkshowjump?jump=nextslide");

            case GO_TO_PREVIOUS:
                return buildActionTag(triggerTag, "ppaction://hlinkshowjump?jump=previousslide");

            case END_SHOW:
                return buildActionTag(triggerTag, "ppaction://hlinkshowjump?jump=endshow");

            case RUN_PROGRAM:
                if (target != null) {
                    return buildActionTag(triggerTag, "ppaction://run?file=" + target);
                }
                break;

            case RUN_MACRO:
                if (target != null) {
                    return buildActionTag(triggerTag, "ppaction://macro?name=" + target);
                }
                break;

            case PLAY_SOUND:
                if (target != null) {
                    return buildActionTag(triggerTag, "ppaction://sound?file=" + target);
                }
                break;

            case STOP_SOUND:
                return buildActionTag(triggerTag, "ppaction://stopsound");

            case OLE_ACTIVATE:
            case OLE_EDIT:
            case OLE_OPEN:
                // OLE 
                break;
        }

        return "";
    }

    /**
     * Builds a complete XML tag for a hyperlink action.
     *
     * <p>This helper method formats the trigger tag with the DrawingML namespace declaration
     * and the escaped action value. The resulting XML tag is self-closing and ready for
     * insertion into the shape's XML structure.</p>
     *
     * @param triggerTag the XML element name, either "a:hlinkClick" or "a:hlinkHover"
     * @param actionValue the action URI value to be included in the 'action' attribute
     * @return a complete XML tag string with namespace declaration and escaped attribute value
     * @see XmlUtils#escapeXml(String)
     */
    private static String buildActionTag(String triggerTag, String actionValue) {
        return String.format("<%s xmlns:a='%s' action='%s'/>",
            triggerTag, DRAWINGML_NS, XmlUtils.escapeXml(actionValue));
    }

    /**
     * Removes either click or hover actions from a PowerPoint shape.
     *
     * <p>This method deletes all hyperlink action elements of the specified type from the
     * shape's underlying XML structure. It uses XPath to locate either {@code a:hlinkClick}
     * or {@code a:hlinkHover} elements within the shape's XML and removes them. If the shape
     * parameter is null, the method returns without performing any operation.</p>
     *
     * <p>Any exception during XML manipulation is caught, logged as a warning, and rethrown
     * as a {@code PotException} with context indicating whether hover or click actions
     * were being removed.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.getShapes().get(0);
     * // Remove click actions only
     * ActionHelper.removeAction(shape, false);
     * // Remove hover actions only
     * ActionHelper.removeAction(shape, true);
     * }</pre>
     *
     * @param shape the PowerPoint shape from which actions will be removed
     * @param isHover if true, removes hover actions; if false, removes click actions
     * @throws PotException if the XML manipulation fails during action removal
     * @see XmlUtils#deleteNodes(XmlObject, String)
     */
    static void removeAction(XSLFShape shape, boolean isHover) {
        if (shape == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            String xpath = isHover
                ? A_DECL + "$this//a:hlinkHover"
                : A_DECL + "$this//a:hlinkClick";
            XmlUtils.deleteNodes(xmlObject, xpath);
        } catch (Exception e) {
            PotLogger.warn(ActionHelper.class, "removeAction",
                "Failed to remove " + (isHover ? "hover" : "click") + " action", e);
            throw new PotException("Failed to remove " + (isHover ? "hover" : "click") + " action", e,
                PotException.ErrorCode.ACTION_ERROR)
                .withContext("isHover", isHover);
        }
    }
}