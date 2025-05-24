/*
 * Copyright 2025 Lauri "datafox" Heino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

fun Project.dokkaExtraSources(project: Project) {
    val sourceDirectories = project.projectDir.resolve("src/main/kotlin")

    tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask>().configureEach {
        dokkaSourceSets.configureEach {
            // Add sources to be analyzed by Dokka,
            // it will use it for inheriting documentation
            sourceRoots.from(sourceDirectories)

            // Suppress these sources from
            // being generated in Documentation
            suppressedFiles.from(sourceDirectories)
        }
    }
}