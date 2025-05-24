plugins {
    id("org.jetbrains.dokka")
}

dokka {
    moduleName = "DFXTools"
}

dependencies {
    dokka(project(":configuration"))
    dokka(project(":handles"))
    dokka(project(":utils"))
}