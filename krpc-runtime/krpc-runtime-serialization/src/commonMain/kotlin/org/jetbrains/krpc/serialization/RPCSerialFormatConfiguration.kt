/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.serialization

/**
 * Special interface to configure serialization for a kRPC service in RPCConfig
 * ```kotlin
 * // this: RPCConfig
 * serialization { // this: RPCSerialFormatConfiguration
 *    // register serialization here
 * }
 * ```
 */
interface RPCSerialFormatConfiguration {
    fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.String<*, *>)

    fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.Binary<*, *>)
}
