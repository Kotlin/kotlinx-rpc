package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.server.RPCServer
import org.jetbrains.krpc.server.rpcServerOf
import org.jetbrains.krpc.transport.ktor.KtorTransport
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : RPC> Route.rpc(path: String, service: T, json: Json = Json) {
    route(path) {
        rpc(service, typeOf<T>(), json)
    }
}

fun <T: RPC> Route.rpc(service: T, serviceType: KType, json: Json) {
    webSocket {
        val transport = KtorTransport(json, this)
        val backend: RPCServer<T> = rpcServerOf(service, serviceType, transport)
        backend.run()
    }
}
