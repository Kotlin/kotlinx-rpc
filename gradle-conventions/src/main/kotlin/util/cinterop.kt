/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import util.other.localProperties
import util.tasks.BazelBuildTask
import util.tasks.PublishCLibDependencyTask
import java.io.File

// works with the cinterop-c Bazel project
fun KotlinMultiplatformExtension.configureCLibCInterop(
    project: Project,
    bazelTask: String,
    configureCinterop: NamedDomainObjectContainer<DefaultCInteropSettings>.(cLibSource: File, cLibOutDir: File) -> Unit,
) {
    val buildTargetName = bazelTask.split(":").last()
    val cLibSource = project.cinteropLibDir
    val cLibOutDir = project.bazelBuildDir.dir(buildTargetName).asFile


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
        // the name used for the static library files (e.g. iosSimulatorArm64 -> ios_simulator_arm64)
        val platformShortName = konanTarget.visibleName
        val fileName = "lib$buildTargetName.$platformShortName.a"
        val outFile = project.bazelBuildDir.file("$buildTargetName/$fileName")

        val buildCinteropCLib = project.tasks.registerBuildClibTask(
            name = "build${canonicalCLibTaskPostfix(buildTargetName)}",
            bazelTask = bazelTask,
            outFile = outFile,
            target = this,
        )

        // cinterop klib build config
        compilations.getByName("main") {
            cinterops {
                configureCinterop(cLibSource, cLibOutDir)
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
 * Creates tasks to build and publish a C library dependency on the
 * [Jetbrains Space package repository](https://jetbrains.team/p/krpc/packages/files/bazel-build-deps).
 *
 * @param project The Gradle project in which this configuration is applied.
 * @param bazelTask The Bazel task responsible for building the C library.
 * @param dependencyVersion The version of the C library dependency to be published.
 * @param dependencyNamespace The namespace under which the C library dependency will be published.
 */
fun KotlinMultiplatformExtension.configureCLibDependency(
    project: Project,
    bazelTask: String,
    bazelIncludeZipTask: String,
    dependencyVersion: String,
    dependencyNamespace: String,
) {
    val cinteropCLib = project.cinteropLibDir
    val buildTargetName = bazelTask.split(":").last()

    targets.withType<KotlinNativeTarget>().configureEach {
        val platformShortName = konanTarget.visibleName
        val fileName = "lib$buildTargetName.$platformShortName.a"

        val buildCLib = project.tasks.registerBuildClibTask(
            name = "build${canonicalCLibTaskPostfix(buildTargetName)}",
            bazelTask = bazelTask,
            outFile = project.bazelBuildDir.file("${buildTargetName}/$fileName"),
            target = this,
        )

        val publishTaskName = "publish${canonicalCLibTaskPostfix(buildTargetName)}"
        project.tasks.register<PublishCLibDependencyTask>(publishTaskName) {
            dependsOn(buildCLib)

            file = buildCLib.get().outputs.files.singleFile
            namespace = dependencyNamespace
            version = dependencyVersion
            artifactName = fileName
            token = project.localProperties().getProperty("kotlinx.rpc.team.space.packages.token")
        }
    }

    val bundleInclude = project.tasks.register<BazelBuildTask>("bundleIncludeZip${buildTargetName.capitalized()}") {
        bazelProjectDir = cinteropCLib
        bazelBuildTask = bazelIncludeZipTask
        outputFile = project.bazelBuildDir.file("${buildTargetName}/include.zip").asFile
    }

    val publishInclude =
        project.tasks.register<PublishCLibDependencyTask>("publishIncludeZip${buildTargetName.capitalized()}") {
            dependsOn(bundleInclude)
            file = bundleInclude.get().outputFile
            namespace = dependencyNamespace
            version = dependencyVersion
            artifactName = "include.zip"
            token = project.localProperties().getProperty("kotlinx.rpc.team.space.packages.token")
        }

    project.tasks.register("publishAllTargetsForCLib${buildTargetName.capitalized()}ToSpace") {
        group = "publishing"
        dependsOn(publishInclude)
        targets.withType<KotlinNativeTarget>().forEach { target ->
            dependsOn("publish${target.canonicalCLibTaskPostfix(buildTargetName)}")
        }
    }
}

private val Project.bazelBuildDir: Directory
    get() = layout.buildDirectory.dir("bazel-out").get()


private fun KotlinNativeTarget.canonicalCLibTaskPostfix(buildTargetName: String): String {
    return "CLib${buildTargetName.capitalized()}_$targetName"
}

private fun TaskContainer.registerBuildClibTask(
    name: String,
    bazelTask: String,
    outFile: RegularFile,
    target: KotlinNativeTarget,
): TaskProvider<Exec> {
    return register<Exec>(name) {
        val platform = target.bazelPlatformName
        val os = target.bazelOsName

        group = "build"
        workingDir = project.cinteropLibDir
        commandLine("bash", "-c", "./build_target.sh $bazelTask $outFile $platform $os")
        inputs.files(project.fileTree(project.cinteropLibDir) { exclude("bazel-*/**", "out/**") })
        outputs.files(outFile)

        // TODO: how to register the task idempotently?
        // dependsOn(checkBazel)
    }
}

private val Project.cinteropLibDir: File
    get() {
        val globalRootDir: String by project.extra
        return layout.projectDirectory.dir("$globalRootDir/cinterop-c").asFile
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
