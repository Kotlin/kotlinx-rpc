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
import java.io.File
import kotlin.reflect.full.memberFunctions

const val UNSUPPORTED_TARGET = "-"
const val FULLY_SUPPORTED_TARGET = "*"
const val TARGETS_SINCE_KOTLIN_LOOKUP_PATH = "gradle/targets-since-kotlin-lookup.json"

/**
 * In the lookup table:
 * "*" - target supported any Kotlin compiler version
 * "-" - target supported  none of Kotlin compiler versions
 * "<some_version>" - target supported since <some_version> kotlin version including that version
 */
@Suppress("UNCHECKED_CAST")
private fun loadTargetsSinceKotlinLookupTable(rootDir: String): Map<String, String> {
    val file = File("$rootDir/$TARGETS_SINCE_KOTLIN_LOOKUP_PATH")
    val table = JsonSlurper()
        .parseText(file.readText(Charsets.UTF_8)) as Map<String, String>

    return table.filterValues { value -> value != UNSUPPORTED_TARGET }
}

private fun isIncluded(targetName: String, kotlinVersion: String, lookupTable: Map<String, String>): Boolean {
    return lookupTable[targetName]?.let { sinceKotlin ->
        sinceKotlin == FULLY_SUPPORTED_TARGET || sinceKotlin <= kotlinVersion
    } ?: false
}

private fun KotlinMultiplatformExtension.configureTargets(
    project: Project,
    kotlinVersion: String,
    targetsLookup: Map<String, String>,
    jvm: Boolean = true,
    js: Boolean = true,
    native: Boolean = true,
): List<KotlinTarget> {
    val targets = mutableListOf<KotlinTarget>()

    if (native) {
        val nativeLookup = targetsLookup.filterKeys { key -> key != "jvm" && key != "js" }

        val nativeTargets = this::class.memberFunctions
            .filter { targetFunction ->
                targetFunction.parameters.size == 1 && isIncluded(targetFunction.name, kotlinVersion, nativeLookup)
            }.map { function ->
                function.call(this) as KotlinTarget
            }

        targets.addAll(nativeTargets)

        // TLDR: Default hierarchy template is enabled by default since 1.9.20
        //
        // https://kotlinlang.org/docs/multiplatform-hierarchy.html#default-hierarchy-template
        if (nativeTargets.isNotEmpty() && kotlinVersion < "1.9.20") {
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

    if (jvm && isIncluded("jvm", kotlinVersion, targetsLookup)) {
        jvm {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(8))
            }
        }.also { targets.add(it) }
    }

    if (js && isIncluded("js", kotlinVersion, targetsLookup)) {
        js(IR) {
            nodejs()
            browser()
        }.also { targets.add(it) }
    }

    targets.forEach { target ->
        target.mavenPublication {
            setPublicArtifactId(project)
        }
    }

    return targets
}

private fun Project.configureDetekt(targets: List<KotlinTarget>) {
    val sources = (targets.map { it.name} + "common" + "native").flatMap { name ->
        listOf("src/${name}Main/kotlin", "src/${name}Test/kotlin")
    }

    the<DetektExtension>().source.from(sources)
}

fun Project.configureKotlin(
    jvm: Boolean = true,
    js: Boolean = true,
    native: Boolean = true,
    action: Action<KotlinMultiplatformExtension> = Action { },
) {
    val kotlinVersion = libs.versions.kotlin.lang.get()
    val lookupTable = loadTargetsSinceKotlinLookupTable(rootProject.rootDir.absolutePath)

    if (js) {
        configureJs()
    }

    kotlin {
        val includedTargets = configureTargets(project, kotlinVersion, lookupTable, jvm, js, native)

        configureDetekt(includedTargets)

        action.execute(this)
    }
}
