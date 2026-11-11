/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.decodeFromByteArray
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.grpc.marshaller.internal.AbstractGrpcMessageReader
import kotlinx.rpc.grpc.marshaller.internal.AbstractGrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageWriter
import test.groups.WithGroups
import test.groups.invoke
import test.nested.NestedOuter
import test.nested.invoke
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CodecIoMatrixTest {
    @Test
    fun encodesToEachWriterKind() {
        for (case in codecCases()) {
            val array = ByteArrayMessageWriter(ByteArray(case.bytes.size))
            case.checkEncode(array, "ByteArrayMessageWriter") { array.array }

            val buffer = BufferMessageWriter(Buffer(), case.bytes.size)
            case.checkEncode(buffer, "BufferMessageWriter") { buffer.buffer.readByteArray() }

            val thirdParty = ThirdPartyWriter(case.bytes.size)
            case.checkEncode(thirdParty, "ThirdPartyWriter") { thirdParty.bytes() }
        }
    }

    @Test
    fun decodesFromEachReaderKind() {
        for (case in codecCases()) {
            for (kind in readerKinds) {
                case.checkDecode(kind.create(case.bytes), kind.name)
            }
        }
    }

    @Test
    fun unknownFieldsStayAfterDecoding() {
        val all = grpcMarshallerOf<UnknownFieldsAll>().encodeToByteArray(unknownFieldsAll())
        val subset = codecCases().single { it.message is UnknownFieldsSubset }
        assertContentEquals(all, subset.bytes)
    }
}

/** A message and its marshaller. [bytes] is the encoding of [message] through a [BufferMessageWriter]. */
internal class CodecCase<M : Any>(
    private val marshaller: GrpcMarshaller<M>,
    val message: M,
) {
    val bytes: ByteArray = marshaller.encodeToByteArray(message)

    /** Encodes [message] to [writer] and checks that [written] gives [bytes]. */
    fun checkEncode(writer: GrpcMessageWriter, kind: String, written: () -> ByteArray) {
        marshaller.prepare(message).writeTo(writer)
        assertEquals(0, writer.remaining, "$kind, $message")
        assertContentEquals(bytes, written(), "$kind, $message")
    }

    /** Decodes from [reader], checks the result, and checks that the result encodes to [bytes]. */
    fun checkDecode(reader: GrpcMessageReader, kind: String) {
        val decoded = marshaller.decode(reader)
        assertEquals(message, decoded, kind)
        assertEquals(0, reader.remaining, "$kind, $message")
        assertContentEquals(bytes, marshaller.encodeToByteArray(decoded), "$kind, $message")
    }
}

/** A reader type, and how to make a reader of that type over some bytes. */
internal class ReaderKind(val name: String, val create: (ByteArray) -> GrpcMessageReader)

/** Each reader type that the common codec handles in a different way. */
internal val readerKinds: List<ReaderKind> = buildList {
    add(ReaderKind("ByteArrayMessageReader") { ByteArrayMessageReader(it) })
    add(ReaderKind("BufferMessageReader") { BufferMessageReader(Buffer().apply { write(it) }, it.size) })
    for (chunkSize in listOf(1, 3, 7, 1000)) {
        add(ReaderKind("BufferMessageReader, segments of $chunkSize bytes") {
            BufferMessageReader(bufferOf(it, chunkSize), it.size)
        })
    }
    add(ReaderKind("ThirdPartyReader") { ThirdPartyReader(it) })
}

/** The messages that the codec tests encode and decode with each reader and writer type. */
internal fun codecCases(): List<CodecCase<*>> = listOf(
    CodecCase(grpcMarshallerOf<AllPrimitives>(), AllPrimitives {
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
        string = "codec ü 世界 🙂"
        bytes = byteArrayOf(0, 1, -1).asByteString()
        requiredString = "required"
        requiredBytes = byteArrayOf(7).asByteString()
    }),
    CodecCase(grpcMarshallerOf<AllPrimitives>(), AllPrimitives {
        bytes = ByteArray(1024 * 1024) { it.toByte() }.asByteString()
    }),
    CodecCase(grpcMarshallerOf<Repeated>(), Repeated {
        listInt32 = listOf(-1, 0, 127, Int.MAX_VALUE)
        listInt32Packed = listOf(-1, 0, 128)
        listFixed32 = listOf(1u, UInt.MAX_VALUE)
        listFixed32Packed = listOf(1u, UInt.MAX_VALUE)
        listString = listOf("one", "two")
        listMessage = listOf(Repeated.Other { a = 3 }, Repeated.Other { a = -4 })
    }),
    CodecCase(grpcMarshallerOf<TestMap>(), TestMap {
        primitives = mapOf("one" to 1, "two" to 2)
        messages = mapOf(1 to PresenceCheck { requiredPresence = 7 })
    }),
    CodecCase(grpcMarshallerOf<NestedOuter>(), NestedOuter {
        deep = NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner { num = 42 }
    }),
    CodecCase(grpcMarshallerOf<WithGroups>(), WithGroups {
        firstgroup = WithGroups.FirstGroup { value = 23u }
        secondgroup = listOf(WithGroups.SecondGroup { value = "second" })
    }),
    CodecCase(
        grpcMarshallerOf<UnknownFieldsSubset>(),
        grpcMarshallerOf<UnknownFieldsSubset>().decodeFromByteArray(
            grpcMarshallerOf<UnknownFieldsAll>().encodeToByteArray(unknownFieldsAll()),
        ),
    ),
)

/** A message with fields of each wire type that [UnknownFieldsSubset] doesn't have. */
private fun unknownFieldsAll(): UnknownFieldsAll = UnknownFieldsAll {
    field1 = 1
    intMissing = 99
    sint64Missing = -7
    fixed32Missing = 7u
    fixed64Missing = 8uL
    stringMissing = "ü"
    bytesMissing = byteArrayOf(1, 2, 3).asByteString()
    repeatedIntMissing = listOf(1, -2, 300)
    allPrimitivesMissing = AllPrimitives { int32 = 5 }
}

/** A reader that isn't a reader type of the gRPC runtime, so the codec reads it through its public API. */
internal class ThirdPartyReader(private val bytes: ByteArray) : AbstractGrpcMessageReader(bytes.size) {
    private var position = 0

    override fun readByte(): Byte = recordRead(1) { bytes[position++] }

    override fun readTo(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val count = endIndex - startIndex
        recordRead(count) {
            this.bytes.copyInto(bytes, startIndex, position, position + count)
            position += count
        }
    }

    override fun asSource(): Source = recordRead(remaining) {
        Buffer().apply {
            write(bytes, position, bytes.size)
            position = bytes.size
        }
    }
}

/** A writer that isn't a writer type of the gRPC runtime, so the codec writes to it through its public API. */
internal class ThirdPartyWriter(size: Int) : AbstractGrpcMessageWriter(size) {
    private val storage = ByteArray(size)

    override fun writeByte(byte: Byte) {
        recordWrite(1) { storage[written] = byte }
    }

    override fun write(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val count = checkedWriteRangeSize(bytes, startIndex, endIndex)
        recordWrite(count) { bytes.copyInto(storage, written, startIndex, endIndex) }
    }

    /** The bytes written so far. */
    fun bytes(): ByteArray = storage.copyOf(written)
}
