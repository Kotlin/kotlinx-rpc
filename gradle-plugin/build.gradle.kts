/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.conventions.gradle.publish)
    alias(libs.plugins.gradle.kotlin.dsl)
    alias(libs.plugins.gradle.plugin.publish)
}

group = "org.jetbrains.kotlinx"
version = rootProject.libs.versions.kotlinx.rpc.get()

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
}

// This block is needed to show plugin tasks on --dry-run
//  and to not run task actions on ":plugin:task --dry-run".
//  The bug is known since June 2017 and still not fixed.
//  The workaround used below is described here: https://github.com/gradle/gradle/issues/2517#issuecomment-437490287
if (gradle.parent != null && gradle.parent!!.startParameter.isDryRun) {
    gradle.startParameter.isDryRun = true
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "org.jetbrains.kotlinx.rpc.plugin"

            displayName = "kotlinx.rpc Gradle Plugin"
            implementationClass = "kotlinx.rpc.RPCGradlePlugin"
            description = """
                The plugin ensures correct RPC configurations for your project, that will allow proper code generation. 
                Additionally, it applies the kotlinx.rpc BOM file for the project.
            """.trimIndent()
        }
    }

    plugins {
        create("platform") {
            id = "org.jetbrains.kotlinx.rpc.platform"

            displayName = "kotlinx.rpc Platform Plugin"
            implementationClass = "kotlinx.rpc.RPCPlatformPlugin"
            description = """
                Applies the kotlinx.rpc BOM file for the project. 
            """.trimIndent()
        }
    }
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
            package kotlinx.rpc

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
