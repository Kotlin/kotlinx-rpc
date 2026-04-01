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

fun findRequiredFile(baseDir: File, relativePaths: List<String>, description: String): String =
    relativePaths.firstNotNullOfOrNull { relativePath ->
        baseDir.resolve(relativePath).takeIf { it.isFile && it.canExecute() }?.absolutePath
    } ?: error("Required $description not found under ${baseDir.absolutePath}. Checked: ${relativePaths.joinToString()}")

fun findKonanManagedLlvmAr(konanHome: String): String {
    val konanDepsDir = File(konanHome).resolve("../dependencies").normalize()
    val llvmBundle = konanDepsDir.listFiles()
        ?.filter { it.isDirectory && it.name.startsWith("llvm-") }
        ?.sortedByDescending { it.name }
        ?.firstOrNull()
        ?: error("Could not find an llvm-* bundle under ${konanDepsDir.absolutePath}")
    return findRequiredFile(llvmBundle, listOf("bin/llvm-ar"), "llvm-ar executable")
}

fun findKonanManagedObjcopy(konanHome: String, targetName: String): String {
    val konanDepsDir = File(konanHome).resolve("../dependencies").normalize()
    val targetTriple = when (targetName) {
        "linux_x64" -> "x86_64-unknown-linux-gnu"
        "linux_arm64" -> "aarch64-unknown-linux-gnu"
        else -> error("No KONAN-managed objcopy expected for target $targetName")
    }
    val gccBundle = konanDepsDir.listFiles()
        ?.filter { it.isDirectory && it.name.startsWith("$targetTriple-gcc-") }
        ?.sortedByDescending { it.name }
        ?.firstOrNull()
        ?: error("Could not find a $targetTriple-gcc-* bundle under ${konanDepsDir.absolutePath}")
    return findRequiredFile(gccBundle, listOf("$targetTriple/bin/objcopy"), "objcopy executable")
}

fun findHostManagedLlvmObjcopy(): String {
    val xcrunLlvmObjcopy = runCatching {
        val process = ProcessBuilder("xcrun", "--find", "llvm-objcopy")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText().trim()
        val exitCode = process.waitFor()
        if (exitCode == 0 && output.isNotBlank()) output else null
    }.getOrNull()
    return xcrunLlvmObjcopy
        ?: runCatching { findRequiredExecutable("llvm-objcopy", "objcopy") }.getOrNull()
        ?: error("Required host objcopy executable not found. Expected llvm-objcopy or objcopy on PATH, or xcrun --find llvm-objcopy to succeed.")
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

                val konanHome = nativeShim.konanHome.get()
                // KRPC-540 temporary Linux linker workaround.
                // Prefer the Kotlin/Native-managed toolchain locations because CI runners do not
                // necessarily expose llvm-objcopy/objcopy on PATH. Remove together with the symbol
                // rewrite once protobuf becomes Kotlin-only.
                val llvmAr = runCatching { findKonanManagedLlvmAr(konanHome) }
                    .getOrElse { findRequiredExecutable("llvm-ar", "ar") }
                val hostOs = System.getProperty("os.name").lowercase()
                val llvmObjcopy = runCatching { findHostManagedLlvmObjcopy() }
                    .getOrElse {
                        // KRPC-540 fallback for Linux hosts only. The KONAN GCC bundles ship Linux-hosted
                        // objcopy binaries, so they are not executable on macOS runners.
                        if (hostOs.contains("linux")) {
                            findKonanManagedObjcopy(konanHome, target.bazelName)
                        } else {
                            throw it
                        }
                    }
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
