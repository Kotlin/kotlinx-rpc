package org.jetbrains.krpc.client

internal interface RPCProperty<Self> {
    suspend fun await(): Self
}
