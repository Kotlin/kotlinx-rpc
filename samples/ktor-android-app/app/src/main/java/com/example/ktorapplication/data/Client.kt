package com.example.ktorapplication.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.url
import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.transport.ktor.client.rpc
import org.jetbrains.krpc.transport.ktor.client.rpcConfig

suspend fun createRpcClient(): RPCClient {
    return HttpClient(OkHttp) {
        install(WebSockets)
    }.rpc {
        url("ws://localhost:8080/api")

        rpcConfig {
            serialization {
                json()
            }
        }
    }
}