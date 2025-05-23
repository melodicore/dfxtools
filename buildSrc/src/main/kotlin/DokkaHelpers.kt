
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

fun Project.dokkaExtraSources(project: Project) {
    val sourceDirectories = project.projectDir.resolve("src/main/kotlin")

    tasks.withType<org.jetbrains.dokka.gradle.AbstractDokkaLeafTask>().configureEach {
        dokkaSourceSets.configureEach {
            // Add sources to be analyzed by Dokka,
            // it will use it for inheriting documentation
            sourceRoots.from(sourceDirectories)

            // Suppress these sources from
            // being generated in Documentation
            suppressedFiles.from(sourceDirectories)
        }
    }
}