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
import kotlinx.rpc.protoc.android.dependencySourceSets
import kotlinx.rpc.protoc.android.kotlinProxyFromAndroidOriginSourceSetName
import kotlinx.rpc.util.AndroidComponents
import kotlinx.rpc.util.KotlinPluginId
import kotlinx.rpc.util.ensureDirectoryExists
import kotlinx.rpc.util.hasAndroidKmpLibrary
import kotlinx.rpc.util.hasLegacyAndroid
import kotlinx.rpc.util.kotlinPluginId
import kotlinx.rpc.util.withLegacyAndroid
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
            configureMultiplatformWithAndroidSourceSets configure@{ protoSourceSet ->
                // configureTasks is done in configureLegacyAndroidVariants even for KMP
                if (protoSourceSet.isLegacyAndroid.get()) {
                    protoSourceSet.setupDefaultImports(protoSourceSets)
                    return@configure
                }

                configureTasks(protoSourceSet)
            }

            protoSourceSets.all {
                if (this !is DefaultProtoSourceSet) {
                    return@all
                }

                withFullyInitializedProtoSourceSet(this) { protoSourceSet ->
                    // configureTasks is done in configureLegacyAndroidVariants
                    if (protoSourceSet.isLegacyAndroid.get()) {
                        return@withFullyInitializedProtoSourceSet
                    }

                    if (hasAndroidKmpLibrary) {
                        project.tryConfigureKmpLibAndroidVariant(protoSourceSet)
                    }

                    configureTasks(protoSourceSet)
                }

                sourceSetCreated(this)
            }
        }

        project.withLegacyAndroid {
            withLazyLegacyAndroidComponentsExtension {
                configureLegacyAndroidVariants(
                    project = project,
                    isKmp = project.kotlinPluginId == KotlinPluginId.MULTIPLATFORM,
                    onSourceSet = ::whenSourceSetIsCreated,
                    configureTasks = ::configureTasks
                )
            }
        }
    }

    private val sourceSetCallbacks = mutableMapOf<String, MutableList<(DefaultProtoSourceSet) -> Unit>>()
    private fun whenSourceSetIsCreated(name: String?, configure: (DefaultProtoSourceSet) -> Unit) {
        if (name == null) {
            return
        }

        project.protoSourceSets.findByName(name)?.let { protoSourceSet ->
            configure(protoSourceSet as DefaultProtoSourceSet)
            return
        }

        sourceSetCallbacks.computeIfAbsent(name) { mutableListOf() }.add(configure)
    }

    private fun sourceSetCreated(protoSourceSet: DefaultProtoSourceSet) {
        sourceSetCallbacks.remove(protoSourceSet.name).orEmpty().forEach { configure ->
            configure(protoSourceSet)
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
        if (protoSourceSet.tasksConfigured.get()) {
            return
        }

        val baseName = protoSourceSet.name

        val buildSourceSetsDir = project.protoBuildDirSourceSets.resolve(baseName)
            .ensureDirectoryExists()

        val buildSourceSetsProtoDir = buildSourceSetsDir.resolve(PROTO_FILES_DIR)
            .ensureDirectoryExists()

        val buildSourceSetsImportDir = buildSourceSetsDir.resolve(PROTO_FILES_IMPORT_DIR)
            .ensureDirectoryExists()

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

        protoSourceSet.tasksConfigured.set(true)
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
            sourceSet.java.srcDir(javaOutputs)
        }

        project.withKotlin {
            languageSets.filterIsInstance<KotlinSourceSet>().forEach { sourceSet ->
                sourceSet.kotlin.srcDir(kotlinOutputs)
            }
        }

        project.withLegacyAndroid {
            // android + kotlin is always done in withKotlin above
            languageSets.filterIsInstance<AndroidSourceSet>().forEach { sourceSet ->
                sourceSet.java.srcDirs(javaOutputs)
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
        importsAllFrom(getDependsOnImports(protoSourceSets))

        // isLegacyAndroid -> not a 'com.android.kotlin.multiplatform.library' source set,
        // other imports are set up on variant
        if (isLegacyAndroid.get()) {
            return
        }

        if (name.lowercase().endsWith("test")) {
            importsAllFrom(getImportsCorrespondingMainSourceSets(protoSourceSets))
        }
    }

    private fun DefaultProtoSourceSet.getImportsCorrespondingMainSourceSets(
        protoSourceSets: ProtoSourceSets,
    ): Provider<List<ProtoSourceSet>> {
        return languageSourceSets.map { list ->
            val kotlin = list.filterIsInstance<KotlinSourceSet>()
            val java = list.filterIsInstance<SourceSet>()

            kotlin.mapNotNull {
                correspondingMainNameSourceSet(it.name, protoSourceSets)
            } + java.mapNotNull {
                correspondingMainNameSourceSet(it.name, protoSourceSets)
            }.distinct()
        }
    }

    private fun DefaultProtoSourceSet.getDependsOnImports(protoSourceSets: ProtoSourceSets): Provider<List<ProtoSourceSet>> {
        return languageSourceSets.map { list ->
            val kotlin = list.filterIsInstance<KotlinSourceSet>()

            kotlin.flatMap {
                it.dependsOn.mapNotNull { dep -> protoSourceSets.getByName(dep.name) }
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
        sourceSetNames = setOf(protoSourceSet.name),
        buildType = null,
        flavors = emptyList(),
        variant = null,
    )

    protoSourceSet.androidProperties.set(properties)
}

// init isLegacyAndroid
private fun Project.withFullyInitializedProtoSourceSet(
    sourceSet: ProtoSourceSet,
    body: (DefaultProtoSourceSet) -> Unit,
) {
    if (sourceSet !is DefaultProtoSourceSet) {
        return
    }

    val kotlinPluginId = kotlinPluginId

    val languageSets = sourceSet.languageSourceSets.get()

    val anyLegacyAndroid = hasLegacyAndroid && languageSets.any { it is AndroidSourceSet }
    val anyKotlin = kotlinPluginId != null && languageSets.any { it is KotlinSourceSet }

    if (anyLegacyAndroid && !anyKotlin) {
        sourceSet.isLegacyAndroid.set(true)
        body(sourceSet)
        return
    }

    when (kotlinPluginId) {
        KotlinPluginId.ANDROID -> {
            // todo should be .set(anyLegacyAndroid),
            //  but some phantom kotlin source sets ruin the picture.
            //  Should investigate where they are coming from (debugAndroidTest, debugUnitTest, releaseUnitTest).
            sourceSet.isLegacyAndroid.set(true)
            body(sourceSet)
        }

        KotlinPluginId.MULTIPLATFORM -> {
            if (!hasLegacyAndroid || hasAndroidKmpLibrary || !anyKotlin) {
                sourceSet.isLegacyAndroid.set(false)
                body(sourceSet)
            }
            // no else here, it is handled by configureMultiplatformWithAndroidSourceSets
        }

        KotlinPluginId.JVM, null -> {
            sourceSet.isLegacyAndroid.set(false)
            body(sourceSet)
        }
    }
}

private fun Project.configureMultiplatformWithAndroidSourceSets(body: (DefaultProtoSourceSet) -> Unit) {
    if (!hasLegacyAndroid || hasAndroidKmpLibrary || kotlinPluginId != KotlinPluginId.MULTIPLATFORM) {
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

                    if (protoSourceSet.tasksConfigured.orElse(false).get()) {
                        return@forAll
                    }

                    protoSourceSet.isLegacyAndroid.set(target is KotlinAndroidTarget)
                    body(protoSourceSet)
                }
            }
        }
}

private fun AndroidComponents.configureLegacyAndroidVariants(
    project: Project,
    isKmp: Boolean,
    onSourceSet: (String?, (DefaultProtoSourceSet) -> Unit) -> Unit,
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

            val variantProtoSourceSet = project.protoSourceSets.getByName(variantSourceSetName)
                    as? DefaultProtoSourceSet ?: return@forEach

            val proxyNames = mutableListOf<String>()
            (dependencySourceSetNames + testFixtureSetNames)
                .filter { it != variantSourceSetName }
                .forEach { dependencyName ->
                    val dependencyProtoSourceSet = project.protoSourceSets.findByName(dependencyName)
                        ?: return@forEach

                    val proxyName = dependencyName.kotlinProxyFromAndroidOriginSourceSetName(rootName)
                    if (proxyName != null && isKmp) {
                        onSourceSet(proxyName) { proxyDependency ->
                            proxyNames += proxyName
                            variantProtoSourceSet.extendsFrom(proxyDependency)
                        }
                    }

                    variantProtoSourceSet.extendsFrom(dependencyProtoSourceSet)
                }

            // for pure android or kotlin.android the variant source set with the associated tasks are android-like
            // for kmp + legacy android - the tasks are associated with kmp-style source set
            if (isKmp) {
                val variantProtoSourceSetProxyName =
                    variantSourceSetName.kotlinProxyFromAndroidOriginSourceSetName(rootName)

                onSourceSet(variantProtoSourceSetProxyName) { variantProtoSourceSetProxy ->
                    variantProtoSourceSetProxy.extendsFrom(variantProtoSourceSet)

                    if (rootName != LegacyAndroidRootSourceSets.Main) {
                        val mainVariantProtoSourceSetProxyName =
                            variant.name.kotlinProxyFromAndroidOriginSourceSetName(LegacyAndroidRootSourceSets.Main)

                        onSourceSet(mainVariantProtoSourceSetProxyName) { mainVariantProtoSourceSetProxy ->
                            variantProtoSourceSetProxy.androidDependencies.add(mainVariantProtoSourceSetProxy)

                            variantProtoSourceSetProxy.importsFrom(mainVariantProtoSourceSetProxy)
                        }
                    }

                    val sourceSetNames = dependencySourceSetNames + proxyNames + variantProtoSourceSetProxy.name

                    val properties = androidProperties(
                        rootName = rootName,
                        variant = variant,
                        sourceSetNames = sourceSetNames,
                    )

                    variantProtoSourceSetProxy.androidProperties.set(properties)

                    configureTasks(variantProtoSourceSetProxy)
                }
            } else {
                if (rootName != LegacyAndroidRootSourceSets.Main) {
                    val mainVariantProtoSourceSet = project.protoSourceSets.findByName(variant.name)
                            as? DefaultProtoSourceSet

                    if (mainVariantProtoSourceSet != null) {
                        variantProtoSourceSet.androidDependencies.add(mainVariantProtoSourceSet)

                        variantProtoSourceSet.importsFrom(mainVariantProtoSourceSet)
                    }
                }

                val properties = androidProperties(
                    rootName = rootName,
                    variant = variant,
                    sourceSetNames = dependencySourceSetNames,
                )
                variantProtoSourceSet.androidProperties.set(properties)

                configureTasks(variantProtoSourceSet)
            }
        }
    }
}

private fun androidProperties(
    rootName: LegacyAndroidRootSourceSets,
    variant: Variant,
    sourceSetNames: Iterable<String>,
): ProtoTask.AndroidProperties {
    return ProtoTask.AndroidProperties(
        isTest = rootName != LegacyAndroidRootSourceSets.Main,
        isInstrumentedTest = rootName == LegacyAndroidRootSourceSets.AndroidTest,
        isUnitTest = rootName == LegacyAndroidRootSourceSets.Test,
        sourceSetNames = sourceSetNames.toSet(),
        buildType = variant.buildType,
        flavors = variant.productFlavors.map { (_, flavor) -> flavor },
        variant = variant.name,
    )
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
        name.endsWith("HostTest") -> name.removeSuffix("HostTest") + "Main"
        name.endsWith("DeviceTest") -> name.removeSuffix("DeviceTest") + "Main"
        name.endsWith("Test") -> name.removeSuffix("Test") + "Main"
        else -> throw GradleException("Unknown test source set name: $name")
    }
}
