/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.grpc.marshaller.marshallerOf
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.buildProtoExtensionRegistry
import kotlinx.rpc.protobuf.internal.InternalExtensionDescriptor.Companion.string
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProtoExtensionTest {

    private fun completeMessage() = ExtensionBase {
        int32 = 42
        enum = MyEnum.THREE
        msg = AllPrimitives {
            int32 = 123
            string = "test"
        }
        subExt = ExtensionBase {
            enum = MyEnum.ONE
            msg = AllPrimitives {
                bytes = byteArrayOf(1, 2, 3)
            }
        }
    }

    @Test
    fun `test extension registry - register single extension`() {
        val registry = buildProtoExtensionRegistry {
            register(ExtensionBase.int32)
        }

        val extensions = registry.allExtensionsForMessage(ExtensionBase::class)
        assertEquals(1, extensions.size)
        assertEquals(10, extensions[10]?.fieldNumber)
    }

    @Test
    fun `test extension registry - register all extensions from other registry`() {
        val other = buildProtoExtensionRegistry {
            register(ExtensionBase.int32)
            register(ExtensionBase.enum)
        }
        val registry = buildProtoExtensionRegistry {
            registerAll(other)
        }

        val extensions = registry.allExtensionsForMessage(ExtensionBase::class)
        assertEquals(ExtensionBase.int32, extensions[10])
        assertEquals(ExtensionBase.enum, extensions[11])
    }


    @Test
    fun `test extension message getter setter`() {
        val message = completeMessage()

        assertEquals(42, message.int32)
        assertEquals(MyEnum.THREE, message.enum)
    }

    @Test
    fun `test extension message get non-set`() {
        val message = ExtensionBase {
            int32 = 42
        }

        assertEquals(42, message.int32)
        assertEquals(null, message.enum)
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
        val plainCodec = marshallerOf<ExtensionBase>()
        val encoded = plainCodec.encode(message)

        val registry = buildProtoExtensionRegistry {
            +ExtensionBase.int32
            +ExtensionBase.enum
            +ExtensionBase.msg
            +ExtensionBase.subExt
        }
        val config = ProtobufConfig(extensionRegistry = registry)
        val extensionCodec = marshallerOf<ExtensionBase>(config)

        val decoded = extensionCodec.decode(encoded)
        assertEquals(message, decoded)
        assertEquals(message.int32, decoded.int32)
        assertEquals(message.enum, decoded.enum)
        assertEquals(message.msg, decoded.msg)
        assertEquals(byteArrayOf(1, 2, 3), decoded.subExt.msg.bytes)
    }

    @Test
    fun `test nested extension message decoding minimal`() {
        val message = ExtensionBase {
            subExt = ExtensionBase {
                int32 = 123
            }
        }

        val plainCodec = marshallerOf<ExtensionBase>()
        val encoded = plainCodec.encode(message)

        val registry = buildProtoExtensionRegistry {
            +ExtensionBase.int32
            +ExtensionBase.subExt
        }
        val config = ProtobufConfig(extensionRegistry = registry)
        val extensionCodec = marshallerOf<ExtensionBase>(config)

        val decoded = extensionCodec.decode(encoded)
        assertEquals(message, decoded)
        assertEquals(123, decoded.subExt.int32)
    }

    @Test
    fun `test extension message decode with missing extension`() {
        val message = completeMessage()
        val codec = marshallerOf<ExtensionBase>()
        val encoded = codec.encode(message)
        val decoded = codec.decode(encoded)
        // equals to extension message without any extension fields set
        assertEquals(ExtensionBase { }, decoded)
    }
}
