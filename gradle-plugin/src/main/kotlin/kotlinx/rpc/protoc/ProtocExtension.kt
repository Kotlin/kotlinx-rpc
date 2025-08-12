/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.BufExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer

/**
 * Configuration for the gRPC capabilities.
 *
 * To enable the gRPC capabilities, add the following minimal code to your `build.gradle.kts`:
 * ```kotlin
 * rpc {
 *     protoc()
 * }
 * ```
 */
public interface ProtocExtension {
    /**
     * List of protoc plugins to be applied to the project.
     */
    public val plugins: NamedDomainObjectContainer<ProtocPlugin>

    /**
     * Configures the protoc plugins.
     */
    public fun plugins(action: Action<NamedDomainObjectContainer<ProtocPlugin>>)

    /**
     * Configuration for the Buf build tool.
     */
    public val buf: BufExtension

    /**
     * Configures the Buf build tool.
     */
    public fun buf(action: Action<BufExtension>)
}
