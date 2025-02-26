/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import ReferenceTestService
import References
import invoke
import Other
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class ReferenceTestServiceImpl : ReferenceTestService {
    override suspend fun Get(message: References): kotlinx.rpc.protobuf.test.References {
        return kotlinx.rpc.protobuf.test.References {
            other = kotlinx.rpc.protobuf.test.Other {
                field = message.other.arg.toInt()
            }

            primitive = message.other.arg
        }
    }

    override suspend fun Enum(message: UsingEnum): UsingEnum {
        return message
    }

    override suspend fun Optional(message: OptionalTypes): OptionalTypes {
        return message
    }
}

class TestReferenceService {
    @Test
    fun testReferenceService()= runBlocking {
        val grpcClient = initializeServerAndClient()

        val service = grpcClient.withService<ReferenceTestService>()
        val result = service.Get(References {
            other = Other {
                arg = "42"
            }
        })

        assertEquals("42", result.primitive)
        assertEquals(42, result.other.field)
    }

    @Test
    fun testEnum() = runBlocking {
        val grpcClient = initializeServerAndClient()

        val service = grpcClient.withService<ReferenceTestService>()
        val result = service.Enum(UsingEnum {
            enum = Enum.ONE
        })

        assertEquals(Enum.ONE, result.enum)
    }

    @Test
    fun testOptional() = runBlocking {
        val grpcClient = initializeServerAndClient()

        val service = grpcClient.withService<ReferenceTestService>()
        val resultNotNull = service.Optional(OptionalTypes {
            name = "test"
            age = 42
            reference = kotlinx.rpc.protobuf.test.Other {
                field = 42
            }
        })

        assertEquals("test", resultNotNull.name)
        assertEquals(42, resultNotNull.age)
        assertEquals(42, resultNotNull.reference?.field)

        val resultNullable = service.Optional(OptionalTypes {
            name = null
            age = null
            reference = null
        })

        assertEquals(null, resultNullable.name)
        assertEquals(null, resultNullable.age)
        assertEquals(null, resultNullable.reference)
    }

    private fun initializeServerAndClient(): GrpcClient {
        val grpcClient = GrpcClient("localhost", 8080) {
            usePlaintext()
        }

        val grpcServer = GrpcServer(8080) {
            registerService<ReferenceTestService> { ReferenceTestServiceImpl() }
        }

        grpcServer.start()
        return grpcClient
    }
}
