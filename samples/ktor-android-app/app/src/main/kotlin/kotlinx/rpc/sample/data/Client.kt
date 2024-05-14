/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.sample.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.url
import kotlinx.rpc.RPCClient
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig
import kotlinx.rpc.transport.ktor.client.installRPC

suspend fun createRpcClient(): RPCClient {
    return HttpClient(OkHttp) {
        installRPC()
    }.rpc {
        url("ws://10.0.2.2:8080/api")

        rpcConfig {
            serialization {
                json()
            }
        }
    }
}
