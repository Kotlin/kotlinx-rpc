/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
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
    val patcherJar: Provider<RegularFile>,
    val patchTasks: MutableList<TaskProvider<out Task>>,
)

/**
 * Normalized per-target values passed from the shared target loop into each
 * module-specific callback.
 *
 * This keeps the module build scripts from re-deriving task suffixes and local
 * output paths for every target.
 */
data class NativeShimTargetContext(
    val target: NativeDependencyTarget,
    val taskSuffix: String,
    val publicationTaskSuffix: String,
    val packedCinteropKlibFile: Provider<RegularFile>,
)

/**
 * Shared bootstrap for native-deps shim modules.
 *
 * Shim implementations need the same surrounding build machinery:
 * Bazel availability checks, Kotlin/Native distribution bootstrapping, Bazel
 * module version synchronization, and access to the shared KLIB patcher tool.
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
    val patcherProject = project(":klib-patcher")
    val patcherJar = patcherProject.layout.buildDirectory.file(
        "libs/${patcherProject.name}-${patcherProject.version}.jar",
    )

    return NativeShimBuildSupport(
        targets = nativeDependencyTargets,
        konanHome = konanHome,
        checkBazel = checkBazel,
        checkKonanHome = checkKonanHome,
        syncModuleVersionTask = syncModuleVersionTask,
        patcherJar = patcherJar,
        patchTasks = mutableListOf(),
    )
}

/**
 * Registers the standard Bazel-backed static library build task used by shim modules.
 *
 * The actual script lives once under native-deps/shims and expects to be run from
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
        layout.projectDirectory.dir("..").file("build_target.sh").asFile.absolutePath,
        label,
        outputFile.get().asFile.absolutePath,
        target.bazelName,
        nativeShim.konanHome.get(),
    )
}

/**
 * Registers the two KLIB patching tasks for one target:
 *
 * 1. patch the unpacked cinterop output and repack it into build/libs for
 *    local inspection and explicit task graph visibility
 * 2. patch the published cinterop archive so downstream consumers see the
 *    opt-in annotation in the artifact that actually gets resolved
 *
 * Publication metadata tasks depend on both patch steps so we never publish
 * metadata that points at an unpatched cinterop artifact.
 */
fun Project.registerNativeShimApiPatching(
    taskSuffix: String,
    publicationTaskSuffix: String,
    cinteropTask: TaskProvider<CInteropProcess>,
    cinteropPublicationTask: TaskProvider<Zip>,
    packedCinteropKlibFile: Provider<RegularFile>,
    targetPackageName: String,
    annotationClassName: String,
    annotationDependencyUniqueName: String,
    patcherJar: Provider<RegularFile>,
    patchTasks: MutableList<TaskProvider<out Task>>,
) {
    val inputKlibDirProvider = cinteropTask.flatMap { it.klibDirectory }
    fun PatchNativeShimApiTask.configurePatchTask(outputKlibFile: Provider<RegularFile>) {
        dependsOn(":klib-patcher:jar")
        classpath(files(patcherJar))
        mainClass.set("kotlinx.rpc.nativedeps.tooling.KlibPatcher")
        workingDir = layout.projectDirectory.asFile
        inputs.file(patcherJar)

        inputKlibDir.set(inputKlibDirProvider)
        this.outputKlibFile.set(outputKlibFile)
        this.targetPackageName.set(targetPackageName)
        this.annotationClassName.set(annotationClassName)
        this.annotationDependencyUniqueName.set(annotationDependencyUniqueName)
    }

    val patchInternalNativeRpcApiTask = tasks.register<PatchNativeShimApiTask>("patchInternalNativeRpcApi${taskSuffix}") {
        dependsOn(cinteropTask)
        configurePatchTask(packedCinteropKlibFile)
    }
    patchTasks.add(patchInternalNativeRpcApiTask)

    val patchPublishedInternalNativeRpcApiTask = tasks.register<PatchNativeShimApiTask>(
        "patchPublishedInternalNativeRpcApi${taskSuffix}",
    ) {
        dependsOn(cinteropPublicationTask)
        configurePatchTask(cinteropPublicationTask.flatMap { it.archiveFile })
    }

    tasks.matching { task ->
        task.name == "generateMetadataFileFor${publicationTaskSuffix}Publication" ||
            task.name == "generatePomFileFor${publicationTaskSuffix}Publication" ||
            task.name == "patchModuleJsonFor${publicationTaskSuffix}"
    }.configureEach {
        dependsOn(patchInternalNativeRpcApiTask)
        dependsOn(patchPublishedInternalNativeRpcApiTask)
    }
}

/**
 * Shared per-target orchestration for shim cinterop publications.
 *
 * Each shim module still describes how to prepare its own target-specific
 * native inputs and how to configure the cinterop itself. This helper takes
 * care of the repeated scaffolding around that:
 *
 * - map the Kotlin/Native target to the native-deps target descriptor
 * - derive stable task/publication suffixes
 * - compute the local packed KLIB path under build/libs
 * - locate the generated cinterop and publication tasks by name
 * - attach the post-cinterop KLIB patching flow
 *
 * The [configureTarget] callback should register the module-specific per-target
 * tasks and return the tasks that the generated cinterop task must depend on.
 */
fun KotlinMultiplatformExtension.configureNativeShimTargets(
    nativeShim: NativeShimBuildSupport,
    interopName: String,
    targetPackageName: String,
    annotationClassName: String,
    annotationDependencyUniqueName: String,
    configureTarget: KotlinNativeTarget.(NativeShimTargetContext) -> List<Any>,
) {
    val interopTaskName = interopName.replaceFirstChar { it.uppercase() }

    targets.withType<KotlinNativeTarget>().configureEach {
        val target = nativeShim.targets.single { it.bazelName == konanTarget.visibleName }
        val taskSuffix = target.bazelName.toTaskSuffix()
        val publicationTaskSuffix = target.kotlinName.replaceFirstChar { it.uppercase() }
        val packedCinteropKlibFile = project.layout.buildDirectory.file(
            "libs/${project.name}-${target.kotlinName}Cinterop-$interopName" +
                "Main-${project.version}.klib",
        )
        val context = NativeShimTargetContext(
            target = target,
            taskSuffix = taskSuffix,
            publicationTaskSuffix = publicationTaskSuffix,
            packedCinteropKlibFile = packedCinteropKlibFile,
        )

        val cinteropDependencies = configureTarget(context)
        val cinteropTaskName = "cinterop$interopTaskName$publicationTaskSuffix"
        val cinteropTask = project.tasks.named(cinteropTaskName, CInteropProcess::class) {
            dependsOn(cinteropDependencies)
        }
        val cinteropPublicationTaskName = "${target.kotlinName}Cinterop-$interopName" + "Klib"
        val cinteropPublicationTask = project.tasks.named(cinteropPublicationTaskName, Zip::class)

        project.registerNativeShimApiPatching(
            taskSuffix = taskSuffix,
            publicationTaskSuffix = publicationTaskSuffix,
            cinteropTask = cinteropTask,
            cinteropPublicationTask = cinteropPublicationTask,
            packedCinteropKlibFile = packedCinteropKlibFile,
            targetPackageName = targetPackageName,
            annotationClassName = annotationClassName,
            annotationDependencyUniqueName = annotationDependencyUniqueName,
            patcherJar = nativeShim.patcherJar,
            patchTasks = nativeShim.patchTasks,
        )
    }

    project.registerNativeShimPatchArtifactsTask(nativeShim.patchTasks)
}

/**
 * Adds a single aggregate task for all per-target KLIB patching work and
 * attaches it to assemble so local builds also produce patched artifacts.
 */
fun Project.registerNativeShimPatchArtifactsTask(
    patchTasks: MutableList<TaskProvider<out Task>>,
    taskName: String = "patchInternalNativeRpcApiArtifacts",
) {
    tasks.register(taskName) {
        group = "build"
        dependsOn(patchTasks)
    }

    tasks.named("assemble") {
        dependsOn(taskName)
    }
}

/**
 * JVM task wrapper around the shared KLIB patcher CLI.
 *
 * The CLI itself lives in the shims-internal `:klib-patcher` module; this task
 * just binds Gradle inputs/outputs to the command-line arguments expected by
 * that tool.
 */
abstract class PatchNativeShimApiTask : org.gradle.api.tasks.JavaExec() {
    @get:InputDirectory
    abstract val inputKlibDir: DirectoryProperty

    @get:OutputFile
    abstract val outputKlibFile: RegularFileProperty

    @get:Input
    abstract val targetPackageName: Property<String>

    @get:Input
    abstract val annotationClassName: Property<String>

    @get:Input
    abstract val annotationDependencyUniqueName: Property<String>

    @TaskAction
    override fun exec() {
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
