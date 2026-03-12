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

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test cases for EffectHelper functionality.
 * Validates visual effects and 3D effect application and removal on shapes.
 */
class EffectHelperTest {

    /**
     * Tests application and removal of visual and 3D effects without breaking element state.
     * Covers reflection, glow, soft edge, rotation 3D, bevel, material, and lighting effects.
     */
    @Test
    void shouldApplyAndRemoveVisualAnd3dEffectsWithoutBreakingElementState() {
        try (PotPresentation presentation = PotPresentation.create()) {
            PotShape shape = presentation.getSlide(0).addShape(PotShapeType.RECTANGLE);

            shape.reflection(PotEffect.reflection().blurRadius(4).opacity(0.6, 0.1).distance(3).direction(90));
            shape.glow(PotEffect.glow().radius(3).color(PotColor.RED));
            shape.softEdge(PotEffect.softEdge().radius(2));
            shape.rotation3D(Pot3DFormat.rotation().perspective(10, 20).z(30));
            shape.bevel(Pot3DFormat.square().size(2, 3));
            shape.material(Pot3DFormat.Material.METAL);
            shape.lighting(Pot3DFormat.Lighting.BRIGHT);

            assertNotNull(shape.getRawShape());

            shape.removeEffects();
            assertNotNull(shape.getRawShape());
        }
    }
}

