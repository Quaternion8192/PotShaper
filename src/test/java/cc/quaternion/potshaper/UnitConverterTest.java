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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for UnitConverter functionality.
 * Validates bidirectional conversion between points, EMU, inches, centimeters, pixels, and angles.
 */
class UnitConverterTest {

    /**
     * Tests bidirectional conversion between points and EMU units.
     * Verifies precision and round-trip accuracy.
     */
    @Test
    void shouldConvertPointsAndEmuBidirectionally() {
        long emu = UnitConverter.pointsToEmu(72.0);
        assertEquals(914400L, emu);
        assertEquals(72.0, UnitConverter.emuToPoints(emu), 1e-9);
    }

    /**
     * Tests consistent conversion between inches, centimeters, pixels, and EMU.
     * Verifies standard conversion factors and round-trip precision.
     */
    @Test
    void shouldConvertInchesAndCentimetersAndPixelsConsistently() {
        assertEquals(914400L, UnitConverter.inchesToEmu(1.0));
        assertEquals(1.0, UnitConverter.emuToInches(914400L), 1e-9);

        assertEquals(914400L, UnitConverter.cmToEmu(2.54));
        assertEquals(2.54, UnitConverter.emuToCm(914400L), 1e-6);

        assertEquals(914400L, UnitConverter.pixelsToEmu(96.0));
        assertEquals(96.0, UnitConverter.emuToPixels(914400L), 1e-6);
    }

    /**
     * Tests angle and percentage/fraction conversion to EMU representation.
     * Verifies degrees, percentages, and fractions are properly scaled.
     */
    @Test
    void shouldConvertAnglesAndPercentages() {
        int emuAngle = UnitConverter.degreesToEmuAngle(45.5);
        assertEquals(45.5, UnitConverter.emuAngleToDegrees(emuAngle), 1e-9);

        int emuPercent = UnitConverter.percentToEmuPercent(12.34);
        assertEquals(12.34, UnitConverter.emuPercentToPercent(emuPercent), 1e-9);

        int emuFraction = UnitConverter.fractionToEmuPercent(0.45678);
        assertEquals(0.45678, UnitConverter.emuPercentToFraction(emuFraction), 1e-5);
    }

    /**
     * Tests lossless conversion for line width and font size units.
     * Verifies specialized methods maintain precision.
     */
    @Test
    void shouldConvertLineWidthAndFontUnitsLosslessly() {
        double emuLineWidth = UnitConverter.lineWidthPointsToEmu(1.5);
        assertEquals(1.5, UnitConverter.lineWidthEmuToPoints(emuLineWidth), 1e-9);

        assertEquals(18.0, UnitConverter.fontPointsToPoiUnits(18.0), 1e-9);
        assertEquals(18.0, UnitConverter.poiUnitsToFontPoints(18.0), 1e-9);
    }
}

