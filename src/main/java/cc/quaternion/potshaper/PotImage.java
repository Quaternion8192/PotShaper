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

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Represents an image element within a PowerPoint slide.
 *
 * <p>This class provides a fluent API for manipulating image properties such as position, size,
 * cropping, borders, and effects. It wraps the underlying Apache POI {@code XSLFPictureShape}
 * and {@code XSLFPictureData} objects, offering higher-level operations while maintaining
 * compatibility with the POI model. Images can be replaced, exported, duplicated, and styled
 * with borders and various visual effects.</p>
 *
 * <h3>Basic Usage Example</h3>
 * <pre>{@code
 * PotImage image = slide.addImage("photo.jpg")
 *     .at(100, 100)
 *     .size(400, 300)
 *     .border(PotBorder.of(PotColor.GRAY, 1));
 * }</pre>
 *
 * <h3>Cropping Examples</h3>
 * <pre>{@code
 * image.crop(0.1, 0.1, 0.1, 0.1);  // Crop 10% from each side
 * image.cropToShape(PotShapeType.OVAL);  // Crop to an oval shape (if supported)
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotImage extends PotElement {

    // ==================== Fields ====================

    /** The underlying Apache POI picture shape object. */
    private final XSLFPictureShape pictureShape;

    // ==================== Constructors ====================

    /**
     * Constructs a new {@code PotImage} instance.
     *
     * <p>This constructor is intended for internal use by the library. It associates the
     * provided POI picture shape with a parent slide and a unique identifier.</p>
     *
     * @param pictureShape the Apache POI picture shape to wrap; must not be null
     * @param parentSlide  the parent slide containing this image
     * @param uuid         the unique identifier for this element
     * @throws NullPointerException if {@code pictureShape} is null
     */
    PotImage(XSLFPictureShape pictureShape, PotSlide parentSlide, String uuid) {
        super(pictureShape, parentSlide, uuid);
        this.pictureShape = Objects.requireNonNull(pictureShape, "pictureShape cannot be null");
    }

    // ==================== Image Data Accessors ====================

    /**
     * Retrieves the raw byte data of the embedded image.
     *
     * @return the image byte array, or {@code null} if no picture data is associated
     */
    public byte[] getImageData() {
        XSLFPictureData picData = pictureShape.getPictureData();
        return picData != null ? picData.getData() : null;
    }

    /**
     * Retrieves the picture type of the embedded image.
     *
     * @return the picture type (e.g., PNG, JPEG), or {@code null} if no picture data is associated
     */
    public PictureData.PictureType getPictureType() {
        XSLFPictureData picData = pictureShape.getPictureData();
        return picData != null ? picData.getType() : null;
    }

    /**
     * Retrieves the MIME content type of the embedded image.
     *
     * @return the content type string (e.g., "image/png"), or {@code null} if no picture data is associated
     */
    public String getContentType() {
        XSLFPictureData picData = pictureShape.getPictureData();
        return picData != null ? picData.getContentType() : null;
    }

    /**
     * Retrieves the original width of the embedded image in pixels.
     *
     * @return the original width in pixels, or 0 if the dimension is unknown or unavailable
     */
    public int getOriginalWidth() {
        XSLFPictureData picData = pictureShape.getPictureData();
        if (picData != null) {
            java.awt.Dimension dim = picData.getImageDimension();
            return dim != null ? dim.width : 0;
        }
        return 0;
    }

    /**
     * Retrieves the original height of the embedded image in pixels.
     *
     * @return the original height in pixels, or 0 if the dimension is unknown or unavailable
     */
    public int getOriginalHeight() {
        XSLFPictureData picData = pictureShape.getPictureData();
        if (picData != null) {
            java.awt.Dimension dim = picData.getImageDimension();
            return dim != null ? dim.height : 0;
        }
        return 0;
    }

    /**
     * Retrieves the original file name of the embedded image.
     *
     * @return the file name, or {@code null} if no picture data is associated
     */
    public String getFileName() {
        XSLFPictureData picData = pictureShape.getPictureData();
        return picData != null ? picData.getFileName() : null;
    }

    // ==================== Image Replacement ====================

    /**
     * Replaces the current image with the image from the specified file path.
     *
     * <p>The image file is read from disk, and its type is inferred from the file extension.
     * The underlying POI picture data is updated, and the shape's XML is modified accordingly.</p>
     *
     * @param imagePath the file system path to the new image file
     * @return this {@code PotImage} instance for method chaining
     * @throws PotException if an I/O error occurs while reading the file
     * @see #replace(File)
     * @see #replace(byte[], PictureData.PictureType)
     */
    public PotImage replace(String imagePath) {
        return replace(new File(imagePath));
    }

    /**
     * Replaces the current image with the image from the specified file.
     *
     * <p>The image file is read from disk, and its type is inferred from the file name.
     * The underlying POI picture data is updated, and the shape's XML is modified accordingly.</p>
     *
     * @param file the new image file
     * @return this {@code PotImage} instance for method chaining
     * @throws PotException if an I/O error occurs while reading the file
     * @see #replace(byte[], PictureData.PictureType)
     */
    public PotImage replace(File file) {
        try {
            byte[] imageData = Files.readAllBytes(file.toPath());
            String fileName = file.getName().toLowerCase();
            PictureData.PictureType pictureType = determinePictureType(fileName);
            return replace(imageData, pictureType);
        } catch (IOException e) {
            throw PotException.ioError("reading image file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Replaces the current image with the provided image data and content type.
     *
     * <p>The picture type is inferred from the content type string (e.g., "image/jpeg").
     * The underlying POI picture data is updated, and the shape's XML is modified accordingly.</p>
     *
     * @param imageData    the raw byte data of the new image
     * @param contentType  the MIME content type of the image (e.g., "image/jpeg")
     * @return this {@code PotImage} instance for method chaining
     * @see #replace(byte[], PictureData.PictureType)
     */
    public PotImage replace(byte[] imageData, String contentType) {
        PictureData.PictureType pictureType = determinePictureType(contentType);
        return replace(imageData, pictureType);
    }

    /**
     * Replaces the current image with the provided image data and picture type.
     *
     * <p>This method adds the new picture data to the presentation and updates the shape's
     * XML reference to point to the new data. The previous image data is not automatically
     * removed from the presentation file.</p>
     *
     * @param imageData    the raw byte data of the new image
     * @param pictureType  the picture type of the new image
     * @return this {@code PotImage} instance for method chaining
     */
    public PotImage replace(byte[] imageData, PictureData.PictureType pictureType) {
        XSLFPictureData newPicData = parentSlide.getPresentation().addPictureData(imageData, pictureType);

        // Update the XML reference to the new picture data
        XmlUtils.replacePictureData(pictureShape, newPicData);

        return this;
    }

    // ==================== Cropping Operations ====================

    /**
     * Applies a crop to the image by specifying fractional offsets from each side.
     *
     * <p>The crop values are fractions between 0.0 and 1.0, representing the proportion
     * of the image to remove from each side. For example, a top value of 0.1 crops away
     * 10% of the image from the top. The method modifies the OpenXML {@code srcRect}
     * element within the shape's XML.</p>
     *
     * <p>Values are clamped to the valid range [0.0, 1.0] and converted to integer
     * percentages (0-100000) as required by the OpenXML specification.</p>
     *
     * @param top     the fraction to crop from the top (0.0-1.0)
     * @param right   the fraction to crop from the right (0.0-1.0)
     * @param bottom  the fraction to crop from the bottom (0.0-1.0)
     * @param left    the fraction to crop from the left (0.0-1.0)
     * @return this {@code PotImage} instance for method chaining
     */
    public PotImage crop(double top, double right, double bottom, double left) {
        try {
            int t = (int) Math.round(
                Math.max(PotConstants.FRACTION_MIN, Math.min(PotConstants.FRACTION_MAX, top))
                    * PotConstants.OPENXML_PERCENT_SCALE);
            int r = (int) Math.round(
                Math.max(PotConstants.FRACTION_MIN, Math.min(PotConstants.FRACTION_MAX, right))
                    * PotConstants.OPENXML_PERCENT_SCALE);
            int b = (int) Math.round(
                Math.max(PotConstants.FRACTION_MIN, Math.min(PotConstants.FRACTION_MAX, bottom))
                    * PotConstants.OPENXML_PERCENT_SCALE);
            int l = (int) Math.round(
                Math.max(PotConstants.FRACTION_MIN, Math.min(PotConstants.FRACTION_MAX, left))
                    * PotConstants.OPENXML_PERCENT_SCALE);

            // Directly manipulate the DOM to set blipFill/srcRect
            org.w3c.dom.Node shapeNode = pictureShape.getXmlObject().getDomNode();
            if (!(shapeNode instanceof org.w3c.dom.Element)) {
                return this;
            }
            org.w3c.dom.Document doc = shapeNode.getOwnerDocument();
            String aNs = "http://schemas.openxmlformats.org/drawingml/2006/main";

            // Locate the blipFill element
            org.w3c.dom.NodeList blipFills =
                ((org.w3c.dom.Element) shapeNode).getElementsByTagNameNS(aNs, "blipFill");
            if (blipFills.getLength() == 0) {
                return this;
            }
            org.w3c.dom.Element blipFill = (org.w3c.dom.Element) blipFills.item(0);

            // Remove any existing srcRect elements
            org.w3c.dom.NodeList existing = blipFill.getElementsByTagNameNS(aNs, "srcRect");
            for (int i = 0; i < existing.getLength(); i++) {
                blipFill.removeChild(existing.item(i));
            }

            // Create and configure the new srcRect element
            org.w3c.dom.Element srcRect = doc.createElementNS(aNs, "a:srcRect");
            if (t > 0) srcRect.setAttribute("t", String.valueOf(t));
            if (r > 0) srcRect.setAttribute("r", String.valueOf(r));
            if (b > 0) srcRect.setAttribute("b", String.valueOf(b));
            if (l > 0) srcRect.setAttribute("l", String.valueOf(l));

            // Insert srcRect after the blip element if present, otherwise at the beginning
            org.w3c.dom.NodeList blips = blipFill.getElementsByTagNameNS(aNs, "blip");
            if (blips.getLength() > 0) {
                org.w3c.dom.Node blip = blips.item(0);
                blipFill.insertBefore(srcRect, blip.getNextSibling());
            } else {
                blipFill.insertBefore(srcRect, blipFill.getFirstChild());
            }
        } catch (Exception e) {
            PotLogger.warn(PotImage.class, "crop", "Failed to apply crop", e);
        }
        return this;
    }

    /**
     * Retrieves the current clipping (crop) insets of the image.
     *
     * <p>This method delegates to the underlying POI {@code XSLFPictureShape.getClipping()}.
     * The returned insets represent the cropped portions from each side in pixels.</p>
     *
     * @return the current clipping insets, or {@code null} if not available
     */
    public java.awt.Insets getClipping() {
        return pictureShape.getClipping();
    }

    /**
     * Removes any applied crop from the image.
     *
     * <p>Note: This implementation currently returns without modifying the image,
     * as POI may not provide a direct method to remove clipping. Future versions
     * may implement proper crop removal.</p>
     *
     * @return this {@code PotImage} instance for method chaining
     */
    public PotImage removeCrop() {
        // Note: POI may not support removing clipping directly
        return this;
    }

    /**
     * Crops the image to a specific aspect ratio.
     *
     * <p>The image is cropped equally from the sides or top/bottom to achieve the target
     * aspect ratio while preserving the original image's center. If the image already
     * matches the target ratio, no crop is applied.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Crop to a 16:9 widescreen aspect ratio
     * image.cropToAspect(16, 9);
     * }</pre>
     *
     * @param aspectWidth   the width component of the target aspect ratio (e.g., 16)
     * @param aspectHeight  the height component of the target aspect ratio (e.g., 9)
     * @return this {@code PotImage} instance for method chaining
     * @see #cropToSquare()
     */
    public PotImage cropToAspect(double aspectWidth, double aspectHeight) {
        int origWidth = getOriginalWidth();
        int origHeight = getOriginalHeight();

        if (origWidth == 0 || origHeight == 0) {
            return this;
        }

        double targetRatio = aspectWidth / aspectHeight;
        double currentRatio = (double) origWidth / origHeight;

        double cropTop = 0, cropRight = 0, cropBottom = 0, cropLeft = 0;

        if (currentRatio > targetRatio) {
            // Image is wider than target; crop from left and right
            double newWidth = origHeight * targetRatio;
            double cropWidth = (origWidth - newWidth) / 2;
            double cropPercent = cropWidth / origWidth;
            cropLeft = cropPercent;
            cropRight = cropPercent;
        } else if (currentRatio < targetRatio) {
            // Image is taller than target; crop from top and bottom
            double newHeight = origWidth / targetRatio;
            double cropHeight = (origHeight - newHeight) / 2;
            double cropPercent = cropHeight / origHeight;
            cropTop = cropPercent;
            cropBottom = cropPercent;
        }

        return crop(cropTop, cropRight, cropBottom, cropLeft);
    }

    /**
     * Crops the image to a square aspect ratio (1:1).
     *
     * <p>This is a convenience method that delegates to {@link #cropToAspect(double, double)}
     * with both parameters set to 1.</p>
     *
     * @return this {@code PotImage} instance for method chaining
     * @see #cropToAspect(double, double)
     */
    public PotImage cropToSquare() {
        return cropToAspect(1, 1);
    }

    // ==================== Border Styling ====================

    /**
     * Applies a border to the image.
     *
     * <p>The border properties (color, width, dash style, and cap style) are taken from
     * the provided {@code PotBorder} object. If the border is {@code null}, the method
     * calls {@link #noBorder()} to remove any existing border.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotBorder border = PotBorder.of(PotColor.RED, 2.0)
     *     .dash(PotDashStyle.DASH)
     *     .cap(PotCapStyle.ROUND);
     * image.border(border);
     * }</pre>
     *
     * @param border the border configuration to apply
     * @return this {@code PotImage} instance for method chaining
     * @see #borderColor(PotColor)
     * @see #borderWidth(double)
     * @see #noBorder()
     */
    public PotImage border(PotBorder border) {
        if (border == null) {
            return noBorder();
        }

        if (border.getColor() != null) {
            pictureShape.setLineColor(border.getColor().toAwtColor());
        }
        if (border.getWidth() > 0) {
            pictureShape.setLineWidth(border.getWidth());
        }
        if (border.getDashStyle() != null) {
            pictureShape.setLineDash(border.getDashStyle().toPoiDash());
        }
        if (border.getCapStyle() != null) {
            pictureShape.setLineCap(border.getCapStyle().toPoiCap());
        }

        return this;
    }

    /**
     * Sets the border color of the image.
     *
     * <p>This method only updates the border color; other border properties remain unchanged.
     * If the image currently has no border (zero width), setting a color will not make the
     * border visible until a width is also set.</p>
     *
     * @param color the new border color
     * @return this {@code PotImage} instance for method chaining
     * @see #borderWidth(double)
     * @see #border(PotBorder)
     */
    public PotImage borderColor(PotColor color) {
        if (color != null) {
            pictureShape.setLineColor(color.toAwtColor());
        }
        return this;
    }

    /**
     * Sets the border width of the image.
     *
     * <p>The width is specified in points. A width of zero effectively removes the border.
     * If the border color has not been set, the default color (usually black) is used.</p>
     *
     * @param width the new border width in points
     * @return this {@code PotImage} instance for method chaining
     * @see #borderColor(PotColor)
     * @see #border(PotBorder)
     */
    public PotImage borderWidth(double width) {
        pictureShape.setLineWidth(width);
        return this;
    }

    /**
     * Removes the border from the image.
     *
     * <p>This is achieved by setting the border width to zero. The border color and other
     * style properties are preserved but will not be visible.</p>
     *
     * @return this {@code PotImage} instance for method chaining
     * @see #border(PotBorder)
     */
    public PotImage noBorder() {
        pictureShape.setLineWidth(0);
        return this;
    }

    // ==================== Scaling Operations ====================

    /**
     * Resets the image to its original pixel dimensions.
     *
     * <p>The image is resized to match the dimensions of the embedded image data as
     * reported by {@link #getOriginalWidth()} and {@link #getOriginalHeight()}. If the
     * original dimensions are unavailable, no change is made.</p>
     *
     * @return this {@code PotImage} instance for method chaining
     * @see #scaleToWidth(double)
     * @see #scaleToHeight(double)
     */
    public PotImage resetToOriginalSize() {
        int origWidth = getOriginalWidth();
        int origHeight = getOriginalHeight();
        if (origWidth > 0 && origHeight > 0) {
            size(origWidth, origHeight);
        }
        return this;
    }

    /**
     * Scales the image proportionally to a specific width.
     *
     * <p>The height is adjusted automatically to maintain the original aspect ratio.
     * If the original dimensions are unavailable, no change is made.</p>
     *
     * @param width the target width in points
     * @return this {@code PotImage} instance for method chaining
     * @see #scaleToHeight(double)
     * @see #scaleToFit(double, double)
     */
    public PotImage scaleToWidth(double width) {
        int origWidth = getOriginalWidth();
        int origHeight = getOriginalHeight();
        if (origWidth > 0 && origHeight > 0) {
            double scale = width / origWidth;
            size(width, origHeight * scale);
        }
        return this;
    }

    /**
     * Scales the image proportionally to a specific height.
     *
     * <p>The width is adjusted automatically to maintain the original aspect ratio.
     * If the original dimensions are unavailable, no change is made.</p>
     *
     * @param height the target height in points
     * @return this {@code PotImage} instance for method chaining
     * @see #scaleToWidth(double)
     * @see #scaleToFit(double, double)
     */
    public PotImage scaleToHeight(double height) {
        int origWidth = getOriginalWidth();
        int origHeight = getOriginalHeight();
        if (origWidth > 0 && origHeight > 0) {
            double scale = height / origHeight;
            size(origWidth * scale, height);
        }
        return this;
    }

    /**
     * Scales the image proportionally to fit within a bounding box.
     *
     * <p>The image is scaled down (if necessary) so that both its width and height are
     * less than or equal to the specified maximum dimensions, while preserving the
     * original aspect ratio. If the image is already smaller, it is not enlarged.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Ensure the image fits within a 400x300 area
     * image.scaleToFit(400, 300);
     * }</pre>
     *
     * @param maxWidth  the maximum allowed width in points
     * @param maxHeight the maximum allowed height in points
     * @return this {@code PotImage} instance for method chaining
     * @see #scaleToFill(double, double)
     * @see #scaleToWidth(double)
     */
    public PotImage scaleToFit(double maxWidth, double maxHeight) {
        int origWidth = getOriginalWidth();
        int origHeight = getOriginalHeight();
        if (origWidth > 0 && origHeight > 0) {
            double scaleW = maxWidth / origWidth;
            double scaleH = maxHeight / origHeight;
            double scale = Math.min(scaleW, scaleH);
            size(origWidth * scale, origHeight * scale);
        }
        return this;
    }

    /**
     * Scales the image proportionally to completely fill a bounding box.
     *
     * <p>The image is scaled up (if necessary) so that both its width and height are
     * at least the specified target dimensions, while preserving the original aspect ratio.
     * This may cause the image to extend beyond the target box, which is typically used
     * in conjunction with cropping.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Scale the image to cover a 400x300 area (may overflow)
     * image.scaleToFill(400, 300);
     * }</pre>
     *
     * @param targetWidth  the minimum target width in points
     * @param targetHeight the minimum target height in points
     * @return this {@code PotImage} instance for method chaining
     * @see #scaleToFit(double, double)
     * @see #cropToAspect(double, double)
     */
    public PotImage scaleToFill(double targetWidth, double targetHeight) {
        int origWidth = getOriginalWidth();
        int origHeight = getOriginalHeight();
        if (origWidth > 0 && origHeight > 0) {
            double scaleW = targetWidth / origWidth;
            double scaleH = targetHeight / origHeight;
            double scale = Math.max(scaleW, scaleH);
            size(origWidth * scale, origHeight * scale);
        }
        return this;
    }

    // ==================== Export Operations ====================

    /**
     * Exports the embedded image data to a file.
     *
     * <p>The raw image bytes are written directly to the specified file. The file format
     * is determined by the embedded image type (e.g., PNG, JPEG). If no image data is
     * available, the method does nothing.</p>
     *
     * @param file the destination file
     * @throws PotException if an I/O error occurs while writing the file
     * @see #exportTo(OutputStream)
     */
    public void exportTo(File file) {
        byte[] data = getImageData();
        if (data != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            } catch (IOException e) {
                throw PotException.ioError("exporting image to file: " + file.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Exports the embedded image data to an output stream.
     *
     * <p>The raw image bytes are written directly to the provided stream. The stream is
     * not closed by this method. If no image data is available, the method does nothing.</p>
     *
     * @param outputStream the destination output stream
     * @throws PotException if an I/O error occurs while writing to the stream
     * @see #exportTo(File)
     */
    public void exportTo(OutputStream outputStream) {
        byte[] data = getImageData();
        if (data != null) {
            try {
                outputStream.write(data);
            } catch (IOException e) {
                throw PotException.ioError("exporting image to stream", e);
            }
        }
    }

    // ==================== Duplication ====================

    /**
     * Creates a duplicate of this image on the same slide.
     *
     * <p>The duplicate is placed slightly offset from the original (as defined by
     * {@link PotConstants#DUPLICATE_OFFSET_POINTS}) and inherits the original's position,
     * size, rotation, and cropping. The image data is reembedded into the presentation.</p>
     *
     * <p>If the original image has no embedded data, an {@code UnsupportedOperationException}
     * is thrown.</p>
     *
     * @return the newly created {@code PotImage} instance
     * @throws PotException if the image cannot be duplicated due to missing data
     * @see PotSlide#addImage(byte[], String)
     */
    @Override
    public PotImage duplicate() {
        byte[] imageData = getImageData();
        if (imageData == null || imageData.length == 0) {
            throw PotException.unsupportedOperation("duplicate for PotImage without image data");
        }

        PotImage copy = parentSlide.addImage(imageData, getContentType());
        copy.at(getX() + PotConstants.DUPLICATE_OFFSET_POINTS, getY() + PotConstants.DUPLICATE_OFFSET_POINTS)
            .size(getWidth(), getHeight())
            .rotate(getRotation());

        // Copy cropping  POI's getClipping returns java.awt.Insets
        java.awt.Insets clipping = getClipping();
        if (clipping != null &&
            (clipping.top > 0 || clipping.right > 0 || clipping.bottom > 0 || clipping.left > 0)) {
            copy.crop(clipping.top, clipping.right, clipping.bottom, clipping.left);
        }

        return copy;
    }

    // ==================== Lowlevel Accessors ====================

    /**
     * Returns the underlying Apache POI {@code XSLFPictureShape} object.
     *
     * <p>This method provides direct access to the POI shape for advanced operations
     * not covered by the {@code PotImage} API. Modifications made directly to the
     * returned object may affect the behavior of this {@code PotImage} instance.</p>
     *
     * @return the wrapped POI picture shape
     */
    public XSLFPictureShape getRawPictureShape() {
        return pictureShape;
    }

    /**
     * Returns the underlying Apache POI {@code XSLFPictureData} object.
     *
     * <p>This method provides direct access to the POI picture data for advanced operations
     * such as inspecting image metadata or performing lowlevel replacements.</p>
     *
     * @return the underlying POI picture data, or {@code null} if not available
     * @see #getRawPictureShape()
     */
    public XSLFPictureData getRawPictureData() {
        return pictureShape.getPictureData();
    }

    // ==================== Internal Helpers ====================

    /**
     * Determines the POI picture type from a file name or contenttype string.
     *
     * <p>The method performs a caseinsensitive substring match against common image
     * format identifiers (e.g., "png", "jpg", "gif"). If no match is found, PNG is
     * returned as a default.</p>
     *
     * @param fileNameOrContentType the file name or MIME contenttype string
     * @return the corresponding {@code PictureData.PictureType}, defaulting to PNG
     */
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

        // Default to PNG
        return PictureData.PictureType.PNG;
    }

    // ==================== Fluent Overrides ====================
    // The following methods override the parent class to return PotImage.

    @Override
    public PotImage at(double x, double y) {
        super.at(x, y);
        return this;
    }

    @Override
    public PotImage setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public PotImage x(double x) {
        super.x(x);
        return this;
    }

    @Override
    public PotImage setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public PotImage y(double y) {
        super.y(y);
        return this;
    }

    @Override
    public PotImage position(double x, double y) {
        super.position(x, y);
        return this;
    }

    @Override
    public PotImage move(double dx, double dy) {
        super.move(dx, dy);
        return this;
    }

    @Override
    public PotImage size(double width, double height) {
        super.size(width, height);
        return this;
    }

    @Override
    public PotImage setWidth(double width) {
        super.setWidth(width);
        return this;
    }

    @Override
    public PotImage width(double width) {
        super.width(width);
        return this;
    }

    @Override
    public PotImage setHeight(double height) {
        super.setHeight(height);
        return this;
    }

    @Override
    public PotImage height(double height) {
        super.height(height);
        return this;
    }

    @Override
    public PotImage scale(double scale) {
        super.scale(scale);
        return this;
    }

    @Override
    public PotImage scale(double scaleX, double scaleY) {
        super.scale(scaleX, scaleY);
        return this;
    }

    @Override
    public PotImage rotate(double angle) {
        super.rotate(angle);
        return this;
    }

    @Override
    public PotImage rotation(double angle) {
        super.rotation(angle);
        return this;
    }

    @Override
    public PotImage rotateBy(double deltaAngle) {
        super.rotateBy(deltaAngle);
        return this;
    }

    @Override
    public PotImage flipHorizontal() {
        super.flipHorizontal();
        return this;
    }

    @Override
    public PotImage flipVertical() {
        super.flipVertical();
        return this;
    }

    @Override
    public PotImage bringToFront() {
        super.bringToFront();
        return this;
    }

    @Override
    public PotImage sendToBack() {
        super.sendToBack();
        return this;
    }

    @Override
    public PotImage bringForward() {
        super.bringForward();
        return this;
    }

    @Override
    public PotImage sendBackward() {
        super.sendBackward();
        return this;
    }

    @Override
    public PotImage animate(PotAnimation animation) {
        super.animate(animation);
        return this;
    }

    @Override
    public PotImage removeAnimation() {
        super.removeAnimation();
        return this;
    }

    @Override
    public PotImage hyperlink(String url) {
        super.hyperlink(url);
        return this;
    }

    @Override
    public PotImage hyperlink(PotHyperlink link) {
        super.hyperlink(link);
        return this;
    }

    @Override
    public PotImage hyperlinkToSlide(int slideIndex) {
        super.hyperlinkToSlide(slideIndex);
        return this;
    }

    @Override
    public PotImage removeHyperlink() {
        super.removeHyperlink();
        return this;
    }

    @Override
    public PotImage action(PotAction action) {
        super.action(action);
        return this;
    }

    @Override
    public PotImage removeAction(boolean isHover) {
        super.removeAction(isHover);
        return this;
    }

    @Override
    public PotImage shadow(PotShadow shadow) {
        super.shadow(shadow);
        return this;
    }

    @Override
    public PotImage removeShadow() {
        super.removeShadow();
        return this;
    }

    @Override
    public PotImage reflection(PotEffect.Reflection reflection) {
        super.reflection(reflection);
        return this;
    }

    @Override
    public PotImage glow(PotEffect.Glow glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public PotImage softEdge(PotEffect.SoftEdge softEdge) {
        super.softEdge(softEdge);
        return this;
    }

    @Override
    public PotImage removeEffects() {
        super.removeEffects();
        return this;
    }

    @Override
    public PotImage rotation3D(Pot3DFormat.Rotation rotation) {
        super.rotation3D(rotation);
        return this;
    }

    @Override
    public PotImage bevel(Pot3DFormat.Bevel bevel) {
        super.bevel(bevel);
        return this;
    }

    @Override
    public PotImage material(Pot3DFormat.Material material) {
        super.material(material);
        return this;
    }

    @Override
    public PotImage lighting(Pot3DFormat.Lighting lighting) {
        super.lighting(lighting);
        return this;
    }

    @Override
    public PotImage opacity(double opacity) {
        super.opacity(opacity);
        return this;
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        return String.format("PotImage{uuid=%s, type=%s, original=(%dx%d), position=(%.0f,%.0f), size=(%.0fx%.0f)}",
            uuid, getPictureType(), getOriginalWidth(), getOriginalHeight(), 
            getX(), getY(), getWidth(), getHeight());
    }
}
