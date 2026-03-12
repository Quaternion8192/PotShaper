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
import org.apache.xmlbeans.XmlObject;

/**
 * A utility class providing helper methods for configuring media properties within PowerPoint shapes.
 *
 * <p>This class operates on {@link XSLFPictureShape} objects that contain embedded media (e.g., audio or video).
 * It manipulates the underlying XML structure of the presentation to set various playback and display attributes
 * such as auto-play, looping, volume, and trimming. All methods are static and handle null shapes or invalid
 * parameters gracefully by returning early. Any XML processing errors are logged as warnings and do not propagate
 * exceptions to the caller.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * XSLFPictureShape mediaShape = ...; // obtain a shape containing media
 * MediaHelper.setAutoPlay(mediaShape, true);
 * MediaHelper.setVolume(mediaShape, 0.75);
 * MediaHelper.setLoop(mediaShape, false);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class MediaHelper {

    private static final String P_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    private static final String MEDIA_NODE_XPATH =
        "declare namespace p='" + P_NS + "'; .//p:cMediaNode";

    /**
     * Suppresses default constructor to prevent instantiation.
     *
     * <p>This is a utility class intended for static use only. Calling this constructor will throw an
     * {@link AssertionError}.</p>
     *
     * @throws AssertionError always thrown when an attempt is made to instantiate this class.
     */
    private MediaHelper() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Enables or disables automatic playback for the media contained in the specified shape.
     *
     * <p>This method sets the {@code autoPlay} attribute on the media node within the shape's XML.
     * If the shape is {@code null}, the method returns without performing any action. If an error occurs
     * during XML manipulation, a warning is logged and no exception is thrown.</p>
     *
     * @param shape     the picture shape containing the media; may be {@code null}.
     * @param autoPlay  {@code true} to enable automatic playback, {@code false} to disable it.
     */
    static void setAutoPlay(XSLFPictureShape shape, boolean autoPlay) {
        if (shape == null) return;
        try {
            //  XML  autoPlay 
            XmlObject xmlObject = shape.getXmlObject();
            String attr = autoPlay ? "1" : "0";
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "autoPlay", attr);
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setAutoPlay",
                "Failed to set media autoPlay", e);
        }
    }

    /**
     * Enables or disables looping playback for the media contained in the specified shape.
     *
     * <p>This method sets the {@code loop} attribute on the media node within the shape's XML.
     * If the shape is {@code null}, the method returns without performing any action. If an error occurs
     * during XML manipulation, a warning is logged and no exception is thrown.</p>
     *
     * @param shape the picture shape containing the media; may be {@code null}.
     * @param loop  {@code true} to enable looping, {@code false} to disable it.
     */
    static void setLoop(XSLFPictureShape shape, boolean loop) {
        if (shape == null) return;
        try {
            XmlObject xmlObject = shape.getXmlObject();
            String attr = loop ? "1" : "0";
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "loop", attr);
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setLoop",
                "Failed to set media loop", e);
        }
    }

    /**
     * Sets the playback volume for the media contained in the specified shape.
     *
     * <p>This method sets the {@code vol} attribute on the media node within the shape's XML.
     * The provided volume value is scaled from the range 0.0 to 1.0 to the internal representation
     * of 0 to 100000. If the shape is {@code null} or the volume is outside the valid range,
     * the method returns without performing any action. If an error occurs during XML manipulation,
     * a warning is logged and no exception is thrown.</p>
     *
     * @param shape  the picture shape containing the media; may be {@code null}.
     * @param volume the volume level between 0.0 (silent) and 1.0 (maximum). Values outside this range are ignored.
     */
    static void setVolume(XSLFPictureShape shape, double volume) {
        if (shape == null || volume < 0 || volume > 1) return;
        try {
            XmlObject xmlObject = shape.getXmlObject();
            // POI  0-100000 0.0-1.0
            int volumeValue = (int) (volume * 100000);
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "vol", String.valueOf(volumeValue));
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setVolume",
                "Failed to set media volume", e);
        }
    }

    /**
     * Enables or disables fullscreen playback for the media contained in the specified shape.
     *
     * <p>This method sets the {@code fullScrn} attribute on the media node within the shape's XML.
     * If the shape is {@code null}, the method returns without performing any action. If an error occurs
     * during XML manipulation, a warning is logged and no exception is thrown.</p>
     *
     * @param shape      the picture shape containing the media; may be {@code null}.
     * @param fullScreen {@code true} to enable fullscreen playback, {@code false} to disable it.
     */
    static void setFullScreen(XSLFPictureShape shape, boolean fullScreen) {
        if (shape == null) return;
        try {
            XmlObject xmlObject = shape.getXmlObject();
            String attr = fullScreen ? "1" : "0";
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "fullScrn", attr);
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setFullScreen",
                "Failed to set media fullScreen", e);
        }
    }

    /**
     * Shows or hides the media icon when playback is stopped.
     *
     * <p>This method sets the {@code showWhenStopped} attribute on the media node within the shape's XML.
     * A value of {@code "1"} indicates the icon is shown when stopped; {@code "0"} hides it.
     * If the shape is {@code null}, the method returns without performing any action. If an error occurs
     * during XML manipulation, a warning is logged and no exception is thrown.</p>
     *
     * @param shape    the picture shape containing the media; may be {@code null}.
     * @param hideIcon {@code true} to hide the icon when stopped, {@code false} to show it.
     */
    static void setHideIcon(XSLFPictureShape shape, boolean hideIcon) {
        if (shape == null) return;
        try {
            XmlObject xmlObject = shape.getXmlObject();
            String attr = hideIcon ? "1" : "0";
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "showWhenStopped", attr);
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setHideIcon",
                "Failed to set media hideIcon", e);
        }
    }

    /**
     * Marks the media shape as having a poster frame.
     *
     * <p>This method inserts a {@code <p:hlinkHover>} element with the {@code posterFrame} attribute set to "1"
     * as a child of the media node. The actual image data is not stored by this method; it only sets the flag
     * indicating that a poster frame exists. If the shape or image data is {@code null}, the method returns
     * without performing any action. If an error occurs during XML manipulation, a warning is logged and no
     * exception is thrown.</p>
     *
     * @param shape        the picture shape containing the media; may be {@code null}.
     * @param imageData    the byte array containing the poster frame image data; ignored in this implementation.
     * @param contentType  the MIME type of the image data; ignored in this implementation.
     */
    static void setPosterFrame(XSLFPictureShape shape, byte[] imageData, String contentType) {
        if (shape == null || imageData == null) return;
        try {
            XmlObject xmlObject = shape.getXmlObject();
            XmlUtils.insertXml(xmlObject, MEDIA_NODE_XPATH,
                "<p:hlinkHover xmlns:p='" + P_NS + "' posterFrame='1'/>", XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setPosterFrame",
                "Failed to set media poster frame", e);
        }
    }

    /**
     * Sets the start and end trim points for the media contained in the specified shape.
     *
     * <p>This method sets the {@code st} (start) and {@code end} attributes on the media node within the shape's XML.
     * The provided time values in seconds are converted to milliseconds for storage. If the shape is {@code null},
     * the start time is negative, or the end time is less than the start time, the method returns without performing
     * any action. If an error occurs during XML manipulation, a warning is logged and no exception is thrown.</p>
     *
     * @param shape        the picture shape containing the media; may be {@code null}.
     * @param startSeconds the start time of the media segment in seconds; must be nonnegative.
     * @param endSeconds   the end time of the media segment in seconds; must be greater than or equal to startSeconds.
     */
    static void setTrim(XSLFPictureShape shape, double startSeconds, double endSeconds) {
        if (shape == null || startSeconds < 0 || endSeconds < startSeconds) return;
        try {
            long startMs = (long) (startSeconds * 1000);
            long endMs = (long) (endSeconds * 1000);

            XmlObject xmlObject = shape.getXmlObject();
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "st", String.valueOf(startMs));
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "end", String.valueOf(endMs));
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setTrim",
                "Failed to set media trim range", e);
        }
    }

    /**
     * Sets the fadein duration for the media contained in the specified shape.
     *
     * <p>This method sets the {@code fadeIn} attribute on the media node within the shape's XML.
     * The provided duration in seconds is converted to milliseconds for storage. If the shape is {@code null}
     * or the duration is negative, the method returns without performing any action. If an error occurs during
     * XML manipulation, a warning is logged and no exception is thrown.</p>
     *
     * @param shape   the picture shape containing the media; may be {@code null}.
     * @param seconds the fadein duration in seconds; must be nonnegative.
     */
    static void setFadeIn(XSLFPictureShape shape, double seconds) {
        if (shape == null || seconds < 0) return;
        try {
            long ms = (long) (seconds * 1000);
            XmlObject xmlObject = shape.getXmlObject();
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "fadeIn", String.valueOf(ms));
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setFadeIn",
                "Failed to set media fadeIn duration", e);
        }
    }

    /**
     * Sets the fadeout duration for the media contained in the specified shape.
     *
     * <p>This method sets the {@code fadeOut} attribute on the media node within the shape's XML.
     * The provided duration in seconds is converted to milliseconds for storage. If the shape is {@code null}
     * or the duration is negative, the method returns without performing any action. If an error occurs during
     * XML manipulation, a warning is logged and no exception is thrown.</p>
     *
     * @param shape   the picture shape containing the media; may be {@code null}.
     * @param seconds the fadeout duration in seconds; must be nonnegative.
     */
    static void setFadeOut(XSLFPictureShape shape, double seconds) {
        if (shape == null || seconds < 0) return;
        try {
            long ms = (long) (seconds * 1000);
            XmlObject xmlObject = shape.getXmlObject();
            XmlUtils.setAttribute(xmlObject, MEDIA_NODE_XPATH, "fadeOut", String.valueOf(ms));
        } catch (Exception e) {
            PotLogger.warn(MediaHelper.class, "setFadeOut",
                "Failed to set fadeOut duration", e);
        }
    }
}