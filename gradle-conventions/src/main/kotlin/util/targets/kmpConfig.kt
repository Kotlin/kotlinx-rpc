/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.konan.target.HostManager
import util.kotlinVersionParsed
import util.other.optionalProperty
import util.other.optionalPropertyValue
import java.io.File
import kotlin.reflect.full.memberFunctions

private const val UNSUPPORTED_TARGET = "-"
private const val FULLY_SUPPORTED_TARGET = "*"
private const val TARGETS_SINCE_KOTLIN_LOOKUP_PATH = "versions-root/targets-since-kotlin-lookup.json"

private val APPLE_TARGET_PREFIXES = listOf(
    "ios",
    "watchos",
    "tvos",
    "macos",
)

/**
 * In the lookup table:
 * "*" – target supported any Kotlin compiler version
 * "-" – target supported none of Kotlin compiler versions
 * "<some_version>" – target supported since a <some_version> Kotlin version including that version
 */
@Suppress("UNCHECKED_CAST")
private fun loadTargetsSinceKotlinLookupTable(rootDir: String): Map<String, String> {
    val file = File("$rootDir/$TARGETS_SINCE_KOTLIN_LOOKUP_PATH")
    val table = JsonSlurper()
        .parseText(file.readText(Charsets.UTF_8)) as Map<String, String>

    return table.filterValues { value -> value != UNSUPPORTED_TARGET }
}

class KmpConfig(
    project: Project,
    val kotlinVersion: KotlinVersion,
) : Project by project {
    private val targetsLookup by lazy {
        val globalRootDir: String by extra
        loadTargetsSinceKotlinLookupTable(globalRootDir)
    }

    private fun isIncluded(
        targetName: String,
        lookupTable: Map<String, String> = targetsLookup,
    ): Boolean {
        return lookupTable[targetName]?.let { sinceKotlin ->
            sinceKotlin == FULLY_SUPPORTED_TARGET || sinceKotlin.kotlinVersionParsed() <= kotlinVersion
        } ?: false
    }

    private val excludeJvm: Boolean by optionalProperty("exclude")
    private val excludeJs: Boolean by optionalProperty("exclude")
    private val excludeWasmJs: Boolean by optionalProperty("exclude")
    private val excludeWasmJsD8: Boolean by optionalProperty("exclude", "wasmJs")
    private val excludeWasmWasi: Boolean by optionalProperty("exclude")
    val excludeNative: Boolean by optionalProperty("exclude")

    val jvm: Boolean by lazy { !excludeJvm && isIncluded("jvm") }
    val js: Boolean by lazy { !excludeJs && isIncluded("js") }
    val wasmJs: Boolean by lazy { !excludeWasmJs && isIncluded("wasmJs") }
    val wasmJsD8: Boolean by lazy { !excludeWasmJsD8 && wasmJs }
    val wasmWasi: Boolean by lazy { !excludeWasmWasi && isIncluded("wasmWasi") }
    val native = !excludeNative

    private val nativeLookup by lazy {
        targetsLookup.filterKeys { key ->
            key != "jvm" && key != "js" && !key.startsWith("wasm")
        }
    }

    val kotlinMasterBuild by optionalProperty()

    private val isAppleOnLinuxByRequirement: Boolean by lazy {
        // Disabling Apple targets on Linux hosts for grpc:* and protobuf:* modules
        // due to missing native libraries/compilers for these targets on Linux.
        HostManager.hostIsLinux
    }

    fun nativeTargets(kmp: KotlinMultiplatformExtension) = kmp::class.memberFunctions
        .filter { targetFunction ->
            val isApple = APPLE_TARGET_PREFIXES.any { targetFunction.name.startsWith(it) }

            !kotlinMasterBuild &&
                targetFunction.parameters.size == 1 &&
                isIncluded(
                    targetName = targetFunction.name,
                    lookupTable = nativeLookup,
                ) &&
                !optionalPropertyValue(targetFunction.name, "exclude") &&
                !(isAppleOnLinuxByRequirement && isApple)
        }.map { function ->
            function.call(kmp) as KotlinTarget
        }
}

fun Project.withKmpConfig(configure: KmpConfig.() -> Unit) {
    val kotlinVersion: KotlinVersion by extra

    KmpConfig(
        project = project,
        kotlinVersion = kotlinVersion,
    ).configure()
}
