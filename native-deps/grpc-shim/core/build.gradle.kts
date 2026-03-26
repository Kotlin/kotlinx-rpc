/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
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

/**
 * Configures a Kotlin Multiplatform project to build and publish a gRPC shim KLIB.
 *
 * This build process includes assembling a KLIB from the following components:
 * 1. gRPC headers and prebuilt archives provided by native-deps/grpc.
 * 2. A small target-specific native shim library built using Bazel.
 * 3. A post-processing step that applies the `InternalNativeRpcApi` marker.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("conventions-publishing")
}

repositories {
    configureSpacePackagesConsumerRepository(repoName = "grpc")
}

val grpcVersion = requireGradleProperty("grpcVersion")
val grpcCoreInteropName = "grpcCoreInterop"
val grpcCoreInteropTaskName = grpcCoreInteropName.replaceFirstChar { it.uppercase() }
val grpcInteropPackageName = "kotlinx.rpc.grpc.internal.cinterop"
val grpcInternalNativeRpcApiClassName = "kotlinx/rpc/grpc/internal/InternalNativeRpcApi"
val grpcInternalNativeRpcApiDependencyUniqueName = "org.jetbrains.kotlinx\\:kotlinx-rpc-grpc-core-shim-annotation"

// The checked-in .def file stays small and stable. We derive a target-specific copy during the build with
// the full static library list gathered from the unpacked grpc bundle.
val grpcCoreInteropDefTemplate = layout.projectDirectory.file("src/nativeInterop/cinterop/grpcCoreInterop.def")
val grpcShimTargets = nativeDependencyTargets
val grpcShimModuleFile = layout.projectDirectory.file("MODULE.bazel").asFile

// cinterop does not load our compiler plugin on this build path, so grpc-shim uses a dedicated internal
// KLIB patcher to add the opt-in marker after cinterop and before publication.
val internalNativeRpcApiPatcherProject = project(":klib-patcher")
val internalNativeRpcApiPatcherJar = internalNativeRpcApiPatcherProject.layout.buildDirectory.file(
    "libs/${internalNativeRpcApiPatcherProject.name}-${project.version}.jar",
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
val patchInternalNativeRpcApiTasks = mutableListOf<TaskProvider<out Task>>()
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
        nativeMain {
            dependencies {
                // The cinterop dependency configuration resolves from the implementation side,
                // while downstream consumers still need the marker as a transitive API dependency.
                implementation(project(":kotlinx-rpc-grpc-core-shim-annotation"))
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        val target = grpcShimTargets.single { it.bazelName == konanTarget.visibleName }
        val taskSuffix = target.bazelName.toTaskSuffix()
        val publicationTaskSuffix = target.kotlinName.replaceFirstChar { it.uppercase() }
        val grpcPrebuiltDir = layout.buildDirectory.dir("grpc/${target.bazelName}/prebuilt")
        val interopLibDir = layout.buildDirectory.dir("grpc/${target.bazelName}/interop-libs")
        val generatedDefFile = layout.buildDirectory.file("grpc/${target.bazelName}/$grpcCoreInteropName.def")
        val shimLibFile = layout.buildDirectory.file("grpc-shim/${target.bazelName}/lib$grpcCoreInteropTaskName.${target.bazelName}.a")
        val packedCinteropKlibFile = layout.buildDirectory.file(
            "libs/${project.name}-${target.kotlinName}Cinterop-$grpcCoreInteropName" +
                "Main-$version.klib",
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

        val cinteropTaskName = "cinterop$grpcCoreInteropTaskName${target.kotlinName.replaceFirstChar { it.uppercase() }}"
        val cinteropTask = tasks.named(cinteropTaskName, CInteropProcess::class) {
            dependsOn(prepareGrpcHeaders, generateInteropDef)
        }
        val cinteropPublicationTaskName = "${target.kotlinName}Cinterop-$grpcCoreInteropName" + "Klib"
        val cinteropPublicationTask = tasks.named(cinteropPublicationTaskName, Zip::class)

        // Produces a patched local-packed KLIB under build/libs. This is useful for inspection and for keeping
        // the build graph explicit around the post-cinterop metadata rewrite.
        val patchInternalNativeRpcApiTask = tasks.register<PatchInternalNativeRpcApiTask>("patchInternalNativeRpcApi${taskSuffix}") {
            dependsOn(cinteropTask)
            dependsOn(":klib-patcher:jar")
            classpath(files(internalNativeRpcApiPatcherJar))
            mainClass.set("kotlinx.rpc.nativedeps.tooling.KlibPatcher")
            workingDir = layout.projectDirectory.asFile
            inputs.file(internalNativeRpcApiPatcherJar)
            inputKlibDir.set(cinteropTask.flatMap { it.klibDirectory })
            outputKlibFile.set(packedCinteropKlibFile)
            targetPackageName.set(grpcInteropPackageName)
            annotationClassName.set(grpcInternalNativeRpcApiClassName)
            annotationDependencyUniqueName.set(grpcInternalNativeRpcApiDependencyUniqueName)
        }
        patchInternalNativeRpcApiTasks.add(patchInternalNativeRpcApiTask)

        // Gradle also creates a separate zipped cinterop publication artifact. Patch that archive too so the
        // actually published .klib carries the opt-in marker and annotation dependency metadata.
        val patchPublishedInternalNativeRpcApiTask = tasks.register<PatchInternalNativeRpcApiTask>(
            "patchPublishedInternalNativeRpcApi${taskSuffix}",
        ) {
            dependsOn(cinteropPublicationTask)
            dependsOn(":klib-patcher:jar")
            classpath(files(internalNativeRpcApiPatcherJar))
            mainClass.set("kotlinx.rpc.nativedeps.tooling.KlibPatcher")
            workingDir = layout.projectDirectory.asFile
            inputs.file(internalNativeRpcApiPatcherJar)
            inputKlibDir.set(cinteropTask.flatMap { it.klibDirectory })
            outputKlibFile.set(cinteropPublicationTask.flatMap { it.archiveFile })
            targetPackageName.set(grpcInteropPackageName)
            annotationClassName.set(grpcInternalNativeRpcApiClassName)
            annotationDependencyUniqueName.set(grpcInternalNativeRpcApiDependencyUniqueName)
        }

        tasks.matching { task ->
            task.name == "generateMetadataFileFor${publicationTaskSuffix}Publication" ||
                task.name == "generatePomFileFor${publicationTaskSuffix}Publication" ||
                task.name == "patchModuleJsonFor${publicationTaskSuffix}"
        }.configureEach {
            // Publication metadata must be generated only after both patch steps have run, otherwise one build
            // path can still publish an unpatched cinterop artifact.
            dependsOn(patchInternalNativeRpcApiTask)
            dependsOn(patchPublishedInternalNativeRpcApiTask)
        }
    }
}

tasks.register("patchInternalNativeRpcApiArtifacts") {
    group = "build"
    dependsOn(patchInternalNativeRpcApiTasks)
}

tasks.named("assemble") {
    dependsOn("patchInternalNativeRpcApiArtifacts")
}

abstract class PatchInternalNativeRpcApiTask : JavaExec() {
    @get:InputDirectory
    abstract val inputKlibDir: DirectoryProperty

    @get:OutputFile
    abstract val outputKlibFile: RegularFileProperty

    @get:org.gradle.api.tasks.Input
    abstract val targetPackageName: Property<String>

    @get:org.gradle.api.tasks.Input
    abstract val annotationClassName: Property<String>

    @get:org.gradle.api.tasks.Input
    abstract val annotationDependencyUniqueName: Property<String>

    @TaskAction
    override fun exec() {
        // The patcher reads the unpacked cinterop KLIB directory and writes a packed .klib output.
        args = listOf(
            inputKlibDir.get().asFile.absolutePath,
            outputKlibFile.get().asFile.absolutePath,
            targetPackageName.get(),
            annotationClassName.get(),
            annotationDependencyUniqueName.get(),
        )
        super.exec()
    }
}
