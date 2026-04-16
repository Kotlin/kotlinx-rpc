/*
* Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

@file:Suppress("PropertyName")

import kotlin.text.trim

rootProject.name = "gradle-conventions"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    includeBuild("../gradle-conventions-settings")
}

plugins {
    id("conventions-repositories")
    id("conventions-version-resolution")
}

dependencyResolutionManagement {
    // Additional repositories for build-logic
    @Suppress("UnstableApiUsage")
    repositories {
        gradlePluginPortal()
    }
}

fun rewrite(file: File, markers: Map<String, String>) {
    val lines = file.readLines()
    val newLines = mutableListOf<String>()
    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val key = line.toKey()
        if (key in markers.keys) {
            newLines.add(line)
            newLines.addAll(markers[key]!!.split("\n").filter { it.isNotBlank() })
            i++
            while (i < lines.size) {
                if (lines[i].toKey() == "/$key") {
                    newLines.add(lines[i])
                    i++
                    break
                }
                i++
            }
        } else {
            newLines.add(line)
            i++
        }
    }
    val newContent = newLines.joinToString("\n")
    // Only write if content actually changed — unconditional writeText updates the file timestamp
    // even when content is identical, causing Gradle to recompile convention plugins and invalidate
    // the configuration cache.
    if (file.readText() != newContent) {
        file.writeText(newContent)
    }
}

fun String.toKey(): String = trim().removePrefix("//").trim()

val kotlinVersion: KotlinVersion by extra
val apiValidation_kt = settingsDir
    .resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("util")
    .resolve("apiValidation.kt")

val conventions_kmp = settingsDir
    .resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("conventions-kmp.gradle.kts")

val conventions_jvm = settingsDir
    .resolve("src")
    .resolve("main")
    .resolve("kotlin")
    .resolve("conventions-jvm.gradle.kts")

// TODO KRPC-553: remove apiValidation_kt after 2.3.20, remove everything after 2.4.0
if (kotlinVersion.isAtLeast(2, 4, 0)) {
    rewrite(
        file = apiValidation_kt,
        markers = mapOf(
            "marker-imports" to """
                import org.gradle.api.Project
                import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationExtension
                import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
            """.trimIndent(),
            "marker-configureAbiFilters" to """
                @OptIn(ExperimentalAbiValidation::class)
                fun AbiValidationExtension.configureAbiFilters() {
                    filters {
                        exclude {
                            annotatedWith.add("kotlinx.rpc.internal.utils.InternalRpcApi")
                            byNames.add("kotlinx.rpc.internal.**")
                            byNames.add("kotlinx.rpc.krpc.internal.**")
                            byNames.add("kotlinx.rpc.grpc.internal.**")
                            byNames.add("kotlinx.rpc.protobuf.internal.**")
                        }
                    }
                }
            """.trimIndent()
        )
    )
    rewrite(
        file = conventions_kmp,
        // no trimIndent
        markers = mapOf(
            "marker-abi-kmp" to """
    if (enableAbiValidation) {
        abiValidation {
            configureAbiFilters()
        }
    }
            """
        )
    )
    rewrite(
        file = conventions_jvm,
        // no trimIndent
        markers = mapOf(
            "marker-abi-jvm" to """
    if (enableAbiValidation) {
        abiValidation {
            configureAbiFilters()
        }
    }
            """
        )
    )
} else {
    rewrite(
        file = apiValidation_kt,
        markers = mapOf(
            "marker-imports" to """
                import org.gradle.api.Project
                import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationVariantSpec
                import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
            """.trimIndent(),
            "marker-configureAbiFilters" to """
                @OptIn(ExperimentalAbiValidation::class)
                fun AbiValidationVariantSpec.configureAbiFilters() {
                    filters {
                        @Suppress("DEPRECATION_ERROR") // TODO KRPC-553: temp, remove after update to 2.3.20
                        excluded {
                            annotatedWith.add("kotlinx.rpc.internal.utils.InternalRpcApi")
                            byNames.add("kotlinx.rpc.internal.**")
                            byNames.add("kotlinx.rpc.krpc.internal.**")
                            byNames.add("kotlinx.rpc.grpc.internal.**")
                            byNames.add("kotlinx.rpc.protobuf.internal.**")
                        }
                    }
                }
            """.trimIndent()
        )
    )
    rewrite(
        file = conventions_kmp,
        // no trimIndent
        markers = mapOf(
            "marker-abi-kmp" to """
    abiValidation {
        enabled = enableAbiValidation
        klib {
            enabled = enableAbiValidation
        }
        configureAbiFilters()
    }
            """
        )
    )
    rewrite(
        file = conventions_jvm,
        // no trimIndent
        markers = mapOf(
            "marker-abi-jvm" to """
    abiValidation {
        enabled = enableAbiValidation
        configureAbiFilters()
    }
            """
        )
    )
}
