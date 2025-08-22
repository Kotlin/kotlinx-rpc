/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File

// works with the cinterop-c Bazel project
fun KotlinMultiplatformExtension.configureCLibCInterop(
    project: Project,
    bazelTask: String,
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

    targets.withType<KotlinNativeTarget>().configureEach {
        val buildTargetName = bazelTask.split(":").last()
        // bazel library build task
        val taskName = "buildCLib${buildTargetName.capitalized()}_$targetName"
        val buildCinteropCLib = project.tasks.register<Exec>(taskName) {
            val platform = bazelPlatformName
            val os = bazelOsName

            // the name used for the static library files (e.g. iosSimulatorArm64 -> ios_simulator_arm64)
            val platformShortName = konanTarget.visibleName
            val fileName = "lib$buildTargetName.$platformShortName.a"
            val outFile = cinteropCLib.resolve("out").resolve(fileName)

            group = "build"
            workingDir = cinteropCLib
            commandLine("bash", "-c", "./build_target.sh $bazelTask $outFile $platform $os")
            inputs.files(project.fileTree(cinteropCLib) { exclude("bazel-*/**", "out/**") })
            outputs.files(outFile)

            dependsOn(checkBazel)
        }

        // cinterop klib build config
        compilations.getByName("main") {
            cinterops {
                configureCinterop(cinteropCLib)
            }

            cinterops.all {
                val interop = this

                val interopTask = "cinterop${interop.name.capitalized()}${this@configureEach.targetName.capitalized()}"
                project.tasks.named(interopTask, CInteropProcess::class) {
                    dependsOn(buildCinteropCLib)
                }
            }
        }
    }
}

/**
 * Returns the Bazel platform name for the given [KotlinNativeTarget].
 *
 * For Apple targets, compare the following two lists:
 * - https://kotlinlang.org/docs/native-target-support.html
 * - https://github.com/bazelbuild/apple_support/blob/master/configs/platforms.bzl
 */
private val KotlinNativeTarget.bazelPlatformName: String
    get() {
        val appleSupport = "@build_bazel_apple_support//platforms"
        return when (konanTarget) {
            KonanTarget.MACOS_ARM64 -> "$appleSupport:macos_arm64"
            KonanTarget.MACOS_X64 -> "$appleSupport:macos_x86_64"
            KonanTarget.IOS_ARM64 -> "$appleSupport:ios_arm64"
            KonanTarget.IOS_SIMULATOR_ARM64 -> "$appleSupport:ios_sim_arm64"
            KonanTarget.IOS_X64 -> "$appleSupport:ios_x86_64"
            KonanTarget.WATCHOS_ARM32 -> "$appleSupport:watchos_armv7k"
            // WATCHOS_ARM64 is the "older" arm64_32 target, not arm64 (which is WATCH_DEVICE_ARM64)
            KonanTarget.WATCHOS_ARM64 -> "$appleSupport:watchos_arm64_32"
            KonanTarget.WATCHOS_DEVICE_ARM64 -> "$appleSupport:watchos_device_arm64"
            KonanTarget.WATCHOS_SIMULATOR_ARM64 -> "$appleSupport:watchos_arm64"
            KonanTarget.WATCHOS_X64 -> "$appleSupport:watchos_x86_64"
            KonanTarget.TVOS_ARM64 -> "$appleSupport:tvos_arm64"
            KonanTarget.TVOS_SIMULATOR_ARM64 -> "$appleSupport:tvos_sim_arm64"
            KonanTarget.TVOS_X64 -> "$appleSupport:tvos_x86_64"
            KonanTarget.LINUX_ARM32_HFP -> TODO()
            KonanTarget.LINUX_ARM64 -> TODO()
            KonanTarget.LINUX_X64 -> TODO()
            KonanTarget.ANDROID_ARM32 -> TODO()
            KonanTarget.ANDROID_ARM64 -> TODO()
            KonanTarget.ANDROID_X64 -> TODO()
            KonanTarget.ANDROID_X86 -> TODO()
            KonanTarget.MINGW_X64 -> TODO()
        }
    }

private val KotlinNativeTarget.bazelOsName
    get() = when (konanTarget.family) {
        Family.OSX -> "macos"
        Family.IOS -> "ios"
        Family.TVOS -> "tvos"
        Family.WATCHOS -> "watchos"
        else -> TODO()
    }
