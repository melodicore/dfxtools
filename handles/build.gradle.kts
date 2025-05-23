plugins {
    id("dfxtools.maven-library-convention")
    id("dfxtools.dokka-convention")
}

version = libs.versions.handles.get()

dependencies {
    implementation(project(":utils"))
    implementation(libs.logging)
    implementation(libs.slf4j.api)
    testRuntimeClasspath(libs.log4j)
}

dokkaExtraSources(project(":utils"))

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = "DFXTools Handles"
        description = "Dynamic enum-like values to use for identification, such as map keys, and tools to manipulate them"
    }
}