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
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Provides a centralized logging facility for the PotShaper project.
 * <p>
 * This utility class configures a {@link java.util.logging.Logger} with a custom compact formatter
 * and offers static convenience methods for logging at various severity levels (error, warning, info, debug).
 * It initializes a root logger that suppresses parent handlers and uses a console handler with a custom
 * {@link CompactFormatter} that outputs timestamps, severity levels, simplified class names, and truncated
 * stack traces for exceptions. The logger is lazily initialized upon first use.
 * </p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class PotLogger {

    /** The root logger instance for the PotShaper package. */
    private static final Logger ROOT_LOGGER = Logger.getLogger("cc.quaternion.potshaper");

    /** Flag indicating whether the logger has been initialized. */
    private static boolean initialized = false;

    /** Formatter for timestamps in log output. */
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    static {
        initializeLogger();
    }

    /**
     * Initializes the root logger configuration.
     * <p>
     * This method sets up the root logger to use a custom {@link CompactFormatter} on a {@link ConsoleHandler}.
     * It configures the logger to not propagate messages to parent handlers and sets the default level to
     * {@link Level#WARNING}. The initialization is performed only once; subsequent calls have no effect.
     * </p>
     */
    private static void initializeLogger() {
        if (initialized) {
            return;
        }

        // Set default level to WARNING
        ROOT_LOGGER.setLevel(Level.WARNING);
        ROOT_LOGGER.setUseParentHandlers(false);

        // Configure console handler with custom formatter
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new CompactFormatter());
        ROOT_LOGGER.addHandler(consoleHandler);

        initialized = true;
    }

    /**
     * Retrieves a logger instance for the specified class.
     * <p>
     * This method returns a {@link Logger} associated with the fully qualified name of the given class.
     * The returned logger inherits the configuration (handlers, level) from the root PotShaper logger.
     * </p>
     *
     * @param clazz the class for which to obtain a logger.
     * @return a logger instance configured for the specified class.
     */
    static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * Logs an error message with an optional associated throwable.
     * <p>
     * This method logs a message at the {@link Level#SEVERE} level. If a throwable is provided,
     * it is attached to the log record. The log message is prefixed with the originating method name.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * try {
     *     riskyOperation();
     * } catch (IOException e) {
     *     PotLogger.error(MyClass.class, "processFile", "Failed to read input file", e);
     * }
     * }</pre>
     *
     * @param clazz   the class where the error occurred.
     * @param method  the name of the method where the error occurred.
     * @param message the descriptive error message.
     * @param e       the throwable associated with the error, may be {@code null}.
     */
    static void error(Class<?> clazz, String method, String message, Throwable e) {
        Logger logger = getLogger(clazz);
        if (e != null) {
            logger.log(Level.SEVERE, formatMessage(method, message), e);
        } else {
            logger.severe(formatMessage(method, message));
        }
    }

    /**
     * Logs a warning message.
     * <p>
     * This method logs a message at the {@link Level#WARNING} level. The log message is prefixed
     * with the originating method name.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * if (deprecatedParameter != null) {
     *     PotLogger.warn(MyClass.class, "calculate", "Parameter 'deprecatedParameter' is deprecated.");
     * }
     * }</pre>
     *
     * @param clazz   the class where the warning is issued.
     * @param method  the name of the method where the warning is issued.
     * @param message the descriptive warning message.
     */
    static void warn(Class<?> clazz, String method, String message) {
        Logger logger = getLogger(clazz);
        logger.warning(formatMessage(method, message));
    }

    /**
     * Logs a warning message with an optional associated throwable.
     * <p>
     * This method logs a message at the {@link Level#WARNING} level. If a throwable is provided,
     * it is attached to the log record. The log message is prefixed with the originating method name.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * try {
     *     fallbackOperation();
     * } catch (RecoverableException e) {
     *     PotLogger.warn(MyClass.class, "fallback", "Fallback operation encountered a minor issue", e);
     * }
     * }</pre>
     *
     * @param clazz   the class where the warning is issued.
     * @param method  the name of the method where the warning is issued.
     * @param message the descriptive warning message.
     * @param e       the throwable associated with the warning, may be {@code null}.
     */
    static void warn(Class<?> clazz, String method, String message, Throwable e) {
        Logger logger = getLogger(clazz);
        if (e != null) {
            logger.log(Level.WARNING, formatMessage(method, message), e);
        } else {
            logger.warning(formatMessage(method, message));
        }
    }

    /**
     * Logs an informational message.
     * <p>
     * This method logs a message at the {@link Level#INFO} level. The log message is prefixed
     * with the originating method name.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotLogger.info(MyClass.class, "startup", "Application initialization completed successfully.");
     * }</pre>
     *
     * @param clazz   the class where the information is logged.
     * @param method  the name of the method where the information is logged.
     * @param message the descriptive informational message.
     */
    static void info(Class<?> clazz, String method, String message) {
        Logger logger = getLogger(clazz);
        logger.info(formatMessage(method, message));
    }

    /**
     * Logs a debug message.
     * <p>
     * This method logs a message at the {@link Level#FINE} level. The log message is prefixed
     * with the originating method name. Debug messages are only visible if the logger level is set to
     * {@link Level#FINE} or lower (e.g., via {@link #enableDebug()}).
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotLogger.debug(MyClass.class, "calculate", "Intermediate value computed: " + value);
     * }</pre>
     *
     * @param clazz   the class where the debug information is logged.
     * @param method  the name of the method where the debug information is logged.
     * @param message the descriptive debug message.
     */
    static void debug(Class<?> clazz, String method, String message) {
        Logger logger = getLogger(clazz);
        logger.fine(formatMessage(method, message));
    }

    /**
     * Formats a log message by optionally prepending the method name.
     * <p>
     * If the provided method name is not null and not empty, the returned string is formatted as
     * "[{@code method}] {@code message}". Otherwise, the original message is returned unchanged.
     * </p>
     *
     * @param method  the name of the originating method, may be {@code null} or empty.
     * @param message the base log message.
     * @return the formatted log message.
     */
    private static String formatMessage(String method, String message) {
        if (method != null && !method.isEmpty()) {
            return String.format("[%s] %s", method, message);
        }
        return message;
    }

    /**
     * Sets the logging level for the root PotShaper logger.
     * <p>
     * This method changes the severity threshold for the root logger. Only log records with a level
     * equal to or higher than the specified level will be published.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * // Enable INFO and higher levels
     * PotLogger.setLevel(Level.INFO);
     * }</pre>
     *
     * @param level the new logging level.
     * @see java.util.logging.Level
     */
    static void setLevel(Level level) {
        ROOT_LOGGER.setLevel(level);
    }

    /**
     * Enables debug-level logging for the root PotShaper logger.
     * <p>
     * This is a convenience method that sets the root logger level to {@link Level#ALL},
     * allowing all log messages (including {@link Level#FINE} debug messages) to be published.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotLogger.enableDebug();
     * }</pre>
     */
    static void enableDebug() {
        ROOT_LOGGER.setLevel(Level.ALL);
    }

    /**
     * Disables all logging output from the root PotShaper logger.
     * <p>
     * This is a convenience method that sets the root logger level to {@link Level#OFF},
     * suppressing all log messages.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotLogger.disableLogging();
     * }</pre>
     */
    static void disableLogging() {
        ROOT_LOGGER.setLevel(Level.OFF);
    }

    /**
     * A custom log formatter that produces compact, readable output.
     * <p>
     * This formatter generates a single line per log record containing a timestamp, severity level,
     * simplified class name, and the log message. If the log record contains a throwable, a truncated
     * stack trace (up to three frames) is appended.
     * </p>
     */
    private static class CompactFormatter extends Formatter {
        /**
         * Formats a log record into a compact string representation.
         * <p>
         * The output format is: {@code yyyy-MM-dd HH:mm:ss.SSS [LEVEL] ShortClassName - message}.
         * If a throwable is present, its simple class name, message, and up to three stack trace
         * elements are appended on subsequent lines.
         * </p>
         *
         * @param record the log record to be formatted.
         * @return the formatted log string.
         */
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            // Timestamp
            sb.append(DATETIME_FORMATTER.format(
                LocalDateTime.now()));
            sb.append(" ");

            // Severity level
            Level level = record.getLevel();
            if (level == Level.SEVERE) {
                sb.append("[ERROR] ");
            } else if (level == Level.WARNING) {
                sb.append("[WARN ] ");
            } else if (level == Level.INFO) {
                sb.append("[INFO ] ");
            } else {
                sb.append("[DEBUG] ");
            }

            // Simplified class name
            String className = record.getSourceClassName();
            if (className != null) {
                int lastDot = className.lastIndexOf('.');
                if (lastDot >= 0) {
                    className = className.substring(lastDot + 1);
                }
                sb.append(className);
                sb.append(" - ");
            }

            // Log message
            sb.append(formatMessage(record));
            sb.append(System.lineSeparator());

            // Truncated stack trace for throwables
            if (record.getThrown() != null) {
                Throwable t = record.getThrown();
                sb.append("   ");
                sb.append(t.getClass().getSimpleName());
                sb.append(": ");
                sb.append(t.getMessage());
                sb.append(System.lineSeparator());

                // Include up to 3 stack trace elements
                StackTraceElement[] traces = t.getStackTrace();
                int maxTraces = Math.min(3, traces.length);
                for (int i = 0; i < maxTraces; i++) {
                    sb.append("    at ");
                    sb.append(traces[i].toString());
                    sb.append(System.lineSeparator());
                }
                if (traces.length > maxTraces) {
                    sb.append("    ... ");
                    sb.append(traces.length - maxTraces);
                    sb.append(" more");
                    sb.append(System.lineSeparator());
                }
            }

            return sb.toString();
        }
    }
}