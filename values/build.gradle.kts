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

plugins {
    id("dfxtools.maven-library-convention")
    id("dfxtools.dokka-convention")
}

version = libs.versions.values.get()

dependencies {
    implementation(project(":handles"))
    implementation(project(":invalidation"))
    implementation(project(":utils"))
    implementation(libs.logging)
    implementation(libs.slf4j.api)
    compileClasspath(libs.kotlin.reflect)
    testRuntimeClasspath(libs.log4j)
}

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = "DFXTools Values"
        description = "Mutable numbers with an extensible modifier system"
    }
}