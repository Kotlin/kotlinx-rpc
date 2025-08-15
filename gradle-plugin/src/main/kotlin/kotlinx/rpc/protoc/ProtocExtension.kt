/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.BufExtension
import org.gradle.api.Action

/**
 * Configuration for the Protoc capabilities.
 *
 * To enable the Protoc capabilities, add the following minimal code to your `build.gradle.kts`:
 * ```kotlin
 * rpc {
 *     protoc()
 * }
 * ```
 */
public interface ProtocExtension {
    /**
     * Configuration for the Buf build tool.
     */
    public val buf: BufExtension

    /**
     * Configures the Buf build tool.
     */
    public fun buf(action: Action<BufExtension>)
}
