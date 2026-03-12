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

import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.xmlbeans.XmlObject;

/**
 * A utility class providing static methods to apply various visual effects to PowerPoint shapes.
 *
 * <p>This helper class encapsulates the XML manipulation required to apply reflection, glow,
 * soft edge, 3D rotation, bevel, material, and lighting effects to shapes in PowerPoint
 * presentations. All methods handle null parameters gracefully by returning early without
 * applying any effect. When XML operations fail, methods log warnings and throw
 * {@code PotException} with {@code EFFECT_ERROR} code.</p>
 *
 * <p>Effects are applied by constructing appropriate DrawingML XML fragments and inserting
 * them into the shape's XML structure using {@link XmlUtils}. The class uses predefined
 * namespace declarations for the DrawingML (a:) and PresentationML (p:) namespaces.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
class EffectHelper {

    private static final String A_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    private static final String P_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    private static final String A_DECL = "declare namespace a='" + A_NS + "'; ";
    private static final String P_DECL = "declare namespace p='" + P_NS + "'; ";

    /**
     * Prevents instantiation of this utility class.
     *
     * <p>This constructor throws an {@code AssertionError} if invoked, enforcing the class's
     * static-only nature.</p>
     *
     * @throws AssertionError always thrown when this constructor is called
     */
    private EffectHelper() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Applies a reflection effect to the specified shape.
     *
     * <p>This method creates a DrawingML {@code <a:reflection>} element with attributes
     * calculated from the provided {@code PotEffect.Reflection} parameters. The effect is
     * inserted as a child of the shape's effect list ({@code //a:effectLst}). Distance and
     * blur radius values are converted from points to English Metric Units (EMU) by
     * multiplying by 12700. Opacity values are converted to percentage units (0-100000)
     * by multiplying by 100000. Direction is converted to 1/60000 degree units.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.createAutoShape();
     * PotEffect.Reflection reflection = new PotEffect.Reflection(5.0, 0.75, 0.25, 10.0, 90.0);
     * EffectHelper.applyReflection(shape, reflection);
     * }</pre>
     *
     * @param shape the PowerPoint shape to apply the reflection effect to; if null, the method returns immediately
     * @param reflection the reflection effect configuration containing blur radius, start/end opacity,
     *                   distance, and direction; if null, the method returns immediately
     * @throws PotException if the XML insertion operation fails, with error code {@code EFFECT_ERROR}
     * @see PotEffect.Reflection
     * @see XmlUtils#insertXml(XmlObject, String, String, XmlUtils.InsertPosition)
     */
    static void applyReflection(XSLFShape shape, PotEffect.Reflection reflection) {
        if (shape == null || reflection == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            double blurRad = reflection.getBlurRadius() * 12700;
            double stA = reflection.getStartOpacity() * 100000;
            double endA = reflection.getEndOpacity() * 100000;
            double dist = reflection.getDistance() * 12700;
            double dir = reflection.getDirection() * 60000;

            String xml = String.format(
                "<a:reflection blurRad='%d' stA='%d' endA='%d' dist='%d' dir='%d'/>",
                (long) blurRad, (long) stA, (long) endA, (long) dist, (long) dir
            );

            XmlUtils.insertXml(xmlObject, A_DECL + "$this//a:effectLst", xml, XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "applyReflection",
                "Failed to apply reflection effect", e);
            throw new PotException("Failed to apply reflection effect", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }

    /**
     * Applies a glow effect to the specified shape.
     *
     * <p>This method creates a DrawingML {@code <a:glow>} element containing an {@code <a:srgbClr>}
     * child element with the specified color. The glow radius is converted from points to
     * English Metric Units (EMU) by multiplying by 12700. The color is extracted from the
     * {@code PotEffect.Glow} configuration and formatted as a 6-digit hexadecimal RGB value.
     * The effect is inserted as a child of the shape's effect list ({@code //a:effectLst}).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.createAutoShape();
     * PotColor color = new PotColor(255, 0, 0);
     * PotEffect.Glow glow = new PotEffect.Glow(10.0, color);
     * EffectHelper.applyGlow(shape, glow);
     * }</pre>
     *
     * @param shape the PowerPoint shape to apply the glow effect to; if null, the method returns immediately
     * @param glow the glow effect configuration containing radius and color; if null, the method returns immediately
     * @throws PotException if the XML insertion operation fails, with error code {@code EFFECT_ERROR}
     * @see PotEffect.Glow
     * @see PotColor#toArgbInt()
     * @see XmlUtils#insertXml(XmlObject, String, String, XmlUtils.InsertPosition)
     */
    static void applyGlow(XSLFShape shape, PotEffect.Glow glow) {
        if (shape == null || glow == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            double rad = glow.getRadius() * 12700;
            PotColor color = glow.getColor();
            int colorValue = color.toArgbInt();
            String colorHex = String.format("%06X", colorValue & 0xFFFFFF);

            String xml = String.format(
                "<a:glow rad='%d'><a:srgbClr val='%s'/></a:glow>",
                (long) rad, colorHex
            );

            XmlUtils.insertXml(xmlObject, A_DECL + "$this//a:effectLst", xml, XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "applyGlow",
                "Failed to apply glow effect", e);
            throw new PotException("Failed to apply glow effect", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }

    /**
     * Applies a soft edge effect to the specified shape.
     *
     * <p>This method creates a DrawingML {@code <a:softEdge>} element with a radius attribute
     * converted from points to English Metric Units (EMU) by multiplying by 12700. The effect
     * is inserted as a child of the shape's effect list ({@code //a:effectLst}).</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.createAutoShape();
     * PotEffect.SoftEdge softEdge = new PotEffect.SoftEdge(5.0);
     * EffectHelper.applySoftEdge(shape, softEdge);
     * }</pre>
     *
     * @param shape the PowerPoint shape to apply the soft edge effect to; if null, the method returns immediately
     * @param softEdge the soft edge effect configuration containing the radius; if null, the method returns immediately
     * @throws PotException if the XML insertion operation fails, with error code {@code EFFECT_ERROR}
     * @see PotEffect.SoftEdge
     * @see XmlUtils#insertXml(XmlObject, String, String, XmlUtils.InsertPosition)
     */
    static void applySoftEdge(XSLFShape shape, PotEffect.SoftEdge softEdge) {
        if (shape == null || softEdge == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            double rad = softEdge.getRadius() * 12700;

            String xml = String.format(
                "<a:softEdge rad='%d'/>",
                (long) rad
            );

            XmlUtils.insertXml(xmlObject, A_DECL + "$this//a:effectLst", xml, XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "applySoftEdge",
                "Failed to apply soft edge effect", e);
            throw new PotException("Failed to apply soft edge effect", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }

    /**
     * Applies a 3D rotation effect to the specified shape.
     *
     * <p>This method creates a DrawingML {@code <a:scene3d>} element containing a camera
     * with rotation attributes. The rotation values (X, Y, Z) are converted to 1/60000 degree
     * units by multiplying by 60000. The XML fragment is inserted as a child of the shape's
     * shape properties ({@code //p:spPr}) within the PresentationML namespace.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.createAutoShape();
     * Pot3DFormat.Rotation rotation = new Pot3DFormat.Rotation(30.0, 45.0, 15.0);
     * EffectHelper.apply3DRotation(shape, rotation);
     * }</pre>
     *
     * @param shape the PowerPoint shape to apply the 3D rotation to; if null, the method returns immediately
     * @param rotation the 3D rotation configuration containing X, Y, and Z rotation angles;
     *                 if null, the method returns immediately
     * @throws PotException if the XML insertion operation fails, with error code {@code EFFECT_ERROR}
     * @see Pot3DFormat.Rotation
     * @see XmlUtils#insertXml(XmlObject, String, String, XmlUtils.InsertPosition)
     */
    static void apply3DRotation(XSLFShape shape, Pot3DFormat.Rotation rotation) {
        if (shape == null || rotation == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            double rotX = rotation.getRotationX() * 60000;
            double rotY = rotation.getRotationY() * 60000;
            double rotZ = rotation.getRotationZ() * 60000;

            String xml = String.format(
                "<a:scene3d xmlns:a='%s'><a:camera prst='perspectiveFront'><a:rot lat='%d' lon='%d' rev='%d'/>" +
                "</a:camera></a:scene3d>",
                A_NS, (long) rotX, (long) rotY, (long) rotZ
            );

            XmlUtils.insertXml(xmlObject, P_DECL + "$this//p:spPr", xml, XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "apply3DRotation",
                "Failed to apply 3D rotation", e);
            throw new PotException("Failed to apply 3D rotation", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }

    /**
     * Applies a bevel effect to the specified shape.
     *
     * <p>This method creates a DrawingML {@code <a:sp3d>} element containing a top bevel
     * ({@code <a:bevelT>}) with width and height attributes converted from points to English
     * Metric Units (EMU) by multiplying by 12700. The bevel preset type is taken from the
     * configuration. The XML fragment is inserted as a child of the shape's shape properties
     * ({@code //p:spPr}) within the PresentationML namespace.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.createAutoShape();
     * Pot3DFormat.Bevel bevel = new Pot3DFormat.Bevel(2.0, 2.0, "circle");
     * EffectHelper.applyBevel(shape, bevel);
     * }</pre>
     *
     * @param shape the PowerPoint shape to apply the bevel effect to; if null, the method returns immediately
     * @param bevel the bevel effect configuration containing width, height, and preset type;
     *              if null, the method returns immediately
     * @throws PotException if the XML insertion operation fails, with error code {@code EFFECT_ERROR}
     * @see Pot3DFormat.Bevel
     * @see XmlUtils#insertXml(XmlObject, String, String, XmlUtils.InsertPosition)
     */
    static void applyBevel(XSLFShape shape, Pot3DFormat.Bevel bevel) {
        if (shape == null || bevel == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            double w = bevel.getWidth() * 12700;
            double h = bevel.getHeight() * 12700;
            String preset = bevel.getPreset();

            String xml = String.format(
                "<a:sp3d xmlns:a='%s'><a:bevelT w='%d' h='%d' prst='%s'/></a:sp3d>",
                A_NS, (long) w, (long) h, preset
            );

            XmlUtils.insertXml(xmlObject, P_DECL + "$this//p:spPr", xml, XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "applyBevel",
                "Failed to apply bevel effect", e);
            throw new PotException("Failed to apply bevel effect", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }

    /**
     * Applies a material effect to the specified shape.
     *
     * <p>This method creates a DrawingML {@code <a:sp3d>} element containing an effect list
     * with a material ({@code <a:mtl>}) of the specified preset type. The XML fragment is
     * inserted as a child of the shape's shape properties ({@code //p:spPr}) within the
     * PresentationML namespace.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * XSLFShape shape = slide.createAutoShape();
     * Pot3DFormat.Material material = new Pot3DFormat.Material("plastic");
     * EffectHelper.applyMaterial(shape, material);
     * }</pre>
     *
     * @param shape the PowerPoint shape to apply the material effect to; if null, the method returns immediately
     * @param material the material effect configuration containing the preset type;
     *                 if null, the method returns immediately
     * @throws PotException if the XML insertion operation fails, with error code {@code EFFECT_ERROR}
     * @see Pot3DFormat.Material
     * @see XmlUtils#insertXml(XmlObject, String, String, XmlUtils.InsertPosition)
     */
    static void applyMaterial(XSLFShape shape, Pot3DFormat.Material material) {
        if (shape == null || material == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            String mtlPreset = material.getValue();

            String xml = String.format(
                "<a:sp3d xmlns:a='%s'><a:effectLst><a:mtl prst='%s'/></a:effectLst></a:sp3d>",
                A_NS, mtlPreset
            );

            XmlUtils.insertXml(xmlObject, P_DECL + "$this//p:spPr", xml, XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "applyMaterial",
                "Failed to apply material", e);
            throw new PotException("Failed to apply material", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }

    /**
     * Applies 3D lighting effect to the specified shape.
     *
     * <p>This method configures a light rig on the shape's 3D scene definition, controlling
     * how the shape is illuminated in 3D space. The lighting direction is set to left ('l'),
     * and the rig type determines the intensity and angle of the light source.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShape shape = slide.addShape(PotShapeType.RECTANGLE);
     * EffectHelper.applyLighting(shape.getRawShape(), Pot3DFormat.Lighting.BRIGHT_ROOM);
     * }</pre>
     *
     * @param shape the shape to apply lighting to; must not be null
     * @param lighting the lighting configuration defining the light rig type; must not be null
     * @throws PotException with error code {@link PotException.ErrorCode#EFFECT_ERROR} if lighting application fails due to XML manipulation errors
     * @see Pot3DFormat.Lighting
     */
    static void applyLighting(XSLFShape shape, Pot3DFormat.Lighting lighting) {
        if (shape == null || lighting == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            String ltgRig = lighting.getValue();

            String xml = String.format(
                "<a:scene3d xmlns:a='%s'><a:lightRig rig='%s' dir='l'/></a:scene3d>",
                A_NS, ltgRig
            );

            XmlUtils.insertXml(xmlObject, P_DECL + "$this//p:spPr", xml, XmlUtils.InsertPosition.CHILD);
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "applyLighting",
                "Failed to apply lighting", e);
            throw new PotException("Failed to apply lighting", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }

    /**
     * Removes all visual effects from the specified shape.
     *
     * <p>This method clears all effect-related XML nodes from the shape, including
     * effect lists ({@code a:effectLst}), 3D scene definitions ({@code a:scene3d}),
     * and 3D shape properties ({@code a:sp3d}). It provides a clean slate for applying
     * new effects or reverting to the shape's default appearance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotShape shape = slide.addShape(PotShapeType.RECTANGLE);
     * shape.shadow(PotShadow.outer(5, PotColor.GRAY))
     *      .glow(PotEffect.Glow.red(3));
     *
     * // Remove all effects
     * EffectHelper.removeAllEffects(shape.getRawShape());
     * }</pre>
     *
     * @param shape the shape from which to remove all effects; if null, the method returns immediately without action
     * @throws PotException with error code {@link PotException.ErrorCode#EFFECT_ERROR} if effect removal fails due to XML manipulation errors
     */
    static void removeAllEffects(XSLFShape shape) {
        if (shape == null) return;

        try {
            XmlObject xmlObject = shape.getXmlObject();
            XmlUtils.deleteNodes(xmlObject, A_DECL + "$this//a:effectLst");
            XmlUtils.deleteNodes(xmlObject, A_DECL + "$this//a:scene3d");
            XmlUtils.deleteNodes(xmlObject, A_DECL + "$this//a:sp3d");
        } catch (Exception e) {
            PotLogger.warn(EffectHelper.class, "removeAllEffects",
                "Failed to remove all effects", e);
            throw new PotException("Failed to remove all effects", e,
                PotException.ErrorCode.EFFECT_ERROR);
        }
    }
}
