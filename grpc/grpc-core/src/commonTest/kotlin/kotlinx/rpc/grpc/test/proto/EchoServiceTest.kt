/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.grpc.test.*
import kotlinx.rpc.registerService
import kotlin.test.Test
import kotlin.time.Duration

class EchoServiceImpl : EchoService {

    override suspend fun UnaryEcho(message: EchoRequest): EchoResponse {
        delay(message.timeout.toLong())
        return EchoResponse { this.message = message.message }
    }

    override fun ServerStreamingEcho(message: EchoRequest): Flow<EchoResponse> {
        val count = message.serverStreamReps ?: 5u
        return flow {
            repeat(count.toInt()) {
                emit(EchoResponse { this.message = message.message })
            }
        }
    }

    override suspend fun ClientStreamingEcho(message: Flow<EchoRequest>): EchoResponse {
        val result = message.toList().joinToString(", ") { it.message }
        return EchoResponse { this.message = result }
    }

    override fun BidirectionalStreamingEcho(message: Flow<EchoRequest>): Flow<EchoResponse> {
        return flow {
            message.collect {
                emit(EchoResponse { this.message = it.message })
            }
        }
    }
}

class EchoServiceTest : BaseGrpcServiceTest() {


    @Test
    fun startServiceUntilTerminated() = runTest(timeout = Duration.INFINITE) {
        val server = GrpcServer(
            port = PORT,
            builder = { registerService<EchoService> { EchoServiceImpl() } }
        )

        server.start()

        server.awaitTermination()
    }

}