package org.jetbrains.krpc.ktor.server

import KtorTransport
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.server.RPCServer
import org.jetbrains.krpc.server.rpcBackendOf
import org.jetbrains.krpc.server.rpcServiceMethodSerializationTypeOf
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : RPC> Route.rpc(service: T, json: Json = Json) {
    rpc(service, typeOf<T>(), json)
}

fun <T: RPC> Route.rpc(service: T, serviceType: KType, json: Json) {
    webSocket {
        val transport = KtorTransport(json, this)
        val backend: RPCServer<T> = rpcBackendOf(service, serviceType, transport)
        backend.start().join()
    }
}
