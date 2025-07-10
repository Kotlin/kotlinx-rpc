/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import StreamingTestService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.RpcServer
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamingTestServiceImpl : StreamingTestService {
    override fun Server(message: References): Flow<References> {
        return flow { emit(message); emit(message); emit(message) }
    }

    override suspend fun Client(message: Flow<References>): References {
        return message.last()
    }

    override fun Bidi(message: Flow<References>): Flow<References> {
        return message
    }
}

class StreamingTest : GrpcServerTest() {
    override fun RpcServer.registerServices() {
        registerService<StreamingTestService> { StreamingTestServiceImpl() }
    }

    @Test
    fun testServerStreaming() = runGrpcTest {  grpcClient ->
        val service = grpcClient.withService<StreamingTestService>()
        service.Server(References {
            other = Other {
                field= 42
            }
        }).toList().run {
            assertEquals(3, size)

            forEach {
                assertEquals(42, it.other.field)
            }
        }
    }

    @Test
    fun testClientStreaming() = runGrpcTest {  grpcClient ->
        val service = grpcClient.withService<StreamingTestService>()
        val result = service.Client(flow {
            repeat(3) {
                emit(References {
                    other = Other {
                        field = 42 + it
                    }
                })
            }
        })

        assertEquals(44, result.other.field)
    }

    @Test
    fun testBidiStreaming() = runGrpcTest {  grpcClient ->
        val service = grpcClient.withService<StreamingTestService>()
        service.Bidi(flow {
            repeat(3) {
                emit(References {
                    other = Other {
                        field = 42 + it
                    }
                })
            }
        }).collectIndexed { i, it ->
            assertEquals(42 + i, it.other.field)
        }
    }
}
