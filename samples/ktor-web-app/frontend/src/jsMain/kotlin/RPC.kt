/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.serialization.json
import org.jetbrains.krpc.transport.ktor.client.rpc
import org.jetbrains.krpc.transport.ktor.client.rpcConfig

suspend fun initRpcClient(): RPCClient {
    return HttpClient(Js) {
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
