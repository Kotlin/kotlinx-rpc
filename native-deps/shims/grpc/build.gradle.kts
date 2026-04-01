/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.register
import util.compositeCatalogVersion
import util.configureNativeShimBuild
import util.registerNativeShimBazelBuildTask
import util.configureNativeShimTargets
import util.configureSpacePackagesConsumerRepository

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

val grpcShimVersion = compositeCatalogVersion("internal-native-grpc-shim")
val grpcVersion = grpcShimVersion.base
version = grpcShimVersion.full

val grpcCoreInteropName = "grpcCoreInterop"
val grpcCoreInteropTaskName = grpcCoreInteropName.replaceFirstChar { it.uppercase() }
val grpcInteropPackageName = "kotlinx.rpc.grpc.internal.cinterop"
val grpcInternalNativeRpcApiClassName = "kotlinx/rpc/grpc/internal/shim/InternalNativeRpcApi"
val grpcInternalNativeRpcApiDependencyUniqueName = "org.jetbrains.kotlinx\\:kotlinx-rpc-native-shims-annotation"
// grpc-core already depends on protobuf-api, so grpc consumers see the protobuf shim transitively.
// Keep an explicit exclusion list here so the grpc shim can drop bundle archives whose symbols are
// already owned by the protobuf shim and avoid duplicate native definitions at final link time.
//
// The overlap is target-specific: for example, excluding libsymbolize helps on Linux where it
// collides with protobuf-shim, but it breaks macOS where grpc still needs absl::Symbolize from its
// own bundle. The excludes file therefore supports both `all:` and `<target>:` entries.
val grpcOverlapArchiveExcludesFile = layout.projectDirectory.file("overlap-archive-excludes.txt")

// The checked-in .def file stays small and stable. We derive a target-specific copy during the build with
// the full static library list gathered from the unpacked grpc bundle.
val grpcCoreInteropDefTemplate = layout.projectDirectory.file("src/nativeInterop/cinterop/grpcCoreInterop.def")
val grpcShimModuleFile = layout.projectDirectory.file("MODULE.bazel").asFile

val nativeShim = configureNativeShimBuild(
    downloadTaskPath = ":core:downloadKotlinNativeDistribution",
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

// The excludes file is intentionally human-editable and is also consumed by the overlap analysis
// helper script. Allow entries in either the bundle-manifest form (lib/foo/bar.a) or the flattened
// interop KLIB form (foo+__bar.a) so the build script can match both stages naturally.
//
// Valid lines are:
// - `all:<archive>` for exclusions that apply to every target
// - `<target>:<archive>` for target-specific exclusions, where target matches the KLIB target name
//   such as linux_x64, linux_arm64, macos_arm64, or ios_arm64
fun loadGrpcOverlapArchiveExcludes(targetName: String): Set<String> = grpcOverlapArchiveExcludesFile.asFile
    .takeIf { it.isFile }
    ?.readLines()
    ?.mapNotNull { rawLine ->
        val line = rawLine.substringBefore('#').trim()
        if (line.isEmpty()) {
            return@mapNotNull null
        }
        val separatorIndex = line.indexOf(':')
        check(separatorIndex > 0) {
            "Invalid grpc overlap archive exclude entry '$line'. Expected all:<archive> or <target>:<archive>."
        }
        val scope = line.substring(0, separatorIndex).trim()
        val archiveName = line.substring(separatorIndex + 1).trim()
        check(archiveName.isNotEmpty()) {
            "Invalid grpc overlap archive exclude entry '$line'. Archive name must not be empty."
        }
        if (scope == "all" || scope == targetName) archiveName else null
    }
    ?.toSet()
    ?: emptySet()

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
            inputs.file(grpcOverlapArchiveExcludesFile)
            outputs.dir(interopLibDir)

            doLast {
                val prebuiltDir = grpcPrebuiltDir.get().asFile
                val manifestFile = prebuiltDir.resolve("metadata/archives.txt")
                val excludedArchives = loadGrpcOverlapArchiveExcludes(target.bazelName)
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
                        val targetName = normalizedPath.toInteropArchiveName()
                        // Exclude protobuf-owned archives before copying them into the grpc shim KLIB.
                        // The final binary will still get those symbols from protobuf-api -> protobuf-shim.
                        if (relativePath in excludedArchives || normalizedPath in excludedArchives || targetName in excludedArchives) {
                            return@forEach
                        }
                        val source = prebuiltDir.resolve(relativePath)
                        check(source.isFile) {
                            "Missing grpc archive from manifest: ${source.absolutePath}"
                        }
                        source.copyTo(interopDir.resolve(targetName), overwrite = true)
                    }
            }
        }

        val generateInteropDef = tasks.register("generateGrpcShimInteropDef${taskSuffix}") {
            dependsOn(prepareInterop)
            inputs.file(grpcCoreInteropDefTemplate)
            inputs.file(shimLibFile)
            inputs.file(grpcPrebuiltDir.map { it.file("metadata/archives.txt") })
            inputs.file(grpcOverlapArchiveExcludesFile)
            outputs.file(generatedDefFile)

            doLast {
                // Keep the checked-in .def file small and stable, and derive the long archive list from
                // the published grpc bundle that was just unpacked for this target.
                val prebuiltDir = grpcPrebuiltDir.get().asFile
                val excludedArchives = loadGrpcOverlapArchiveExcludes(target.bazelName)
                val archives = prebuiltDir.resolve("metadata/archives.txt")
                    .readLines()
                    .filter { it.isNotBlank() }
                    .mapNotNull { relativePath ->
                        val normalizedPath = relativePath.normalizeGrpcArchivePath()
                        val targetName = normalizedPath.toInteropArchiveName()
                        // Mirror the prepareInterop filtering here so staticLibraries references only the
                        // archives that were actually copied into interop-libs for this target.
                        if (relativePath in excludedArchives || normalizedPath in excludedArchives || targetName in excludedArchives) {
                            null
                        } else {
                            normalizedPath
                        }
                    }
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
