/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.register
import util.configureNativeShimBuild
import util.configureNativeShimTargets
import util.registerNativeShimBazelBuildTask

/**
 * Configures the protobuf module inside native-deps/shims to build and publish the protobuf shim KLIB.
 *
 * This build process includes:
 * 1. Building the target-specific `protowire_fat` static library with Bazel.
 * 2. Running cinterop against the published protowire headers and static library.
 * 3. Patching the produced cinterop KLIB so the generated declarations require
 *    explicit opt-in via `InternalNativeProtobufApi`.
 */
plugins {
    id("conventions-native-shim")
}

val protobufVersion = providers.gradleProperty("protobufVersion").get()
val protowireInteropName = "libprotowire"
val protowireInteropTaskName = protowireInteropName.replaceFirstChar { it.uppercase() }
val protowireInteropDefFile = layout.projectDirectory.file("src/nativeInterop/cinterop/libprotowire.def")
val protobufInteropPackageName = "kotlinx.rpc.protobuf.internal.cinterop"
val protobufInternalNativeRpcApiClassName = "kotlinx/rpc/protobuf/internal/shim/InternalNativeProtobufApi"
val protobufInternalNativeRpcApiDependencyUniqueName = "org.jetbrains.kotlinx\\:kotlinx-rpc-native-shims-annotation"
val protobufShimModuleFile = layout.projectDirectory.file("MODULE.bazel").asFile

val nativeShim = configureNativeShimBuild(
    downloadTaskPath = ":core:downloadKotlinNativeDistribution",
    moduleFile = protobufShimModuleFile,
    moduleVersion = protobufVersion,
    moduleVersionVariableName = "PROTOBUF_VERSION",
    syncTaskName = "syncProtobufVersionToBazelModule",
)

kotlin {
    configureNativeShimTargets(
        nativeShim = nativeShim,
        interopName = protowireInteropName,
        targetPackageName = protobufInteropPackageName,
        annotationClassName = protobufInternalNativeRpcApiClassName,
        annotationDependencyUniqueName = protobufInternalNativeRpcApiDependencyUniqueName,
    ) { context ->
        val target = context.target
        val taskSuffix = context.taskSuffix
        val shimLibDir = layout.buildDirectory.dir("protobuf/${target.bazelName}")
        val shimLibFile = layout.buildDirectory.file("protobuf/${target.bazelName}/libprotowire_fat.${target.bazelName}.a")

        val buildProtobufShim = registerNativeShimBazelBuildTask(
            taskName = "buildProtobufShim${taskSuffix}",
            nativeShim = nativeShim,
            target = target,
            label = ":protowire_fat",
            outputFile = shimLibFile,
        )

        compilations.getByName("main").cinterops.create(protowireInteropName) {
            defFile(protowireInteropDefFile.asFile)
            includeDirs(layout.projectDirectory.dir("include").asFile)
            extraOpts("-libraryPath", shimLibDir.get().asFile.absolutePath)
        }
        listOf(buildProtobufShim)
    }
}
