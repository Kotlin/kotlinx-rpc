/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.compat.service

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.client.InitializedKrpcClient
import kotlinx.rpc.krpc.client.KrpcClient
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.server.KrpcServer
import kotlinx.rpc.krpc.test.compat.CompatService
import kotlinx.rpc.krpc.test.compat.CompatServiceImpl
import kotlinx.rpc.krpc.test.compat.CompatTransport
import kotlinx.rpc.krpc.test.compat.Starter
import kotlinx.rpc.krpc.test.compat.TestConfig
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.coroutines.CoroutineContext

fun CompatTransport.toKrpc(): KrpcTransport {
    return object : KrpcTransport {
        override suspend fun send(message: KrpcTransportMessage) {
            this@toKrpc.send((message as KrpcTransportMessage.StringMessage).value)
        }

        override suspend fun receive(): KrpcTransportMessage {
            return KrpcTransportMessage.StringMessage(this@toKrpc.receive())
        }

        override val coroutineContext: CoroutineContext = this@toKrpc.coroutineContext
    }
}

@Suppress("unused")
class TestStarter : Starter {
    private var client: KrpcClient? = null
    private var server: KrpcServer? = null

    override suspend fun startClient(transport: CompatTransport, config: TestConfig): CompatService {
        val transport = transport.toKrpc()
        val clientConfig = rpcClientConfig {
            serialization {
                json()
            }

            connector {
                perCallBufferSize = config.perCallBufferSize
            }
        }

        client = object : InitializedKrpcClient(clientConfig, transport) {}
        val service = client!!.withService<TestService>()
        return object : CompatService {
            override suspend fun unary(n: Int): Int {
                return service.unary(n)
            }

            override fun serverStreaming(num: Int): Flow<Int> {
                return service.serverStreaming(num)
            }

            override suspend fun clientStreaming(n: Flow<Int>): Int {
                return service.clientStreaming(n)
            }

            override fun bidiStreaming(flow: Flow<Int>): Flow<Int> {
                return service.bidiStreaming(flow)
            }

            override suspend fun requestCancellation() {
                return service.requestCancellation()
            }

            override fun serverStreamCancellation(): Flow<Int> {
                return service.serverStreamCancellation()
            }

            override suspend fun clientStreamCancellation(n: Flow<Int>) {
                return service.clientStreamCancellation(n)
            }

            override fun fastServerProduce(n: Int): Flow<Int> {
                return service.fastServerProduce(n)
            }
        }
    }

    override suspend fun stopClient() {
        client?.close()
        client?.awaitCompletion()
    }

    override suspend fun startServer(transport: CompatTransport, config: TestConfig): CompatServiceImpl {
        val transport = transport.toKrpc()
        val serverConfig = rpcServerConfig {
            serialization {
                json()
            }

            connector {
                perCallBufferSize = config.perCallBufferSize
            }
        }

        server = object : KrpcServer(serverConfig, transport) {}
        val impl = TestServiceImpl()
        server?.registerService<TestService> { impl }
        return impl
    }

    override suspend fun stopServer() {
        server?.close()
        server?.awaitCompletion()
    }
}
