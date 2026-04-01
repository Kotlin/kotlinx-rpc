/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.register
import util.compositeCatalogVersion
import util.configureNativeShimBuild
import util.configureNativeShimTargets
import util.registerNativeShimBazelBuildTask
import java.io.File

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

val protobufShimVersion = compositeCatalogVersion("internal-native-protobuf-shim")
val protobufVersion = protobufShimVersion.base
version = protobufShimVersion.full

val protowireInteropName = "libprotowire"
val protowireInteropTaskName = protowireInteropName.replaceFirstChar { it.uppercase() }
val protowireInteropDefFile = layout.projectDirectory.file("src/nativeInterop/cinterop/libprotowire.def")
val protobufInteropPackageName = "kotlinx.rpc.protobuf.internal.cinterop"
val protobufInternalNativeRpcApiClassName = "kotlinx/rpc/protobuf/internal/shim/InternalNativeProtobufApi"
val protobufInternalNativeRpcApiDependencyUniqueName = "org.jetbrains.kotlinx\\:kotlinx-rpc-native-shims-annotation"
val protobufShimModuleFile = layout.projectDirectory.file("MODULE.bazel").asFile
// KRPC-540 temporary Linux linker workaround.
// Remove this symbol rewrite once protobuf moves to a Kotlin-only implementation and this native
// protobuf/absl archive overlap disappears entirely.
val protobufLinuxSymbolPatchTargets = setOf("linux_x64", "linux_arm64")
val protobufLinuxSymbolPatchSourceName = "AbslInternalGetFileMappingHint"
val protobufLinuxSymbolPatchTargetName = "KrpcProtobuf_AbslInternalGetFileMappingHint"

val nativeShim = configureNativeShimBuild(
    downloadTaskPath = ":core:downloadKotlinNativeDistribution",
    moduleFile = protobufShimModuleFile,
    moduleVersion = protobufVersion,
    moduleVersionVariableName = "PROTOBUF_VERSION",
    syncTaskName = "syncProtobufVersionToBazelModule",
)

fun findRequiredExecutable(vararg names: String): String {
    val pathEntries = (System.getenv("PATH") ?: "")
        .split(File.pathSeparatorChar)
        .filter { it.isNotBlank() }

    names.forEach { name ->
        pathEntries.firstNotNullOfOrNull { entry ->
            File(entry, name).takeIf { it.isFile && it.canExecute() }
        }?.let { return it.absolutePath }
    }

    error("Required executable not found on PATH. Expected one of: ${names.joinToString()}")
}

// KRPC-540 temporary helper for the protobuf-shim archive rewrite.
// Remove together with the Linux symbol rewrite once protobuf becomes Kotlin-only.
fun runCheckedCommand(workingDir: File, vararg args: String) {
    val process = ProcessBuilder(*args)
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()
    check(exitCode == 0) {
        buildString {
            append("Command failed with exit code ")
            append(exitCode)
            append(": ")
            append(args.joinToString(" "))
            if (output.isNotBlank()) {
                appendLine()
                append(output.trim())
            }
        }
    }
}

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
        val interopLibDir = layout.buildDirectory.dir("protobuf/${target.bazelName}/interop-libs")

        val buildProtobufShim = registerNativeShimBazelBuildTask(
            taskName = "buildProtobufShim${taskSuffix}",
            nativeShim = nativeShim,
            target = target,
            label = ":protowire_fat",
            outputFile = shimLibFile,
        )

        val prepareInterop = tasks.register("prepareProtobufShimInterop${taskSuffix}") {
            dependsOn(buildProtobufShim)
            inputs.file(shimLibFile)
            outputs.dir(interopLibDir)

            doLast {
                val interopDir = interopLibDir.get().asFile.apply {
                    deleteRecursively()
                    mkdirs()
                }
                val interopArchive = interopDir.resolve(shimLibFile.get().asFile.name)
                shimLibFile.get().asFile.copyTo(interopArchive, overwrite = true)

                // KRPC-540 temporary Linux linker workaround.
                // grpc and protobuf currently bundle different Abseil LTS versions, so both Symbolize
                // implementations must remain available. The one plain C helper symbol below collides
                // across both archives though, so rewrite only the protobuf copy after the Bazel build
                // and before cinterop packages the archive into the KLIB.
                //
                // Remove this block once protobuf moves to a Kotlin-only implementation and the shim
                // no longer republishes protobuf/absl native archives.
                if (target.bazelName !in protobufLinuxSymbolPatchTargets) {
                    return@doLast
                }

                val llvmAr = findRequiredExecutable("llvm-ar", "ar")
                val llvmObjcopy = findRequiredExecutable("llvm-objcopy", "objcopy")
                val extractedObject = interopDir.resolve("symbolize.o")

                runCheckedCommand(interopDir, llvmAr, "x", interopArchive.absolutePath, extractedObject.name)
                runCheckedCommand(
                    interopDir,
                    llvmObjcopy,
                    "--redefine-sym",
                    "$protobufLinuxSymbolPatchSourceName=$protobufLinuxSymbolPatchTargetName",
                    extractedObject.absolutePath,
                )
                runCheckedCommand(interopDir, llvmAr, "r", interopArchive.absolutePath, extractedObject.name)

                extractedObject.delete()
            }
        }

        compilations.getByName("main").cinterops.create(protowireInteropName) {
            defFile(protowireInteropDefFile.asFile)
            includeDirs(layout.projectDirectory.dir("include").asFile)
            extraOpts("-libraryPath", interopLibDir.get().asFile.absolutePath)
        }
        listOf(prepareInterop)
    }
}
