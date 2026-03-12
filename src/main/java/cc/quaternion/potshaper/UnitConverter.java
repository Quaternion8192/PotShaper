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
 * Provides static utility methods for converting between PowerPoint's internal EMU units and various display units.
 * <p>This class handles conversions between English Metric Units (EMU) used internally by PowerPoint and common
 * units such as points, inches, centimeters, and pixels. It also provides conversion for angles and percentages
 * as used in PowerPoint's shape properties. All methods are static and the class cannot be instantiated.</p>
 *
 * <h3>Unit Definitions</h3>
 * <ul>
 *   <li><b>EMU</b> - English Metric Units, the internal coordinate unit used by PowerPoint (1 inch = 914400 EMU).</li>
 *   <li><b>Points</b> - A typographic unit where 1 point = 1/72 inch.</li>
 *   <li><b>Inches</b> - Standard imperial length unit.</li>
 *   <li><b>Centimeters</b> - Standard metric length unit.</li>
 *   <li><b>Pixels</b> - Screen pixels at a standard resolution of 96 DPI.</li>
 * </ul>
 *
 * <h3>Base Conversion Ratios</h3>
 * <pre>
 * 1 inch = 914400 EMU
 * 1 inch = 72 points
 * 1 inch = 2.54 cm
 * 1 inch = 96 pixels (at 96 DPI)
 * </pre>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class UnitConverter {

    /** EMU per inch conversion constant (914400). */
    private static final double EMU_PER_INCH = 914400.0;

    /** Points per inch conversion constant (72). */
    private static final double POINTS_PER_INCH = 72.0;

    /** Centimeters per inch conversion constant (2.54). */
    private static final double CM_PER_INCH = 2.54;

    /** Pixels per inch conversion constant at 96 DPI (96). */
    private static final double PIXELS_PER_INCH = 96.0;

    /** EMU per point conversion constant, derived from EMU_PER_INCH / POINTS_PER_INCH. */
    private static final double EMU_PER_POINT = EMU_PER_INCH / POINTS_PER_INCH;

    /** EMU per centimeter conversion constant, derived from EMU_PER_INCH / CM_PER_INCH. */
    private static final double EMU_PER_CM = EMU_PER_INCH / CM_PER_INCH;

    /** EMU per pixel conversion constant, derived from EMU_PER_INCH / PIXELS_PER_INCH. */
    private static final double EMU_PER_PIXEL = EMU_PER_INCH / PIXELS_PER_INCH;

    /**
     * Private constructor to prevent instantiation.
     * <p>This is a utility class with only static methods.</p>
     */
    private UnitConverter() {
    }

    // ==================== Points to EMU Conversions ====================

    /**
     * Converts a measurement in points to PowerPoint's internal EMU units.
     * <p>The result is rounded to the nearest whole EMU using {@link Math#round(double)}.</p>
     *
     * @param points the length value in points
     * @return the equivalent length in EMU, rounded to the nearest long
     */
    static long pointsToEmu(double points) {
        return Math.round(points * EMU_PER_POINT);
    }

    /**
     * Converts a measurement in PowerPoint's internal EMU units to points.
     *
     * @param emu the length value in EMU
     * @return the equivalent length in points as a double
     */
    static double emuToPoints(long emu) {
        return emu / EMU_PER_POINT;
    }

    // ==================== Inches Conversions ====================

    /**
     * Converts a measurement in inches to PowerPoint's internal EMU units.
     * <p>The result is rounded to the nearest whole EMU using {@link Math#round(double)}.</p>
     *
     * @param inches the length value in inches
     * @return the equivalent length in EMU, rounded to the nearest long
     */
    static long inchesToEmu(double inches) {
        return Math.round(inches * EMU_PER_INCH);
    }

    /**
     * Converts a measurement in PowerPoint's internal EMU units to inches.
     *
     * @param emu the length value in EMU
     * @return the equivalent length in inches as a double
     */
    static double emuToInches(long emu) {
        return emu / EMU_PER_INCH;
    }

    /**
     * Converts a measurement in points to inches.
     *
     * @param points the length value in points
     * @return the equivalent length in inches as a double
     */
    static double pointsToInches(double points) {
        return points / POINTS_PER_INCH;
    }

    /**
     * Converts a measurement in inches to points.
     *
     * @param inches the length value in inches
     * @return the equivalent length in points as a double
     */
    static double inchesToPoints(double inches) {
        return inches * POINTS_PER_INCH;
    }

    // ==================== Centimeters Conversions ====================

    /**
     * Converts a measurement in centimeters to PowerPoint's internal EMU units.
     * <p>The result is rounded to the nearest whole EMU using {@link Math#round(double)}.</p>
     *
     * @param cm the length value in centimeters
     * @return the equivalent length in EMU, rounded to the nearest long
     */
    static long cmToEmu(double cm) {
        return Math.round(cm * EMU_PER_CM);
    }

    /**
     * Converts a measurement in PowerPoint's internal EMU units to centimeters.
     *
     * @param emu the length value in EMU
     * @return the equivalent length in centimeters as a double
     */
    static double emuToCm(long emu) {
        return emu / EMU_PER_CM;
    }

    /**
     * Converts a measurement in points to centimeters.
     *
     * @param points the length value in points
     * @return the equivalent length in centimeters as a double
     */
    static double pointsToCm(double points) {
        return points / POINTS_PER_INCH * CM_PER_INCH;
    }

    /**
     * Converts a measurement in centimeters to points.
     *
     * @param cm the length value in centimeters
     * @return the equivalent length in points as a double
     */
    static double cmToPoints(double cm) {
        return cm / CM_PER_INCH * POINTS_PER_INCH;
    }

    // ==================== Pixels Conversions ====================

    /**
     * Converts a measurement in pixels (at 96 DPI) to PowerPoint's internal EMU units.
     * <p>The result is rounded to the nearest whole EMU using {@link Math#round(double)}.</p>
     *
     * @param pixels the length value in pixels
     * @return the equivalent length in EMU, rounded to the nearest long
     */
    static long pixelsToEmu(double pixels) {
        return Math.round(pixels * EMU_PER_PIXEL);
    }

    /**
     * Converts a measurement in PowerPoint's internal EMU units to pixels (at 96 DPI).
     *
     * @param emu the length value in EMU
     * @return the equivalent length in pixels as a double
     */
    static double emuToPixels(long emu) {
        return emu / EMU_PER_PIXEL;
    }

    /**
     * Converts a measurement in points to pixels (at 96 DPI).
     *
     * @param points the length value in points
     * @return the equivalent length in pixels as a double
     */
    static double pointsToPixels(double points) {
        return points / POINTS_PER_INCH * PIXELS_PER_INCH;
    }

    /**
     * Converts a measurement in pixels (at 96 DPI) to points.
     *
     * @param pixels the length value in pixels
     * @return the equivalent length in points as a double
     */
    static double pixelsToPoints(double pixels) {
        return pixels / PIXELS_PER_INCH * POINTS_PER_INCH;
    }

    // ==================== Angle Conversions ====================

    /**
     * Converts an angle in degrees to PowerPoint's internal EMU angle units.
     * <p>PowerPoint stores angles as integer EMU units where 60000 EMU = 1 degree.
     * The result is rounded to the nearest integer using {@link Math#round(double)}.</p>
     *
     * @param degrees the angle in degrees
     * @return the equivalent angle in EMU units, rounded to the nearest int
     */
    static int degreesToEmuAngle(double degrees) {
        return (int) Math.round(degrees * 60000);
    }

    /**
     * Converts an angle in PowerPoint's internal EMU units to degrees.
     *
     * @param emuAngle the angle in EMU units
     * @return the equivalent angle in degrees as a double
     */
    static double emuAngleToDegrees(int emuAngle) {
        return emuAngle / 60000.0;
    }

    // ==================== Percentage Conversions ====================

    /**
     * Converts a percentage value (0-100) to PowerPoint's internal EMU percentage units.
     * <p>PowerPoint stores percentages as integer EMU units where 1% = 1000 EMU.
     * The result is rounded to the nearest integer using {@link Math#round(double)}.</p>
     *
     * @param percent the percentage value in the range 0-100
     * @return the equivalent value in EMU percentage units, rounded to the nearest int
     */
    static int percentToEmuPercent(double percent) {
        return (int) Math.round(percent * 1000);
    }

    /**
     * Converts a percentage value in PowerPoint's internal EMU units to a standard percentage (0-100).
     *
     * @param emuPercent the percentage value in EMU units
     * @return the equivalent percentage in the range 0-100 as a double
     */
    static double emuPercentToPercent(int emuPercent) {
        return emuPercent / 1000.0;
    }

    /**
     * Converts a fractional value (0-1) to PowerPoint's internal EMU percentage units.
     * <p>PowerPoint stores percentages as integer EMU units where 1.0 = 100000 EMU.
     * The result is rounded to the nearest integer using {@link Math#round(double)}.</p>
     *
     * @param fraction the fractional value in the range 0-1
     * @return the equivalent value in EMU percentage units, rounded to the nearest int
     */
    static int fractionToEmuPercent(double fraction) {
        return (int) Math.round(fraction * 100000);
    }

    /**
     * Converts a percentage value in PowerPoint's internal EMU units to a fractional value (0-1).
     *
     * @param emuPercent the percentage value in EMU units
     * @return the equivalent fractional value in the range 0-1 as a double
     */
    static double emuPercentToFraction(int emuPercent) {
        return emuPercent / 100000.0;
    }

    // ==================== Font Unit Conversions ====================

    /**
     * Converts a font size in points to the units used internally by Apache POI.
     * <p>This is a direct pass-through conversion where points map 1:1 to POI units.</p>
     *
     * @param points the font size in points
     * @return the equivalent font size in POI internal units
     */
    static double fontPointsToPoiUnits(double points) {
        return points;
    }

    /**
     * Converts a font size from Apache POI internal units to points.
     * <p>This is a direct pass-through conversion where POI units map 1:1 to points.</p>
     *
     * @param poiUnits the font size in POI internal units
     * @return the equivalent font size in points
     */
    static double poiUnitsToFontPoints(double poiUnits) {
        return poiUnits;
    }

    // ==================== Line Width Conversions ====================

    /**
     * Converts a line width in points to PowerPoint's internal EMU units.
     * <p>This conversion uses the same ratio as {@link #pointsToEmu(double)} but returns a double
     * without rounding, suitable for precise line width calculations.</p>
     *
     * @param points the line width in points
     * @return the equivalent line width in EMU as a double
     */
    static double lineWidthPointsToEmu(double points) {
        return points * EMU_PER_POINT;
    }

    /**
     * Converts a line width in PowerPoint's internal EMU units to points.
     *
     * @param emu the line width in EMU
     * @return the equivalent line width in points as a double
     */
    static double lineWidthEmuToPoints(double emu) {
        return emu / EMU_PER_POINT;
    }
}