package org.jetbrains.krpc.server

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.RPCTransport
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : RPC> RPC.Companion.serverOf(
    service: T,
    transport: RPCTransport,
    noinline configBuilder: RPCConfigBuilder.Server.() -> Unit = {},
): RPCServerEngine<T> =
    RPC.serverOf(service, typeOf<T>(), transport, configBuilder)

fun <T : RPC> RPC.Companion.serverOf(
    service: T,
    serviceType: KType,
    transport: RPCTransport,
    configBuilder: RPCConfigBuilder.Server.() -> Unit = {},
): RPCServerEngine<T> {
    val config = RPCConfigBuilder.Server().apply(configBuilder).build()
    return RPCServerEngine(service, transport, serviceType, config)
}

@Suppress("unused")
@Deprecated(
    "Use RPC.serverOf(service, transport) instead.",
    ReplaceWith("RPC.serverOf(service, transport)", "org.jetbrains.krpc.server.serverOf", "org.jetbrains.krpc.RPC"),
    DeprecationLevel.WARNING
)
inline fun <reified T : RPC> rpcServerOf(
    service: T,
    transport: RPCTransport,
    noinline configBuilder: RPCConfigBuilder.Server.() -> Unit,
): RPCServerEngine<T> =
    RPC.serverOf(service, typeOf<T>(), transport, configBuilder)


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
    configBuilder: RPCConfigBuilder.Server.() -> Unit = {},
): RPCServerEngine<T> {
    return RPC.serverOf(service, serviceType, transport, configBuilder)
}
