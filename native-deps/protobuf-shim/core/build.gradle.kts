/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Zip
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
import util.registerSyncBazelModuleVersionTask
import util.toTaskSuffix

/**
 * Configures a Kotlin Multiplatform project to build and publish the protobuf shim KLIB.
 *
 * This build process includes:
 * 1. Building the target-specific `protowire_fat` static library with Bazel.
 * 2. Running cinterop against the published protowire headers and static library.
 * 3. Patching the produced cinterop KLIB so the generated declarations require
 *    explicit opt-in via `InternalNativeRpcApi`.
 */
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("conventions-publishing")
}

val protobufVersion = providers.gradleProperty("protobufVersion").get()
val protowireInteropName = "libprotowire"
val protowireInteropTaskName = protowireInteropName.replaceFirstChar { it.uppercase() }
val protowireInteropDefFile = layout.projectDirectory.file("src/nativeInterop/cinterop/libprotowire.def")
val protobufInteropPackageName = "kotlinx.rpc.protobuf.internal.cinterop"
val protobufInternalNativeRpcApiClassName = "kotlinx/rpc/protobuf/internal/InternalNativeRpcApi"
val protobufInternalNativeRpcApiDependencyUniqueName = "org.jetbrains.kotlinx\\:kotlinx-rpc-protobuf-shim-annotation"
val protobufShimTargets = nativeDependencyTargets
val protobufShimModuleFile = layout.projectDirectory.file("MODULE.bazel").asFile

// cinterop does not load our compiler plugin on this build path, so protobuf-shim uses a dedicated
// internal KLIB patcher to add the opt-in marker after cinterop and before publication.
val internalNativeRpcApiPatcherProject = project(":klib-patcher")
val internalNativeRpcApiPatcherJar = internalNativeRpcApiPatcherProject.layout.buildDirectory.file(
    "libs/${internalNativeRpcApiPatcherProject.name}-${project.version}.jar",
)

val konanHome = konanHomeProvider()
val checkBazel = registerCheckBazelTask()
val prepareKonanHome = registerPrepareKonanHomeTask(
    downloadTaskPath = ":protobuf:protobuf-api:downloadKotlinNativeDistribution",
)
val checkKonanHome = registerCheckKonanHomeTask(
    prepareKonanHome = prepareKonanHome,
    konanHome = konanHome,
)
val syncProtobufVersionToBazelModule = registerSyncBazelModuleVersionTask(
    moduleFile = protobufShimModuleFile,
    version = protobufVersion,
    variableName = "PROTOBUF_VERSION",
    name = "syncProtobufVersionToBazelModule",
)

val patchInternalNativeRpcApiTasks = mutableListOf<TaskProvider<out Task>>()

kotlin {
    registerNativeDependencyTargets()

    sourceSets {
        nativeMain {
            dependencies {
                // The cinterop dependency configuration resolves from the implementation side,
                // while downstream consumers pick up the marker through the patched KLIB metadata.
                implementation(project(":kotlinx-rpc-protobuf-shim-annotation"))
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        val target = protobufShimTargets.single { it.bazelName == konanTarget.visibleName }
        val taskSuffix = target.bazelName.toTaskSuffix()
        val publicationTaskSuffix = target.kotlinName.replaceFirstChar { it.uppercase() }
        val shimLibDir = layout.buildDirectory.dir("protobuf-shim/${target.bazelName}")
        val shimLibFile = layout.buildDirectory.file("protobuf-shim/${target.bazelName}/libprotowire_fat.${target.bazelName}.a")
        val packedCinteropKlibFile = layout.buildDirectory.file(
            "libs/${project.name}-${target.kotlinName}Cinterop-$protowireInteropName" +
                "Main-$version.klib",
        )

        val buildProtobufShim = tasks.register<Exec>("buildProtobufShim${taskSuffix}") {
            dependsOn(syncProtobufVersionToBazelModule, checkBazel, checkKonanHome)
            group = "build"
            workingDir = layout.projectDirectory.asFile
            outputs.file(shimLibFile)
            commandLine(
                "./build_target.sh",
                ":protowire_fat",
                shimLibFile.get().asFile.absolutePath,
                target.bazelName,
                konanHome.get(),
            )
        }

        compilations.getByName("main").cinterops.create(protowireInteropName) {
            defFile(protowireInteropDefFile.asFile)
            includeDirs(layout.projectDirectory.dir("include").asFile)
            extraOpts("-libraryPath", shimLibDir.get().asFile.absolutePath)
        }

        val cinteropTaskName = "cinterop$protowireInteropTaskName${target.kotlinName.replaceFirstChar { it.uppercase() }}"
        val cinteropTask = tasks.named(cinteropTaskName, CInteropProcess::class) {
            dependsOn(buildProtobufShim)
        }
        val cinteropPublicationTaskName = "${target.kotlinName}Cinterop-$protowireInteropName" + "Klib"
        val cinteropPublicationTask = tasks.named(cinteropPublicationTaskName, Zip::class)

        // Patch the local build output in build/libs so local inspection and downstream publication keep
        // the build graph explicit around the post-cinterop metadata rewrite.
        val patchInternalNativeRpcApiTask = tasks.register<PatchInternalNativeRpcApiTask>("patchInternalNativeRpcApi${taskSuffix}") {
            dependsOn(cinteropTask)
            dependsOn(":klib-patcher:jar")

            classpath(files(internalNativeRpcApiPatcherJar))
            mainClass.set("kotlinx.rpc.nativedeps.tooling.KlibPatcher")

            inputKlibDir.set(cinteropTask.flatMap { it.klibDirectory })
            outputKlibFile.set(packedCinteropKlibFile)
            targetPackageName.set(protobufInteropPackageName)
            annotationClassName.set(protobufInternalNativeRpcApiClassName)
            annotationDependencyUniqueName.set(protobufInternalNativeRpcApiDependencyUniqueName)
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

            inputKlibDir.set(cinteropTask.flatMap { it.klibDirectory })
            outputKlibFile.set(cinteropPublicationTask.flatMap { it.archiveFile })
            targetPackageName.set(protobufInteropPackageName)
            annotationClassName.set(protobufInternalNativeRpcApiClassName)
            annotationDependencyUniqueName.set(protobufInternalNativeRpcApiDependencyUniqueName)
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
        args(
            inputKlibDir.get().asFile.absolutePath,
            outputKlibFile.get().asFile.absolutePath,
            targetPackageName.get(),
            annotationClassName.get(),
            annotationDependencyUniqueName.get(),
        )
        super.exec()
    }
}
