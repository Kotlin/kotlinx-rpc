package org.jetbrains.krpc.client

import org.jetbrains.krpc.*
import org.jetbrains.krpc.internal.RPCClientProvider
import org.jetbrains.krpc.internal.findRPCProviderInCompanion
import org.jetbrains.krpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Creates a client for the specified RPC interface using the provided transport.
 *
 * @param T type of the service to be provided
 * @param transport The transport to be used for communication with the remote server
 * @param config Client configuration
 * @return An instance of the client for the specified RPC interface
 */
inline fun <reified T : RPC> RPC.Companion.clientOf(
    transport: RPCTransport,
    noinline config: RPCConfigBuilder.Client.() -> Unit = { },
): T {
    return clientOf(typeOf<T>(), transport, config)
}

/**
 * Creates a client of the specified RPC type using the given RPCEngine.
 *
 * @param T type of the service to be provided
 * @param serviceType The type of the service to retrieve.
 * @param transport The transport to be used for communication with the remote server
 * @param config Client configuration
 * @return A client of the specified RPC type.
 */
fun <T : RPC> RPC.Companion.clientOf(
    serviceType: KType,
    transport: RPCTransport,
    config: RPCConfigBuilder.Client.() -> Unit = { },
): T {
    return clientOf(serviceType.kClass(), transport, config)
}

/**
 * Creates a client of the specified RPC type using the given RPCEngine.
 *
 * @param T type of the service to be provided
 * @param serviceKClass The [KClass] of the service to retrieve.
 * @param transport The transport to be used for communication with the remote server
 * @param config Client configuration
 * @return A client of the specified RPC type.
 */
fun <T : RPC> RPC.Companion.clientOf(
    serviceKClass: KClass<T>,
    transport: RPCTransport,
    config: RPCConfigBuilder.Client.() -> Unit = { },
): T {
    return clientOf(serviceKClass, transport, rpcClientConfig(config))
}

/**
 * Creates a client of the specified RPC type using the given RPCEngine.
 *
 * @param T type of the service to be provided
 * @param serviceKClass The [KClass] of the service to retrieve.
 * @param transport The transport to be used for communication with the remote server
 * @param config Client configuration
 * @return A client of the specified RPC type.
 */
fun <T : RPC> RPC.Companion.clientOf(
    serviceKClass: KClass<T>,
    transport: RPCTransport,
    config: RPCConfig.Client,
): T {
    val engine = RPCClientEngineImpl(transport, serviceKClass, config)
    return clientOf(serviceKClass, engine)
}

/**
 * Returns an instance of the specified service type from the given RPC engine.
 *
 * @param T type of the service to be provided
 * @param serviceType The type of the service to retrieve.
 * @param engine The RPC engine used to retrieve the service.
 * @return An instance of the specified service type.
 */
fun <T : RPC> RPC.Companion.clientOf(serviceType: KType, engine: RPCClientEngine): T {
    return clientOf(serviceType.kClass(), engine)
}

/**
 * Creates a client of the specified RPC type using the given RPCEngine.
 *
 * @param T type of the service to be provided
 * @param engine The RPCEngine instance used for creating the client.
 * @return A client of the specified RPC type.
 */
inline fun <reified T : RPC> RPC.Companion.clientOf(engine: RPCClientEngine): T {
    return clientOf(T::class, engine)
}

/**
 * Returns an instance of the specified service type from the given RPC engine.
 *
 * @param T type of the service to be provided
 * @param kClass The type of the service to retrieve.
 * @param engine The RPC engine used to retrieve the service.
 */
fun <T : RPC> RPC.Companion.clientOf(kClass: KClass<T>, engine: RPCClientEngine): T {
    val withRPCClientObject = findRPCProviderInCompanion<RPCClientProvider<T>>(kClass)
    return withRPCClientObject.client(engine)
}

@Suppress("unused")
@Deprecated(
    "All RPC methods migrated to [RPC] scope",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(
        "RPC.clientOf<T>(transport)", "org.jetbrains.krpc.client.clientOf", "org.jetbrains.krpc.RPC"
    )
)
inline fun <reified T : RPC> rpcServiceOf(transport: RPCTransport): T = RPC.clientOf(transport)

@Suppress("unused")
@Deprecated(
    "All RPC methods migrated to the [RPC] scope",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("RPC.clientOf<T>(engine)", "org.jetbrains.krpc.client.clientOf", "org.jetbrains.krpc.RPC")
)
inline fun <reified T : RPC> rpcServiceOf(engine: RPCClientEngine): T = RPC.clientOf(engine)

@Deprecated(
    "All RPC methods migrated to the [RPC] scope",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(
        "RPC.clientOf<T>(serviceType, engine)",
        "org.jetbrains.krpc.client.clientOf", "org.jetbrains.krpc.RPC"
    )
)
fun <T : RPC> rpcServiceOf(serviceType: KType, engine: RPCClientEngine): T = RPC.clientOf(serviceType, engine)
