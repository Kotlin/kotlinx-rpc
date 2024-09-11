/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import groovy.json.JsonSlurper
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
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

private fun isIncluded(targetName: String, kotlinVersion: KotlinVersion, lookupTable: Map<String, String>): Boolean {
    return lookupTable[targetName]?.let { sinceKotlin ->
        sinceKotlin == FULLY_SUPPORTED_TARGET || sinceKotlin.kotlinVersionParsed() <= kotlinVersion
    } ?: false
}

@OptIn(ExperimentalWasmDsl::class)
private fun KotlinMultiplatformExtension.configureTargets(
    targetsLookup: Map<String, String>,
    config: ProjectKotlinConfig,
): List<KotlinTarget> {
    val targets = mutableListOf<KotlinTarget>()

    if (config.native) {
        val nativeLookup = targetsLookup.filterKeys { key ->
            key != "jvm" && key != "js" && !key.startsWith("wasm")
        }

        val nativeTargets = this::class.memberFunctions
            .filter { targetFunction ->
                targetFunction.parameters.size == 1 && isIncluded(
                    targetName = targetFunction.name,
                    kotlinVersion = config.kotlinVersion,
                    lookupTable = nativeLookup,
                )
            }.map { function ->
                function.call(this) as KotlinTarget
            }

        targets.addAll(nativeTargets)

        // TLDR: Default hierarchy template is enabled by default since 1.9.20
        //
        // https://kotlinlang.org/docs/multiplatform-hierarchy.html#default-hierarchy-template
        if (nativeTargets.isNotEmpty() && !config.kotlinVersion.isAtLeast(1, 9, 20)) {
            val commonMain = sourceSets.findByName("commonMain")!!
            val commonTest = sourceSets.findByName("commonTest")!!
            val nativeMain = sourceSets.create("nativeMain")
            val nativeTest = sourceSets.create("nativeTest")

            nativeMain.dependsOn(commonMain)
            nativeTest.dependsOn(commonTest)

            nativeTargets.forEach { target ->
                sourceSets.findByName("${target.name}Main")?.dependsOn(nativeMain)
                sourceSets.findByName("${target.name}Test")?.dependsOn(nativeTest)
            }
        }
    }

    if (config.jvm && isIncluded("jvm", config.kotlinVersion, targetsLookup)) {
        jvm().also { targets.add(it) }
    }

    if (config.js && isIncluded("js", config.kotlinVersion, targetsLookup)) {
        js(IR) {
            nodejs()
            browser()

            binaries.library()
        }.also { targets.add(it) }
    }

    if (config.wasmJs && isIncluded("wasmJs", config.kotlinVersion, targetsLookup)) {
        wasmJs {
            browser()
            nodejs()

            binaries.library()
        }
    }

    if (config.wasmWasi && isIncluded("wasmWasi", config.kotlinVersion, targetsLookup)) {
        wasmWasi {
            nodejs()

            binaries.library()
        }
    }

    targets.forEach { target ->
        target.mavenPublication {
            setPublicArtifactId(config.project)
        }
    }

    return targets
}

private fun Project.configureDetekt(targets: List<KotlinTarget>) {
    val sources = (targets.map { it.name } + "common" + "native").flatMap { name ->
        listOf("src/${name}Main/kotlin", "src/${name}Test/kotlin")
    }

    the<DetektExtension>().source.from(sources)
}

fun ProjectKotlinConfig.configureKotlin(action: Action<KotlinMultiplatformExtension> = Action { }) {
    val lookupTable = loadTargetsSinceKotlinLookupTable(rootProject.rootDir.absolutePath)

    configureJsAndWasmJs()

    kotlin {
        val includedTargets = configureTargets(lookupTable, this@configureKotlin)

        configureDetekt(includedTargets)

        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }

        action.execute(this)
    }
}
