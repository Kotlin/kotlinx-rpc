/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.coroutines.CoroutineContext

abstract class BaseServiceTest {
    class Env(
        val service: TestService,
        val impl: TestServiceImpl,
        val transport: LocalTransport,
        testScope: CoroutineScope,
    ) : CoroutineScope by testScope

    protected suspend fun CoroutineScope.runServiceTest(
        parentContext: CoroutineContext,
        perCallBufferSize: Int = 100,
        body: suspend Env.() -> Unit,
    ) {
        val transport = LocalTransport(parentContext, recordTimestamps = false)

        val clientConfig = rpcClientConfig {
            serialization {
                json()
            }

            connector {
                this.perCallBufferSize = perCallBufferSize
            }
        }

        val serverConfig = rpcServerConfig {
            serialization {
                json()
            }

            connector {
                this.perCallBufferSize = perCallBufferSize
            }
        }

        val client = KrpcTestClient(clientConfig, transport.client)
        val service = client.withService<TestService>()

        val server = KrpcTestServer(serverConfig, transport.server)
        val impl = TestServiceImpl()
        server.registerService<TestService> { impl }

        val env = Env(service, impl, transport, this)
        try {
            body(env)
        } finally {
            client.close()
            server.close()
            client.awaitCompletion()
            server.awaitCompletion()
            transport.coroutineContext.cancelAndJoin()
        }
    }
}
