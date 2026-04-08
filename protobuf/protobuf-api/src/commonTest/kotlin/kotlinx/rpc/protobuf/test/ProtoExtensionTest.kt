/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtoExtensionRegistry
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProtoExtensionTest {
    private fun completeMessage() = ExtensionBase {
        int32 = 42
        string = "string field"
        enum = MyEnum.THREE
        msg = AllPrimitives {
            int32 = 123
            string = "test"
        }
        subExt = ExtensionBase {
            enum = MyEnum.ONE
            msg = AllPrimitives {
                bytes = byteArrayOf(1, 2, 3).asByteString()
            }
        }
        repeatedInt32 = listOf(1, 2, 3)
        repeatedEnum = listOf(MyEnum.ONE, MyEnum.THREE)
        repeatedMsg = listOf(
            AllPrimitives {
                int32 = 1
            },
            AllPrimitives {
                string = "two"
            },
        )
    }

    @Test
    fun `test extension registry - register single extension`() {
        val registry = ProtoExtensionRegistry {
            register(ExtensionBase.int32)
        }

        val extensions = registry.getAllExtensionsForMessage(ExtensionBase::class)
        assertEquals(1, extensions.size)
        assertEquals(10, extensions[10]?.fieldNumber)
    }

    @Test
    fun `test extension registry - register all extensions from other registry`() {
        val other = ProtoExtensionRegistry {
            register(ExtensionBase.int32)
            register(ExtensionBase.enum)
        }
        val registry = ProtoExtensionRegistry {
            registerAll(other)
        }

        val extensions = registry.getAllExtensionsForMessage(ExtensionBase::class)
        assertEquals(ExtensionBase.int32, extensions[10])
        assertEquals(ExtensionBase.enum, extensions[11])
    }


    @Test
    fun `test extension message getter setter`() {
        val message = completeMessage()

        assertEquals(42, message.int32)
        assertEquals(MyEnum.THREE, message.enum)
        assertEquals(listOf(1, 2, 3), message.repeatedInt32)
        assertEquals(listOf(MyEnum.ONE, MyEnum.THREE), message.repeatedEnum)
        assertEquals(2, message.repeatedMsg.size)
    }

    @Test
    fun `test extension message get non-set`() {
        val message = ExtensionBase {
            int32 = 42
        }

        assertEquals(42, message.int32)
        assertEquals(MyEnum.ZERO, message.enum)
    }

    @Test
    fun `test extension message set twice`() {
        val message = ExtensionBase {
            int32 = 42
            int32 = 123
        }

        assertEquals(123, message.int32)
    }

    @Test
    fun `test extension message equals`() {
        val message1 = completeMessage()
        val message2 = completeMessage()

        assertEquals(message1, message2)
    }

    @Test
    fun `test extension message not-equals`() {
        val message1 = completeMessage()
        val message2 = ExtensionBase {  }

        assertNotEquals(message1, message2)
        assertNotEquals(message2, message1)
    }

    @Test
    fun `test extension message copy`() {
        val message1 = completeMessage()
        val message2 = message1.copy {  }

        assertEquals(message1, message2)
        assertEquals(message2, message1)
    }

    @Test
    fun `test extension message copy not-equal`() {
        val message1 = completeMessage()
        val message2 = message1.copy {
            enum = MyEnum.TWO
        }

        assertNotEquals(message1, message2)
        assertNotEquals(message2, message1)
        assertEquals(MyEnum.TWO, message2.enum)
    }

    @Test
    fun `test extension message decoding`() {
        val message = completeMessage()

        // encoding works without extension registry
        val plainCodec = grpcMarshallerOf<ExtensionBase>()
        val encoded = plainCodec.encode(message)

        val registry = ProtoExtensionRegistry {
            +ExtensionBase.int32
            +ExtensionBase.string
            +ExtensionBase.enum
            +ExtensionBase.msg
            +ExtensionBase.subExt
            +ExtensionBase.repeatedInt32
            +ExtensionBase.repeatedEnum
            +ExtensionBase.repeatedMsg
        }
        val config = ProtoConfig(extensionRegistry = registry)
        val extensionCodec = grpcMarshallerOf<ExtensionBase>(config)

        val decoded = extensionCodec.decode(encoded)
        assertEquals(message.int32, decoded.int32)
        assertEquals(message.enum, decoded.enum)
        assertEquals(message.msg, decoded.msg)
        assertByteStringContentEquals(byteArrayOf(1, 2, 3), decoded.subExt.msg.bytes)
        assertEquals(message.repeatedInt32, decoded.repeatedInt32)
        assertEquals(message.repeatedEnum, decoded.repeatedEnum)
        assertEquals(message.repeatedMsg, decoded.repeatedMsg)
        assertEquals(message, decoded)
    }

    @Test
    fun `test nested extension message decoding minimal`() {
        val message = ExtensionBase {
            subExt = ExtensionBase {
                int32 = 123
            }
        }

        val plainCodec = grpcMarshallerOf<ExtensionBase>()
        val encoded = plainCodec.encode(message)

        val registry = ProtoExtensionRegistry {
            +ExtensionBase.int32
            +ExtensionBase.subExt
        }
        val config = ProtoConfig(extensionRegistry = registry)
        val extensionCodec = grpcMarshallerOf<ExtensionBase>(config)

        val decoded = extensionCodec.decode(encoded)
        assertEquals(message, decoded)
        assertEquals(123, decoded.subExt.int32)
    }

    @Test
    fun `test extension default instance is not mutated during decoding`() {
        val registry = ProtoExtensionRegistry {
            +ExtensionBase.msg
        }
        val codec = grpcMarshallerOf<ExtensionBase>(ProtoConfig(extensionRegistry = registry))
        val populated = ExtensionBase {
            msg = AllPrimitives {
                int32 = 123
                requiredString = "required"
                requiredBytes = byteArrayOf(1).asByteString()
            }
        }

        val decodedPopulated = codec.decode(grpcMarshallerOf<ExtensionBase>().encode(populated))
        assertEquals(123, decodedPopulated.msg.int32)

        val decodedEmpty = codec.decode(Buffer())
        assertEquals(0, decodedEmpty.msg.int32)
        assertEquals("", decodedEmpty.msg.requiredString)
        assertByteStringContentEquals(byteArrayOf(), decodedEmpty.msg.requiredBytes)
    }

    @Test
    fun `test extension message decode with missing extension`() {
        val message = completeMessage()
        val codec = grpcMarshallerOf<ExtensionBase>()
        val encoded = codec.encode(message)
        val decoded = codec.decode(encoded)
        // equals to extension message without any extension fields set
        assertEquals(ExtensionBase { }, decoded)
    }

    @Test
    fun `test generated repeated extension decoding`() {
        val message = ExtensionBase {
            repeatedInt32 = listOf(1, 2, 3)
            repeatedEnum = listOf(MyEnum.ONE, MyEnum.THREE)
            repeatedMsg = listOf(
                AllPrimitives {
                    int32 = 1
                },
                AllPrimitives {
                    string = "two"
                },
            )
            repeatedSubExt = listOf(
                ExtensionBase {
                    subExt = ExtensionBase {
                        msg = AllPrimitives {
                            bytes = byteArrayOf(1, 2, 3).asByteString()
                        }
                    }
                },
                ExtensionBase {
                    string = "some string field"
                },
            )
        }

        val encoded = grpcMarshallerOf<ExtensionBase>().encode(message)
        val registry = ProtoExtensionRegistry {
            +ExtensionBase.string
            +ExtensionBase.msg
            +ExtensionBase.subExt
            +ExtensionBase.repeatedInt32
            +ExtensionBase.repeatedEnum
            +ExtensionBase.repeatedMsg
            +ExtensionBase.repeatedSubExt
        }
        val decoded = grpcMarshallerOf<ExtensionBase>(ProtoConfig(extensionRegistry = registry)).decode(encoded)

        assertEquals(listOf(1, 2, 3), decoded.repeatedInt32)
        assertEquals(listOf(MyEnum.ONE, MyEnum.THREE), decoded.repeatedEnum)
        assertEquals(message.repeatedMsg, decoded.repeatedMsg)
        assertEquals(message.repeatedSubExt, decoded.repeatedSubExt)
    }

    @Test
    fun `test generated group extension decoding`() {
        val message = ExtensionBase {
            testgroup = TestGroup {
                int32 = 123
                string = "group-string"
            }
            with(MessageScopedExtensions) {
                testgroup = MessageScoped.TestGroup {
                    int32 = 456
                }
            }
        }

        val encoded = grpcMarshallerOf<ExtensionBase>().encode(message)
        val registry = ProtoExtensionRegistry {
            +ExtensionBase.testgroup
            with(MessageScopedExtensions) {
                +ExtensionBase.testgroup
            }
        }
        val decoded = grpcMarshallerOf<ExtensionBase>(ProtoConfig(extensionRegistry = registry)).decode(encoded)

        assertEquals(message.testgroup, decoded.testgroup)
        with(MessageScopedExtensions) {
            assertEquals(message.testgroup, decoded.testgroup)
            assertEquals(MessageScoped.TestGroup { int32 = 456 }, decoded.testgroup)
        }
    }

    @Test
    fun `test generated packed extension decoding`() {
        val message = ExtensionBase {
            repeatedInt32 = listOf(1, 2, 3)
            repeatedEnum = listOf(MyEnum.ONE, MyEnum.THREE)
        }

        val encoded = grpcMarshallerOf<ExtensionBase>().encode(message)

        val registry = ProtoExtensionRegistry {
            +ExtensionBase.repeatedInt32
            +ExtensionBase.repeatedEnum
        }
        val decoded = grpcMarshallerOf<ExtensionBase>(ProtoConfig(extensionRegistry = registry)).decode(encoded)

        assertEquals(listOf(1, 2, 3), decoded.repeatedInt32)
        assertEquals(listOf(MyEnum.ONE, MyEnum.THREE), decoded.repeatedEnum)
    }

    @Test
    fun `test generated packed extension decodes unpacked wire format`() {
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)

        listOf(1, 2, 3).forEach { value ->
            encoder.writeInt32(ExtensionBase.repeatedInt32.fieldNumber, value)
        }
        listOf(MyEnum.ONE, MyEnum.THREE).forEach { value ->
            encoder.writeEnum(ExtensionBase.repeatedEnum.fieldNumber, value.number)
        }
        encoder.flush()

        val registry = ProtoExtensionRegistry {
            +ExtensionBase.repeatedInt32
            +ExtensionBase.repeatedEnum
        }
        val decoded = grpcMarshallerOf<ExtensionBase>(ProtoConfig(extensionRegistry = registry)).decode(buffer)

        assertEquals(listOf(1, 2, 3), decoded.repeatedInt32)
        assertEquals(listOf(MyEnum.ONE, MyEnum.THREE), decoded.repeatedEnum)
    }

    @Test
    fun `test nested extension definitions getter setter and decoding`() {
        val message = ExtensionBase {
            conflicting = "apfelstrudel"
            with(MessageScopedExtensions) {
                conflicting = 121
                with(MessageScopedExtensions.MoreNestedExtensions) {
                    conflicting = 122
                }
            }
        }

        val encoded = grpcMarshallerOf<ExtensionBase>().encode(message)
        val registry = ProtoExtensionRegistry {
            +ExtensionBase.conflicting
            with(MessageScopedExtensions) {
                +ExtensionBase.conflicting
                with(MessageScopedExtensions.MoreNestedExtensions) {
                    +ExtensionBase.conflicting
                }
            }
        }
        val decoded = grpcMarshallerOf<ExtensionBase>(ProtoConfig(extensionRegistry = registry)).decode(encoded)

        assertEquals("apfelstrudel", decoded.conflicting)
        with(MessageScopedExtensions) {
            assertEquals(121, decoded.conflicting)
            with(MessageScopedExtensions.MoreNestedExtensions) {
                assertEquals(122, decoded.conflicting)
            }
        }
    }
}
