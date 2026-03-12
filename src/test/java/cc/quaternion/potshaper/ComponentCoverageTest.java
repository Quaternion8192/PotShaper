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

import org.apache.poi.sl.usermodel.ShapeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive coverage test for all component classes.
 * Ensures each component is loadable, enum constants support round-trip conversion,
 * and core utilities provide stable defaults and contracts.
 */
class ComponentCoverageTest {

    private static final String BASE_PACKAGE = "cc.quaternion.potshaper.";

    /**
     * Provides a stream of all component class names for parameterized testing.
     *
     * @return stream of component class names to be tested
     */
    static Stream<String> componentClassNames() {
        return Stream.of(
            "ActionHelper",
            "AnimationHelper",
            "EffectHelper",
            "JsonExporter",
            "Main",
            "MediaHelper",
            "MemoryStats",
            "Pot3DFormat",
            "PotAction",
            "PotActionType",
            "PotAlignment",
            "PotAnimation",
            "PotAudio",
            "PotBorder",
            "PotChart",
            "PotColor",
            "PotConnector",
            "PotDirection",
            "PotEffect",
            "PotElement",
            "PotException",
            "PotFill",
            "PotFont",
            "PotGradient",
            "PotGroup",
            "PotHyperlink",
            "PotImage",
            "PotLayout",
            "PotLinkType",
            "PotLogger",
            "PotMaster",
            "PotMediaOptions",
            "PotPageSize",
            "PotPresentation",
            "PotShape",
            "PotShapeType",
            "PotShadow",
            "PotSlide",
            "PotTable",
            "PotTableCell",
            "PotTextBox",
            "PotTransition",
            "PotUnknownElement",
            "PotVerticalAlignment",
            "PotVideo",
            "PotXml",
            "SaveOptions",
            "ShapeTypeMapper",
            "TransitionHelper",
            "UnitConverter",
            "UuidManager",
            "XmlUtils"
        );
    }

    /**
     * Verifies that each component class can be loaded and belongs to the correct package.
     *
     * @param className the name of the component class to test
     * @throws ClassNotFoundException if the class cannot be found
     */
    @ParameterizedTest
    @MethodSource("componentClassNames")
    void eachComponentShouldBeLoadable(String className) throws ClassNotFoundException {
        Class<?> type = Class.forName(BASE_PACKAGE + className);
        assertNotNull(type);
        assertEquals(BASE_PACKAGE.substring(0, BASE_PACKAGE.length() - 1), type.getPackageName());
    }

    /**
     * Tests that enum components support round-trip conversion by name.
     * Ensures enum constants can be resolved back to themselves using Enum.valueOf().
     *
     * @throws Exception if reflection operations fail
     */
    @Test
    void enumComponentsShouldRoundTripByName() throws Exception {
        for (String className : (Iterable<String>) componentClassNames()::iterator) {
            Class<?> type = Class.forName(BASE_PACKAGE + className);
            if (!type.isEnum()) {
                continue;
            }
            Object[] constants = type.getEnumConstants();
            assertNotNull(constants, className + " should expose enum constants");
            assertTrue(constants.length > 0, className + " should not be empty");

            for (Object constant : constants) {
                @SuppressWarnings({"rawtypes", "unchecked"})
                Enum<?> resolved = Enum.valueOf((Class<? extends Enum>) type, ((Enum<?>) constant).name());
                assertSame(constant, resolved);
            }
        }
    }

    /**
     * Validates that ShapeTypeMapper provides safe default mappings and correct type classification.
     * Tests null handling, connector detection, flowchart shapes, callouts, and arrow identification.
     */
    @Test
    void shapeTypeMapperShouldProvideSafeDefaultsAndClassification() {
        assertEquals(ShapeType.RECT, ShapeTypeMapper.toPoiType(null));
        assertEquals(PotShapeType.RECTANGLE, ShapeTypeMapper.toPotType(null));

        assertTrue(ShapeTypeMapper.isConnector(PotShapeType.LINE));
        assertTrue(ShapeTypeMapper.isFlowchart(PotShapeType.FLOWCHART_PROCESS));
        assertTrue(ShapeTypeMapper.isCallout(PotShapeType.CALLOUT_CLOUD));
        assertTrue(ShapeTypeMapper.isArrow(PotShapeType.RIGHT_ARROW));
        assertFalse(ShapeTypeMapper.isArrow(PotShapeType.RECTANGLE));
    }

    /**
     * Verifies that PotDirection and PotActionType expose stable and consistent contracts.
     * Tests direction operations (opposite, horizontal/vertical checks, angle) and action type resolution.
     */
    @Test
    void directionAndActionTypeShouldExposeStableContracts() {
        assertEquals(PotDirection.FROM_BOTTOM, PotDirection.FROM_TOP.opposite());
        assertTrue(PotDirection.HORIZONTAL.isHorizontal());
        assertFalse(PotDirection.HORIZONTAL.isVertical());
        assertEquals(315, PotDirection.FROM_TOP_RIGHT.getAngle());

        assertEquals(PotActionType.GO_TO_NEXT, PotActionType.fromValue("goToNext"));
        assertEquals("playSound", PotActionType.PLAY_SOUND.getValue());
    }

    /**
     * Ensures that the abstract base class PotElement remains abstract.
     * Validates the design contract that PotElement cannot be instantiated directly.
     *
     * @throws Exception if class loading fails
     */
    @Test
    void abstractComponentShouldStayAbstract() throws Exception {
        Class<?> elementType = Class.forName(BASE_PACKAGE + "PotElement");
        assertTrue(Modifier.isAbstract(elementType.getModifiers()));
    }
}
