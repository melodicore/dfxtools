plugins {
    id("dfxtools.maven-library-convention")
}

version = libs.versions.configuration

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = "DFXTools Configuration"
        description = "Cascading configurations with arbitrary types"
    }
}