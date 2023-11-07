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
