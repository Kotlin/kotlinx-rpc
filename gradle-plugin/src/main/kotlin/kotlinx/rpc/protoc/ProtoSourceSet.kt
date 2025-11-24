/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
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
