/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import com.android.build.api.dsl.AndroidSourceSet
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
import kotlinx.rpc.protoc.android.KmpLibraryAndroidLeafSourceSets
import kotlinx.rpc.protoc.android.LegacyAndroidRootSourceSets
import kotlinx.rpc.protoc.android.androidOriginFromKotlinProxySourceSetName
import kotlinx.rpc.protoc.android.dependencySourceSets
import kotlinx.rpc.protoc.android.kotlinProxyFromAndroidOriginSourceSetName
import kotlinx.rpc.util.ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY
import kotlinx.rpc.util.AndroidComponents
import kotlinx.rpc.util.KotlinPluginId
import kotlinx.rpc.util.ensureDirectoryExists
import kotlinx.rpc.util.hasAndroid
import kotlinx.rpc.util.kotlinPluginId
import kotlinx.rpc.util.withAndroid
import kotlinx.rpc.util.withKotlin
import kotlinx.rpc.util.withLazyLegacyAndroidComponentsExtension
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
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.utils.ObservableSet
import java.io.File
import javax.inject.Inject
import kotlin.collections.filterIsInstance
import kotlin.collections.filterNotNull
import kotlin.collections.plus

internal open class DefaultProtocExtension @Inject constructor(
    objects: ObjectFactory,
    private val project: Project,
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

        // no way to configure tasks before evaluation is done
        project.afterEvaluate {
            configureMultiplatformAndroidSourceSets configure@{ protoSourceSet ->
                // done in configureLegacyAndroidVariants
                if (protoSourceSet.isLegacyAndroid.get()) {
                    protoSourceSet.setupDefaultImports(protoSourceSets)
                    val originSourceSetName = protoSourceSet.name.androidOriginFromKotlinProxySourceSetName()
                        ?: return@configure

                    val originSourceSet = protoSourceSets.findByName(originSourceSetName)
                        ?: return@configure

                    originSourceSet.extendsFrom(protoSourceSet)

                    return@configure
                }

                configureTasks(protoSourceSet)
            }

            protoSourceSets.all {
                withFullyInitializedProtoSourceSet(this) { protoSourceSet ->
                    // done in configureLegacyAndroidVariants
                    if (protoSourceSet.isLegacyAndroid.get()) {
                        return@withFullyInitializedProtoSourceSet
                    }

                    if (project.plugins.hasPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY)) {
                        project.tryConfigureKmpLibAndroidVariant(protoSourceSet)
                    }

                    configureTasks(protoSourceSet)
                }
            }
        }

        project.withAndroid {
            if (project.plugins.hasPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY)) {
                // done in tryConfigureKmpLibAndroidVariant
                return@withAndroid
            }

            withLazyLegacyAndroidComponentsExtension {
                configureLegacyAndroidVariants(
                    project = project,
                    isKmp = project.kotlinPluginId == KotlinPluginId.MULTIPLATFORM
                ) {
                    configureTasks(it)
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
    private fun configureTasks(protoSourceSet: DefaultProtoSourceSet) {
        val baseName = protoSourceSet.name

        val buildSourceSetsDir = project.protoBuildDirSourceSets.resolve(baseName)
            .ensureDirectoryExists()

        val buildSourceSetsProtoDir = buildSourceSetsDir.resolve(PROTO_FILES_DIR)
            .ensureDirectoryExists()

        val buildSourceSetsImportDir = buildSourceSetsDir.resolve(PROTO_FILES_IMPORT_DIR)
            .ensureDirectoryExists()

        // only resolve in task's 'execute' due to the deferred nature of dependsOn
        protoSourceSet.setupDefaultImports(project.protoSourceSets)

        val includedProtocPlugins = project.provider {
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
        val properties = protoSourceSet.protoTaskProperties()

        val processProtoTask = project.registerProcessProtoFilesTask(
            name = baseName,
            destination = buildSourceSetsProtoDir,
            protoFilesDirectorySet = protoFilesDirectorySet,
            properties = properties,
        )

        val processImportProtoTask = project.registerProcessProtoFilesImportsTask(
            name = baseName,
            destination = buildSourceSetsImportDir,
            importsProvider = protoSourceSet.imports,
            rawImports = protoSourceSet.fileImports,
            properties = properties,
        ) {
            dependsOn(processProtoTask)
        }

        val generateBufYamlTask = project.registerGenerateBufYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            buildSourceSetsProtoDir = buildSourceSetsProtoDir,
            buildSourceSetsImportDir = buildSourceSetsImportDir,
            withImport = protoSourceSet.imports.map { it.isNotEmpty() },
            properties = properties,
        ) {
            dependsOn(processProtoTask)
        }

        val generateBufGenYamlTask = project.registerGenerateBufGenYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            protocPlugins = includedProtocPlugins,
            properties = properties,
        ) {
            dependsOn(generateBufYamlTask)
        }

        val sourceSetsProtoDirFileTree = project.fileTree(buildSourceSetsProtoDir)

        val bufGenerateTask = project.registerBufGenerateTask(
            protoSourceSet = protoSourceSet,
            workingDir = buildSourceSetsDir,
            outputDirectory = project.protoBuildDirGenerated.resolve(baseName),
            includedPlugins = includedProtocPlugins,
            properties = properties,
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
                protoSourceSet.getDependsOnTasksOf(project.protoSourceSets).mapNotNull { it.generateTask.orNull }
            }

            dependsOn(dependencies)

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
            properties = properties,
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

        project.withKotlin {
            languageSets.filterIsInstance<KotlinSourceSet>().forEach { sourceSet ->
                sourceSet.kotlin.srcDirs(kotlinOutputs)
            }
        }

        project.withAndroid {
            languageSets.filterIsInstance<AndroidSourceSet>().forEach { sourceSet ->
                sourceSet.java.srcDirs(javaOutputs)
            }

            languageSets.filterIsInstance<AndroidSourceSet>().forEach { sourceSet ->
                sourceSet.kotlin.srcDirs(kotlinOutputs)
            }
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

    private fun configureCustomTasks(
        protoSourceSet: DefaultProtoSourceSet,
        buildSourceSetsDir: File,
        generateBufYamlTask: TaskProvider<GenerateBufYaml>,
        generateBufGenYamlTask: TaskProvider<GenerateBufGenYaml>,
        processProtoTask: TaskProvider<ProcessProtoFiles>,
        processImportProtoTask: TaskProvider<ProcessProtoFiles>,
        sourceSetsProtoDirFileTree: ConfigurableFileTree,
        properties: ProtoTask.Properties,
        configure: BufExecTask.() -> Unit,
    ) {
        val baseName = protoSourceSet.name

        buf.tasks.customTasks.all {
            val taskCapital = name.replaceFirstChar { it.uppercase() }
            fun taskName(baseName: String): String {
                val baseCapital = baseName.replaceFirstChar { it.uppercase() }
                return "buf$taskCapital$baseCapital"
            }

            project.registerBufExecTask(
                clazz = kClass,
                workingDir = project.provider { buildSourceSetsDir },
                properties = properties,
                name = taskName(baseName),
            ) {
                dependsOn(generateBufYamlTask)
                dependsOn(generateBufGenYamlTask)
                dependsOn(processProtoTask)
                dependsOn(processImportProtoTask)

                val dependencies = project.provider {
                    protoSourceSet.getDependsOnTasksOf(project.protoSourceSets).map { dependency ->
                        project.tasks.named(taskName(dependency.name), kClass.java).get()
                    }
                }

                dependsOn(dependencies)

                configure()

                onlyIf { !sourceSetsProtoDirFileTree.filter { it.extension == "proto" }.isEmpty }
            }
        }
    }

    private fun DefaultProtoSourceSet.setupDefaultImports(protoSourceSets: ProtoSourceSets) {
        // isKotlinProxyLegacyAndroid -> this source set has proper dependsOn, that need to be set up
        // isLegacyAndroid -> not a 'com.android.kotlin.multiplatform.library' source set, imports are set up on variant
        if (!isKotlinProxyLegacyAndroid.get() && isLegacyAndroid.get()) {
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

// not considered propper variants, they fit kmp source sets and configured as them,
// except for androidProperties
private fun Project.tryConfigureKmpLibAndroidVariant(protoSourceSet: DefaultProtoSourceSet) {
    val sourceSetEntry = KmpLibraryAndroidLeafSourceSets.entries
        .find { it.sourceSetName == protoSourceSet.name }
        ?: return

    // no testFixtures, because they can have a dependency on commonTest in dependOn section
    if (sourceSetEntry == KmpLibraryAndroidLeafSourceSets.Main) {
        val unitTestSourceSet = project.protoSourceSets
            .findByName(KmpLibraryAndroidLeafSourceSets.HostTest.sourceSetName)

        val instrumentedTestSourceSet = project.protoSourceSets
            .findByName(KmpLibraryAndroidLeafSourceSets.DeviceTest.sourceSetName)

        unitTestSourceSet?.importsFrom(protoSourceSet)
        instrumentedTestSourceSet?.importsFrom(protoSourceSet)
    } else {
        val mainRoot = project.protoSourceSets
            .findByName(KmpLibraryAndroidLeafSourceSets.Main.sourceSetName)

        if (mainRoot != null) {
            protoSourceSet.importsFrom(mainRoot)
        }
    }

    val properties = ProtoTask.AndroidProperties(
        isTest = sourceSetEntry != KmpLibraryAndroidLeafSourceSets.Main,
        isInstrumentedTest = sourceSetEntry == KmpLibraryAndroidLeafSourceSets.DeviceTest,
        isUnitTest = sourceSetEntry == KmpLibraryAndroidLeafSourceSets.HostTest,
        sourceSetName = protoSourceSet.name,
        buildType = null,
        flavors = emptyList(),
        variant = null,
    )

    protoSourceSet.androidProperties.set(properties)
}

// init isLegacyAndroid, isKotlinProxyLegacyAndroid
private fun Project.withFullyInitializedProtoSourceSet(
    sourceSet: ProtoSourceSet,
    body: (DefaultProtoSourceSet) -> Unit,
) {
    if (sourceSet !is DefaultProtoSourceSet) {
        return
    }

    val kotlinPluginId = kotlinPluginId

    val languageSets = sourceSet.languageSourceSets.get()

    val anyAndroid = hasAndroid && languageSets.any { it is AndroidSourceSet }
    val anyKotlin = kotlinPluginId != null && languageSets.any { it is KotlinSourceSet }

    if (anyAndroid && !anyKotlin) {
        sourceSet.isLegacyAndroid.set(true)
        sourceSet.isKotlinProxyLegacyAndroid.set(false)
        body(sourceSet)
        return
    }

    when (kotlinPluginId) {
        KotlinPluginId.ANDROID -> {
            sourceSet.isLegacyAndroid.set(true)
            sourceSet.isKotlinProxyLegacyAndroid.set(false)
            body(sourceSet)
        }

        KotlinPluginId.JVM -> {
            sourceSet.isLegacyAndroid.set(false)
            sourceSet.isKotlinProxyLegacyAndroid.set(false)
            body(sourceSet)
        }

        KotlinPluginId.MULTIPLATFORM -> {
            if (plugins.hasPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY) || !hasAndroid || !anyKotlin) {
                sourceSet.isLegacyAndroid.set(false)
                sourceSet.isKotlinProxyLegacyAndroid.set(false)
                body(sourceSet)
            }
            // no else here, it is handled by configureMultiplatformAndroidSourceSets
        }

        null -> {
            sourceSet.isLegacyAndroid.set(false)
            sourceSet.isKotlinProxyLegacyAndroid.set(false)
            body(sourceSet)
        }
    }
}

private fun Project.configureMultiplatformAndroidSourceSets(body: (DefaultProtoSourceSet) -> Unit) {
    if (!hasAndroid || plugins.hasPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY) || kotlinPluginId != KotlinPluginId.MULTIPLATFORM) {
        return
    }

    project.the<KotlinMultiplatformExtension>()
        .targets.all {
            val target = this

            compilations.all {
                val compilationSourceSets = allKotlinSourceSets as ObservableSet<KotlinSourceSet>

                compilationSourceSets.forAll {
                    val protoSourceSet = protoSourceSets.getByName(it.name)
                            as? DefaultProtoSourceSet ?: return@forAll

                    val isAndroid = target is KotlinAndroidTarget
                    protoSourceSet.isLegacyAndroid.set(isAndroid)
                    protoSourceSet.isKotlinProxyLegacyAndroid.set(isAndroid)
                    body(protoSourceSet)
                }
            }
        }
}

private fun AndroidComponents.configureLegacyAndroidVariants(
    project: Project,
    isKmp: Boolean,
    configureTasks: (DefaultProtoSourceSet) -> Unit,
) {
    val rootSourceSets = LegacyAndroidRootSourceSets.entries
        .mapNotNull { rootName ->
            val sourceSet = project.protoSourceSets.findByName(rootName.stringValue)
                    as? DefaultProtoSourceSet

            sourceSet?.let { rootName to it }
        }.toMap()

    val mainRoot = rootSourceSets.getValue(LegacyAndroidRootSourceSets.Main)
    val testRoot = rootSourceSets[LegacyAndroidRootSourceSets.Test]
    val androidTestRoot = rootSourceSets[LegacyAndroidRootSourceSets.AndroidTest]
    val testFixturesRoot = rootSourceSets[LegacyAndroidRootSourceSets.TestFixtures]

    testRoot?.extendsFrom(testFixturesRoot ?: mainRoot)
    androidTestRoot?.extendsFrom(testFixturesRoot ?: mainRoot)
    testFixturesRoot?.importsFrom(mainRoot)

    val extension = project.the<BaseExtension>()
    onVariants { variant: Variant ->
        val testBuildType = extension.testBuildType
        rootSourceSets.forEach { (rootName) ->
            // testFixtures don't have variants
            if (rootName == LegacyAndroidRootSourceSets.TestFixtures) {
                return@forEach
            }

            if (rootName == LegacyAndroidRootSourceSets.AndroidTest && variant.buildType != testBuildType) {
                return@forEach
            }

            // but testFixtures still have source sets based on flavors
            val testFixtureSetNames = if (rootName != LegacyAndroidRootSourceSets.Main) {
                variant.dependencySourceSets(LegacyAndroidRootSourceSets.TestFixtures)
            } else emptyList()

            val dependencySourceSetNames = variant.dependencySourceSets(rootName)
            val variantSourceSetName = dependencySourceSetNames.lastOrNull()
                ?: throw GradleException("No source sets found for variant ${variant.name}")

            val variantProtoSourceSet = project.protoSourceSets.named(variantSourceSetName)

            variantProtoSourceSet.configure {
                val default = this as? DefaultProtoSourceSet ?: return@configure

                val properties = ProtoTask.AndroidProperties(
                    isTest = rootName != LegacyAndroidRootSourceSets.Main,
                    isInstrumentedTest = rootName == LegacyAndroidRootSourceSets.AndroidTest,
                    isUnitTest = rootName == LegacyAndroidRootSourceSets.Test,
                    sourceSetName = variantSourceSetName,
                    buildType = variant.buildType,
                    flavors = variant.productFlavors.map { (_, flavor) -> flavor },
                    variant = variant.name,
                )
                default.androidProperties.set(properties)

                (dependencySourceSetNames + testFixtureSetNames).forEach { dependencyName ->
                    val dependencyProtoSourceSet = project.protoSourceSets.findByName(dependencyName)
                        ?: return@forEach

                    val proxyName = dependencyName.kotlinProxyFromAndroidOriginSourceSetName(rootName)
                    // can be also null, if not yet configured on the KGP side
                    val proxyDependency = if (isKmp && proxyName != null) {
                        project.protoSourceSets.findByName(proxyName) as? DefaultProtoSourceSet
                    } else {
                        null
                    }

                    if (proxyDependency != null) {
                        default.extendsFrom(proxyDependency)
                    }

                    if (name != dependencyName) {
                        default.extendsFrom(dependencyProtoSourceSet)
                    }
                }

                if (rootName != LegacyAndroidRootSourceSets.Main) {
                    val mainVariantProtoSourceSet = project.protoSourceSets.findByName(variant.name)
                            as? DefaultProtoSourceSet

                    if (mainVariantProtoSourceSet != null) {
                        default.androidDependencies.add(mainVariantProtoSourceSet)

                        default.importsFrom(mainVariantProtoSourceSet)
                    }
                }

                if (isKmp) {
                    if (rootName == LegacyAndroidRootSourceSets.Main) {
                        val commonMain = project.protoSourceSets
                            .findByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
                                as? DefaultProtoSourceSet

                        if (commonMain != null) {
                            default.androidDependencies.add(commonMain)
                        }
                    } else {
                        val commonTest = project.protoSourceSets
                            .findByName(KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME)
                                as? DefaultProtoSourceSet

                        if (commonTest != null) {
                            default.androidDependencies.add(commonTest)
                        }
                    }
                }

                configureTasks(default)
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
    // isLegacyAndroid -> this is not KMP android library source set ->
    // -> this is a source set from configureLegacyAndroidVariants -> only two possible dependencies:
    //  - if pure android - main variant (if this is a test variant: debug for testDebug, release for testRelease, etc.)
    //  - if KMP - also commonMain or commonTest correspondingly
    //  (and if commonTest - it will already have a 'dependsOn' commonMain)
    //
    // so in both cases we store it in androidDependencies
    if (isLegacyAndroid.get()) {
        return androidDependencies.get().toList()
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
