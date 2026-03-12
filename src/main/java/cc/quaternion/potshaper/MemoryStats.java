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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a snapshot of memory usage statistics for a presentation.
 *
 * <p>This class encapsulates counts of presentation components (slides, shapes, media)
 * and calculates an estimated memory footprint. It automatically assigns a warning level
 * based on the estimated memory and slide count, and provides formatted reporting
 * capabilities. Instances are immutable once created.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class MemoryStats {

    // ==================== Instance Fields ====================

    /** The total number of slides in the presentation. */
    private final int slideCount;

    /** The total number of shapes across all slides. */
    private final int shapeCount;

    /** The total number of media elements (images, audio, video) in the presentation. */
    private final int mediaCount;

    /** The estimated memory consumption in megabytes (MB). */
    private final long estimatedMemoryMB;

    /** The date and time when this statistics object was created. */
    private final LocalDateTime timestamp;

    /** The computed warning level based on memory and slide thresholds. */
    private final WarningLevel warningLevel;

    /** Optional detailed description or context for this memory estimate. */
    private final String details;

    // ==================== Constructors ====================

    /**
     * Constructs a MemoryStats instance without detailed description.
     *
     * <p>Creates a memory statistics object with the given component counts and
     * estimated memory. The warning level is calculated automatically, and the
     * timestamp is set to the current time.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(50, 200, 10, 120L);
     * System.out.println(stats.getWarningLevel());
     * }</pre>
     *
     * @param slideCount        the total number of slides, must be non-negative
     * @param shapeCount        the total number of shapes, must be non-negative
     * @param mediaCount        the total number of media elements, must be non-negative
     * @param estimatedMemoryMB the estimated memory usage in megabytes (MB), must be non-negative
     */
    public MemoryStats(int slideCount, int shapeCount, int mediaCount, long estimatedMemoryMB) {
        this(slideCount, shapeCount, mediaCount, estimatedMemoryMB, null);
    }

    /**
     * Constructs a MemoryStats instance with an optional detailed description.
     *
     * <p>Creates a memory statistics object with the given component counts,
     * estimated memory, and an optional descriptive string. The warning level
     * is calculated automatically, and the timestamp is set to the current time.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(50, 200, 10, 120L, "High-resolution images");
     * System.out.println(stats.getDetails());
     * }</pre>
     *
     * @param slideCount        the total number of slides, must be non-negative
     * @param shapeCount        the total number of shapes, must be non-negative
     * @param mediaCount        the total number of media elements, must be non-negative
     * @param estimatedMemoryMB the estimated memory usage in megabytes (MB), must be non-negative
     * @param details           optional descriptive text providing context for the memory estimate,
     *                          may be {@code null}
     */
    public MemoryStats(int slideCount, int shapeCount, int mediaCount, long estimatedMemoryMB, String details) {
        this.slideCount = slideCount;
        this.shapeCount = shapeCount;
        this.mediaCount = mediaCount;
        this.estimatedMemoryMB = estimatedMemoryMB;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.warningLevel = calculateWarningLevel(estimatedMemoryMB, slideCount);
    }

    // ==================== Private Helper ====================

    /**
     * Calculates the warning level based on memory and slide count thresholds.
     *
     * <p>The warning level is determined using the following thresholds:
     * <ul>
     *   <li>CRITICAL: memory exceeds 500 MB</li>
     *   <li>HIGH: memory exceeds 300 MB</li>
     *   <li>MEDIUM: memory exceeds 100 MB OR slide count exceeds 100</li>
     *   <li>LOW: memory exceeds 50 MB</li>
     *   <li>NONE: otherwise</li>
     * </ul></p>
     *
     * @param memoryMB   the estimated memory in megabytes (MB)
     * @param slideCount the total number of slides
     * @return the computed {@link WarningLevel}
     */
    private static WarningLevel calculateWarningLevel(long memoryMB, int slideCount) {
        // Critical if memory exceeds 500 MB
        if (memoryMB > 500) {
            return WarningLevel.CRITICAL;
        }
        // High if memory exceeds 300 MB
        if (memoryMB > 300) {
            return WarningLevel.HIGH;
        }
        // Medium if memory exceeds 100 MB OR slide count exceeds 100
        if (memoryMB > 100 || slideCount > 100) {
            return WarningLevel.MEDIUM;
        }
        // Low if memory exceeds 50 MB
        if (memoryMB > 50) {
            return WarningLevel.LOW;
        }
        // No warning otherwise
        return WarningLevel.NONE;
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the total number of slides.
     *
     * @return the slide count
     */
    public int getSlideCount() {
        return slideCount;
    }

    /**
     * Returns the total number of shapes.
     *
     * @return the shape count
     */
    public int getShapeCount() {
        return shapeCount;
    }

    /**
     * Returns the total number of media elements.
     *
     * @return the media count
     */
    public int getMediaCount() {
        return mediaCount;
    }

    /**
     * Returns the estimated memory consumption in megabytes (MB).
     *
     * @return the estimated memory in MB
     */
    public long getEstimatedMemoryMB() {
        return estimatedMemoryMB;
    }

    /**
     * Returns the timestamp when this statistics object was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the computed warning level.
     *
     * @return the warning level
     */
    public WarningLevel getWarningLevel() {
        return warningLevel;
    }

    /**
     * Returns the optional detailed description.
     *
     * @return the details string, or {@code null} if none was provided
     */
    public String getDetails() {
        return details;
    }

    // ==================== Query Methods ====================

    /**
     * Checks whether the estimated memory exceeds a specified threshold.
     *
     * <p>This method provides a simple way to test if the memory estimate is above
     * a custom limit, independent of the internal warning level thresholds.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(50, 200, 10, 120L);
     * if (stats.exceedsThreshold(100)) {
     *     System.out.println("Memory exceeds 100 MB threshold.");
     * }
     * }</pre>
     *
     * @param thresholdMB the memory threshold in megabytes (MB) to compare against
     * @return {@code true} if the estimated memory is greater than the threshold,
     *         {@code false} otherwise
     */
    public boolean exceedsThreshold(int thresholdMB) {
        return estimatedMemoryMB > thresholdMB;
    }

    /**
     * Determines if any warning level is active.
     *
     * <p>This is a convenience method equivalent to checking if the warning level
     * is not {@link WarningLevel#NONE}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(50, 200, 10, 120L);
     * if (stats.hasWarning()) {
     *     System.out.println("Warning present: " + stats.getWarningMessage());
     * }
     * }</pre>
     *
     * @return {@code true} if the warning level is not NONE, {@code false} otherwise
     */
    public boolean hasWarning() {
        return warningLevel != WarningLevel.NONE;
    }

    /**
     * Determines if the warning level is CRITICAL.
     *
     * <p>This is a convenience method for checking the highest severity warning.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(200, 800, 30, 550L);
     * if (stats.isCritical()) {
     *     System.out.println("Critical memory usage detected!");
     * }
     * }</pre>
     *
     * @return {@code true} if the warning level is CRITICAL, {@code false} otherwise
     */
    public boolean isCritical() {
        return warningLevel == WarningLevel.CRITICAL;
    }

    // ==================== Formatting Methods ====================

    /**
     * Returns a human-readable formatted string for the estimated memory.
     *
     * <p>If the estimated memory is less than 1 MB, the value is converted to
     * kilobytes (KB). Otherwise, the value is shown in megabytes (MB).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(5, 20, 1, 0L);
     * System.out.println(stats.getFormattedMemory()); // Prints "0 KB"
     * }</pre>
     *
     * @return a formatted memory string (e.g., "125 MB" or "512 KB")
     */
    public String getFormattedMemory() {
        if (estimatedMemoryMB < 1) {
            long kb = estimatedMemoryMB * 1024;
            return kb + " KB";
        }
        return estimatedMemoryMB + " MB";
    }

    /**
     * Returns a descriptive warning message corresponding to the current warning level.
     *
     * <p>The message explains the threshold that triggered the warning. If the warning
     * level is NONE, this method returns {@code null}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(50, 200, 10, 120L);
     * String msg = stats.getWarningMessage();
     * if (msg != null) {
     *     System.out.println("Warning: " + msg);
     * }
     * }</pre>
     *
     * @return a warning message string, or {@code null} if no warning is active
     */
    public String getWarningMessage() {
        switch (warningLevel) {
            case CRITICAL:
                return "Estimated memory exceeds 500 MB";
            case HIGH:
                return "Estimated memory exceeds 300 MB";
            case MEDIUM:
                return "Estimated memory exceeds 100 MB or slide count exceeds 100";
            case LOW:
                return "Estimated memory exceeds 50 MB";
            case NONE:
            default:
                return null;
        }
    }

    /**
     * Generates a detailed multi-line report of all statistics.
     *
     * <p>The report includes timestamp, all counts, formatted memory, warning level,
     * warning message (if any), and optional details. The output is formatted for
     * human readability, suitable for logging or display.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * MemoryStats stats = new MemoryStats(50, 200, 10, 120L, "High-resolution images");
     * System.out.println(stats.getDetailedReport());
     * }</pre>
     *
     * @return a formatted detailed report string
     */
    public String getDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Memory Statistics Report ===\n");
        sb.append("Timestamp: ").append(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Slide Count: ").append(slideCount).append("\n");
        sb.append("Shape Count: ").append(shapeCount).append("\n");
        sb.append("Media Count: ").append(mediaCount).append("\n");
        sb.append("Estimated Memory: ").append(getFormattedMemory()).append("\n");
        sb.append("Warning Level: ").append(warningLevel.getDisplayName()).append("\n");

        String warning = getWarningMessage();
        if (warning != null) {
            sb.append("Warning: ").append(warning).append("\n");
        }

        if (details != null) {
            sb.append("Details: ").append(details).append("\n");
        }

        return sb.toString();
    }

    // ==================== Object Overrides ====================

    @Override
    public String toString() {
        return "MemoryStats{" +
                "slides=" + slideCount +
                ", shapes=" + shapeCount +
                ", media=" + mediaCount +
                ", memory=" + estimatedMemoryMB + "MB" +
                ", warning=" + warningLevel +
                '}';
    }

    // ==================== Nested Enum ====================

    /**
     * Represents the severity level of a memory usage warning.
     *
     * <p>This enum defines a hierarchy of warning levels based on estimated memory consumption
     * and slide count thresholds. Each level has a corresponding display name for user-facing output.</p>
     *
     * @since 1.0
     */
    public enum WarningLevel {
        /** No warning; memory usage is within safe limits. */
        NONE("None"),
        /** Low severity warning; memory usage exceeds 50MB. */
        LOW("Low"),
        /** Medium severity warning; memory usage exceeds 100MB or slide count exceeds 100. */
        MEDIUM("Medium"),
        /** High severity warning; memory usage exceeds 300MB. */
        HIGH("High"),
        /** Critical severity warning; memory usage exceeds 500MB. */
        CRITICAL("Critical");

        private final String displayName;

        WarningLevel(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Returns the user-friendly display name for this warning level.
         *
         * @return the display name of this warning level
         */
        public String getDisplayName() {
            return displayName;
        }
    }
}
