/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.test

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.cbor.cbor
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.boot.builder.SpringApplicationBuilder
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSerializationApi::class)
internal class KrpcSpringServer(
    private val serializationFormat: String = "json",
) : AutoCloseable {
    private val context = SpringApplicationBuilder(TestApplication::class.java)
        .run(
            "--server.port=0",
            "--spring.main.banner-mode=off",
            "--logging.level.root=ERROR",
            "--test.serialization=$serializationFormat",
        )

    private val port = context.environment.getProperty("local.server.port")!!.toInt()
    private val httpClient = HttpClient(CIO) { install(WebSockets) }

    // runTest uses virtual time, so the timeout runs on a real dispatcher.
    suspend fun <T> withRpcTimeout(block: suspend () -> T): T = withContext(Dispatchers.Default) {
        withTimeout(10.seconds) { block() }
    }

    fun rpcClient(): KtorRpcClient = httpClient.rpc("ws://127.0.0.1:$port/krpc") {
        rpcConfig {
            serialization {
                when (serializationFormat) {
                    "json" -> json()
                    "cbor" -> cbor()
                    else -> error("Unsupported test serialization format: $serializationFormat")
                }
            }
        }
    }

    fun closeApplicationContext() {
        context.close()
    }

    override fun close() {
        httpClient.close()
        closeApplicationContext()
    }
}
