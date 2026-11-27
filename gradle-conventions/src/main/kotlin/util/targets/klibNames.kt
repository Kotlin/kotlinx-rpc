/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeCompilerOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import util.withRpcArtifactPrefix

internal fun KotlinMultiplatformExtension.configureKlibNames(project: Project) {
    val libraryName = "${project.group}:${project.name.withRpcArtifactPrefix()}"

    targets.configureEach {
        if (platformType == KotlinPlatformType.common) {
            compilations.configureEach {
                val metadataName = "${libraryName}_$name"
                compileTaskProvider.configure {
                    @Suppress("DEPRECATION")
                    when (this) {
                        is KotlinCompileCommon -> moduleName.set(metadataName)
                        is KotlinNativeCompile -> compilerOptions.moduleName.set(metadataName)
                    }
                }
            }
        } else if (platformType in klibPlatforms) {
            compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME) {
                compileTaskProvider.configure {
                    when (val options = compilerOptions) {
                        is KotlinNativeCompilerOptions -> options.moduleName.set(libraryName)
                        is KotlinJsCompilerOptions -> options.freeCompilerArgs.add("-Xir-module-name=$libraryName")
                    }
                }
            }
        }
    }
}

private val klibPlatforms = setOf(KotlinPlatformType.native, KotlinPlatformType.js, KotlinPlatformType.wasm)
