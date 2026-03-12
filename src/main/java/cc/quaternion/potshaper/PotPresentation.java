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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The central class for creating, opening, and manipulating PowerPoint presentations.
 * <p>
 * This class serves as the main entry point for the PotShaper library, providing a high-level,
 * fluent API for slide management, content addition, and presentation persistence. It wraps the
 * underlying Apache POI {@code XMLSlideShow} and manages the lifecycle of slides and elements.
 * Each presentation maintains internal registries for slides and elements using UUIDs for
 * identification, supports custom page sizes, and offers utilities for rendering and metadata
 * management.
 * </p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * // Create a new presentation with a default widescreen slide
 * PotPresentation ppt = PotPresentation.create();
 *
 * // Open an existing presentation from a file
 * PotPresentation ppt = PotPresentation.open("test.pptx");
 *
 * // Add a text box to the first slide
 * PotSlide slide = ppt.getSlide(0);
 * slide.addTextBox("Hello").at(100, 100);
 *
 * // Save the presentation and release resources
 * ppt.save("output.pptx");
 * ppt.close();
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotPresentation implements AutoCloseable {

    // ==================== Fields ====================

    /** The underlying Apache POI presentation object. */
    private final XMLSlideShow slideShow;

    /** The source file if the presentation was opened from disk; {@code null} for new presentations. */
    private final File sourceFile;

    /** Manager for generating unique identifiers for slides and elements. */
    private final UuidManager uuidManager;

    /** Registry mapping element UUIDs to their corresponding {@code PotElement} instances. */
    private final Map<String, PotElement> elementRegistry;

    /** Registry mapping slide UUIDs to their corresponding {@code PotSlide} instances. */
    private final Map<String, PotSlide> slideRegistry;

    /** Cache of slides in presentation order for fast indexed access. */
    private final List<PotSlide> slideCache;

    /** Flag indicating whether this presentation has been closed and its resources released. */
    private boolean closed = false;

    // ==================== Constructors ====================

    /**
     * Constructs a new presentation wrapper around an existing Apache POI slide show.
     * <p>
     * This constructor is private; use the static factory methods {@link #create()} or
     * {@link #open(File)} to obtain instances. It initializes the internal registries and
     * caches the slides from the provided {@code XMLSlideShow}.
     * </p>
     *
     * @param slideShow  the Apache POI presentation object to wrap; must not be {@code null}
     * @param sourceFile the source file on disk, or {@code null} if created in memory
     * @throws NullPointerException if {@code slideShow} is {@code null}
     */
    private PotPresentation(XMLSlideShow slideShow, File sourceFile) {
        this.slideShow = Objects.requireNonNull(slideShow, "slideShow cannot be null");
        this.sourceFile = sourceFile;
        this.uuidManager = new UuidManager();
        this.elementRegistry = new LinkedHashMap<>();
        this.slideRegistry = new LinkedHashMap<>();
        this.slideCache = new ArrayList<>();

        // Initialize the slide cache from the POI slide list
        initializeCache();
    }

    // ==================== Static Factory Methods ====================

    /**
     * Opens an existing presentation from the specified file path.
     * <p>
     * This method loads the presentation file from disk and creates a new {@code PotPresentation}
     * instance. The file must exist and be a valid PowerPoint (.pptx) file.
     * </p>
     *
     * @param path the file system path to the presentation file
     * @return a new {@code PotPresentation} instance representing the opened file
     * @throws PotException if the file does not exist, cannot be read, or is not a valid presentation
     * @see #open(File)
     * @see #open(InputStream)
     */
    public static PotPresentation open(String path) {
        return open(new File(path));
    }

    /**
     * Opens an existing presentation from the specified file.
     * <p>
     * This method loads the presentation file from disk and creates a new {@code PotPresentation}
     * instance. The file must exist and be a valid PowerPoint (.pptx) file.
     * </p>
     *
     * @param file the presentation file to open
     * @return a new {@code PotPresentation} instance representing the opened file
     * @throws PotException if the file does not exist, cannot be read, or is not a valid presentation
     * @see #open(String)
     * @see #open(InputStream)
     */
    public static PotPresentation open(File file) {
        if (!file.exists()) {
            throw PotException.fileNotFound(file.getAbsolutePath());
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            XMLSlideShow slideShow = new XMLSlideShow(fis);
            return new PotPresentation(slideShow, file);
        } catch (IOException e) {
            throw PotException.ioError("opening file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Opens an existing presentation from an input stream.
     * <p>
     * This method reads the presentation data from the provided stream and creates a new
     * {@code PotPresentation} instance. The stream must contain valid PowerPoint (.pptx) data.
     * The caller is responsible for managing the stream's lifecycle; it will be read from but not
     * closed by this method.
     * </p>
     *
     * @param inputStream the input stream containing presentation data
     * @return a new {@code PotPresentation} instance representing the opened data
     * @throws PotException if the stream cannot be read or does not contain valid presentation data
     * @see #open(String)
     * @see #open(File)
     */
    public static PotPresentation open(InputStream inputStream) {
        try {
            XMLSlideShow slideShow = new XMLSlideShow(inputStream);
            return new PotPresentation(slideShow, null);
        } catch (IOException e) {
            throw PotException.ioError("opening from stream", e);
        }
    }

    /**
     * Creates a new blank presentation with a default widescreen (16:9) page size.
     * <p>
     * The new presentation will contain a single empty slide. The page size is set to
     * {@link PotPageSize#WIDESCREEN_16_9}.
     * </p>
     *
     * @return a new {@code PotPresentation} instance with one empty slide
     * @throws PotException if the underlying Apache POI library fails to initialize
     * @see #create(PotPageSize)
     * @see #create(PotPageSize.CustomPageSize)
     */
    public static PotPresentation create() {
        return create(PotPageSize.WIDESCREEN_16_9);
    }

    /**
     * Creates a new blank presentation with the specified standard page size.
     * <p>
     * The new presentation will contain a single empty slide. If {@code pageSize} is {@code null}
     * or {@link PotPageSize#CUSTOM}, the default Apache POI page size is used.
     * </p>
     *
     * @param pageSize the standard page size to apply; may be {@code null}
     * @return a new {@code PotPresentation} instance with one empty slide
     * @throws PotException if the underlying Apache POI library fails to initialize
     * @see #create()
     * @see #create(PotPageSize.CustomPageSize)
     */
    public static PotPresentation create(PotPageSize pageSize) {
        XMLSlideShow slideShow;
        try {
            slideShow = new XMLSlideShow();
        } catch (VirtualMachineError e) {
            // Re-throw JVM fatal errors
            throw e;
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "create",
                "XMLSlideShow initialization failed on JDK " + System.getProperty("java.version"), e);
            throw PotException.ioError("Failed to create XMLSlideShow", e);
        }

        // Apply the page size if specified and not custom
        if (pageSize != null && pageSize != PotPageSize.CUSTOM) {
            Dimension size = new Dimension(
                (int) pageSize.getWidth(),
                (int) pageSize.getHeight()
            );
            slideShow.setPageSize(size);
        }

        PotPresentation ppt = new PotPresentation(slideShow, null);

        // Add a default first slide
        ppt.addSlide();

        return ppt;
    }

    /**
     * Creates a new blank presentation with a custom page size.
     * <p>
     * The new presentation will contain a single empty slide. The page dimensions are taken from
     * the provided {@code CustomPageSize} object.
     * </p>
     *
     * @param customSize the custom page dimensions; may be {@code null}
     * @return a new {@code PotPresentation} instance with one empty slide
     * @throws PotException if the underlying Apache POI library fails to initialize
     * @see #create()
     * @see #create(PotPageSize)
     */
    public static PotPresentation create(PotPageSize.CustomPageSize customSize) {
        XMLSlideShow slideShow;
        try {
            slideShow = new XMLSlideShow();
        } catch (VirtualMachineError e) {
            throw e;
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "create",
                "XMLSlideShow initialization failed on JDK " + System.getProperty("java.version"), e);
            throw PotException.ioError("Failed to create XMLSlideShow", e);
        }

        if (customSize != null) {
            slideShow.setPageSize(customSize.toDimension());
        }

        PotPresentation ppt = new PotPresentation(slideShow, null);
        ppt.addSlide();
        return ppt;
    }

    // ==================== Save and Close ====================

    /**
     * Saves the presentation to its original source file.
     * <p>
     * This method writes the current state of the presentation back to the file from which it was
     * opened. If the presentation was created in memory (i.e., has no source file), a
     * {@code PotException} is thrown.
     * </p>
     *
     * @throws PotException if the presentation has no source file, is closed, or an I/O error occurs
     * @see #save(String)
     * @see #save(File)
     * @see #save(OutputStream)
     */
    public void save() {
        ensureNotClosed();
        if (sourceFile == null) {
            throw new PotException("No source file. Use save(path) instead.");
        }
        save(sourceFile);
    }

    /**
     * Saves the presentation to the specified file path.
     * <p>
     * This method writes the current state of the presentation to a new file. If a file already
     * exists at the given path, it will be overwritten.
     * </p>
     *
     * @param path the file system path where the presentation should be saved
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save()
     * @see #save(File)
     * @see #save(OutputStream)
     */
    public void save(String path) {
        save(new File(path));
    }

    /**
     * Saves the presentation to a new file (alias for {@link #save(String)}).
     * <p>
     * This method provides a fluent API alternative to {@link #save(String)}. It behaves identically.
     * </p>
     *
     * @param path the file system path where the presentation should be saved
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save(String)
     */
    public void saveAs(String path) {
        save(path);
    }

    /**
     * Saves the presentation to a new file (alias for {@link #save(File)}).
     * <p>
     * This method provides a fluent API alternative to {@link #save(File)}. It behaves identically.
     * </p>
     *
     * @param file the file where the presentation should be saved
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save(File)
     */
    public void saveAs(File file) {
        save(file);
    }

    /**
     * Saves the presentation to the specified file.
     * <p>
     * This method writes the current state of the presentation to the given file. If the file
     * already exists, it will be overwritten.
     * </p>
     *
     * @param file the file where the presentation should be saved
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save()
     * @see #save(String)
     * @see #save(OutputStream)
     */
    public void save(File file) {
        ensureNotClosed();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            slideShow.write(fos);
        } catch (IOException e) {
            throw PotException.ioError("saving to file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Saves the presentation to the specified output stream.
     * <p>
     * This method writes the current state of the presentation to the provided output stream.
     * The caller is responsible for closing the stream after this method returns.
     * </p>
     *
     * @param outputStream the output stream to write the presentation data to
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save()
     * @see #save(String)
     * @see #save(File)
     */
    public void save(OutputStream outputStream) {
        ensureNotClosed();
        try {
            slideShow.write(outputStream);
        } catch (IOException e) {
            throw PotException.ioError("saving to stream", e);
        }
    }

    /**
     * Saves the presentation to the specified file path with additional options.
     * <p>
     * This method writes the current state of the presentation to a new file, applying the
     * provided {@code SaveOptions} such as backup creation, buffering, and web optimization.
     * </p>
     *
     * @param path    the file system path where the presentation should be saved
     * @param options the configuration options for the save operation; may be {@code null}
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save(File, SaveOptions)
     * @see #save(OutputStream, SaveOptions)
     */
    public void save(String path, SaveOptions options) {
        save(new File(path), options);
    }

    /**
     * Saves the presentation to the specified file with additional options.
     * <p>
     * This method writes the current state of the presentation to the given file, applying the
     * provided {@code SaveOptions} such as backup creation, buffering, and web optimization.
     * </p>
     *
     * @param file    the file where the presentation should be saved
     * @param options the configuration options for the save operation; may be {@code null}
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save(String, SaveOptions)
     * @see #save(OutputStream, SaveOptions)
     */
    public void save(File file, SaveOptions options) {
        ensureNotClosed();
        if (options == null) {
            save(file);
            return;
        }

        try {
            // Create a versioned backup if requested and the file exists
            if (options.isCreateBackup() && file.exists()) {
                createVersionedBackup(file, options.hasBackupPath() ? options.getBackupPath() : null);
            }

            // Write with buffering
            try (BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file), options.getBufferSize())) {
                slideShow.write(bos);
            }
        } catch (IOException e) {
            throw PotException.ioError("saving to file with options: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Saves the presentation to the specified output stream with additional options.
     * <p>
     * This method writes the current state of the presentation to the provided output stream,
     * applying the given {@code SaveOptions} such as buffering and web optimization.
     * </p>
     *
     * @param outputStream the output stream to write the presentation data to
     * @param options      the configuration options for the save operation; may be {@code null}
     * @throws PotException if the presentation is closed or an I/O error occurs
     * @see #save(String, SaveOptions)
     * @see #save(File, SaveOptions)
     */
    public void save(OutputStream outputStream, SaveOptions options) {
        ensureNotClosed();
        if (options == null) {
            save(outputStream);
            return;
        }

        try {
            OutputStream finalOut = outputStream;

            // Apply buffering for web optimization
            if (options.isOptimizeForWeb()) {
                finalOut = new BufferedOutputStream(outputStream, options.getBufferSize());
            }

            slideShow.write(finalOut);

            if (finalOut != outputStream) {
                finalOut.flush();
            }
        } catch (IOException e) {
            throw PotException.ioError("saving to stream with options", e);
        }
    }

    /**
     * Closes the presentation and releases all underlying resources.
     * <p>
     * This method closes the internal Apache POI {@code XMLSlideShow} and clears all internal
     * registries. After calling this method, the presentation cannot be used further. If the
     * presentation is already closed, this method does nothing.
     * </p>
     *
     * @throws PotException if an I/O error occurs while closing the underlying resources
     */
    @Override
    public void close() {
        if (!closed) {
            IOException closeException = null;
            try {
                slideShow.close();
            } catch (IOException e) {
                closeException = e;
                PotLogger.warn(PotPresentation.class, "close",
                    "Failed to close presentation resources", e);
            } finally {
                elementRegistry.clear();
                slideRegistry.clear();
                slideCache.clear();
                closed = true;
            }

            if (closeException != null) {
                throw PotException.ioError("closing presentation", closeException);
            }
        }
    }

    // ==================== Memory Management ====================

    /**
     * Calculates and returns memory usage statistics for this presentation.
     * <p>
     * This method estimates the memory footprint based on the number of slides, shapes, and media
     * elements. The estimation uses predefined constants and provides a rough guide for memory
     * management.
     * </p>
     *
     * @return a {@code MemoryStats} object containing slide, shape, and media counts with an
     *         estimated memory usage in megabytes
     */
    public MemoryStats getMemoryStats() {
        ensureNotClosed();

        int slideCount = getSlideCount();
        int shapeCount = 0;
        int mediaCount = 0;

        // Iterate through all slides to count shapes and media
        for (PotSlide slide : slideCache) {
            try {
                List<PotElement> elements = slide.getElements();
                shapeCount += elements.size();

                // Count audio and video elements as media
                for (PotElement element : elements) {
                    if (element instanceof PotAudio || element instanceof PotVideo) {
                        mediaCount++;
                    }
                }
            } catch (Exception e) {
                PotLogger.warn(PotPresentation.class, "getMemoryStats", "Failed to collect slide memory stats", e);
            }
        }

        // Estimate memory usage in MB using a heuristic formula:
        // base + (slides / divisor) + (shapes / divisor) + (media * per-media)
        long estimatedMemoryMB = PotConstants.MEMORY_BASE_MB
            + (slideCount / PotConstants.MEMORY_SLIDES_DIVISOR)
            + (shapeCount / PotConstants.MEMORY_SHAPES_DIVISOR)
            + ((long) mediaCount * PotConstants.MEMORY_PER_MEDIA_MB);

        return new MemoryStats(slideCount, shapeCount, mediaCount, estimatedMemoryMB,
            "");
    }

    /**
     * Sets a warning listener that triggers when the presentation's estimated memory exceeds a threshold.
     * <p>
     * This method immediately checks the current memory usage and invokes the provided listener if
     * the estimated memory exceeds the specified threshold. The listener receives the current
     * {@code MemoryStats} for further inspection.
     * </p>
     *
     * @param thresholdMB the memory threshold in megabytes; if the estimated memory exceeds this,
     *                    the listener is invoked
     * @param listener    a consumer that accepts {@code MemoryStats} when the threshold is exceeded;
     *                    may be {@code null}
     */
    public void setLargeFileWarning(int thresholdMB, java.util.function.Consumer<MemoryStats> listener) {
        ensureNotClosed();

        MemoryStats stats = getMemoryStats();
        if (stats.exceedsThreshold(thresholdMB) && listener != null) {
            listener.accept(stats);
        }
    }

    // ==================== Page Size Management ====================

    /**
     * Returns the current page width of the presentation in points.
     * <p>
     * One point is 1/72 of an inch. This value corresponds to the width of the slides.
     * </p>
     *
     * @return the page width in points
     */
    public double getPageWidth() {
        ensureNotClosed();
        return slideShow.getPageSize().getWidth();
    }

    /**
     * Returns the current page height of the presentation in points.
     * <p>
     * One point is 1/72 of an inch. This value corresponds to the height of the slides.
     * </p>
     *
     * @return the page height in points
     */
    public double getPageHeight() {
        ensureNotClosed();
        return slideShow.getPageSize().getHeight();
    }

    /**
     * Sets the page size of the presentation using a standard page size enumeration.
     * <p>
     * This method changes the dimensions of all slides in the presentation. If {@code pageSize} is
     * {@code null} or {@link PotPageSize#CUSTOM}, no change is made.
     * </p>
     *
     * @param pageSize the standard page size to apply; may be {@code null}
     */
    public void setPageSize(PotPageSize pageSize) {
        ensureNotClosed();
        if (pageSize != null && pageSize != PotPageSize.CUSTOM) {
            slideShow.setPageSize(new Dimension(
                (int) pageSize.getWidth(),
                (int) pageSize.getHeight()
            ));
        }
    }

    /**
     * Sets a custom page size for the presentation.
     * <p>
     * This method changes the dimensions of all slides in the presentation to the specified width
     * and height in points.
     * </p>
     *
     * @param width  the new page width in points
     * @param height the new page height in points
     */
    public void setPageSize(double width, double height) {
        ensureNotClosed();
        slideShow.setPageSize(new Dimension((int) width, (int) height));
    }

    // ==================== Slide Management ====================

    /**
     * Returns the total number of slides in the presentation.
     *
     * @return the slide count
     */
    public int getSlideCount() {
        ensureNotClosed();
        return slideShow.getSlides().size();
    }

    /**
     * Retrieves the slide at the specified index.
     * <p>
     * Slides are zero-indexed. The returned {@code PotSlide} object provides methods for adding
     * and manipulating content on that slide.
     * </p>
     *
     * @param index the zero-based index of the slide to retrieve
     * @return the {@code PotSlide} at the given index
     * @throws PotException if the index is out of bounds (less than 0 or greater than or equal to
     *                      the slide count)
     */
    public PotSlide getSlide(int index) {
        ensureNotClosed();
        int count = getSlideCount();
        if (index < 0 || index >= count) {
            throw PotException.slideIndexOutOfBounds(index, count);
        }
        return slideCache.get(index);
    }

    /**
     * Returns an unmodifiable list of all slides in the presentation.
     * <p>
     * The list is in the same order as the slides appear in the presentation.
     * </p>
     *
     * @return a list of all {@code PotSlide} objects
     */
    public List<PotSlide> getSlides() {
        ensureNotClosed();
        return Collections.unmodifiableList(slideCache);
    }

    /**
     * Appends a new blank slide to the end of the presentation.
     * <p>
     * The new slide uses the default layout from the presentation's master. It is assigned a
     * unique UUID and registered internally.
     * </p>
     *
     * @return the newly created {@code PotSlide}
     */
    public PotSlide addSlide() {
        ensureNotClosed();
        XSLFSlide xslfSlide = slideShow.createSlide();
        return createAndRegisterSlide(xslfSlide);
    }

    /**
     * Inserts a new blank slide at the specified index.
     * <p>
     * If the index is equal to or greater than the current slide count, the slide is appended to
     * the end. The slide is assigned a unique UUID and registered internally.
     * </p>
     *
     * @param index the zero-based index at which to insert the new slide
     * @return the newly created {@code PotSlide}
     */
    public PotSlide insertSlide(int index) {
        ensureNotClosed();
        PotSlide slide = addSlide();
        if (index >= 0 && index < getSlideCount() - 1) {
            moveSlide(getSlideCount() - 1, index);
        }
        return slide;
    }

    /**
     * Creates a duplicate of an existing slide and appends it to the presentation.
     * <p>
     * This method copies the content and layout of the source slide. The new slide is assigned a
     * unique UUID and registered internally.
     * </p>
     *
     * @param sourceIndex the zero-based index of the slide to clone
     * @return the newly created {@code PotSlide} that is a copy of the source
     * @throws PotException if the source index is out of bounds
     */
    public PotSlide cloneSlide(int sourceIndex) {
        ensureNotClosed();
        int count = getSlideCount();
        if (sourceIndex < 0 || sourceIndex >= count) {
            throw PotException.slideIndexOutOfBounds(sourceIndex, count);
        }

        XSLFSlide sourceSlide = slideShow.getSlides().get(sourceIndex);
        XSLFSlideLayout layout = sourceSlide.getSlideLayout();
        XSLFSlide newSlide = layout != null ? slideShow.createSlide(layout) : slideShow.createSlide();

        try {
            // Use Apache POI's importContent to copy shapes and formatting
            newSlide.importContent(sourceSlide);
        } catch (Exception e) {
            throw PotException.wrap("Failed to clone slide content", e);
        }

        return createAndRegisterSlide(newSlide);
    }

    /**
     * Removes the slide at the specified index from the presentation.
     * <p>
     * The slide is unregistered from the internal registry, and all subsequent slides shift up by
     * one position.
     * </p>
     *
     * @param index the zero-based index of the slide to remove
     * @throws PotException if the index is out of bounds
     */
    public void removeSlide(int index) {
        ensureNotClosed();
        int count = getSlideCount();
        if (index < 0 || index >= count) {
            throw PotException.slideIndexOutOfBounds(index, count);
        }

        // Remove from internal cache and registry
        PotSlide slide = slideCache.remove(index);
        if (slide != null && slide.getUUID() != null) {
            slideRegistry.remove(slide.getUUID());
        }

        // Remove from the underlying POI slide show
        slideShow.removeSlide(index);
    }

    /**
     * Moves a slide from one position to another within the presentation.
     * <p>
     * This method reorders slides. All indices must be valid. If {@code fromIndex} equals
     * {@code toIndex}, no action is taken.
     * </p>
     *
     * @param fromIndex the current zero-based index of the slide to move
     * @param toIndex   the new zero-based index for the slide
     * @throws PotException if either index is out of bounds
     */
    public void moveSlide(int fromIndex, int toIndex) {
        ensureNotClosed();
        int count = getSlideCount();
        if (fromIndex < 0 || fromIndex >= count) {
            throw PotException.slideIndexOutOfBounds(fromIndex, count);
        }
        if (toIndex < 0 || toIndex >= count) {
            throw PotException.slideIndexOutOfBounds(toIndex, count);
        }

        if (fromIndex != toIndex) {
            // Reorder using POI's setSlideOrder
            XSLFSlide slide = slideShow.getSlides().get(fromIndex);
            slideShow.setSlideOrder(slide, toIndex);

            // Refresh internal cache to reflect new order
            refreshSlideCache();
        }
    }

    // ==================== Element and Slide Lookup ====================

    /**
     * Finds a presentation element by its unique identifier.
     * <p>
     * This method searches the internal registry for an element with the given UUID. If no
     * matching element is found, {@code null} is returned.
     * </p>
     *
     * @param uuid the UUID string of the element to find
     * @return the {@code PotElement} with the specified UUID, or {@code null}
     */
    public PotElement findElement(String uuid) {
        ensureNotClosed();
        return elementRegistry.get(uuid);
    }

    /**
     * Finds a slide by its unique identifier.
     * <p>
     * This method searches the internal registry for a slide with the given UUID. If no
     * matching slide is found, {@code null} is returned.
     * </p>
     *
     * @param uuid the UUID string of the slide to find
     * @return the {@code PotSlide} with the specified UUID, or {@code null}
     */
    public PotSlide findSlide(String uuid) {
        ensureNotClosed();
        return slideRegistry.get(uuid);
    }

    // ==================== Master and Layout Access ====================

    /**
     * Returns a list of all slide masters defined in the presentation.
     * <p>
     * Slide masters define the overall design and layout templates for slides. Each master is
     * wrapped in a {@code PotMaster} object.
     * </p>
     *
     * @return a list of {@code PotMaster} objects
     */
    public List<PotMaster> getMasters() {
        ensureNotClosed();
        return slideShow.getSlideMasters().stream()
            .map(master -> new PotMaster(master, this))
            .collect(Collectors.toList());
    }

    /**
     * Returns a list of all slide layouts available across all masters.
     * <p>
     * Slide layouts are templates within a master that define placeholder positions and formatting.
     * Each layout is wrapped in a {@code PotLayout} object.
     * </p>
     *
     * @return a list of {@code PotLayout} objects
     */
    public List<PotLayout> getLayouts() {
        ensureNotClosed();
        List<PotLayout> layouts = new ArrayList<>();
        for (XSLFSlideMaster master : slideShow.getSlideMasters()) {
            for (XSLFSlideLayout layout : master.getSlideLayouts()) {
                layouts.add(new PotLayout(layout, new PotMaster(master, this)));
            }
        }
        return layouts;
    }

    // ==================== Export and Rendering ====================

    /**
     * Exports a JSON representation of the presentation's structure and metadata.
     * <p>
     * The JSON includes slide indices, element UUIDs, and basic properties, suitable for
     * indexing or external processing.
     * </p>
     *
     * @return a JSON string representing the presentation index
     */
    public String exportJsonIndex() {
        ensureNotClosed();
        return JsonExporter.export(this);
    }

    /**
     * Renders a slide as a raster image at its native size.
     * <p>
     * This method draws the specified slide onto a {@code BufferedImage} with a scale factor of 1.0.
     * The image background is filled with white.
     * </p>
     *
     * @param index the zero-based index of the slide to render
     * @return a {@code BufferedImage} containing the rendered slide
     * @throws PotException if the index is out of bounds
     */
    public BufferedImage renderSlide(int index) {
        return renderSlide(index, 1.0);
    }

    /**
     * Renders a slide as a raster image at a specified scale.
     * <p>
     * This method draws the specified slide onto a {@code BufferedImage}, scaling the output by
     * the given factor. The image background is filled with white, and high-quality rendering
     * hints are applied.
     * </p>
     *
     * @param index the zero-based index of the slide to render
     * @param scale the scaling factor (e.g., 2.0 for double size, 0.5 for half size)
     * @return a {@code BufferedImage} containing the rendered slide
     * @throws PotException if the index is out of bounds
     */
    public BufferedImage renderSlide(int index, double scale) {
        ensureNotClosed();
        int count = getSlideCount();
        if (index < 0 || index >= count) {
            throw PotException.slideIndexOutOfBounds(index, count);
        }

        XSLFSlide slide = slideShow.getSlides().get(index);
        Dimension pgSize = slideShow.getPageSize();

        int width = (int) Math.ceil(pgSize.width * scale);
        int height = (int) Math.ceil(pgSize.height * scale);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D graphics = img.createGraphics();
        graphics.setRenderingHint(
            java.awt.RenderingHints.KEY_ANTIALIASING,
            java.awt.RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics.setRenderingHint(
            java.awt.RenderingHints.KEY_RENDERING,
            java.awt.RenderingHints.VALUE_RENDER_QUALITY
        );
        graphics.scale(scale, scale);

        // Fill background with white
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, pgSize.width, pgSize.height);

        // Draw the slide content
        slide.draw(graphics);
        graphics.dispose();

        return img;
    }

    // ==================== Internal Accessors ====================

    /**
     * Provides direct access to the underlying Apache POI {@code XMLSlideShow} object.
     * <p>
     * This method is intended for advanced use cases requiring low-level POI operations.
     * Modifications made directly to the returned object may affect the integrity of this
     * {@code PotPresentation} wrapper.
     * </p>
     *
     * @return the internal {@code XMLSlideShow} instance
     */
    public XMLSlideShow getRawSlideShow() {
        ensureNotClosed();
        return slideShow;
    }

    /**
     * Returns the source file from which this presentation was opened, if any.
     * <p>
     * If the presentation was created from scratch or loaded from a stream, this method returns
     * {@code null}.
     * </p>
     *
     * @return the source {@code File}, or {@code null}
     */
    public File getSourceFile() {
        return sourceFile;
    }

    // ==================== XML Manipulation API ====================

    /**
     * Returns a helper object for low-level XML manipulation of the presentation.
     * <p>
     * The returned {@code PotXml} provides methods to access and modify the underlying XML
     * structures of the presentation, such as slide relationships and shape properties.
     * </p>
     *
     * @return a {@code PotXml} instance bound to this presentation
     */
    public PotXml xml() {
        ensureNotClosed();
        return new PotXml(this);
    }

    // ==================== Internal Registration Methods ====================

    /**
     * Allocates a new unique identifier for a presentation element.
     * <p>
     * This internal method generates a UUID string that is guaranteed to be unique within this
     * presentation instance.
     * </p>
     *
     * @return a new UUID string
     */
    String allocateUUID() {
        ensureNotClosed();
        return uuidManager.allocate();
    }

    /**
     * Registers an element in the internal UUID registry.
     * <p>
     * This internal method associates a {@code PotElement} with its UUID for later retrieval.
     * </p>
     *
     * @param uuid    the UUID of the element
     * @param element the element to register
     */
    void registerElement(String uuid, PotElement element) {
        ensureNotClosed();
        if (uuid != null && element != null) {
            elementRegistry.put(uuid, element);
        }
    }

    /**
     * Removes an element from the internal UUID registry.
     * <p>
     * This internal method deregisters the element with the given UUID, making it no longer
     * findable via {@link #findElement(String)}.
     * </p>
     *
     * @param uuid the UUID of the element to unregister
     */
    void unregisterElement(String uuid) {
        ensureNotClosed();
        if (uuid != null) {
            elementRegistry.remove(uuid);
        }
    }

    /**
     * Adds binary picture data to the presentation's internal store.
     * <p>
     * This internal method delegates to the underlying POI library to store image bytes and
     * returns a handle to the stored data.
     * </p>
     *
     * @param imageData    the raw image bytes
     * @param pictureType  the type of image (e.g., PNG, JPEG)
     * @return a {@code XSLFPictureData} object representing the stored picture
     */
    XSLFPictureData addPictureData(byte[] imageData, PictureData.PictureType pictureType) {
        ensureNotClosed();
        return slideShow.addPicture(imageData, pictureType);
    }

    // ==================== Private Helper Methods ====================

    /**
     * Creates a versioned backup of a file before overwriting it.
     * <p>
     * This helper method is invoked when {@link SaveOptions#isCreateBackup()} is {@code true}.
     * It copies the original file to a backup location with a timestamp suffix, and manages a
     * rolling window of backup versions.
     * </p>
     * <p>
     * The backup file name follows the pattern:
     * {@code originalName_yyyy-MM-dd_HH-mm-ss.ext.bak}. Up to {@code MAX_BACKUP_VERSIONS}
     * (currently 5) backups are retained; older backups are deleted.
     * </p>
     *
     * @param file      the file to back up
     * @param backupDir an optional directory for backups; if {@code null} or empty, the file's
     *                  parent directory is used
     */
    private void createVersionedBackup(File file, String backupDir) {
        final int MAX_BACKUP_VERSIONS = 5;

        try {
            // Determine backup target directory
            File targetDir = (backupDir != null && !backupDir.isEmpty())
                ? new File(backupDir)
                : file.getParentFile();

            if (targetDir != null && !targetDir.exists()) {
                targetDir.mkdirs();
            }

            // Generate timestamped backup name
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            String baseName = file.getName();
            String nameWithoutExt;
            String ext;
            int dotIdx = baseName.lastIndexOf('.');
            if (dotIdx > 0) {
                nameWithoutExt = baseName.substring(0, dotIdx);
                ext = baseName.substring(dotIdx); // includes ".pptx"
            } else {
                nameWithoutExt = baseName;
                ext = "";
            }

            String backupName = nameWithoutExt + "_" + timestamp + ext + ".bak";
            File backupFile = new File(targetDir, backupName);

            // Create the backup copy
            Files.copy(file.toPath(), backupFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Clean up excess backups
            if (targetDir != null) {
                final String prefix = nameWithoutExt + "_";
                final String suffix = ext + ".bak";
                File[] backups = targetDir.listFiles(f ->
                    f.isFile()
                    && f.getName().startsWith(prefix)
                    && f.getName().endsWith(suffix)
                );
                if (backups != null && backups.length > MAX_BACKUP_VERSIONS) {
                    Arrays.sort(backups); // sort by name (which includes timestamp)
                    int toDelete = backups.length - MAX_BACKUP_VERSIONS;
                    for (int i = 0; i < toDelete; i++) {
                        try {
                            backups[i].delete();
                        } catch (Exception ex) {
                            PotLogger.warn(PotPresentation.class, "createVersionedBackup",
                                "Failed to delete old backup: " + backups[i].getName(), ex);
                        }
                    }
                }
            }

        } catch (IOException e) {
            // Log warning but do not fail the save operation
            PotLogger.warn(PotPresentation.class, "createVersionedBackup",
                "Failed to create versioned backup for: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Ensures the presentation has not been closed.
     * <p>
     * This internal method throws a {@code PotException} if {@link #close()} has already been
     * called, preventing operations on a closed presentation.
     * </p>
     *
     * @throws PotException if the presentation is closed
     */
    void ensureNotClosed() {
        if (closed) {
            throw new PotException("Presentation has been closed");
        }
    }

    /**
     * Initializes the internal slide cache from the underlying POI slide list.
     * <p>
     * This method is called during construction to wrap each POI slide in a {@code PotSlide}
     * and register it with a UUID.
     * </p>
     */
    private void initializeCache() {
        for (XSLFSlide xslfSlide : slideShow.getSlides()) {
            // Allocate a new UUID for each slide
            String uuid = uuidManager.allocate();
            PotSlide slide = new PotSlide(xslfSlide, this, uuid);
            slideCache.add(slide);
            slideRegistry.put(uuid, slide);
        }
    }

    /**
     * Creates a {@code PotSlide} wrapper for a new POI slide and registers it.
     * <p>
     * This internal method allocates a UUID, creates the wrapper, and adds it to the cache
     * and registry.
     * </p>
     *
     * @param xslfSlide the newly created POI slide
     * @return the wrapped {@code PotSlide}
     */
    private PotSlide createAndRegisterSlide(XSLFSlide xslfSlide) {
        String uuid = uuidManager.allocate();
        PotSlide slide = new PotSlide(xslfSlide, this, uuid);
        slideCache.add(slide);
        slideRegistry.put(uuid, slide);

        return slide;
    }

    /**
     * Refreshes the internal slide cache to reflect the current order of slides.
     * <p>
     * This method is called after slide reordering operations to ensure the cache matches
     * the underlying POI slide list. Existing slides are preserved; new slides (if any) are
     * wrapped and registered.
     * </p>
     */
    private void refreshSlideCache() {
        slideCache.clear();
        List<XSLFSlide> xslfSlides = slideShow.getSlides();

        for (XSLFSlide xslfSlide : xslfSlides) {
            // Find existing PotSlide for this XSLFSlide
            PotSlide existingSlide = null;
            for (PotSlide slide : slideRegistry.values()) {
                if (slide.getSlide() == xslfSlide) {
                    existingSlide = slide;
                    break;
                }
            }

            if (existingSlide != null) {
                slideCache.add(existingSlide);
            } else {
                // This should not happen under normal circumstances
                String uuid = uuidManager.allocate();
                PotSlide slide = new PotSlide(xslfSlide, this, uuid);
                slideCache.add(slide);
                slideRegistry.put(uuid, slide);
            }
        }
    }

    // ==================== Document Properties API ====================

    /**
     * Sets the title of the presentation.
     * <p>
     * The title is stored in the document's core properties and may be displayed by
     * presentation viewers.
     * </p>
     *
     * @param title the new title (may be {@code null})
     */
    public void setTitle(String title) {
        ensureNotClosed();
        try {
            slideShow.getProperties().getCoreProperties().setTitle(title);
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "setTitle", "Failed to set title", e);
        }
    }

    /**
     * Returns the title of the presentation.
     * <p>
     * If no title has been set, this method returns {@code null}.
     * </p>
     *
     * @return the presentation title, or {@code null}
     */
    public String getTitle() {
        ensureNotClosed();
        try {
            return slideShow.getProperties().getCoreProperties().getTitle();
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "getTitle", "Failed to read title", e);
            return null;
        }
    }

    /**
     * Sets the author of the presentation.
     * <p>
     * The author is stored in the document's core properties and may be displayed by
     * presentation viewers.
     * </p>
     *
     * @param author the new author (may be {@code null})
     */
    public void setAuthor(String author) {
        ensureNotClosed();
        try {
            slideShow.getProperties().getCoreProperties().setCreator(author);
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "setAuthor", "Failed to set author", e);
        }
    }

    /**
     * Returns the author of the presentation.
     * <p>
     * If no author has been set, this method returns {@code null}.
     * </p>
     *
     * @return the presentation author, or {@code null}
     */
    public String getAuthor() {
        ensureNotClosed();
        try {
            return slideShow.getProperties().getCoreProperties().getCreator();
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "getAuthor", "Failed to read author", e);
            return null;
        }
    }

    /**
     * Sets the subject of the presentation.
     * <p>
     * The subject is stored in the document's core properties and may be displayed by
     * presentation viewers.
     * </p>
     *
     * @param subject the new subject (may be {@code null})
     */
    public void setSubject(String subject) {
        ensureNotClosed();
        try {
            slideShow.getProperties().getCoreProperties().setSubjectProperty(subject);
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "setSubject", "Failed to set subject", e);
        }
    }

    /**
     * Returns the subject of the presentation.
     * <p>
     * If no subject has been set, this method returns {@code null}.
     * </p>
     *
     * @return the presentation subject, or {@code null}
     */
    public String getSubject() {
        ensureNotClosed();
        try {
            return slideShow.getProperties().getCoreProperties().getSubject();
        } catch (Exception e) {
            PotLogger.warn(PotPresentation.class, "getSubject", "Failed to read subject", e);
            return null;
        }
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        if (closed) {
            return "PotPresentation{closed}";
        }
        return String.format("PotPresentation{slides=%d, size=%.0fx%.0f}",
            getSlideCount(), getPageWidth(), getPageHeight());
    }
}
