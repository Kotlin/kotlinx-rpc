/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

public typealias ProtoSourceSets = NamedDomainObjectContainer<ProtoSourceSet>

/**
 * Represents a source set for proto files.
 *
 * Acts like a [SourceDirectorySet] but also allows configuring protoc plugins.
 *
 * All source sets have [kotlinMultiplatform] and [grpcKotlinMultiplatform] plugins by default.
 *
 * Example:
 * ```kotlin
 * kotlin.sourceSets {
 *     commonMain {
 *         proto {
 *             exclude("some.proto")
 *             plugin { getByName("myPlugin") }
 *         }
 *     }
 * }
 * ```
 */
public sealed interface ProtoSourceSet : SourceDirectorySet {
    /**
     * Add a plugin to this source set and allows to configure it specifically for this source set.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     commonMain {
     *         proto {
     *             plugin(myPlugin) {
     *                 options.put("key", "value") // only for commonMain
     *             }
     *         }
     *     }
     * }
     * ```
     */
    public fun plugin(plugin: ProtocPlugin, configure: Action<ProtocPlugin>? = null)

    /**
     * Add a plugin to this source set and allows to configure it specifically for this source set.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     commonMain {
     *         proto {
     *             plugin(myPlugin) {
     *                 options.put("key", "value") // only for commonMain
     *             }
     *         }
     *     }
     * }
     * ```
     */
    public fun plugin(provider: NamedDomainObjectProvider<ProtocPlugin>, configure: Action<ProtocPlugin>? = null)

    /**
     * Add a plugin to this source set and allows to configure it specifically for this source set.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     commonMain {
     *         proto {
     *             plugin(myPlugin) {
     *                 options.put("key", "value") // only for commonMain
     *             }
     *         }
     *     }
     * }
     * ```
     */
    public fun plugin(provider: Provider<ProtocPlugin>, configure: Action<ProtocPlugin>? = null)

    /**
     * Add a plugin to this source set and allows to configure it specifically for this source set.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     commonMain {
     *         proto {
     *             plugin {
     *                 getByName("myPlugin")
     *             }
     *             // or
     *             plugin({
     *                options.put("key", "value") // only for commonMain
     *             }) {
     *                 getByName("myPlugin")
     *             }
     *         }
     *     }
     * }
     * ```
     */
    public fun plugin(
        configure: Action<ProtocPlugin>? = null,
        select: NamedDomainObjectContainer<ProtocPlugin>.() -> ProtocPlugin,
    )

    /**
     * Protoc plugins that will be applied to proto files in this source set.
     *
     * Combined with the plugins from [extendsFrom] source sets.
     *
     * Plugins from [importsFrom] and [importsAllFrom] source sets are not included.
     */
    public val plugins: SetProperty<ProtocPlugin>

    /**
     * Proto files from [protoSourceSet] will be available as import, but will not be used for code generation.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     val someProto = create("someProto")
     *     commonMain {
     *         proto {
     *             importsFrom(protoSourceSets.getByName("someProto"))
     *         }
     *     }
     * }
     * ```
     */
    public fun importsFrom(protoSourceSet: ProtoSourceSet)

    /**
     * Proto files from [protoSourceSet] will be available as import, but will not be used for code generation.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     val someProto = create("someProto")
     *     commonMain {
     *         proto {
     *             importsFrom(protoSourceSets.getByName("someProto"))
     *         }
     *     }
     * }
     * ```
     */
    public fun importsFrom(protoSourceSet: Provider<ProtoSourceSet>)

    /**
     * Proto files from [protoSourceSet] will be available as import, but will not be used for code generation.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     val someProto = create("someProto")
     *     commonMain {
     *         proto {
     *             importsFrom(protoSourceSets.getByName("someProto"))
     *         }
     *     }
     * }
     * ```
     */
    public fun importsFrom(protoSourceSet: NamedDomainObjectProvider<ProtoSourceSet>)

    /**
     * Proto files from [protoSourceSets] will be available as import, but will not be used for code generation.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     val someProto = create("someProto")
     *     commonMain {
     *         proto {
     *             importsAllFrom(project.provider { protoSourceSets.filter { it.name == someProto.name } })
     *         }
     *     }
     * }
     * ```
     */
    public fun importsAllFrom(protoSourceSets: Provider<List<ProtoSourceSet>>)

    /**
     * List of all imported proto source sets.
     */
    public val imports: SetProperty<ProtoSourceSet>

    /**
     * A collection of proto files that are imported by this source set but do not belong to any other source set.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     commonMain {
     *         proto {
     *             fileImports.from("path/to/proto/file.proto")
     *         }
     *     }
     * }
     * ```
     */
    public val fileImports: ConfigurableFileCollection

    /**
     * Proto source sets that this source set extends from.
     * All proto files from [protoSourceSet] will be used for code generation.
     * All imports [protoSourceSet] will be included as well.
     *
     * Example:
     * ```kotlin
     * kotlin.sourceSets {
     *     val someProto = create("someProto")
     *     commonMain {
     *         proto {
     *             extendsFrom(protoSourceSets.getByName("someProto"))
     *         }
     *     }
     * }
     * ```
     */
    public fun extendsFrom(protoSourceSet: ProtoSourceSet)
}

/**
 * Returns the proto source set for this [KotlinSourceSet].
 */
public val KotlinSourceSet.proto: ProtoSourceSet
    get(): ProtoSourceSet {
        return project.protoSourceSets.getByName(name)
    }

/**
 * Executes the given [action] on the proto source set for this [KotlinSourceSet].
 */
public fun KotlinSourceSet.proto(action: Action<ProtoSourceSet>) {
    proto.apply(action::execute)
}

/**
 * Returns the proto source set for this [KotlinSourceSet].
 */
@get:JvmName("proto_kotlin")
public val NamedDomainObjectProvider<KotlinSourceSet>.proto: Provider<ProtoSourceSet>
    get() {
        return map { it.proto }
    }

/**
 * Executes the given [action] on the proto source set for this [KotlinSourceSet].
 */
@JvmName("proto_kotlin")
public fun NamedDomainObjectProvider<KotlinSourceSet>.proto(action: Action<ProtoSourceSet>) {
    configure {
        proto(action)
    }
}

/**
 * Returns the proto source set for this [SourceSet].
 */
public val SourceSet.proto: ProtoSourceSet
    get(): ProtoSourceSet {
        return extensions.getByType(ProtoSourceSet::class.java)
    }

/**
 * Executes the given [action] on the proto source set for this [SourceSet].
 */
public fun SourceSet.proto(action: Action<ProtoSourceSet>) {
    extensions.configure(ProtoSourceSet::class.java, action::execute)
}

/**
 * Returns the proto source set for this [SourceSet].
 */
public val NamedDomainObjectProvider<SourceSet>.proto: Provider<ProtoSourceSet>
    get() {
        return map { it.proto }
    }

/**
 * Executes the given [action] on the proto source set for this [SourceSet].
 */
public fun NamedDomainObjectProvider<SourceSet>.proto(action: Action<ProtoSourceSet>) {
    configure {
        proto(action)
    }
}
