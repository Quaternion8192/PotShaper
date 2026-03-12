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

import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;

/**
 * Represents a chart element within a PowerPoint slide.
 *
 * <p>This class provides a wrapper around an Apache POI {@code XSLFChart} and its containing
 * graphic frame, enabling fluent manipulation of the chart's position, size, rotation, and
 * visual effects. It inherits common shape manipulation capabilities from {@link PotElement}
 * and returns {@code PotChart} from chainable methods for convenient builder-style usage.
 * Direct duplication of chart elements is not supported and will throw an exception.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotChart extends PotElement {

    private final XSLFChart chart;

    /**
     * Constructs a new {@code PotChart} instance.
     *
     * <p>Initializes the chart element by associating the provided POI objects and identifiers.
     * The underlying shape must be an {@code XSLFGraphicFrame} that contains the specified chart.</p>
     *
     * @param shape       the POI graphic frame shape that contains the chart
     * @param chart       the POI chart object representing the chart data and formatting
     * @param parentSlide the parent slide that contains this chart element
     * @param uuid        the unique identifier for this element
     */
    public PotChart(XSLFGraphicFrame shape, XSLFChart chart, PotSlide parentSlide, String uuid) {
        super(shape, parentSlide, uuid);
        this.chart = chart;
    }

    /**
     * Returns the underlying Apache POI chart object.
     *
     * <p>This method provides direct access to the POI {@code XSLFChart} instance, allowing
     * for low-level chart data and formatting manipulations that are not covered by the
     * higher-level {@code PotChart} API.</p>
     *
     * @return the {@code XSLFChart} instance associated with this element
     */
    public XSLFChart getChart() {
        return chart;
    }

    /**
     * Sets the position of this chart element to the specified coordinates.
     *
     * <p>This method updates both the X and Y coordinates of the chart's anchor point
     * simultaneously. The coordinates are typically in points relative to the slide's
     * top-left corner. The method is chainable, returning this {@code PotChart} instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotChart chart = slide.addChart(...);
     * chart.at(100.0, 150.0);
     * }</pre>
     *
     * @param x the new X-coordinate for the chart
     * @param y the new Y-coordinate for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #position(double, double)
     * @see #setX(double)
     * @see #setY(double)
     */
    @Override
    public PotChart at(double x, double y) {
        super.at(x, y);
        return this;
    }

    /**
     * Sets the X-coordinate of this chart element.
     *
     * <p>Updates the horizontal position of the chart's anchor point. The method is chainable,
     * returning this {@code PotChart} instance.</p>
     *
     * @param x the new X-coordinate for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #at(double, double)
     * @see #x(double)
     */
    @Override
    public PotChart setX(double x) {
        super.setX(x);
        return this;
    }

    /**
     * Sets the X-coordinate of this chart element (fluent alias for {@link #setX}).
     *
     * <p>This method provides a fluent-style alternative to {@code setX}. It updates the
     * horizontal position and returns this instance for chaining.</p>
     *
     * @param x the new X-coordinate for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #setX(double)
     * @see #at(double, double)
     */
    @Override
    public PotChart x(double x) {
        super.x(x);
        return this;
    }

    /**
     * Sets the Y-coordinate of this chart element.
     *
     * <p>Updates the vertical position of the chart's anchor point. The method is chainable,
     * returning this {@code PotChart} instance.</p>
     *
     * @param y the new Y-coordinate for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #at(double, double)
     * @see #y(double)
     */
    @Override
    public PotChart setY(double y) {
        super.setY(y);
        return this;
    }

    /**
     * Sets the Y-coordinate of this chart element (fluent alias for {@link #setY}).
     *
     * <p>This method provides a fluent-style alternative to {@code setY}. It updates the
     * vertical position and returns this instance for chaining.</p>
     *
     * @param y the new Y-coordinate for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #setY(double)
     * @see #at(double, double)
     */
    @Override
    public PotChart y(double y) {
        super.y(y);
        return this;
    }

    /**
     * Sets the position of this chart element to the specified coordinates.
     *
     * <p>This method updates both the X and Y coordinates of the chart's anchor point
     * simultaneously. It is functionally equivalent to {@link #at(double, double)} and is
     * provided for API consistency. The method is chainable.</p>
     *
     * @param x the new X-coordinate for the chart
     * @param y the new Y-coordinate for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #at(double, double)
     */
    @Override
    public PotChart position(double x, double y) {
        super.position(x, y);
        return this;
    }

    /**
     * Sets the width and height of this chart element.
     *
     * <p>This method updates the dimensions of the chart's bounding rectangle simultaneously.
     * The dimensions are typically specified in points. The method is chainable, returning
     * this {@code PotChart} instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotChart chart = slide.addChart(...);
     * chart.size(300.0, 200.0);
     * }</pre>
     *
     * @param width  the new width for the chart
     * @param height the new height for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #setWidth(double)
     * @see #setHeight(double)
     */
    @Override
    public PotChart size(double width, double height) {
        super.size(width, height);
        return this;
    }

    /**
     * Sets the width of this chart element.
     *
     * <p>Updates the horizontal dimension of the chart's bounding rectangle. The method is
     * chainable, returning this {@code PotChart} instance.</p>
     *
     * @param width the new width for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #size(double, double)
     * @see #width(double)
     */
    @Override
    public PotChart setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    /**
     * Sets the width of this chart element (fluent alias for {@link #setWidth}).
     *
     * <p>This method provides a fluent-style alternative to {@code setWidth}. It updates the
     * width and returns this instance for chaining.</p>
     *
     * @param width the new width for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #setWidth(double)
     * @see #size(double, double)
     */
    @Override
    public PotChart width(double width) {
        super.width(width);
        return this;
    }

    /**
     * Sets the height of this chart element.
     *
     * <p>Updates the vertical dimension of the chart's bounding rectangle. The method is
     * chainable, returning this {@code PotChart} instance.</p>
     *
     * @param height the new height for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #size(double, double)
     * @see #height(double)
     */
    @Override
    public PotChart setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    /**
     * Sets the height of this chart element (fluent alias for {@link #setHeight}).
     *
     * <p>This method provides a fluent-style alternative to {@code setHeight}. It updates the
     * height and returns this instance for chaining.</p>
     *
     * @param height the new height for the chart
     * @return this {@code PotChart} instance for method chaining
     * @see #setHeight(double)
     * @see #size(double, double)
     */
    @Override
    public PotChart height(double height) {
        super.height(height);
        return this;
    }

    /**
     * Sets the rotation angle of this chart element.
     *
     * <p>This method rotates the chart around its center by the specified angle, measured in
     * degrees clockwise. The method is chainable, returning this {@code PotChart} instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotChart chart = slide.addChart(...);
     * chart.rotate(45.0); // Rotates the chart 45 degrees clockwise
     * }</pre>
     *
     * @param angle the rotation angle in degrees clockwise
     * @return this {@code PotChart} instance for method chaining
     * @see #rotation(double)
     */
    @Override
    public PotChart rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    /**
     * Sets the rotation angle of this chart element (fluent alias for {@link #rotate}).
     *
     * <p>This method provides a fluent-style alternative to {@code rotate}. It sets the
     * rotation angle and returns this instance for chaining.</p>
     *
     * @param angle the rotation angle in degrees clockwise
     * @return this {@code PotChart} instance for method chaining
     * @see #rotate(double)
     */
    @Override
    public PotChart rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    /**
     * Throws an {@code UnsupportedOperationException} as chart duplication is not supported.
     *
     * <p>This operation is intentionally unsupported for {@code PotChart} instances due to
     * complexities in duplicating chart data and formatting within the underlying POI model.
     * Attempting to call this method will always result in an exception.</p>
     *
     * @return nothing; this method always throws an exception
     * @throws PotException always, with an "unsupported operation" message
     * @see PotException#unsupportedOperation(String)
     */
    @Override
    public PotChart duplicate() {
        throw PotException.unsupportedOperation("duplicate for PotChart");
    }

    /**
     * Applies an animation to this chart element.
     *
     * <p>This method associates the specified animation with the chart, which will be played
     * during slide show presentation according to the animation's timing and effect properties.
     * The method delegates to the parent class implementation and is chainable.</p>
     *
     * @param animation the animation to apply to this chart
     * @return this {@code PotChart} instance for method chaining
     * @see PotElement#animate(PotAnimation)
     */
    @Override
    public PotChart animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    /**
     * Attaches a hyperlink to this chart element.
     *
     * <p>This method sets a hyperlink that will be activated when the chart is clicked during
     * a slide show. The hyperlink can point to a URL, another slide, or a custom action.
     * The method delegates to the parent class implementation and is chainable.</p>
     *
     * @param link the hyperlink to attach to this chart
     * @return this {@code PotChart} instance for method chaining
     * @see PotElement#hyperlink(PotHyperlink)
     */
    @Override
    public PotChart hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    /**
     * Applies a shadow effect to this chart element.
     *
     * <p>This method sets a visual shadow effect for the chart, controlling properties such
     * as blur, distance, angle, and color. The method delegates to the parent class
     * implementation and is chainable.</p>
     *
     * @param shadow the shadow effect to apply to this chart
     * @return this {@code PotChart} instance for method chaining
     * @see PotElement#shadow(PotShadow)
     */
    @Override
    public PotChart shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    /**
     * Returns the underlying Apache POI graphic frame shape that contains this chart.
     *
     * <p>This method provides direct access to the POI {@code XSLFGraphicFrame} instance
     * that serves as the container for the chart. This can be useful for low-level
     * manipulations of the shape's properties not exposed by the {@code PotChart} API.</p>
     *
     * @return the {@code XSLFGraphicFrame} instance that contains this chart
     * @see PotElement#getRawShape()
     */
    @Override
    public XSLFGraphicFrame getRawShape() {
        return (XSLFGraphicFrame) super.getRawShape();
    }
}