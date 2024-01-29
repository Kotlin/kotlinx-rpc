/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.server

import kotlinx.coroutines.launch
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCServer
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.logging.DumpLogger
import org.jetbrains.krpc.internal.logging.DumpLoggerNoop
import org.jetbrains.krpc.internal.qualifiedClassName
import org.jetbrains.krpc.internal.transport.RPCConnector
import org.jetbrains.krpc.server.internal.RPCServerService
import kotlin.reflect.KClass

/**
 * Default implementation of [RPCServer].
 * Takes care of tracking requests and responses,
 * serializing data, tracking streams, processing exceptions and other protocol responsibilities.
 * Routes resulting messages to the proper registered services.
 * Leaves out the delivery of encoded messages to the specific implementations.
 *
 * Simple example, how this server may be implemented:
 * ```kotlin
 * class MyTransport : RPCTransport { /*...*/ }
 *
 * class MyServer(
 *     config: RPCConfig.Server,
 *     override val coroutineContext: CoroutineContext,
 * ): KRPCServer(config), RPCTransport by MyTransport()
 * ```
 *
 * @param config configuration provided for that specific server. Applied to all services that use this server.
 */
public abstract class KRPCServer(private val config: RPCConfig.Server) : RPCServer, RPCTransport {
    override fun <Service : RPC> registerService(service: Service, serviceKClass: KClass<Service>) {
        val name = serviceKClass.qualifiedClassName

        val rpcService = RPCServerService(service, serviceKClass, config, connector)

        launch {
            connector.subscribeToMessages(name) {
                rpcService.accept(it)
            }
        }
    }

    private val connector by lazy {
        RPCConnector(
            serialFormat = config.serialFormatInitializer.build(),
            transport = this,
            waitForSubscribers = config.waitForServices,
            getKey = {
                serviceType
                    .removePrefix("class ") // beta-4.2 compatibility
            },
            isServer = true,
            dumpLogger = dumpLogger,
        )
    }

    @InternalKRPCApi
    protected open val dumpLogger: DumpLogger = DumpLoggerNoop
}
