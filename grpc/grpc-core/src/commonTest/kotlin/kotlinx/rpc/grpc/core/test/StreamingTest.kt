/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.core.test

import StreamingTestService
import kotlinx.coroutines.flow.*
import kotlinx.rpc.RpcServer
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamingTestServiceImpl : StreamingTestService {
    override fun Server(message: kotlinx.rpc.grpc.test.References): Flow<kotlinx.rpc.grpc.test.References> {
        return flow { emit(message); emit(message); emit(message) }
    }

    override suspend fun Client(message: Flow<kotlinx.rpc.grpc.test.References>): kotlinx.rpc.grpc.test.References {
        return message.last()
    }

    override fun Bidi(message: Flow<kotlinx.rpc.grpc.test.References>): Flow<kotlinx.rpc.grpc.test.References> {
        return message
    }
}

class StreamingTest : GrpcServerTest() {
    override fun RpcServer.registerServices() {
        registerService<StreamingTestService> { StreamingTestServiceImpl() }
    }

    @Test
    fun testServerStreaming() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<StreamingTestService>()
        service.Server(kotlinx.rpc.grpc.test.References {
            other = kotlinx.rpc.grpc.test.Other {
                field = 42
            }
        }).toList().run {
            assertEquals(3, size)

            forEach {
                assertEquals(42, it.other.field)
            }
        }
    }

    @Test
    fun testClientStreaming() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<StreamingTestService>()
        val result = service.Client(flow {
            repeat(3) {
                emit(kotlinx.rpc.grpc.test.References {
                    other = kotlinx.rpc.grpc.test.Other {
                        field = 42 + it
                    }
                })
            }
        })

        assertEquals(44, result.other.field)
    }

    @Test
    fun testBidiStreaming() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<StreamingTestService>()
        service.Bidi(flow {
            repeat(3) {
                emit(kotlinx.rpc.grpc.test.References {
                    other = kotlinx.rpc.grpc.test.Other {
                        field = 42 + it
                    }
                })
            }
        }).collectIndexed { i, it ->
            assertEquals(42 + i, it.other.field)
        }
    }
}
