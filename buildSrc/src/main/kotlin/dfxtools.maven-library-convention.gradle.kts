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

dependencies {
    testImplementation(kotlin("test"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}

tasks.jar {
    manifest {
        val moduleName = project.name
        val moduleVersion = project.version
        attributes(
            "Implementation-Title" to moduleName,
            "Implementation-Version" to moduleVersion,
            "Automatic-Module-Name" to "dfxtools.$moduleName"
        )
    }
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
    //Bouncy Castle is not working properly
    useGpgCmd()
}