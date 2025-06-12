/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server

import kotlinx.coroutines.*
import kotlinx.rpc.RpcServer
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.internal.*
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.server.internal.KrpcServerConnector
import kotlinx.rpc.krpc.server.internal.KrpcServerService
import kotlin.concurrent.Volatile
import kotlin.reflect.KClass

/**
 * kRPC implementation of the [RpcServer].
 * Takes care of tracking requests and responses,
 * serializing data, tracking streams, processing exceptions, and other protocol responsibilities.
 * Routes resulting messages to the proper registered services.
 * Leaves out the delivery of encoded messages to the specific implementations with [KrpcTransport].
 *
 * @param config configuration provided for that specific server. Applied to all services that use this server.
 * @param transport [KrpcTransport] instance that will be used to send and receive RPC messages.
 * IMPORTANT: Must be exclusive to this server, otherwise unexpected behavior may occur.
 */
@OptIn(InternalCoroutinesApi::class)
public abstract class KrpcServer(
    private val config: KrpcConfig.Server,
    transport: KrpcTransport,
) : RpcServer, KrpcEndpoint {

    /*
     * #####################################################################
     * #                                                                   #
     * #                         INTERNALS AHEAD                           #
     * #                                                                   #
     * #####################################################################
     */

    @InternalRpcApi
    public val internalScope: CoroutineScope = CoroutineScope(SupervisorJob(transport.coroutineContext.job))

    private val logger = RpcInternalCommonLogger.logger(rpcInternalObjectId())

    private val connector by lazy {
        KrpcServerConnector(
            serialFormat = config.serialFormatInitializer.build(),
            transport = transport,
            waitForServices = config.waitForServices,
        )
    }

    @InternalRpcApi
    final override val sender: KrpcMessageSender get() = connector

    @InternalRpcApi
    final override var supportedPlugins: Set<KrpcPlugin> = emptySet()
        private set

    private val rpcServices = RpcInternalConcurrentHashMap<String, KrpcServerService<*>>()

    @Volatile
    private var cancelledByClient = false

    init {
        internalScope.coroutineContext.job.invokeOnCompletion(onCancelling = true) {
            if (!cancelledByClient) {
                sendCancellation(CancellationType.ENDPOINT, null, null, closeTransportAfterSending = true)
            }
        }

        internalScope.launch(CoroutineName("krpc-server-generic-protocol-messages")) {
            connector.subscribeToProtocolMessages(::handleProtocolMessage)

            connector.subscribeToGenericMessages(::handleGenericMessage)
        }
    }

    private suspend fun handleProtocolMessage(message: KrpcProtocolMessage) {
        when (message) {
            is KrpcProtocolMessage.Handshake -> {
                supportedPlugins = message.supportedPlugins
                connector.sendMessage(KrpcProtocolMessage.Handshake(KrpcPlugin.ALL, connectionId = 1))
            }

            is KrpcProtocolMessage.Failure -> {
                logger.error {
                    "Client [${message.connectionId}] failed to handle protocol message ${message.failedMessage}: " +
                            message.errorMessage
                }
            }
        }
    }

    final override fun <@Rpc Service : Any> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: () -> Service,
    ) {
        val descriptor = serviceDescriptorOf(serviceKClass)

        internalScope.launch(CoroutineName("krpc-server-service-$descriptor")) {
            connector.subscribeToServiceMessages(descriptor.fqName) { message ->
                val rpcServerService = rpcServices.computeIfAbsent(descriptor.fqName) {
                    createNewServiceInstance(descriptor, serviceFactory)
                }

                rpcServerService.accept(message)
            }
        }
    }

    override fun <@Rpc Service : Any> deregisterService(serviceKClass: KClass<Service>) {
        connector.unsubscribeFromServiceMessages(serviceDescriptorOf(serviceKClass).fqName)
    }

    private fun <@Rpc Service : Any> createNewServiceInstance(
        descriptor: RpcServiceDescriptor<Service>,
        serviceFactory: () -> Service,
    ): KrpcServerService<Service> {
        return KrpcServerService(
            service = serviceFactory(),
            descriptor = descriptor,
            config = config,
            connector = connector,
            supportedPlugins = supportedPlugins,
            serverScope = internalScope,
        )
    }

    @InternalRpcApi
    final override suspend fun handleCancellation(message: KrpcGenericMessage) {
        when (val type = message.cancellationType()) {
            CancellationType.ENDPOINT -> {
                cancelledByClient = true

                internalScope.cancel("Server cancelled by client")
                rpcServices.clear()
            }

            CancellationType.REQUEST -> {
                val serviceType = message[KrpcPluginKey.CLIENT_SERVICE_ID]
                    ?: error("Expected CLIENT_SERVICE_ID for cancellation of type 'request'")

                val callId = message[KrpcPluginKey.CANCELLATION_ID]
                    ?: error("Expected CANCELLATION_ID for cancellation of type 'request'")

                rpcServices[serviceType]?.cancelRequestWithOptionalAck(callId, "Request cancelled by client")
            }

            else -> {
                logger.warn {
                    "Unsupported ${KrpcPluginKey.CANCELLATION_TYPE} $type for server, " +
                            "only 'endpoint' type may be sent by a server"
                }
            }
        }
    }
}
