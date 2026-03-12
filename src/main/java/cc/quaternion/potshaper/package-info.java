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

/**
 * PotShaper is a fluent Java wrapper for Apache POI that reshapes chaotic
 * PowerPoint elements with the intuitive ease of Swing/JFrame.
 * * <p>
 * This package provides a high-level API to create, modify, and export
 * PowerPoint presentations without dealing with the complex underlying XML
 * structures of Apache POI.
 * </p>
 *
 * <h3>Quick Start</h3>
 * <pre>{@code
 * // Example: Creating a simple presentation
 * PotPresentation pres = PotPresentation.create();
 * PotSlide slide = pres.addSlide();
 * slide.addTextBox("Hello PotShaper!", 100, 100, 200, 50);
 * pres.save("output.pptx");
 * }</pre>
 *
 * @author Quaternion8192
 * @since 1.0.0
 */
package cc.quaternion.potshaper;
