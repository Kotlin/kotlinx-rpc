/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.runTest
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

    object CoroutineElement : CoroutineContext.Element {
        object Key : CoroutineContext.Key<CoroutineElement>

        override val key: CoroutineContext.Key<*> = Key
    }

    @Test
    fun test() = runTest {
        var actualContext: CoroutineElement? = null
        val transport = LocalTransport(CoroutineScope(CoroutineElement))
        val server = KrpcTestServer(rpcServerConfig, transport.server)
        val client = KrpcTestClient(rpcClientConfig, transport.client)
        server.registerService(Echo::class) {
            object : Echo {
                override suspend fun echo(message: String): String = run {
                    actualContext = currentCoroutineContext().get(CoroutineElement.Key)
                    "response"
                }

                override val coroutineContext: CoroutineContext = it
            }
        }
        client.withService(Echo::class).echo("request")
        assertEquals(actualContext, CoroutineElement)
    }
}