/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.protobuf.test

import OneOfMsg
import invoke
import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.decodeFromSource
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageWriter
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.withWireEncoder
import test.groups.WithGroups
import test.groups.invoke
import test.nested.NestedOuter
import test.nested.invoke
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class DirectWireEncoderTest {
    private fun <M : Any> compare(message: M, marshaller: GrpcMarshaller<M>) {
        val prepared = marshaller.prepare(message)
        val buffer = Buffer()
        prepared.writeTo(BufferMessageWriter(buffer, prepared.size))
        val expected = buffer.readByteArray()

        val direct = HeapMessageWriter(expected.size)
        try {
            marshaller.prepare(message).writeTo(direct)
            assertEquals(0, direct.remaining)
            assertContentEquals(expected, direct.snapshot())
            assertEquals(if (expected.isEmpty()) 0 else 1, direct.scopes)
        } finally {
            direct.release()
        }
    }

    @Test
    fun generatedMessagesMatchBufferEncoding() {
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
            string = "direct encoding 世界"
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
        compare(OneOfMsg { sint = -23 }, grpcMarshallerOf<OneOfMsg>())
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
        val unknown = grpcMarshallerOf<UnknownFieldsSubset>().decodeFromSource(
            grpcMarshallerOf<UnknownFieldsAll>().let { marshaller ->
                val message = UnknownFieldsAll { field1 = 1; intMissing = 99 }
                val prepared = marshaller.prepare(message)
                Buffer().also { prepared.writeTo(BufferMessageWriter(it, prepared.size)) }
            },
        )
        compare(unknown, grpcMarshallerOf<UnknownFieldsSubset>())
        compare(AllPrimitives { bytes = ByteArray(1024 * 1024) { it.toByte() }.asByteString() },
            grpcMarshallerOf<AllPrimitives>())
    }

    // Every generated message in tests:test-protos is checked at its default wire state.
    // The populated cases above exercise the field shapes. Two messages have required fields.
    @Test
    fun everyTestProtoMessageMatchesBufferEncoding() {
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
        compare(marshaller.decodeFromSource(Buffer()), marshaller)
    }

    private fun <M : Any> compareRequiredString(marshaller: GrpcMarshaller<M>, value: String) {
        val wire = Buffer()
        WireEncoder(wire).apply { writeString(1, value); flush() }
        compare(marshaller.decodeFromSource(wire), marshaller)
    }

    @Test
    fun rejectsTooSmallStorageWithoutOverflow() {
        val writer = HeapMessageWriter(2)
        try {
            assertFails { withWireEncoder(writer) { it.writeBytes(1, ByteArray(1024)) } }
            assertFailsWith<IllegalStateException> { writer.writeByte(0) }
        } finally {
            writer.release()
        }
    }

    @Test
    fun rejectsTooLargeStorage() {
        val writer = HeapMessageWriter(8)
        try {
            assertFailsWith<IllegalStateException> { withWireEncoder(writer) { it.writeBool(1, true) } }
            assertFailsWith<IllegalStateException> { writer.writeByte(0) }
        } finally {
            writer.release()
        }
    }

    @Test
    fun underfilledEncodingCannotBeCompletedAfterFailure() {
        val writer = HeapMessageWriter(8)
        try {
            assertFailsWith<IllegalStateException> { withWireEncoder(writer) { it.writeBool(1, true) } }
            assertFailsWith<IllegalStateException> { writer.write(ByteArray(6)) }
        } finally {
            writer.release()
        }
    }

    @Test
    fun failedEmptyEncodingPropagatesFailure() {
        val writer = HeapMessageWriter(0)
        try {
            assertFailsWith<IllegalArgumentException> {
                withWireEncoder(writer) { throw IllegalArgumentException("encode failed") }
            }
        } finally {
            writer.release()
        }
    }

    @Test
    fun encoderCannotWriteAfterScope() {
        val writer = HeapMessageWriter(2)
        var escaped: WireEncoder? = null
        try {
            withWireEncoder(writer) { encoder ->
                escaped = encoder
                encoder.writeBool(1, true)
            }
            assertEquals(0, writer.remaining)
            assertFailsWith<IllegalStateException> { escaped!!.writeBool(2, true) }
            assertFailsWith<IllegalStateException> { escaped!!.flush() }
            assertFailsWith<IllegalStateException> { escaped!!.writeRawBytes(byteArrayOf(), 0, 0) }
            assertFailsWith<IllegalStateException> { escaped!!.writeRawBytes(Buffer()) }
        } finally {
            writer.release()
        }
    }

    @Test
    fun blockFailureClosesEncoderAndFailsWriter() {
        val writer = HeapMessageWriter(2)
        var escaped: WireEncoder? = null
        val failure = IllegalArgumentException("encode failed")
        try {
            val caught = assertFailsWith<IllegalArgumentException> {
                withWireEncoder(writer) { encoder ->
                    escaped = encoder
                    throw failure
                }
            }
            assertEquals(failure, caught)
            assertFailsWith<IllegalStateException> { escaped!!.writeBool(1, true) }
            assertFailsWith<IllegalStateException> { writer.writeByte(0) }
        } finally {
            writer.release()
        }
    }
}

private class HeapMessageWriter(size: Int) : NativeBufferMessageWriter(size) {
    private val allocation = nativeHeap.allocArray<ByteVar>(size + 2)
    private var released = false
    var scopes = 0
        private set

    init {
        allocation[0] = 0x5a.toByte()
        allocation[size + 1] = 0x5a.toByte()
    }

    override fun accessBuffer(block: (CPointer<ByteVar>?) -> Unit) {
        scopes++
        block(allocation + 1)
    }

    fun release() {
        if (!released) {
            assertGuardsIntact()
            released = true
            nativeHeap.free(allocation)
        }
    }

    fun snapshot(): ByteArray = ByteArray(size) { allocation[it + 1] }

    fun assertGuardsIntact() {
        assertEquals(0x5a.toByte(), allocation[0])
        assertEquals(0x5a.toByte(), allocation[size + 1])
    }
}
