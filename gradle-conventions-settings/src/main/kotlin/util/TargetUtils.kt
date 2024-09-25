/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

private fun KotlinMultiplatformExtension.configureTargets(config: ProjectKotlinConfig): List<KotlinTarget> {
    val targets = mutableListOf<KotlinTarget>()

    if (config.native) {
        val nativeTargets = config.nativeTargets(this)
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

    if (config.jvm) {
        jvm().also { targets.add(it) }
    }

    if (config.js) {
        js(IR) {
            nodejs()
            browser()

            binaries.library()
        }.also { targets.add(it) }
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
    kotlin {
        val includedTargets = configureTargets(this@configureKotlin)

        configureDetekt(includedTargets)

        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }

        action.execute(this)
    }
}
