package org.jetbrains.krpc.ktor.server

import KtorTransport
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.server.RPCServer
import org.jetbrains.krpc.server.rpcBackendOf
import org.jetbrains.krpc.server.serviceMethodOf
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T: RPC> Route.RPC(service: T, json: Json = Json) {
    RPC(service, typeOf<T>(), json) { serviceMethodOf<T>(it) }
}

fun <T: RPC> Route.RPC(service: T, serviceType: KType, json: Json, serviceMethodsGetter: (String) -> KType?) {
    webSocket {
        val transport = KtorTransport(json, this)
        val backend: RPCServer<T> = rpcBackendOf(service, serviceType, transport, serviceMethodsGetter)
        backend.start().join()
    }
}
