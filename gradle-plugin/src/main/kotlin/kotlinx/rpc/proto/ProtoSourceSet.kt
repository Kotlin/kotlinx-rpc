/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.file.SourceDirectorySet

public typealias ProtoSourceSets = NamedDomainObjectContainer<ProtoSourceSet>

/**
 * Represents a source set for proto files.
 */
public interface ProtoSourceSet {
    /**
     * Name of the source set.
     */
    public val name: String

    /**
     * Adds a new protoc plugin.
     *
     * Example:
     * ```kotlin
     * protocPlugin(rpc.grpc.protocPlugins.myPlugin)
     * ```
     */
    public fun protocPlugin(plugin: NamedDomainObjectProvider<ProtocPlugin>)

    /**
     * Adds a new protoc plugin.
     *
     * Example:
     * ```kotlin
     * protocPlugin(rpc.grpc.protocPlugins.myPlugin.get())
     * ```
     */
    public fun protocPlugin(plugin: ProtocPlugin)

    /**
     * Default [SourceDirectorySet] for proto files.
     */
    public val proto: SourceDirectorySet

    /**
     * Configures [proto] source directory set.
     */
    public fun proto(action: Action<SourceDirectorySet>) {
        action.execute(proto)
    }
}
