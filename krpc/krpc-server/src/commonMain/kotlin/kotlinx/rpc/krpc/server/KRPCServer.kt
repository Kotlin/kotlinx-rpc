/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server

import kotlinx.coroutines.*
import kotlinx.rpc.RPCServer
import kotlinx.rpc.RemoteService
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.internal.utils.InternalRPCApi
import kotlinx.rpc.internal.utils.map.ConcurrentHashMap
import kotlinx.rpc.krpc.RPCConfig
import kotlinx.rpc.krpc.RPCTransport
import kotlinx.rpc.krpc.internal.*
import kotlinx.rpc.krpc.internal.logging.CommonLogger
import kotlinx.rpc.krpc.server.internal.RPCServerConnector
import kotlinx.rpc.krpc.server.internal.RPCServerService
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

    final override fun <Service : RemoteService> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: (CoroutineContext) -> Service,
    ) {
        val descriptor = serviceDescriptorOf(serviceKClass)

        launch {
            connector.subscribeToServiceMessages(descriptor.fqName) { message ->
                val rpcServerService = when (val id = message.serviceId) {
                    null -> nullRpcServices.computeIfAbsent(descriptor.fqName) {
                        createNewServiceInstance(descriptor, serviceFactory)
                    }

                    else -> rpcServices.computeIfAbsent(id) {
                        createNewServiceInstance(descriptor, serviceFactory)
                    }
                }

                rpcServerService.accept(message)
            }
        }
    }

    private fun <Service : RemoteService> createNewServiceInstance(
        descriptor: RpcServiceDescriptor<Service>,
        serviceFactory: (CoroutineContext) -> Service,
    ): RPCServerService<Service> {
        val serviceInstanceContext = SupervisorJob(coroutineContext.job)

        return RPCServerService(
            service = serviceFactory(serviceInstanceContext),
            descriptor = descriptor,
            config = config,
            connector = connector,
            coroutineContext = serviceInstanceContext,
        ).apply {
            coroutineContext.job.invokeOnCompletion {
                connector.unsubscribeFromServiceMessages(descriptor.fqName)
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
