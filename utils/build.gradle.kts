plugins {
    id("dfxtools.maven-library-convention")
    id("dfxtools.dokka-convention")
}

version = libs.versions.handles.get()

dependencies {
    implementation(project(":configuration"))
    implementation(libs.logging)
    implementation(libs.slf4j.api)
}

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = "DFXTools Utils"
        description = "Utilities used by other DFXTools modules"
    }
}