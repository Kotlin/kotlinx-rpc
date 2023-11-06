package org.jetbrains.krpc.serialization

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat

interface RPCSerialFormatConfiguration {
    fun register(rpcSerialFormatInitializer: RPCSerialFormatInitializer<out StringFormat, *>)

    fun register(rpcSerialFormatInitializer: RPCSerialFormatInitializer<out BinaryFormat, *>)
}
