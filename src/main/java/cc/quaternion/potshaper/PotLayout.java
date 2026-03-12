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

import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * Represents a slide layout within a PowerPoint presentation, providing a higher-level abstraction over Apache POI's XSLFSlideLayout.
 *
 * <p>A slide layout defines the arrangement and types of placeholders (like title, content, images) on a slide.
 * Each layout belongs to a specific master slide ({@link PotMaster}) and can be applied to individual slides
 * to establish their visual structure. This class offers convenient methods to inspect layout properties,
 * manage placeholder relationships, and control background inheritance from the master slide.</p>
 *
 * <h3>Retrieving Layouts</h3>
 * <pre>{@code
 * // Get a specific layout by name from a master
 * PotLayout layout = master.getLayout("Title Slide");
 *
 * // Get all layouts from a presentation
 * List<PotLayout> allLayouts = ppt.getLayouts();
 * }</pre>
 *
 * <h3>Applying Layouts</h3>
 * <pre>{@code
 * // Apply a layout to a slide
 * slide.applyLayout(layout);
 *
 * // Alternative method to apply layout
 * layout.applyTo(slide);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotLayout {

    // ==================== Fields ====================

    /** The underlying Apache POI slide layout object. */
    private final XSLFSlideLayout layout;

    /** The master slide to which this layout belongs. */
    private final PotMaster master;

    // ==================== Constructors ====================

    /**
     * Constructs a new PotLayout wrapper around a POI slide layout.
     *
     * <p>This constructor is package-private as layouts should be obtained through
     * {@link PotMaster#getLayout(String)} or {@link PotPresentation#getLayouts()}.</p>
     *
     * @param layout the Apache POI XSLFSlideLayout to wrap, must not be null
     * @param master the master slide that owns this layout, must not be null
     * @throws NullPointerException if either parameter is null
     */
    PotLayout(XSLFSlideLayout layout, PotMaster master) {
        this.layout = Objects.requireNonNull(layout, "layout cannot be null");
        this.master = Objects.requireNonNull(master, "master cannot be null");
    }

    // ==================== Basic Properties ====================

    /**
     * Returns the name of this layout.
     *
     * <p>The name is typically descriptive, such as "Title Slide", "Title and Content",
     * or "Blank". Custom layouts may have user-defined names.</p>
     *
     * @return the layout name, never null
     */
    public String getName() {
        return layout.getName();
    }

    /**
     * Returns the standardized type of this layout.
     *
     * <p>The type is an enumeration that categorizes the layout according to PowerPoint's
     * predefined layout types. Returns null for custom layouts that don't match
     * a standard type.</p>
     *
     * @return the SlideLayout enum value, or null for custom layouts
     * @see SlideLayout
     */
    public SlideLayout getType() {
        return layout.getType();
    }

    /**
     * Returns the type name of this layout as a string.
     *
     * <p>For standard layouts, this returns the enum constant name (e.g., "TITLE").
     * For custom layouts, returns "CUSTOM". This method never returns null.</p>
     *
     * @return the type name string, "CUSTOM" if getType() returns null
     */
    public String getTypeName() {
        SlideLayout type = layout.getType();
        return type != null ? type.name() : "CUSTOM";
    }

    /**
     * Returns the master slide that owns this layout.
     *
     * <p>Every layout belongs to exactly one master slide. The master defines
     * shared design elements that layouts may inherit.</p>
     *
     * @return the parent PotMaster instance
     */
    public PotMaster getMaster() {
        return master;
    }

    // ==================== Layout Application ====================

    /**
     * Applies this layout to the specified slide.
     *
     * <p>This method establishes a relationship between the slide and this layout
     * in the underlying OPC package. It replaces any existing layout relationship
     * on the slide. If the slide parameter is null, this method does nothing.
     * Application failures are logged as warnings but do not throw exceptions.</p>
     *
     * @param slide the target slide to receive this layout, may be null
     * @see PotSlide#applyLayout(PotLayout)
     */
    public void applyTo(PotSlide slide) {
        if (slide == null) {
            return;
        }
        try {
            XSLFSlide rawSlide = slide.getRawSlide();
            PackagePart slidePart = rawSlide.getPackagePart();
            PackagePart layoutPart = layout.getPackagePart();

            // Remove existing slideLayout relationship
            String slideLayoutRelType =
                "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout";

            String existingRelId = null;
            for (PackageRelationship rel : slidePart.getRelationships()) {
                if (slideLayoutRelType.equals(rel.getRelationshipType())) {
                    existingRelId = rel.getId();
                    break;
                }
            }

            if (existingRelId != null) {
                slidePart.removeRelationship(existingRelId);
            }

            // Add new relationship to this layout
            slidePart.addRelationship(
                layoutPart.getPartName(),
                org.apache.poi.openxml4j.opc.TargetMode.INTERNAL,
                slideLayoutRelType
            );
        } catch (Exception e) {
            PotLogger.warn(PotLayout.class, "applyTo",
                "Failed to apply layout to slide", e);
        }
    }

    // ==================== Placeholder Management ====================

    /**
     * Returns all text placeholders defined in this layout.
     *
     * <p>Placeholders are shapes specifically designated to hold text content,
     * such as titles, bullet points, or captions. This includes all shapes
     * of type XSLFTextShape that have a non-null text type.</p>
     *
     * @return a list of text placeholder shapes, never null but may be empty
     */
    public List<XSLFTextShape> getPlaceholders() {
        List<XSLFTextShape> placeholders = new ArrayList<>();
        for (XSLFShape shape : layout.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape textShape = (XSLFTextShape) shape;
                if (textShape.getTextType() != null) {
                    placeholders.add(textShape);
                }
            }
        }
        return placeholders;
    }

    /**
     * Returns the number of text placeholders in this layout.
     *
     * @return the count of text placeholder shapes
     */
    public int getPlaceholderCount() {
        return getPlaceholders().size();
    }

    /**
     * Returns the placeholder of the specified type, if present.
     *
     * <p>Searches through all shapes in the layout for a text shape matching
     * the given placeholder type. Common types include TITLE, BODY, CONTENT,
     * CENTERED_TITLE, etc.</p>
     *
     * @param type the placeholder type to find, may be null
     * @return the matching XSLFTextShape, or null if not found or type is null
     * @see org.apache.poi.sl.usermodel.Placeholder
     */
    public XSLFTextShape getPlaceholder(org.apache.poi.sl.usermodel.Placeholder type) {
        if (type == null) return null;
        
        for (XSLFShape shape : layout.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape textShape = (XSLFTextShape) shape;
                if (type.equals(textShape.getTextType())) {
                    return textShape;
                }
            }
        }
        return null;
    }

    /**
     * Checks whether this layout contains a title placeholder.
     *
     * <p>A title placeholder is either Placeholder.TITLE or Placeholder.CENTERED_TITLE.
     * This is useful for determining if a slide using this layout can display a title.</p>
     *
     * @return true if a title placeholder exists, false otherwise
     */
    public boolean hasTitlePlaceholder() {
        return getPlaceholder(org.apache.poi.sl.usermodel.Placeholder.TITLE) != null ||
               getPlaceholder(org.apache.poi.sl.usermodel.Placeholder.CENTERED_TITLE) != null;
    }

    /**
     * Checks whether this layout contains a content placeholder.
     *
     * <p>A content placeholder is either Placeholder.BODY (for bullet points) or
     * Placeholder.CONTENT (for general content). This indicates if the layout
     * supports main content areas.</p>
     *
     * @return true if a content placeholder exists, false otherwise
     */
    public boolean hasContentPlaceholder() {
        return getPlaceholder(org.apache.poi.sl.usermodel.Placeholder.BODY) != null ||
               getPlaceholder(org.apache.poi.sl.usermodel.Placeholder.CONTENT) != null;
    }

    // ==================== Shape Access ====================

    /**
     * Returns all shapes defined in this layout.
     *
     * <p>This includes both placeholder shapes and decorative shapes (lines,
     * images, etc.) that are part of the layout's design.</p>
     *
     * @return a list of all shapes in the layout, never null
     */
    public List<XSLFShape> getShapes() {
        return layout.getShapes();
    }

    /**
     * Returns the total number of shapes in this layout.
     *
     * @return the count of all shapes
     */
    public int getShapeCount() {
        return layout.getShapes().size();
    }

    // ==================== Background Management ====================

    /**
     * Returns the background of this layout.
     *
     * <p>The background may include fill colors, gradients, images, or patterns.
     * If the layout follows the master background, this may return a background
     * object that references the master's background settings.</p>
     *
     * @return the XSLFBackground object, or null if no specific background is set
     */
    public XSLFBackground getBackground() {
        return layout.getBackground();
    }

    /**
     * Determines whether this layout displays the master slide's background.
     *
     * <p>By default, layouts inherit the background from their master slide.
     * This method checks the "showMasterBg" XML attribute to determine if
     * inheritance is active. Returns true if the attribute is absent or set
     * to a truthy value ("1" or non-"0"/"false").</p>
     *
     * @return true if the master background is shown, false if the layout uses its own background
     */
    public boolean isFollowMasterBackground() {
        try (XmlCursor cursor = layout.getXmlObject().newCursor()) {
            cursor.toFirstContentToken();
            String showMasterBg = cursor.getAttributeText(new QName("showMasterBg"));
            return showMasterBg == null || !"0".equals(showMasterBg) && !"false".equalsIgnoreCase(showMasterBg);
        }
    }

    /**
     * Sets whether this layout should display the master slide's background.
     *
     * <p>When set to true, the layout will use the background defined in the
     * master slide. When false, the layout will use its own background
     * (as returned by {@link #getBackground()}). This modifies the underlying
     * XML directly.</p>
     *
     * @param follow true to show the master background, false to use layout-specific background
     * @return this PotLayout instance for method chaining
     */
    public PotLayout setFollowMasterBackground(boolean follow) {
        try (XmlCursor cursor = layout.getXmlObject().newCursor()) {
            cursor.toFirstContentToken();
            cursor.setAttributeText(new QName("showMasterBg"), follow ? "1" : "0");
        }
        return this;
    }

    // ==================== Raw Access ====================

    /**
     * Returns the underlying Apache POI XSLFSlideLayout object.
     *
     * <p>This provides direct access to POI's native API for advanced operations
     * not covered by PotLayout's abstraction. Use with caution as modifications
     * may affect PotLayout's internal state.</p>
     *
     * @return the wrapped XSLFSlideLayout instance
     */
    public XSLFSlideLayout getRawLayout() {
        return layout;
    }

    // ==================== Layout Type Checks ====================

    /**
     * Checks if this is a title slide layout.
     *
     * <p>Title slides typically contain only a title placeholder, often used
     * as the first slide in a presentation.</p>
     *
     * @return true if layout type is TITLE or TITLE_ONLY, false otherwise
     */
    public boolean isTitleSlide() {
        SlideLayout type = layout.getType();
        return type == SlideLayout.TITLE || type == SlideLayout.TITLE_ONLY;
    }

    /**
     * Checks if this is a title and content layout.
     *
     * <p>This common layout type includes both a title placeholder and a
     * content placeholder for bullet points or other main content.</p>
     *
     * @return true if layout type is TITLE_AND_CONTENT, false otherwise
     */
    public boolean isTitleAndContent() {
        SlideLayout type = layout.getType();
        return type == SlideLayout.TITLE_AND_CONTENT;
    }

    /**
     * Checks if this is a blank layout.
     *
     * <p>Blank layouts contain no placeholders, providing a completely empty
     * canvas for custom content arrangement.</p>
     *
     * @return true if layout type is BLANK, false otherwise
     */
    public boolean isBlank() {
        SlideLayout type = layout.getType();
        return type == SlideLayout.BLANK;
    }

    /**
     * Checks if this is a two-column layout.
     *
     * <p>Two-column layouts arrange content in two side-by-side sections.
     * This includes various subtypes like two text objects, two objects,
     * or combinations of text and objects.</p>
     *
     * @return true if layout type is TWO_TX_TWO_OBJ, TWO_OBJ, or TWO_OBJ_AND_TX
     */
    public boolean isTwoColumn() {
        SlideLayout type = layout.getType();
        return type == SlideLayout.TWO_TX_TWO_OBJ ||
               type == SlideLayout.TWO_OBJ ||
               type == SlideLayout.TWO_OBJ_AND_TX;
    }

    // ==================== Object Methods ====================

    /**
     * Returns a string representation of this layout.
     *
     * <p>The format includes the layout name, type, and placeholder count,
     * suitable for debugging and logging purposes.</p>
     *
     * @return a descriptive string representation
     */
    @Override
    public String toString() {
        return String.format("PotLayout{name='%s', type=%s, placeholders=%d}",
            getName(), getTypeName(), getPlaceholderCount());
    }

    /**
     * Compares this layout with another object for equality.
     *
     * <p>Two PotLayout instances are considered equal if they wrap the same
     * underlying XSLFSlideLayout object, regardless of their wrapper state.</p>
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotLayout potLayout = (PotLayout) o;
        return Objects.equals(layout, potLayout.layout);
    }

    /**
     * Returns a hash code value for this layout.
     *
     * <p>The hash code is based on the underlying XSLFSlideLayout object.</p>
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(layout);
    }
}