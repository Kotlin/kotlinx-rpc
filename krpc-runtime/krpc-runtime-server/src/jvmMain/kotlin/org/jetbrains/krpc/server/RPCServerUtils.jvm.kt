package org.jetbrains.krpc.server

import org.jetbrains.krpc.*
import org.jetbrains.krpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Creates a server engine for the specified RPC interface using the provided transport and service.
 *
 * @param service The service implementation that should handle incoming calls
 * @param transport The transport to be used for communication with the remote client
 * @param config Server configuration
 * @return An instance of the server engine for the specified RPC service
 */
inline fun <reified T : RPC> RPC.Companion.serverOf(
    service: T,
    transport: RPCTransport,
    noinline config: RPCConfigBuilder.Server.() -> Unit = { },
): RPCServerEngine<T> {
    return RPC.serverOf(service, typeOf<T>(), transport, config)
}

/**
 * Creates a server engine for the specified RPC interface using the provided transport and service.
 *
 * @param service The service implementation that should handle incoming calls
 * @param serviceType The [KType] value of the [service]
 * @param transport The transport to be used for communication with the remote client
 * @param config Server configuration
 * @return An instance of the server engine for the specified RPC service
 */
fun <T : RPC> RPC.Companion.serverOf(
    service: T,
    serviceType: KType,
    transport: RPCTransport,
    config: RPCConfigBuilder.Server.() -> Unit = { },
): RPCServerEngine<T> {
    return serverOf(service, serviceType.kClass(), transport, config)
}

/**
 * Creates a server engine for the specified RPC interface using the provided transport and service.
 *
 * @param service The service implementation that should handle incoming calls
 * @param serviceKClass The [KClass] value of the [service]
 * @param transport The transport to be used for communication with the remote client
 * @param config Server configuration
 * @return An instance of the server engine for the specified RPC service
 */
fun <T : RPC> RPC.Companion.serverOf(
    service: T,
    serviceKClass: KClass<T>,
    transport: RPCTransport,
    config: RPCConfigBuilder.Server.() -> Unit = { },
): RPCServerEngine<T> {
    return RPCServerEngine(service, transport, serviceKClass, rpcServerConfig(config))
}

/**
 * Creates a server engine for the specified RPC interface using the provided transport and service.
 *
 * @param service The service implementation that should handle incoming calls
 * @param serviceKClass The [KClass] value of the [service]
 * @param transport The transport to be used for communication with the remote client
 * @param config Server configuration
 * @return An instance of the server engine for the specified RPC service
 */
fun <T : RPC> RPC.Companion.serverOf(
    service: T,
    serviceKClass: KClass<T>,
    transport: RPCTransport,
    config: RPCConfig.Server,
): RPCServerEngine<T> {
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
    noinline config: RPCConfigBuilder.Server.() -> Unit = { },
): RPCServerEngine<T> =
    RPC.serverOf(service, typeOf<T>(), transport, config)

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
    config: RPCConfigBuilder.Server.() -> Unit = { },
): RPCServerEngine<T> {
    return RPC.serverOf(service, serviceType, transport, config)
}
