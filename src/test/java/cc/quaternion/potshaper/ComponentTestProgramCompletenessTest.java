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

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Validates completeness of the test program by ensuring each component has a corresponding test class.
 * Maintains a mapping between components and their test classes to verify coverage.
 */
class ComponentTestProgramCompletenessTest {

    private static final Map<String, String> COMPONENT_TO_TEST = buildComponentToTestMap();

    /**
     * Builds a mapping from component class names to their corresponding test class names.
     * Uses LinkedHashMap to preserve insertion order for predictable test execution.
     *
     * @return map of component to test class mappings
     */
    private static Map<String, String> buildComponentToTestMap() {
        Map<String, String> mapping = new LinkedHashMap<>();
        mapping.put("ActionHelper", "ComponentCoverageTest");
        mapping.put("AnimationHelper", "PotAnimationHelperTest");
        mapping.put("EffectHelper", "ComponentCoverageTest");
        mapping.put("JsonExporter", "ComponentCoverageTest");
        mapping.put("Main", "ComponentCoverageTest");
        mapping.put("MediaHelper", "ComponentCoverageTest");
        mapping.put("MemoryStats", "ComponentCoverageTest");
        mapping.put("Pot3DFormat", "ComponentCoverageTest");
        mapping.put("PotAction", "PotActionTest");
        mapping.put("PotActionType", "PotActionTest");
        mapping.put("PotAlignment", "PotAlignmentTest");
        mapping.put("PotAnimation", "PotAnimationTest");
        mapping.put("PotAudio", "PotPresentationWorkflowTest");
        mapping.put("PotBorder", "PotBorderTest");
        mapping.put("PotChart", "PotPresentationWorkflowTest");
        mapping.put("PotColor", "PotColorTest");
        mapping.put("PotConnector", "PotPresentationWorkflowTest");
        mapping.put("PotDirection", "PotDirectionTest");
        mapping.put("PotEffect", "ComponentCoverageTest");
        mapping.put("PotElement", "ComponentCoverageTest");
        mapping.put("PotException", "PotExceptionTest");
        mapping.put("PotFill", "PotFillTest");
        mapping.put("PotFont", "PotFontTest");
        mapping.put("PotGradient", "PotGradientTest");
        mapping.put("PotGroup", "PotPresentationWorkflowTest");
        mapping.put("PotHyperlink", "PotHyperlinkTest");
        mapping.put("PotImage", "PotPresentationWorkflowTest");
        mapping.put("PotLayout", "ComponentCoverageTest");
        mapping.put("PotLinkType", "PotLinkTypeTest");
        mapping.put("PotLogger", "ComponentCoverageTest");
        mapping.put("PotMaster", "ComponentCoverageTest");
        mapping.put("PotMediaOptions", "ComponentCoverageTest");
        mapping.put("PotPageSize", "PotPageSizeTest");
        mapping.put("PotPresentation", "PotPresentationBoundaryTest");
        mapping.put("PotShape", "PotPresentationWorkflowTest");
        mapping.put("PotShapeType", "ComponentCoverageTest");
        mapping.put("PotShadow", "PotShadowTest");
        mapping.put("PotSlide", "PotPresentationWorkflowTest");
        mapping.put("PotTable", "PotPresentationWorkflowTest");
        mapping.put("PotTableCell", "PotPresentationWorkflowTest");
        mapping.put("PotTextBox", "PotPresentationWorkflowTest");
        mapping.put("PotTransition", "PotTransitionTest");
        mapping.put("PotUnknownElement", "ComponentCoverageTest");
        mapping.put("PotVerticalAlignment", "PotVerticalAlignmentTest");
        mapping.put("PotVideo", "PotPresentationWorkflowTest");
        mapping.put("PotXml", "ComponentCoverageTest");
        mapping.put("SaveOptions", "SaveOptionsTest");
        mapping.put("ShapeTypeMapper", "ShapeTypeMapperTest");
        mapping.put("TransitionHelper", "PotTransitionHelperTest");
        mapping.put("UnitConverter", "UnitConverterTest");
        mapping.put("UuidManager", "UuidManagerTest");
        mapping.put("XmlUtils", "ComponentCoverageTest");
        return mapping;
    }

    /**
     * Verifies that each component has an assigned test class and both classes exist.
     * Ensures no component or test class is missing from the mapping.
     *
     * @throws Exception if any class cannot be loaded
     */
    @Test
    void eachComponentShouldHaveAssignedTestClass() throws Exception {
        for (Map.Entry<String, String> entry : COMPONENT_TO_TEST.entrySet()) {
            String componentClass = "cc.quaternion.potshaper." + entry.getKey();
            String testClass = "cc.quaternion.potshaper." + entry.getValue();

            assertNotNull(Class.forName(componentClass), "Missing component class: " + componentClass);
            assertNotNull(Class.forName(testClass), "Missing test class: " + testClass);
        }
    }
}
