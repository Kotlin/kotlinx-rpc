/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.serialization

/**
 * Special interface to configure serialization for a kRPC protocol in KrpcConfig
 * ```kotlin
 * // this: KrpcConfigBuilder
 * serialization { // this: KrpcSerialFormatConfiguration
 *    // register serialization here
 * }
 * ```
 */
public interface KrpcSerialFormatConfiguration {
    /**
     * Register a serialization format in the KrpcSerialFormatConfiguration.
     *
     * @param rpcSerialFormatInitializer The serialization format initializer of String type.
     * It is used to configure the serialization format for a kRPC protocol.
     */
    public fun register(rpcSerialFormatInitializer: KrpcSerialFormatBuilder.String<*, *>)

    /**
     * Register a serialization format in the KrpcSerialFormatConfiguration.
     *
     * @param rpcSerialFormatInitializer The serialization format initializer of Binary type.
     * It is used to configure the serialization format for a kRPC protocol.
     */
    public fun register(rpcSerialFormatInitializer: KrpcSerialFormatBuilder.Binary<*, *>)
}
