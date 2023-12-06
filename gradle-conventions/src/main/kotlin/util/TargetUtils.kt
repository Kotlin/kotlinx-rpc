package util

import groovy.json.JsonSlurper
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
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
    kotlinVersion: String,
    targetsLookup: Map<String, String>,
    jvm: Boolean = true,
    js: Boolean = true,
    native: Boolean = true,
) {
    if (native) {
        val nativeLookup = targetsLookup.filterKeys { key -> key != "jvm" && key != "js" }

        val nativeTargets = this::class.memberFunctions
            .filter { targetFunction ->
                targetFunction.parameters.size == 1 && isIncluded(targetFunction.name, kotlinVersion, nativeLookup)
            }.map { function ->
                function.call(this) as KotlinTarget
            }

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
        }
    }

    if (js && isIncluded("js", kotlinVersion, targetsLookup)) {
        js(IR) {
            nodejs()
            browser()
        }
    }
}

fun Project.configureKotlin(
    jvm: Boolean = true,
    js: Boolean = true,
    native: Boolean = true,
    action: Action<KotlinMultiplatformExtension> = Action { },
) {
    val kotlinVersion = getKotlinPluginVersion()
    val lookupTable = loadTargetsSinceKotlinLookupTable(rootProject.rootDir.absolutePath)

    if (js) {
        configureJs()
    }

    kotlin {
        configureTargets(kotlinVersion, lookupTable, jvm, js, native)

        action.execute(this)
    }
}
