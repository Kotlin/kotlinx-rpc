/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.tasks.BufExecTask
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.rpcExtension
import kotlinx.rpc.util.findOrCreate
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
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import org.jetbrains.kotlin.tooling.core.extrasKeyOf
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
        val isAndroid = project.the<KotlinSourceSetContainer>()
            .sourceSets.findByName(name)
            ?.extras
            ?.get(isAndroidKey)
            ?: false

        return project.objects.newInstance(DefaultProtoSourceSet::class.java, project, name, isAndroid)
    }
}

internal fun Project.findOrCreateProtoSourceSets(): NamedDomainObjectContainer<ProtoSourceSet> =
    project.findOrCreate(PROTO_SOURCE_SETS) {
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
    internal val isAndroid: Boolean,
) : ProtoSourceSet, SourceDirectorySet by sourceDirectorySet {

    @Inject
    constructor(project: Project, protoName: String, isAndroid: Boolean) : this(
        project = project,
        sourceDirectorySet = project.objects.sourceDirectorySet(protoName, "Proto sources for $protoName").apply {
            srcDirs("src/${protoName}/proto")
        },
        isAndroid = isAndroid,
    )

    private val explicitApiModeEnabled = project.provider {
        project.the<KotlinProjectExtension>().explicitApi != ExplicitApiMode.Disabled
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
        if (this@DefaultProtoSourceSet.name.lowercase().endsWith("main")) {
            copy.options.put("explicitApiModeEnabled", explicitApiModeEnabled)
        }

        configure?.execute(copy)
    }

    init {
        project.rpcExtension().whenProtocApplied {
            plugin(plugins.kotlinMultiplatform)
            plugin(plugins.grpcKotlinMultiplatform)
        }
    }

    val languageSourceSets: ListProperty<Any> = project.objects.listProperty<Any>()
    val generateTask: Property<BufGenerateTask?> = project.objects.property<BufGenerateTask?>()

    // main/test for Kotlin/Android, androidMain/androidTest for KMP
    // only set for variant.name sourceSets
    val androidRoot: Property<DefaultProtoSourceSet> = project.objects.property()
    // used to track test tasks' dependencies for main tasks, e.g.:
    // - bufGenerateTestDebug depends on bufGenerateDebug
    //
    // so we need to track this dependency using this property
    val androidMain: Property<DefaultProtoSourceSet> = project.objects.property()

    // only set for variant.name sourceSets
    val androidProperties: Property<BufExecTask.AndroidProperties?> =
        project.objects.property<BufExecTask.AndroidProperties?>()
            .convention(null)

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

    override fun importsAllFrom(protoSourceSet: Provider<List<ProtoSourceSet>>) {
        imports.addAll(protoSourceSet.checkSelfImport())
        imports.addAll(protoSourceSet.map { list -> list.flatMap { it.imports.checkSelfImport().get() } })
    }

    override fun importsFrom(protoSourceSet: NamedDomainObjectProvider<ProtoSourceSet>) {
        imports.add(protoSourceSet.checkSelfImport())
        imports.addAll(protoSourceSet.flatMap { it.imports.checkSelfImport() })
    }

    override fun extendsFrom(protoSourceSet: ProtoSourceSet) {
        if (this == protoSourceSet) {
            throw IllegalArgumentException("$name proto source set cannot extend from self")
        }

        if (protoSourceSet !is DefaultProtoSourceSet) {
            throw IllegalArgumentException(
                "$name proto source set can only extend from other default proto source sets." +
                        "${protoSourceSet.name} is not a ${DefaultProtoSourceSet::class.simpleName}",
            )
        }

        source(protoSourceSet.sourceDirectorySet)
        imports.addAll(protoSourceSet.imports.checkSelfImport())
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
    ): ProtoSourceSet {
        val container = findOrCreateProtoSourceSets()
        val protoSourceSet = container.maybeCreate(languageSourceSetName) as DefaultProtoSourceSet

        languageSourceSet?.let { protoSourceSet.languageSourceSets.add(it) }

        return protoSourceSet
    }

    project.withKotlinSourceSets { isAndroid, extension ->
        extension.sourceSets.all {
            extras[isAndroidKey] = isAndroid
            findOrCreateAndConfigure(name, this)
        }
    }

    project.withLazyJavaPluginExtension {
        sourceSets.configureEach {
            val protoSourceSet = findOrCreateAndConfigure(name, this)

            findOrCreate(PROTO_SOURCE_SET_EXTENSION_NAME) {
                extensions.add(PROTO_SOURCE_SET_EXTENSION_NAME, protoSourceSet)
            }
        }
    }
}

private val isAndroidKey = extrasKeyOf<Boolean>("kxrpc_proto_source_set_is_android")

internal fun DefaultProtoSourceSet.bufExecProperties(): BufExecTask.Properties {
    // main has androidRoot.isPresent=false and should not be included, although it is an Android source set
    return if (isAndroid && androidRoot.isPresent) {
        androidProperties.orNull
            ?: throw GradleException("Android properties are not set for source set $name")
    } else {
        BufExecTask.Properties(
            isTest = name.lowercase().endsWith("test"),
            sourceSetName = name,
        )
    }
}
