package org.jetbrains.krpc.server

import kotlinx.coroutines.launch
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransportMessage
import org.jetbrains.krpc.internal.qualifiedClassName
import org.jetbrains.krpc.internal.transport.RPCConnector
import org.jetbrains.krpc.internal.transport.RPCTransport
import org.jetbrains.krpc.server.internal.RPCServerService
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

abstract class KRPCServer(
    private val config: RPCConfig.Server,
    private val waitForServices: Boolean = true,
): RPCServer {
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
            transport = Transport(),
            waitForSubscribers = waitForServices,
            getKey = { serviceType },
        )
    }

    abstract suspend fun send(message: RPCTransportMessage)

    abstract suspend fun receive(): RPCTransportMessage

    private inner class Transport : RPCTransport {
        override val coroutineContext: CoroutineContext get() = this@KRPCServer.coroutineContext

        override suspend fun send(message: RPCTransportMessage) {
            this@KRPCServer.send(message)
        }

        override suspend fun receive(): RPCTransportMessage {
            return this@KRPCServer.receive()
        }
    }
}
