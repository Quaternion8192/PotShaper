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

/**
 * Enumerates the types of hyperlinks that can be associated with presentation elements.
 * <p>
 * This enum classifies hyperlinks into categories such as external web addresses, internal
 * navigation within the presentation, email links, file references, and placeholders.
 * It provides methods to determine if a link is external or internal and to infer the link type
 * from a raw address string based on common patterns and protocols.
 *
 * @author Quaternion8192
 * @since 1.0
 */
public enum PotLinkType {

    /**
     * A standard web URL using the HTTP or HTTPS protocol.
     */
    URL,

    /**
     * A link that targets a specific slide within the same presentation.
     */
    SLIDE,

    /**
     * A link that navigates to the first slide of the presentation.
     */
    FIRST_SLIDE,

    /**
     * A link that navigates to the last slide of the presentation.
     */
    LAST_SLIDE,

    /**
     * A link that navigates to the next slide relative to the current position.
     */
    NEXT_SLIDE,

    /**
     * A link that navigates to the previous slide relative to the current position.
     */
    PREVIOUS_SLIDE,

    /**
     * A link that references a local or network file, typically using a file:// URI or a file path.
     */
    FILE,

    /**
     * An email link using the mailto: protocol.
     */
    EMAIL,

    /**
     * A link that targets a named bookmark within the presentation document.
     */
    BOOKMARK,

    /**
     * Represents the absence of a meaningful link or an empty link address.
     */
    NONE;

    /**
     * Indicates whether this link type points to an external resource.
     * <p>
     * A link is considered external if it references a resource outside the presentation
     * document, such as a web page, a local file, or an email address.
     *
     * @return {@code true} if this link type is {@link #URL}, {@link #FILE}, or {@link #EMAIL};
     *         {@code false} otherwise.
     */
    public boolean isExternal() {
        return this == URL || this == FILE || this == EMAIL;
    }

    /**
     * Indicates whether this link type points to an internal location within the presentation.
     * <p>
     * A link is considered internal if it is used for navigation between slides or to
     * bookmarks within the same presentation document.
     *
     * @return {@code true} if this link type is {@link #SLIDE}, {@link #BOOKMARK},
     *         {@link #FIRST_SLIDE}, {@link #LAST_SLIDE}, {@link #NEXT_SLIDE}, or
     *         {@link #PREVIOUS_SLIDE}; {@code false} otherwise.
     */
    public boolean isInternal() {
        return this == SLIDE || this == BOOKMARK ||
               this == FIRST_SLIDE || this == LAST_SLIDE ||
               this == NEXT_SLIDE || this == PREVIOUS_SLIDE;
    }

    /**
     * Determines the appropriate link type by analyzing the given address string.
     * <p>
     * This method examines the address for known protocol prefixes, file path patterns,
     * and internal presentation navigation markers to classify the link. If the address
     * is {@code null} or empty, {@link #NONE} is returned. The matching is performed
     * case-insensitively.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotLinkType type1 = PotLinkType.fromAddress("https://example.com");
     * // type1 is PotLinkType.URL
     *
     * PotLinkType type2 = PotLinkType.fromAddress("mailto:user@example.com");
     * // type2 is PotLinkType.EMAIL
     *
     * PotLinkType type3 = PotLinkType.fromAddress("Slide5");
     * // type3 is PotLinkType.SLIDE
     * }</pre>
     *
     * @param address the raw hyperlink address string to be analyzed.
     * @return the inferred {@code PotLinkType}; never {@code null}.
     */
    public static PotLinkType fromAddress(String address) {
        if (address == null || address.isEmpty()) {
            return NONE;
        }

        String lower = address.toLowerCase();

        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return URL;
        }

        if (lower.startsWith("mailto:")) {
            return EMAIL;
        }

        if (lower.startsWith("file://") || lower.startsWith("file:///")) {
            return FILE;
        }

        // Detect PowerPoint internal slide jump actions or patterns containing "slide" followed by digits.
        if (lower.contains("ppaction://hlinksldjump") ||
            lower.contains("ppaction://hlinkshowjump") ||
            lower.matches(".*slide\\d+.*")) {
            return SLIDE;
        }

        // Detect bookmark references, typically starting with '#' or containing the word "bookmark".
        if (lower.startsWith("#") || lower.contains("bookmark")) {
            return BOOKMARK;
        }

        // Detect Windows drive paths (e.g., C:\...), Unix-style absolute paths, or UNC network paths.
        if (lower.matches("^[a-z]:\\\\.*") || lower.startsWith("/") || lower.startsWith("\\\\")) {
            return FILE;
        }

        // Default classification for any other non-empty address is a web URL.
        return URL;
    }
}