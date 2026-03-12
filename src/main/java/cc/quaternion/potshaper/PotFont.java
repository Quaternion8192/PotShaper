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

import java.util.Objects;

/**
 * Represents an immutable font configuration for text styling within the PotShaper library.
 *
 * <p>This class encapsulates font properties such as family, size, weight, style, and color.
 * It provides a fluent API for creating modified instances, following an immutable builder pattern.
 * Predefined constants are available for common use cases like titles, body text, and code.</p>
 *
 * @author Quaternion8192
 * @since 1.0
 */
public class PotFont {

    // ==================== Predefined Font Constants ====================

    /**
     * The default font configuration.
     * <p>This constant uses the "Calibri" family with a size of 18 points.</p>
     */
    public static final PotFont DEFAULT = new PotFont("Calibri", 18);

    /**
     * A font configuration suitable for main titles.
     * <p>This constant uses the "Calibri Light" family with a size of 44 points.</p>
     */
    public static final PotFont TITLE = new PotFont("Calibri Light", 44);

    /**
     * A font configuration suitable for subtitles.
     * <p>This constant uses the "Calibri" family with a size of 32 points.</p>
     */
    public static final PotFont SUBTITLE = new PotFont("Calibri", 32);

    /**
     * A font configuration suitable for standard body text.
     * <p>This constant uses the "Calibri" family with a size of 18 points.</p>
     */
    public static final PotFont BODY = new PotFont("Calibri", 18);

    /**
     * A font configuration suitable for displaying source code.
     * <p>This constant uses the monospaced "Consolas" family with a size of 14 points.</p>
     */
    public static final PotFont CODE = new PotFont("Consolas", 14);

    // ==================== Instance Fields ====================

    private final String family;
    private final double size;
    private final boolean bold;
    private final boolean italic;
    private final boolean underline;
    private final boolean strikethrough;
    private final PotColor color;
    private final Double characterSpacing; // null indicates default spacing
    private final String language; // null indicates default language

    // ==================== Constructors ====================

    /**
     * Constructs a new font configuration with the specified family and size.
     *
     * <p>All other style properties (bold, italic, underline, strikethrough, color, spacing, language)
     * are set to their default values (false or null). If the provided family is {@code null},
     * the family defaults to "Calibri". If the provided size is not positive, the size defaults to 18.</p>
     *
     * @param family the name of the font family, or {@code null} to use the default "Calibri"
     * @param size   the font size in points; must be positive or will default to 18
     */
    public PotFont(String family, double size) {
        this(family, size, false, false, false, false, null, null, null);
    }

    /**
     * Private full constructor used internally by the fluent builder methods.
     *
     * @param family          the font family name
     * @param size            the font size in points
     * @param bold            {@code true} if the font is bold
     * @param italic          {@code true} if the font is italic
     * @param underline       {@code true} if the font is underlined
     * @param strikethrough   {@code true} if the font has strikethrough
     * @param color           the text color, or {@code null} for default
     * @param characterSpacing the character spacing in points, or {@code null} for default
     * @param language        the language tag (e.g., "en-US"), or {@code null} for default
     */
    private PotFont(String family, double size, boolean bold, boolean italic,
                    boolean underline, boolean strikethrough, PotColor color,
                    Double characterSpacing, String language) {
        this.family = family != null ? family : "Calibri";
        this.size = size > 0 ? size : 18;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikethrough = strikethrough;
        this.color = color;
        this.characterSpacing = characterSpacing;
        this.language = language;
    }

    // ==================== Static Factory Methods ====================

    /**
     * Creates a new font configuration with the specified family and size.
     *
     * <p>This is a static convenience method equivalent to the public constructor
     * {@link #PotFont(String, double)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont customFont = PotFont.of("Arial", 24.0);
     * }</pre>
     *
     * @param family the name of the font family, or {@code null} to use the default "Calibri"
     * @param size   the font size in points; must be positive or will default to 18
     * @return a new {@code PotFont} instance with the given family and size
     * @see #PotFont(String, double)
     */
    public static PotFont of(String family, double size) {
        return new PotFont(family, size);
    }

    /**
     * Creates a new font configuration with the specified size, using the default family.
     *
     * <p>The font family is set to the default "Calibri".</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont largeDefault = PotFont.size(36.0);
     * }</pre>
     *
     * @param size the font size in points; must be positive or will default to 18
     * @return a new {@code PotFont} instance with the default family and the given size
     */
    public static PotFont size(double size) {
        return new PotFont(null, size);
    }

    /**
     * Creates a new font configuration with the specified family, using the default size.
     *
     * <p>The font size is set to the default 18 points.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont timesFont = PotFont.family("Times New Roman");
     * }</pre>
     *
     * @param family the name of the font family, or {@code null} to use the default "Calibri"
     * @return a new {@code PotFont} instance with the given family and default size
     */
    public static PotFont family(String family) {
        return new PotFont(family, 18);
    }

    /**
     * Creates a new font configuration with all default properties.
     *
     * <p>This method returns a font equivalent to the {@link #DEFAULT} constant,
     * using the "Calibri" family and a size of 18 points.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont defaultFont = PotFont.create();
     * }</pre>
     *
     * @return a new {@code PotFont} instance with default family and size
     * @see #DEFAULT
     */
    public static PotFont create() {
        return new PotFont("Calibri", 18);
    }

    // ==================== Fluent Builder Methods ====================

    /**
     * Returns a new font configuration with the specified family, preserving all other properties.
     *
     * <p>This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont original = PotFont.DEFAULT;
     * PotFont modified = original.withFamily("Verdana");
     * }</pre>
     *
     * @param family the new font family name, or {@code null} to use the default "Calibri"
     * @return a new {@code PotFont} instance with the updated family
     */
    public PotFont withFamily(String family) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    /**
     * Returns a new font configuration with the specified size, preserving all other properties.
     *
     * <p>This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont original = PotFont.TITLE;
     * PotFont smallerTitle = original.withSize(32.0);
     * }</pre>
     *
     * @param size the new font size in points; must be positive or will default to 18
     * @return a new {@code PotFont} instance with the updated size
     */
    public PotFont withSize(double size) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    /**
     * Returns a new font configuration with the bold property set to {@code true}.
     *
     * <p>This is a convenience method equivalent to {@code bold(true)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont boldBody = PotFont.BODY.bold();
     * }</pre>
     *
     * @return a new {@code PotFont} instance with bold enabled
     * @see #bold(boolean)
     */
    public PotFont bold() {
        return bold(true);
    }

    /**
     * Returns a new font configuration with the specified bold property, preserving all other properties.
     *
     * <p>This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont original = PotFont.CODE;
     * PotFont nonBoldCode = original.bold(false);
     * }</pre>
     *
     * @param bold {@code true} to enable bold styling, {@code false} to disable it
     * @return a new {@code PotFont} instance with the updated bold property
     */
    public PotFont bold(boolean bold) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    /**
     * Returns a new font configuration with the italic property set to {@code true}.
     *
     * <p>This is a convenience method equivalent to {@code italic(true)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont italicTitle = PotFont.TITLE.italic();
     * }</pre>
     *
     * @return a new {@code PotFont} instance with italic enabled
     * @see #italic(boolean)
     */
    public PotFont italic() {
        return italic(true);
    }

    /**
     * Returns a new font configuration with the specified italic property, preserving all other properties.
     *
     * <p>This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont original = PotFont.SUBTITLE;
     * PotFont nonItalicSubtitle = original.italic(false);
     * }</pre>
     *
     * @param italic {@code true} to enable italic styling, {@code false} to disable it
     * @return a new {@code PotFont} instance with the updated italic property
     */
    public PotFont italic(boolean italic) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    /**
     * Returns a new font configuration with the underline property set to {@code true}.
     *
     * <p>This is a convenience method equivalent to {@code underline(true)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont underlinedBody = PotFont.BODY.underline();
     * }</pre>
     *
     * @return a new {@code PotFont} instance with underline enabled
     * @see #underline(boolean)
     */
    public PotFont underline() {
        return underline(true);
    }

    /**
     * Returns a new font configuration with the specified underline property, preserving all other properties.
     *
     * <p>This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont original = PotFont.DEFAULT;
     * PotFont notUnderlined = original.underline(false);
     * }</pre>
     *
     * @param underline {@code true} to enable underline styling, {@code false} to disable it
     * @return a new {@code PotFont} instance with the updated underline property
     */
    public PotFont underline(boolean underline) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    /**
     * Returns a new font configuration with the strikethrough property set to {@code true}.
     *
     * <p>This is a convenience method equivalent to {@code strikethrough(true)}.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont struckThrough = PotFont.CODE.strikethrough();
     * }</pre>
     *
     * @return a new {@code PotFont} instance with strikethrough enabled
     * @see #strikethrough(boolean)
     */
    public PotFont strikethrough() {
        return strikethrough(true);
    }

    /**
     * Returns a new font configuration with the specified strikethrough property, preserving all other properties.
     *
     * <p>This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont original = PotFont.BODY;
     * PotFont notStruckThrough = original.strikethrough(false);
     * }</pre>
     *
     * @param strikethrough {@code true} to enable strikethrough styling, {@code false} to disable it
     * @return a new {@code PotFont} instance with the updated strikethrough property
     */
    public PotFont strikethrough(boolean strikethrough) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    /**
     * Returns a new font configuration with the specified text color, preserving all other properties.
     *
     * <p>This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont redTitle = PotFont.TITLE.withColor(PotColor.RED);
     * }</pre>
     *
     * @param color the new text color, or {@code null} to remove any specific color
     * @return a new {@code PotFont} instance with the updated color
     */
    public PotFont withColor(PotColor color) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    /**
     * Returns a new font configuration with the specified text color, preserving all other properties.
     *
     * <p>This method is an alias for {@link #withColor(PotColor)} and is provided for API fluency.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont blueCode = PotFont.CODE.color(PotColor.BLUE);
     * }</pre>
     *
     * @param color the new text color, or {@code null} to remove any specific color
     * @return a new {@code PotFont} instance with the updated color
     * @see #withColor(PotColor)
     */
    public PotFont color(PotColor color) {
        return withColor(color);
    }

    /**
     * Returns a new font configuration with the specified character spacing, preserving all other properties.
     *
     * <p>Character spacing adjusts the horizontal space between characters in points.
     * This method does not modify the current instance.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont spacedFont = PotFont.DEFAULT.withCharacterSpacing(1.5);
     * }</pre>
     *
     * @param spacing the character spacing in points
     * @return a new {@code PotFont} instance with the updated character spacing
     */
    public PotFont withCharacterSpacing(double spacing) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, spacing, language);
    }

    /**
     * Returns a new font configuration with the specified language tag, preserving all other properties.
     *
     * <p>This method does not modify the current instance. The language tag can be used by text
     * layout engines for locale-specific typographic features, such as hyphenation or glyph selection.
     * If the provided language is {@code null}, the resulting font will have no explicit language setting.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotFont japaneseFont = PotFont.BODY.withLanguage("ja-JP");
     * }</pre>
     *
     * @param language the language tag (e.g., "zh-CN", "en-US"), or {@code null} to clear the setting
     * @return a new {@code PotFont} instance with the updated language
     */
    public PotFont withLanguage(String language) {
        return new PotFont(family, size, bold, italic, underline, strikethrough,
                          color, characterSpacing, language);
    }

    // ==================== Getter Methods ====================

    /**
     * Returns the font family name.
     *
     * @return the font family name, never {@code null}
     */
    public String getFamily() {
        return family;
    }

    /**
     * Returns the font size in points.
     *
     * @return the font size in points
     */
    public double getSize() {
        return size;
    }

    /**
     * Returns whether the font is bold.
     *
     * @return {@code true} if the font is bold, {@code false} otherwise
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Returns whether the font is italic.
     *
     * @return {@code true} if the font is italic, {@code false} otherwise
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Returns whether the font is underlined.
     *
     * @return {@code true} if the font is underlined, {@code false} otherwise
     */
    public boolean isUnderline() {
        return underline;
    }

    /**
     * Returns whether the font has strikethrough.
     *
     * @return {@code true} if the font has strikethrough, {@code false} otherwise
     */
    public boolean isStrikethrough() {
        return strikethrough;
    }

    /**
     * Returns the text color.
     *
     * @return the text color, or {@code null} if no explicit color is set
     */
    public PotColor getColor() {
        return color;
    }

    /**
     * Returns the character spacing.
     *
     * @return the character spacing in points, or {@code null} if no explicit spacing is set
     */
    public Double getCharacterSpacing() {
        return characterSpacing;
    }

    /**
     * Returns the language tag.
     *
     * @return the language tag (e.g., "en-US"), or {@code null} if no explicit language is set
     */
    public String getLanguage() {
        return language;
    }

    // ==================== Object Overrides ====================

    /**
     * Compares this font configuration to the specified object for equality.
     *
     * <p>Two {@code PotFont} instances are considered equal if all their property values
     * are equal. This includes family, size, bold, italic, underline, strikethrough, color,
     * character spacing, and language. Floating-point comparison for size uses exact
     * comparison via {@link Double#compare(double, double)}.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotFont potFont = (PotFont) o;
        return Double.compare(potFont.size, size) == 0 &&
               bold == potFont.bold &&
               italic == potFont.italic &&
               underline == potFont.underline &&
               strikethrough == potFont.strikethrough &&
               Objects.equals(family, potFont.family) &&
               Objects.equals(color, potFont.color) &&
               Objects.equals(characterSpacing, potFont.characterSpacing) &&
               Objects.equals(language, potFont.language);
    }

    /**
     * Returns a hash code value for this font configuration.
     *
     * <p>The hash code is computed from all property values.</p>
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(family, size, bold, italic, underline, strikethrough,
                           color, characterSpacing, language);
    }

    /**
     * Returns a string representation of this font configuration.
     *
     * <p>The string includes all non-default property values in a human-readable format.
     * Properties with default values (e.g., {@code false} for bold) are omitted for brevity.</p>
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PotFont{");
        sb.append("family='").append(family).append('\'');
        sb.append(", size=").append(size);
        if (bold) sb.append(", bold");
        if (italic) sb.append(", italic");
        if (underline) sb.append(", underline");
        if (strikethrough) sb.append(", strikethrough");
        if (color != null) sb.append(", color=").append(color);
        if (characterSpacing != null) sb.append(", spacing=").append(characterSpacing);
        sb.append('}');
        return sb.toString();
    }
}
