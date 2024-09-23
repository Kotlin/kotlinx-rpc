/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.serialization

/**
 * Special interface to configure serialization for a kRPC protocol in RPCConfig
 * ```kotlin
 * // this: RPCConfigBuilder
 * serialization { // this: RPCSerialFormatConfiguration
 *    // register serialization here
 * }
 * ```
 */
public interface RPCSerialFormatConfiguration {
    /**
     * Register a serialization format in the RPCSerialFormatConfiguration.
     *
     * @param rpcSerialFormatInitializer The serialization format initializer of String type.
     * It is used to configure the serialization format for a kRPC protocol.
     */
    public fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.String<*, *>)

    /**
     * Register a serialization format in the RPCSerialFormatConfiguration.
     *
     * @param rpcSerialFormatInitializer The serialization format initializer of Binary type.
     * It is used to configure the serialization format for a kRPC protocol.
     */
    public fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.Binary<*, *>)
}
