import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(24)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_22
    }
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.mavenPublishPlugin)
    implementation(libs.dokkaPlugin)
}
