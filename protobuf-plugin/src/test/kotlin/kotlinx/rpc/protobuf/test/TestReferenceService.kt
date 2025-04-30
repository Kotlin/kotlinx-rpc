/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import ReferenceTestService
import References
import invoke
import Other
import kotlinx.rpc.RpcServer
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

    override suspend fun Repeated(message: Repeated): Repeated {
        return message
    }
}

class TestReferenceService : GrpcServerTest() {
    override fun RpcServer.registerServices() {
        registerService<ReferenceTestService> { ReferenceTestServiceImpl() }
    }

    @Test
    fun testReferenceService()= runGrpcTest { grpcClient ->
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
    fun testEnum() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<ReferenceTestService>()
        val result = service.Enum(UsingEnum {
            enum = Enum.ONE
        })

        assertEquals(Enum.ONE, result.enum)
    }

    @Test
    fun testOptional() = runGrpcTest { grpcClient ->
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

    @Test
    fun testRepeated() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<ReferenceTestService>()
        val result = service.Repeated(Repeated {
            listString = listOf("test", "hello")
            listReference = listOf(kotlinx.rpc.protobuf.test.References {
                other = kotlinx.rpc.protobuf.test.Other {
                    field = 42
                }
            })
        })

        assertEquals(listOf("test", "hello"), result.listString)
        assertEquals(1, result.listReference.size)
        assertEquals(42, result.listReference[0].other.field)

        val resultEmpty = service.Repeated(Repeated {})

        assertEquals(emptyList(), resultEmpty.listString)
        assertEquals(emptyList(), resultEmpty.listReference)
    }
}
