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

import org.apache.poi.xslf.usermodel.*;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.*;

import java.util.*;

/**
 * Provides low-level XML access and manipulation for PowerPoint presentation components.
 *
 * <p>This class serves as a bridge to the underlying XML representation of a presentation,
 * allowing direct retrieval, querying, and patching of XML content for the entire presentation,
 * individual slides, slide masters, and shapes. It wraps Apache POI's XML layer with
 * convenience methods and consistent error handling.</p>
 *
 * <h3>Obtaining a PotXml Instance</h3>
 * <pre>{@code
 * PotXml xml = ppt.xml();
 * }</pre>
 *
 * <h3>Retrieving XML Content</h3>
 * <pre>{@code
 * // Get the entire presentation's XML
 * String presentationXml = xml.getPresentationXml();
 *
 * // Get XML for a specific slide by index
 * String slideXml = xml.getSlideXml(0);
 *
 * // Get XML for a specific shape
 * PotElement element = slide.getElements().get(0);
 * String shapeXml = xml.getShapeXml(element);
 * }</pre>
 *
 * <h3>Modifying XML Content</h3>
 * <pre>{@code
 * // Patch a slide using an XPath expression
 * xml.patchSlide(0, "//p:sp/p:spPr/a:solidFill/a:srgbClr/@val", "FF0000");
 *
 * // Replace a shape's entire XML fragment
 * xml.replaceShapeXml(element, newXmlFragment);
 * }</pre>
 *
 * <p><b>Note:</b> Direct XML manipulation can corrupt the presentation if invalid XML is used.
 * Use validation methods where appropriate.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotXml {

    // ==================== Fields ====================

    /** The parent presentation instance. */
    private final PotPresentation presentation;

    /** The underlying Apache POI XMLSlideShow object. */
    private final XMLSlideShow slideShow;

    // ==================== Constructors ====================

    /**
     * Constructs a new XML accessor for the specified presentation.
     *
     * @param presentation the parent presentation, must not be null
     */
    PotXml(PotPresentation presentation) {
        this.presentation = Objects.requireNonNull(presentation, "presentation cannot be null");
        this.slideShow = presentation.getRawSlideShow();
    }

    // ==================== XML Retrieval ====================

    /**
     * Returns the complete XML representation of the entire presentation.
     *
     * <p>This method serializes the root presentation element, including all slides,
     * masters, and document-level properties, into a formatted XML string.</p>
     *
     * @return the presentation XML as a formatted string
     * @throws PotException if an error occurs during XML serialization
     */
    public String getPresentationXml() {
        try {
            CTPresentation ctPresentation = slideShow.getCTPresentation();
            return ctPresentation.xmlText(XmlUtils.getXmlOptions());
        } catch (Exception e) {
            throw PotException.xmlError("getting presentation XML", e);
        }
    }

    /**
     * Returns the XML representation of a specific slide by its index.
     *
     * <p>The slide index is zero-based. The returned XML includes the slide's
     * properties, layout reference, and all contained shapes.</p>
     *
     * @param slideIndex the zero-based index of the slide
     * @return the slide XML as a formatted string
     * @throws PotException if the slide index is invalid or an XML error occurs
     */
    public String getSlideXml(int slideIndex) {
        validateSlideIndex(slideIndex);
        try {
            XSLFSlide slide = slideShow.getSlides().get(slideIndex);
            CTSlide ctSlide = slide.getXmlObject();
            return ctSlide.xmlText(XmlUtils.getXmlOptions());
        } catch (Exception e) {
            throw PotException.xmlError("getting slide XML", e);
        }
    }

    /**
     * Returns the XML representation of the specified slide.
     *
     * <p>This is a convenience method that delegates to the underlying POI slide object.
     * If the provided slide is {@code null}, this method returns {@code null}.</p>
     *
     * @param slide the slide instance, may be {@code null}
     * @return the slide XML as a formatted string, or {@code null} if the input is {@code null}
     * @throws PotException if an XML serialization error occurs
     */
    public String getSlideXml(PotSlide slide) {
        if (slide == null) return null;
        try {
            CTSlide ctSlide = slide.getRawSlide().getXmlObject();
            return ctSlide.xmlText(XmlUtils.getXmlOptions());
        } catch (Exception e) {
            throw PotException.xmlError("getting slide XML", e);
        }
    }

    /**
     * Returns the XML representation of the specified shape element.
     *
     * <p>The shape XML includes its geometry, formatting, text, and other properties.
     * If the provided element is {@code null}, this method returns {@code null}.</p>
     *
     * @param element the shape element, may be {@code null}
     * @return the shape XML as a formatted string, or {@code null} if the input is {@code null}
     * @throws PotException if an XML serialization error occurs
     */
    public String getShapeXml(PotElement element) {
        if (element == null) return null;
        try {
            XSLFShape shape = element.getRawShape();
            XmlObject xmlObject = shape.getXmlObject();
            return xmlObject.xmlText(XmlUtils.getXmlOptions());
        } catch (Exception e) {
            throw PotException.xmlError("getting shape XML", e);
        }
    }

    /**
     * Returns the XML representation of a specific slide master by its index.
     *
     * <p>The master index is zero-based. Slide masters define the default layout and
     * formatting for slides that use them.</p>
     *
     * @param masterIndex the zero-based index of the slide master
     * @return the master XML as a formatted string
     * @throws PotException if the master index is invalid or an XML error occurs
     */
    public String getMasterXml(int masterIndex) {
        List<XSLFSlideMaster> masters = slideShow.getSlideMasters();
        if (masterIndex < 0 || masterIndex >= masters.size()) {
            throw PotException.invalidParameter("masterIndex", "out of range");
        }
        try {
            XSLFSlideMaster master = masters.get(masterIndex);
            return master.getXmlObject().xmlText(XmlUtils.getXmlOptions());
        } catch (Exception e) {
            throw PotException.xmlError("getting master XML", e);
        }
    }

    // ==================== XML Patching ====================

    /**
     * Patches a slide's XML by setting the value of nodes matching an XPath expression.
     *
     * <p>The XPath expression is evaluated against the slide's XML. If matching nodes are found,
     * their values are updated to the specified string. The change is applied directly to the
     * underlying POI model.</p>
     *
     * @param slideIndex the zero-based index of the slide to patch
     * @param xpath      the XPath expression to locate target nodes
     * @param value      the new value to set
     * @return {@code true} if the patch was successfully applied, {@code false} otherwise
     * @throws PotException if the slide index is invalid or an XML processing error occurs
     */
    public boolean patchSlide(int slideIndex, String xpath, String value) {
        validateSlideIndex(slideIndex);
        try {
            XSLFSlide slide = slideShow.getSlides().get(slideIndex);
            CTSlide ctSlide = slide.getXmlObject();
            return XmlUtils.patchXml(ctSlide, xpath, value);
        } catch (Exception e) {
            throw PotException.xmlError("patching slide XML", e);
        }
    }

    /**
     * Patches a shape's XML by setting the value of nodes matching an XPath expression.
     *
     * <p>The XPath expression is evaluated against the shape's XML. If the element is
     * {@code null} or no matching nodes are found, the method returns {@code false}.</p>
     *
     * @param element the shape element to patch, may be {@code null}
     * @param xpath   the XPath expression to locate target nodes
     * @param value   the new value to set
     * @return {@code true} if the patch was successfully applied, {@code false} otherwise
     * @throws PotException if an XML processing error occurs
     */
    public boolean patchShape(PotElement element, String xpath, String value) {
        if (element == null) return false;
        try {
            XmlObject xmlObject = element.getRawShape().getXmlObject();
            return XmlUtils.patchXml(xmlObject, xpath, value);
        } catch (Exception e) {
            throw PotException.xmlError("patching shape XML", e);
        }
    }

    /**
     * Replaces the entire XML content of a shape with a new XML fragment.
     *
     * <p>The provided XML string must be a valid XML fragment that can replace the
     * shape's existing XML structure. If either parameter is {@code null}, the method
     * returns {@code false}.</p>
     *
     * @param element    the shape element to replace, may be {@code null}
     * @param xmlContent the new XML content as a string, may be {@code null}
     * @return {@code true} if the replacement was successful, {@code false} otherwise
     * @throws PotException if an XML processing error occurs
     */
    public boolean replaceShapeXml(PotElement element, String xmlContent) {
        if (element == null || xmlContent == null) return false;
        try {
            return XmlUtils.replaceShapeXml(element.getRawShape(), xmlContent);
        } catch (Exception e) {
            throw PotException.xmlError("replacing shape XML", e);
        }
    }

    // ==================== XML Querying ====================

    /**
     * Queries a slide's XML using an XPath expression and returns matching values.
     *
     * <p>The XPath expression is evaluated against the slide's XML. The returned list
     * contains the string values of all matching nodes, in document order. If no nodes
     * match, an empty list is returned.</p>
     *
     * @param slideIndex the zero-based index of the slide to query
     * @param xpath      the XPath expression to evaluate
     * @return a list of matching node values, never {@code null}
     * @throws PotException if the slide index is invalid or an XML processing error occurs
     */
    public List<String> querySlide(int slideIndex, String xpath) {
        validateSlideIndex(slideIndex);
        try {
            XSLFSlide slide = slideShow.getSlides().get(slideIndex);
            CTSlide ctSlide = slide.getXmlObject();
            return XmlUtils.queryXml(ctSlide, xpath);
        } catch (Exception e) {
            throw PotException.xmlError("querying slide XML", e);
        }
    }

    /**
     * Checks whether any nodes in a slide match a given XPath expression.
     *
     * <p>This is a convenience method that performs a query and checks if the result is non-empty.</p>
     *
     * @param slideIndex the zero-based index of the slide to check
     * @param xpath      the XPath expression to evaluate
     * @return {@code true} if at least one node matches the XPath, {@code false} otherwise
     * @throws PotException if the slide index is invalid or an XML processing error occurs
     */
    public boolean existsInSlide(int slideIndex, String xpath) {
        List<String> results = querySlide(slideIndex, xpath);
        return results != null && !results.isEmpty();
    }

    // ==================== Batch Operations ====================

    /**
     * Applies multiple XML patches to a single slide in a single operation.
     *
     * <p>Each entry in the map represents an XPath expression and its corresponding new value.
     * The patches are applied sequentially. The method returns the number of successfully
     * applied patches.</p>
     *
     * @param slideIndex  the zero-based index of the slide to patch
     * @param patchGroups a map of XPath expressions to new values
     * @return the number of patches successfully applied
     * @throws PotException if the slide index is invalid or an XML processing error occurs
     */
    public int batchPatchSlide(int slideIndex, Map<String, String> patchGroups) {
        if (patchGroups == null || patchGroups.isEmpty()) return 0;
        
        int count = 0;
        for (Map.Entry<String, String> entry : patchGroups.entrySet()) {
            if (patchSlide(slideIndex, entry.getKey(), entry.getValue())) {
                count++;
            }
        }
        return count;
    }

    // ==================== Validation ====================

    /**
     * Validates an arbitrary XML string for well-formedness.
     *
     * <p>This method checks basic XML syntax but does not validate against a schema.
     * It is useful for checking user-provided XML before attempting to insert it.</p>
     *
     * @param xmlContent the XML string to validate
     * @return {@code true} if the XML is well-formed, {@code false} otherwise
     */
    public boolean validateXml(String xmlContent) {
        return XmlUtils.validateXml(xmlContent);
    }

    /**
     * Validates the XML structure of a specific slide.
     *
     * <p>This method performs internal consistency checks on the slide's XML representation.
     * If validation passes, {@code null} is returned. Otherwise, a descriptive error message
     * is returned.</p>
 *
     * @param slideIndex the zero-based index of the slide to validate
     * @return {@code null} if the slide XML is valid, otherwise an error message string
     * @throws PotException if the slide index is invalid
     */
    public String validateSlideXml(int slideIndex) {
        validateSlideIndex(slideIndex);
        try {
            XSLFSlide slide = slideShow.getSlides().get(slideIndex);
            CTSlide ctSlide = slide.getXmlObject();
            return XmlUtils.validateCTSlide(ctSlide);
        } catch (Exception e) {
            return "Validation error: " + e.getMessage();
        }
    }

    // ==================== Utilities ====================

    /**
     * Returns a map of namespace prefixes to URIs commonly used in PowerPoint XML.
     *
     * <p>This map is suitable for use with XPath expressions that require namespace context.
     * The prefixes include "a" for drawingML, "r" for relationships, "p" for presentationML,
     * and others.</p>
     *
     * @return a map from namespace prefix to URI, in a stable iteration order
     */
    public Map<String, String> getNamespaces() {
        Map<String, String> ns = new LinkedHashMap<>();
        ns.put("a", "http://schemas.openxmlformats.org/drawingml/2006/main");
        ns.put("r", "http://schemas.openxmlformats.org/officeDocument/2006/relationships");
        ns.put("p", "http://schemas.openxmlformats.org/presentationml/2006/main");
        ns.put("mc", "http://schemas.openxmlformats.org/markup-compatibility/2006");
        ns.put("p14", "http://schemas.microsoft.com/office/powerpoint/2010/main");
        ns.put("p15", "http://schemas.microsoft.com/office/powerpoint/2012/main");
        return ns;
    }

    // ==================== Formatting ====================

    /**
     * Formats an XML string with consistent indentation and line breaks.
     *
     * <p>This method applies standard pretty-printing to make XML human-readable.
     * It is useful for debugging or logging XML content retrieved from other methods.</p>
     *
     * @param xmlContent the XML string to format
     * @return the formatted XML string, or the original string if formatting fails
     */
    public String formatXml(String xmlContent) {
        return XmlUtils.formatXml(xmlContent);
    }

    // ==================== Internal Helpers ====================

    /**
     * Validates that a slide index is within the bounds of the presentation.
     *
     * @param slideIndex the index to validate
     * @throws PotException if the index is out of bounds
     */
    private void validateSlideIndex(int slideIndex) {
        int count = slideShow.getSlides().size();
        if (slideIndex < 0 || slideIndex >= count) {
            throw PotException.slideIndexOutOfBounds(slideIndex, count);
        }
    }

    // ==================== Object Overrides ====================

    /**
     * Returns a string representation of this XML accessor.
     *
     * @return a string describing the number of slides in the underlying presentation
     */
    @Override
    public String toString() {
        return String.format("PotXml{slides=%d}", slideShow.getSlides().size());
    }
}