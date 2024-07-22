/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.server

import kotlinx.coroutines.*
import kotlinx.rpc.RPC
import kotlinx.rpc.RPCConfig
import kotlinx.rpc.RPCServer
import kotlinx.rpc.RPCTransport
import kotlinx.rpc.internal.InternalRPCApi
import kotlinx.rpc.internal.logging.CommonLogger
import kotlinx.rpc.internal.map.ConcurrentHashMap
import kotlinx.rpc.internal.objectId
import kotlinx.rpc.internal.qualifiedClassName
import kotlinx.rpc.internal.transport.*
import kotlinx.rpc.server.internal.RPCServerConnector
import kotlinx.rpc.server.internal.RPCServerService
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

/**
 * Default implementation of [RPCServer].
 * Takes care of tracking requests and responses,
 * serializing data, tracking streams, processing exceptions, and other protocol responsibilities.
 * Routes resulting messages to the proper registered services.
 * Leaves out the delivery of encoded messages to the specific implementations.
 *
 * A simple example of how this server may be implemented:
 * ```kotlin
 * class MyTransport : RPCTransport { /*...*/ }
 *
 * class MyServer(config: RPCConfig.Server): KRPCServer(config, MyTransport())
 * ```
 *
 * @param config configuration provided for that specific server. Applied to all services that use this server.
 * @param transport [RPCTransport] instance that will be used to send and receive RPC messages.
 * IMPORTANT: Must be exclusive to this server, otherwise unexpected behavior may occur.
 */
@OptIn(InternalCoroutinesApi::class)
public abstract class KRPCServer(
    private val config: RPCConfig.Server,
    transport: RPCTransport,
) : RPCServer, RPCEndpoint {
    // we make a child here, so we can send cancellation messages before closing the connection
    final override val coroutineContext: CoroutineContext = SupervisorJob(transport.coroutineContext.job)

    private val logger = CommonLogger.logger(objectId())

    private val connector by lazy {
        RPCServerConnector(
            serialFormat = config.serialFormatInitializer.build(),
            transport = transport,
            waitForServices = config.waitForServices,
        )
    }

    @InternalRPCApi
    final override val sender: RPCMessageSender get() = connector

    @InternalRPCApi
    final override var supportedPlugins: Set<RPCPlugin> = emptySet()
        private set

    private val rpcServices = ConcurrentHashMap<Long, RPCServerService<*>>()

    // compatibility with clients that do not have serviceId
    private var nullRpcServices = ConcurrentHashMap<String, RPCServerService<*>>()

    private var cancelledByClient = false

    init {
        coroutineContext.job.invokeOnCompletion(onCancelling = true) {
            if (!cancelledByClient) {
                sendCancellation(CancellationType.ENDPOINT, null, null, closeTransportAfterSending = true)
            }
        }

        launch {
            connector.subscribeToProtocolMessages(::handleProtocolMessage)

            connector.subscribeToGenericMessages(::handleGenericMessage)
        }
    }

    private suspend fun handleProtocolMessage(message: RPCProtocolMessage) {
        when (message) {
            is RPCProtocolMessage.Handshake -> {
                supportedPlugins = message.supportedPlugins
                connector.sendMessage(RPCProtocolMessage.Handshake(RPCPlugin.ALL, connectionId = 1))
            }

            is RPCProtocolMessage.Failure -> {
                logger.error {
                    "Client [${message.connectionId}] failed to handle protocol message ${message.failedMessage}: " +
                            message.errorMessage
                }
            }
        }
    }

    final override fun <Service : RPC> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: (CoroutineContext) -> Service,
    ) {
        val fqServiceName = serviceKClass.qualifiedClassName

        launch {
            connector.subscribeToServiceMessages(fqServiceName) { message ->
                val rpcServerService = when (val id = message.serviceId) {
                    null -> nullRpcServices.computeIfAbsent(fqServiceName) {
                        createNewServiceInstance(serviceKClass, serviceFactory)
                    }

                    else -> rpcServices.computeIfAbsent(id) {
                        createNewServiceInstance(serviceKClass, serviceFactory)
                    }
                }

                rpcServerService.accept(message)
            }
        }
    }

    private fun <Service : RPC> createNewServiceInstance(
        serviceKClass: KClass<Service>,
        serviceFactory: (CoroutineContext) -> Service,
    ): RPCServerService<Service> {
        val serviceInstanceContext = SupervisorJob(coroutineContext.job)

        return RPCServerService(
            service = serviceFactory(serviceInstanceContext),
            serviceKClass = serviceKClass,
            config = config,
            connector = connector,
            coroutineContext = serviceInstanceContext,
        ).apply {
            coroutineContext.job.invokeOnCompletion {
                connector.unsubscribeFromServiceMessages(serviceKClass.qualifiedClassName)
            }
        }
    }

    @InternalRPCApi
    final override suspend fun handleCancellation(message: RPCGenericMessage) {
        when (val type = message.cancellationType()) {
            CancellationType.ENDPOINT -> {
                cancelledByClient = true

                cancel("Server cancelled by client")
                rpcServices.clear()
            }

            CancellationType.SERVICE -> {
                val serviceId = message[RPCPluginKey.CLIENT_SERVICE_ID]?.toLongOrNull()
                    ?: error("Expected CLIENT_SERVICE_ID for cancellation of type 'service' as Long value")

                rpcServices[serviceId]?.cancel("Service cancelled by client")
            }

            CancellationType.REQUEST -> {
                val serviceId = message[RPCPluginKey.CLIENT_SERVICE_ID]?.toLongOrNull()
                    ?: error("Expected CLIENT_SERVICE_ID for cancellation of type 'request' as Long value")

                val callId = message[RPCPluginKey.CANCELLATION_ID]
                    ?: error("Expected CANCELLATION_ID for cancellation of type 'request'")

                rpcServices[serviceId]?.cancelRequest(callId, "Request cancelled by client")
            }

            else -> {
                logger.warn {
                    "Unsupported ${RPCPluginKey.CANCELLATION_TYPE} $type for server, " +
                            "only 'endpoint' type may be sent by a server"
                }
            }
        }
    }
}
