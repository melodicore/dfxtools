plugins {
    id("dfxtools.maven-library-convention")
    id("dfxtools.dokka-convention")
}

version = libs.versions.text.get()

dependencies {
    implementation(project(":configuration"))
    implementation(project(":utils"))
    implementation(libs.logging)
    implementation(libs.bigmath)
    implementation(libs.bigmath.kotlin)
}

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = "DFXTools Text"
        description = "Number formatting and text handling"
    }
}