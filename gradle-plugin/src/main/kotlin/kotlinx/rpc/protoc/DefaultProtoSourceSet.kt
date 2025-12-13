/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.rpcExtension
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
import org.gradle.api.file.ConfigurableFileCollection
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

        isMain && project.the<KotlinProjectExtension>().explicitApi != ExplicitApiMode.Disabled
    }

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

    private fun initPlugin(copy: ProtocPlugin, configure: Action<ProtocPlugin>?) {
        copy.options.put("explicitApiModeEnabled", explicitApiModeEnabled)

        configure?.execute(copy)
    }

    init {
        project.rpcExtension().whenProtocApplied {
            plugin(plugins.kotlinMultiplatform)
            plugin(plugins.grpcKotlinMultiplatform)
        }
    }

    val tasksConfigured: Property<Boolean> = project.objects.property<Boolean>()
        .convention(false)

    // Collection of AndroidSourceSet, KotlinSourceSet, SourceSet (java) associated with this proto source set
    val languageSourceSets: ListProperty<Any> = project.objects.listProperty<Any>()
    val generateTask: Property<BufGenerateTask?> = project.objects.property<BufGenerateTask?>()

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
    val androidProperties: Property<ProtoTask.AndroidProperties?> = project.objects.property()

    override val imports: SetProperty<ProtoSourceSet> = project.objects.setProperty()
    override val fileImports: ConfigurableFileCollection = project.objects.fileCollection()

    override fun importsFrom(protoSourceSet: ProtoSourceSet) {
        imports.add(protoSourceSet.checkSelfImport())
        imports.addAll(protoSourceSet.imports.checkSelfImport())
    }

    override fun importsFrom(protoSourceSet: Provider<ProtoSourceSet>) {
        imports.add(protoSourceSet.checkSelfImport())
        imports.addAll(protoSourceSet.flatMap { it.imports.checkSelfImport() })
    }

    override fun importsAllFrom(protoSourceSets: Provider<List<ProtoSourceSet>>) {
        imports.addAll(protoSourceSets.checkSelfImport())
        imports.addAll(protoSourceSets.map { list -> list.flatMap { it.imports.checkSelfImport().get() } })
    }

    override fun importsFrom(protoSourceSet: NamedDomainObjectProvider<ProtoSourceSet>) {
        imports.add(protoSourceSet.checkSelfImport())
        imports.addAll(protoSourceSet.flatMap { it.imports.checkSelfImport() })
    }

    private val extendsFrom: MutableSet<ProtoSourceSet> = mutableSetOf()

    override fun extendsFrom(protoSourceSet: ProtoSourceSet) {
        if (extendsFrom.contains(protoSourceSet)) {
            return
        }

        if (this == protoSourceSet) {
            throw IllegalArgumentException("$name proto source set cannot extend from self")
        }

        if (protoSourceSet !is DefaultProtoSourceSet) {
            throw IllegalArgumentException(
                "$name proto source set can only extend from other default proto source sets." +
                        "${protoSourceSet.name} is not a ${DefaultProtoSourceSet::class.simpleName}",
            )
        }

        extendsFrom += protoSourceSet

        source(protoSourceSet.sourceDirectorySet)
        imports.addAll(protoSourceSet.imports.checkSelfImport())

        plugins.addAll(protoSourceSet.plugins)
    }

    @JvmName("checkSelfImport_provider")
    private fun Provider<ProtoSourceSet>.checkSelfImport() = map {
        it.checkSelfImport()
    }

    private fun SetProperty<ProtoSourceSet>.checkSelfImport() = map { set ->
        set.onEach { it.checkSelfImport() }
    }

    @JvmName("checkSelfImport_provider_list")
    private fun Provider<List<ProtoSourceSet>>.checkSelfImport() = map { set ->
        set.onEach { it.checkSelfImport() }
    }

    private fun ProtoSourceSet.checkSelfImport(): ProtoSourceSet {
        if (this@DefaultProtoSourceSet == this) {
            throw IllegalArgumentException("${this@DefaultProtoSourceSet.name} proto source set cannot import from itself")
        }

        return this
    }

    // Java default methods

    override fun forEach(action: Consumer<in File>?) {
        sourceDirectorySet.forEach(action)
    }

    override fun spliterator(): Spliterator<File?> {
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
