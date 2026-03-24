/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.tasks.Exec
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import util.konanHomeProvider
import util.nativeDependencyTargets
import util.configureSpacePackagesConsumerRepository
import util.registerCheckBazelTask
import util.registerCheckKonanHomeTask
import util.registerNativeDependencyTargets
import util.registerPrepareKonanHomeTask
import util.registerSyncBazelModuleVersionTask
import util.requireGradleProperty
import util.toTaskSuffix

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("conventions-publishing")
}

val globalRootDir: String by extra

repositories {
    // Fall back to the public grpc package repository when the local dev repo does not contain the bundle.
    configureSpacePackagesConsumerRepository(repoName = "grpc")
}

val grpcVersion = requireGradleProperty("grpcVersion")

val libkgrpcDefTemplate = layout.projectDirectory.file("src/nativeInterop/cinterop/libkgrpc.def")
val grpcShimTargets = nativeDependencyTargets
val grpcShimModuleFile = layout.projectDirectory.file("MODULE.bazel").asFile
val unsupportedApiPluginProject = project(":klib-patcher")
val unsupportedApiPluginJar = unsupportedApiPluginProject.layout.buildDirectory.file(
    "libs/${unsupportedApiPluginProject.name}-${project.version}.jar",
)

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
unsupportedApiPluginProject.tasks.withType<KotlinCompile>().configureEach {
    dependsOn(checkKonanHome)
}
val syncGrpcVersionToBazelModule = registerSyncBazelModuleVersionTask(
    moduleFile = grpcShimModuleFile,
    version = grpcVersion,
)

fun String.normalizeGrpcArchivePath(): String =
    if (endsWith(".lo")) removeSuffix(".lo") + ".a" else this

fun String.toInteropArchiveName(): String = normalizeGrpcArchivePath()
    .removePrefix("lib/")
    .replace("/", "__")

val grpcHeadersDir = layout.buildDirectory.dir("grpc/headers")
val patchUnsupportedApiTasks = mutableListOf<TaskProvider<Task>>()
val prepareGrpcHeaders = tasks.register<Sync>("prepareGrpcHeaders") {
    group = "build"
    into(grpcHeadersDir)
    from({ zipTree(grpcHeaders.singleFile) })
    doFirst {
        delete(grpcHeadersDir)
    }
}

kotlin {
    registerNativeDependencyTargets()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":annotation"))
            }
        }

        nativeMain {
            dependencies {
                // The cinterop dependency configuration resolves from the implementation side,
                // while downstream consumers still need the marker as a transitive API dependency.
                implementation(project(":annotation"))
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        val target = grpcShimTargets.single { it.bazelName == konanTarget.visibleName }
        val taskSuffix = target.bazelName.toTaskSuffix()
        val publicationTaskSuffix = target.kotlinName.replaceFirstChar { it.uppercase() }
        val grpcPrebuiltDir = layout.buildDirectory.dir("grpc/${target.bazelName}/prebuilt")
        val interopLibDir = layout.buildDirectory.dir("grpc/${target.bazelName}/interop-libs")
        val generatedDefFile = layout.buildDirectory.file("grpc/${target.bazelName}/libkgrpc.def")
        val shimLibFile = layout.buildDirectory.file("grpc-shim/${target.bazelName}/libkgrpc.${target.bazelName}.a")
        val packedCinteropKlibFile = layout.buildDirectory.file(
            "libs/${project.name}-${target.kotlinName}Cinterop-libkgrpcMain-$version.klib",
        )

        val prepareGrpcBundle = tasks.register<Sync>("prepareGrpc${taskSuffix}") {
            group = "build"
            into(grpcPrebuiltDir)
            from({ zipTree(grpcTargetBundles.getValue(target).singleFile) })
        }

        val buildGrpcShim = tasks.register<Exec>("buildGrpcShim${taskSuffix}") {
            dependsOn(syncGrpcVersionToBazelModule, checkBazel, checkKonanHome)
            group = "build"
            workingDir = layout.projectDirectory.asFile
            outputs.file(shimLibFile)
            commandLine(
                "./build_target.sh",
                ":grpc_shim",
                shimLibFile.get().asFile.absolutePath,
                target.bazelName,
                konanHome.get(),
            )
        }

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

                val shimLibName = "libkgrpc.${target.bazelName}.a"
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
                    .map { it.normalizeGrpcArchivePath() }
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
            )
            extraOpts("-libraryPath", interopLibDir.get().asFile.absolutePath)
        }

        val cinteropTaskName = "cinteropLibkgrpc${target.kotlinName.replaceFirstChar { it.uppercase() }}"
        val cinteropTask = tasks.named(cinteropTaskName, CInteropProcess::class) {
            dependsOn(prepareGrpcHeaders, generateInteropDef)
        }
        val cinteropPublicationTaskName = "${target.kotlinName}Cinterop-libkgrpcKlib"

        fun patchPackedKlib(outputFile: File) {
            val toolJar = unsupportedApiPluginJar.get().asFile
            check(toolJar.isFile) {
                "Missing grpc-shim unsupported API tool jar: ${toolJar.absolutePath}"
            }
            val process = ProcessBuilder(
                "java",
                "-cp",
                toolJar.absolutePath,
                "kotlinx.rpc.grpc.nativedeps.tooling.KlibPatcher",
                cinteropTask.get().klibDirectory.get().asFile.absolutePath,
                outputFile.absolutePath,
            )
                .directory(layout.projectDirectory.asFile)
                .inheritIO()
                .start()
            check(process.waitFor() == 0) {
                "grpc-shim unsupported API patcher failed for ${outputFile.absolutePath}"
            }
        }

        val patchUnsupportedApiTask = tasks.register("patchGrpcShimUnsupportedApi${taskSuffix}") {
            dependsOn(cinteropTask)
            dependsOn(":klib-patcher:jar")
            inputs.file(unsupportedApiPluginJar)
            inputs.dir(cinteropTask.flatMap { it.klibDirectory })
            outputs.file(packedCinteropKlibFile)

            doLast {
                patchPackedKlib(packedCinteropKlibFile.get().asFile)
            }
        }
        patchUnsupportedApiTasks += patchUnsupportedApiTask
        tasks.withType<Zip>().matching { it.name == cinteropPublicationTaskName }.configureEach {
            dependsOn(cinteropTask)
            dependsOn(":klib-patcher:jar")
            inputs.file(unsupportedApiPluginJar)
            inputs.dir(cinteropTask.flatMap { it.klibDirectory })

            doLast {
                patchPackedKlib(archiveFile.get().asFile)
            }
        }
        tasks.matching { task ->
            task.name == "generateMetadataFileFor${publicationTaskSuffix}Publication" ||
                task.name == "generatePomFileFor${publicationTaskSuffix}Publication" ||
                task.name == "patchModuleJsonFor${publicationTaskSuffix}"
        }.configureEach {
            dependsOn(patchUnsupportedApiTask)
        }
    }
}

tasks.register("patchGrpcShimUnsupportedApiArtifacts") {
    group = "build"
    dependsOn(patchUnsupportedApiTasks)
}

tasks.named("assemble") {
    dependsOn("patchGrpcShimUnsupportedApiArtifacts")
}

publishing {
    publications.withType(MavenPublication::class).configureEach {
        artifactId = when {
            artifactId == "core" ->
                "kotlinx-rpc-grpc-core-shim"
            artifactId.startsWith("core-") ->
                artifactId.replaceFirst("core", "kotlinx-rpc-grpc-core-shim")
            artifactId == "kotlinx-rpc-core" ->
                "kotlinx-rpc-grpc-core-shim"
            artifactId.startsWith("kotlinx-rpc-core-") ->
                artifactId.replaceFirst("kotlinx-rpc-core", "kotlinx-rpc-grpc-core-shim")
            else -> artifactId
        }
    }
}
