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

import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides utility methods for XML manipulation and processing within PowerPoint documents.
 *
 * <p>This class handles operations such as parsing, querying, modifying, and serializing XML
 * content that constitutes shapes, slides, and other elements in a presentation. It includes
 * support for XPath queries, namespace management, and safe XML processing with protection
 * against XML External Entity (XXE) attacks.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class XmlUtils {

    /** A map of namespace prefixes to their URIs used in OpenXML documents. */
    private static final Map<String, String> NAMESPACE_MAP = new HashMap<>();

    private static final String DRAWINGML_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    private static final String RELATIONSHIP_NS = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";

    static {
        // DrawingML namespace for drawing elements.
        NAMESPACE_MAP.put("a", "http://schemas.openxmlformats.org/drawingml/2006/main");
        // PresentationML namespace for presentation elements.
        NAMESPACE_MAP.put("p", "http://schemas.openxmlformats.org/presentationml/2006/main");
        // Relationships namespace for relationship identifiers.
        NAMESPACE_MAP.put("r", "http://schemas.openxmlformats.org/officeDocument/2006/relationships");
        // Office namespace for Microsoft Office extensions.
        NAMESPACE_MAP.put("o", "urn:schemas-microsoft-com:office:office");
        // VML namespace for Vector Markup Language.
        NAMESPACE_MAP.put("v", "urn:schemas-microsoft-com:vml");
        // WordprocessingML namespace for Word documents.
        NAMESPACE_MAP.put("w", "http://schemas.openxmlformats.org/wordprocessingml/2006/main");
        // SpreadsheetML namespace for Excel documents.
        NAMESPACE_MAP.put("x", "http://schemas.openxmlformats.org/spreadsheetml/2006/main");
        // DrawingML 2010 namespace for extended drawing features.
        NAMESPACE_MAP.put("a14", "http://schemas.microsoft.com/office/drawing/2010/main");
        // PresentationML 2010 namespace for extended presentation features.
        NAMESPACE_MAP.put("p14", "http://schemas.microsoft.com/office/powerpoint/2010/main");
        // PotShaper custom namespace for proprietary extensions.
        NAMESPACE_MAP.put("pot", "http://potshaper.quaternion.cc/");
    }

    /** Factory for creating XPath instances. */
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    /** Document Builder Factory configured for secure XML parsing. */
    private static final DocumentBuilderFactory DOC_BUILDER_FACTORY;

    /** Transformer Factory for XML serialization. */
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    static {
        DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        DOC_BUILDER_FACTORY.setNamespaceAware(true);
        DOC_BUILDER_FACTORY.setXIncludeAware(false);
        DOC_BUILDER_FACTORY.setExpandEntityReferences(false);

        // XXE baseline: disable DTD/entity expansion and external resource access.
        setFactoryFeature(DOC_BUILDER_FACTORY, XMLConstants.FEATURE_SECURE_PROCESSING, true);
        setFactoryFeature(DOC_BUILDER_FACTORY, "http://apache.org/xml/features/disallow-doctype-decl", true);
        setFactoryFeature(DOC_BUILDER_FACTORY, "http://xml.org/sax/features/external-general-entities", false);
        setFactoryFeature(DOC_BUILDER_FACTORY, "http://xml.org/sax/features/external-parameter-entities", false);
        setFactoryFeature(DOC_BUILDER_FACTORY, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        setFactoryAttribute(DOC_BUILDER_FACTORY, XMLConstants.ACCESS_EXTERNAL_DTD, "");
        setFactoryAttribute(DOC_BUILDER_FACTORY, XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private XmlUtils() {
    }

    // ==================== XML Extraction ====================

    /**
     * Retrieves the XML representation of a shape as a formatted string.
     *
     * <p>This method obtains the underlying XmlObject of the shape and serializes it to a
     * pretty-printed XML string. If the shape or its XML is null, or if serialization fails,
     * null is returned and a warning is logged.</p>
     *
     * @param shape the shape whose XML is to be retrieved; may be null.
     * @return the formatted XML string of the shape, or null if unavailable or an error occurs.
     */
    static String getShapeXml(XSLFShape shape) {
        if (shape == null) {
            return null;
        }
        try {
            XmlObject xmlObject = shape.getXmlObject();
            if (xmlObject == null) {
                return null;
            }
            return xmlObject.xmlText(getXmlOptions());
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "getShapeXml",
                "Failed to get shape XML", e);
            return null;
        }
    }

    /**
     * Retrieves the XML representation of a slide as a formatted string.
     *
     * <p>This method obtains the underlying XmlObject of the slide and serializes it to a
     * pretty-printed XML string. If the slide or its XML is null, or if serialization fails,
     * null is returned and a warning is logged.</p>
     *
     * @param slide the slide whose XML is to be retrieved; may be null.
     * @return the formatted XML string of the slide, or null if unavailable or an error occurs.
     */
    static String getSlideXml(XSLFSlide slide) {
        if (slide == null) {
            return null;
        }
        try {
            XmlObject xmlObject = slide.getXmlObject();
            if (xmlObject == null) {
                return null;
            }
            return xmlObject.xmlText(getXmlOptions());
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "getSlideXml",
                "Failed to get slide XML", e);
            return null;
        }
    }

    // ==================== XPath Query ====================

    /**
     * Executes an XPath query on an XML string and returns the matching nodes.
     *
     * <p>The XML string is parsed into a Document, and the XPath expression is evaluated
     * with namespace context from the predefined namespace map. If parsing or evaluation
     * fails, null is returned and a warning is logged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String shapeXml = getShapeXml(someShape);
     * NodeList textNodes = XmlUtils.queryNodes(shapeXml, "//a:t");
     * }</pre>
     *
     * @param xml   the XML string to query; must be well-formed.
     * @param xpath the XPath expression to evaluate.
     * @return a NodeList containing all matching nodes, or null if an error occurs.
     */
    static NodeList queryNodes(String xml, String xpath) {
        try {
            Document doc = parseXml(xml);
            if (doc == null) {
                return null;
            }
            XPath xPath = createXPath();
            XPathExpression expr = xPath.compile(xpath);
            return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "queryNodes",
                "Failed to query nodes with XPath: " + xpath, e);
            return null;
        }
    }

    /**
     * Executes an XPath query on an XML string and returns the first matching node.
     *
     * <p>The XML string is parsed into a Document, and the XPath expression is evaluated
     * with namespace context from the predefined namespace map. If parsing or evaluation
     * fails, or if no node matches, null is returned and a warning is logged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String slideXml = getSlideXml(someSlide);
     * Node titleNode = XmlUtils.queryNode(slideXml, "//p:sp[1]");
     * }</pre>
     *
     * @param xml   the XML string to query; must be well-formed.
     * @param xpath the XPath expression to evaluate.
     * @return the first matching Node, or null if none found or an error occurs.
     */
    static Node queryNode(String xml, String xpath) {
        try {
            Document doc = parseXml(xml);
            if (doc == null) {
                return null;
            }
            XPath xPath = createXPath();
            XPathExpression expr = xPath.compile(xpath);
            return (Node) expr.evaluate(doc, XPathConstants.NODE);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "queryNode",
                "Failed to query node with XPath: " + xpath, e);
            return null;
        }
    }

    /**
     * Executes an XPath query on an XML string and returns the string value of the result.
     *
     * <p>The XML string is parsed into a Document, and the XPath expression is evaluated
     * with namespace context from the predefined namespace map. The result is converted to
     * a string via XPathConstants.STRING. If parsing or evaluation fails, null is returned
     * and a warning is logged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String shapeXml = getShapeXml(someShape);
     * String fillColor = XmlUtils.queryString(shapeXml, "//a:solidFill/a:srgbClr/@val");
     * }</pre>
     *
     * @param xml   the XML string to query; must be well-formed.
     * @param xpath the XPath expression to evaluate.
     * @return the string value of the XPath evaluation result, or null if an error occurs.
     */
    static String queryString(String xml, String xpath) {
        try {
            Document doc = parseXml(xml);
            if (doc == null) {
                return null;
            }
            XPath xPath = createXPath();
            XPathExpression expr = xPath.compile(xpath);
            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "queryString",
                "Failed to query string with XPath: " + xpath, e);
            return null;
        }
    }

    // ==================== XML Modification ====================

    /**
     * Sets an attribute value on an XmlObject at the node(s) matched by an XPath expression.
     *
     * <p>This method uses an XmlCursor to navigate the XmlObject, selects nodes via the XPath,
     * and sets the specified attribute to the given value on the first matching node. The XPath
     * expression should be compatible with XmlCursor's selectPath method, typically requiring
     * namespace declarations.</p>
     *
     * <p>If the XmlObject is null, no nodes are matched, or an error occurs, the method returns
     * false and logs a warning.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlObject shapeXml = shape.getXmlObject();
     * boolean success = XmlUtils.setAttribute(shapeXml,
     *     "declare namespace a='...'; //a:srgbClr",
     *     "val", "FF0000");
     * }</pre>
     *
     * @param xmlObject the XmlObject to modify.
     * @param xpath     the XPath expression to select the target node(s).
     * @param attrName  the name of the attribute to set.
     * @param value     the value to assign to the attribute.
     * @return true if the attribute was successfully set on at least one node, false otherwise.
     */
    static boolean setAttribute(XmlObject xmlObject, String xpath, String attrName, String value) {
        if (xmlObject == null) {
            return false;
        }
        try {
            XmlCursor cursor = xmlObject.newCursor();
            cursor.selectPath(prepareCursorXPath(xpath));
            if (cursor.toNextSelection()) {
                cursor.setAttributeText(new QName(attrName), value);
                cursor.dispose();
                return true;
            }
            cursor.dispose();
            return false;
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "setAttribute",
                "Failed to set attribute '" + attrName + "' with XPath: " + xpath, e);
            return false;
        }
    }

    /**
     * Sets the text content of an XmlObject at the node(s) matched by an XPath expression.
     *
     * <p>This method uses an XmlCursor to navigate the XmlObject, selects nodes via the XPath,
     * and sets the text value of the first matching node. The XPath expression should be
     * compatible with XmlCursor's selectPath method, typically requiring namespace declarations.</p>
     *
     * <p>If the XmlObject is null, no nodes are matched, or an error occurs, the method returns
     * false and logs a warning.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlObject shapeXml = shape.getXmlObject();
     * boolean success = XmlUtils.setText(shapeXml,
     *     "declare namespace a='...'; //a:t",
     *     "New Text");
     * }</pre>
     *
     * @param xmlObject the XmlObject to modify.
     * @param xpath     the XPath expression to select the target node(s).
     * @param text      the text content to set.
     * @return true if the text was successfully set on at least one node, false otherwise.
     */
    static boolean setText(XmlObject xmlObject, String xpath, String text) {
        if (xmlObject == null) {
            return false;
        }
        try {
            XmlCursor cursor = xmlObject.newCursor();
            cursor.selectPath(prepareCursorXPath(xpath));
            if (cursor.toNextSelection()) {
                cursor.setTextValue(text);
                cursor.dispose();
                return true;
            }
            cursor.dispose();
            return false;
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "setText",
                "Failed to set text with XPath: " + xpath, e);
            return false;
        }
    }

    /**
     * Inserts an XML fragment relative to the first node matched by an XPath expression within an XmlObject.
     *
     * <p>This method uses an XmlCursor to navigate the XmlObject, selects nodes via the XPath,
     * and inserts the provided XML fragment at the specified position relative to the first matching node.
     * The XPath expression should be compatible with XmlCursor's selectPath method, typically requiring
     * namespace declarations.</p>
     *
     * <p>If the XmlObject or fragment is null, no nodes are matched, or an error occurs, the method returns
     * false and logs a warning.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlObject shapeXml = shape.getXmlObject();
     * String fragment = "<a:solidFill><a:srgbClr val=\"FF0000\"/></a:solidFill>";
     * boolean success = XmlUtils.insertXml(shapeXml,
     *     "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; //a:spPr",
     *     fragment, InsertPosition.CHILD);
     * }</pre>
     *
     * @param xmlObject    the XmlObject to modify; must not be null.
     * @param xpath        the XPath expression to select the target node; should include namespace declarations.
     * @param xmlFragment  the well-formed XML fragment to insert.
     * @param position     the insertion position relative to the matched node.
     * @return true if the fragment was successfully inserted; false otherwise.
     * @see InsertPosition
     */
    static boolean insertXml(XmlObject xmlObject, String xpath, String xmlFragment, InsertPosition position) {
        if (xmlObject == null || xmlFragment == null) {
            return false;
        }
        try {
            XmlCursor cursor = xmlObject.newCursor();
            cursor.selectPath(prepareCursorXPath(xpath));
            if (cursor.toNextSelection()) {
                XmlObject fragment = XmlObject.Factory.parse(xmlFragment);
                XmlCursor fragCursor = fragment.newCursor();
                fragCursor.toFirstContentToken();

                switch (position) {
                    case BEFORE:
                        fragCursor.copyXml(cursor);
                        break;
                    case AFTER:
                        cursor.toEndToken();
                        cursor.toNextToken();
                        fragCursor.copyXml(cursor);
                        break;
                    case CHILD:
                        cursor.toEndToken();
                        fragCursor.copyXml(cursor);
                        break;
                }

                fragCursor.dispose();
                cursor.dispose();
                return true;
            }
            cursor.dispose();
            return false;
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "insertXml",
                "Failed to insert XML at XPath: " + xpath + ", position: " + position, e);
            return false;
        }
    }

    /**
     * Deletes all nodes matched by an XPath expression within an XmlObject.
     *
     * <p>This method uses an XmlCursor to navigate the XmlObject, selects nodes via the XPath,
     * and removes each matching node from the XML tree. The XPath expression should be compatible
     * with XmlCursor's selectPath method, typically requiring namespace declarations.</p>
     *
     * <p>If the XmlObject is null or an error occurs, the method returns 0 and logs a warning.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlObject shapeXml = shape.getXmlObject();
     * int deleted = XmlUtils.deleteNodes(shapeXml,
     *     "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; //a:shadow");
     * }</pre>
     *
     * @param xmlObject the XmlObject to modify; must not be null.
     * @param xpath     the XPath expression to select nodes for deletion; should include namespace declarations.
     * @return the number of nodes successfully deleted.
     */
    static int deleteNodes(XmlObject xmlObject, String xpath) {
        if (xmlObject == null) {
            return 0;
        }
        try {
            XmlCursor cursor = xmlObject.newCursor();
            cursor.selectPath(prepareCursorXPath(xpath));
            int count = 0;
            while (cursor.toNextSelection()) {
                cursor.removeXml();
                count++;
            }
            cursor.dispose();
            return count;
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "deleteNodes",
                "Failed to delete nodes with XPath: " + xpath, e);
            return 0;
        }
    }

    // ==================== XML Parsing and Serialization ====================

    /**
     * Parses a well-formed XML string into a DOM Document.
     *
     * <p>This method uses a secure DocumentBuilderFactory configured to prevent XXE attacks.
     * If the input string is null, empty, or malformed, null is returned and a warning is logged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String xml = "<a:spPr><a:solidFill><a:srgbClr val=\"FF0000\"/></a:solidFill></a:spPr>";
     * Document doc = XmlUtils.parseXml(xml);
     * }</pre>
     *
     * @param xml the XML string to parse; must be well-formed.
     * @return a Document representing the parsed XML, or null if parsing fails.
     */
    static Document parseXml(String xml) {
        if (xml == null || xml.isEmpty()) {
            return null;
        }
        try {
            DocumentBuilder builder = DOC_BUILDER_FACTORY.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "parseXml", "Failed to parse XML string", e);
            return null;
        }
    }

    /**
     * Serializes a DOM Document to a formatted XML string.
     *
     * <p>The output is pretty-printed with indentation and omits the XML declaration.
     * If the Document is null or serialization fails, null is returned and a warning is logged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * Document doc = parseXml(someXml);
     * String serialized = XmlUtils.serializeXml(doc);
     * }</pre>
     *
     * @param doc the Document to serialize; may be null.
     * @return a formatted XML string, or null if serialization fails.
     */
    static String serializeXml(Document doc) {
        if (doc == null) {
            return null;
        }
        try {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "serializeXml",
                "Failed to serialize XML document", e);
            return null;
        }
    }

    /**
     * Serializes a single DOM Node to an XML string.
     *
     * <p>The output omits the XML declaration. If the Node is null or serialization fails,
     * null is returned and a warning is logged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * NodeList nodes = queryNodes(xml, "//a:t");
     * String nodeXml = XmlUtils.serializeNode(nodes.item(0));
     * }</pre>
     *
     * @param node the Node to serialize; may be null.
     * @return an XML string representing the node, or null if serialization fails.
     */
    static String serializeNode(Node node) {
        if (node == null) {
            return null;
        }
        try {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "serializeNode",
                "Failed to serialize XML node", e);
            return null;
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a new XPath instance configured with the predefined namespace map.
     *
     * <p>The returned XPath object is ready to evaluate expressions using the standard
     * OpenXML namespace prefixes (a, p, r, etc.).</p>
     *
     * @return a configured XPath instance.
     */
    private static XPath createXPath() {
        XPath xPath = XPATH_FACTORY.newXPath();
        xPath.setNamespaceContext(new javax.xml.namespace.NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return NAMESPACE_MAP.getOrDefault(prefix, "");
            }

            @Override
            public String getPrefix(String namespaceURI) {
                for (Map.Entry<String, String> entry : NAMESPACE_MAP.entrySet()) {
                    if (entry.getValue().equals(namespaceURI)) {
                        return entry.getKey();
                    }
                }
                return null;
            }

            @Override
            public java.util.Iterator<String> getPrefixes(String namespaceURI) {
                return NAMESPACE_MAP.keySet().iterator();
            }
        });
        return xPath;
    }

    /**
     * Returns an XmlOptions instance configured for pretty-printing XML output.
     *
     * <p>The options enable indentation with a two-space indent. This is suitable for
     * generating human-readable XML strings.</p>
     *
     * @return a configured XmlOptions instance.
     */
    static XmlOptions getXmlOptions() {
        XmlOptions options = new XmlOptions();
        options.setSavePrettyPrint();
        options.setSavePrettyPrintIndent(2);
        return options;
    }

    /**
     * Retrieves the namespace URI associated with a given prefix.
     *
     * <p>This method looks up the prefix in the internal namespace map. If the prefix
     * is not registered, null is returned.</p>
     *
     * @param prefix the namespace prefix (e.g., "a", "p").
     * @return the associated namespace URI, or null if the prefix is unknown.
     */
    static String getNamespaceUri(String prefix) {
        return NAMESPACE_MAP.get(prefix);
    }

    /**
     * Registers a namespace prefix and URI for use in XPath queries and XML processing.
     *
     * <p>If the prefix already exists, its mapping is overwritten. This method allows
     * extending the default namespace map with custom or additional namespaces.</p>
     *
     * @param prefix the namespace prefix to register.
     * @param uri    the namespace URI to associate with the prefix.
     */
    static void registerNamespace(String prefix, String uri) {
        NAMESPACE_MAP.put(prefix, uri);
    }

    /**
     * Escapes XML special characters in a string.
     *
     * <p>Replaces the characters &amp;, &lt;, &gt;, &quot;, and &apos; with their corresponding
     * XML entities. If the input is null, null is returned.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String escaped = XmlUtils.escapeXml("A < B & C > D");
     * // Result: "A &lt; B &amp; C &gt; D"
     * }</pre>
     *
     * @param text the text to escape; may be null.
     * @return the escaped string, or null if the input is null.
     */
    static String escapeXml(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }

    /**
     * Unescapes XML entities in a string.
     *
     * <p>Replaces the XML entities &amp;amp;, &amp;lt;, &amp;gt;, &amp;quot;, and &amp;apos;
     * with their corresponding characters. If the input is null, null is returned.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String unescaped = XmlUtils.unescapeXml("A &lt; B &amp; C &gt; D");
     * // Result: "A < B & C > D"
     * }</pre>
     *
     * @param text the text containing XML entities; may be null.
     * @return the unescaped string, or null if the input is null.
     */
    static String unescapeXml(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("&apos;", "'")
                   .replace("&quot;", "\"")
                   .replace("&gt;", ">")
                   .replace("&lt;", "<")
                   .replace("&amp;", "&");
    }

    /**
     * Specifies the position for inserting XML fragments relative to a target node.
     *
     * @since 1.0
     */
    enum InsertPosition {
        /** Insert the fragment as a sibling immediately before the target node. */
        BEFORE,
        /** Insert the fragment as a sibling immediately after the target node. */
        AFTER,
        /** Insert the fragment as the last child of the target node. */
        CHILD
    }

    // ==================== Shape Z-Order Operations ====================

    /**
     * Brings a shape to the front of the z-order on its slide.
     *
     * <p>This method reorders the shape within the slide's shape tree so that it appears
     * above all other shapes. If the shape or slide is null, the operation is silently skipped.
     * If an error occurs during reordering, a warning is logged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlUtils.bringToFront(slide, shape);
     * }</pre>
     *
     * @param slide the slide containing the shape.
     * @param shape the shape to bring to the front.
     */
    static void bringToFront(XSLFSlide slide, XSLFShape shape) {
        reorderShapeInSpTree(slide, shape, ReorderMode.TO_FRONT);
    }

    /**
     * Sends a shape to the back of the z-order on a slide.
     *
     * <p>This method reorders the shape within the slide's shape tree so that it appears
     * behind all other shapes. The operation modifies the underlying XML structure
     * of the slide.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.getShapes().get(0);
     * XmlUtils.sendToBack(slide, shape);
     * }</pre>
     *
     * @param slide the slide containing the shape; must not be null.
     * @param shape the shape to send to the back; must not be null.
     */
    static void sendToBack(XSLFSlide slide, XSLFShape shape) {
        reorderShapeInSpTree(slide, shape, ReorderMode.TO_BACK);
    }

    /**
     * Brings a shape one level forward in the z-order on a slide.
     *
     * <p>This method reorders the shape within the slide's shape tree so that it moves
     * one position forward relative to other shapes. If the shape is already at the front,
     * no change occurs.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.getShapes().get(2);
     * XmlUtils.bringForward(slide, shape);
     * }</pre>
     *
     * @param slide the slide containing the shape; must not be null.
     * @param shape the shape to bring forward; must not be null.
     */
    static void bringForward(XSLFSlide slide, XSLFShape shape) {
        reorderShapeInSpTree(slide, shape, ReorderMode.FORWARD_ONE);
    }

    /**
     * Sends a shape one level backward in the z-order on a slide.
     *
     * <p>This method reorders the shape within the slide's shape tree so that it moves
     * one position backward relative to other shapes. If the shape is already at the back,
     * no change occurs.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.getShapes().get(0);
     * XmlUtils.sendBackward(slide, shape);
     * }</pre>
     *
     * @param slide the slide containing the shape; must not be null.
     * @param shape the shape to send backward; must not be null.
     */
    static void sendBackward(XSLFSlide slide, XSLFShape shape) {
        reorderShapeInSpTree(slide, shape, ReorderMode.BACKWARD_ONE);
    }

    // ==================== Hyperlink Manipulation ====================

    /**
     * Removes any hyperlink from a shape.
     *
     * <p>This method deletes the hyperlink-related elements (hlinkClick and hlinkHover)
     * from the shape's XML, effectively removing the hyperlink. If the shape has no hyperlink,
     * the method does nothing. The operation modifies the underlying XML of the shape.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFSimpleShape shape = (XSLFSimpleShape) slide.getShapes().get(0);
     * XmlUtils.removeHyperlink(shape);
     * }</pre>
     *
     * @param shape the shape from which to remove the hyperlink; may be null.
     * @throws PotException if an error occurs during XML manipulation.
     */
    static void removeHyperlink(org.apache.poi.xslf.usermodel.XSLFSimpleShape shape) {
        if (shape == null) {
            return;
        }

        try {
            // Check if a hyperlink exists via POI API
            org.apache.poi.xslf.usermodel.XSLFHyperlink link = shape.getHyperlink();
            if (link == null) {
                // No hyperlink to remove
                return;
            }

            // Directly manipulate the XML because POI's removeHyperlink may not fully clean up
            XmlObject xmlObject = shape.getXmlObject();

            // Remove hlinkClick and hlinkHover elements under cNvPr
            deleteNodes(xmlObject, "//p:cNvPr/a:hlinkClick");
            deleteNodes(xmlObject, "//p:cNvPr/a:hlinkHover");

            PotLogger.warn(XmlUtils.class, "removeHyperlink",
                "Successfully removed hyperlink from shape");

        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "removeHyperlink",
                "Failed to remove hyperlink from shape", e);
            throw new PotException("Failed to remove hyperlink", e,
                PotException.ErrorCode.XML_MANIPULATION_ERROR)
                .withContext("shapeType", shape.getClass().getSimpleName());
        }
    }

    // ==================== Shadow Effects ====================

    /**
     * Applies a shadow effect to a shape.
     *
     * <p>This method modifies the shape's XML to include a shadow effect according to the
     * specified PotShadow properties. If the shadow type is NONE, the existing shadow is removed.
     * The shadow is added to the shape's effect list (a:effectLst).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShadow shadow = new PotShadow(PotShadow.Type.OUTER, PotColor.RED, 0.8, 10.0, 45.0, 0.5, 0.5);
     * XmlUtils.applyShadow(shape, shadow);
     * }</pre>
     *
     * @param shape  the shape to which the shadow will be applied; may be null.
     * @param shadow the shadow configuration; may be null.
     */
    static void applyShadow(org.apache.poi.xslf.usermodel.XSLFSimpleShape shape, PotShadow shadow) {
        if (shape == null || shadow == null) {
            return;
        }
        if (shadow.getType() == PotShadow.Type.NONE) {
            removeShadow(shape);
            return;
        }

        try {
            Document doc = parseXmlObject(shape.getXmlObject());
            if (doc == null) {
                return;
            }

            Element spPr = findFirstElementByLocalName(doc.getDocumentElement(), "spPr");
            if (spPr == null) {
                return;
            }

            Element effectLst = getOrCreateDirectChild(spPr, DRAWINGML_NS, "effectLst");
            removeDirectChildrenByLocalName(effectLst, "outerShdw", "innerShdw", "prstShdw");

            Element shadowElement;
            if (shadow.getType() == PotShadow.Type.INNER) {
                shadowElement = doc.createElementNS(DRAWINGML_NS, "a:innerShdw");
            } else {
                shadowElement = doc.createElementNS(DRAWINGML_NS, "a:outerShdw");
            }
            shadowElement.setAttribute("blurRad", Long.toString(shadow.getBlurEmu()));
            shadowElement.setAttribute("dist", Long.toString(shadow.getDistanceEmu()));
            shadowElement.setAttribute("dir", Integer.toString(shadow.getAngleEmu()));
            shadowElement.setAttribute("rotWithShape", "0");

            if (shadow.getType() == PotShadow.Type.PERSPECTIVE) {
                shadowElement.setAttribute("sx", Integer.toString((int) Math.round(shadow.getScaleX() * 100000)));
                shadowElement.setAttribute("sy", Integer.toString((int) Math.round(shadow.getScaleY() * 100000)));
            }

            Element srgbClr = doc.createElementNS(DRAWINGML_NS, "a:srgbClr");
            srgbClr.setAttribute("val", shadow.getColor().toHex());
            Element alpha = doc.createElementNS(DRAWINGML_NS, "a:alpha");
            alpha.setAttribute("val", Integer.toString(UnitConverter.fractionToEmuPercent(shadow.getColor().getOpacity())));
            srgbClr.appendChild(alpha);
            shadowElement.appendChild(srgbClr);

            effectLst.appendChild(shadowElement);
            applyDocumentToXmlObject(shape.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "applyShadow",
                "Failed to apply shadow", e);
        }
    }

    /**
     * Removes any shadow effect from a shape.
     *
     * <p>This method deletes shadow elements (outerShdw, innerShdw, prstShdw) from the
     * shape's effect list. If no shadow exists, the method does nothing.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlUtils.removeShadow(shape);
     * }</pre>
     *
     * @param shape the shape from which to remove the shadow; may be null.
     */
    static void removeShadow(org.apache.poi.xslf.usermodel.XSLFSimpleShape shape) {
        if (shape == null) {
            return;
        }

        try {
            Document doc = parseXmlObject(shape.getXmlObject());
            if (doc == null) {
                return;
            }
            Element spPr = findFirstElementByLocalName(doc.getDocumentElement(), "spPr");
            if (spPr == null) {
                return;
            }
            Element effectLst = findDirectChildByLocalName(spPr, "effectLst");
            if (effectLst == null) {
                return;
            }
            removeDirectChildrenByLocalName(effectLst, "outerShdw", "innerShdw", "prstShdw");
            applyDocumentToXmlObject(shape.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "removeShadow",
                "Failed to remove shadow", e);
        }
    }

    // ==================== Opacity Manipulation ====================

    /**
     * Sets the opacity (alpha) of a shape.
     *
     * <p>This method applies an alphaModFix effect to the shape's effect list,
     * controlling its overall transparency. The alpha value is clamped between 0.0 (fully transparent)
     * and 1.0 (fully opaque). The operation modifies the underlying XML of the shape.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.getShapes().get(0);
     * XmlUtils.setOpacity(shape, 0.5); // 50% opacity
     * }</pre>
     *
     * @param shape the shape whose opacity to set; may be null.
     * @param alpha the opacity value (0.0 to 1.0).
     */
    static void setOpacity(XSLFShape shape, double alpha) {
        if (shape == null) {
            return;
        }
        double safeAlpha = Math.max(0, Math.min(1, alpha));

        try {
            Document doc = parseXmlObject(shape.getXmlObject());
            if (doc == null) {
                return;
            }
            Element root = doc.getDocumentElement();
            Element spPr = findFirstElementByLocalName(root, "spPr", "grpSpPr");
            if (spPr == null) {
                return;
            }

            Element effectLst = getOrCreateDirectChild(spPr, DRAWINGML_NS, "effectLst");
            removeDirectChildrenByLocalName(effectLst, "alphaModFix");

            Element alphaModFix = doc.createElementNS(DRAWINGML_NS, "a:alphaModFix");
            alphaModFix.setAttribute("amt", Integer.toString(UnitConverter.fractionToEmuPercent(safeAlpha)));
            effectLst.appendChild(alphaModFix);

            applyDocumentToXmlObject(shape.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "setOpacity",
                "Failed to set shape opacity", e);
        }
    }

    // ==================== Fill Manipulation ====================

    /**
     * Applies a gradient fill to a text shape.
     *
     * <p>This method replaces the existing fill of the shape with a gradient fill
     * defined by the provided PotGradient configuration. The gradient is applied
     * to the shape's spPr element. The operation modifies the underlying XML of the shape.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = new PotGradient(PotGradient.Type.LINEAR, 45.0, true);
     * gradient.addStop(0.0, PotColor.RED, 1.0);
     * gradient.addStop(1.0, PotColor.BLUE, 1.0);
     * XSLFTextShape textShape = (XSLFTextShape) slide.getShapes().get(0);
     * XmlUtils.applyGradientFill(textShape, gradient);
     * }</pre>
     *
     * @param shape    the text shape to fill; may be null.
     * @param gradient the gradient configuration; may be null.
     */
    static void applyGradientFill(org.apache.poi.xslf.usermodel.XSLFTextShape shape, PotGradient gradient) {
        if (shape == null || gradient == null) {
            return;
        }
        applyGradientFill(shape.getXmlObject(), gradient);
    }

    /**
     * Applies a gradient fill to an auto shape.
     *
     * <p>This method replaces the existing fill of the shape with a gradient fill
     * defined by the provided PotGradient configuration. The gradient is applied
     * to the shape's spPr element. The operation modifies the underlying XML of the shape.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = new PotGradient(PotGradient.Type.RADIAL, 0.0, false);
     * gradient.addStop(0.0, PotColor.YELLOW, 1.0);
     * gradient.addStop(1.0, PotColor.GREEN, 0.8);
     * XSLFAutoShape autoShape = (XSLFAutoShape) slide.getShapes().get(0);
     * XmlUtils.applyGradientFill(autoShape, gradient);
     * }</pre>
     *
     * @param shape    the auto shape to fill; may be null.
     * @param gradient the gradient configuration; may be null.
     */
    static void applyGradientFill(org.apache.poi.xslf.usermodel.XSLFAutoShape shape, PotGradient gradient) {
        if (shape == null || gradient == null) {
            return;
        }
        applyGradientFill(shape.getXmlObject(), gradient);
    }

    /**
     * Applies a picture fill to a shape using a relationship ID.
     *
     * <p>This method replaces the existing fill of the shape with a blipFill that references
     * an embedded picture via its relationship ID. The picture is stretched to fill the shape.
     * The operation modifies the underlying XML of the shape.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFPictureData picData = slide.getSlideShow().addPicture(imageBytes, PictureData.PictureType.PNG);
     * String rId = slide.addPictureReference(picData);
     * XSLFShape shape = slide.getShapes().get(0);
     * XmlUtils.applyPictureFill(shape, rId);
     * }</pre>
     *
     * @param shape the shape to fill with a picture; may be null.
     * @param rId   the relationship ID of the embedded picture; must not be null or empty.
     */
    static void applyPictureFill(XSLFShape shape, String rId) {
        if (shape == null || rId == null || rId.isEmpty()) {
            return;
        }
        try {
            Document doc = parseXmlObject(shape.getXmlObject());
            if (doc == null) {
                return;
            }
            Element spPr = findFirstElementByLocalName(doc.getDocumentElement(), "spPr");
            if (spPr == null) {
                return;
            }
            removeDirectChildrenByLocalName(spPr,
                "noFill", "solidFill", "gradFill", "blipFill", "pattFill", "grpFill");

            Element blipFill = doc.createElementNS(DRAWINGML_NS, "a:blipFill");
            Element blip = doc.createElementNS(DRAWINGML_NS, "a:blip");
            blip.setAttributeNS(RELATIONSHIP_NS, "r:embed", rId);
            blipFill.appendChild(blip);

            Element stretch = doc.createElementNS(DRAWINGML_NS, "a:stretch");
            Element fillRect = doc.createElementNS(DRAWINGML_NS, "a:fillRect");
            stretch.appendChild(fillRect);
            blipFill.appendChild(stretch);

            spPr.appendChild(blipFill);
            applyDocumentToXmlObject(shape.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "applyPictureFill",
                "Failed to apply picture fill", e);
        }
    }

    /**
     * Applies a pattern fill to a shape.
     *
     * <p>This method replaces the existing fill of the shape with a pattern fill
     * defined by the specified pattern type and colors. The pattern is drawn using
     * the foreground and background colors. The operation modifies the underlying XML of the shape.</p>
     *
     * <p>The pattern type is mapped to the corresponding OpenXML ST_PresetPatternVal value.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlUtils.applyPatternFill(shape, PotFill.PatternType.DIAGONAL_BRICK,
     *                           PotColor.RED, PotColor.WHITE);
     * }</pre>
     *
     * @param shape       the shape to fill; may be null.
     * @param patternType the pattern type; must not be null.
     * @param fgColor     the foreground color; may be null (defaults to black).
     * @param bgColor     the background color; may be null (defaults to white).
     */
    static void applyPatternFill(XSLFShape shape, PotFill.PatternType patternType,
                                 PotColor fgColor, PotColor bgColor) {
        if (shape == null || patternType == null) {
            return;
        }
        try {
            Document doc = parseXmlObject(shape.getXmlObject());
            if (doc == null) {
                return;
            }
            Element spPr = findFirstElementByLocalName(doc.getDocumentElement(), "spPr");
            if (spPr == null) {
                return;
            }
            removeDirectChildrenByLocalName(spPr,
                "noFill", "solidFill", "gradFill", "blipFill", "pattFill", "grpFill");

            Element pattFill = doc.createElementNS(DRAWINGML_NS, "a:pattFill");
            pattFill.setAttribute("prst", toOxmlPatternVal(patternType));

            if (fgColor != null) {
                Element fgClr = doc.createElementNS(DRAWINGML_NS, "a:fgClr");
                Element srgb = doc.createElementNS(DRAWINGML_NS, "a:srgbClr");
                srgb.setAttribute("val", fgColor.toHex());
                fgClr.appendChild(srgb);
                pattFill.appendChild(fgClr);
            }
            if (bgColor != null) {
                Element bgClr = doc.createElementNS(DRAWINGML_NS, "a:bgClr");
                Element srgb = doc.createElementNS(DRAWINGML_NS, "a:srgbClr");
                srgb.setAttribute("val", bgColor.toHex());
                bgClr.appendChild(srgb);
                pattFill.appendChild(bgClr);
            }

            spPr.appendChild(pattFill);
            applyDocumentToXmlObject(shape.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "applyPatternFill",
                "Failed to apply pattern fill", e);
        }
    }

    /**
     * Converts a PotFill.PatternType to the corresponding OpenXML ST_PresetPatternVal string.
     *
     * @param type the pattern type to convert.
     * @return the OpenXML preset pattern value.
     */
    private static String toOxmlPatternVal(PotFill.PatternType type) {
        switch (type) {
            case PERCENT_5:          return "pct5";
            case PERCENT_10:         return "pct10";
            case PERCENT_20:         return "pct20";
            case PERCENT_25:         return "pct25";
            case PERCENT_30:         return "pct30";
            case PERCENT_40:         return "pct40";
            case PERCENT_50:         return "pct50";
            case PERCENT_60:         return "pct60";
            case PERCENT_70:         return "pct70";
            case PERCENT_75:         return "pct75";
            case PERCENT_80:         return "pct80";
            case PERCENT_90:         return "pct90";
            case HORIZONTAL:         return "horz";
            case VERTICAL:           return "vert";
            case LIGHT_HORIZONTAL:   return "ltHorz";
            case LIGHT_VERTICAL:     return "ltVert";
            case DARK_HORIZONTAL:    return "dkHorz";
            case DARK_VERTICAL:      return "dkVert";
            case NARROW_HORIZONTAL:  return "narHorz";
            case NARROW_VERTICAL:    return "narVert";
            case DASHED_HORIZONTAL:  return "dashHorz";
            case DASHED_VERTICAL:    return "dashVert";
            case CROSS:              return "cross";
            case DOWNWARD_DIAGONAL:  return "dnDiag";
            case UPWARD_DIAGONAL:    return "upDiag";
            case LIGHT_DOWNWARD_DIAGONAL: return "ltDnDiag";
            case LIGHT_UPWARD_DIAGONAL:   return "ltUpDiag";
            case DARK_DOWNWARD_DIAGONAL:  return "dkDnDiag";
            case DARK_UPWARD_DIAGONAL:    return "dkUpDiag";
            case WIDE_DOWNWARD_DIAGONAL:  return "wdDnDiag";
            case WIDE_UPWARD_DIAGONAL:    return "wdUpDiag";
            case DASHED_DOWNWARD_DIAGONAL: return "dashDnDiag";
            case DASHED_UPWARD_DIAGONAL:   return "dashUpDiag";
            case DIAGONAL_CROSS:     return "diagCross";
            case SMALL_CHECKER_BOARD: return "smCheck";
            case LARGE_CHECKER_BOARD: return "lgCheck";
            case SMALL_GRID:         return "smGrid";
            case LARGE_GRID:         return "lgGrid";
            case DOTTED_GRID:        return "dotGrid";
            case SMALL_CONFETTI:     return "smConfetti";
            case LARGE_CONFETTI:     return "lgConfetti";
            case HORIZONTAL_BRICK:   return "horzBrick";
            case DIAGONAL_BRICK:     return "diagBrick";
            case SOLID_DIAMOND:      return "solidDmnd";
            case OPEN_DIAMOND:       return "openDmnd";
            case DOTTED_DIAMOND:     return "dotDmnd";
            case PLAID:              return "plaid";
            case SPHERE:             return "sphere";
            case WEAVE:              return "weave";
            case DIVOT:              return "divot";
            case SHINGLE:            return "shingle";
            case WAVE:               return "wave";
            case TRELLIS:            return "trellis";
            case ZIG_ZAG:            return "zigZag";
            default:                 return "horz";
        }
    }

    // ==================== Shape Adjustment Value Manipulation ====================

    /**
     * Sets an adjustment value (guide) for an AutoShape.
     *
     * <p>This method modifies the shape's geometry adjustment list (avLst) to set a named guide
     * to a specific value. The value is clamped between 0 and 100,000 (EMU units). If a guide with
     * the same name already exists, it is replaced. The operation modifies the underlying XML of the shape.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFAutoShape arrow = slide.createAutoShape();
     * arrow.setShapeType(ShapeType.LEFT_ARROW);
     * XmlUtils.setAdjustValue(arrow, "adj1", 50000); // Set the arrow head width
     * }</pre>
     *
     * @param shape the AutoShape to modify; may be null.
     * @param name  the name of the adjustment guide (e.g., "adj1", "adj2"); must not be null or empty.
     * @param value the adjustment value (0 to 100,000 EMU).
     */
    static void setAdjustValue(org.apache.poi.xslf.usermodel.XSLFAutoShape shape, String name, int value) {
        if (shape == null || name == null || name.trim().isEmpty()) {
            return;
        }

        int safeValue = Math.max(0, Math.min(100000, value));

        try {
            Document doc = parseXmlObject(shape.getXmlObject());
            if (doc == null) {
                return;
            }
            Element prstGeom = findFirstElementByLocalName(doc.getDocumentElement(), "prstGeom");
            if (prstGeom == null) {
                return;
            }

            Element avLst = getOrCreateDirectChild(prstGeom, DRAWINGML_NS, "avLst");

            NodeList guides = avLst.getChildNodes();
            List<Element> toRemove = new ArrayList<>();
            for (int i = 0; i < guides.getLength(); i++) {
                Node child = guides.item(i);
                if (child instanceof Element) {
                    Element element = (Element) child;
                    if ("gd".equals(element.getLocalName()) && name.equals(element.getAttribute("name"))) {
                        toRemove.add(element);
                    }
                }
            }
            for (Element element : toRemove) {
                avLst.removeChild(element);
            }

            Element guide = doc.createElementNS(DRAWINGML_NS, "a:gd");
            guide.setAttribute("name", name);
            guide.setAttribute("fmla", "val " + safeValue);
            avLst.appendChild(guide);

            applyDocumentToXmlObject(shape.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "setAdjustValue",
                "Failed to set adjust value", e);
        }
    }

    // ==================== Picture Data Replacement ====================

    /**
     * Replaces the picture data referenced by a picture shape.
     *
     * <p>This method updates the embedded relationship (r:embed) of a picture shape to point to
     * a new XSLFPictureData object. The new picture data must already be added to the slide show.
     * The operation modifies the underlying XML of the shape.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFPictureShape picShape = (XSLFPictureShape) slide.getShapes().get(0);
     * byte[] newImageBytes = Files.readAllBytes(Paths.get("new_image.png"));
     * XSLFPictureData newData = slide.getSlideShow().addPicture(newImageBytes, PictureData.PictureType.PNG);
     * XmlUtils.replacePictureData(picShape, newData);
     * }</pre>
     *
     * @param shape   the picture shape whose data to replace; may be null.
     * @param newData the new picture data to reference; may be null.
     * @throws PotException if the XML manipulation fails.
     */
    static void replacePictureData(org.apache.poi.xslf.usermodel.XSLFPictureShape shape,
                                   org.apache.poi.xslf.usermodel.XSLFPictureData newData) {
        if (shape == null || newData == null) {
            return;
        }

        try {
            XSLFSheet sheet = shape.getSheet();
            if (sheet == null) {
                return;
            }

            PackageRelationship relationship = sheet.getPackagePart().addRelationship(
                newData.getPackagePart().getPartName(),
                TargetMode.INTERNAL,
                XSLFRelation.IMAGES.getRelation()
            );
            String newRelId = relationship.getId();

            Document doc = parseXmlObject(shape.getXmlObject());
            if (doc == null) {
                return;
            }

            Element blip = findFirstElementByLocalName(doc.getDocumentElement(), "blip");
            if (blip == null) {
                return;
            }

            blip.setAttributeNS(RELATIONSHIP_NS, "r:embed", newRelId);
            applyDocumentToXmlObject(shape.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "replacePictureData",
                "Failed to replace picture data", e);
            throw new PotException("Failed to replace picture data", e,
                PotException.ErrorCode.XML_MANIPULATION_ERROR);
        }
    }

    // ==================== Table Manipulation ====================

    /**
     * Inserts a new row into a table at the specified index.
     *
     * <p>This method clones the first row of the table (including its cell structure and formatting)
     * and inserts the copy at the given position. The text content of the new row's cells is cleared.
     * The operation modifies the underlying XML of the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFTable table = (XSLFTable) slide.getShapes().get(0);
     * XmlUtils.insertTableRow(table, 1); // Insert a row after the first row
     * }</pre>
     *
     * @param table the table to modify; may be null.
     * @param index the insertion index (0-based); must be between 0 and the current number of rows inclusive.
     */
    static void insertTableRow(org.apache.poi.xslf.usermodel.XSLFTable table, int index) {
        if (table == null || index < 0 || index > table.getNumberOfRows()) {
            return;
        }

        try {
            Document doc = parseXmlObject(table.getXmlObject());
            if (doc == null) {
                return;
            }

            Element tbl = doc.getDocumentElement();
            if (!"tbl".equals(tbl.getLocalName())) {
                return;
            }

            // Find existing rows
            NodeList rows = tbl.getElementsByTagNameNS("*", "tr");
            if (rows.getLength() == 0) {
                return;
            }

            // Clone the first row as a template
            Element sourceRow = (Element) rows.item(0);
            Element newRow = (Element) sourceRow.cloneNode(true);

            // Clear text content in all cells of the new row
            NodeList cells = newRow.getElementsByTagNameNS("*", "tc");
            for (int i = 0; i < cells.getLength(); i++) {
                Element cell = (Element) cells.item(i);
                // Find all text bodies within the cell
                NodeList txBodies = cell.getElementsByTagNameNS("*", "txBody");
                for (int j = 0; j < txBodies.getLength(); j++) {
                    Element txBody = (Element) txBodies.item(j);
                    // Remove all paragraphs (<a:p>) except <a:bodyPr> and <a:lstStyle>
                    NodeList children = txBody.getChildNodes();
                    List<Node> toRemove = new ArrayList<>();
                    for (int k = 0; k < children.getLength(); k++) {
                        Node child = children.item(k);
                        if (child instanceof Element) {
                            Element elem = (Element) child;
                            if ("p".equals(elem.getLocalName())) {
                                toRemove.add(child);
                            }
                        }
                    }
                    for (Node node : toRemove) {
                        txBody.removeChild(node);
                    }
                    // Add a single empty paragraph
                    Element emptyPara = doc.createElementNS(DRAWINGML_NS, "a:p");
                    txBody.appendChild(emptyPara);
                }
            }

            // Insert the new row at the specified position
            if (index < rows.getLength()) {
                Element targetRow = (Element) rows.item(index);
                tbl.insertBefore(newRow, targetRow);
            } else {
                tbl.appendChild(newRow);
            }

            applyDocumentToXmlObject(table.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "insertTableRow",
                "Failed to insert table row at index " + index, e);
        }
    }

    /**
     * Removes a row from a table at the specified index.
     *
     * <p>This method deletes the row element at the given position from the table's XML structure.
     * The operation modifies the underlying XML of the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFTable table = (XSLFTable) slide.getShapes().get(0);
     * XmlUtils.removeTableRow(table, 0); // Remove the first row
     * }</pre>
     *
     * @param table the table to modify; may be null.
     * @param index the removal index (0-based); must be between 0 and (number of rows - 1).
     */
    static void removeTableRow(org.apache.poi.xslf.usermodel.XSLFTable table, int index) {
        if (table == null || index < 0 || index >= table.getNumberOfRows()) {
            return;
        }

        try {
            Document doc = parseXmlObject(table.getXmlObject());
            if (doc == null) {
                return;
            }

            Element tbl = doc.getDocumentElement();
            if (!"tbl".equals(tbl.getLocalName())) {
                return;
            }

            NodeList rows = tbl.getElementsByTagNameNS("*", "tr");
            if (index >= rows.getLength()) {
                return;
            }

            Element rowToRemove = (Element) rows.item(index);
            tbl.removeChild(rowToRemove);

            applyDocumentToXmlObject(table.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "removeTableRow",
                "Failed to remove table row at index " + index, e);
        }
    }

    /**
     * Inserts a new column into a table at the specified index.
     *
     * <p>This method adds a new grid column definition and inserts a cell into each existing row
     * at the given position. The new cells are cloned from the first cell of each row, with their
     * text content cleared. The operation modifies the underlying XML of the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFTable table = (XSLFTable) slide.getShapes().get(0);
     * XmlUtils.insertTableColumn(table, 1); // Insert a column after the first column
     * }</pre>
     *
     * @param table the table to modify; may be null.
     * @param index the insertion index (0-based); must be between 0 and the current number of columns inclusive.
     */
    static void insertTableColumn(org.apache.poi.xslf.usermodel.XSLFTable table, int index) {
        if (table == null || index < 0 || index > table.getNumberOfColumns()) {
            return;
        }

        try {
            Document doc = parseXmlObject(table.getXmlObject());
            if (doc == null) {
                return;
            }

            Element tbl = doc.getDocumentElement();
            if (!"tbl".equals(tbl.getLocalName())) {
                return;
            }

            // Locate the tableGrid element
            Element tableGrid = findFirstElementByLocalName(tbl, "tableGrid");
            if (tableGrid == null) {
                return;
            }

            // Find existing grid columns
            NodeList gridCols = tableGrid.getElementsByTagNameNS("*", "gridCol");
            if (gridCols.getLength() == 0) {
                return;
            }

            // Clone the first grid column as a template
            Element sourceCol = (Element) gridCols.item(0);
            Element newGridCol = (Element) sourceCol.cloneNode(false);

            // Insert the new grid column at the specified position
            if (index < gridCols.getLength()) {
                Element targetCol = (Element) gridCols.item(index);
                tableGrid.insertBefore(newGridCol, targetCol);
            } else {
                tableGrid.appendChild(newGridCol);
            }

            // Insert a cell into each row at the same column index
            NodeList rows = tbl.getElementsByTagNameNS("*", "tr");
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                NodeList cells = row.getElementsByTagNameNS("*", "tc");

                // Clone the first cell as a template if any cells exist
                if (cells.getLength() > 0) {
                    Element sourceCell = (Element) cells.item(0);
                    Element newCell = (Element) sourceCell.cloneNode(true);

                    // Clear text content in the new cell
                    NodeList txBodies = newCell.getElementsByTagNameNS("*", "txBody");
                    for (int j = 0; j < txBodies.getLength(); j++) {
                        Element txBody = (Element) txBodies.item(j);
                        NodeList children = txBody.getChildNodes();
                        List<Node> toRemove = new ArrayList<>();
                        for (int k = 0; k < children.getLength(); k++) {
                            Node child = children.item(k);
                            if (child instanceof Element) {
                                Element elem = (Element) child;
                                if ("p".equals(elem.getLocalName())) {
                                    toRemove.add(child);
                                }
                            }
                        }
                        for (Node node : toRemove) {
                            txBody.removeChild(node);
                        }
                        // Add a single empty paragraph
                        Element emptyPara = doc.createElementNS(DRAWINGML_NS, "a:p");
                        txBody.appendChild(emptyPara);
                    }

                    // Insert the new cell at the specified position
                    if (index < cells.getLength()) {
                        Element targetCell = (Element) cells.item(index);
                        row.insertBefore(newCell, targetCell);
                    } else {
                        row.appendChild(newCell);
                    }
                }
            }

            applyDocumentToXmlObject(table.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "insertTableColumn",
                "Failed to insert table column at index " + index, e);
        }
    }

    /**
     * Removes a column from a table at the specified index.
     *
     * <p>This method deletes the column element at the given position from the table's XML structure,
     * including both the grid column definition and all corresponding cells in each row.
     * The operation modifies the underlying XML of the table.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFTable table = (XSLFTable) slide.getShapes().get(0);
     * XmlUtils.removeTableColumn(table, 0); // Remove the first column
     * }</pre>
     *
     * @param table the table to modify; may be null.
     * @param index the removal index (0-based); must be between 0 and the current number of columns exclusive.
     */
    static void removeTableColumn(org.apache.poi.xslf.usermodel.XSLFTable table, int index) {
        if (table == null || index < 0 || index >= table.getNumberOfColumns()) {
            return;
        }

        try {
            Document doc = parseXmlObject(table.getXmlObject());
            if (doc == null) {
                return;
            }

            Element tbl = doc.getDocumentElement();
            if (!"tbl".equals(tbl.getLocalName())) {
                return;
            }

            // Remove the grid column definition
            Element tableGrid = findFirstElementByLocalName(tbl, "tableGrid");
            if (tableGrid != null) {
                NodeList gridCols = tableGrid.getElementsByTagNameNS("*", "gridCol");
                if (index < gridCols.getLength()) {
                    Element colToRemove = (Element) gridCols.item(index);
                    tableGrid.removeChild(colToRemove);
                }
            }

            // Remove corresponding cells from each row
            NodeList rows = tbl.getElementsByTagNameNS("*", "tr");
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                NodeList cells = row.getElementsByTagNameNS("*", "tc");
                if (index < cells.getLength()) {
                    Element cellToRemove = (Element) cells.item(index);
                    row.removeChild(cellToRemove);
                }
            }

            applyDocumentToXmlObject(table.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "removeTableColumn",
                "Failed to remove table column at index " + index, e);
        }
    }

    // ==================== Group Shape Manipulation ====================

    /**
     * Ungroups a group shape, moving its child shapes directly onto the slide.
     *
     * <p>This method extracts all child shapes from a group, transforms their coordinates
     * from the group's local coordinate system to the slide's coordinate system,
     * and removes the group container from the slide's shape tree. The child shapes
     * retain their formatting and properties.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFGroupShape group = (XSLFGroupShape) slide.getShapes().get(0);
     * XmlUtils.ungroupShapes(slide, group);
     * }</pre>
     *
     * @param slide the slide containing the group; may be null.
     * @param group the group shape to ungroup; may be null.
     */
    static void ungroupShapes(XSLFSlide slide, org.apache.poi.xslf.usermodel.XSLFGroupShape group) {
        if (slide == null || group == null) {
            return;
        }

        try {
            // Collect child shapes before modifying the group
            java.util.List<org.apache.poi.xslf.usermodel.XSLFShape> children =
                new java.util.ArrayList<>(group.getShapes());

            if (children.isEmpty()) {
                return;
            }

            // Parse the group's XML to extract its transformation
            Document groupDoc = parseXmlObject(group.getXmlObject());
            if (groupDoc == null) {
                return;
            }

            // Extract group offset (xfrm/off)
            Element grpSpPr = findFirstElementByLocalName(groupDoc.getDocumentElement(), "grpSpPr");
            long groupX = 0, groupY = 0;
            if (grpSpPr != null) {
                Element xfrm = findFirstElementByLocalName(grpSpPr, "xfrm");
                if (xfrm != null) {
                    Element off = findFirstElementByLocalName(xfrm, "off");
                    if (off != null) {
                        try {
                            groupX = Long.parseLong(off.getAttribute("x"));
                            groupY = Long.parseLong(off.getAttribute("y"));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            // Parse the slide's XML to access the shape tree
            Document slideDoc = parseXmlObject(slide.getXmlObject());
            if (slideDoc == null) {
                return;
            }

            Element spTree = findFirstElementByLocalName(slideDoc.getDocumentElement(), "spTree");
            if (spTree == null) {
                return;
            }

            // Transform each child shape's coordinates
            for (org.apache.poi.xslf.usermodel.XSLFShape child : children) {
                try {
                    Document childDoc = parseXmlObject(child.getXmlObject());
                    if (childDoc == null) {
                        continue;
                    }

                    Element childElement = childDoc.getDocumentElement();
                    Element childXfrm = findFirstElementByLocalName(childElement, "xfrm");

                    if (childXfrm != null) {
                        Element off = findFirstElementByLocalName(childXfrm, "off");
                        if (off != null) {
                            try {
                                // Read child's local coordinates
                                long childX = Long.parseLong(off.getAttribute("x"));
                                long childY = Long.parseLong(off.getAttribute("y"));

                                // Convert to slide coordinates
                                off.setAttribute("x", String.valueOf(groupX + childX));
                                off.setAttribute("y", String.valueOf(groupY + childY));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                    applyDocumentToXmlObject(child.getXmlObject(), childDoc);
                } catch (Exception e) {
                    PotLogger.warn(XmlUtils.class, "ungroupShapes",
                        "Failed to transform child shape coordinates", e);
                }
            }

            // Remove the group element from the slide's shape tree
            Document parentDoc = parseXmlObject(slide.getXmlObject());
            if (parentDoc != null && spTree != null) {
                Element groupElement = findGroupElement(spTree, group.getShapeId());
                if (groupElement != null) {
                    spTree.removeChild(groupElement);
                    applyDocumentToXmlObject(slide.getXmlObject(), parentDoc);
                }
            }

        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "ungroupShapes",
                "Failed to ungroup shapes", e);
        }
    }

    private static Element findGroupElement(Element spTree, long groupId) {
        NodeList children = spTree.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            Element element = (Element) node;
            if ("grpSp".equals(element.getLocalName())) {
                NodeList cNvPrList = element.getElementsByTagNameNS("*", "cNvPr");
                if (cNvPrList.getLength() > 0) {
                    Element cNvPr = (Element) cNvPrList.item(0);
                    String idText = cNvPr.getAttribute("id");
                    try {
                        if (Long.parseLong(idText) == groupId) {
                            return element;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }

    // ==================== Slide Background Manipulation ====================

    private static final String PPTNS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    private static final javax.xml.namespace.QName BG_QNAME    = new javax.xml.namespace.QName(PPTNS, "bg");
    private static final javax.xml.namespace.QName BGPR_QNAME  = new javax.xml.namespace.QName(PPTNS, "bgPr");

    /**
     * Applies a custom fill to the slide background.
     *
     * <p>This method replaces the slide's background fill with custom XML content.
     * The fill XML must be a valid DrawingML fill element (e.g., solidFill, gradFill, blipFill).
     * The method ensures the necessary background structure exists and removes any existing fill.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String solidFillXml = "<a:solidFill><a:srgbClr val=\"FF0000\"/></a:solidFill>";
     * XmlUtils.applySlideBackground(slide, () -> solidFillXml);
     * }</pre>
     *
     * @param slide    the slide whose background to modify; may be null.
     * @param fillXmlFn a supplier that provides the fill XML string; may be null.
     */
    static void applySlideBackground(XSLFSlide slide,
                                     java.util.function.Supplier<String> fillXmlFn) {
        if (slide == null || fillXmlFn == null) return;
        try {
            String fillXml = fillXmlFn.get();
            if (fillXml == null || fillXml.isEmpty()) return;

            XmlObject xmlObj = slide.getXmlObject();
            String pDecl = "declare namespace p='" + PPTNS + "'; ";
            String aDecl = "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main'; ";

            // Step 1: Ensure p:bg / p:bgPr elements exist
            ensureSlideBgPr(xmlObj);

            // Step 2: Remove any existing p:bgRef
            deleteNodes(xmlObj, pDecl + "$this//p:bg/p:bgRef");

            // Step 3: Remove any existing fill from bgPr
            for (String tag : new String[]{"solidFill", "gradFill", "blipFill", "noFill", "pattFill"}) {
                deleteNodes(xmlObj, pDecl + aDecl + "$this//p:bgPr/a:" + tag);
            }

            // Step 4: Insert the new fill XML as a child of bgPr
            insertXml(xmlObj, pDecl + "$this//p:bgPr", fillXml, InsertPosition.CHILD);

        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "applySlideBackground",
                "Failed to apply slide background", e);
        }
    }

    /**
     * Ensures that a slide's XML object contains the required background structure.
     *
     * <p>This internal helper method creates the p:bg and p:bgPr elements if they do not exist.
     * It uses XmlCursor to navigate and modify the XML structure directly.</p>
     *
     * @param xmlObj the slide's XmlObject to modify.
     */
    private static void ensureSlideBgPr(XmlObject xmlObj) {
        String pDecl = "declare namespace p='" + PPTNS + "'; ";

        // Check if bgPr already exists
        XmlCursor chk = xmlObj.newCursor();
        chk.selectPath(pDecl + "$this//p:bgPr");
        boolean exists = chk.toNextSelection();
        chk.dispose();
        if (exists) return;

        // Create p:bg and p:bgPr if missing
        XmlCursor c = xmlObj.newCursor();
        try {
            // Navigate to p:cSld (slide content)
            if (!c.toFirstChild()) return;          // <p:sld> -> <p:cSld>
            // Ensure bg element exists under cSld
            if (!c.toChild(BG_QNAME)) {
                c.toFirstContentToken();
                c.insertElement(BG_QNAME);
                c.toPrevSibling();                  // Move back to bg
            }
            // Ensure bgPr element exists under bg
            if (!c.toChild(BGPR_QNAME)) {
                c.toFirstContentToken();
                c.insertElement(BGPR_QNAME);
            }
        } finally {
            c.dispose();
        }
    }

    // ==================== PotXml Utility Methods ====================

    /**
     * Patches an XML attribute value using an XPath expression.
     *
     * <p>This convenience method sets the {@code val} attribute of the node(s)
     * selected by the given XPath expression. It is equivalent to calling
     * {@link #setAttribute(XmlObject, String, String, String)} with {@code attrName="val"}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XmlUtils.patchXml(slide.getXmlObject(),
     *     "declare namespace a='...'; $this//a:solidFill/a:srgbClr",
     *     "FF0000");
     * }</pre>
     *
     * @param xmlObject the XmlObject to modify; may be null.
     * @param xpath     the XPath expression selecting the target node(s).
     * @param value     the new value for the {@code val} attribute.
     * @return {@code true} if the attribute was set on at least one node, {@code false} otherwise.
     * @see #setAttribute(XmlObject, String, String, String)
     */
    static boolean patchXml(XmlObject xmlObject, String xpath, String value) {
        return setAttribute(xmlObject, xpath, "val", value);
    }

    /**
     * Queries text values from an XmlObject using an XPath expression.
     *
     * <p>This method evaluates the XPath expression against the XmlObject and collects
     * the text value of each selected node. The result list preserves the order of
     * selection. If the XPath cannot be evaluated or no nodes match, an empty list
     * is returned.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * List<String> colors = XmlUtils.queryXml(shape.getXmlObject(),
     *     "declare namespace a='...'; $this//a:srgbClr/@val");
     * }</pre>
     *
     * @param xmlObject the XmlObject to query; may be null.
     * @param xpath     the XPath expression to evaluate.
     * @return a list of text values from the matched nodes; never null.
     */
    static java.util.List<String> queryXml(XmlObject xmlObject, String xpath) {
        java.util.List<String> results = new java.util.ArrayList<>();
        if (xmlObject == null) return results;
        try {
            XmlCursor cursor = xmlObject.newCursor();
            cursor.selectPath(prepareCursorXPath(xpath));
            while (cursor.toNextSelection()) {
                results.add(cursor.getTextValue());
            }
            cursor.dispose();
        } catch (Exception e) {
            // Logging is omitted for query failures as they are often expected.
        }
        return results;
    }

    /**
     * Replaces the entire XML content of a shape with new XML.
     *
     * <p>This method parses the provided XML string, validates its root element,
     * ensures necessary namespace declarations are present, and replaces the shape's
     * underlying XmlObject. The new XML must represent a valid OpenXML shape element
     * (e.g., {@code sp}, {@code grpSp}, {@code pic}, {@code tbl}).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String newXml = "<p:sp xmlns:p=\"...\" xmlns:a=\"...\">...</p:sp>";
     * boolean replaced = XmlUtils.replaceShapeXml(shape, newXml);
     * }</pre>
     *
     * @param shape      the shape whose XML should be replaced; may be null.
     * @param xmlContent the new XML content as a string; may be null or empty.
     * @return {@code true} if the replacement succeeded, {@code false} otherwise.
     * @throws PotException if the XML is malformed or the root element is invalid.
     */
    static boolean replaceShapeXml(XSLFShape shape, String xmlContent) {
        if (shape == null || xmlContent == null || xmlContent.trim().isEmpty()) {
            return false;
        }

        try {
            // Parse the new XML content
            Document newDoc = parseXml(xmlContent.trim());
            if (newDoc == null) {
                PotLogger.warn(XmlUtils.class, "replaceShapeXml",
                    "XML content is not valid XML");
                return false;
            }

            Element rootElement = newDoc.getDocumentElement();
            if (rootElement == null) {
                return false;
            }

            // Validate the root element local name
            String localName = rootElement.getLocalName();
            if (!isValidShapeElement(localName)) {
                PotLogger.warn(XmlUtils.class, "replaceShapeXml",
                    "Root element '" + localName + "' is not a valid shape element");
                return false;
            }

            // Ensure required namespace declarations are present
            ensureNamespaces(rootElement);

            // Serialize the modified document back to XML
            String serializedXml = serializeXml(newDoc);
            if (serializedXml == null || serializedXml.isEmpty()) {
                return false;
            }

            XmlObject newXmlObject = XmlObject.Factory.parse(serializedXml);
            shape.getXmlObject().set(newXmlObject);

            return true;
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "replaceShapeXml",
                "Failed to replace shape XML", e);
            return false;
        }
    }

    /**
     * Determines whether a local name corresponds to a valid OpenXML shape element.
     *
     * @param localName the local name to check.
     * @return {@code true} if the name is a recognized shape element, {@code false} otherwise.
     */
    private static boolean isValidShapeElement(String localName) {
        return "sp".equals(localName)           // simple shape
            || "grpSp".equals(localName)        // group shape
            || "pic".equals(localName)          // picture
            || "cxnSp".equals(localName)        // connector shape
            || "graphicFrame".equals(localName) // graphic frame (e.g., chart, table)
            || "tbl".equals(localName);         // table
    }

    /**
     * Ensures that a root element has the necessary namespace declarations for OpenXML.
     *
     * <p>If the element lacks a namespace URI, this method adds default namespace
     * declarations appropriate for its local name (PresentationML for shapes,
     * DrawingML for tables). This is a safety measure for XML fragments that may
     * omit namespace prefixes.</p>
     *
     * @param element the element to augment with namespace declarations.
     */
    private static void ensureNamespaces(Element element) {
        // Only add namespaces if the element itself has no namespace
        if (element.getNamespaceURI() == null || element.getNamespaceURI().isEmpty()) {
            String localName = element.getLocalName();
            if ("sp".equals(localName) || "grpSp".equals(localName) || "pic".equals(localName)) {
                // PresentationML shapes require p, a, and r namespaces
                element.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:p",
                    "http://schemas.openxmlformats.org/presentationml/2006/main"
                );
                element.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:a",
                    "http://schemas.openxmlformats.org/drawingml/2006/main"
                );
                element.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:r",
                    "http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                );
            } else if ("tbl".equals(localName)) {
                // DrawingML tables require the a namespace
                element.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:a",
                    "http://schemas.openxmlformats.org/drawingml/2006/main"
                );
            }
        }
    }

    /**
     * Validates that a string contains wellformed XML.
     *
     * <p>This method attempts to parse the string as XML; success indicates wellformedness.
     * It does not perform schema or DTD validation.</p>
     *
     * @param xmlContent the XML string to validate; may be null.
     * @return {@code true} if the string is wellformed XML, {@code false} otherwise.
     */
    static boolean validateXml(String xmlContent) {
        try {
            parseXml(xmlContent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates a CTSlide object against its XML schema.
     *
     * <p>This method calls the XmlBeans {@code validate()} method on the provided
     * CTSlide instance. If validation fails, the error message is returned;
     * otherwise {@code null} is returned.</p>
     *
     * @param ctSlide the CTSlide to validate; may be null.
     * @return {@code null} if validation succeeds, otherwise a descriptive error message.
     */
    static String validateCTSlide(org.openxmlformats.schemas.presentationml.x2006.main.CTSlide ctSlide) {
        if (ctSlide == null) return "CTSlide is null";
        try {
            ctSlide.validate();
            return null;
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "validateCTSlide",
                "CTSlide validation failed", e);
            return e.getMessage();
        }
    }

    /**
     * Formats an XML string with prettyprinting (indentation).
     *
     * <p>This method parses the input XML and reserializes it with indentation.
     * If parsing fails, the original string is returned unchanged.</p>
     *
     * @param xmlContent the XML string to format; may be null.
     * @return the formatted XML string, or the original string if formatting fails.
     */
    static String formatXml(String xmlContent) {
        try {
            Document doc = parseXml(xmlContent);
            if (doc == null) return xmlContent;
            return serializeXml(doc);
        } catch (Exception e) {
            return xmlContent;
        }
    }

    // ==================== Internal Helper Methods ====================

    /**
     * Safely sets a feature on a DocumentBuilderFactory, logging any failure.
     *
     * @param factory the factory to configure.
     * @param feature the feature name.
     * @param enabled the desired state.
     */
    private static void setFactoryFeature(DocumentBuilderFactory factory, String feature, boolean enabled) {
        try {
            factory.setFeature(feature, enabled);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "setFactoryFeature",
                "XML parser does not support feature: " + feature, e);
        }
    }

    private static void setFactoryAttribute(DocumentBuilderFactory factory, String attribute, String value) {
        try {
            factory.setAttribute(attribute, value);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "setFactoryAttribute",
                "XML parser does not support attribute: " + attribute, e);
        }
    }

    private static void applyGradientFill(XmlObject xmlObject, PotGradient gradient) {
        try {
            Document doc = parseXmlObject(xmlObject);
            if (doc == null) {
                return;
            }
            Element spPr = findFirstElementByLocalName(doc.getDocumentElement(), "spPr");
            if (spPr == null) {
                return;
            }

            removeDirectChildrenByLocalName(spPr,
                "noFill", "solidFill", "gradFill", "blipFill", "pattFill", "grpFill");

            Element gradFill = doc.createElementNS(DRAWINGML_NS, "a:gradFill");
            gradFill.setAttribute("rotWithShape", gradient.isRotateWithShape() ? "1" : "0");

            Element gsLst = doc.createElementNS(DRAWINGML_NS, "a:gsLst");
            List<PotGradient.GradientStop> stops = new ArrayList<>(gradient.getStops());
            stops.sort(Comparator.comparingDouble(PotGradient.GradientStop::getPosition));
            if (stops.isEmpty()) {
                stops.add(new PotGradient.GradientStop(0.0, PotColor.WHITE, 1.0));
                stops.add(new PotGradient.GradientStop(1.0, PotColor.BLACK, 1.0));
            } else if (stops.size() == 1) {
                PotGradient.GradientStop single = stops.get(0);
                stops.add(new PotGradient.GradientStop(1.0, single.getColor(), single.getOpacity()));
            }

            for (PotGradient.GradientStop stop : stops) {
                Element gs = doc.createElementNS(DRAWINGML_NS, "a:gs");
                gs.setAttribute("pos", Integer.toString(stop.getPositionPercent()));

                Element srgbClr = doc.createElementNS(DRAWINGML_NS, "a:srgbClr");
                srgbClr.setAttribute("val", stop.getColor().toHex());
                if (stop.getOpacity() < 1.0) {
                    Element alpha = doc.createElementNS(DRAWINGML_NS, "a:alpha");
                    alpha.setAttribute("val", Integer.toString(UnitConverter.fractionToEmuPercent(stop.getOpacity())));
                    srgbClr.appendChild(alpha);
                }

                gs.appendChild(srgbClr);
                gsLst.appendChild(gs);
            }
            gradFill.appendChild(gsLst);

            switch (gradient.getType()) {
                case LINEAR:
                    Element lin = doc.createElementNS(DRAWINGML_NS, "a:lin");
                    lin.setAttribute("ang", Integer.toString(UnitConverter.degreesToEmuAngle(gradient.getAngle())));
                    lin.setAttribute("scaled", gradient.isRotateWithShape() ? "1" : "0");
                    gradFill.appendChild(lin);
                    break;
                case RADIAL:
                case RECTANGULAR:
                case PATH:
                    Element path = doc.createElementNS(DRAWINGML_NS, "a:path");
                    if (gradient.getType() == PotGradient.Type.RADIAL) {
                        path.setAttribute("path", "circle");
                    } else if (gradient.getType() == PotGradient.Type.RECTANGULAR) {
                        path.setAttribute("path", "rect");
                    } else {
                        path.setAttribute("path", "shape");
                    }
                    gradFill.appendChild(path);
                    break;
                default:
                    break;
            }

            spPr.appendChild(gradFill);
            applyDocumentToXmlObject(xmlObject, doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "applyGradientFill",
                "Failed to apply gradient fill", e);
        }
    }

    private static Document parseXmlObject(XmlObject xmlObject) {
        if (xmlObject == null) {
            return null;
        }
        return parseXml(xmlObject.xmlText());
    }

    private static void applyDocumentToXmlObject(XmlObject target, Document doc) throws Exception {
        String updatedXml = serializeXml(doc);
        if (updatedXml == null || updatedXml.isEmpty()) {
            return;
        }
        XmlObject updatedObject = XmlObject.Factory.parse(updatedXml);
        target.set(updatedObject);
    }

    private static Element findFirstElementByLocalName(Element root, String... localNames) {
        if (root == null || localNames == null) {
            return null;
        }
        for (String localName : localNames) {
            NodeList nodeList = root.getElementsByTagNameNS("*", localName);
            if (nodeList.getLength() > 0 && nodeList.item(0) instanceof Element) {
                return (Element) nodeList.item(0);
            }
        }
        return null;
    }

    private static Element findDirectChildByLocalName(Element parent, String localName) {
        if (parent == null) {
            return null;
        }
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (localName.equals(element.getLocalName())) {
                    return element;
                }
            }
        }
        return null;
    }

    private static Element getOrCreateDirectChild(Element parent, String namespace, String localName) {
        Element existing = findDirectChildByLocalName(parent, localName);
        if (existing != null) {
            return existing;
        }
        Element created = parent.getOwnerDocument().createElementNS(namespace, "a:" + localName);
        parent.appendChild(created);
        return created;
    }

    private static void removeDirectChildrenByLocalName(Element parent, String... localNames) {
        if (parent == null || localNames == null) {
            return;
        }
        NodeList nodeList = parent.getChildNodes();
        List<Element> toRemove = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                for (String localName : localNames) {
                    if (localName.equals(element.getLocalName())) {
                        toRemove.add(element);
                        break;
                    }
                }
            }
        }
        for (Element element : toRemove) {
            parent.removeChild(element);
        }
    }

    private static void reorderShapeInSpTree(XSLFSlide slide, XSLFShape shape, ReorderMode mode) {
        if (slide == null || shape == null || mode == null) {
            return;
        }

        try {
            Document doc = parseXmlObject(slide.getXmlObject());
            if (doc == null) {
                return;
            }

            Element spTree = findFirstElementByLocalName(doc.getDocumentElement(), "spTree");
            if (spTree == null) {
                return;
            }

            List<Element> drawable = new ArrayList<>();
            NodeList children = spTree.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if (isDrawableShapeNode(element)) {
                        drawable.add(element);
                    }
                }
            }
            if (drawable.size() <= 1) {
                return;
            }

            int currentIndex = findDrawableIndexByShapeId(drawable, shape.getShapeId());
            if (currentIndex < 0) {
                return;
            }

            int targetIndex = currentIndex;
            switch (mode) {
                case TO_FRONT:
                    targetIndex = drawable.size() - 1;
                    break;
                case TO_BACK:
                    targetIndex = 0;
                    break;
                case FORWARD_ONE:
                    targetIndex = Math.min(drawable.size() - 1, currentIndex + 1);
                    break;
                case BACKWARD_ONE:
                    targetIndex = Math.max(0, currentIndex - 1);
                    break;
                default:
                    break;
            }
            if (targetIndex == currentIndex) {
                return;
            }

            Element moving = drawable.remove(currentIndex);
            drawable.add(targetIndex, moving);

            for (Element element : drawable) {
                spTree.removeChild(element);
            }

            Node insertBefore = findSpTreeTailAnchor(spTree);
            for (Element element : drawable) {
                if (insertBefore != null) {
                    spTree.insertBefore(element, insertBefore);
                } else {
                    spTree.appendChild(element);
                }
            }

            applyDocumentToXmlObject(slide.getXmlObject(), doc);
        } catch (Exception e) {
            PotLogger.warn(XmlUtils.class, "reorderShapeInSpTree",
                "Failed to reorder shape z-order", e);
        }
    }

    private static int findDrawableIndexByShapeId(List<Element> drawable, long shapeId) {
        for (int i = 0; i < drawable.size(); i++) {
            Element shapeElement = drawable.get(i);
            NodeList cNvPrList = shapeElement.getElementsByTagNameNS("*", "cNvPr");
            if (cNvPrList.getLength() == 0) {
                continue;
            }
            Node cNvPrNode = cNvPrList.item(0);
            if (!(cNvPrNode instanceof Element)) {
                continue;
            }
            Element cNvPr = (Element) cNvPrNode;
            String idText = cNvPr.getAttribute("id");
            if (idText == null || idText.isEmpty()) {
                continue;
            }
            try {
                if (Long.parseLong(idText) == shapeId) {
                   return i;
                }
            } catch (NumberFormatException ignored) {
                // Skip malformed node and continue scanning.
            }
        }
        return -1;
    }

    private static boolean isDrawableShapeNode(Element element) {
        String localName = element.getLocalName();
        return "sp".equals(localName)
            || "grpSp".equals(localName)
            || "graphicFrame".equals(localName)
            || "cxnSp".equals(localName)
            || "pic".equals(localName)
            || "contentPart".equals(localName);
    }

    private static Node findSpTreeTailAnchor(Element spTree) {
        NodeList children = spTree.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            Element element = (Element) node;
            String localName = element.getLocalName();
            boolean reserved = "nvGrpSpPr".equals(localName)
                || "grpSpPr".equals(localName)
                || isDrawableShapeNode(element);
            if (!reserved) {
                return element;
            }
        }
        return null;
    }

    private enum ReorderMode {
        TO_FRONT,
        TO_BACK,
        FORWARD_ONE,
        BACKWARD_ONE
    }

    private static String prepareCursorXPath(String xpath) {
        if (xpath == null || xpath.isEmpty()) {
            return xpath;
        }

        String trimmed = xpath.trim();
        if (trimmed.startsWith("declare namespace")) {
            return xpath;
        }

        StringBuilder declarations = new StringBuilder();
        for (Map.Entry<String, String> entry : NAMESPACE_MAP.entrySet()) {
            declarations.append("declare namespace ")
                .append(entry.getKey())
                .append("='")
                .append(entry.getValue())
                .append("'; ");
        }
        return declarations + xpath;
    }
}
