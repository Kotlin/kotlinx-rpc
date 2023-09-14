package org.jetbrains.krpc

import kotlin.reflect.KType

interface RPCClientProvider<T : RPC> {
    fun client(engine: RPCEngine): T
}

interface RPCMethodClassTypeProvider {
    fun methodClassType(methodName: String): KType?
}
