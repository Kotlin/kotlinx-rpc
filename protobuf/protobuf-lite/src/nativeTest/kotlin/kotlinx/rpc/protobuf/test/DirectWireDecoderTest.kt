/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.protobuf.test

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.encodeToBuffer
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.NativeBufferZeroCopyInput
import kotlinx.rpc.protobuf.internal.withWireDecoder
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtoExtensionRegistry
import test.groups.WithGroups
import test.groups.invoke
import test.nested.NestedOuter
import test.nested.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DirectWireDecoderTest {
    private fun <M : Any> compare(message: M, marshaller: GrpcMarshaller<M>) {
        compareBytes(marshaller.encodeToBuffer(message).readByteArray(), marshaller)
    }

    private fun <M : Any> compareBytes(bytes: ByteArray, marshaller: GrpcMarshaller<M>) {
        val buffered = Buffer().apply { write(bytes) }
        val expected = marshaller.decode(BufferMessageReader(buffered, bytes.size))
        withReader(bytes) { reader ->
            assertEquals(expected, marshaller.decode(reader))
            assertEquals(bytes.size, bytes.size - reader.remaining)
        }
    }

    @Test
    fun populatedMessagesMatchBufferDecoding() {
        compare(AllPrimitives {}, grpcMarshallerOf<AllPrimitives>())
        compare(AllPrimitives {
            int32 = -123
            int64 = Long.MAX_VALUE
            uint32 = UInt.MAX_VALUE
            uint64 = ULong.MAX_VALUE
            sint32 = -456
            sint64 = Long.MIN_VALUE
            fixed32 = UInt.MAX_VALUE
            fixed64 = ULong.MAX_VALUE
            sfixed32 = Int.MIN_VALUE
            sfixed64 = Long.MIN_VALUE
            bool = true
            float = 1.25f
            double = -42.5
            string = "direct decoding 世界"
            bytes = byteArrayOf(0, 1, -1).asByteString()
        }, grpcMarshallerOf<AllPrimitives>())
        compare(Repeated {
            listInt32 = listOf(-1, 0, 127, Int.MAX_VALUE)
            listInt32Packed = listOf(-1, 0, 128)
            listFixed32 = listOf(1u, UInt.MAX_VALUE)
            listFixed32Packed = listOf(1u, UInt.MAX_VALUE)
            listString = listOf("one", "two")
            listMessage = listOf(Repeated.Other { a = 3 })
        }, grpcMarshallerOf<Repeated>())
        compare(TestMap {
            primitives = mapOf("one" to 1, "two" to 2)
            messages = mapOf(1 to PresenceCheck { requiredPresence = 7 })
        }, grpcMarshallerOf<TestMap>())
        compare(NestedOuter {
            deep = NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner { num = 42 }
        }, grpcMarshallerOf<NestedOuter>())
        compare(WithGroups {
            firstgroup = WithGroups.FirstGroup { value = 23u }
            secondgroup = listOf(WithGroups.SecondGroup { value = "second" })
        }, grpcMarshallerOf<WithGroups>())
        compare(ExtensionBase {
            int32 = 42
            string = "extension"
            repeatedInt32 = listOf(1, 2, 3)
        }, grpcMarshallerOf<ExtensionBase>())
        compare(UnknownFieldsAll { field1 = 1; intMissing = 99 }, grpcMarshallerOf<UnknownFieldsAll>())
        compare(AllPrimitives { bytes = ByteArray(1024 * 1024) { it.toByte() }.asByteString() },
            grpcMarshallerOf<AllPrimitives>())
    }

    @Test
    fun everyTestProtoMessageMatchesBufferDecoding() {
        compareDefault(grpcMarshallerOf<hello.HelloRequest>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.AllPrimitives>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.EchoRequest>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.EchoResponse>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.HelloReply>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.HelloRequest>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Message>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Nested>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Nested.Inner1>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Nested.Inner1.Inner11>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Nested.Inner1.Inner12>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Nested.Inner2>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Nested.Inner2.Inner21>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Nested.Inner2.Inner22>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.OneOf>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.OptionalTypes>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Other>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.References>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.Repeated>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.TestMap>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.UnknownFieldsAll>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.UnknownFieldsSubset>())
        compareDefault(grpcMarshallerOf<kotlinx.rpc.grpc.test.UsingEnum>())
        compareDefault(grpcMarshallerOf<some.Some>())
        compareDefault(grpcMarshallerOf<some.Some.Nested>())
        compareDefault(grpcMarshallerOf<some.Some.Nested.Nested>())
        compareDefault(grpcMarshallerOf<some.Some.Nested.Nested.Nested>())
        compareRequiredString(grpcMarshallerOf<to.be.imported.IWantToBeImported>(), "hello")
        compareRequiredString(grpcMarshallerOf<hello.HelloResponse>(), "reply")
    }

    private fun <M : Any> compareDefault(marshaller: GrpcMarshaller<M>) {
        compareBytes(byteArrayOf(), marshaller)
    }

    private fun <M : Any> compareRequiredString(marshaller: GrpcMarshaller<M>, value: String) {
        val wire = Buffer()
        WireEncoder(wire).apply { writeString(1, value); flush() }
        compareBytes(wire.readByteArray(), marshaller)
    }

    @Test
    fun malformedInputHasSameExceptionType() {
        val encoded = grpcMarshallerOf<AllPrimitives>().encodeToBuffer(AllPrimitives {
            int32 = 123
            string = "truncated field"
            bytes = byteArrayOf(1, 2, 3).asByteString()
        }).readByteArray()
        val malformed = buildList {
            for (offset in 0 until encoded.size) add(encoded.copyOf(offset))
            add(byteArrayOf(0x08, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1))
            add(byteArrayOf(0x72, 0x01, -1)) // invalid UTF-8 string
            add(byteArrayOf(-1, -1, -1, -1, 0x1f)) // out-of-range tag
        }
        val marshaller = grpcMarshallerOf<AllPrimitives>()
        for (bytes in malformed) {
            val expected = runCatching {
                marshaller.decode(BufferMessageReader(Buffer().apply { write(bytes) }, bytes.size))
            }.exceptionOrNull()
            val actual = withReader(bytes) { reader -> runCatching { marshaller.decode(reader) }.exceptionOrNull() }
            assertEquals(expected?.let { it::class }, actual?.let { it::class }, "input size ${bytes.size}")
        }
        for (bytes in malformed.takeLast(3)) {
            val failure = withReader(bytes) { reader -> runCatching { marshaller.decode(reader) }.exceptionOrNull() }
            assertNotNull(failure)
        }

        val nested = grpcMarshallerOf<NestedOuter>().encodeToBuffer(NestedOuter {
            deep = NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner { num = 42 }
        }).readByteArray()
        val limited = grpcMarshallerOf<NestedOuter>(ProtoConfig { recursionLimit = 0 })
        val bufferedFailure = runCatching {
            limited.decode(BufferMessageReader(Buffer().apply { write(nested) }, nested.size))
        }.exceptionOrNull()
        val directFailure = withReader(nested) { reader -> runCatching { limited.decode(reader) }.exceptionOrNull() }
        assertNotNull(bufferedFailure)
        assertEquals(bufferedFailure::class, directFailure?.let { it::class })
    }

    @Test
    fun decodedValuesOwnTheirMemory() {
        val bytes = grpcMarshallerOf<AllPrimitives>().encodeToBuffer(AllPrimitives {
            string = "owned string"
            bytes = byteArrayOf(1, 2, 3).asByteString()
        }).readByteArray()
        withReader(bytes) { reader ->
            val decoded = grpcMarshallerOf<AllPrimitives>().decode(reader)
            reader.overwrite()
            assertEquals("owned string", decoded.string)
            assertEquals(byteArrayOf(1, 2, 3).asByteString(), decoded.bytes)
        }
        val unknownBytes = grpcMarshallerOf<UnknownFieldsAll>().encodeToBuffer(
            UnknownFieldsAll { field1 = 1; intMissing = 99 },
        ).readByteArray()
        withReader(unknownBytes) { reader ->
            val decoded = grpcMarshallerOf<UnknownFieldsSubset>().decode(reader)
            reader.overwrite()
            val roundTrip = grpcMarshallerOf<UnknownFieldsSubset>().encodeToBuffer(decoded).readByteArray()
            assertEquals(99, grpcMarshallerOf<UnknownFieldsAll>().decode(
                BufferMessageReader(Buffer().apply { write(roundTrip) }, roundTrip.size),
            ).intMissing)
        }
        val extensionCodec = grpcMarshallerOf<ExtensionBase>(ProtoConfig {
            extensionRegistry = ProtoExtensionRegistry {
                +ExtensionBase.string
                +ExtensionBase.repeatedInt32
            }
        })
        val extensionBytes = extensionCodec.encodeToBuffer(ExtensionBase {
            string = "owned extension"
            repeatedInt32 = listOf(3, 5, 8)
        }).readByteArray()
        withReader(extensionBytes) { reader ->
            val decoded = extensionCodec.decode(reader)
            reader.overwrite()
            assertEquals("owned extension", decoded.string)
            assertEquals(listOf(3, 5, 8), decoded.repeatedInt32)
        }
    }

    @Test
    fun decoderCannotReadAfterScope() {
        var escaped: WireDecoder? = null
        withReader(byteArrayOf(0x08, 0x01)) { reader ->
            withWireDecoder(reader) { escaped = it; assertNotNull(it.readTag()) }
            assertFailsWith<IllegalStateException> { escaped!!.readTag() }
        }
        withReader(byteArrayOf()) { reader ->
            reader.close()
            assertFailsWith<IllegalStateException> { withWireDecoder(reader) { it.readTag() } }
        }
    }

    @Test
    fun commitsOnlyConsumedBytes() = withReader(byteArrayOf(0x08, 0x01, 0x10, 0x02)) { reader ->
        withWireDecoder(reader) { decoder ->
            assertNotNull(decoder.readTag())
            assertEquals(1, decoder.readInt32())
        }
        assertEquals(2, reader.remaining)
        withWireDecoder(reader) { decoder ->
            assertNotNull(decoder.readTag())
            assertEquals(2, decoder.readInt32())
        }
        assertEquals(0, reader.remaining)
    }

    @Test
    fun nativeInputExposesOneChunkAndChecksBounds() = memScoped {
        val base = allocArray<ByteVar>(4)
        val data = alloc<CPointerVar<ByteVar>>()
        val size = alloc<IntVar>()
        val input = NativeBufferZeroCopyInput(base, 4)
        assertTrue(input.next(data.ptr, size.ptr))
        assertEquals(base, data.value)
        assertEquals(4, size.value)
        assertFalse(input.next(data.ptr, size.ptr))
        input.backUp(2)
        assertEquals(2L, input.byteCount())
        assertTrue(input.next(data.ptr, size.ptr))
        assertEquals(base + 2, data.value)
        assertEquals(2, size.value)
        assertFalse(input.skip(1))
        assertEquals(4L, input.byteCount())
        assertFailsWith<IllegalArgumentException> { NativeBufferZeroCopyInput(null, 1) }
        Unit
    }

    private inline fun <R> withReader(bytes: ByteArray, action: (HeapReader) -> R): R {
        val reader = HeapReader(bytes)
        return try { action(reader) } finally { reader.close(); reader.release() }
    }
}

private class HeapReader(bytes: ByteArray) : NativeBufferMessageReader(bytes.size) {
    private val storage = nativeHeap.allocArray<ByteVar>(maxOf(size, 1))

    init { for (index in bytes.indices) storage[index] = bytes[index] }

    override fun accessBuffer(block: (CPointer<ByteVar>?) -> Unit) {
        block(if (size == 0) null else storage)
    }

    fun overwrite() { for (index in 0 until size) storage[index] = 0 }
    fun release() { nativeHeap.free(storage) }
}
