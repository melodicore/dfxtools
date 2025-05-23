import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
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
