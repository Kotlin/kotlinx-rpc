package org.jetbrains.krpc.server

import kotlinx.coroutines.CoroutineScope
import org.jetbrains.krpc.RPC
import kotlin.reflect.KClass

interface RPCServer : CoroutineScope {
    fun <Service : RPC> registerService(service: Service, serviceKClass: KClass<Service>)
}

inline fun <reified Service : RPC> RPCServer.registerService(service: Service) {
    registerService(service, Service::class)
}
