plugins {
    id("dfxtools.maven-library-convention")
    id("dfxtools.dokka-convention")
}

version = libs.versions.handles.get()

dependencies {
    implementation(project(":utils"))
}

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = "DFXTools Invalidation"
        description = "Dynamic enum-like values to use for identification, such as map keys, and tools to manipulate them"
    }
}