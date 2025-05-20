plugins {
    id("dfxtools.maven-library-convention")
    id("dfxtools.dokka-convention")
}

version = libs.versions.utils.get()

dependencies {
    implementation(libs.logging)
}

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = "DFXTools Utils"
        description = "Utilities used by other DFXTools modules"
    }
}