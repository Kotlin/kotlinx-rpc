/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.Directory
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

    targets.withType<KotlinNativeTarget>().configureEach {
        // the name used for the static library files (e.g. iosSimulatorArm64 -> ios_simulator_arm64)
        val platformShortName = konanTarget.visibleName
        val fileName = "lib$buildTargetName.$platformShortName.a"
        val outFile = project.bazelBuildDir.file("$buildTargetName/$fileName")

        val buildCinteropCLib = project.tasks.registerBuildClibTask(
            name = "build${canonicalCLibTaskPostfix(buildTargetName)}",
            bazelTask = bazelTask,
            outFile = outFile.asFile,
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
 * Creates tasks to build a C library dependency and places it to
 * the `prebuilt-deps` directory in the cinterop-c Bazel project.
 *
 * It also creates tasks to compute the SHA256 checksum of the library and
 * extract the include directory.
 *
 * @param project The Gradle project in which this configuration is applied.
 * @param bazelTask The Bazel task responsible for building the C library.
 */
fun KotlinMultiplatformExtension.configureCLibDependency(
    project: Project,
    bazelTask: String,
    bazelExtractIncludeTask: String? = null,
) {
    val buildTargetName = bazelTask.split(":").last()
    val prebuiltLibDir = project.cLibPrebuiltDepsDir.resolve(buildTargetName)

    targets.withType<KotlinNativeTarget>().configureEach {
        val platformShortName = konanTarget.visibleName
        val fileName = "lib$buildTargetName.$platformShortName.a"
        val staticLibFile = prebuiltLibDir.resolve(fileName)

        project.tasks.registerBuildClibTask(
            name = "buildDependency${canonicalCLibTaskPostfix(buildTargetName)}",
            bazelTask = bazelTask,
            outFile = staticLibFile,
            target = this,
        )

        project.tasks.register<Exec>("computeSha256${canonicalCLibTaskPostfix(buildTargetName)}") {
            val shaFile = prebuiltLibDir.resolve("$fileName.sha256")
            workingDir = prebuiltLibDir
            commandLine("bash", "-c", "sha256sum $staticLibFile | awk '{print $1}' > $shaFile")
            inputs.files(staticLibFile)
            outputs.file(shaFile)
        }
    }

    if (bazelExtractIncludeTask != null) {
        project.tasks.register<Exec>("buildIncludeDirCLib${buildTargetName.capitalized()}") {
            dependsOn(":checkBazel")
            group = "build"
            workingDir = project.cinteropLibDir
            commandLine(
                "bash",
                "-c",
                "./extract_include_dir.sh //prebuilt-deps/grpc_fat:grpc_include_dir $prebuiltLibDir"
            )
            outputs.dir(prebuiltLibDir.resolve("include"))
        }
    }

    project.tasks.register("buildDependencyAllTargetsForCLib${buildTargetName.capitalized()}") {
        group = "build"
        targets.withType<KotlinNativeTarget>().forEach { target ->
            dependsOn("buildDependency${target.canonicalCLibTaskPostfix(buildTargetName)}")
        }
    }

    project.tasks.register("computeSha256AllTargetsForCLib${buildTargetName.capitalized()}") {
        targets.withType<KotlinNativeTarget>().forEach { target ->
            dependsOn("computeSha256${target.canonicalCLibTaskPostfix(buildTargetName)}")
        }
    }
}

private val Project.cLibPrebuiltDepsDir: File get() = cinteropLibDir.resolve("prebuilt-deps")

private val Project.bazelBuildDir: Directory
    get() = layout.buildDirectory.dir("bazel-out").get()


private fun KotlinNativeTarget.canonicalCLibTaskPostfix(buildTargetName: String): String {
    return "CLib${buildTargetName.capitalized()}_$targetName"
}

private fun TaskContainer.registerBuildClibTask(
    name: String,
    bazelTask: String,
    outFile: File,
    target: KotlinNativeTarget,
): TaskProvider<Exec> {
    return register<Exec>(name) {
        val platform = target.bazelPlatformName
        val os = target.bazelOsName

        group = "build"
        workingDir = project.cinteropLibDir
        commandLine("bash", "-c", "./build_target.sh $bazelTask $outFile $platform $os")
        inputs.files(project.fileTree(project.cinteropLibDir) { exclude("bazel-*/**", "prebuilt-deps/**") })
        outputs.files(outFile)

        dependsOn(":checkBazel")
    }
}

private val Project.cinteropLibDir: File
    get() {
        val globalRootDir: String by project.extra
        return layout.projectDirectory.dir("$globalRootDir/cinterop-c").asFile.absoluteFile
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
