/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.test

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import org.springframework.boot.builder.SpringApplicationBuilder
import java.util.Base64
import kotlin.time.Duration.Companion.seconds

internal class SecuredKrpcSpringServer : AutoCloseable {
    private val context = SpringApplicationBuilder(SecuredTestApplication::class.java)
        .run(
            "--server.port=0",
            "--spring.main.banner-mode=off",
            "--logging.level.root=ERROR",
        )

    private val port = context.environment.getProperty("local.server.port")!!.toInt()
    private val httpClient = HttpClient(CIO) { install(WebSockets) }

    // runTest uses virtual time, so the timeout runs on a real dispatcher.
    suspend fun <T> withRpcTimeout(block: suspend () -> T): T = withContext(Dispatchers.Default) {
        withTimeout(10.seconds) { block() }
    }

    fun rpcClient(username: String? = null, password: String? = null): KtorRpcClient =
        httpClient.rpc("ws://127.0.0.1:$port/krpc") {
            if (username != null && password != null) {
                val credentials = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
                header(HttpHeaders.Authorization, "Basic $credentials")
            }
            rpcConfig {
                serialization { json() }
            }
        }

    override fun close() {
        httpClient.close()
        context.close()
    }
}