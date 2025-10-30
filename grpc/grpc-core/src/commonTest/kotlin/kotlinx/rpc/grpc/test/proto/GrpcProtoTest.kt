/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.ClientCallScope
import kotlinx.rpc.grpc.client.ClientCredentials
import kotlinx.rpc.grpc.client.ClientInterceptor
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.ServerCallScope
import kotlinx.rpc.grpc.server.ServerCredentials
import kotlinx.rpc.grpc.server.ServerInterceptor

abstract class GrpcProtoTest {
    private val serverMutex = Mutex()

    abstract fun RpcServer.registerServices()

    protected fun runGrpcTest(
        serverCreds: ServerCredentials? = null,
        clientCreds: ClientCredentials? = null,
        overrideAuthority: String? = null,
        clientInterceptors: List<ClientInterceptor> = emptyList(),
        serverInterceptors: List<ServerInterceptor> = emptyList(),
        test: suspend (GrpcClient) -> Unit,
    ) = runTest {
        serverMutex.withLock {
            val grpcClient = GrpcClient("localhost", PORT) {
                credentials = clientCreds ?: plaintext()
                if (overrideAuthority != null) this.overrideAuthority = overrideAuthority
                clientInterceptors.forEach { intercept(it) }
            }

            val grpcServer = GrpcServer(
                PORT,
            ) {
                credentials = serverCreds
                serverInterceptors.forEach { intercept(it) }
                services { registerServices() }
            }

            grpcServer.start()
            try {
                test(grpcClient)
            } finally {
                grpcServer.shutdown()
                grpcServer.awaitTermination()
                grpcClient.shutdown()
                grpcClient.awaitTermination()
            }
        }
    }

    companion object {
        const val PORT = 8080
    }

    internal fun serverInterceptor(
        block: ServerCallScope<Any, Any>.(Flow<Any>) -> Flow<Any>,
    ): List<ServerInterceptor> {
        return listOf(object : ServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Req, Resp> ServerCallScope<Req, Resp>.intercept(
                request: Flow<Req>,
            ): Flow<Resp> {
                with(this as ServerCallScope<Any, Any>) {
                    return block(request as Flow<Any>) as Flow<Resp>
                }
            }
        })
    }

    internal fun clientInterceptor(
        block: ClientCallScope<Any, Any>.(Flow<Any>) -> Flow<Any>,
    ): List<ClientInterceptor> {
        return listOf(object : ClientInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Req, Resp> ClientCallScope<Req, Resp>.intercept(
                request: Flow<Req>,
            ): Flow<Resp> {
                with(this as ClientCallScope<Any, Any>) {
                    return block(request as Flow<Any>) as Flow<Resp>
                }
            }
        })
    }
}
