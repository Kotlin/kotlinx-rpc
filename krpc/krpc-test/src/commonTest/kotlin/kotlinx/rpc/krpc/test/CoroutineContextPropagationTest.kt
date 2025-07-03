/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

class CoroutineContextPropagationTest {
    private val rpcServerConfig = rpcServerConfig {
        serialization {
            json()
        }
    }
    private val rpcClientConfig = rpcClientConfig {
        serialization {
            json {
                ignoreUnknownKeys = true
            }
        }
    }

    data class CoroutineElement(val value: String) : CoroutineContext.Element {
        object Key : CoroutineContext.Key<CoroutineElement>

        override val key: CoroutineContext.Key<*> = Key
    }

    @Test
    fun test() = runTest {
        var actualContext: CoroutineElement? = null
        val transport = LocalTransport(CoroutineElement("transport"))
        val server = KrpcTestServer(rpcServerConfig, transport.server)
        val client = KrpcTestClient(rpcClientConfig, transport.client)
        withContext(CoroutineElement("server")) {
            server.registerService(Echo::class) {
                object : Echo {
                    override suspend fun echo(message: String): String = run {
                        actualContext = currentCoroutineContext().get(CoroutineElement.Key)
                        "response"
                    }
                }
            }
        }
        withContext(CoroutineElement("client")) {
            client.withService(Echo::class).echo("request")
        }
        assertEquals(CoroutineElement("transport"), actualContext)
    }
}
