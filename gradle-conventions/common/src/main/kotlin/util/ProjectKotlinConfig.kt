/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File
import kotlin.reflect.full.memberFunctions

const val UNSUPPORTED_TARGET = "-"
const val FULLY_SUPPORTED_TARGET = "*"
const val TARGETS_SINCE_KOTLIN_LOOKUP_PATH = "versions-root/targets-since-kotlin-lookup.json"

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

class ProjectKotlinConfig(
    project: Project,
    val kotlinVersion: KotlinVersion,
    jvm: Boolean = true,
    js: Boolean = true,
    wasmJs: Boolean = true,
    wasmJsD8: Boolean = true,
    wasmWasi: Boolean = true,
    val native: Boolean = true,
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

    val jvm: Boolean by lazy { jvm && isIncluded("jvm") }
    val js: Boolean by lazy { js && isIncluded("js") }
    val wasmJs: Boolean by lazy {  wasmJs && isIncluded("wasmJs") }
    val wasmJsD8: Boolean by lazy {  wasmJsD8 && wasmJs }
    val wasmWasi: Boolean by lazy {  wasmWasi && isIncluded("wasmWasi") }

    private val nativeLookup by lazy {
        targetsLookup.filterKeys { key ->
            key != "jvm" && key != "js" && !key.startsWith("wasm")
        }
    }

    val kotlinMasterBuild by optionalProperty()

    fun nativeTargets(kmp: KotlinMultiplatformExtension) = kmp::class.memberFunctions
        .filter { targetFunction ->
            !kotlinMasterBuild && targetFunction.parameters.size == 1 && isIncluded(
                targetName = targetFunction.name,
                lookupTable = nativeLookup,
            )
        }.map { function ->
            function.call(kmp) as KotlinTarget
        }
}

fun Project.withKotlinConfig(configure: ProjectKotlinConfig.() -> Unit) {
    val kotlinVersion: KotlinVersion by extra
    val excludeJvm: Boolean by optionalProperty()
    val excludeJs: Boolean by optionalProperty()
    val excludeWasmJs: Boolean by optionalProperty()
    val excludeWasmJsD8: Boolean by optionalProperty()
    val excludeWasmWasi: Boolean by optionalProperty()
    val excludeNative: Boolean by optionalProperty()

    ProjectKotlinConfig(
        project = project,
        kotlinVersion = kotlinVersion,
        jvm = !excludeJvm,
        js = !excludeJs,
        wasmJs = !excludeWasmJs,
        wasmJsD8 = !excludeWasmJsD8,
        wasmWasi = !excludeWasmWasi,
        native = !excludeNative,
    ).configure()
}
