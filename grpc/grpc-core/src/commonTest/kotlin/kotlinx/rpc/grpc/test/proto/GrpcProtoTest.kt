/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.ChannelCredentials
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.grpc.ServerCredentials

abstract class GrpcProtoTest {
    private val serverMutex = Mutex()

    abstract fun RpcServer.registerServices()

    protected fun runGrpcTest(
        serverCreds: ServerCredentials? = null,
        clientCreds: ChannelCredentials? = null,
        overrideAuthority: String? = null,
        test: suspend (GrpcClient) -> Unit,
    ) = runTest {
        serverMutex.withLock {
            val grpcClient = GrpcClient("localhost", PORT, credentials = clientCreds) {
                if (overrideAuthority != null) overrideAuthority(overrideAuthority)
                if (clientCreds == null) {
                    usePlaintext()
                }
            }

            val grpcServer = GrpcServer(
                PORT,
                credentials = serverCreds,
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
