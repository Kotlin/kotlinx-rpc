/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import util.kmp
import util.setPublicArtifactId
import kotlin.collections.map

private fun KotlinMultiplatformExtension.configureTargets(config: KmpConfig): List<KotlinTarget> {
    val targets = mutableListOf<KotlinTarget>()

    if (config.native) {
        val nativeTargets = config.nativeTargets(this)
        targets.addAll(nativeTargets)
        config.project.configureNativePublication(nativeTargets)
    }

    if (config.jvm) {
        jvm().also { targets.add(it) }
    }

    if (config.js) {
        js(IR) {
            nodejs()
            if (!config.kotlinMasterBuild) {
                browser()
            }

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

fun KmpConfig.configureKotlinExtension(action: Action<KotlinMultiplatformExtension> = Action { }) {
    kmp {
        val includedTargets = configureTargets(this@configureKotlinExtension)

        configureDetekt(includedTargets)

        action.execute(this)
    }
}
