package org.jetbrains.krpc

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable

interface RPCTransport {
    val incoming: SharedFlow<RPCMessage>

    suspend fun send(message: RPCMessage)
}


@Serializable
data class SerializedException(val message: String)
