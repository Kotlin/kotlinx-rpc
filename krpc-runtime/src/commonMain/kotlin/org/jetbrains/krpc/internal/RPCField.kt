package org.jetbrains.krpc.internal

@InternalKRPCApi
interface RPCField<Self> {
    suspend fun await(): Self
}
