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
 */
public sealed interface ProtoSourceSet : SourceDirectorySet {
    public fun plugin(plugin: ProtocPlugin, configure: Action<ProtocPlugin>? = null)

    public fun plugin(provider: NamedDomainObjectProvider<ProtocPlugin>, configure: Action<ProtocPlugin>? = null)

    public fun plugin(provider: Provider<ProtocPlugin>, configure: Action<ProtocPlugin>? = null)

    public fun plugin(
        configure: Action<ProtocPlugin>? = null,
        select: NamedDomainObjectContainer<ProtocPlugin>.() -> ProtocPlugin,
    )
}

public val KotlinSourceSet.proto: ProtoSourceSet
    get(): ProtoSourceSet {
        return project.protoSourceSets.getByName(name)
    }

public fun KotlinSourceSet.proto(action: Action<ProtoSourceSet>) {
    proto.apply(action::execute)
}

@get:JvmName("proto_kotlin")
public val NamedDomainObjectProvider<KotlinSourceSet>.proto: Provider<ProtoSourceSet>
    get() {
        return map { it.proto }
    }

@JvmName("proto_kotlin")
public fun NamedDomainObjectProvider<KotlinSourceSet>.proto(action: Action<ProtoSourceSet>) {
    configure {
        proto(action)
    }
}

public val SourceSet.proto: ProtoSourceSet
    get(): ProtoSourceSet {
        return extensions.getByType(ProtoSourceSet::class.java)
    }

public fun SourceSet.proto(action: Action<ProtoSourceSet>) {
    extensions.configure(ProtoSourceSet::class.java, action::execute)
}

public val NamedDomainObjectProvider<SourceSet>.proto: Provider<ProtoSourceSet>
    get() {
        return map { it.proto }
    }

public fun NamedDomainObjectProvider<SourceSet>.proto(action: Action<ProtoSourceSet>) {
    configure {
        proto(action)
    }
}
