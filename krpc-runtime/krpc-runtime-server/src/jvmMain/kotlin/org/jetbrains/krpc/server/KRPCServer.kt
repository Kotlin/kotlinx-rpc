package org.jetbrains.krpc.server

import kotlinx.coroutines.launch
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.qualifiedClassName
import org.jetbrains.krpc.internal.transport.RPCConnector
import org.jetbrains.krpc.server.internal.RPCServerService
import kotlin.reflect.KClass

abstract class KRPCServer(private val config: RPCConfig.Server): RPCServer, RPCTransport {
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
            getKey = { serviceType },
        )
    }
}
