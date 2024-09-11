/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate

class ProjectKotlinConfig(
    project: Project,
    val kotlinVersion: KotlinVersion,
    val jvm: Boolean = true,
    val js: Boolean = true,
    val wasmJs: Boolean = true,
    val wasmWasi: Boolean = true,
    val native: Boolean = true,
) : Project by project

fun Project.withKotlinConfig(configure: ProjectKotlinConfig.() -> Unit) {
    val kotlinVersion: KotlinVersion by extra
    val excludeJvm: Boolean by optionalProperty()
    val excludeJs: Boolean by optionalProperty()
    val excludeWasmJs: Boolean by optionalProperty()
    val excludeWasmWasi: Boolean by optionalProperty()
    val excludeNative: Boolean by optionalProperty()

    ProjectKotlinConfig(
        project = project,
        kotlinVersion = kotlinVersion,
        jvm = !excludeJvm,
        js = !excludeJs,
        wasmJs = !excludeWasmJs,
        wasmWasi = !excludeWasmWasi,
        native = !excludeNative,
    ).configure()
}
