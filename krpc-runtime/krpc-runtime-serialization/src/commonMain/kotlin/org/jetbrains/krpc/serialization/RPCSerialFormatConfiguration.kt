package org.jetbrains.krpc.serialization

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat

interface RPCSerialFormatConfiguration {
    fun <Format : StringFormat> registerString(rpcSerialFormatInitializer: RPCSerialFormatInitializer<Format, *>)

    fun <Format : BinaryFormat> registerBinary(rpcSerialFormatInitializer: RPCSerialFormatInitializer<Format, *>)
}
