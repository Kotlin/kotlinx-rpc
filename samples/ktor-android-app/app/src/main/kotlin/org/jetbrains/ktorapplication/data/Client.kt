/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.ktorapplication.data

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
        url("ws://10.0.2.2:8080/api")

        rpcConfig {
            serialization {
                json()
            }
        }
    }
}