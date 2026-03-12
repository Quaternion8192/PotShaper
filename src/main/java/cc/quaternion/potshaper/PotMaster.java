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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a slide master within a presentation, providing access to layouts, theme properties, and visual elements.
 *
 * <p>A slide master defines the overall design template for a presentation, including background styles, color schemes,
 * font families, and placeholder arrangements. All slide layouts are derived from a master, ensuring visual consistency
 * across slides. This class wraps the underlying Apache POI {@code XSLFSlideMaster} to offer a more convenient and
 * type-safe API for accessing and manipulating master-level properties.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * List<PotMaster> masters = ppt.getMasters();
 * PotMaster master = masters.get(0);
 * System.out.println("Master name: " + master.getName());
 * }</pre>
 *
 * <h3>Layout Access Example</h3>
 * <pre>{@code
 * List<PotLayout> layouts = master.getLayouts();
 * for (PotLayout layout : layouts) {
 *     System.out.println("Layout: " + layout.getName());
 * }
 *
 * // Retrieve a specific layout by name
 * PotLayout titleLayout = master.getLayout("Title Slide");
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotMaster {

    // ==================== Fields ====================

    /** The underlying Apache POI slide master object. */
    private final XSLFSlideMaster master;

    /** The parent presentation that contains this master. */
    private final PotPresentation presentation;

    // ==================== Constructors ====================

    /**
     * Constructs a new {@code PotMaster} wrapper for the specified Apache POI slide master.
     *
     * <p>This constructor is package-private as {@code PotMaster} instances should be obtained through the
     * {@link PotPresentation} API. Both parameters must be non-null.</p>
     *
     * @param master       the Apache POI slide master to wrap
     * @param presentation the parent presentation
     * @throws NullPointerException if {@code master} or {@code presentation} is {@code null}
     */
    PotMaster(XSLFSlideMaster master, PotPresentation presentation) {
        this.master = Objects.requireNonNull(master, "master cannot be null");
        this.presentation = Objects.requireNonNull(presentation, "presentation cannot be null");
    }

    // ==================== Basic Properties ====================

    /**
     * Returns the display name of this master, typically derived from its theme.
     *
     * <p>The name is obtained from the associated {@code XSLFTheme}. If the theme is unavailable or an error occurs,
     * the string "Unknown" is returned.</p>
     *
     * @return the master name, or "Unknown" if it cannot be determined
     */
    public String getName() {
        try {
            XSLFTheme theme = master.getTheme();
            return theme != null ? theme.getName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Returns the name of the theme associated with this master.
     *
     * <p>This method retrieves the theme name directly. If no theme is associated or an error occurs, {@code null}
     * is returned.</p>
     *
     * @return the theme name, or {@code null} if not available
     */
    public String getThemeName() {
        try {
            XSLFTheme theme = master.getTheme();
            return theme != null ? theme.getName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== Layout Management ====================

    /**
     * Returns a list of all slide layouts defined by this master.
     *
     * <p>The layouts are wrapped as {@link PotLayout} objects. If the master contains no layouts, an empty list is
     * returned.</p>
     *
     * @return an unmodifiable list of layouts belonging to this master, never {@code null}
     */
    public List<PotLayout> getLayouts() {
        XSLFSlideLayout[] layouts = master.getSlideLayouts();
        if (layouts == null) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(layouts)
            .map(layout -> new PotLayout(layout, this))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific layout by its name.
     *
     * <p>The search is case-sensitive. If no layout with the given name exists, {@code null} is returned.</p>
     *
     * @param name the name of the layout to find
     * @return the corresponding {@code PotLayout}, or {@code null} if not found
     */
    public PotLayout getLayout(String name) {
        if (name == null) return null;
        
        for (XSLFSlideLayout layout : master.getSlideLayouts()) {
            if (name.equals(layout.getName())) {
                return new PotLayout(layout, this);
            }
        }
        return null;
    }

    /**
     * Retrieves a layout by its predefined slide layout type.
     *
     * <p>This method uses the Apache POI {@link SlideLayout} enumeration to identify the layout. If the master does not
     * contain a layout of the specified type, {@code null} is returned.</p>
     *
     * @param type the slide layout type to find
     * @return the corresponding {@code PotLayout}, or {@code null} if not found
     */
    public PotLayout getLayout(org.apache.poi.xslf.usermodel.SlideLayout type) {
        if (type == null) return null;
        
        XSLFSlideLayout layout = master.getLayout(type);
        return layout != null ? new PotLayout(layout, this) : null;
    }

    /**
     * Returns the number of layouts defined by this master.
     *
     * @return the count of layouts
     */
    public int getLayoutCount() {
        XSLFSlideLayout[] layouts = master.getSlideLayouts();
        return layouts != null ? layouts.length : 0;
    }

    // ==================== Background ====================

    /**
     * Returns the background object of this master.
     *
     * <p>The background defines fill color, pattern, or image for slides based on this master. Modifications to the
     * returned object affect all slides using this master, unless overridden at the slide level.</p>
     *
     * @return the Apache POI {@code XSLFBackground} object, or {@code null} if the master has no background
     */
    public XSLFBackground getBackground() {
        return master.getBackground();
    }

    /**
     * Sets the background fill color for this master.
     *
     * <p>If the master has a background object, its fill color is updated. If {@code color} is {@code null}, no action
     * is taken. This change propagates to all slides using this master unless they have individual background
     * overrides.</p>
     *
     * @param color the new background color
     * @return this {@code PotMaster} instance for method chaining
     */
    public PotMaster setBackground(PotColor color) {
        if (color != null) {
            XSLFBackground bg = master.getBackground();
            if (bg != null) {
                bg.setFillColor(color.toAwtColor());
            }
        }
        return this;
    }

    // ==================== Shapes ====================

    /**
     * Returns all shapes defined directly on the master slide.
     *
     * <p>These shapes are typically placeholders (e.g., title, content) or decorative elements that appear on every
     * slide based on this master. The list is directly from the underlying Apache POI object.</p>
     *
     * @return a list of shapes on the master slide
     */
    public List<XSLFShape> getShapes() {
        return master.getShapes();
    }

    /**
     * Returns the number of shapes defined directly on the master slide.
     *
     * @return the count of shapes
     */
    public int getElementCount() {
        return master.getShapes().size();
    }

    // ==================== Theme Colors ====================

    /**
     * Retrieves a theme color by its index.
     *
     * <p>Theme colors are defined in the presentation's theme and are referenced by index (0-11) in slide elements.
     * This method currently returns {@code null} as direct access via {@code XSLFTheme.getColor()} is not fully
     * supported; future implementations may provide this functionality.</p>
     *
     * @param index the color index, typically 0-11
     * @return the theme color, or {@code null} if not available
     */
    public PotColor getThemeColor(int index) {
        // XSLFTheme.getColor() returns null in current POI versions
        // Implementation may be added later via direct XML access
        return null;
    }

    // ==================== Fonts ====================

    /**
     * Returns the major (heading) font family defined by the master's theme.
     *
     * <p>The major font is used for titles and headings. If the theme is unavailable or an error occurs, {@code null}
     * is returned.</p>
     *
     * @return the major font family name, or {@code null}
     */
    public String getMajorFont() {
        try {
            XSLFTheme theme = master.getTheme();
            if (theme != null) {
                return theme.getMajorFont();
            }
        } catch (Exception e) {
            // Ignore and return null
        }
        return null;
    }

    /**
     * Returns the minor (body) font family defined by the master's theme.
     *
     * <p>The minor font is used for body text and content. If the theme is unavailable or an error occurs, {@code null}
     * is returned.</p>
     *
     * @return the minor font family name, or {@code null}
     */
    public String getMinorFont() {
        try {
            XSLFTheme theme = master.getTheme();
            if (theme != null) {
                return theme.getMinorFont();
            }
        } catch (Exception e) {
            // Ignore and return null
        }
        return null;
    }

    // ==================== Raw Access ====================

    /**
     * Returns the underlying Apache POI {@code XSLFSlideMaster} object.
     *
     * <p>This provides direct access to the POI API for advanced operations not covered by this wrapper.</p>
     *
     * @return the raw {@code XSLFSlideMaster}
     */
    public XSLFSlideMaster getRawMaster() {
        return master;
    }

    /**
     * Returns the underlying Apache POI {@code XSLFTheme} object.
     *
     * <p>The theme contains color schemes, font definitions, and other design elements. May be {@code null} if the
     * master has no theme.</p>
     *
     * @return the raw {@code XSLFTheme}, or {@code null}
     */
    public XSLFTheme getRawTheme() {
        return master.getTheme();
    }

    /**
     * Returns the parent presentation that contains this master.
     *
     * @return the parent {@code PotPresentation}
     */
    public PotPresentation getPresentation() {
        return presentation;
    }

    // ==================== Object Overrides ====================

    /**
     * Returns a string representation of this master.
     *
     * @return a string containing the master name and layout count
     */
    @Override
    public String toString() {
        return String.format("PotMaster{name=%s, layouts=%d}",
            getName(), getLayoutCount());
    }

    /**
     * Compares this master with another object for equality.
     *
     * <p>Two {@code PotMaster} objects are considered equal if they wrap the same underlying {@code XSLFSlideMaster}
     * instance.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotMaster potMaster = (PotMaster) o;
        return Objects.equals(master, potMaster.master);
    }

    /**
     * Returns a hash code based on the underlying {@code XSLFSlideMaster}.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(master);
    }
}