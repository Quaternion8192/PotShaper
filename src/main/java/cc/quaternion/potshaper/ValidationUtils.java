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

import java.util.Collection;

/**
 * Provides static utility methods for validating method parameters and state.
 * <p>
 * This class contains a collection of common validation checks that throw a {@link PotException}
 * with a descriptive message when a validation fails. Each method returns the validated value
 * if it passes, allowing for inline usage in assignments and method calls. All methods are
 * designed to be used for precondition checking, typically at the beginning of a method.
 * </p>
 * <h3>Usage Example</h3>
 * <pre>{@code
 * public void processData(String id, byte[] payload, int count) {
 *     this.id = ValidationUtils.notBlank(id, "id");
 *     this.payload = ValidationUtils.notEmpty(payload, "payload");
 *     this.count = ValidationUtils.positive(count, "count");
 *     // Proceed with processing...
 * }
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public final class ValidationUtils {

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This utility class is not designed to be instantiated.
     * </p>
     */
    private ValidationUtils() {
    }

    /**
     * Validates that an object reference is not {@code null}.
     * <p>
     * If the provided value is {@code null}, a {@link PotException} is thrown indicating
     * the parameter name and that it cannot be null.
     * </p>
     *
     * @param <T>       the type of the object to validate
     * @param value     the object reference to check for null
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated non-null {@code value}
     * @throws PotException if {@code value} is {@code null}
     * @since 1.0
     */
    public static <T> T notNull(T value, String paramName) {
        if (value == null) {
            throw PotException.invalidParameter(paramName, "cannot be null");
        }
        return value;
    }

    /**
     * Validates that a string is not {@code null}, empty, or consists only of whitespace.
     * <p>
     * The check trims the string and verifies the result is not empty. If the value is
     * {@code null} or blank after trimming, a {@link PotException} is thrown.
     * </p>
     *
     * @param value     the string to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated non-blank string (the original, untrimmed {@code value})
     * @throws PotException if {@code value} is {@code null} or blank
     * @since 1.0
     */
    public static String notBlank(String value, String paramName) {
        if (value == null || value.trim().isEmpty()) {
            throw PotException.invalidParameter(paramName, "cannot be null or blank");
        }
        return value;
    }

    /**
     * Validates that a byte array is not {@code null} and has a length greater than zero.
     *
     * @param value     the byte array to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated non-empty byte array
     * @throws PotException if {@code value} is {@code null} or has zero length
     * @since 1.0
     */
    public static byte[] notEmpty(byte[] value, String paramName) {
        if (value == null || value.length == 0) {
            throw PotException.invalidParameter(paramName, "cannot be null or empty");
        }
        return value;
    }

    /**
     * Validates that a collection is not {@code null} and is not empty.
     *
     * @param <T>       the type of elements in the collection
     * @param value     the collection to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated non-empty collection
     * @throws PotException if {@code value} is {@code null} or {@link Collection#isEmpty()} returns {@code true}
     * @since 1.0
     */
    public static <T> Collection<T> notEmpty(Collection<T> value, String paramName) {
        if (value == null || value.isEmpty()) {
            throw PotException.invalidParameter(paramName, "cannot be null or empty");
        }
        return value;
    }

    /**
     * Validates that an integer value is positive (greater than zero).
     *
     * @param value     the integer value to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated positive integer
     * @throws PotException if {@code value} is less than or equal to zero
     * @since 1.0
     */
    public static int positive(int value, String paramName) {
        if (value <= 0) {
            throw PotException.invalidParameter(paramName, "must be > 0, got " + value);
        }
        return value;
    }

    /**
     * Validates that a double value is a finite number and positive (greater than zero).
     * <p>
     * This method first calls {@link #finite(double, String)} to ensure the value is finite.
     * </p>
     *
     * @param value     the double value to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated finite, positive double
     * @throws PotException if {@code value} is not finite or is less than or equal to zero
     * @see #finite(double, String)
     * @since 1.0
     */
    public static double positive(double value, String paramName) {
        finite(value, paramName);
        if (value <= 0) {
            throw PotException.invalidParameter(paramName, "must be > 0, got " + value);
        }
        return value;
    }

    /**
     * Validates that a double value is a finite number and non-negative (greater than or equal to zero).
     * <p>
     * This method first calls {@link #finite(double, String)} to ensure the value is finite.
     * </p>
     *
     * @param value     the double value to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated finite, non-negative double
     * @throws PotException if {@code value} is not finite or is less than zero
     * @see #finite(double, String)
     * @since 1.0
     */
    public static double nonNegative(double value, String paramName) {
        finite(value, paramName);
        if (value < 0) {
            throw PotException.invalidParameter(paramName, "must be >= 0, got " + value);
        }
        return value;
    }

    /**
     * Validates that a double value is a finite number and lies within a specified inclusive range.
     * <p>
     * This method first calls {@link #finite(double, String)} to ensure the value is finite.
     * </p>
     *
     * @param value         the double value to check
     * @param minInclusive  the minimum allowed value (inclusive)
     * @param maxInclusive  the maximum allowed value (inclusive)
     * @param paramName     the name of the parameter being validated, used in the exception message
     * @return the validated finite double within the specified range
     * @throws PotException if {@code value} is not finite or is outside the range {@code [minInclusive, maxInclusive]}
     * @see #finite(double, String)
     * @since 1.0
     */
    public static double inRange(double value, double minInclusive, double maxInclusive, String paramName) {
        finite(value, paramName);
        if (value < minInclusive || value > maxInclusive) {
            throw PotException.invalidParameter(
                paramName,
                String.format("must be between %.4f and %.4f, got %.4f", minInclusive, maxInclusive, value));
        }
        return value;
    }

    /**
     * Validates that a double value is a finite number (not NaN and not infinite).
     *
     * @param value     the double value to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated finite double
     * @throws PotException if {@code value} is {@link Double#isNaN() NaN} or {@link Double#isInfinite() infinite}
     * @since 1.0
     */
    public static double finite(double value, String paramName) {
        if (!Double.isFinite(value)) {
            throw PotException.invalidParameter(paramName, "must be a finite number, got " + value);
        }
        return value;
    }

    /**
     * Validates that an integer is a valid index for a collection or array of a given size.
     * <p>
     * A valid index must satisfy {@code 0 <= index < size}. This is suitable for accessing
     * existing elements.
     * </p>
     *
     * @param index     the index to validate
     * @param size      the size of the collection or array
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated index
     * @throws PotException if {@code index} is less than zero or greater than or equal to {@code size}
     * @since 1.0
     */
    public static int validIndex(int index, int size, String paramName) {
        if (index < 0 || index >= size) {
            throw PotException.invalidParameter(
                paramName,
                String.format("must be between 0 and %d, got %d", size - 1, index));
        }
        return index;
    }

    /**
     * Validates that an integer is a valid insertion index for a collection or array of a given size.
     * <p>
     * A valid insertion index must satisfy {@code 0 <= index <= size}. This is suitable for
     * operations that insert a new element at a specific position.
     * </p>
     *
     * @param index     the insertion index to validate
     * @param size      the current size of the collection or array
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated insertion index
     * @throws PotException if {@code index} is less than zero or greater than {@code size}
     * @since 1.0
     */
    public static int validInsertIndex(int index, int size, String paramName) {
        if (index < 0 || index > size) {
            throw PotException.invalidParameter(
                paramName,
                String.format("must be between 0 and %d, got %d", size, index));
        }
        return index;
    }

    /**
     * Normalizes an angle in degrees to the range {@code [0, 360)}.
     * <p>
     * The input angle is first validated to be finite via {@link #finite(double, String)}.
     * The normalization is performed by computing the remainder of division by 360.0 and
     * adjusting negative results to be positive.
     * </p>
     * <h3>Usage Example</h3>
     * <pre>{@code
     * double angle = 450.0;
     * double normalized = ValidationUtils.normalizeAngle(angle); // Returns 90.0
     * }</pre>
     *
     * @param angle the angle in degrees to normalize
     * @return the normalized angle in the range {@code [0, 360)}
     * @throws PotException if {@code angle} is not a finite number
     * @see #finite(double, String)
     * @since 1.0
     */
    public static double normalizeAngle(double angle) {
        finite(angle, "angle");
        double normalized = angle % 360.0;
        return normalized < 0 ? normalized + 360.0 : normalized;
    }

    /**
     * Validates that a double value represents a percentage in the unit interval {@code [0.0, 1.0]}.
     * <p>
     * This is a convenience method that delegates to {@link #inRange(double, double, double, String)}.
     * </p>
     *
     * @param value     the double value to check
     * @param paramName the name of the parameter being validated, used in the exception message
     * @return the validated percentage value
     * @throws PotException if {@code value} is not finite or is outside the range {@code [0.0, 1.0]}
     * @see #inRange(double, double, double, String)
     * @since 1.0
     */
    public static double percentage01(double value, String paramName) {
        return inRange(value, 0.0, 1.0, paramName);
    }
}