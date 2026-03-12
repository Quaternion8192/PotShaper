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
 * Configures options for saving and processing presentation files.
 *
 * <p>This class provides a fluent builder pattern to define various processing
 * options such as web optimization, media compression, macro removal, metadata
 * cleaning, validation, and backup creation. It includes factory methods for
 * common configuration presets and chainable setters for custom configuration.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class SaveOptions {

    // ==================== Web Optimization ====================

    /**
     * Indicates whether the output should be optimized for web delivery.
     */
    private boolean optimizeForWeb = false;

    /**
     * The buffer size in bytes used during processing operations.
     */
    private int bufferSize = 8192;

    // ==================== Media and Content ====================

    /**
     * Indicates whether embedded media should be compressed.
     */
    private boolean compressMedia = false;

    /**
     * Indicates whether macros should be removed from the presentation.
     */
    private boolean removeMacros = false;

    /**
     * Indicates whether personal and sensitive metadata should be cleaned.
     */
    private boolean cleanMetadata = false;

    // ==================== Safety and Validation ====================

    /**
     * Indicates whether the presentation structure should be validated before saving.
     */
    private boolean validate = false;

    /**
     * Indicates whether a backup copy of the original file should be created.
     */
    private boolean createBackup = false;

    /**
     * The custom file system path where the backup should be saved.
     */
    private String backupPath;

    // ==================== Constructors ====================

    /**
     * Constructs a new {@code SaveOptions} instance with default settings.
     *
     * <p>Default settings are: web optimization disabled, buffer size of 8192 bytes,
     * media compression disabled, macro removal disabled, metadata cleaning disabled,
     * validation disabled, backup creation disabled, and no custom backup path.</p>
     */
    public SaveOptions() {
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a {@code SaveOptions} instance pre-configured for web delivery.
     *
     * <p>This preset enables web optimization and sets a larger buffer size (65536 bytes)
     * suitable for streaming or web server output.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * SaveOptions options = SaveOptions.forWeb();
     * }</pre>
     *
     * @return a new {@code SaveOptions} instance configured for web optimization
     */
    public static SaveOptions forWeb() {
        SaveOptions options = new SaveOptions();
        options.optimizeForWeb = true;
        options.bufferSize = 65536; // 64KB
        return options;
    }

    /**
     * Creates a {@code SaveOptions} instance pre-configured for high compression.
     *
     * <p>This preset enables media compression and metadata cleaning to minimize file size.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * SaveOptions options = SaveOptions.highCompression();
     * }</pre>
     *
     * @return a new {@code SaveOptions} instance configured for high compression
     */
    public static SaveOptions highCompression() {
        SaveOptions options = new SaveOptions();
        options.compressMedia = true;
        options.cleanMetadata = true;
        return options;
    }

    /**
     * Creates a {@code SaveOptions} instance pre-configured for safe operations.
     *
     * <p>This preset enables validation before saving and automatic backup creation.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * SaveOptions options = SaveOptions.safe();
     * }</pre>
     *
     * @return a new {@code SaveOptions} instance configured for safe operations
     */
    public static SaveOptions safe() {
        SaveOptions options = new SaveOptions();
        options.validate = true;
        options.createBackup = true;
        return options;
    }

    // ==================== Web Optimization Setters ====================

    /**
     * Enables or disables optimization for web delivery.
     *
     * <p>When enabled, the processor may apply transformations such as resizing images,
     * using web-friendly formats, or adjusting streaming characteristics.</p>
     *
     * @param optimize {@code true} to enable web optimization, {@code false} to disable
     * @return this {@code SaveOptions} instance for method chaining
     */
    public SaveOptions optimizeForWeb(boolean optimize) {
        this.optimizeForWeb = optimize;
        return this;
    }

    /**
     * Sets the buffer size in bytes used during processing operations.
     *
     * <p>The buffer size must be a positive integer. Larger buffers may improve
     * performance for large files at the cost of higher memory usage.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * SaveOptions options = new SaveOptions().bufferSize(16384);
     * }</pre>
     *
     * @param size the buffer size in bytes, must be positive
     * @return this {@code SaveOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code size} is not positive
     * @see ValidationUtils#positive(int, String)
     */
    public SaveOptions bufferSize(int size) {
        this.bufferSize = ValidationUtils.positive(size, "bufferSize");
        return this;
    }

    // ==================== Media and Content Setters ====================

    /**
     * Enables or disables compression of embedded media.
     *
     * <p>When enabled, images, audio, and video within the presentation may be
     * re-encoded or compressed to reduce file size, potentially affecting quality.</p>
     *
     * @param compress {@code true} to enable media compression, {@code false} to disable
     * @return this {@code SaveOptions} instance for method chaining
     */
    public SaveOptions compressMedia(boolean compress) {
        this.compressMedia = compress;
        return this;
    }

    // ==================== Security and Privacy Setters ====================

    /**
     * Enables or disables removal of macros from the presentation.
     *
     * <p>When enabled, any embedded macros (e.g., VBA scripts) are stripped from the file,
     * which can enhance security but may break functionality.</p>
     *
     * @param remove {@code true} to remove macros, {@code false} to preserve them
     * @return this {@code SaveOptions} instance for method chaining
     */
    public SaveOptions removeMacros(boolean remove) {
        this.removeMacros = remove;
        return this;
    }

    /**
     * Enables or disables cleaning of personal and sensitive metadata.
     *
     * <p>When enabled, metadata fields such as author name, creation date, revision history,
     * and comments may be removed or anonymized.</p>
     *
     * @param clean {@code true} to clean metadata, {@code false} to preserve it
     * @return this {@code SaveOptions} instance for method chaining
     */
    public SaveOptions cleanMetadata(boolean clean) {
        this.cleanMetadata = clean;
        return this;
    }

    // ==================== Safety and Validation Setters ====================

    /**
     * Enables or disables validation of the presentation structure before saving.
     *
     * <p>When enabled, the processor checks for structural integrity and may throw an
     * exception if the presentation is malformed, preventing a corrupt output file.</p>
     *
     * @param validate {@code true} to enable validation, {@code false} to disable
     * @return this {@code SaveOptions} instance for method chaining
     */
    public SaveOptions validate(boolean validate) {
        this.validate = validate;
        return this;
    }

    /**
     * Enables or disables creation of a backup copy of the original file.
     *
     * <p>When enabled, the original file is copied to a backup location before processing.
     * If {@link #backupPath(String)} is not set, a default backup location is used.</p>
     *
     * @param backup {@code true} to enable backup creation, {@code false} to disable
     * @return this {@code SaveOptions} instance for method chaining
     */
    public SaveOptions createBackup(boolean backup) {
        this.createBackup = backup;
        return this;
    }

    /**
     * Sets a custom file system path for the backup file.
     *
     * <p>The path must be a non-blank string. If set, backups will be saved to this
     * location when {@link #createBackup} is enabled. If not set, a default location
     * is used.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * SaveOptions options = new SaveOptions()
     *     .createBackup(true)
     *     .backupPath("/backups/presentation_backup.pptx");
     * }</pre>
     *
     * @param path the custom backup file path, must not be blank
     * @return this {@code SaveOptions} instance for method chaining
     * @throws IllegalArgumentException if {@code path} is blank
     * @see ValidationUtils#notBlank(String, String)
     */
    public SaveOptions backupPath(String path) {
        this.backupPath = ValidationUtils.notBlank(path, "backupPath");
        return this;
    }

    // ==================== Getters ====================

    /**
     * Returns whether web optimization is enabled.
     *
     * @return {@code true} if web optimization is enabled, {@code false} otherwise
     */
    public boolean isOptimizeForWeb() {
        return optimizeForWeb;
    }

    /**
     * Returns the buffer size in bytes used during processing.
     *
     * @return the buffer size in bytes
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Returns whether media compression is enabled.
     *
     * @return {@code true} if media compression is enabled, {@code false} otherwise
     */
    public boolean isCompressMedia() {
        return compressMedia;
    }

    /**
     * Returns whether macro removal is enabled.
     *
     * @return {@code true} if macro removal is enabled, {@code false} otherwise
     */
    public boolean isRemoveMacros() {
        return removeMacros;
    }

    /**
     * Returns whether metadata cleaning is enabled.
     *
     * @return {@code true} if metadata cleaning is enabled, {@code false} otherwise
     */
    public boolean isCleanMetadata() {
        return cleanMetadata;
    }

    /**
     * Returns whether validation is enabled.
     *
     * @return {@code true} if validation is enabled, {@code false} otherwise
     */
    public boolean isValidate() {
        return validate;
    }

    /**
     * Returns whether backup creation is enabled.
     *
     * @return {@code true} if backup creation is enabled, {@code false} otherwise
     */
    public boolean isCreateBackup() {
        return createBackup;
    }

    /**
     * Returns the custom backup file path, if set.
     *
     * @return the custom backup path, or {@code null} if not set
     */
    public String getBackupPath() {
        return backupPath;
    }

    /**
     * Checks whether a custom backup path has been set and is non-empty.
     *
     * @return {@code true} if a non-null, non-empty backup path is set, {@code false} otherwise
     */
    public boolean hasBackupPath() {
        return backupPath != null && !backupPath.isEmpty();
    }

    // ==================== Object Methods ====================

    /**
     * Returns a string representation of this {@code SaveOptions} instance.
     *
     * <p>The string includes the current state of all configuration flags and the buffer size,
     * formatted for debugging purposes.</p>
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "SaveOptions{" +
                "optimizeForWeb=" + optimizeForWeb +
                ", bufferSize=" + bufferSize +
                ", compressMedia=" + compressMedia +
                ", removeMacros=" + removeMacros +
                ", cleanMetadata=" + cleanMetadata +
                ", validate=" + validate +
                ", createBackup=" + createBackup +
                '}';
    }
}