package org.jetbrains.krpc

import org.jetbrains.krpc.internal.InternalKRPCApi

@InternalKRPCApi
interface RPCProperty<Self> {
    suspend fun awaitField(): Self
}
