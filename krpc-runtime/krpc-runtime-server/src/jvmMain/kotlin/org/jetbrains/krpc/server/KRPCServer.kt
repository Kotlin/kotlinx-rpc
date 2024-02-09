/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.server

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCServer
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.SequentialIdCounter
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.objectId
import org.jetbrains.krpc.internal.qualifiedClassName
import org.jetbrains.krpc.internal.transport.RPCPlugin
import org.jetbrains.krpc.internal.transport.RPCProtocolMessage
import org.jetbrains.krpc.server.internal.RPCServerConnector
import org.jetbrains.krpc.server.internal.RPCServerService
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

private val SERVER_ATOMIC_CONNECTION_COUNTER = atomic(initial = 0L)

/**
 * Gives ids to the incoming connections in sequential order. Ids are sent to peers during the handshake process.
 */
private val serverConnectionIdConuter = object : SequentialIdCounter {
    override fun nextId(): Long {
        return SERVER_ATOMIC_CONNECTION_COUNTER.incrementAndGet()
    }
}

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
public abstract class KRPCServer(
    private val config: RPCConfig.Server,
    transport: RPCTransport,
) : RPCServer {
    final override val coroutineContext: CoroutineContext = transport.coroutineContext

    private val logger = CommonLogger.initialized().logger(objectId())

    private val connector by lazy {
        RPCServerConnector(
            serialFormat = config.serialFormatInitializer.build(),
            transport = transport,
            waitForServices = config.waitForServices,
        )
    }

    private val clientSupportedPlugins: MutableMap<Long, Set<RPCPlugin>> = mutableMapOf()

    private val idCounter: SequentialIdCounter = serverConnectionIdConuter

    private val subscribeToProtocolMessages: Job = launch {
        connector.subscribeToProtocolMessages(::handleProtocolMessage)
    }

    private suspend fun handleProtocolMessage(message: RPCProtocolMessage) {
        when (message) {
            is RPCProtocolMessage.Handshake -> {
                val connectionId = idCounter.nextId()
                clientSupportedPlugins[connectionId] = message.supportedPlugins
                connector.sendMessage(RPCProtocolMessage.Handshake(RPCPlugin.ALL, connectionId))
            }

            is RPCProtocolMessage.Failure -> {
                logger.error {
                    "Client [${message.connectionId}] failed to handle protocol message ${message.failedMessage}: " +
                            message.errorMessage
                }
            }
        }
    }

    override fun <Service : RPC> registerService(service: Service, serviceKClass: KClass<Service>) {
        val name = serviceKClass.qualifiedClassName

        val rpcService = RPCServerService(service, serviceKClass, config, connector) { connectionId ->
            connectionId?.let { clientSupportedPlugins[it] } ?: emptySet()
        }

        launch {
            subscribeToProtocolMessages.join()

            connector.subscribeToServiceMessages(name) {
                rpcService.accept(it)
            }
        }
    }
}
