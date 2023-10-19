package org.jetbrains.krpc.server

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCTransport
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : RPC> RPC.Companion.serverOf(service: T, transport: RPCTransport): RPCServerEngine<T> =
    RPC.serverOf(service, typeOf<T>(), transport)

fun <T : RPC> RPC.Companion.serverOf(
    service: T,
    serviceType: KType,
    transport: RPCTransport,
): RPCServerEngine<T> = RPCServerEngine(service, transport, serviceType)

@Suppress("unused")
@Deprecated(
    "Use RPC.serverOf(service, transport) instead.",
    ReplaceWith("RPC.serverOf(service, transport)", "org.jetbrains.krpc.server.serverOf", "org.jetbrains.krpc.RPC"),
    DeprecationLevel.WARNING
)
inline fun <reified T : RPC> rpcServerOf(service: T, transport: RPCTransport): RPCServerEngine<T> =
    RPC.serverOf(service, typeOf<T>(), transport)


@Suppress("unused")
@Deprecated(
    "Use RPC.serverOf(service, serviceType, transport) instead.", ReplaceWith(
        "RPC.serverOf(service, serviceType, transport)", "org.jetbrains.krpc.server.serverOf", "org.jetbrains.krpc.RPC"
    ), DeprecationLevel.WARNING
)
fun <T : RPC> rpcServerOf(
    service: T,
    serviceType: KType,
    transport: RPCTransport,
): RPCServerEngine<T> {
    return RPC.serverOf(service, serviceType, transport)
}
