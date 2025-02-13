/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */


plugins {
    `kotlin-dsl`
    alias(libs.plugins.conventions.jvm)
    alias(libs.plugins.conventions.gradle.publish)
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.conventions.gradle.doctor)
}

group = "org.jetbrains.kotlinx"
version = rootProject.libs.versions.kotlinx.rpc.get()

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.protobuf.gradle.plugin)
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
            implementationClass = "kotlinx.rpc.RpcGradlePlugin"
            description = """
                The plugin ensures correct RPC configurations for your project, that will allow proper code generation. 
            """.trimIndent()
        }
    }
}

abstract class GeneratePluginVersionTask @Inject constructor(
    @get:Input val libraryVersion: String,
    @get:Input val protobufVersion: String,
    @get:Input val grpcVersion: String,
    @get:Input val grpcKotlinVersion: String,
    @get:OutputDirectory val sourcesDir: File
) : DefaultTask() {
    @TaskAction
    fun generate() {
        val sourceFile = File(sourcesDir, "Versions.kt")

        sourceFile.writeText(
            """
            package kotlinx.rpc

            public const val LIBRARY_VERSION: String = "$libraryVersion"

            @Deprecated("Use kotlinx.rpc.LIBRARY_VERSION instead", ReplaceWith("kotlinx.rpc.LIBRARY_VERSION"))
            public const val PLUGIN_VERSION: String = LIBRARY_VERSION

            public const val PROTOBUF_VERSION: String = "$protobufVersion"
            public const val GRPC_VERSION: String = "$grpcVersion"
            public const val GRPC_KOTLIN_VERSION: String = "$grpcKotlinVersion"
            
            """.trimIndent()
        )
    }
}

val sourcesDir = File(project.layout.buildDirectory.asFile.get(), "generated-sources/pluginVersion")

val generatePluginVersionTask = tasks.register<GeneratePluginVersionTask>(
    "generatePluginVersion",
    version.toString(),
    libs.versions.protobuf.asProvider().get().toString(),
    libs.versions.grpc.asProvider().get().toString(),
    libs.versions.grpc.kotlin.get().toString(),
    sourcesDir,
)

kotlin {
    sourceSets {
        main {
            kotlin.srcDir(generatePluginVersionTask.map { it.sourcesDir })
        }
    }
}

println("[Gradle Plugin] kotlinx.rpc project version: $version")
