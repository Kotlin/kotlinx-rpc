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
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

    override suspend fun Nested(message: Nested): Nested {
        return message
    }

    override suspend fun Map(message: TestMap): TestMap {
        return message
    }

    override suspend fun OneOf(message: OneOf): OneOf {
        return message
    }
}

class TestReferenceService : GrpcServerTest() {
    override fun RpcServer.registerServices() {
        registerService<ReferenceTestService> { ReferenceTestServiceImpl() }
    }

    @Test
    fun testReferenceService() = runGrpcTest { grpcClient ->
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

    @Test
    fun testNested() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<ReferenceTestService>()
        val result = service.Nested(Nested {
            inner1 = Nested.Inner1 {
                inner11 = Nested.Inner1.Inner11 {
                    reference21 = null
                    reference12 = Nested.Inner1.Inner12 {
                        recursion = null
                    }
                    enum = Nested.Inner2.NestedEnum.ZERO
                }

                inner22 = Nested.Inner1.Inner12 {
                    recursion = Nested.Inner1.Inner12 {
                        recursion = null
                    }
                }

                string = "42_1"

                inner1 = null
            }

            inner2 = Nested.Inner2 {
                inner21 = Nested.Inner2.Inner21 {
                    reference11 = Nested.Inner1.Inner11 {
                        reference21 = null
                        reference12 = Nested.Inner1.Inner12 {
                            recursion = null
                        }
                        enum = Nested.Inner2.NestedEnum.ZERO
                    }
                    
                    reference22 = Nested.Inner2.Inner22 {
                        enum = Nested.Inner2.NestedEnum.ZERO
                    }
                }

                inner22 = Nested.Inner2.Inner22 {
                    enum = Nested.Inner2.NestedEnum.ZERO
                }
                string = "42_2"
            }

            string = "42"
            enum = Nested.Inner2.NestedEnum.ZERO
        })

        // Assert Inner1.Inner11
        assertEquals(null, result.inner1.inner11.reference21)
        assertEquals(null, result.inner1.inner11.reference12.recursion)
        assertEquals(Nested.Inner2.NestedEnum.ZERO, result.inner1.inner11.enum)

        // Assert Inner1.Inner12
        assertNotNull(result.inner1.inner22.recursion)
        assertEquals(null, result.inner1.inner22.recursion?.recursion)

        // Assert Inner1
        assertEquals("42_1", result.inner1.string)
        assertEquals(null, result.inner1.inner1)

        // Assert Inner2.Inner21
        assertEquals(null, result.inner2.inner21.reference11.reference21)
        assertEquals(null, result.inner2.inner21.reference11.reference12.recursion)
        assertEquals(Nested.Inner2.NestedEnum.ZERO, result.inner2.inner21.reference11.enum)
        assertEquals(Nested.Inner2.NestedEnum.ZERO, result.inner2.inner21.reference22.enum)

        // Assert Inner2.Inner22
        assertEquals(Nested.Inner2.NestedEnum.ZERO, result.inner2.inner22.enum)

        // Assert Inner2
        assertEquals("42_2", result.inner2.string)

        // Assert root Nested
        assertEquals("42", result.string)
        assertEquals(Nested.Inner2.NestedEnum.ZERO, result.enum)
    }

    @Test
    fun testMap() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<ReferenceTestService>()
        val result = service.Map(TestMap {
            primitives = mapOf("1" to 2, "2" to 1)
            references = mapOf("ref" to kotlinx.rpc.protobuf.test.References {
                other = kotlinx.rpc.protobuf.test.Other {
                    field = 42
                }
            })
        })

        assertEquals(mapOf("1" to 2L, "2" to 1L), result.primitives)
        assertEquals(mapOf("ref" to 42), result.references.mapValues { it.value.other.field })
    }

    @Test
    fun testOneOf() = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<ReferenceTestService>()
        val result1 = service.OneOf(OneOf {
            primitives = OneOf.Primitives.StringValue("42")
            references = OneOf.References.Other(kotlinx.rpc.protobuf.test.Other {
                field = 42
            })
            mixed = OneOf.Mixed.Int64(42L)
            single = OneOf.Single.Bytes(byteArrayOf(42))
        })

        assertEquals("42", (result1.primitives as OneOf.Primitives.StringValue).value)
        assertEquals(42, (result1.references as OneOf.References.Other).value.field)
        assertEquals(42L, (result1.mixed as OneOf.Mixed.Int64).value)
        assertContentEquals(byteArrayOf(42), (result1.single as OneOf.Single.Bytes).value)

        val result2 = service.OneOf(OneOf {
            primitives = OneOf.Primitives.Bool(true)
            references = OneOf.References.InnerReferences(kotlinx.rpc.protobuf.test.References {
                other = kotlinx.rpc.protobuf.test.Other {
                    field = 42
                }
            })
            mixed = OneOf.Mixed.AllPrimitives(AllPrimitives {
                string = "42"
            })
        })

        assertEquals(true, (result2.primitives as OneOf.Primitives.Bool).value)
        assertEquals(42, (result2.references as OneOf.References.InnerReferences).value.other.field)
        assertEquals("42", (result2.mixed as OneOf.Mixed.AllPrimitives).value.string)
        assertEquals(null, result2.single)

        val result3 = service.OneOf(OneOf {
            primitives = OneOf.Primitives.Int32(42)
        })

        assertEquals(42, (result3.primitives as OneOf.Primitives.Int32).value)
        assertEquals(null, result3.references)
        assertEquals(null, result3.mixed)
        assertEquals(null, result3.single)
    }
}
