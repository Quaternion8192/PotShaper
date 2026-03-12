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

import java.util.HashMap;
import java.util.Map;

/**
 * The base runtime exception for the PotShaper library.
 *
 * <p>This exception provides structured error handling with error codes and contextual information.
 * It is used throughout the library to indicate failures in operations such as XML processing,
 * shape manipulation, I/O, and parameter validation. The exception supports chaining of causes,
 * custom error codes from the {@link ErrorCode} enumeration, and a mutable context map for
 * attaching diagnostic data.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * try {
 *     PotShaper.loadPresentation("presentation.pot");
 * } catch (PotException e) {
 *     System.err.println("Error: " + e.getErrorCode());
 *     e.getContext().forEach((k, v) -> System.err.println(k + " = " + v));
 * }
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** The error code categorizing this exception. */
    private final ErrorCode errorCode;

    /** A map of contextual key-value pairs providing additional diagnostic information. */
    private final Map<String, Object> context;

    /**
     * Constructs a new exception with a detail message.
     *
     * <p>The error code is set to {@link ErrorCode#GENERAL_ERROR} and the context is initialized empty.</p>
     *
     * @param message the detail message explaining the error
     */
    public PotException(String message) {
        super(message);
        this.errorCode = ErrorCode.GENERAL_ERROR;
        this.context = new HashMap<>();
    }

    /**
     * Constructs a new exception with a detail message and a cause.
     *
     * <p>The error code is set to {@link ErrorCode#GENERAL_ERROR} and the context is initialized empty.</p>
     *
     * @param message the detail message explaining the error
     * @param cause   the underlying cause of this exception
     */
    public PotException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.GENERAL_ERROR;
        this.context = new HashMap<>();
    }

    /**
     * Constructs a new exception with a cause.
     *
     * <p>The detail message is derived from the cause, the error code is set to
     * {@link ErrorCode#GENERAL_ERROR}, and the context is initialized empty.</p>
     *
     * @param cause the underlying cause of this exception
     */
    public PotException(Throwable cause) {
        super(cause);
        this.errorCode = ErrorCode.GENERAL_ERROR;
        this.context = new HashMap<>();
    }

    /**
     * Constructs a new exception with a detail message and a specific error code.
     *
     * <p>The context is initialized empty.</p>
     *
     * @param message   the detail message explaining the error
     * @param errorCode the error code categorizing this exception
     */
    public PotException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.context = new HashMap<>();
    }

    /**
     * Constructs a new exception with a detail message, a cause, and a specific error code.
     *
     * <p>The context is initialized empty.</p>
     *
     * @param message   the detail message explaining the error
     * @param cause     the underlying cause of this exception
     * @param errorCode the error code categorizing this exception
     */
    public PotException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = new HashMap<>();
    }

    /**
     * Adds a key-value pair to the context of this exception.
     *
     * <p>The context is a mutable map that can store additional diagnostic information.
     * This method returns {@code this} to allow for method chaining.</p>
     *
     * @param key   the key for the context entry
     * @param value the value for the context entry
     * @return this exception instance for chaining
     */
    public PotException withContext(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Returns a copy of the context map.
     *
     * <p>The returned map is a snapshot to prevent external modification of the internal state.</p>
     *
     * @return a new map containing all context key-value pairs
     */
    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }

    /**
     * Retrieves a value from the context by its key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if no mapping exists
     */
    public Object getContextValue(String key) {
        return context.get(key);
    }

    /**
     * Wraps a throwable into a PotException, preserving the original if it is already a PotException.
     *
     * <p>If the provided cause is already an instance of {@code PotException}, it is returned directly.
     * Otherwise, a new {@code PotException} is created with the given message and cause, using the
     * {@link ErrorCode#GENERAL_ERROR} code.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * try {
     *     parseXml(input);
     * } catch (Exception e) {
     *     throw PotException.wrap("Failed to parse XML", e);
     * }
     * }</pre>
     *
     * @param message the detail message for the new exception
     * @param cause   the throwable to wrap
     * @return a PotException, either the original cause or a new wrapper
     */
    public static PotException wrap(String message, Throwable cause) {
        if (cause instanceof PotException) {
            return (PotException) cause;
        }
        return new PotException(message, cause);
    }

    /**
     * Wraps a throwable into a PotException with a specific error code, preserving the original if it is already a PotException.
     *
     * <p>If the provided cause is already an instance of {@code PotException}, it is returned directly.
     * Otherwise, a new {@code PotException} is created with the given message, cause, and error code.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * try {
     *     loadFile(path);
     * } catch (IOException e) {
     *     throw PotException.wrap("Cannot load file", e, ErrorCode.IO_ERROR);
     * }
     * }</pre>
     *
     * @param message   the detail message for the new exception
     * @param cause     the throwable to wrap
     * @param errorCode the error code for the new exception
     * @return a PotException, either the original cause or a new wrapper
     */
    public static PotException wrap(String message, Throwable cause, ErrorCode errorCode) {
        if (cause instanceof PotException) {
            return (PotException) cause;
        }
        return new PotException(message, cause, errorCode);
    }

    /**
     * Enumeration of error codes used to categorize PotException instances.
     *
     * <p>Each code has a machine-readable string and a human-readable description.
     * The {@link #toString()} method returns a combined representation.</p>
     */
    public enum ErrorCode {
        /** A general, uncategorized error. */
        GENERAL_ERROR("GENERAL_ERROR", "General error"),

        /** Failed to parse XML content. */
        XML_PARSE_ERROR("XML_PARSE_ERROR", "Failed to parse XML"),
        /** Failed to manipulate XML structure. */
        XML_MANIPULATION_ERROR("XML_MANIPULATION_ERROR", "Failed to manipulate XML"),
        /** XML validation against a schema or rules failed. */
        XML_VALIDATION_ERROR("XML_VALIDATION_ERROR", "XML validation failed"),

        /** A requested shape or element was not found. */
        SHAPE_NOT_FOUND("SHAPE_NOT_FOUND", "Shape not found"),
        /** An operation on a shape failed. */
        SHAPE_OPERATION_ERROR("SHAPE_OPERATION_ERROR", "Shape operation failed"),

        /** A provided parameter value is invalid. */
        INVALID_PARAMETER("INVALID_PARAMETER", "Invalid parameter"),
        /** A required parameter is null. */
        NULL_PARAMETER("NULL_PARAMETER", "Parameter cannot be null"),

        /** The requested operation is not supported. */
        UNSUPPORTED_OPERATION("UNSUPPORTED_OPERATION", "Operation not supported"),
        /** An operation failed for reasons not covered by other codes. */
        OPERATION_FAILED("OPERATION_FAILED", "Operation failed"),

        /** An I/O operation failed. */
        IO_ERROR("IO_ERROR", "I/O operation failed"),
        /** A file was not found at the specified path. */
        FILE_NOT_FOUND("FILE_NOT_FOUND", "File not found"),

        /** An animation-related operation failed. */
        ANIMATION_ERROR("ANIMATION_ERROR", "Animation operation failed"),

        /** A media-related operation failed. */
        MEDIA_ERROR("MEDIA_ERROR", "Media operation failed"),

        /** An effect-related operation failed. */
        EFFECT_ERROR("EFFECT_ERROR", "Effect operation failed"),

        /** An action-related operation failed. */
        ACTION_ERROR("ACTION_ERROR", "Action operation failed");

        private final String code;
        private final String description;

        ErrorCode(String code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * Returns the machine-readable error code.
         *
         * @return the error code string
         */
        public String getCode() {
            return code;
        }

        /**
         * Returns the human-readable description of the error.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns a combined string representation of the code and description.
         *
         * @return a string in the format "code: description"
         */
        @Override
        public String toString() {
            return code + ": " + description;
        }
    }

    // ==================== Factory Methods ====================

    /**
     * Creates a PotException for an I/O error during a specified operation.
     *
     * <p>The exception uses the {@link ErrorCode#IO_ERROR} code and includes the operation name in the message.</p>
     *
     * @param operation a description of the I/O operation that failed
     * @param cause     the underlying I/O exception
     * @return a new PotException configured for an I/O error
     */
    public static PotException ioError(String operation, Throwable cause) {
        return new PotException("IO error during " + operation, cause, ErrorCode.IO_ERROR);
    }

    /**
     * Creates a PotException for an XML manipulation error during a specified operation.
     *
     * <p>The exception uses the {@link ErrorCode#XML_MANIPULATION_ERROR} code and includes the operation name in the message.</p>
     *
     * @param operation a description of the XML operation that failed
     * @param cause     the underlying XML processing exception
     * @return a new PotException configured for an XML manipulation error
     */
    public static PotException xmlError(String operation, Throwable cause) {
        return new PotException("XML error during " + operation, cause, ErrorCode.XML_MANIPULATION_ERROR);
    }

    /**
     * Creates a PotException for a file not found error.
     *
     * <p>The exception uses the {@link ErrorCode#FILE_NOT_FOUND} code and includes the file path in the message.</p>
     *
     * @param path the path of the file that was not found
     * @return a new PotException configured for a file not found error
     */
    public static PotException fileNotFound(String path) {
        return new PotException("File not found: " + path, ErrorCode.FILE_NOT_FOUND);
    }

    /**
     * Creates a PotException for an element not found by its UUID.
     *
     * <p>The exception uses the {@link ErrorCode#SHAPE_NOT_FOUND} code and includes the UUID in the message.</p>
     *
     * @param uuid the UUID of the element that was not found
     * @return a new PotException configured for an element not found error
     */
    public static PotException elementNotFound(String uuid) {
        return new PotException("Element not found with UUID: " + uuid, ErrorCode.SHAPE_NOT_FOUND);
    }

    /**
     * Creates a PotException for an invalid parameter.
     *
     * <p>The exception uses the {@link ErrorCode#INVALID_PARAMETER} code and formats a message with the parameter name and details.</p>
     *
     * @param paramName the name of the invalid parameter
     * @param message   a detailed explanation of why the parameter is invalid
     * @return a new PotException configured for an invalid parameter error
     */
    public static PotException invalidParameter(String paramName, String message) {
        return new PotException(String.format("Invalid parameter '%s': %s", paramName, message),
            ErrorCode.INVALID_PARAMETER);
    }

    /**
     * Creates a PotException for an unsupported operation.
     *
     * <p>The exception uses the {@link ErrorCode#UNSUPPORTED_OPERATION} code and includes the operation name in the message.</p>
     *
     * @param operation the name or description of the unsupported operation
     * @return a new PotException configured for an unsupported operation error
     */
    public static PotException unsupportedOperation(String operation) {
        return new PotException("Unsupported operation: " + operation, ErrorCode.UNSUPPORTED_OPERATION);
    }

    /**
     * Creates a PotException for a slide index that is out of bounds.
     *
     * <p>The exception uses the {@link ErrorCode#INVALID_PARAMETER} code, formats a descriptive message,
     * and adds the index and slide count to the context for diagnostics.</p>
     *
     * @param index      the requested slide index
     * @param slideCount the total number of slides
     * @return a new PotException configured for an out-of-bounds slide index error
     */
    public static PotException slideIndexOutOfBounds(int index, int slideCount) {
        return new PotException(String.format(
            "Slide index out of bounds: %d (slide count: %d)", index, slideCount),
            ErrorCode.INVALID_PARAMETER)
            .withContext("index", index)
            .withContext("slideCount", slideCount);
    }
}