package org.jetbrains.krpc.client

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCProperty
import org.jetbrains.krpc.internal.InternalKRPCApi

@OptIn(InternalKRPCApi::class)
suspend fun <T : RPC, R> T.awaitFieldInitialization(getter: T.() -> R): R {
    val field = getter()

    if (field is RPCProperty<*>) {
        @Suppress("UNCHECKED_CAST")
        return (field as RPCProperty<R>).awaitField()
    }

    error("Receiver RPC interface is not valid RPC client. Please, use RPC.clientOf method to get valid implementation of RPC interface")
}
