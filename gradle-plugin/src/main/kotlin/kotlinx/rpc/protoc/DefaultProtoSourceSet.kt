/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.rpcExtension
import kotlinx.rpc.util.extendsFromLazy
import kotlinx.rpc.util.findOrCreate
import kotlinx.rpc.util.withLegacyAndroid
import kotlinx.rpc.util.withAndroidSourceSets
import kotlinx.rpc.util.withKotlin
import kotlinx.rpc.util.withLazyJavaPluginExtension
import kotlinx.rpc.util.withKotlinSourceSets
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File
import java.util.*
import java.util.function.Consumer
import javax.inject.Inject
import kotlin.contracts.ExperimentalContracts

@Suppress("UNCHECKED_CAST")
internal val Project.protoSourceSets: ProtoSourceSets
    get() = extensions.findByName(PROTO_SOURCE_SETS) as? ProtoSourceSets
        ?: throw GradleException("Unable to find proto source sets in project $name")

internal class ProtoSourceSetFactory(
    private val project: Project,
) : NamedDomainObjectFactory<ProtoSourceSet> {
    override fun create(name: String): ProtoSourceSet {
        return project.objects.newInstance(DefaultProtoSourceSet::class.java, project, name)
    }
}

internal fun Project.findOrCreateProtoSourceSets(): NamedDomainObjectContainer<ProtoSourceSet> =
    findOrCreate(PROTO_SOURCE_SETS) {
        val container = objects.domainObjectContainer(
            ProtoSourceSet::class.java,
            ProtoSourceSetFactory(project)
        )

        project.extensions.add<ProtoSourceSets>(PROTO_SOURCE_SETS, container)

        container
    }

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
internal open class DefaultProtoSourceSet(
    internal val project: Project,
    internal val sourceDirectorySet: SourceDirectorySet,
) : ProtoSourceSet, SourceDirectorySet by sourceDirectorySet {
    @Inject
    constructor(
        project: Project,
        protoName: String,
    ) : this(
        project = project,
        sourceDirectorySet = project.objects.sourceDirectorySet(protoName, "Proto sources for $protoName").apply {
            srcDirs("src/${protoName}/proto")
        },
    )

    private val explicitApiModeEnabled = project.provider {
        val isMain = androidProperties.orNull?.isTest?.not() ?: name.lowercase().endsWith("main")

        isMain && project.the<KotlinProjectExtension>().explicitApi == ExplicitApiMode.Strict
    }

    override val includeDefaultProtobufPlugin: Property<Boolean> = project.objects.property<Boolean>().convention(true)
    override val includeDefaultGrpcPlugin: Property<Boolean> = project.objects.property<Boolean>().convention(true)

    override val plugins = project.objects.setProperty<ProtocPlugin>()

    override fun plugin(plugin: ProtocPlugin, configure: Action<ProtocPlugin>?) {
        plugins.add(plugin.copy().also { initPlugin(it, configure) })
    }

    override fun plugin(provider: NamedDomainObjectProvider<ProtocPlugin>, configure: Action<ProtocPlugin>?) {
        plugins.add(provider.map { plugin -> plugin.copy().also { initPlugin(it, configure) } })
    }

    override fun plugin(provider: Provider<ProtocPlugin>, configure: Action<ProtocPlugin>?) {
        plugins.add(provider.map { plugin -> plugin.copy().also { initPlugin(it, configure) } })
    }

    override fun plugin(
        configure: Action<ProtocPlugin>?,
        select: NamedDomainObjectContainer<ProtocPlugin>.() -> ProtocPlugin,
    ) {
        plugins.add(project.rpcExtension().protoc.map { protoc ->
            protoc.plugins.select().copy().also { initPlugin(it, configure) }
        })
    }

    internal val platformOption = project.objects.property<String>().convention("")

    private fun initPlugin(copy: ProtocPlugin, configure: Action<ProtocPlugin>?) {
        copy.options.put("explicitApiModeEnabled", explicitApiModeEnabled)
        copy.options.put("platform", platformOption)

        configure?.execute(copy)
    }

    init {
        project.rpcExtension().whenProtocApplied {
            plugin(plugins.kotlinMultiplatform)
            plugin(plugins.grpcKotlinMultiplatform)
        }
    }

    override val bsrDeps: ProtocBufDeps = project.objects.newInstance(ProtocBufDeps::class.java, name)

    override fun bsrDeps(configure: Action<ProtocBufDeps>) {
        configure.execute(bsrDeps)
    }

    val tasksConfigured: Property<Boolean> = project.objects.property<Boolean>()
        .convention(false)

    // Collection of AndroidSourceSet, KotlinSourceSet, SourceSet (java) associated with this proto source set
    val languageSourceSets: ListProperty<Any> = project.objects.listProperty<Any>()
    val generateTask: Property<BufGenerateTask> = project.objects.property<BufGenerateTask>()

    // source set that is associated with com.android.(application|library|test|dynamic-feature) plugin
    //
    // androidMain/androidHostTest/androidDeviceTest source sets for com.android.kotlin.multiplatform.library
    // are NOT considered to be legacy Android source sets
    //
    // androidDebug, androidInstrumentedTest, androidInstrumentedTestDebug, androidMain, etc. from
    // the combination of com.android.* and kotlin.(multiplatform|android) are considered legacy Android source sets
    internal val isLegacyAndroid: Property<Boolean> = project.objects.property<Boolean>()
        .convention(false)

    // used to track tasks' dependencies for main/commonMain/commonTest tasks, e.g.:
    // - bufGenerateTestDebug depends on bufGenerateDebug
    // - bufGenerateTestDebug depends on bufGenerateCommonTest and bufGenerateDebug for KMP
    //
    // So we need to track this dependency using this property
    val androidDependencies: SetProperty<DefaultProtoSourceSet> = project.objects.setProperty()

    // only set for variant.name sourceSets
    val androidProperties: Property<ProtoTask.AndroidProperties> = project.objects.property()

    // Proto dependency configuration for this source set, created when protoc is activated.
    // Allows users to declare proto dependencies via `dependencies { <name>Proto("...") }`.
    // Resolved artifacts are extracted and included in code generation.
    internal val protoConfiguration: Configuration

    // Proto import dependency configuration for this source set, created when protoc is activated.
    // Allows users to declare proto import dependencies via `dependencies { <name>ProtoImport("...") }`.
    // Resolved artifacts are extracted and available as imports, but not for code generation.
    private val protoImportConfigurationNew: Configuration

    // Configurations attached lazily via importsFrom(Provider<...>) / importsAllFrom that cannot
    // be wired through Configuration.extendsFrom: Gradle 8.8–9.3 has no Provider-based overload at
    // all, and no Gradle version exposes a Provider<List<Configuration>> overload. Their files are
    // merged into [protoImportFiles] at the consumer site, so resolution still sees them.
    private val protoImportConfigurationLegacyList: ListProperty<Configuration> = project.objects.listProperty()

    // Aggregated proto-import inputs for tasks: declared dependencies plus the transitive closure
    // built up via Configuration.extendsFrom, plus everything stored in protoImportConfigOverflow.
    internal val protoImportConfiguration: FileCollection by lazy {
        project.files(protoImportConfigurationNew, protoImportConfigurationLegacyList)
    }

    init {
        val protoConfigName = protoConfigurationName(name)
        protoConfiguration = project.configurations.maybeCreate(protoConfigName).apply {
            isCanBeResolved = true
            isCanBeConsumed = false
            description = "Proto file dependencies for source set '$name' (code generation)"
        }

        val protoImportConfigName = protoImportConfigurationName(name)
        protoImportConfigurationNew = project.configurations.maybeCreate(protoImportConfigName).apply {
            isCanBeResolved = true
            isCanBeConsumed = false
            description = "Proto file import dependencies for source set '$name' (imports only)"
        }
    }

    override val imports: SetProperty<ProtoSourceSet> = project.objects.setProperty()
    override val fileImports: ConfigurableFileCollection = project.objects.fileCollection()

    override fun importsFrom(rawProtoSourceSet: ProtoSourceSet) {
        val protoSourceSet = rawProtoSourceSet.asDefault("extend")

        imports.add(protoSourceSet.checkSelfImport())
        imports.addAll(protoSourceSet.imports.checkSelfImport())

        protoImportConfigurationNew.extendsFrom(protoSourceSet.protoImportConfigurationNew)
        protoImportConfigurationLegacyList.addAll(protoSourceSet.protoImportConfigurationLegacyList)

        bsrDeps.modules.addAll(protoSourceSet.bsrDeps.modules)
    }

    override fun importsFrom(rawProtoSourceSet: Provider<ProtoSourceSet>) {
        val protoSourceSet = rawProtoSourceSet.asDefault("extend")

        imports.add(protoSourceSet.checkSelfImport())
        imports.addAll(protoSourceSet.flatMap { it.imports.checkSelfImport() })

        protoImportConfigurationNew.extendsFromLazy(
            legacyList = protoImportConfigurationLegacyList,
            provider = protoSourceSet.map { it.protoImportConfigurationNew },
        )
        protoImportConfigurationLegacyList.addAll(protoSourceSet.flatMap { it.protoImportConfigurationLegacyList })

        bsrDeps.modules.addAll(protoSourceSet.flatMap { it.bsrDeps.modules })
    }

    override fun importsAllFrom(rawProtoSourceSets: Provider<List<ProtoSourceSet>>) {
        val protoSourceSets = rawProtoSourceSets.asDefault("extend")

        imports.addAll(protoSourceSets.checkSelfImport())
        imports.addAll(protoSourceSets.map { list -> list.flatMap { it.imports.checkSelfImport().get() } })

        protoImportConfigurationLegacyList.addAll(
            protoSourceSets.map { list -> list.map { it.protoImportConfigurationNew } },
        )
        protoImportConfigurationLegacyList.addAll(
            protoSourceSets.map { list -> list.flatMap { it.protoImportConfigurationLegacyList.get() } },
        )

        bsrDeps.modules.addAll(
            protoSourceSets.map { list -> list.flatMap { it.bsrDeps.modules.get() } }
        )
    }

    private val extendsFrom: MutableSet<ProtoSourceSet> = mutableSetOf()

    override fun extendsFrom(rawProtoSourceSet: ProtoSourceSet) {
        if (extendsFrom.contains(rawProtoSourceSet)) {
            return
        }

        require(this != rawProtoSourceSet) {
            "$name proto source set cannot extend from self"
        }

        val protoSourceSet = rawProtoSourceSet.asDefault("extend")

        extendsFrom += protoSourceSet

        source(protoSourceSet.sourceDirectorySet)
        imports.addAll(protoSourceSet.imports.checkSelfImport())

        plugins.addAll(protoSourceSet.plugins)

        bsrDeps.modules.addAll(protoSourceSet.bsrDeps.modules)

        // Wire Gradle configuration inheritance for proto dependency configurations
        protoConfiguration.extendsFrom(protoSourceSet.protoConfiguration)
        protoImportConfigurationNew.extendsFrom(protoSourceSet.protoImportConfigurationNew)
        protoImportConfigurationLegacyList.addAll(protoSourceSet.protoImportConfigurationLegacyList)
    }

    @JvmName("checkSelfImport_provider")
    private fun Provider<DefaultProtoSourceSet>.checkSelfImport() = map {
        it.checkSelfImport()
    }

    private fun SetProperty<ProtoSourceSet>.checkSelfImport() = map { set ->
        set.onEach { it.checkSelfImport() }
    }

    @JvmName("checkSelfImport_provider_list")
    private fun Provider<List<DefaultProtoSourceSet>>.checkSelfImport() = map { set ->
        set.onEach { it.checkSelfImport() }
    }

    private fun ProtoSourceSet.checkSelfImport(): ProtoSourceSet {
        require(this@DefaultProtoSourceSet != this) {
            "${this@DefaultProtoSourceSet.name} proto source set cannot import from itself"
        }

        return this
    }

    private fun Provider<ProtoSourceSet>.asDefault(action: String): Provider<DefaultProtoSourceSet> {
        return map { it.asDefault(action) }
    }

    @JvmName("asDefault_provider_list")
    private fun Provider<List<ProtoSourceSet>>.asDefault(action: String): Provider<List<DefaultProtoSourceSet>> {
        return map { lists -> lists.map { it.asDefault(action) } }
    }

    @OptIn(ExperimentalContracts::class)
    private fun ProtoSourceSet.asDefault(action: String): DefaultProtoSourceSet {
        require(this is DefaultProtoSourceSet) {
            "$name proto source set can only $action from other default proto source sets." +
                "${this.name} is not a ${DefaultProtoSourceSet::class.simpleName}"
        }

        return this
    }

    // Java default methods

    override fun forEach(action: Consumer<in File>?) {
        sourceDirectorySet.forEach(action)
    }

    override fun spliterator(): Spliterator<File> {
        return sourceDirectorySet.spliterator()
    }
}

internal fun Project.createProtoExtensions() {
    fun findOrCreateAndConfigure(
        languageSourceSetName: String,
        languageSourceSet: Any?,
    ): DefaultProtoSourceSet {
        val container = findOrCreateProtoSourceSets()
        val protoSourceSet = container.maybeCreate(languageSourceSetName) as DefaultProtoSourceSet

        languageSourceSet?.let { protoSourceSet.languageSourceSets.add(it) }
        return protoSourceSet
    }

    // CCE free check for kotlin
    withKotlin {
        withKotlinSourceSets { extension ->
            extension.sourceSets.all {
                findOrCreateAndConfigure(name, this)
            }
        }
    }

    // CCE free check for android
    withLegacyAndroid {
        withAndroidSourceSets { sourceSets ->
            sourceSets.all {
                findOrCreateAndConfigure(name, this)
            }
        }
    }

    withLazyJavaPluginExtension {
        sourceSets.all {
            val protoSourceSet = findOrCreateAndConfigure(name, this)

            findOrCreate(PROTO_SOURCE_SET_EXTENSION_NAME) {
                extensions.add(PROTO_SOURCE_SET_EXTENSION_NAME, protoSourceSet)
            }
        }
    }
}

internal fun DefaultProtoSourceSet.protoTaskProperties(): ProtoTask.Properties {
    return androidProperties.orNull ?: ProtoTask.Properties(
        isTest = name.lowercase().endsWith("test"),
        sourceSetNames = setOf(name),
    )
}

internal object PlatformOption {
    const val JVM = "jvm"
    const val ANDROID = "android"
    const val ANDROID_NATIVE = "android-native"
    const val JS = "js"
    const val NATIVE = "native"
    const val COMMON = "common"
    const val WASM = "wasm"
}

internal fun protoConfigurationName(sourceSetName: String): String {
    if (sourceSetName == "main") return "proto"
    return "${sourceSetName}Proto"
}

internal fun protoImportConfigurationName(sourceSetName: String): String {
    if (sourceSetName == "main") return "protoImport"
    return "${sourceSetName}ProtoImport"
}
