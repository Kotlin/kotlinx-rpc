import java.io.File

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
}

abstract class GeneratePluginVersionTask @Inject constructor(
    @get:Input val pluginVersion: String,
    @get:OutputDirectory val sourcesDir: File
) : DefaultTask() {
    @TaskAction
    fun generate() {
        val sourceFile = File(sourcesDir, "PluginVersion.kt")

        sourceFile.writeText(
            """
            package org.jetbrains.krpc

            const val PLUGIN_VERSION = "$pluginVersion"
            
            """.trimIndent()
        )
    }
}

val sourcesDir = File(project.layout.buildDirectory.asFile.get(), "generated-sources/pluginVersion")

val generatePluginVersionTask =
    tasks.register<GeneratePluginVersionTask>("generatePluginVersion", version.toString(), sourcesDir)

kotlin {
    sourceSets {
        main {
            kotlin.srcDir(generatePluginVersionTask.map { it.sourcesDir })
        }
    }
}
