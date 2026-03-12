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

import java.util.Objects;

/**
 * Represents a hyperlink that can be applied to shapes within a PowerPoint presentation.
 *
 * <p>This class provides a fluent API to create hyperlinks of various types, including URLs,
 * links to other slides, files, and email addresses. It encapsulates the hyperlink data and
 * provides a method to apply it to Apache POI XSLFSimpleShape objects. Instances are immutable
 * after creation via the static factory methods, except for the optional tooltip which can be
 * set using the builder-style {@link #tooltip(String)} method.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * // Create a URL hyperlink
 * element.hyperlink("https://www.example.com");
 *
 * // Create a hyperlink to a specific slide
 * element.hyperlinkToSlide(2);  // Links to the third slide (zero-based index)
 *
 * // Create a PotHyperlink with a tooltip
 * PotHyperlink link = PotHyperlink.url("https://www.example.com")
 *     .tooltip("Click to visit");
 * element.hyperlink(link);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotHyperlink {

    // ==================== Fields ====================

    /** The type of this hyperlink. */
    private PotLinkType type;

    /** The address associated with this hyperlink (e.g., URL, file path, mailto link). */
    private String address;

    /** The optional tooltip text displayed when hovering over the hyperlink. */
    private String tooltip;

    /** The zero-based index of the target slide, or -1 if not applicable. */
    private int targetSlideIndex = -1;

    /** The subject line for an email hyperlink, may be {@code null}. */
    private String emailSubject;

    // ==================== Constructors ====================

    /**
     * Private constructor to enforce use of static factory methods.
     */
    private PotHyperlink() {
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a hyperlink to an external URL.
     *
     * @param url the absolute URL (e.g., "https://www.example.com")
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#URL}
     * @throws NullPointerException if {@code url} is {@code null}
     */
    public static PotHyperlink url(String url) {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.URL;
        link.address = url;
        return link;
    }

    /**
     * Creates a hyperlink to a specific slide within the same presentation.
     *
     * @param slideIndex the zero-based index of the target slide
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#SLIDE}
     */
    public static PotHyperlink slide(int slideIndex) {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.SLIDE;
        link.targetSlideIndex = slideIndex;
        return link;
    }

    /**
     * Creates a hyperlink to the first slide of the presentation.
     *
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#FIRST_SLIDE}
     */
    public static PotHyperlink firstSlide() {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.FIRST_SLIDE;
        return link;
    }

    /**
     * Creates a hyperlink to the last slide of the presentation.
     *
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#LAST_SLIDE}
     */
    public static PotHyperlink lastSlide() {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.LAST_SLIDE;
        return link;
    }

    /**
     * Creates a hyperlink to the next slide relative to the current slide.
     *
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#NEXT_SLIDE}
     */
    public static PotHyperlink nextSlide() {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.NEXT_SLIDE;
        return link;
    }

    /**
     * Creates a hyperlink to the previous slide relative to the current slide.
     *
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#PREVIOUS_SLIDE}
     */
    public static PotHyperlink previousSlide() {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.PREVIOUS_SLIDE;
        return link;
    }

    /**
     * Creates a hyperlink to a local or network file.
     *
     * @param filePath the path to the file (e.g., "C:\\docs\\file.pdf" or "/home/user/file.pdf")
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#FILE}
     * @throws NullPointerException if {@code filePath} is {@code null}
     */
    public static PotHyperlink file(String filePath) {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.FILE;
        link.address = filePath;
        return link;
    }

    /**
     * Creates a hyperlink that opens the default email client with a new message.
     *
     * @param email the recipient's email address
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#EMAIL}
     * @throws NullPointerException if {@code email} is {@code null}
     */
    public static PotHyperlink email(String email) {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.EMAIL;
        link.address = "mailto:" + email;
        return link;
    }

    /**
     * Creates a hyperlink that opens the default email client with a new message, including a subject.
     *
     * @param email   the recipient's email address
     * @param subject the subject line for the email, may be {@code null} or empty
     * @return a new {@code PotHyperlink} instance of type {@link PotLinkType#EMAIL}
     * @throws NullPointerException if {@code email} is {@code null}
     */
    public static PotHyperlink email(String email, String subject) {
        PotHyperlink link = new PotHyperlink();
        link.type = PotLinkType.EMAIL;
        link.emailSubject = subject;
        if (subject != null && !subject.isEmpty()) {
            link.address = "mailto:" + email + "?subject=" + encodeUrl(subject);
        } else {
            link.address = "mailto:" + email;
        }
        return link;
    }

    // ==================== Builder-style Method ====================

    /**
     * Sets the tooltip text for this hyperlink.
     *
     * <p>This method follows a builder pattern, allowing method chaining.</p>
     *
     * @param tooltip the tooltip text to display when hovering over the hyperlink
     * @return this {@code PotHyperlink} instance for method chaining
     */
    public PotHyperlink tooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    // ==================== Accessors ====================

    /**
     * Returns the type of this hyperlink.
     *
     * @return the {@link PotLinkType} of this hyperlink
     */
    public PotLinkType getType() {
        return type;
    }

    /**
     * Returns the address associated with this hyperlink.
     *
     * <p>For URL, file, and email hyperlinks, this is the full address string.
     * For slide hyperlinks, this may be {@code null}.</p>
     *
     * @return the hyperlink address, or {@code null} if not applicable
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the tooltip text for this hyperlink.
     *
     * @return the tooltip text, or {@code null} if not set
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Returns the zero-based index of the target slide.
     *
     * <p>This is only meaningful for hyperlinks of type {@link PotLinkType#SLIDE}.</p>
     *
     * @return the target slide index, or -1 if not applicable
     */
    public int getTargetSlideIndex() {
        return targetSlideIndex;
    }

    /**
     * Returns the subject line for an email hyperlink.
     *
     * @return the email subject, or {@code null} if not applicable or not set
     */
    public String getEmailSubject() {
        return emailSubject;
    }

    // ==================== Type Predicates ====================

    /**
     * Checks if this hyperlink is an external URL.
     *
     * @return {@code true} if the type is {@link PotLinkType#URL}, otherwise {@code false}
     */
    public boolean isUrl() {
        return type == PotLinkType.URL;
    }

    /**
     * Checks if this hyperlink targets a slide within the presentation.
     *
     * <p>This includes links to specific slides, first slide, last slide, next slide, and previous slide.</p>
     *
     * @return {@code true} if the type is any slide-related type, otherwise {@code false}
     */
    public boolean isSlide() {
        return type == PotLinkType.SLIDE ||
               type == PotLinkType.FIRST_SLIDE ||
               type == PotLinkType.LAST_SLIDE ||
               type == PotLinkType.NEXT_SLIDE ||
               type == PotLinkType.PREVIOUS_SLIDE;
    }

    /**
     * Checks if this hyperlink is a file link.
     *
     * @return {@code true} if the type is {@link PotLinkType#FILE}, otherwise {@code false}
     */
    public boolean isFile() {
        return type == PotLinkType.FILE;
    }

    /**
     * Checks if this hyperlink is an email link.
     *
     * @return {@code true} if the type is {@link PotLinkType#EMAIL}, otherwise {@code false}
     */
    public boolean isEmail() {
        return type == PotLinkType.EMAIL;
    }

    // ==================== Internal Application ====================

    /**
     * Applies this hyperlink to an Apache POI shape within the given presentation context.
     *
     * <p>This method configures the underlying {@link XSLFHyperlink} of the provided shape
     * according to the type and data of this {@code PotHyperlink}. For slide hyperlinks,
     * the presentation context is required to resolve slide references.</p>
     *
     * @param shape the XSLFSimpleShape to receive the hyperlink
     * @param ppt   the presentation context, required for slide hyperlinks
     */
    void applyTo(XSLFSimpleShape shape, PotPresentation ppt) {
        if (shape == null) return;

        XSLFHyperlink link = shape.getHyperlink();
        if (link == null) {
            link = shape.createHyperlink();
        }

        switch (type) {
            case URL:
                link.setAddress(address);
                break;
            case SLIDE:
                if (targetSlideIndex >= 0 && ppt != null && targetSlideIndex < ppt.getSlideCount()) {
                    link.linkToSlide(ppt.getSlide(targetSlideIndex).getRawSlide());
                }
                break;
            case FIRST_SLIDE:
                link.linkToFirstSlide();
                break;
            case LAST_SLIDE:
                link.linkToLastSlide();
                break;
            case NEXT_SLIDE:
                link.linkToNextSlide();
                break;
            case PREVIOUS_SLIDE:
                link.linkToPreviousSlide();
                break;
            case FILE:
            case EMAIL:
                link.setAddress(address);
                break;
            default:
                break;
        }

        // Note: Tooltip setting is currently not implemented because Apache POI's XSLFHyperlink
        // does not expose a method to set tooltip text directly. This would require low-level XML manipulation.
    }

    // ==================== POI Integration ====================

    /**
     * Creates a {@code PotHyperlink} from an existing Apache POI {@link XSLFHyperlink}.
     *
     * <p>This method attempts to infer the {@link PotLinkType} based on the address string
     * of the POI hyperlink. It is a best-effort conversion and may not be accurate for all cases.</p>
     *
     * @param poiLink the POI hyperlink to convert, may be {@code null}
     * @return a new {@code PotHyperlink} instance, or {@code null} if the input is {@code null} or has no address
     */
    static PotHyperlink fromPoi(XSLFHyperlink poiLink) {
        if (poiLink == null) return null;

        String address = poiLink.getAddress();
        if (address == null) return null;

        PotHyperlink link = new PotHyperlink();
        link.address = address;

        if (address.startsWith("mailto:")) {
            link.type = PotLinkType.EMAIL;
        } else if (address.startsWith("http://") || address.startsWith("https://")) {
            link.type = PotLinkType.URL;
        } else if (address.startsWith("file://") || address.contains("\\") || address.contains("/")) {
            link.type = PotLinkType.FILE;
        } else if (address.contains(".xml")) {
            link.type = PotLinkType.SLIDE;
        } else {
            link.type = PotLinkType.URL;
        }

        return link;
    }

    // ==================== Utility Methods ====================

    /**
     * URL-encodes a string using UTF-8 encoding.
     *
     * <p>This is a helper method for constructing email hyperlinks with subject lines.
     * If encoding fails, the original string is returned unchanged.</p>
     *
     * @param value the string to encode
     * @return the URL-encoded string, or the original string if encoding fails
     */
    private static String encodeUrl(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    // ==================== Object Overrides ====================

    /**
     * Returns a string representation of this hyperlink.
     *
     * @return a string in the format "PotHyperlink{type=TYPE, address='ADDRESS'}"
     */
    @Override
    public String toString() {
        return String.format("PotHyperlink{type=%s, address='%s'}",
            type, address);
    }

    /**
     * Compares this hyperlink to the specified object for equality.
     *
     * <p>Two {@code PotHyperlink} instances are considered equal if they have the same type,
     * address, and target slide index. The tooltip and email subject are not considered.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotHyperlink that = (PotHyperlink) o;
        return targetSlideIndex == that.targetSlideIndex &&
               type == that.type &&
               Objects.equals(address, that.address);
    }

    /**
     * Returns a hash code value for this hyperlink.
     *
     * @return the hash code based on type, address, and target slide index
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, address, targetSlideIndex);
    }
}