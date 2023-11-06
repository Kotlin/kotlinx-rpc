package org.jetbrains.krpc.serialization

interface RPCSerialFormatConfiguration {
    fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.String<*, *>)

    fun register(rpcSerialFormatInitializer: RPCSerialFormatBuilder.Binary<*, *>)
}
