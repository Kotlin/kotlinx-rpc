package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.job
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.server.RPCServerEngine
import org.jetbrains.krpc.server.serverOf
import org.jetbrains.krpc.transport.ktor.KtorTransport
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : RPC> Route.rpc(
    path: String,
    service: T,
    json: Json = Json,
    rpcConfig: RPCConfig.Server = RPCConfig.Server.Default,
) {
    route(path) {
        rpc(service, typeOf<T>(), json, rpcConfig)
    }
}

fun <T: RPC> Route.rpc(
    service: T,
    serviceType: KType,
    json: Json,
    rpcConfig: RPCConfig.Server = RPCConfig.Server.Default,
) {
    webSocket {
        val transport = KtorTransport(json, this)
        val server = RPC.serverOf(service, serviceType, transport, rpcConfig)
        server.coroutineContext.job.join()
    }
}
