/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.BufExtension
import kotlinx.rpc.buf.configureBufExecutable
import kotlinx.rpc.buf.tasks.BufExecTask
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.buf.tasks.GenerateBufGenYaml
import kotlinx.rpc.buf.tasks.GenerateBufYaml
import kotlinx.rpc.buf.tasks.registerBufExecTask
import kotlinx.rpc.buf.tasks.registerBufGenerateTask
import kotlinx.rpc.buf.tasks.registerGenerateBufGenYamlTask
import kotlinx.rpc.buf.tasks.registerGenerateBufYamlTask
import kotlinx.rpc.protoc.ProtocPlugin.Companion.GRPC_KOTLIN_MULTIPLATFORM
import kotlinx.rpc.protoc.ProtocPlugin.Companion.KOTLIN_MULTIPLATFORM
import kotlinx.rpc.util.ensureDirectoryExists
import kotlinx.rpc.util.kotlinJvmExtensionOrNull
import kotlinx.rpc.util.kotlinKmpExtensionOrNull
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.io.File
import javax.inject.Inject
import kotlin.collections.filterIsInstance
import kotlin.collections.filterNotNull
import kotlin.collections.plus

internal open class DefaultProtocExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
) : ProtocExtension {
    override val buf: BufExtension = project.objects.newInstance<BufExtension>()
    override fun buf(action: Action<BufExtension>) {
        action.execute(buf)
    }

    override val plugins: NamedDomainObjectContainer<ProtocPlugin> =
        project.objects.domainObjectContainer(ProtocPlugin::class.java) { name ->
            ProtocPlugin(name, project)
        }

    override fun plugins(action: Action<NamedDomainObjectContainer<ProtocPlugin>>) {
        action.execute(plugins)
    }

    init {
        project.configureBufExecutable()
        project.configureKotlinMultiplatformPluginJarConfiguration()
        project.configureGrpcKotlinMultiplatformPluginJarConfiguration()

        // ignore for bufGenerate task caching
        project.normalization.runtimeClasspath.ignore("**/protoc-gen-kotlin-multiplatform.log")
        project.normalization.runtimeClasspath.ignore("**/protoc-gen-grpc-kotlin-multiplatform.log")
        project.normalization.runtimeClasspath.ignore("**/.keep")

        plugins.create(KOTLIN_MULTIPLATFORM) {
            local {
                javaJar(project.kotlinMultiplatformProtocPluginJarPath)
            }

            defaultOptions()
        }

        plugins.create(GRPC_KOTLIN_MULTIPLATFORM) {
            local {
                javaJar(project.grpcKotlinMultiplatformProtocPluginJarPath)
            }

            defaultOptions()
        }

        project.protoSourceSets.all {
            if (this !is DefaultProtoSourceSet) {
                return@all
            }

            project.configureTasks(this)
        }
    }

    private fun ProtocPlugin.defaultOptions() {
        options.put("debugOutput", "protoc-gen-$name.log")

        options.put("generateComments", buf.generate.comments.copyComments)
        options.put("generateFileLevelComments", buf.generate.comments.includeFileLevelComments)
        options.put("indentSize", buf.generate.indentSize)
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

        // only resolve in task's 'execute' due to the deferred nature of dependsOn
        val importsProvider = protoSourceSet.getImports(protoSourceSets)

        val includedProtocPlugins = provider {
            protoSourceSet.plugins.get().also { list ->
                list.forEach { plugin ->
                    if (!plugin.artifact.isPresent) {
                        throw GradleException(
                            "Artifact is not specified for protoc plugin ${plugin.name}. " +
                                    "Use `local {}` or `remote {}` to specify it."
                        )
                    }
                }
            }
        }

        val protoFiles = protoSourceSet as SourceDirectorySet

        val processProtoTask = registerProcessProtoFilesTask(
            name = baseName,
            destination = buildSourceSetsProtoDir,
            protoFiles = protoFiles,
        )

        val processImportProtoTask = registerProcessProtoFilesImportsTask(
            name = baseName,
            destination = buildSourceSetsImportDir,
            importsProvider = importsProvider,
        ) {
            dependsOn(processProtoTask)
        }

        val generateBufYamlTask = registerGenerateBufYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            buildSourceSetsProtoDir = buildSourceSetsProtoDir,
            buildSourceSetsImportDir = buildSourceSetsImportDir,
            withImport = importsProvider.map { it.isNotEmpty() },
        ) {
            dependsOn(processProtoTask)
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
            sourceSetName = baseName,
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
            dependsOn(processImportProtoTask)

            val dependencies = project.provider {
                protoSourceSet.getDependsOn(protoSourceSets).map { it.generateTask.get() }
            }

            dependsOn(dependencies)

            bufTaskDependencies.set(importsProvider.map { list ->
                list.map { it.generateTask.get().name }
            })

            onlyIf { !sourceSetsProtoDirFileTree.filter { it.extension == "proto" }.isEmpty }
        }

        protoSourceSet.generateTask.set(bufGenerateTask)

        val compilationNameTestTag = if (baseName.lowercase().endsWith("test")) "Test" else ""
        val compileTargetName = baseName.replaceFirstChar { it.uppercase() }
            .removeSuffix("Main")
            .removeSuffix("Test")
            .removeSuffix("main")
            .removeSuffix("test")

        // compileKotlin - main
        // compileTestKotlin - test
        // compileKotlinJvm - jvmMain
        // compileTestKotlinJvm - jvmTest
        // compileKotlinIosArm64 - iosArm64Main
        // compileTestKotlinIosArm64 - iosArm64Test
        val kotlinCompilationName = "compile${compilationNameTestTag}Kotlin${compileTargetName}"

        project.tasks.withType<KotlinCompilationTask<*>>().all {
            if (name == kotlinCompilationName) {
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

        configureSourceDirectories(
            baseName = baseName,
            includedProtocPlugins = includedProtocPlugins,
            bufGenerateTask = bufGenerateTask,
        )

        configureCustomTasks(
            baseName = baseName,
            protoSourceSet = protoSourceSet,
            buildSourceSetsDir = buildSourceSetsDir,
            generateBufYamlTask = generateBufYamlTask,
            generateBufGenYamlTask = generateBufGenYamlTask,
            processProtoTask = processProtoTask,
            processImportProtoTask = processImportProtoTask,
            sourceSetsProtoDirFileTree = sourceSetsProtoDirFileTree,
            importsProvider = importsProvider,
        )
    }

    private fun Project.configureSourceDirectories(
        baseName: String,
        includedProtocPlugins: Provider<Set<ProtocPlugin>>,
        bufGenerateTask: TaskProvider<BufGenerateTask>,
    ) {
        // locates correctly jvmMain, main jvmTest, test
        extensions.findByType<JavaPluginExtension>()?.sourceSets?.all {
            val javaSourceSet = this

            if (javaSourceSet.name == baseName) {
                val javaSourcesProvider = includedProtocPlugins.map { plugins ->
                    val out = bufGenerateTask.get().outputDirectory.get()
                    plugins.filter { it.isJava.get() }.map { out.resolve(it.name) }
                }

                javaSourceSet.java.srcDirs(javaSourcesProvider)
            }
        }

        val kotlinSourcesProvider = includedProtocPlugins.map { plugins ->
            val out = bufGenerateTask.get().outputDirectory.get()
            plugins.filter { !it.isJava.get() }.map { out.resolve(it.name) }
        }

        project.kotlinJvmExtensionOrNull?.sourceSets?.all {
            if (name == baseName) {
                kotlin.srcDirs(kotlinSourcesProvider)
            }
        }

        project.kotlinKmpExtensionOrNull?.sourceSets?.all {
            if (name == baseName) {
                kotlin.srcDirs(kotlinSourcesProvider)
            }
        }
    }

    private fun Project.configureCustomTasks(
        baseName: String,
        protoSourceSet: DefaultProtoSourceSet,
        buildSourceSetsDir: File,
        generateBufYamlTask: TaskProvider<GenerateBufYaml>,
        generateBufGenYamlTask: TaskProvider<GenerateBufGenYaml>,
        processProtoTask: TaskProvider<ProcessProtoFiles>,
        processImportProtoTask: TaskProvider<ProcessProtoFiles>,
        sourceSetsProtoDirFileTree: ConfigurableFileTree,
        importsProvider: Provider<List<DefaultProtoSourceSet>>,
    ) {
        buf.tasks.customTasks.all {
            val taskCapital = name.replaceFirstChar { it.uppercase() }
            fun taskName(baseName: String): String {
                val baseCapital = baseName.replaceFirstChar { it.uppercase() }
                return "buf$taskCapital$baseCapital"
            }

            val properties = BufExecTask.Properties(
                isTest = baseName.lowercase().endsWith("test"),
                sourceSetName = baseName,
            )

            registerBufExecTask(
                clazz = kClass,
                workingDir = provider { buildSourceSetsDir },
                properties = properties,
                name = taskName(baseName),
            ) {
                dependsOn(generateBufYamlTask)
                dependsOn(generateBufGenYamlTask)
                dependsOn(processProtoTask)
                dependsOn(processImportProtoTask)

                val dependencies = project.provider {
                    protoSourceSet.getDependsOn(protoSourceSets).map { dependency ->
                        project.tasks.named(taskName(dependency.name), kClass.java).get()
                    }
                }

                dependsOn(dependencies)

                bufTaskDependencies.set(importsProvider.map { list ->
                    list.map { dependency ->
                        project.tasks.named(taskName(dependency.name), kClass.java).get().name
                    }
                })

                onlyIf { !sourceSetsProtoDirFileTree.filter { it.extension == "proto" }.isEmpty }
            }
        }
    }

    private fun DefaultProtoSourceSet.getImports(
        protoSourceSets: ProtoSourceSets,
    ): Provider<List<DefaultProtoSourceSet>> {
        return when {
            name.lowercase().endsWith("main") -> {
                getImportsForTestOrMain(protoSourceSets)
            }

            name.lowercase().endsWith("test") -> {
                val main = getImportsForTestOrMain(protoSourceSets)
                val test = (project.protoSourceSets.findByName(correspondingMainName()) as? DefaultProtoSourceSet)
                    ?.getImportsForTestOrMain(protoSourceSets)

                if (test == null) main else main.zip(test) { a, b -> a + b }
            }

            else -> {
                throw GradleException("Unknown source set name: $name")
            }
        }
    }

    private fun DefaultProtoSourceSet.getImportsForTestOrMain(
        protoSourceSets: ProtoSourceSets,
    ): Provider<List<DefaultProtoSourceSet>> {
        return languageSourceSets.map { list ->
            list.filterIsInstance<KotlinSourceSet>().flatMap {
                it.dependsOn.mapNotNull { dependency ->
                    (protoSourceSets.getByName(dependency.name) as? DefaultProtoSourceSet)
                }.flatMap { proto ->
                    // can't use plus because DefaultProtoSourceSet is Iterable
                    proto.getImportsForTestOrMain(protoSourceSets).get().toMutableList().apply {
                        add(proto)
                    }
                }
            } + list.filterIsInstance<KotlinSourceSet>().mapNotNull {
                if (it.name.endsWith("Test")) {
                    project.protoSourceSets.findByName(correspondingMainName()) as? DefaultProtoSourceSet
                } else {
                    null
                }
            } + list.filterIsInstance<SourceSet>().mapNotNull {
                if (it.name == SourceSet.TEST_SOURCE_SET_NAME) {
                    project.protoSourceSets.findByName(correspondingMainName()) as? DefaultProtoSourceSet
                } else {
                    null
                }
            }
        }
    }
}

internal fun DefaultProtoSourceSet.getDependsOn(protoSourceSets: ProtoSourceSets): List<DefaultProtoSourceSet> {
    val sourceSets = languageSourceSets.get()

    val kmpDependsOn = sourceSets
        .filterIsInstance<KotlinSourceSet>()
        .flatMap {
            it.dependsOn.map { dependency -> dependency.name }
        }
        .distinct()
        .mapNotNull {
            protoSourceSets.getByName(it) as? DefaultProtoSourceSet
        }

    val kmp = if (name.endsWith("Test")) {
        (protoSourceSets.getByName(correspondingMainName()) as? DefaultProtoSourceSet)
    } else {
        null
    }

    val jvm = if (name == SourceSet.TEST_SOURCE_SET_NAME) {
        (protoSourceSets.getByName(correspondingMainName()) as? DefaultProtoSourceSet)
    } else {
        null
    }

    return (kmpDependsOn + kmp + jvm).filterNotNull()
}

private fun Named.correspondingMainName(): String {
    return when {
        name == "test" -> "main"
        name.endsWith("Test") -> name.removeSuffix("Test") + "Main"
        else -> throw GradleException("Unknown test source set name: $name")
    }
}
