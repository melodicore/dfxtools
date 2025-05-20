dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "dfxtools"
include("configuration")
include("docs")
include("handles")
include("invalidation")
include("text")
include("utils")
include("values")