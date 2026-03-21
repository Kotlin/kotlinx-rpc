/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.Exec
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import util.konanHomeProvider
import util.nativeDependencyTargets
import util.registerCheckBazelTask
import util.registerCheckKonanHomeTask
import util.registerNativeDependencyTargets
import util.registerPrepareKonanHomeTask
import util.requireGradleProperty
import util.toTaskSuffix

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("conventions-publishing")
}

group = "org.jetbrains.kotlinx"
val globalRootDir: String by extra

repositories {
    maven("$globalRootDir/build/repo")
}

val grpcVersion = requireGradleProperty("grpcVersion")
val shimVersion = requireGradleProperty("shimVersion")
version = "$grpcVersion-$shimVersion"
val libkgrpcDefTemplate = layout.projectDirectory.file("src/nativeInterop/cinterop/libkgrpc.def")
val grpcShimTargets = nativeDependencyTargets

val grpcHeaders by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}

val grpcTargetBundles = grpcShimTargets.associateWith { target ->
    configurations.create("grpc${target.kotlinName}Bundle") {
        isCanBeConsumed = false
        isCanBeResolved = true
        isTransitive = false
    }
}

dependencies {
    add(grpcHeaders.name, "org.jetbrains.kotlinx:kotlinx-rpc-grpc-core-c-headers:$grpcVersion@zip")
    grpcShimTargets.forEach { target ->
        add(
            grpcTargetBundles.getValue(target).name,
            "org.jetbrains.kotlinx:kotlinx-rpc-grpc-core-c-deps-${target.publicationSuffix}:$grpcVersion@zip",
        )
    }
}

val konanHome = konanHomeProvider()
val checkBazel = registerCheckBazelTask()
val prepareKonanHome = registerPrepareKonanHomeTask(
    downloadTaskPath = ":grpc:grpc-core:downloadKotlinNativeDistribution",
)
val checkKonanHome = registerCheckKonanHomeTask(
    prepareKonanHome = prepareKonanHome,
    konanHome = konanHome,
)

fun String.toInteropArchiveName(): String = removePrefix("lib/")
    .replace("/", "__")

kotlin {
    registerNativeDependencyTargets()

    targets.withType<KotlinNativeTarget>().configureEach {
        val target = grpcShimTargets.single { it.bazelName == konanTarget.visibleName }
        val taskSuffix = target.bazelName.toTaskSuffix()
        val grpcPrebuiltDir = layout.buildDirectory.dir("grpc/${target.bazelName}/prebuilt")
        val grpcHeadersDir = grpcPrebuiltDir.map { it.dir("headers") }
        val interopLibDir = layout.buildDirectory.dir("grpc/${target.bazelName}/interop-libs")
        val generatedDefFile = layout.buildDirectory.file("grpc/${target.bazelName}/libkgrpc.def")
        val shimLibFile = layout.buildDirectory.file("grpc-shim/${target.bazelName}/libkgrpc.${target.bazelName}.a")

        val prepareGrpcBundle = tasks.register<Sync>("prepareGrpc${taskSuffix}") {
            group = "build"
            into(grpcPrebuiltDir)
            from({ zipTree(grpcHeaders.singleFile) }) {
                into("headers")
            }
            from({ zipTree(grpcTargetBundles.getValue(target).singleFile) })
        }

        val buildGrpcShim = tasks.register<Exec>("buildGrpcShim${taskSuffix}") {
            dependsOn(prepareGrpcBundle, checkBazel, checkKonanHome)
            group = "build"
            workingDir = layout.projectDirectory.asFile
            outputs.file(shimLibFile)
            commandLine(
                "./build_target.sh",
                ":grpc_shim_${target.bazelName}",
                shimLibFile.get().asFile.absolutePath,
                target.bazelName,
                konanHome.get(),
            )
        }

        val prepareInterop = tasks.register("prepareGrpcShimInterop${taskSuffix}") {
            dependsOn(buildGrpcShim)
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

                val shimLibName = "libkgrpc.${target.bazelName}.a"
                shimLibFile.get().asFile.copyTo(interopDir.resolve(shimLibName), overwrite = true)

                // The grpc bundle keeps repo-qualified subdirectories to avoid basename collisions.
                // For cinterop we flatten them into a single directory with deterministic names, because
                // staticLibraries lists files by name and duplicate basenames like Abseil's libinternal.a
                // would otherwise resolve ambiguously.
                manifestFile.readLines()
                    .filter { it.isNotBlank() }
                    .forEach { relativePath ->
                        val source = prebuiltDir.resolve(relativePath)
                        check(source.isFile) {
                            "Missing grpc archive from manifest: ${source.absolutePath}"
                        }
                        val targetName = relativePath.toInteropArchiveName()
                        source.copyTo(interopDir.resolve(targetName), overwrite = true)
                    }
            }
        }

        val generateInteropDef = tasks.register("generateGrpcShimInteropDef${taskSuffix}") {
            dependsOn(prepareInterop)
            inputs.file(libkgrpcDefTemplate)
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
                    .map { it.toInteropArchiveName() }
                val staticLibraries = buildList {
                    add("libkgrpc.${target.bazelName}.a")
                    addAll(archives)
                }.joinToString(" ")

                generatedDefFile.get().asFile.apply {
                    parentFile.mkdirs()
                    writeText(
                        libkgrpcDefTemplate.asFile.readText()
                            .replace("__STATIC_LIBRARIES__", "staticLibraries = $staticLibraries"),
                    )
                }
            }
        }

        compilations.getByName("main").cinterops.create("libkgrpc") {
            defFile(generatedDefFile.get().asFile)
            includeDirs(
                layout.projectDirectory.dir("include").asFile,
                grpcHeadersDir.get().asFile.resolve("include"),
                grpcHeadersDir.get().asFile,
            )
            extraOpts("-libraryPath", interopLibDir.get().asFile.absolutePath)
        }

        val cinteropTaskName = "cinteropLibkgrpc${target.kotlinName.replaceFirstChar { it.uppercase() }}"
        tasks.named(cinteropTaskName, CInteropProcess::class) {
            dependsOn(generateInteropDef)
        }
    }
}

publishing {
    publications.withType(MavenPublication::class).configureEach {
        artifactId = when {
            artifactId == "grpc-shim" ->
                "kotlinx-rpc-grpc-core-shim"
            artifactId.startsWith("grpc-shim-") ->
                artifactId.replaceFirst("grpc-shim", "kotlinx-rpc-grpc-core-shim")
            artifactId == "kotlinx-rpc-grpc-shim" ->
                "kotlinx-rpc-grpc-core-shim"
            artifactId.startsWith("kotlinx-rpc-grpc-shim-") ->
                artifactId.replaceFirst("kotlinx-rpc-grpc-shim", "kotlinx-rpc-grpc-core-shim")
            else -> artifactId
        }
    }
}
