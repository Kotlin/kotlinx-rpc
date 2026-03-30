/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.register
import util.configureNativeShimBuild
import util.registerNativeShimBazelBuildTask
import util.configureNativeShimTargets
import util.configureSpacePackagesConsumerRepository
import util.requireGradleProperty

/**
 * Configures the gRPC module inside native-deps/shims to build and publish the gRPC shim KLIB.
 *
 * This build process includes assembling a KLIB from the following components:
 * 1. gRPC headers and prebuilt archives provided by native-deps/grpc-c-prebuilt.
 * 2. A small target-specific native shim library built using Bazel.
 * 3. A post-processing step that applies the `InternalNativeRpcApi` marker.
 */

plugins {
    id("conventions-native-shim")
}

repositories {
    configureSpacePackagesConsumerRepository(repoName = "grpc")
}

val grpcVersion = requireGradleProperty("grpcVersion")
val grpcCoreInteropName = "grpcCoreInterop"
val grpcCoreInteropTaskName = grpcCoreInteropName.replaceFirstChar { it.uppercase() }
val grpcInteropPackageName = "kotlinx.rpc.grpc.internal.cinterop"
val grpcInternalNativeRpcApiClassName = "kotlinx/rpc/grpc/internal/InternalNativeRpcApi"
val grpcInternalNativeRpcApiDependencyUniqueName = "org.jetbrains.kotlinx\\:kotlinx-rpc-native-shims-annotation"

// The checked-in .def file stays small and stable. We derive a target-specific copy during the build with
// the full static library list gathered from the unpacked grpc bundle.
val grpcCoreInteropDefTemplate = layout.projectDirectory.file("src/nativeInterop/cinterop/grpcCoreInterop.def")
val grpcShimModuleFile = layout.projectDirectory.file("MODULE.bazel").asFile

val nativeShim = configureNativeShimBuild(
    downloadTaskPath = ":core:core:downloadKotlinNativeDistribution",
    moduleFile = grpcShimModuleFile,
    moduleVersion = grpcVersion,
)

val grpcHeaders by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}

val grpcTargetBundles = nativeShim.targets.associateWith { target ->
    configurations.create("grpc${target.kotlinName}Bundle") {
        isCanBeConsumed = false
        isCanBeResolved = true
        isTransitive = false
    }
}

dependencies {
    add(grpcHeaders.name, "org.jetbrains.kotlinx:kotlinx-rpc-grpc-core-c-headers:$grpcVersion@zip")
    nativeShim.targets.forEach { target ->
        add(
            grpcTargetBundles.getValue(target).name,
            "org.jetbrains.kotlinx:kotlinx-rpc-grpc-core-c-deps-${target.publicationSuffix}:$grpcVersion@zip",
        )
    }
}

fun String.normalizeGrpcArchivePath(): String =
    if (endsWith(".lo")) removeSuffix(".lo") + ".a" else this

fun String.toInteropArchiveName(): String = normalizeGrpcArchivePath()
    .removePrefix("lib/")
    .replace("/", "__")

val grpcHeadersDir = layout.buildDirectory.dir("grpc/headers")
val prepareGrpcHeaders = tasks.register<Sync>("prepareGrpcHeaders") {
    group = "build"
    into(grpcHeadersDir)
    from({ zipTree(grpcHeaders.singleFile) })
    doFirst {
        delete(grpcHeadersDir)
    }
}

kotlin {
    configureNativeShimTargets(
        nativeShim = nativeShim,
        interopName = grpcCoreInteropName,
        targetPackageName = grpcInteropPackageName,
        annotationClassName = grpcInternalNativeRpcApiClassName,
        annotationDependencyUniqueName = grpcInternalNativeRpcApiDependencyUniqueName,
    ) { context ->
        val target = context.target
        val taskSuffix = context.taskSuffix
        val grpcPrebuiltDir = layout.buildDirectory.dir("grpc/${target.bazelName}/prebuilt")
        val interopLibDir = layout.buildDirectory.dir("grpc/${target.bazelName}/interop-libs")
        val generatedDefFile = layout.buildDirectory.file("grpc/${target.bazelName}/$grpcCoreInteropName.def")
        val shimLibFile = layout.buildDirectory.file("grpc/${target.bazelName}/lib$grpcCoreInteropTaskName.${target.bazelName}.a")

        val prepareGrpcBundle = tasks.register<Sync>("prepareGrpc${taskSuffix}") {
            group = "build"
            into(grpcPrebuiltDir)
            from({ zipTree(grpcTargetBundles.getValue(target).singleFile) })
        }

        val buildGrpcShim = registerNativeShimBazelBuildTask(
            taskName = "buildGrpcShim${taskSuffix}",
            nativeShim = nativeShim,
            target = target,
            label = ":grpc_shim",
            outputFile = shimLibFile,
        )

        val prepareInterop = tasks.register("prepareGrpcShimInterop${taskSuffix}") {
            dependsOn(prepareGrpcBundle, buildGrpcShim)
            outputs.dir(interopLibDir)

            doLast {
                val prebuiltDir = grpcPrebuiltDir.get().asFile
                val manifestFile = prebuiltDir.resolve("metadata/archives.txt")
                check(manifestFile.isFile) {
                    "Missing grpc archive manifest: ${manifestFile.absolutePath}"
                }

                val interopDir = interopLibDir.get().asFile.apply {
                    deleteRecursively()
                    mkdirs()
                }

                val shimLibName = "lib$grpcCoreInteropTaskName.${target.bazelName}.a"
                shimLibFile.get().asFile.copyTo(interopDir.resolve(shimLibName), overwrite = true)

                // The grpc bundle keeps repo-qualified subdirectories to avoid basename collisions.
                // For cinterop we flatten them into a single directory with deterministic names, because
                // staticLibraries lists files by name and duplicate basenames like Abseil's libinternal.a
                // would otherwise resolve ambiguously.
                manifestFile.readLines()
                    .filter { it.isNotBlank() }
                    .forEach { relativePath ->
                        val normalizedPath = relativePath.normalizeGrpcArchivePath()
                        val source = prebuiltDir.resolve(relativePath)
                        check(source.isFile) {
                            "Missing grpc archive from manifest: ${source.absolutePath}"
                        }
                        val targetName = normalizedPath.toInteropArchiveName()
                        source.copyTo(interopDir.resolve(targetName), overwrite = true)
                    }
            }
        }

        val generateInteropDef = tasks.register("generateGrpcShimInteropDef${taskSuffix}") {
            dependsOn(prepareInterop)
            inputs.file(grpcCoreInteropDefTemplate)
            inputs.file(shimLibFile)
            inputs.file(grpcPrebuiltDir.map { it.file("metadata/archives.txt") })
            outputs.file(generatedDefFile)

            doLast {
                // Keep the checked-in .def file small and stable, and derive the long archive list from
                // the published grpc bundle that was just unpacked for this target.
                val prebuiltDir = grpcPrebuiltDir.get().asFile
                val archives = prebuiltDir.resolve("metadata/archives.txt")
                    .readLines()
                    .filter { it.isNotBlank() }
                    .map { it.normalizeGrpcArchivePath() }
                    .map { it.toInteropArchiveName() }
                val staticLibraries = buildList {
                    add("lib$grpcCoreInteropTaskName.${target.bazelName}.a")
                    addAll(archives)
                }.joinToString(" ")

                generatedDefFile.get().asFile.apply {
                    parentFile.mkdirs()
                    writeText(
                        grpcCoreInteropDefTemplate.asFile.readText()
                            .replace("__STATIC_LIBRARIES__", "staticLibraries = $staticLibraries"),
                    )
                }
            }
        }

        compilations.getByName("main").cinterops.create(grpcCoreInteropName) {
            defFile(generatedDefFile.get().asFile)
            includeDirs(
                layout.projectDirectory.dir("include").asFile,
                grpcHeadersDir.get().asFile.resolve("include"),
            )
            extraOpts("-libraryPath", interopLibDir.get().asFile.absolutePath)
        }
        listOf(prepareGrpcHeaders, generateInteropDef)
    }
}
