/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
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
import kotlinx.rpc.protoc.android.AndroidRootSourceSets
import kotlinx.rpc.protoc.android.sourceSets
import kotlinx.rpc.util.ensureDirectoryExists
import kotlinx.rpc.util.withLazyAndroidComponentsExtension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
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
            if (this !is DefaultProtoSourceSet || isAndroid) {
                return@all
            }

            project.configureTasks(this)
        }

        project.withLazyAndroidComponentsExtension {
            val rootSourceSets = AndroidRootSourceSets.entries
                .mapNotNull { rootName ->
                    val sourceSet = project.protoSourceSets.findByName(rootName.stringValue)
                            as? DefaultProtoSourceSet

                    sourceSet?.let { rootName to it }
                }.toMap()

            val mainRoot = rootSourceSets.getValue(AndroidRootSourceSets.Main)
            val testRoot = rootSourceSets[AndroidRootSourceSets.Test]
            val androidTestRoot = rootSourceSets[AndroidRootSourceSets.AndroidTest]
            val testFixturesRoot = rootSourceSets[AndroidRootSourceSets.TestFixtures]

            // todo KMP imports

            testRoot?.extendsFrom(testFixturesRoot ?: mainRoot)
            androidTestRoot?.extendsFrom(testFixturesRoot ?: mainRoot)
            testFixturesRoot?.importsFrom(mainRoot)

            val extension = project.the<BaseExtension>()
            onVariants { variant: Variant ->
                val testBuildType = extension.testBuildType
                rootSourceSets.forEach { (rootName, rootSourceSet) ->
                    // testFixtures don't have variants
                    if (rootName == AndroidRootSourceSets.TestFixtures) {
                        return@forEach
                    }

                    if (rootName == AndroidRootSourceSets.AndroidTest && variant.buildType != testBuildType) {
                        return@forEach
                    }

                    // but testFixtures still have source sets based on flavors
                    val testFixtureSets = if (rootName != AndroidRootSourceSets.Main) {
                        variant.sourceSets(AndroidRootSourceSets.TestFixtures)
                    } else emptyList()

                    val sourceSets = variant.sourceSets(rootName)
                    val variantSourceSetName = sourceSets.lastOrNull()
                        ?: throw GradleException("No source sets found for variant ${variant.name}")

                    val variantProtoSourceSet = project.protoSourceSets.named(variantSourceSetName)

                    variantProtoSourceSet.configure {
                        val default = this as? DefaultProtoSourceSet ?: return@configure

                        default.androidRoot.set(rootSourceSet)
                        val properties = BufExecTask.AndroidProperties(
                            isTest = rootName != AndroidRootSourceSets.Main,
                            isAndroidTest = rootName == AndroidRootSourceSets.AndroidTest,
                            isUnitTest = rootName == AndroidRootSourceSets.Test,
                            sourceSetName = variantSourceSetName,
                            buildType = variant.buildType,
                            flavors = variant.productFlavors.map { (_, flavor) -> flavor },
                            variant = variant.name,
                        )
                        default.androidProperties.set(properties)

                        (sourceSets + testFixtureSets).forEach { dependency ->
                            val dependencyProtoSourceSet = project.protoSourceSets.findByName(dependency)
                                ?: return@forEach

                            if (name != dependency) {
                                extendsFrom(dependencyProtoSourceSet)
                            }
                        }

                        if (rootName != AndroidRootSourceSets.Main) {
                            val mainVariantProtoSourceSet = project.protoSourceSets.findByName(variant.name)
                                as? DefaultProtoSourceSet

                            if (mainVariantProtoSourceSet != null) {
                                default.androidMain.set(mainVariantProtoSourceSet)

                                importsFrom(mainVariantProtoSourceSet)
                            }
                        }

                        project.configureTasks(default)
                    }
                }
            }
        }
    }

    private fun ProtocPlugin.defaultOptions() {
        isKotlin.set(true)
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
        protoSourceSet.setupDefaultImports(protoSourceSets)

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

        val protoFilesDirectorySet = protoSourceSet as SourceDirectorySet

        val processProtoTask = registerProcessProtoFilesTask(
            name = baseName,
            destination = buildSourceSetsProtoDir,
            protoFilesDirectorySet = protoFilesDirectorySet,
        )

        val processImportProtoTask = registerProcessProtoFilesImportsTask(
            name = baseName,
            destination = buildSourceSetsImportDir,
            importsProvider = protoSourceSet.imports,
            rawImports = protoSourceSet.fileImports,
        ) {
            dependsOn(processProtoTask)
        }

        val generateBufYamlTask = registerGenerateBufYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            buildSourceSetsProtoDir = buildSourceSetsProtoDir,
            buildSourceSetsImportDir = buildSourceSetsImportDir,
            withImport = protoSourceSet.imports.map { it.isNotEmpty() },
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
            protoSourceSet = protoSourceSet,
            workingDir = buildSourceSetsDir,
            outputDirectory = protoBuildDirGenerated.resolve(baseName),
            includedPlugins = includedProtocPlugins,
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

            protoFiles.set(processProtoTask.map { it.outputs.files })
            importProtoFiles.set(processImportProtoTask.map { it.outputs.files })

            dependsOn(generateBufGenYamlTask)
            dependsOn(generateBufYamlTask)
            dependsOn(processProtoTask)
            dependsOn(processImportProtoTask)

            val dependencies = project.provider {
                protoSourceSet.getDependsOnTasksOf(protoSourceSets).mapNotNull { it.generateTask.orNull }
            }

            dependsOn(dependencies)

            bufTaskDependencies.set(protoSourceSet.imports.map { list ->
                list.filterIsInstance<DefaultProtoSourceSet>().mapNotNull { it.generateTask.orNull?.name }
            })

            onlyIf { !sourceSetsProtoDirFileTree.filter { it.extension == "proto" }.isEmpty }
        }

        protoSourceSet.generateTask.set(bufGenerateTask)

        configureSourceDirectories(
            protoSourceSet = protoSourceSet,
            includedProtocPlugins = includedProtocPlugins,
            bufGenerateTask = bufGenerateTask,
        )

        configureCustomTasks(
            protoSourceSet = protoSourceSet,
            buildSourceSetsDir = buildSourceSetsDir,
            generateBufYamlTask = generateBufYamlTask,
            generateBufGenYamlTask = generateBufGenYamlTask,
            processProtoTask = processProtoTask,
            processImportProtoTask = processImportProtoTask,
            sourceSetsProtoDirFileTree = sourceSetsProtoDirFileTree,
        ) {
            protoFiles.set(processProtoTask.map { it.outputs.files })
            importProtoFiles.set(processImportProtoTask.map { it.outputs.files })
        }
    }

    private fun configureSourceDirectories(
        protoSourceSet: DefaultProtoSourceSet,
        includedProtocPlugins: Provider<Set<ProtocPlugin>>,
        bufGenerateTask: TaskProvider<BufGenerateTask>,
    ) {
        val languageSets = protoSourceSet.languageSourceSets.get()

        val javaOutputs = languageOutputs(includedProtocPlugins, bufGenerateTask) { it.isJava.get() }
        val kotlinOutputs = languageOutputs(includedProtocPlugins, bufGenerateTask) { it.isKotlin.get() }

        languageSets.filterIsInstance<SourceSet>().forEach { sourceSet ->
            sourceSet.java.srcDirs(javaOutputs)
        }

        languageSets.filterIsInstance<KotlinSourceSet>().forEach { sourceSet ->
            sourceSet.kotlin.srcDirs(kotlinOutputs)
        }
    }

    private fun languageOutputs(
        includedProtocPlugins: Provider<Set<ProtocPlugin>>,
        bufGenerateTask: TaskProvider<BufGenerateTask>,
        pluginFilter: (ProtocPlugin) -> Boolean,
    ): Provider<List<File>> {
        val plugins = includedProtocPlugins.map { plugins ->
            plugins.filter(pluginFilter).map { it.name }.toSet()
        }

        return bufGenerateTask.flatMap { task ->
            val pluginsSet = plugins.get()
            task.outputSourceDirectories.map { list -> list.filter { it.name in pluginsSet } }
        }
    }

    private fun Project.configureCustomTasks(
        protoSourceSet: DefaultProtoSourceSet,
        buildSourceSetsDir: File,
        generateBufYamlTask: TaskProvider<GenerateBufYaml>,
        generateBufGenYamlTask: TaskProvider<GenerateBufGenYaml>,
        processProtoTask: TaskProvider<ProcessProtoFiles>,
        processImportProtoTask: TaskProvider<ProcessProtoFiles>,
        sourceSetsProtoDirFileTree: ConfigurableFileTree,
        configure: BufExecTask.() -> Unit,
    ) {
        val baseName = protoSourceSet.name

        buf.tasks.customTasks.all {
            val taskCapital = name.replaceFirstChar { it.uppercase() }
            fun taskName(baseName: String): String {
                val baseCapital = baseName.replaceFirstChar { it.uppercase() }
                return "buf$taskCapital$baseCapital"
            }

            registerBufExecTask(
                clazz = kClass,
                workingDir = provider { buildSourceSetsDir },
                properties = protoSourceSet.bufExecProperties(),
                name = taskName(baseName),
            ) {
                dependsOn(generateBufYamlTask)
                dependsOn(generateBufGenYamlTask)
                dependsOn(processProtoTask)
                dependsOn(processImportProtoTask)

                val dependencies = project.provider {
                    protoSourceSet.getDependsOnTasksOf(protoSourceSets).map { dependency ->
                        project.tasks.named(taskName(dependency.name), kClass.java).get()
                    }
                }

                dependsOn(dependencies)

                bufTaskDependencies.set(protoSourceSet.imports.map { list ->
                    list.mapNotNull { dependency ->
                        project.tasks.findByName(taskName(dependency.name))?.name
                    }
                })

                configure()

                onlyIf { !sourceSetsProtoDirFileTree.filter { it.extension == "proto" }.isEmpty }
            }
        }
    }

    private fun DefaultProtoSourceSet.setupDefaultImports(protoSourceSets: ProtoSourceSets) {
        if (isAndroid) {
            // imports are set up on variant
            return
        }

        val calculatedImports: Provider<List<ProtoSourceSet>> = when {
            name.lowercase().endsWith("main") -> {
                getImportsForTestOrMain(protoSourceSets)
            }

            name.lowercase().endsWith("test") -> {
                val test = getImportsForTestOrMain(protoSourceSets)
                val main = (correspondingMainNameSourceSet(name, protoSourceSets) as? DefaultProtoSourceSet)
                    ?.getImportsForTestOrMain(protoSourceSets)

                if (main == null) test else test.zip(main) { a, b -> a + b }
            }

            else -> {
                project.provider { emptyList() }
            }
        }

        importsAllFrom(calculatedImports)
    }

    private fun DefaultProtoSourceSet.getImportsForTestOrMain(
        protoSourceSets: ProtoSourceSets,
    ): Provider<List<ProtoSourceSet>> {
        return languageSourceSets.map { list ->
            val kotlin = list.filterIsInstance<KotlinSourceSet>()
            val java = list.filterIsInstance<SourceSet>()

            kotlin.flatMap {
                it.dependsOn.mapNotNull { dep -> protoSourceSets.getByName(dep.name) }
            } + kotlin.mapNotNull {
                correspondingMainNameSourceSet(it.name, protoSourceSets)
            } + java.mapNotNull {
                correspondingMainNameSourceSet(it.name, protoSourceSets)
            }
        }
    }
}

/**
 * Return a list of [DefaultProtoSourceSet] that have tasks that [this] will depend on.
 * It's a different list from [DefaultProtoSourceSet.imports],
 * as not all [DefaultProtoSourceSet] have associated tasks (on Android, for example)
 */
internal fun DefaultProtoSourceSet.getDependsOnTasksOf(protoSourceSets: ProtoSourceSets): List<DefaultProtoSourceSet> {
    // todo check KMP
    // androidMain/androidTest for KMP are evaluated as usual (they don't have androidRoot prop)
    // androidRoot.isPresent == this is not androidMain/androidTest
    if (isAndroid && androidRoot.isPresent) {
        return listOfNotNull(androidRoot.get(), androidMain.orNull)
    }

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

    val main = correspondingMainNameSourceSet(name, protoSourceSets) as? DefaultProtoSourceSet

    return (kmpDependsOn + main).filterNotNull()
}

private fun correspondingMainNameSourceSet(name: String, protoSourceSets: ProtoSourceSets): ProtoSourceSet? {
    return if (name.lowercase().endsWith("test")) {
        protoSourceSets.findByName(correspondingMainName(name))
    } else {
        null
    }
}

private fun correspondingMainName(name: String): String {
    return when {
        name == "test" -> "main"
        name.endsWith("Test") -> name.removeSuffix("Test") + "Main"
        else -> throw GradleException("Unknown test source set name: $name")
    }
}
