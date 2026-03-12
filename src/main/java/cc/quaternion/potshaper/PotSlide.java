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

import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * Represents a single slide within a presentation, providing methods to add, retrieve, and manage visual elements.
 *
 * <p>This class serves as the primary interface for manipulating slide content, including shapes, text boxes,
 * images, tables, connectors, media, and charts. It maintains a cache of all elements on the slide, each assigned
 * a unique UUID for identification. The slide can be styled with backgrounds, transitions, notes, and layouts.
 * Elements can be grouped, searched, and managed collectively.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * PotPresentation pres = PotPresentation.create();
 * PotSlide slide = pres.addSlide();
 * PotTextBox title = slide.addTextBox("Presentation Title");
 * title.setFontSize(36).setBold(true);
 * PotImage logo = slide.addImage("/path/to/logo.png");
 * logo.setPosition(50, 50);
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotSlide {

    // ==================== Fields ====================

    /** The underlying Apache POI slide object. */
    private final XSLFSlide slide;

    /** The parent presentation that contains this slide. */
    private final PotPresentation presentation;

    /** The unique identifier for this slide. */
    private final String uuid;

    /** A cache of all elements present on this slide. */
    private final List<PotElement> elementCache;

    private static final byte[] DEFAULT_MEDIA_ICON = Base64.getDecoder().decode(
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII="
    );

    // ==================== Constructors ====================

    /**
     * Constructs a new slide wrapper for the given Apache POI slide.
     *
     * <p>This constructor is intended for internal use by the library. It initializes the slide's element cache
     * by scanning the underlying POI shapes and converting them into the appropriate {@link PotElement} subclasses.</p>
     *
     * @param slide         The Apache POI slide object to wrap.
     * @param presentation  The parent presentation that contains this slide.
     * @param uuid          The unique identifier for this slide.
     */
    PotSlide(XSLFSlide slide, PotPresentation presentation, String uuid) {
        this.slide = ValidationUtils.notNull(slide, "slide");
        this.presentation = ValidationUtils.notNull(presentation, "presentation");
        this.uuid = uuid;
        this.elementCache = new ArrayList<>();

        // Initialize the element cache from existing shapes
        initializeElementCache();
    }

    // ==================== Slide Properties ====================

    /**
     * Returns the unique identifier (UUID) of this slide.
     *
     * @return The UUID string of this slide.
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Returns the zero-based index of this slide within the presentation.
     *
     * @return The zero-based index.
     */
    public int getIndex() {
        List<PotSlide> slides = presentation.getSlides();
        return slides.indexOf(this);
    }

    /**
     * Returns the one-based slide number within the presentation.
     *
     * @return The one-based slide number.
     */
    public int getSlideNumber() {
        return getIndex() + 1;
    }

    /**
     * Returns the parent presentation that contains this slide.
     *
     * @return The parent PotPresentation instance.
     */
    public PotPresentation getPresentation() {
        return presentation;
    }

    // ==================== Element Creation ====================

    /**
     * Adds a new text box to the slide with default position and size.
     *
     * <p>The text box is created at position (100, 100) with a width of 400 and height of 50.</p>
     *
     * @param text The initial text content for the text box.
     * @return The newly created PotTextBox instance.
     */
    public PotTextBox addTextBox(String text) {
        return addTextBox(text, 100, 100, 400, 50);
    }

    /**
     * Adds a new empty text box to the slide with default position and size.
     *
     * <p>The text box is created at position (100, 100) with a width of 400 and height of 50.</p>
     *
     * @return The newly created PotTextBox instance.
     */
    public PotTextBox addTextBox() {
        return addTextBox(null, 100, 100, 400, 50);
    }

    /**
     * Adds a new text box to the slide with specified position, size, and optional initial text.
     *
     * <p>The text box is anchored at the given coordinates with the specified dimensions. If the text parameter
     * is null, the text box will be created empty.</p>
     *
     * @param text    The initial text content for the text box, or null for an empty text box.
     * @param x       The X-coordinate of the top-left corner in points.
     * @param y       The Y-coordinate of the top-left corner in points.
     * @param width   The width of the text box in points.
     * @param height  The height of the text box in points.
     * @return The newly created PotTextBox instance.
     * @throws IllegalArgumentException if width or height is not positive, or if x or y is not finite.
     */
    public PotTextBox addTextBox(String text, double x, double y, double width, double height) {
        ValidationUtils.finite(x, "x");
        ValidationUtils.finite(y, "y");
        ValidationUtils.positive(width, "width");
        ValidationUtils.positive(height, "height");
        XSLFTextBox poiTextBox = slide.createTextBox();
        poiTextBox.setAnchor(new Rectangle2D.Double(x, y, width, height));
        if (text != null) {
            poiTextBox.setText(text);
        }

        String uuid = presentation.allocateUUID();

        PotTextBox textBox = new PotTextBox(poiTextBox, this, uuid);
        elementCache.add(textBox);
        presentation.registerElement(uuid, textBox);
        return textBox;
    }

    /**
     * Adds a new geometric shape to the slide with default position and size.
     *
     * <p>The shape is created at position (100, 100) with a width of 200 and height of 200.</p>
     *
     * @param shapeType The type of shape to create.
     * @return The newly created PotShape instance.
     */
    public PotShape addShape(PotShapeType shapeType) {
        return addShape(shapeType, 100, 100, 200, 200);
    }

    /**
     * Adds a new geometric shape to the slide with specified position and size.
     *
     * <p>The shape is anchored at the given coordinates with the specified dimensions. The shape type determines
     * the geometric form (e.g., rectangle, ellipse, triangle).</p>
     *
     * @param shapeType The type of shape to create.
     * @param x         The X-coordinate of the top-left corner in points.
     * @param y         The Y-coordinate of the top-left corner in points.
     * @param width     The width of the shape in points.
     * @param height    The height of the shape in points.
     * @return The newly created PotShape instance.
     * @throws IllegalArgumentException if shapeType is null, width or height is not positive, or if x or y is not finite.
     */
    public PotShape addShape(PotShapeType shapeType, double x, double y, double width, double height) {
        ValidationUtils.notNull(shapeType, "shapeType");
        ValidationUtils.finite(x, "x");
        ValidationUtils.finite(y, "y");
        ValidationUtils.positive(width, "width");
        ValidationUtils.positive(height, "height");
        XSLFAutoShape poiShape = slide.createAutoShape();
        poiShape.setShapeType(ShapeTypeMapper.toPoiType(shapeType));
        poiShape.setAnchor(new Rectangle2D.Double(x, y, width, height));

        String uuid = presentation.allocateUUID();

        PotShape shape = new PotShape(poiShape, this, uuid);
        elementCache.add(shape);
        presentation.registerElement(uuid, shape);
        return shape;
    }

    /**
     * Adds a new image to the slide from a file path.
     *
     * <p>The image file is read from the specified path and embedded into the presentation. The image is placed
     * at the default position and size determined by the underlying POI library.</p>
     *
     * @param imagePath The file system path to the image file.
     * @return The newly created PotImage instance.
     * @throws IllegalArgumentException if imagePath is null or blank.
     * @throws PotException if an I/O error occurs while reading the file.
     */
    public PotImage addImage(String imagePath) {
        ValidationUtils.notBlank(imagePath, "imagePath");
        return addImage(new File(imagePath));
    }

    /**
     * Adds a new image to the slide from a File object.
     *
     * <p>The image file is read and embedded into the presentation. The image is placed at the default position
     * and size determined by the underlying POI library.</p>
     *
     * @param file The File object representing the image file.
     * @return The newly created PotImage instance.
     * @throws IllegalArgumentException if file is null.
     * @throws PotException if an I/O error occurs while reading the file.
     */
    public PotImage addImage(File file) {
        ValidationUtils.notNull(file, "file");
        try {
            byte[] imageData = Files.readAllBytes(file.toPath());
            String fileName = file.getName().toLowerCase();
            PictureData.PictureType pictureType = determinePictureType(fileName);
            return addImage(imageData, pictureType);
        } catch (IOException e) {
            throw PotException.ioError("reading image file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Adds a new image to the slide from raw byte data with a specified content type.
     *
     * <p>The image data is embedded into the presentation. The image is placed at the default position and size
     * determined by the underlying POI library. The content type should be a standard MIME type like "image/jpeg"
     * or "image/png".</p>
     *
     * @param imageData    The raw byte data of the image.
     * @param contentType  The MIME content type of the image (e.g., "image/jpeg", "image/png").
     * @return The newly created PotImage instance.
     * @throws IllegalArgumentException if imageData is null or empty, or if contentType is null or blank.
     */
    public PotImage addImage(byte[] imageData, String contentType) {
        ValidationUtils.notEmpty(imageData, "imageData");
        ValidationUtils.notBlank(contentType, "contentType");
        PictureData.PictureType pictureType = determinePictureType(contentType);
        return addImage(imageData, pictureType);
    }

    /**
     * Adds a new image to the slide from raw byte data with a specified picture type.
     *
     * <p>The image data is embedded into the presentation. The image is placed at the default position and size
     * determined by the underlying POI library. The picture type determines the image format (e.g., PNG, JPEG).</p>
     *
     * @param imageData   The raw byte data of the image.
     * @param pictureType The picture format type of the image.
     * @return The newly created PotImage instance.
     * @throws IllegalArgumentException if imageData is null or empty, or if pictureType is null.
     */
    private PotImage addImage(byte[] imageData, PictureData.PictureType pictureType) {
        XSLFPictureData picData = presentation.addPictureData(imageData, pictureType);
        XSLFPictureShape poiPicture = slide.createPicture(picData);

        String uuid = presentation.allocateUUID();

        PotImage image = new PotImage(poiPicture, this, uuid);
        elementCache.add(image);
        presentation.registerElement(uuid, image);
        return image;
    }

    /**
     * Adds a new table to the slide with default position and size.
     *
     * <p>The table is created at position (100, 100) with a width of 400 and height of 200. The table will have
     * the specified number of rows and columns, all initially empty.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTable table = slide.addTable(3, 4);
     * table.getCell(0, 0).setText("Header 1");
     * table.getCell(0, 1).setText("Header 2");
     * }</pre>
     *
     * @param rows The number of rows in the table (must be positive).
     * @param cols The number of columns in the table (must be positive).
     * @return The newly created PotTable instance.
     * @throws IllegalArgumentException if rows or cols is not positive.
     */
    public PotTable addTable(int rows, int cols) {
        return addTable(rows, cols, 100, 100, 400, 200);
    }

    /**
     * Adds a new table to the slide with specified position, size, and dimensions.
     *
     * <p>The table is anchored at the given coordinates with the specified dimensions. The table will have
     * the specified number of rows and columns, all initially empty.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTable table = slide.addTable(5, 3, 50, 200, 500, 150);
     * table.setColumnWidth(0, 150);
     * }</pre>
     *
     * @param rows   The number of rows in the table (must be positive).
     * @param cols   The number of columns in the table (must be positive).
     * @param x      The X-coordinate of the top-left corner in points.
     * @param y      The Y-coordinate of the top-left corner in points.
     * @param width  The width of the table in points.
     * @param height The height of the table in points.
     * @return The newly created PotTable instance.
     * @throws IllegalArgumentException if rows or cols is not positive, width or height is not positive,
     *                                  or if x or y is not finite.
     */
    public PotTable addTable(int rows, int cols, double x, double y, double width, double height) {
        ValidationUtils.positive(rows, "rows");
        ValidationUtils.positive(cols, "cols");
        ValidationUtils.finite(x, "x");
        ValidationUtils.finite(y, "y");
        ValidationUtils.positive(width, "width");
        ValidationUtils.positive(height, "height");
        XSLFTable poiTable = slide.createTable(rows, cols);
        poiTable.setAnchor(new Rectangle2D.Double(x, y, width, height));

        String uuid = presentation.allocateUUID();

        PotTable table = new PotTable(poiTable, this, uuid);
        elementCache.add(table);
        presentation.registerElement(uuid, table);
        return table;
    }

    /**
     * Adds a new connector shape to the slide with a specified connector type.
     *
     * <p>The connector is created without defined start or end points. The connector type determines the visual
     * style of the line (e.g., straight, elbow, curved). The connector must be positioned and connected using
     * methods like {@link PotConnector#from(double, double)} and {@link PotConnector#to(double, double)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotConnector connector = slide.addConnector(PotConnector.ConnectorType.ELBOW);
     * connector.from(100, 100).to(300, 300);
     * connector.setLineWidth(2.0);
     * }</pre>
     *
     * @param connectorType The type of connector to create.
     * @return The newly created PotConnector instance.
     * @throws IllegalArgumentException if connectorType is null.
     */
    public PotConnector addConnector(PotConnector.ConnectorType connectorType) {
        ValidationUtils.notNull(connectorType, "connectorType");
        XSLFConnectorShape poiConnector = slide.createConnector();

        String uuid = presentation.allocateUUID();

        PotConnector connector = new PotConnector(poiConnector, this, uuid, connectorType);
        elementCache.add(connector);
        presentation.registerElement(uuid, connector);
        return connector;
    }

    /**
     * Adds a new straight connector between two elements.
     *
     * <p>The connector is created as a straight line connecting the centers of the two specified elements.
     * If either element is null, the connector will start or end at the center of the non-null element.
     * If both elements are null, the connector is created without defined endpoints.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShape box1 = slide.addShape(PotShapeType.RECTANGLE, 50, 50, 100, 100);
     * PotShape box2 = slide.addShape(PotShapeType.ELLIPSE, 250, 150, 100, 100);
     * PotConnector connector = slide.addConnector(box1, box2);
     * connector.setLineColor(PotColor.RED);
     * }</pre>
     *
     * @param from The starting element for the connector, or null for no starting element.
     * @param to   The ending element for the connector, or null for no ending element.
     * @return The newly created PotConnector instance.
     */
    public PotConnector addConnector(PotElement from, PotElement to) {
        PotConnector connector = addConnector(PotConnector.ConnectorType.STRAIGHT);

        if (from != null && to != null) {
            // Connect from center of 'from' to center of 'to'
            double fromCX = from.getX() + from.getWidth() / 2.0;
            double fromCY = from.getY() + from.getHeight() / 2.0;
            double toCX = to.getX() + to.getWidth() / 2.0;
            double toCY = to.getY() + to.getHeight() / 2.0;
            connector.line(fromCX, fromCY, toCX, toCY);
            connector.connectFrom(from);
            connector.connectTo(to);
        } else if (from != null) {
            double cx = from.getX() + from.getWidth() / 2.0;
            double cy = from.getY() + from.getHeight() / 2.0;
            connector.from(cx, cy);
            connector.connectFrom(from);
        } else if (to != null) {
            double cx = to.getX() + to.getWidth() / 2.0;
            double cy = to.getY() + to.getHeight() / 2.0;
            connector.to(cx, cy);
            connector.connectTo(to);
        }

        return connector;
    }

    /**
     * Adds a new audio element to the slide from raw byte data.
     *
     * <p>The audio data is embedded into the presentation. A placeholder icon is displayed on the slide at a
     * default position and size (50x50 points). The content type should be a standard MIME type like "audio/mp3"
     * or "audio/wav".</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * byte[] audioData = Files.readAllBytes(Paths.get("sound.mp3"));
     * PotAudio audio = slide.addAudio(audioData, "audio/mp3");
     * audio.setPosition(200, 100);
     * }</pre>
     *
     * @param data        The raw byte data of the audio file.
     * @param contentType The MIME content type of the audio (e.g., "audio/mp3", "audio/wav").
     * @return The newly created PotAudio instance.
     * @throws IllegalArgumentException if data is null or empty.
     * @throws PotException if an error occurs while embedding the media.
     */
    public PotAudio addAudio(byte[] data, String contentType) {
        ValidationUtils.notEmpty(data, "data");

        XSLFPictureShape audioShape = createMediaPlaceholder(50, 50, 50, 50);
        attachMediaToShape(audioShape, data, normalizeMediaContentType(contentType, false), false);

        String uuid = presentation.allocateUUID();

        PotAudio audio = new PotAudio(audioShape, this, uuid);
        elementCache.add(audio);
        presentation.registerElement(uuid, audio);
        return audio;
    }

    /**
     * Adds a new video element to the slide from raw byte data.
     *
     * <p>The video data is embedded into the presentation. A placeholder icon is displayed on the slide at a
     * default position and size (400x300 points). The content type should be a standard MIME type like "video/mp4"
     * or "video/avi".</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * byte[] videoData = Files.readAllBytes(Paths.get("clip.mp4"));
     * PotVideo video = slide.addVideo(videoData, "video/mp4");
     * video.setPosition(100, 150);
     * }</pre>
     *
     * @param data        The raw byte data of the video file.
     * @param contentType The MIME content type of the video (e.g., "video/mp4", "video/avi").
     * @return The newly created PotVideo instance.
     * @throws IllegalArgumentException if data is null or empty.
     * @throws PotException if an error occurs while embedding the media.
     */
    public PotVideo addVideo(byte[] data, String contentType) {
        ValidationUtils.notEmpty(data, "data");

        XSLFPictureShape videoShape = createMediaPlaceholder(100, 100, 400, 300);
        attachMediaToShape(videoShape, data, normalizeMediaContentType(contentType, true), true);

        String uuid = presentation.allocateUUID();

        PotVideo video = new PotVideo(videoShape, this, uuid);
        elementCache.add(video);
        presentation.registerElement(uuid, video);
        return video;
    }

    /**
     * Adds a new chart to the slide with default position and size.
     *
     * <p>The chart is created at position (100, 100) with a width of 500 and height of 350. The chart type
     * determines the kind of chart (e.g., "BAR_CHART", "LINE_CHART", "PIE_CHART"). The chart data must be
     * populated separately using the returned {@link PotChart} instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotChart chart = slide.addChart("BAR_CHART");
     * chart.setTitle("Sales Data");
     * chart.setCategoryAxisTitle("Quarter");
     * chart.setValueAxisTitle("Revenue");
     * }</pre>
     *
     * @param chartType The type of chart to create (e.g., "BAR_CHART", "LINE_CHART", "PIE_CHART").
     * @return The newly created PotChart instance.
     * @throws IllegalArgumentException if chartType is null or blank.
     * @throws PotException if the chart cannot be created due to an underlying POI error.
     */
    public PotChart addChart(String chartType) {
        return addChart(chartType, 100, 100, 500, 350);
    }

    /**
     * Adds a new chart to the slide with specified position and size.
     *
     * <p>The chart is anchored at the given coordinates with the specified dimensions. The chart type
     * determines the kind of chart (e.g., "BAR_CHART", "LINE_CHART", "PIE_CHART"). The chart data must be
     * populated separately using the returned {@link PotChart} instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotChart chart = slide.addChart("LINE_CHART", 200, 50, 600, 400);
     * chart.setTitle("Growth Trend");
     * chart.setData(categories, values);
     * }</pre>
     *
     * @param chartType The type of chart to create (e.g., "BAR_CHART", "LINE_CHART", "PIE_CHART").
     * @param x         The X-coordinate of the top-left corner in points.
     * @param y         The Y-coordinate of the top-left corner in points.
     * @param width     The width of the chart in points.
     * @param height    The height of the chart in points.
     * @return The newly created PotChart instance.
     * @throws IllegalArgumentException if chartType is null or blank, width or height is not positive,
     *                                  or if x or y is not finite.
     * @throws PotException if the chart cannot be created due to an underlying POI error.
     */
    public PotChart addChart(String chartType, double x, double y, double width, double height) {
        ValidationUtils.notBlank(chartType, "chartType");
        ValidationUtils.finite(x, "x");
        ValidationUtils.finite(y, "y");
        ValidationUtils.positive(width, "width");
        ValidationUtils.positive(height, "height");

        try {
            XSLFChart chart = presentation.getRawSlideShow().createChart();
            initializeChartContent(chart, chartType);
            slide.addChart(chart, new Rectangle2D.Double(x, y, width, height));

            XSLFGraphicFrame frame = findChartFrame();
            if (frame == null) {
                throw PotException.unsupportedOperation("Chart frame was not created by POI");
            }
            frame.setAnchor(new Rectangle2D.Double(x, y, width, height));

            String uuid = presentation.allocateUUID();
            PotChart potChart = new PotChart(frame, chart, this, uuid);
            elementCache.add(potChart);
            presentation.registerElement(uuid, potChart);
            return potChart;
        } catch (Exception e) {
            throw PotException.wrap("Failed to create chart", e);
        }
    }

    private void initializeChartContent(XSLFChart chart, String chartType) {
        ChartTypes effectiveType = resolveChartType(chartType);

        String[] categories = new String[] {"A", "B", "C"};
        Double[] values = new Double[] {1.0, 2.0, 3.0};
        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categories);
        XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(values);

        if (effectiveType == ChartTypes.PIE) {
            XDDFChartData pieData = chart.createData(ChartTypes.PIE, null, null);
            XDDFChartData.Series series = pieData.addSeries(categoryData, valueData);
            series.setTitle("Series 1", null);
            chart.plot(pieData);
            return;
        }

        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFChartData chartData = chart.createData(effectiveType, categoryAxis, valueAxis);
        XDDFChartData.Series series = chartData.addSeries(categoryData, valueData);
        series.setTitle("Series 1", null);

        if (chartData instanceof XDDFBarChartData) {
            ((XDDFBarChartData) chartData).setBarDirection(BarDirection.COL);
        }

        chart.plot(chartData);
    }

    private ChartTypes resolveChartType(String chartType) {
        String normalized = chartType.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("LINE")) {
            return ChartTypes.LINE;
        }
        if (normalized.contains("PIE")) {
            return ChartTypes.PIE;
        }
        return ChartTypes.BAR;
    }

    // ==================== Element Retrieval ====================

    /**
     * Returns an unmodifiable list of all elements on this slide.
     *
     * <p>The list includes all shapes, text boxes, images, tables, groups, connectors, charts, and media elements
     * that have been added to the slide. The order of elements in the list corresponds to their Z-order from
     * bottom to top.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * List<PotElement> allElements = slide.getElements();
     * for (PotElement element : allElements) {
     *     System.out.println(element.getUUID() + ": " + element.getClass().getSimpleName());
     * }
     * }</pre>
     *
     * @return An unmodifiable list of all elements on this slide.
     */
    public List<PotElement> getElements() {
        ensurePresentationOpen();
        return Collections.unmodifiableList(elementCache);
    }

    /**
     * Returns an unmodifiable list of elements filtered by a specific type.
     *
     * <p>This method returns all elements on the slide that are instances of the specified class. The returned
     * list maintains the Z-order of elements from bottom to top.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * List<PotShape> allShapes = slide.getElements(PotShape.class);
     * List<PotTextBox> allTexts = slide.getElements(PotTextBox.class);
     * }</pre>
     *
     * @param <T>  The type of elements to return.
     * @param type The class object representing the element type to filter by.
     * @return An unmodifiable list of elements of the specified type.
     * @throws IllegalArgumentException if type is null.
     */
    @SuppressWarnings("unchecked")
    public <T extends PotElement> List<T> getElements(Class<T> type) {
        ensurePresentationOpen();
        ValidationUtils.notNull(type, "type");
        return Collections.unmodifiableList(elementCache.stream()
            .filter(type::isInstance)
            .map(e -> (T) e)
            .collect(Collectors.toList()));
    }

    /**
     * Returns an unmodifiable list of elements filtered by a specific type.
     *
     * <p>This is an alias for {@link #getElements(Class)}. It returns all elements on the slide that are
     * instances of the specified class, maintaining Z-order from bottom to top.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * List<PotImage> images = slide.findElementsByType(PotImage.class);
     * }</pre>
     *
     * @param <T>  The type of elements to return.
     * @param type The class object representing the element type to filter by.
     * @return An unmodifiable list of elements of the specified type.
     * @throws IllegalArgumentException if type is null.
     * @see #getElements(Class)
     */
    public <T extends PotElement> List<T> findElementsByType(Class<T> type) {
        return getElements(type);
    }

    /**
     * Returns an unmodifiable list of elements that have a specific name.
     *
     * <p>This method searches for elements whose name matches the specified string exactly. Element names
     * can be obtained using {@link PotElement#getName()}. The comparison is case-sensitive.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * List<PotElement> logoElements = slide.findElementsByName("Company Logo");
     * }</pre>
     *
     * @param name The name to search for.
     * @return An unmodifiable list of elements with the specified name.
     * @throws IllegalArgumentException if name is null or blank.
     */
    public List<PotElement> findElementsByName(String name) {
        ensurePresentationOpen();
        ValidationUtils.notBlank(name, "name");
        return Collections.unmodifiableList(elementCache.stream()
            .filter(e -> name.equals(e.getName()))
            .collect(Collectors.toList()));
    }

    /**
     * Returns an unmodifiable list of elements that satisfy a given predicate.
     *
     * <p>This method provides a flexible way to filter elements using custom criteria. The predicate is
     * applied to each element, and only elements for which the predicate returns true are included in
     * the result.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Find all red elements
     * List<PotElement> redElements = slide.findElements(e ->
     *     e instanceof PotShape && PotColor.RED.equals(((PotShape) e).getFillColor()));
     *
     * // Find elements wider than 200 points
     * List<PotElement> wideElements = slide.findElements(e -> e.getWidth() > 200);
     * }</pre>
     *
     * @param predicate The predicate to test each element.
     * @return An unmodifiable list of elements that satisfy the predicate.
     * @throws IllegalArgumentException if predicate is null.
     */
    public List<PotElement> findElements(java.util.function.Predicate<PotElement> predicate) {
        ensurePresentationOpen();
        ValidationUtils.notNull(predicate, "predicate");
        return Collections.unmodifiableList(elementCache.stream()
            .filter(predicate)
            .collect(Collectors.toList()));
    }

    /**
     * Returns the element with the specified UUID, or null if not found.
     *
     * <p>Each element on the slide has a unique UUID assigned when it is created. This method provides
     * a way to retrieve a specific element by its identifier.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotElement element = slide.getElement("550e8400-e29b-41d4-a716-446655440000");
     * if (element != null) {
     *     element.setPosition(100, 100);
     * }
     * }</pre>
     *
     * @param uuid The UUID of the element to retrieve.
     * @return The element with the specified UUID, or null if no such element exists.
     * @throws IllegalArgumentException if uuid is null or blank.
     */
    public PotElement getElement(String uuid) {
        ensurePresentationOpen();
        ValidationUtils.notBlank(uuid, "uuid");
        return elementCache.stream()
            .filter(e -> uuid.equals(e.getUUID()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Returns the total number of elements on this slide.
     *
     * <p>This count includes all shapes, text boxes, images, tables, groups, connectors, charts, and media
     * elements that have been added to the slide.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * int count = slide.getElementCount();
     * System.out.println("Slide has " + count + " elements");
     * }</pre>
     *
     * @return The number of elements on this slide.
     */
    public int getElementCount() {
        ensurePresentationOpen();
        return elementCache.size();
    }

    // ==================== Grouping ====================

    /**
     * Groups multiple elements together into a single group element.
     *
     * <p>This method creates a new group that contains all the specified elements. The group's bounding
     * rectangle is calculated to encompass all the elements. The original elements are removed from the
     * slide's element list and are now accessible only through the group. Grouping allows elements to be
     * moved, resized, and formatted together as a single unit.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShape rect = slide.addShape(PotShapeType.RECTANGLE, 50, 50, 100, 100);
     * PotTextBox text = slide.addTextBox("Label", 60, 60, 80, 30);
     * List<PotElement> elements = Arrays.asList(rect, text);
     * PotGroup group = slide.group(elements);
     * group.setPosition(200, 200); // Moves both rectangle and text together
     * }</pre>
     *
     * @param elements The list of elements to group together.
     * @return The newly created PotGroup instance containing the grouped elements.
     * @throws IllegalArgumentException if elements is null, empty, or contains null items.
     */
    public PotGroup group(List<PotElement> elements) {
        ensurePresentationOpen();
        ValidationUtils.notEmpty(elements, "elements");
        if (elements.stream().anyMatch(Objects::isNull)) {
            throw PotException.invalidParameter("elements", "cannot contain null items");
        }

        XSLFGroupShape groupShape = slide.createGroup();

        String uuid = presentation.allocateUUID();

        // Calculate group bounds and set anchor
        Rectangle2D bounds = calculateBounds(elements);
        groupShape.setAnchor(bounds);
        groupShape.setInteriorAnchor(bounds);

        // Move elements into the group by manipulating XML
        moveElementsIntoGroup(groupShape, elements, bounds);

        PotGroup group = new PotGroup(groupShape, this, uuid);
        elementCache.add(group);
        presentation.registerElement(uuid, group);

        // Remove original elements from cache
        elementCache.removeAll(elements);

        return group;
    }

    /**
     * Moves elements into a group shape by manipulating the underlying XML structure.
     *
     * <p>This method adjusts the coordinates of each element relative to the group's origin and
     * moves their XML nodes from the slide's shape tree into the group's shape tree.</p>
     *
     * @param groupShape The POI group shape to receive the elements.
     * @param elements   The elements to move into the group.
     * @param groupBounds The bounding rectangle of the group.
     */
    private void moveElementsIntoGroup(XSLFGroupShape groupShape, List<PotElement> elements,
                                       Rectangle2D groupBounds) {
        // Track successfully moved elements for potential rollback
        List<PotElement> movedElements = new ArrayList<>();

        try {
            org.w3c.dom.Node groupNode = groupShape.getXmlObject().getDomNode();
            if (groupNode == null) {
                throw new PotException("Group shape XML node is null - cannot create group");
            }

            // Convert group offset from points to EMU (English Metric Units)
            long groupOffX = (long) (groupBounds.getX() * 12700); // pt to EMU
            long groupOffY = (long) (groupBounds.getY() * 12700);

            for (PotElement element : elements) {
                org.w3c.dom.Node elementNode = element.getShape().getXmlObject().getDomNode();
                if (elementNode == null) {
                    PotLogger.warn(PotSlide.class, "moveElementsIntoGroup",
                        "Element XML node is null, skipping: " + element.getUUID());
                    continue;
                }

                // Adjust element coordinates relative to group origin
                adjustElementOffset(elementNode, -groupOffX, -groupOffY);

                // Import element node into group
                org.w3c.dom.Node importedNode = groupNode.getOwnerDocument().importNode(elementNode, true);
                groupNode.appendChild(importedNode);

                // Remove element node from slide's shape tree
                org.w3c.dom.Node parentNode = elementNode.getParentNode();
                if (parentNode != null) {
                    parentNode.removeChild(elementNode);
                }

                // Track for rollback if needed
                movedElements.add(element);
            }
        } catch (Exception e) {
            // Log the error
            PotLogger.error(PotSlide.class, "moveElementsIntoGroup",
                "Failed to move elements into group. Attempting rollback...", e);

            // Attempt rollback: re-add removed elements to slide
            if (!movedElements.isEmpty()) {
                try {
                    for (PotElement element : movedElements) {
                        // Re-add element to slide's shape list
                        getRawSlide().addShape(element.getShape());
                    }
                    PotLogger.info(PotSlide.class, "moveElementsIntoGroup",
                        "Rollback successful: " + movedElements.size() + " elements restored");
                } catch (Exception rollbackEx) {
                    PotLogger.error(PotSlide.class, "moveElementsIntoGroup",
                        "Rollback failed - elements may be in inconsistent state", rollbackEx);
                }
            }

            // Re-throw as PotException to inform caller
            throw PotException.wrap("Failed to group elements: " + e.getMessage(), e);
        }
    }

    /**
     * Adjusts the offset coordinates of an element in the XML representation.
     *
     * <p>This method finds the "off" element in the shape's XML and adjusts its x and y attributes
     * by the specified delta values. The coordinates are expected to be in EMU units.</p>
     *
     * @param elementNode The DOM node of the element to adjust.
     * @param dx          The delta to add to the x-coordinate (in EMU).
     * @param dy          The delta to add to the y-coordinate (in EMU).
     */
    private void adjustElementOffset(org.w3c.dom.Node elementNode, long dx, long dy) {
        if (!(elementNode instanceof org.w3c.dom.Element)) {
            return;
        }
        org.w3c.dom.NodeList descendants = ((org.w3c.dom.Element) elementNode)
            .getElementsByTagNameNS("*", "off");
        if (descendants == null) {
            return;
        }
        for (int i = 0; i < descendants.getLength(); i++) {
            org.w3c.dom.Node node = descendants.item(i);
            if (node instanceof org.w3c.dom.Element) {
                org.w3c.dom.Element off = (org.w3c.dom.Element) node;
                String xStr = off.getAttribute("x");
                String yStr = off.getAttribute("y");
                try {
                    long x = Long.parseLong(xStr) + dx;
                    long y = Long.parseLong(yStr) + dy;
                    off.setAttribute("x", String.valueOf(x));
                    off.setAttribute("y", String.valueOf(y));
                } catch (NumberFormatException ignored) {
                }
                break; // Only adjust the first "off" element (xfrm/off)
            }
        }
    }

    // ==================== Background ====================

    /**
     * Sets a solid color background for the slide.
     *
     * <p>This method replaces any existing background with a solid color fill. The color can include
     * transparency through its alpha component. If the color is null, the method returns without
     * making any changes.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Set solid blue background
     * slide.setBackground(PotColor.BLUE);
     *
     * // Set semi-transparent red background
     * slide.setBackground(new PotColor(255, 0, 0, 128));
     * }</pre>
     *
     * @param color The color to use for the background, or null to leave unchanged.
     * @return This PotSlide instance for method chaining.
     */
    public PotSlide setBackground(PotColor color) {
        if (color == null) return this;
        XmlUtils.applySlideBackground(slide, () -> {
            String aNs = "http://schemas.openxmlformats.org/drawingml/2006/main";
            StringBuilder sb = new StringBuilder();
            sb.append("<a:solidFill xmlns:a=\"").append(aNs).append("\">");
            sb.append("<a:srgbClr val=\"").append(color.toHex()).append("\"");
            if (color.getAlpha() < 255) {
                sb.append("><a:alpha val=\"")
                  .append(UnitConverter.fractionToEmuPercent(color.getAlpha() / 255.0))
                  .append("\"/></a:srgbClr>");
            } else {
                sb.append("/>");
            }
            sb.append("</a:solidFill>");
            return sb.toString();
        });
        return this;
    }

    /**
     * Sets an image as the background of this slide.
     *
     * <p>This method loads an image from the specified file path and sets it as the slide background.
     * The image is stretched to fill the entire slide area. Supported image formats include PNG, JPEG,
     * GIF, BMP, WMF, EMF, TIFF, and SVG.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * slide.setBackground("/path/to/background.png");
     * }</pre>
     *
     * @param imagePath The file system path to the image file.
     * @return This slide instance for method chaining.
     * @throws PotException if the file cannot be read or the image format is not supported.
     */
    public PotSlide setBackground(String imagePath) {
        try {
            byte[] imageData = Files.readAllBytes(new File(imagePath).toPath());
            return setBackground(imageData, determinePictureType(imagePath));
        } catch (IOException e) {
            throw PotException.ioError("reading background image: " + imagePath, e);
        }
    }

    /**
     * Sets an image as the background of this slide using raw image data.
     *
     * <p>This internal method embeds the provided image data into the presentation and sets it as
     * the slide background with a stretch fill. The image type is determined by the provided
     * PictureData.PictureType.</p>
     *
     * @param imageData   The raw bytes of the image.
     * @param pictureType The type of the image (e.g., PNG, JPEG).
     * @return This slide instance for method chaining.
     */
    private PotSlide setBackground(byte[] imageData, PictureData.PictureType pictureType) {
        try {
            XSLFPictureData picData = presentation.addPictureData(imageData, pictureType);
            PackagePart slidePart = slide.getPackagePart();
            String rId = slidePart.addRelationship(
                picData.getPackagePart().getPartName(),
                TargetMode.INTERNAL,
                "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"
            ).getId();

            XmlUtils.applySlideBackground(slide, () -> {
                String aNs = "http://schemas.openxmlformats.org/drawingml/2006/main";
                String rNs = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
                return "<a:blipFill xmlns:a=\"" + aNs + "\" xmlns:r=\"" + rNs + "\">" +
                       "<a:blip r:embed=\"" + rId + "\"/>" +
                       "<a:stretch><a:fillRect/></a:stretch>" +
                       "</a:blipFill>";
            });
        } catch (Exception e) {
            PotLogger.warn(PotSlide.class, "setBackground",
                "Failed to set image background", e);
        }
        return this;
    }

    /**
     * Sets a gradient fill as the background of this slide.
     *
     * <p>This method applies a gradient background using the specified gradient configuration.
     * The gradient can be linear, radial, or rectangular, with multiple color stops defining
     * the transition. If the gradient is null, the method returns without making any changes.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotGradient gradient = new PotGradient(PotGradient.Type.LINEAR, 45.0)
     *     .addStop(0.0, PotColor.RED)
     *     .addStop(1.0, PotColor.YELLOW);
     * slide.setBackground(gradient);
     * }</pre>
     *
     * @param gradient The gradient configuration to apply.
     * @return This slide instance for method chaining.
     */
    public PotSlide setBackground(PotGradient gradient) {
        if (gradient == null) return this;
        XmlUtils.applySlideBackground(slide, () -> {
            String aNs = "http://schemas.openxmlformats.org/drawingml/2006/main";
            StringBuilder sb = new StringBuilder();
            sb.append("<a:gradFill xmlns:a=\"").append(aNs).append("\" rotWithShape=\"")
              .append(gradient.isRotateWithShape() ? "1" : "0").append("\">");
            sb.append("<a:gsLst>");
            List<PotGradient.GradientStop> stops = new ArrayList<>(gradient.getStops());
            stops.sort(java.util.Comparator.comparingDouble(PotGradient.GradientStop::getPosition));
            if (stops.isEmpty()) {
                stops.add(new PotGradient.GradientStop(0.0, PotColor.WHITE, 1.0));
                stops.add(new PotGradient.GradientStop(1.0, PotColor.BLACK, 1.0));
            }
            for (PotGradient.GradientStop stop : stops) {
                sb.append("<a:gs pos=\"").append(stop.getPositionPercent()).append("\">");
                sb.append("<a:srgbClr val=\"").append(stop.getColor().toHex()).append("\"");
                if (stop.getOpacity() < 1.0) {
                    sb.append("><a:alpha val=\"")
                      .append(UnitConverter.fractionToEmuPercent(stop.getOpacity()))
                      .append("\"/></a:srgbClr>");
                } else {
                    sb.append("/>");
                }
                sb.append("</a:gs>");
            }
            sb.append("</a:gsLst>");
            switch (gradient.getType()) {
                case LINEAR:
                    sb.append("<a:lin ang=\"")
                      .append(UnitConverter.degreesToEmuAngle(gradient.getAngle()))
                      .append("\" scaled=\"0\"/>");
                    break;
                case RADIAL:
                    sb.append("<a:path path=\"circle\"/>");
                    break;
                case RECTANGULAR:
                    sb.append("<a:path path=\"rect\"/>");
                    break;
                default:
                    sb.append("<a:lin ang=\"0\" scaled=\"0\"/>");
                    break;
            }
            sb.append("</a:gradFill>");
            return sb.toString();
        });
        return this;
    }


    // ==================== Transition Management ====================

    /**
     * Applies a slide transition effect.
     *
     * <p>This method sets the transition effect that will be used when advancing to this slide
     * during a presentation. The transition includes the effect type, duration, and optional
     * sound. If the transition is null, no transition will be applied.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition transition = new PotTransition(PotTransition.Type.FADE, 1.5);
     * slide.setTransition(transition);
     * }</pre>
     *
     * @param transition The transition configuration to apply.
     * @return This slide instance for method chaining.
     */
    public PotSlide setTransition(PotTransition transition) {
        TransitionHelper.apply(slide, transition);
        return this;
    }

    /**
     * Removes any transition effect from this slide.
     *
     * <p>This method clears any previously applied transition, resulting in an immediate cut
     * when advancing to this slide during presentation.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * slide.removeTransition();
     * }</pre>
     *
     * @return This slide instance for method chaining.
     */
    public PotSlide removeTransition() {
        TransitionHelper.remove(slide);
        return this;
    }

    /**
     * Returns the current transition effect applied to this slide.
     *
     * <p>This method retrieves the transition configuration that was previously set using
     * {@link #setTransition(PotTransition)}. If no transition has been applied, it returns null.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotTransition current = slide.getTransition();
     * if (current != null) {
     *     System.out.println("Transition type: " + current.getType());
     * }
     * }</pre>
     *
     * @return The current transition configuration, or null if no transition is set.
     */
    public PotTransition getTransition() {
        return TransitionHelper.get(slide);
    }

    // ==================== Notes Management ====================

    /**
     * Returns the text content of the slide notes.
     *
     * <p>This method extracts the text from the notes section associated with this slide.
     * The notes are typically used by the presenter for reference during a presentation.
     * If no notes exist or the notes are empty, this method returns null.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * String notes = slide.getNotes();
     * if (notes != null) {
     *     System.out.println("Presenter notes: " + notes);
     * }
     * }</pre>
     *
     * @return The notes text, or null if no notes exist.
     */
    public String getNotes() {
        XSLFNotes notes = slide.getNotes();
        if (notes == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (XSLFShape shape : notes.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape textShape = (XSLFTextShape) shape;
                if (textShape.getTextType() == org.apache.poi.sl.usermodel.Placeholder.BODY) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append(textShape.getText());
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    /**
     * Sets or updates the text content of the slide notes.
     *
     * <p>This method replaces the existing notes text with the provided content. If the slide
     * does not have a notes section, one will be created automatically. If the provided notes
     * parameter is null, the notes will be cleared (set to an empty string).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * slide.setNotes("Remember to discuss the quarterly results in detail.");
     * }</pre>
     *
     * @param notes The new notes text to set.
     * @return This slide instance for method chaining.
     */
    public PotSlide setNotes(String notes) {
        XSLFNotes slideNotes = slide.getNotes();
        if (slideNotes == null) {
            slideNotes = presentation.getRawSlideShow().getNotesSlide(slide);
        }

        if (slideNotes != null) {
            for (XSLFShape shape : slideNotes.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    if (textShape.getTextType() == org.apache.poi.sl.usermodel.Placeholder.BODY) {
                        textShape.setText(notes != null ? notes : "");
                        break;
                    }
                }
            }
        }
        return this;
    }

    // ==================== Visibility Management ====================

    /**
     * Checks whether this slide is marked as hidden.
     *
     * <p>A hidden slide is not displayed during a normal slide show presentation but remains
     * in the presentation file. Hidden slides can be shown manually if needed.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * if (slide.isHidden()) {
     *     System.out.println("This slide is hidden and won't show during presentation.");
     * }
     * }</pre>
     *
     * @return true if the slide is hidden, false otherwise.
     */
    public boolean isHidden() {
        CTSlide ctSlide = slide.getXmlObject();
        return ctSlide.isSetShow() && !ctSlide.getShow();
    }

    /**
     * Sets the hidden state of this slide.
     *
     * <p>This method marks the slide as either hidden or visible for presentation purposes.
     * When a slide is hidden, it will be skipped during a normal slide show but can still
     * be accessed programmatically.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * slide.setHidden(true);  // Hide the slide
     * slide.setHidden(false); // Make the slide visible
     * }</pre>
     *
     * @param hidden true to hide the slide, false to make it visible.
     * @return This slide instance for method chaining.
     */
    public PotSlide setHidden(boolean hidden) {
        CTSlide ctSlide = slide.getXmlObject();
        ctSlide.setShow(!hidden);
        return this;
    }

    // ==================== Layout Management ====================

    /**
     * Applies a layout template to this slide.
     *
     * <p>This method applies the specified layout, which defines the arrangement of placeholders
     * and default formatting. The layout must belong to the same presentation as this slide.
     * If the layout is null, no changes are made.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotLayout titleLayout = presentation.getLayout("Title Slide");
     * slide.applyLayout(titleLayout);
     * }</pre>
     *
     * @param layout The layout to apply to this slide.
     * @return This slide instance for method chaining.
     */
    public PotSlide applyLayout(PotLayout layout) {
        if (layout != null) {
            layout.applyTo(this);
        }
        return this;
    }

    /**
     * Returns the layout currently applied to this slide.
     *
     * <p>This method retrieves the layout template that defines the slide's structure and
     * placeholder arrangement. If the slide has no explicit layout, this method returns null.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotLayout layout = slide.getLayout();
     * if (layout != null) {
     *     System.out.println("Layout name: " + layout.getName());
     * }
     * }</pre>
     *
     * @return The current layout of this slide, or null if no layout is set.
     */
    public PotLayout getLayout() {
        ensurePresentationOpen();
        XSLFSlideLayout layout = slide.getSlideLayout();
        if (layout == null) {
            return null;
        }
        XSLFSlideMaster master = layout.getSlideMaster();
        return new PotLayout(layout, new PotMaster(master, presentation));
    }

    // ==================== Internal Access ====================

    /**
     * Returns the underlying Apache POI XSLFSlide object.
     *
     * <p>This method provides direct access to the POI slide object for advanced operations
     * that are not covered by the PotSlide API. Use with caution as direct modifications
     * may not be tracked by the PotSlide instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFSlide poiSlide = slide.getRawSlide();
     * // Perform POI-specific operations
     * }</pre>
     *
     * @return The underlying XSLFSlide object.
     */
    public XSLFSlide getRawSlide() {
        ensurePresentationOpen();
        return slide;
    }

    // ==================== Package-Private Methods ====================

    /**
     * Returns the underlying XSLFSlide for internal use.
     *
     * <p>This package-private method provides access to the POI slide object while ensuring
     * the presentation is still open.</p>
     *
     * @return The underlying XSLFSlide object.
     */
    XSLFSlide getSlide() {
        ensurePresentationOpen();
        return slide;
    }

    private void ensurePresentationOpen() {
        presentation.ensureNotClosed();
    }

    /**
     * Removes an element from the internal cache.
     *
     * <p>This method is called internally when an element is deleted or moved to a group.
     * It removes the specified element from the slide's element cache.</p>
     *
     * @param element The element to remove from the cache.
     */
    void removeFromCache(PotElement element) {
        elementCache.remove(element);
    }

    // ==================== Private Helper Methods ====================

    private void initializeElementCache() {
        for (XSLFShape shape : slide.getShapes()) {
            PotElement element = createElementFromShape(shape);
            if (element != null) {
                elementCache.add(element);
            }
        }
    }

    private PotElement createElementFromShape(XSLFShape shape) {
        String uuid = presentation.allocateUUID();

        PotElement element = null;

        if (shape instanceof XSLFTextBox) {
            element = new PotTextBox((XSLFTextBox) shape, this, uuid);
        } else if (shape instanceof XSLFAutoShape) {
            element = new PotShape((XSLFAutoShape) shape, this, uuid);
        } else if (shape instanceof XSLFPictureShape) {
            element = new PotImage((XSLFPictureShape) shape, this, uuid);
        } else if (shape instanceof XSLFTable) {
            element = new PotTable((XSLFTable) shape, this, uuid);
        } else if (shape instanceof XSLFGroupShape) {
            element = new PotGroup((XSLFGroupShape) shape, this, uuid);
        } else if (shape instanceof XSLFConnectorShape) {
            element = new PotConnector((XSLFConnectorShape) shape, this, uuid, PotConnector.ConnectorType.STRAIGHT);
        } else {
            element = new PotUnknownElement(shape, this, uuid);
        }

        if (element != null) {
            presentation.registerElement(uuid, element);
        }

        return element;
    }

    private PictureData.PictureType determinePictureType(String fileNameOrContentType) {
        String lower = fileNameOrContentType.toLowerCase();

        if (lower.contains("png")) {
            return PictureData.PictureType.PNG;
        } else if (lower.contains("jpg") || lower.contains("jpeg")) {
            return PictureData.PictureType.JPEG;
        } else if (lower.contains("gif")) {
            return PictureData.PictureType.GIF;
        } else if (lower.contains("bmp")) {
            return PictureData.PictureType.BMP;
        } else if (lower.contains("wmf")) {
            return PictureData.PictureType.WMF;
        } else if (lower.contains("emf")) {
            return PictureData.PictureType.EMF;
        } else if (lower.contains("tif") || lower.contains("tiff")) {
            return PictureData.PictureType.TIFF;
        } else if (lower.contains("svg")) {
            return PictureData.PictureType.SVG;
        }

        return PictureData.PictureType.PNG;
    }

    private XSLFPictureShape createMediaPlaceholder(double x, double y, double width, double height) {
        XSLFPictureData picData = presentation.addPictureData(DEFAULT_MEDIA_ICON, PictureData.PictureType.PNG);
        XSLFPictureShape mediaShape = slide.createPicture(picData);
        mediaShape.setAnchor(new Rectangle2D.Double(x, y, width, height));
        return mediaShape;
    }

    private void attachMediaToShape(XSLFPictureShape shape, byte[] mediaData, String contentType, boolean video) {
        try {
            PackagePart slidePart = slide.getPackagePart();
            String extension = mediaExtension(contentType, video);
            PackagePartName mediaPartName = PackagingURIHelper.createPartName(
                "/ppt/media/media-" + UUID.randomUUID() + "." + extension
            );
            PackagePart mediaPart = slidePart.getPackage().createPart(mediaPartName, contentType);
            try (OutputStream outputStream = mediaPart.getOutputStream()) {
                outputStream.write(mediaData);
            }

            String relId = slidePart.addRelationship(
                mediaPartName,
                TargetMode.INTERNAL,
                "http://schemas.openxmlformats.org/officeDocument/2006/relationships/media"
            ).getId();

            bindMediaXml(shape, relId, video);
        } catch (Exception e) {
            throw new PotException("Failed to attach media to shape", e, PotException.ErrorCode.MEDIA_ERROR)
                .withContext("video", video)
                .withContext("contentType", contentType);
        }
    }

    private void bindMediaXml(XSLFPictureShape shape, String relId, boolean video) {
        Node root = shape.getXmlObject().getDomNode();
        if (root == null) {
            return;
        }

        Element nvPr = findFirstElement(root, "nvPr");
        if (nvPr == null) {
            return;
        }

        String drawNs = "http://schemas.openxmlformats.org/drawingml/2006/main";
        String relNs = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
        String pNs = "http://schemas.openxmlformats.org/presentationml/2006/main";
        String p14Ns = "http://schemas.microsoft.com/office/powerpoint/2010/main";

        // audioFile/videoFile belongs under nvPr. Putting it in cNvPr can produce invalid slide XML.
        Element mediaRef = nvPr.getOwnerDocument().createElementNS(drawNs, video ? "a:videoFile" : "a:audioFile");
        mediaRef.setAttributeNS(relNs, "r:link", relId);
        nvPr.appendChild(mediaRef);

        Element extLst = ensureChild(nvPr, "extLst", pNs, "p:extLst");
        Element ext = nvPr.getOwnerDocument().createElementNS(pNs, "p:ext");
        ext.setAttribute("uri", "{DAA4B4D4-89CB-4D13-93B9-3B38D6D97D14}");
        ext.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:p14", p14Ns);
        Element media = nvPr.getOwnerDocument().createElementNS(p14Ns, "p14:media");
        media.setAttributeNS(relNs, "r:embed", relId);
        ext.appendChild(media);
        extLst.appendChild(ext);
    }

    private Element findFirstElement(Node root, String localName) {
        if (root == null) {
            return null;
        }
        if (root.getNodeType() == Node.ELEMENT_NODE && localName.equals(root.getLocalName())) {
            return (Element) root;
        }
        Node child = root.getFirstChild();
        while (child != null) {
            Element found = findFirstElement(child, localName);
            if (found != null) {
                return found;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    private Element ensureChild(Element parent, String localName, String ns, String qName) {
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE && localName.equals(child.getLocalName())) {
                return (Element) child;
            }
            child = child.getNextSibling();
        }
        Element created = parent.getOwnerDocument().createElementNS(ns, qName);
        parent.appendChild(created);
        return created;
    }

    private String normalizeMediaContentType(String contentType, boolean video) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return video ? "video/mp4" : "audio/mpeg";
        }
        return contentType.trim().toLowerCase(Locale.ROOT);
    }

    private String mediaExtension(String contentType, boolean video) {
        String lower = contentType.toLowerCase(Locale.ROOT);
        if (lower.endsWith("/mp4") || lower.contains("mp4")) return "mp4";
        if (lower.contains("mpeg") || lower.contains("mp3")) return "mp3";
        if (lower.contains("wav")) return "wav";
        if (lower.contains("aac")) return "aac";
        if (lower.contains("ogg")) return "ogg";
        if (lower.contains("webm")) return "webm";
        if (lower.contains("avi")) return "avi";
        return video ? "mp4" : "mp3";
    }

    private XSLFGraphicFrame findChartFrame() {
        List<XSLFShape> shapes = slide.getShapes();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            XSLFShape s = shapes.get(i);
            if (s instanceof XSLFGraphicFrame) {
                return (XSLFGraphicFrame) s;
            }
        }

        try {
            CTSlide ctSlide = slide.getXmlObject();
            if (ctSlide == null || ctSlide.getCSld() == null || ctSlide.getCSld().getSpTree() == null) {
                return null;
            }

            CTGraphicalObjectFrame[] frames = ctSlide.getCSld().getSpTree().getGraphicFrameArray();
            if (frames == null || frames.length == 0) {
                return null;
            }

            CTGraphicalObjectFrame ctFrame = frames[frames.length - 1];
            Constructor<XSLFGraphicFrame> ctor = XSLFGraphicFrame.class
                .getDeclaredConstructor(CTGraphicalObjectFrame.class, XSLFSheet.class);
            ctor.setAccessible(true);
            return ctor.newInstance(ctFrame, slide);
        } catch (Exception e) {
            return null;
        }
    }

    private Rectangle2D calculateBounds(List<PotElement> elements) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (PotElement element : elements) {
            minX = Math.min(minX, element.getX());
            minY = Math.min(minY, element.getY());
            maxX = Math.max(maxX, element.getX() + element.getWidth());
            maxY = Math.max(maxY, element.getY() + element.getHeight());
        }

        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    // ==================== Object Methods ====================

    @Override
    public String toString() {
        return String.format("PotSlide{index=%d, uuid=%s, elements=%d}",
            getIndex(), uuid, elementCache.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotSlide potSlide = (PotSlide) o;
        return Objects.equals(uuid, potSlide.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
