import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("org.jetbrains.dokka")
}

dokka {
    moduleName = project.name.capitalized()

    dokkaSourceSets.all {
        includes.from("README.md")
    }
}