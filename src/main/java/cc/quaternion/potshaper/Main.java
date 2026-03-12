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
 * The main command-line entry point for the PotShaper library.
 *
 * <p>This class provides a minimal executable target primarily for smoke testing,
 * verifying packaging completeness, and confirming successful library assembly.
 * Its behavior is intentionally trivial to ensure library consumers are not
 * forced into any specific runtime path or dependency when integrating PotShaper
 * as a library component.</p>
 *
 * @author Quaternion8192
 */
public final class Main {

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This class serves as a utility-style entry point holder and is not
     * designed to be instantiated.</p>
     */
    private Main() {
        // Utility entry point holder.
    }

    /**
     * The main method executed when the JAR is run directly.
     *
     * <p>Prints a simple readiness message to standard output. This serves as a
     * basic verification that the library has been packaged correctly and can
     * be executed. No command-line arguments are processed.</p>
     *
     * <h3>Usage Example</h3>
     * <pre>{@code
     * java -jar potshaper.jar
     * }</pre>
     *
     * @param args the command-line arguments (ignored in this implementation)
     */
    public static void main(String[] args) {
        String version = Main.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "1.0.0";
        }

        System.out.println("===========================================================================");
        System.out.println(" ██████╗  ██████╗ ████████╗███████╗██╗  ██╗ █████╗ ██████╗ ███████╗██████╗ ");
        System.out.println(" ██╔══██╗██╔═══██╗╚══██╔══╝██╔════╝██║  ██║██╔══██╗██╔══██╗██╔════╝██╔══██╗");
        System.out.println(" ██████╔╝██║   ██║   ██║   ███████╗███████║███████║██████╔╝█████╗  ██████╔╝");
        System.out.println(" ██╔═══╝ ██║   ██║   ██║   ╚════██║██╔══██║██╔══██╗██╔═══╝ ██╔══╝  ██╔══██╗");
        System.out.println(" ██║     ╚██████╔╝   ██║   ███████║██║  ██║██║  ██║██║     ███████╗██║  ██║");
        System.out.println(" ╚═╝      ╚═════╝    ╚═╝   ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚══════╝╚═╝  ╚═╝");
        System.out.println("===========================================================================");
        System.out.println(" PotShaper v" + version);
        System.out.println(" A fluent Java wrapper for Apache POI");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println(" [NOTICE] This is a Java Library, not a standalone application.");
        System.out.println(" To integrate PotShaper, add the following dependency to your pom.xml:");
        System.out.println();
        System.out.println(" <dependency>");
        System.out.println("     <groupId>cc.quaternion</groupId>");
        System.out.println("     <artifactId>potshaper</artifactId>");
        System.out.println("     <version>" + version + "</version>");
        System.out.println(" </dependency>");
        System.out.println();
        System.out.println(" GitHub: https://github.com/quaternion8192/potshaper");
        System.out.println("===========================================================================");
    }
}