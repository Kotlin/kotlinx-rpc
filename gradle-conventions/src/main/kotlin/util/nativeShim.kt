/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import java.io.File

/**
 * Shared build-time handles for one shim module.
 *
 * This bundles the common infra created by [configureNativeShimBuild] so the
 * module build script can reuse it while registering target-specific tasks.
 */
data class NativeShimBuildSupport(
    val targets: List<NativeDependencyTarget>,
    val konanHome: Provider<String>,
    val checkBazel: TaskProvider<Exec>,
    val checkKonanHome: TaskProvider<Task>,
    val syncModuleVersionTask: TaskProvider<Task>,
)

/**
 * Shared bootstrap for native-deps shim modules.
 *
 * Shim implementations need the same surrounding build machinery:
 * Bazel availability checks, Kotlin/Native distribution bootstrapping, and
 * Bazel module version synchronization.
 *
 * The module build scripts still own their native-specific steps, but they use
 * this helper so that the surrounding Gradle infrastructure stays consistent.
 */
fun Project.configureNativeShimBuild(
    downloadTaskPath: String,
    moduleFile: File,
    moduleVersion: String,
    moduleVersionVariableName: String = "GRPC_VERSION",
    syncTaskName: String = "syncGrpcVersionToBazelModule",
): NativeShimBuildSupport {
    val konanHome = konanHomeProvider()
    val checkBazel = registerCheckBazelTask()
    val prepareKonanHome = registerPrepareKonanHomeTask(
        downloadTaskPath = downloadTaskPath,
    )
    val checkKonanHome = registerCheckKonanHomeTask(
        prepareKonanHome = prepareKonanHome,
        konanHome = konanHome,
    )
    val syncModuleVersionTask = registerSyncBazelModuleVersionTask(
        moduleFile = moduleFile,
        version = moduleVersion,
        variableName = moduleVersionVariableName,
        name = syncTaskName,
    )
    return NativeShimBuildSupport(
        targets = enabledNativeDependencyTargets(),
        konanHome = konanHome,
        checkBazel = checkBazel,
        checkKonanHome = checkKonanHome,
        syncModuleVersionTask = syncModuleVersionTask,
    )
}

/**
 * Registers the standard Bazel-backed static library build task used by shim modules.
 *
 * The actual script lives once under native-deps/scripts and expects to be run from
 * the module directory that owns the Bazel workspace. This helper wires the common
 * Gradle-side dependencies and command-line arguments for one target.
 */
fun Project.registerNativeShimBazelBuildTask(
    taskName: String,
    nativeShim: NativeShimBuildSupport,
    target: NativeDependencyTarget,
    label: String,
    outputFile: Provider<RegularFile>,
): TaskProvider<Exec> = tasks.register<Exec>(taskName) {
    dependsOn(nativeShim.syncModuleVersionTask, nativeShim.checkBazel, nativeShim.checkKonanHome)
    group = "build"
    workingDir = layout.projectDirectory.asFile
    outputs.file(outputFile)
    commandLine(
        layout.projectDirectory.dir("../../scripts").file("build_target.sh").asFile.absolutePath,
        label,
        outputFile.get().asFile.absolutePath,
        target.bazelName,
        nativeShim.konanHome.get(),
    )
}
