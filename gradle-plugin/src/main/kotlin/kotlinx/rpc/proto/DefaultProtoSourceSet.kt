/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.util.findOrCreate
import kotlinx.rpc.util.withKotlinJvmExtension
import kotlinx.rpc.util.withKotlinKmpExtension
import org.gradle.api.*
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
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

internal open class DefaultProtoSourceSet @Inject constructor(
    internal val project: Project,
    override val name: String,
) : ProtoSourceSet {
    val languageSourceSets: ListProperty<Any> = project.objects.listProperty<Any>()
    val protocPlugins: ListProperty<String> = project.objects.listProperty<String>().convention(emptyList())
    val generateTask: Property<BufGenerateTask> = project.objects.property<BufGenerateTask>()

    override fun protocPlugin(plugin: NamedDomainObjectProvider<ProtocPlugin>) {
        protocPlugins.add(plugin.name)
    }

    override fun protocPlugin(plugin: ProtocPlugin) {
        protocPlugins.add(plugin.name)
    }

    override val proto: SourceDirectorySet = project.objects.sourceDirectorySet(
        PROTO_SOURCE_DIRECTORY_NAME,
        "Proto sources",
    ).apply {
        srcDirs("src/${this@DefaultProtoSourceSet.name}/proto")
    }

    override fun proto(action: Action<SourceDirectorySet>) {
        action.execute(proto)
    }
}

internal fun Project.createProtoExtensions() {
    fun findOrCreateAndConfigure(languageSourceSetName: String, languageSourceSet: Any?) {
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
    }

    project.withKotlinJvmExtension {
        findOrCreateAndConfigure("main", null)
        findOrCreateAndConfigure("test", null)

        sourceSets.configureEach {
            if (name == SourceSet.MAIN_SOURCE_SET_NAME || name == SourceSet.TEST_SOURCE_SET_NAME) {
                findOrCreateAndConfigure(name, this)
            }
        }

        project.extensions.configure<SourceSetContainer>("sourceSets") {
            configureEach {
                if (name == SourceSet.MAIN_SOURCE_SET_NAME || name == SourceSet.TEST_SOURCE_SET_NAME) {
                    findOrCreateAndConfigure(name, this)
                }
            }
        }
    }

    project.withKotlinKmpExtension {
        findOrCreateAndConfigure("commonMain", null)
        findOrCreateAndConfigure("commonTest", null)

        sourceSets.configureEach {
            if (name == "commonMain" || name == "commonTest") {
                findOrCreateAndConfigure(name, this)
            }
        }
    }
}
