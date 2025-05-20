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