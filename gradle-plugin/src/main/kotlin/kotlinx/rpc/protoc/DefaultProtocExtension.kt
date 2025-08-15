/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.BufExtension
import kotlinx.rpc.buf.configureBufExecutable
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.buf.tasks.GenerateBufGenYaml
import kotlinx.rpc.buf.tasks.GenerateBufYaml
import kotlinx.rpc.buf.tasks.registerBufExecTask
import kotlinx.rpc.buf.tasks.registerBufGenerateTask
import kotlinx.rpc.buf.tasks.registerGenerateBufGenYamlTask
import kotlinx.rpc.buf.tasks.registerGenerateBufYamlTask
import kotlinx.rpc.util.ensureDirectoryExists
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.io.File
import javax.inject.Inject

internal open class DefaultProtocExtension @Inject constructor(
    objects: ObjectFactory,
    private val project: Project,
) : ProtocExtension {
    override val buf: BufExtension = project.objects.newInstance<BufExtension>()
    override fun buf(action: Action<BufExtension>) {
        action.execute(buf)
    }

    init {
        project.configureBufExecutable()
        project.configureKotlinMultiplatformPluginJarConfiguration()
        project.configureGrpcKotlinMultiplatformPluginJarConfiguration()

        // ignore for bufGenerate task caching
        project.normalization.runtimeClasspath.ignore("**/protoc-gen-kotlin-multiplatform.log")
        project.normalization.runtimeClasspath.ignore("**/protoc-gen-grpc-kotlin-multiplatform.log")
        project.normalization.runtimeClasspath.ignore("**/.keep")

        project.protoSourceSets.all {
            if (this !is DefaultProtoSourceSet) {
                return@all
            }

            project.configureTasks(this)
        }
    }

    @Suppress("detekt.LongMethod", "detekt.CyclomaticComplexMethod", "detekt.ThrowsCount")
    private fun Project.configureTasks(protoSourceSet: DefaultProtoSourceSet) {
        val baseName = protoSourceSet.name

        val buildSourceSetsDir = project.protoBuildDirSourceSets.resolve(baseName)
            .ensureDirectoryExists()

        val buildSourceSetsProtoDir = buildSourceSetsDir.resolve(PROTO_FILES_DIR)
            .ensureDirectoryExists()

        val buildSourceSetsImportDir = buildSourceSetsDir.resolve(PROTO_FILES_IMPORT_DIR)
            .ensureDirectoryExists()

        val pairSourceSet = protoSourceSet.correspondingMainSourceSetOrNull()

        val includedProtocPlugins = provider {
            protoSourceSet.plugins.distinct()
        }

        val protoFiles = protoSourceSet.proto

        val processProtoTask = registerProcessProtoFilesTask(
            name = baseName,
            destination = buildSourceSetsProtoDir,
            protoFiles = protoFiles,
        )

        val processImportProtoTask = if (pairSourceSet != null) {
            val importProtoFiles = pairSourceSet.proto

            registerProcessProtoFilesTask(
                name = "${baseName}Import",
                destination = buildSourceSetsImportDir,
                protoFiles = importProtoFiles,
            ) {
                dependsOn(processProtoTask)
            }
        } else {
            null
        }

        val generateBufYamlTask = registerGenerateBufYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            buildSourceSetsProtoDir = buildSourceSetsProtoDir,
            buildSourceSetsImportDir = buildSourceSetsImportDir,
            withImport = pairSourceSet != null,
        ) {
            dependsOn(processProtoTask)
            if (processImportProtoTask != null) {
                dependsOn(processImportProtoTask)
            }
        }

        val generateBufGenYamlTask = registerGenerateBufGenYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            protocPlugins = includedProtocPlugins,
        ) {
            dependsOn(generateBufYamlTask)
        }

        val sourceSetsProtoDirFileTree = fileTree(buildSourceSetsProtoDir)

        val bufGenerateTask = registerBufGenerateTask(
            name = baseName,
            workingDir = buildSourceSetsDir,
            outputDirectory = protoBuildDirGenerated.resolve(baseName),
            protoFilesDir = buildSourceSetsProtoDir,
            importFilesDir = buildSourceSetsImportDir,
        ) {
            executableFiles.addAll(
                includedProtocPlugins.map { list ->
                    list.flatMap { plugin ->
                        plugin.artifact.get().let {
                            if (it is ProtocPlugin.Artifact.Local) {
                                it.executableFiles.get()
                            } else {
                                emptyList()
                            }
                        }
                    }
                }
            )

            dependsOn(generateBufGenYamlTask)
            dependsOn(generateBufYamlTask)
            dependsOn(processProtoTask)
            if (processImportProtoTask != null) {
                dependsOn(processImportProtoTask)
            }

            if (pairSourceSet != null) {
                dependsOn(pairSourceSet.generateTask)
            }

            onlyIf { !sourceSetsProtoDirFileTree.filter { it.extension == "proto" }.isEmpty }
        }

        protoSourceSet.generateTask.set(bufGenerateTask)

        project.tasks.withType<KotlinCompilationTask<*>>().all {
            // compileKotlin - main
            // compileTestKotlin - test
            // compileKotlinJvm - jvmMain
            // compileTestKotlinJvm - jvmTest
            // compileKotlinIosArm64 - iosArm64Main
            // compileTestKotlinIosArm64 - iosArm64Test
            val isTest = name.startsWith("compileTest")
            if (isTest && baseName.lowercase().endsWith("test")) {
                dependsOn(bufGenerateTask)
            } else if (!isTest && baseName.lowercase().endsWith("main")) {
                dependsOn(bufGenerateTask)
            }
        }

        project.tasks.withType<JavaCompile>().all {
            // compileJvmTestJava - test (java, kmp)
            // compileJvmMainJava - main (java, kmp)
            // compileJava - main (java)
            // compileTestJava - test (java)
            val taskNameAsSourceSet = when (name) {
                "compileJvmTestJava" -> "test"
                "compileJvmMainJava" -> "main"
                "compileJava" -> "main"
                "compileTestJava" -> "test"

                else -> throw GradleException("Unknown java compile task name: $name")
            }

            if (taskNameAsSourceSet == baseName) {
                dependsOn(bufGenerateTask)
            }
        }

        configureAfterEvaluate(
            baseName = baseName,
            protoSourceSet = protoSourceSet,
            buildSourceSetsDir = buildSourceSetsDir,
            includedProtocPlugins = includedProtocPlugins,
            generateBufYamlTask = generateBufYamlTask,
            generateBufGenYamlTask = generateBufGenYamlTask,
            processProtoTask = processProtoTask,
            processImportProtoTask = processImportProtoTask,
            bufGenerateTask = bufGenerateTask,
            sourceSetsProtoDirFileTree = sourceSetsProtoDirFileTree,
        )
    }

    private fun Project.configureAfterEvaluate(
        baseName: String,
        protoSourceSet: DefaultProtoSourceSet,
        buildSourceSetsDir: File,
        includedProtocPlugins: Provider<List<ProtocPlugin>>,
        generateBufYamlTask: TaskProvider<GenerateBufYaml>,
        generateBufGenYamlTask: TaskProvider<GenerateBufGenYaml>,
        processProtoTask: TaskProvider<ProcessProtoFiles>,
        processImportProtoTask: TaskProvider<ProcessProtoFiles>?,
        bufGenerateTask: TaskProvider<BufGenerateTask>,
        sourceSetsProtoDirFileTree: ConfigurableFileTree,
    ) = afterEvaluate {
        val out = bufGenerateTask.get().outputDirectory.get()
        val plugins = includedProtocPlugins.get()

        plugins.forEach { plugin ->
            // locates correctly jvmMain, main jvmTest, test
            val javaSourceSet = extensions.findByType<JavaPluginExtension>()
                ?.sourceSets?.findByName(baseName)?.java

            if (plugin.isJava.get() && javaSourceSet != null) {
                javaSourceSet.srcDirs(out.resolve(plugin.name))
            } else {
                protoSourceSet.languageSourceSets.get().find { it is KotlinSourceSet }?.let {
                    (it as KotlinSourceSet)
                        .kotlin.srcDirs(out.resolve(plugin.name))
                } ?: error(
                    "Unable to find fitting source directory set " +
                            "for plugin '${plugin.name}' in '$protoSourceSet' proto source set"
                )
            }
        }

        val baseCapital = baseName.replaceFirstChar { it.uppercase() }
        buf.tasks.customTasks.get().forEach { definition ->
            val capital = definition.name.replaceFirstChar { it.uppercase() }
            val taskName = "buf$capital$baseCapital"

            val customTask = registerBufExecTask(
                clazz = definition.kClass,
                workingDir = provider { buildSourceSetsDir },
                name = taskName,
            ) {
                dependsOn(generateBufYamlTask)
                dependsOn(generateBufGenYamlTask)
                dependsOn(processProtoTask)
                if (processImportProtoTask != null) {
                    dependsOn(processImportProtoTask)
                }

                onlyIf { !sourceSetsProtoDirFileTree.filter { it.extension == "proto" }.isEmpty }
            }

            when {
                baseName.lowercase().endsWith("main") -> {
                    definition.property.mainTask.set(customTask)
                }

                baseName.lowercase().endsWith("test") -> {
                    definition.property.testTask.set(customTask)
                }

                else -> {
                    throw GradleException("Unknown source set name: $baseName")
                }
            }
        }
    }

    private fun DefaultProtoSourceSet.correspondingMainSourceSetOrNull(): DefaultProtoSourceSet? {
        return when {
            name.lowercase().endsWith("main") -> {
                null
            }

            name.lowercase().endsWith("test") -> {
                project.protoSourceSets.findByName(correspondingMainName()) as? DefaultProtoSourceSet
            }

            else -> {
                throw GradleException("Unknown source set name: $name")
            }
        }
    }

    private fun ProtoSourceSet.correspondingMainName(): String {
        return when {
            name == "test" -> "main"
            name.endsWith("Test") -> name.removeSuffix("Test") + "Main"
            else -> throw GradleException("Unknown test source set name: $name")
        }
    }
}
