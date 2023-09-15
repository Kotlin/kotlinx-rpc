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

fun <T: RPC> Route.rpc(service: T, serviceType: KType, json: Json, serviceMethodsGetter: (String) -> KType?) {
    webSocket {
        val transport = KtorTransport(json, this)
        val backend: RPCServer<T> = rpcBackendOf(service, serviceType, transport, serviceMethodsGetter)
        backend.start().join()
    }
}
