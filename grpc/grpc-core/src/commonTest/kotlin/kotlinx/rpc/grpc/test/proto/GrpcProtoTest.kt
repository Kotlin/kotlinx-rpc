/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer

abstract class GrpcProtoTest {
    private val serverMutex = Mutex()

    abstract fun RpcServer.registerServices()

    protected fun runGrpcTest(test: suspend (GrpcClient) -> Unit, ) = runTest {
        serverMutex.withLock {
            val grpcClient = GrpcClient("localhost", 8080) {
                usePlaintext()
            }

            val grpcServer = GrpcServer(8080, builder = {
                registerServices()
            })

            grpcServer.start()
            test(grpcClient)
            grpcServer.shutdown()
            grpcServer.awaitTermination()
            grpcClient.shutdown()
            grpcClient.awaitTermination()
        }
    }
}
