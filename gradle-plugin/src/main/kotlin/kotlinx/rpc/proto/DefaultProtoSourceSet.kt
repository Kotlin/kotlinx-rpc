/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.util.findOrCreate
import kotlinx.rpc.util.withKotlinJvmExtension
import kotlinx.rpc.util.withKotlinKmpExtension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
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
    val baseName: Property<String> = project.objects.property<String>()
    val languageSourceSets: ListProperty<Any> = project.objects.listProperty<Any>()
    val protocPlugins: ListProperty<String> = project.objects.listProperty<String>().convention(emptyList())
    val generateTask: Property<BufGenerateTask> = project.objects.property<BufGenerateTask>()

    override fun protocPlugin(plugin: NamedDomainObjectProvider<ProtocPlugin>) {
        protocPlugins.add(plugin.name)
    }

    override fun protocPlugin(plugin: ProtocPlugin) {
        protocPlugins.add(plugin.name)
    }

    override val proto: SourceDirectorySet = project.objects.sourceDirectorySet(PROTO_SOURCE_DIRECTORY_NAME, "Proto sources").apply {
        srcDirs(baseName.map { "src/$it/proto" })
    }

    override fun proto(action: Action<SourceDirectorySet>) {
        action.execute(proto)
    }
}


internal fun Project.configureProtoExtensions(
    configure: Project.(languageSourceSetName: String, languageSourceSet: Any, protoSourceSet: DefaultProtoSourceSet) -> Unit
) {
    fun findOrCreateAndConfigure(languageSourceSetName: String, languageSourceSet: Any) {
        val protoName = languageSourceSetName.sourceSetToProtoName()
        val container = project.findOrCreate(PROTO_SOURCE_SETS) {
            val container = objects.domainObjectContainer(
                ProtoSourceSet::class.java,
                ProtoSourceSetFactory(project)
            )

            project.extensions.add<ProtoSourceSets>(PROTO_SOURCE_SETS, container)

            container
        }

        val protoSourceSet = container.maybeCreate(protoName) as DefaultProtoSourceSet

        configure(languageSourceSetName, languageSourceSet, protoSourceSet)
    }

    project.withKotlinJvmExtension {
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
        sourceSets.configureEach {
            if (name == "jvmMain" || name == "jvmTest") {
                findOrCreateAndConfigure(name, this)
            }
        }
    }
}

private fun String.sourceSetToProtoName(): String {
    return when  {
        this == "main" -> "protoMain"
        this == "test" -> "protoTest"
        endsWith("Main") -> "${removeSuffix("Main")}ProtoMain"
        endsWith("Test") -> "${removeSuffix("Test")}ProtoTest"
        else -> throw IllegalArgumentException("Unsupported source set name: $this")
    }
}

internal fun Project.createProtoExtensions() {
    configureProtoExtensions { languageSourceSetName, languageSourceSet, sourceSet ->
        sourceSet.initExtension(languageSourceSetName, languageSourceSet)
    }
}

private fun DefaultProtoSourceSet.initExtension(languageSourceSetName: String, languageSourceSet: Any) {
    baseName.set(languageSourceSetName)
    this.languageSourceSets.add(languageSourceSet)
}
