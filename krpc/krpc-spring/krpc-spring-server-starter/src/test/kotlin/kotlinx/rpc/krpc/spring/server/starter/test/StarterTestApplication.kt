/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.starter.test

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.krpc.ktor.client.KtorRpcClient
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.time.Duration.Companion.seconds

// The application has no kRPC configuration, so the starter provides all beans.
@SpringBootApplication(proxyBeanMethods = false)
open class StarterTestApplication

internal suspend fun <T> withRpcTimeout(block: suspend () -> T): T = withContext(Dispatchers.Default) {
    withTimeout(10.seconds) { block() }
}

internal fun HttpClient.krpcClient(port: Int, path: String = "/krpc"): KtorRpcClient =
    rpc("ws://127.0.0.1:$port$path") {
        rpcConfig {
            serialization { json() }
        }
    }

internal fun webSocketHttpClient(): HttpClient = HttpClient(CIO) { install(WebSockets) }

// The AOT test loads Tomcat in a second class loader. The JVM accepts only one URL stream handler factory,
// so each Tomcat copy must skip the registration.
internal fun disableTomcatUrlStreamHandlerFactory() {
    TomcatURLStreamHandlerFactory.disable()
}
