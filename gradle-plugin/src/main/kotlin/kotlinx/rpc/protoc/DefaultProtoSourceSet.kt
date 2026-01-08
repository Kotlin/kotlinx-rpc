/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.rpcExtension
import kotlinx.rpc.util.findOrCreate
import kotlinx.rpc.util.withLazyKotlinJvmExtension
import kotlinx.rpc.util.withLazyKotlinKmpExtension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import java.io.File
import java.util.*
import java.util.function.Consumer
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
internal val Project.protoSourceSets: ProtoSourceSets
    get() = extensions.findByName(PROTO_SOURCE_SETS) as? ProtoSourceSets
        ?: throw GradleException("Unable to find proto source sets in project $name")

internal class ProtoSourceSetFactory(private val project: Project) : NamedDomainObjectFactory<ProtoSourceSet> {
    override fun create(name: String): ProtoSourceSet {
        return project.objects.newInstance(DefaultProtoSourceSet::class.java, project, name)
    }
}

internal open class DefaultProtoSourceSet(
    internal val project: Project,
    private val sourceDirectorySet: SourceDirectorySet,
) : ProtoSourceSet, SourceDirectorySet by sourceDirectorySet {

    @Inject
    constructor(project: Project, protoName: String) : this(
        project = project,
        sourceDirectorySet = project.objects.sourceDirectorySet(protoName, "Proto sources for $protoName").apply {
            srcDirs("src/${protoName}/proto")
        },
    )

    private val explicitApiModeEnabled = project.provider {
        project.the<KotlinBaseExtension>().explicitApi != ExplicitApiMode.Disabled
    }

    val plugins = project.objects.setProperty<ProtocPlugin>()

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
    val generateTask: Property<BufGenerateTask> = project.objects.property<BufGenerateTask>()

    // Java default methods

    override fun forEach(action: Consumer<in File>?) {
        sourceDirectorySet.forEach(action)
    }

    override fun spliterator(): Spliterator<File> {
        return sourceDirectorySet.spliterator()
    }
}

internal fun Project.createProtoExtensions() {
    fun findOrCreateAndConfigure(languageSourceSetName: String, languageSourceSet: Any?): ProtoSourceSet {
        val container = project.findOrCreate(PROTO_SOURCE_SETS) {
            val container = objects.domainObjectContainer(
                ProtoSourceSet::class.java,
                ProtoSourceSetFactory(project)
            )

            project.extensions.add<ProtoSourceSets>(PROTO_SOURCE_SETS, container)

            container
        }

        val protoSourceSet = container.maybeCreate(languageSourceSetName) as DefaultProtoSourceSet

        languageSourceSet?.let { protoSourceSet.languageSourceSets.add(it) }

        return protoSourceSet
    }

    project.withLazyKotlinJvmExtension {
        sourceSets.all {
            findOrCreateAndConfigure(name, this)
        }

        project.extensions.configure<SourceSetContainer>("sourceSets") {
            all {
                val protoSourceSet = findOrCreateAndConfigure(name, this)

                findOrCreate(PROTO_SOURCE_SET_EXTENSION_NAME) {
                    extensions.add(PROTO_SOURCE_SET_EXTENSION_NAME, protoSourceSet)
                }
            }
        }
    }

    project.withLazyKotlinKmpExtension {
        sourceSets.all {
            findOrCreateAndConfigure(name, this)
        }
    }
}
