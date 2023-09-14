package org.jetbrains.krpc.server

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCStubCall
import kotlin.reflect.KType

@RPCStubCall
@Suppress("UNUSED_PARAMETER", "unused")
inline fun <reified T : RPC> serviceMethodOf(methodName: String): KType? {
    error("Stub serviceMethodOf function, will be replaced with call to a companion object in compile time")
}
