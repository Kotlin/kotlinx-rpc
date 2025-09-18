/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.ClientCredentials
import kotlinx.rpc.grpc.client.ClientInterceptor
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.server.GrpcServer
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
                if (clientCreds != null) useCredentials(clientCreds)
                if (overrideAuthority != null) overrideAuthority(overrideAuthority)
                if (clientCreds == null) {
                    usePlaintext()
                }
                clientInterceptors.forEach { intercept(it) }
            }

            val grpcServer = GrpcServer(
                PORT,
                configure = {
                    serverCreds?.let { useCredentials(it) }
                    serverInterceptors.forEach { intercept(it) }
                },
                builder = {
                    registerServices()
                })

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
}
