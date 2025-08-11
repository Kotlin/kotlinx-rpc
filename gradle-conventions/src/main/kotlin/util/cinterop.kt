/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import java.io.File
import kotlin.io.resolve

// works with the cinterop-c Bazel project
fun KotlinMultiplatformExtension.configureCLibCInterop(
    project: Project,
    bazelBuildArg: String,
    configureCinterop: NamedDomainObjectContainer<DefaultCInteropSettings>.(cinteropCLib: File) -> Unit,
) {
    val globalRootDir: String by project.extra

    val cinteropCLib = project.layout.projectDirectory.dir("$globalRootDir/cinterop-c").asFile

    // TODO: Replace function implementation, so it does not use an internal API
    fun findProgram(name: String) = org.gradle.internal.os.OperatingSystem.current().findInPath(name)
    val checkBazel = project.tasks.register("checkBazel") {
        doLast {
            val bazelPath = findProgram("bazel")
            if (bazelPath != null) {
                logger.debug("bazel: {}", bazelPath)
            } else {
                throw GradleException("'bazel' not found on PATH. Please install Bazel (https://bazel.build/).")
            }
        }
    }

    val buildCinteropCLib = project.tasks.register<Exec>("buildCinteropCLib") {
        group = "build"
        workingDir = cinteropCLib
        commandLine("bash", "-c", "bazel build $bazelBuildArg --config=release")
        inputs.files(project.fileTree(cinteropCLib) { exclude("bazel-*/**") })
        outputs.dir(cinteropCLib.resolve("bazel-bin"))

        dependsOn(checkBazel)
    }

    targets.filterIsInstance<KotlinNativeTarget>().forEach {
        it.compilations.getByName("main") {
            cinterops {
                configureCinterop(cinteropCLib)
            }

            cinterops.all {
                val interop = this

                val interopTask = "cinterop${interop.name.capitalized()}${it.targetName.capitalized()}"
                project.tasks.named(interopTask, CInteropProcess::class) {
                    dependsOn(buildCinteropCLib)
                }
            }
        }
    }
}
