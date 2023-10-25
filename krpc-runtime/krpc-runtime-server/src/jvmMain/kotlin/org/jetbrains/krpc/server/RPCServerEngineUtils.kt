package org.jetbrains.krpc.server

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : RPC> RPC.Companion.serverOf(
    service: T,
    transport: RPCTransport,
    noinline configBuilder: RPCConfigBuilder.Server.() -> Unit = {},
): RPCServerEngine<T> =
    RPC.serverOf(service, typeOf<T>(), transport, configBuilder)

@OptIn(InternalKRPCApi::class)
fun <T : RPC> RPC.Companion.serverOf(
    service: T,
    serviceType: KType,
    transport: RPCTransport,
    configBuilder: RPCConfigBuilder.Server.() -> Unit = {},
): RPCServerEngine<T> {
    return serverOf(service, serviceType.kClass(), transport, configBuilder)
}

fun <T : RPC> RPC.Companion.serverOf(
    service: T,
    serviceKClass: KClass<T>,
    transport: RPCTransport,
    configBuilder: RPCConfigBuilder.Server.() -> Unit = {},
): RPCServerEngine<T> {
    val config = RPCConfigBuilder.Server().apply(configBuilder).build()
    return RPCServerEngine(service, transport, serviceKClass, config)
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
