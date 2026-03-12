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
 * Enumerates the types of actions that can be associated with a shape or object in a presentation.
 *
 * <p>Each constant represents a specific interactive command, such as navigating slides,
 * executing external programs, or controlling multimedia. The internal string value
 * corresponds to a standard identifier used within presentation file formats.
 *
 * @author Quaternion8192
 * @since 1.0
 */
public enum PotActionType {

    /**
     * Action to navigate to a specific slide.
     */
    GO_TO_SLIDE("goToSlide"),

    /**
     * Action to navigate to the first slide of the presentation.
     */
    GO_TO_FIRST("goToFirst"),

    /**
     * Action to navigate to the last slide of the presentation.
     */
    GO_TO_LAST("goToLast"),

    /**
     * Action to navigate to the next slide in sequence.
     */
    GO_TO_NEXT("goToNext"),

    /**
     * Action to navigate to the previous slide in sequence.
     */
    GO_TO_PREVIOUS("goToPrevious"),

    /**
     * Action to end the slideshow presentation.
     */
    END_SHOW("endShow"),

    /**
     * Action to open a hyperlink to a web URL or document.
     */
    HYPERLINK("hyperlink"),

    /**
     * Action to launch an external program or executable.
     */
    RUN_PROGRAM("runProgram"),

    /**
     * Action to execute a macro script.
     */
    RUN_MACRO("runMacro"),

    /**
     * Action to start playing an audio file.
     */
    PLAY_SOUND("playSound"),

    /**
     * Action to stop any currently playing audio.
     */
    STOP_SOUND("stopSound"),

    /**
     * Action to activate an embedded OLE (Object Linking and Embedding) object.
     */
    OLE_ACTIVATE("oleActivate"),

    /**
     * Action to open an embedded OLE object for editing.
     */
    OLE_EDIT("oleEdit"),

    /**
     * Action to open an embedded OLE object in its native application.
     */
    OLE_OPEN("oleOpen");

    private final String value;

    /**
     * Constructs an action type with its associated string identifier.
     *
     * @param value the internal string identifier for this action type
     */
    PotActionType(String value) {
        this.value = value;
    }

    /**
     * Returns the internal string identifier for this action type.
     *
     * <p>The returned value is the canonical name used in serialization and
     * matching operations.
     *
     * @return the string identifier for this action type, never {@code null}
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the {@code PotActionType} constant corresponding to the given string identifier.
     *
     * <p>The lookup is performed by comparing the provided string with the
     * internal {@code value} of each enum constant. The comparison is case-sensitive.
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * PotActionType type = PotActionType.fromValue("goToSlide");
     * // type == PotActionType.GO_TO_SLIDE
     * }</pre>
     *
     * @param value the string identifier to look up
     * @return the matching {@code PotActionType} constant
     * @throws IllegalArgumentException if no enum constant has a matching {@code value}
     * @see #getValue()
     */
    public static PotActionType fromValue(String value) {
        for (PotActionType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown action type: " + value);
    }
}