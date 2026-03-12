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
 * Represents a single graphical element within a presentation slide.
 * <p>
 * This interface defines the common properties and operations for any element that can be placed on a slide,
 * such as shapes, text boxes, or images. Each element has a unique identifier, a position, dimensions,
 * a name, and is associated with a specific slide. Implementations of this interface are responsible for
 * managing the element's lifecycle, including removal and duplication.
 * </p>
 *
 * @since 1.0
 */
public interface IPotElement {

    /**
     * Returns the unique identifier for this element.
     * <p>
     * The UUID is a string that uniquely identifies this element within the presentation.
     * It is typically used for serialization, referencing, and internal tracking.
     * </p>
     *
     * @return the unique identifier string for this element, never {@code null}.
     */
    String getUUID();

    /**
     * Returns the slide that contains this element.
     * <p>
     * This method provides access to the parent slide object to which this element belongs.
     * The element's position and visibility are relative to this slide.
     * </p>
     *
     * @return the {@link PotSlide} instance that owns this element.
     */
    PotSlide getSlide();

    /**
     * Returns the horizontal coordinate of the element's top-left corner.
     * <p>
     * The X-coordinate is measured in the slide's coordinate system, typically in points.
     * The origin (0,0) is at the top-left corner of the slide.
     * </p>
     *
     * @return the X-coordinate of the element's position.
     */
    double getX();

    /**
     * Returns the vertical coordinate of the element's top-left corner.
     * <p>
     * The Y-coordinate is measured in the slide's coordinate system, typically in points.
     * The origin (0,0) is at the top-left corner of the slide.
     * </p>
     *
     * @return the Y-coordinate of the element's position.
     */
    double getY();

    /**
     * Returns the width of the element.
     * <p>
     * The width is measured in the same units as the position coordinates (typically points).
     * It represents the horizontal extent of the element's bounding box.
     * </p>
     *
     * @return the width of the element.
     */
    double getWidth();

    /**
     * Returns the height of the element.
     * <p>
     * The height is measured in the same units as the position coordinates (typically points).
     * It represents the vertical extent of the element's bounding box.
     * </p>
     *
     * @return the height of the element.
     */
    double getHeight();

    /**
     * Returns the display name of the element.
     * <p>
     * The name is a human-readable label for the element, which may be used in user interfaces
     * or for debugging purposes. It does not need to be unique.
     * </p>
     *
     * @return the name of the element, may be {@code null} if not set.
     */
    String getName();

    /**
     * Permanently removes this element from its containing slide.
     * <p>
     * This operation detaches the element from its parent slide and should release any associated resources.
     * After removal, the element should generally not be used further. The behavior of calling other methods
     * after removal is implementation-dependent.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * IPotElement element = slide.getElementById("shape1");
     * element.remove();
     * // The element is now removed from the slide.
     * }</pre>
     */
    void remove();

    /**
     * Creates and returns a deep copy of this element.
     * <p>
     * The duplicated element is a new instance with identical properties (position, size, name, etc.)
     * but a newly generated UUID. The duplicate is placed on the same slide as the original element.
     * This method does not automatically add the duplicate to the slide; the caller is responsible for
     * any necessary registration.
     * </p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * IPotElement original = slide.getElementById("logo");
     * IPotElement copy = original.duplicate();
     * copy.setX(original.getX() + 100); // Position the copy offset from the original.
     * slide.addElement(copy); // Explicitly add the copy to the slide.
     * }</pre>
     *
     * @return a new {@code IPotElement} instance that is a duplicate of this element.
     */
    IPotElement duplicate();
}