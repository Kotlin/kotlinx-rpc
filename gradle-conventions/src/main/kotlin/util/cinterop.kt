/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import util.other.libs
import java.io.File

// works with the cinterop-c Bazel project
fun KotlinMultiplatformExtension.configureCLibCInterop(
    project: Project,
    bazelTask: String,
    cinteropTaskDependsOn: List<Any> = emptyList(),
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
                    cinteropTaskDependsOn.forEach { dependsOn(it) }
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

fun Project.registerBuildCLibIncludeDirTask(
    bazelTask: String,
    bazelExtractIncludeOutputDir: Provider<Directory>,
): TaskProvider<Exec> {
    val buildTargetName = bazelTask.split(":").last()
    val includeDir = bazelExtractIncludeOutputDir.get().asFile
    return project.tasks.register<Exec>("buildIncludeDirCLib${buildTargetName.capitalized()}") {
        dependsOn(":checkBazel")
        group = "build"
        workingDir = project.cinteropLibDir
        val konanHome = findKonanHome()
        commandLine(
            "bash",
            "-c",
            "./extract_include_dir.sh //prebuilt-deps/grpc_fat:grpc_include_dir $includeDir $konanHome"
        )
        outputs.dir(includeDir.resolve("include"))
    }
}


private val Project.cLibPrebuiltDepsDir: File get() = cinteropLibDir.resolve("prebuilt-deps")

private val Project.bazelBuildDir: Directory
    get() = layout.buildDirectory.dir("bazel-out").get()


private fun KotlinNativeTarget.canonicalCLibTaskPostfix(buildTargetName: String): String {
    return "CLib${buildTargetName.capitalized()}_$targetName"
}

private val TaskContainer.downloadKotlinNativeDistribution: TaskProvider<Task?>
    get() = named("downloadKotlinNativeDistribution")

private fun TaskContainer.registerBuildClibTask(
    name: String,
    bazelTask: String,
    outFile: File,
    target: KotlinNativeTarget,
): TaskProvider<Exec> {
    return register<Exec>(name) {
        dependsOn(downloadKotlinNativeDistribution)

        val konanTarget = target.konanTarget.visibleName
        val konanHome = project.findKonanHome()

        group = "build"
        workingDir = project.cinteropLibDir
        commandLine("bash", "-c", "./build_target.sh $bazelTask $outFile $konanTarget $konanHome")
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

private fun Project.findKonanHome(): String {
    val userHome = System.getProperty("user.home")
    val osName = System.getProperty("os.name").lowercase()
    val hostOs = when {
        osName.contains("mac") -> "macos"
        osName.contains("linux") -> "linux"
        osName.contains("windows") -> "windows"
        else -> error("Unsupported OS: $osName")
    }
    val hostArch = System.getProperty("os.arch").lowercase().let {
        when {
            it.contains("aarch64") || it.contains("arm64") -> "aarch64"
            it.contains("x86_64") || it.contains("amd64") -> "x86_64"
            else -> error("Unsupported arch: $it")
        }
    }
    val kotlinVersion = project.libs.versions.kotlin.compiler.get()
    return "$userHome/.konan/kotlin-native-prebuilt-$hostOs-$hostArch-$kotlinVersion"
}