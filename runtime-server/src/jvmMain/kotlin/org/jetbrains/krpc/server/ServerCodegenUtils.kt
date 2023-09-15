package org.jetbrains.krpc.server

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCStubCall
import kotlin.reflect.KType

@RPCStubCall
@Suppress("UNUSED_PARAMETER", "unused")
inline fun <reified T : RPC> serviceMethodOf(methodName: String): KType? {
    error("Stub serviceMethodOf function, will be replaced with the module specific version in compile time")
}

@RPCStubCall
@Suppress("UNUSED_PARAMETER")
fun serviceMethodOf(kType: KType, methodName: String): KType? {
    error("Stub serviceMethodOf function, will be replaced with the module specific version in compile time")
}
