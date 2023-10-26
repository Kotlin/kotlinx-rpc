package org.jetbrains.krpc.internal

@InternalKRPCApi
interface RPCMethodClassArguments {
    fun asArray(): Array<Any?>
}
