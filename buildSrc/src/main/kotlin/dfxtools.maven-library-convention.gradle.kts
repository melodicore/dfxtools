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

import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    `java-library`
    signing
    id("com.vanniktech.maven.publish")
}

group = "me.datafox.dfxtools"

dependencies { testImplementation(kotlin("test")) }

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(24)
    compilerOptions { jvmTarget = JvmTarget.JVM_11 }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging { events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED) }
}

tasks.jar {
    manifest {
        val moduleName = project.name
        val moduleVersion = project.version
        attributes(
            "Implementation-Title" to moduleName,
            "Implementation-Version" to moduleVersion,
            "Automatic-Module-Name" to "dfxtools.$moduleName",
        )
    }
}

tasks.register("copyLicense", Copy::class) {
    from(rootDir) {
        include("LICENSE")
        rename { "$it.txt" }
    }
    into(projectDir.resolve("src/main/resources/META-INF"))
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        url = "https://tools.datafox.me"
        scm {
            connection = "scm:git:git://git.datafox.me/datafox/dfxtools.git"
            developerConnection = "scm:git:ssh://git.datafox.me/datafox/dfxtools.git"
            url = "https://git.datafox.me/datafox/dfxtools"
        }
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "datafox"
                name = "Lauri \"datafox\" Heino"
                email = "datafox@datafox.me"
            }
        }
    }
}

signing {
    // Bouncy Castle is not working properly
    useGpgCmd()
}
