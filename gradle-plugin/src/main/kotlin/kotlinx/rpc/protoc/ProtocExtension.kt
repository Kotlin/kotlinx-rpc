/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.BufExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyExhaustive
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyMutableCollection

/**
 * Configuration for the Protoc capabilities.
 *
 * To enable the Protoc capabilities, use one of the following options:
 *
 * Option 1: DSL in `build.gradle.kts`:
 * ```kotlin
 * rpc {
 *     protoc()
 * }
 * ```
 *
 * Option 2: Gradle property in `gradle.properties`:
 * ```properties
 * kotlinx.rpc.protoc=true
 * ```
 */
@IntentionallyExhaustive(reason = ExemptionReason.API_DESIGN)
public sealed interface ProtocExtension {
    /**
     * Configuration for the Buf build tool.
     */
    public val buf: BufExtension

    /**
     * Configures the Buf build tool.
     */
    public fun buf(action: Action<BufExtension>)

    /**
     * Container of protoc plugins.
     */
    @IntentionallyMutableCollection(reason = ExemptionReason.INTEROP, description = "Gradle API")
    public val plugins: NamedDomainObjectContainer<ProtocPlugin>

    /**
     * Configures the protoc plugins.
     */
    @IntentionallyMutableCollection(reason = ExemptionReason.INTEROP, description = "Gradle API")
    public fun plugins(action: Action<NamedDomainObjectContainer<ProtocPlugin>>)
}
