/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.benchmark

import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.decodeFromByteArray
import kotlinx.rpc.grpc.marshaller.decodeFromSource
import kotlinx.rpc.grpc.marshaller.encodeToBuffer
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf

/**
 * One message, its marshaller, and its encoded bytes.
 *
 * The functions are on the case, so that a benchmark can call them on a `BenchmarkCase<*>`.
 */
class BenchmarkCase<T : Any>(
    private val marshaller: GrpcMarshaller<T>,
    private val message: T,
) {
    val bytes: ByteArray = marshaller.encodeToByteArray(message)
    private val buffer: Buffer = Buffer().apply { write(bytes) }

    fun encodeToByteArray(): ByteArray = marshaller.encodeToByteArray(message)

    fun encodeToBuffer(): Buffer = marshaller.encodeToBuffer(message)

    fun prepare(): GrpcEncodedMessage = marshaller.prepare(message)

    fun decode(reader: GrpcMessageReader): Any? = marshaller.decode(reader)

    fun decodeFromByteArray(): Any? = marshaller.decodeFromByteArray(bytes)

    // Buffer.copy() shares the segments of the buffer, so the copy doesn't copy the bytes.
    fun decodeFromBuffer(): Any? = marshaller.decodeFromSource(buffer.copy())
}

/** The names of all cases, for the `@Param` of a benchmark. */
const val SCALARS = "scalars"
const val UNKNOWN_FIELDS = "unknownFields"
const val STRINGS = "strings"
const val BLOB = "blob"
const val PACKED = "packed"
const val TREE = "tree"
const val LARGE = "large"

fun benchmarkCase(name: String): BenchmarkCase<*> = when (name) {
    SCALARS -> BenchmarkCase(grpcMarshallerOf<Scalars>(), scalars(1))
    UNKNOWN_FIELDS -> unknownFieldsCase()
    STRINGS -> BenchmarkCase(grpcMarshallerOf<Strings>(), strings())
    BLOB -> BenchmarkCase(grpcMarshallerOf<Blob>(), Blob { payload = byteString(64 * 1024) })
    PACKED -> BenchmarkCase(grpcMarshallerOf<Packed>(), packed(1_000))
    TREE -> BenchmarkCase(grpcMarshallerOf<Tree>(), tree(depth = 5, width = 3))
    LARGE -> BenchmarkCase(grpcMarshallerOf<Large>(), large())
    else -> error("Unknown benchmark case: $name")
}

private fun unknownFieldsCase(): BenchmarkCase<ScalarsSubset> {
    val subset = grpcMarshallerOf<ScalarsSubset>()
    val withUnknownFields = subset.decodeFromByteArray(grpcMarshallerOf<Scalars>().encodeToByteArray(scalars(1)))
    return BenchmarkCase(subset, withUnknownFields)
}

fun scalars(seed: Int): Scalars = Scalars {
    smallInt = seed
    negativeInt = -seed - 1
    largeLong = Long.MAX_VALUE - seed
    unsignedInt = UInt.MAX_VALUE - seed.toUInt()
    unsignedLong = ULong.MAX_VALUE - seed.toULong()
    zigzagInt = -1_000_000 - seed
    zigzagLong = Long.MIN_VALUE + seed
    fixedInt = 0xCAFE_BABEu + seed.toUInt()
    fixedLong = 0xDEAD_BEEF_CAFE_BABEuL + seed.toULong()
    signedFixedInt = Int.MIN_VALUE + seed
    signedFixedLong = -seed.toLong()
    ratio = seed / 3.0f
    amount = seed * 1.5e10
    flag = seed % 2 == 0
}

private fun strings(): Strings = Strings {
    ascii = List(100) { index -> "ascii-string-number-$index-".padEnd(32, 'x') }
    unicode = List(100) { index -> "Grüße, 你好, привет, 🙂 #$index" }
}

private fun packed(count: Int): Packed = Packed {
    varints = List(count) { index -> index * 1_000 }
    zigzags = List(count) { index -> -index * 1_000_000L }
    fixeds = List(count) { index -> index.toUInt() * 7u }
    doubles = List(count) { index -> index * 0.25 }
}

private fun tree(depth: Int, width: Int): Tree = Tree {
    name = "node-$depth"
    this.depth = depth
    children = if (depth == 0) {
        emptyList()
    } else {
        List(width) { tree(depth - 1, width) }
    }
}

// About 1 MiB, so that the encoded message has many buffer segments.
private fun large(): Large = Large {
    rows = List(10_000) { index -> scalars(index) }
    names = List(1_000) { index -> "name-$index" }
    blob = byteString(256 * 1024)
}

private fun byteString(size: Int): ByteString = ByteString(ByteArray(size) { index -> (index * 31).toByte() })
