/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.io.Sink
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * A platform-specific class that encodes values into protobuf's wire format.
 *
 * If one `write*()` method fails to encode the value in the buffer,
 * it will throw a platform-specific exception.
 *
 * Wrap the encoding of a message with [checkForPlatformEncodeException] to
 * turn all thrown platform-specific exceptions into [ProtobufEncodingException]s.
 *
 * [flush] must be called to ensure that all data is written to the [Sink].
 */
@InternalRpcApi
@OptIn(ExperimentalUnsignedTypes::class)
public interface WireEncoder {
    public fun flush()
    public fun writeBool(fieldNr: Int, value: Boolean)
    public fun writeInt32(fieldNr: Int, value: Int)
    public fun writeInt64(fieldNr: Int, value: Long)
    public fun writeUInt32(fieldNr: Int, value: UInt)
    public fun writeUInt64(fieldNr: Int, value: ULong)
    public fun writeSInt32(fieldNr: Int, value: Int)
    public fun writeSInt64(fieldNr: Int, value: Long)
    public fun writeFixed32(fieldNr: Int, value: UInt)
    public fun writeFixed64(fieldNr: Int, value: ULong)
    public fun writeSFixed32(fieldNr: Int, value: Int)
    public fun writeSFixed64(fieldNr: Int, value: Long)
    public fun writeFloat(fieldNr: Int, value: Float)
    public fun writeDouble(fieldNr: Int, value: Double)
    public fun writeEnum(fieldNr: Int, value: Int)
    public fun writeBytes(fieldNr: Int, value: ByteArray)
    public fun writeString(fieldNr: Int, value: String)
    public fun writePackedBool(fieldNr: Int, value: List<Boolean>, fieldSize: Int)
    public fun writePackedInt32(fieldNr: Int, value: List<Int>, fieldSize: Int)
    public fun writePackedInt64(fieldNr: Int, value: List<Long>, fieldSize: Int)
    public fun writePackedUInt32(fieldNr: Int, value: List<UInt>, fieldSize: Int)
    public fun writePackedUInt64(fieldNr: Int, value: List<ULong>, fieldSize: Int)
    public fun writePackedSInt32(fieldNr: Int, value: List<Int>, fieldSize: Int)
    public fun writePackedSInt64(fieldNr: Int, value: List<Long>, fieldSize: Int)
    public fun writePackedFixed32(fieldNr: Int, value: List<UInt>)
    public fun writePackedFixed64(fieldNr: Int, value: List<ULong>)
    public fun writePackedSFixed32(fieldNr: Int, value: List<Int>)
    public fun writePackedSFixed64(fieldNr: Int, value: List<Long>)
    public fun writePackedFloat(fieldNr: Int, value: List<Float>)
    public fun writePackedDouble(fieldNr: Int, value: List<Double>)
    public fun writePackedEnum(fieldNr: Int, value: List<Int>, fieldSize: Int): Unit =
        writePackedInt32(fieldNr, value, fieldSize)

    public fun <T : InternalMessage> writeMessage(
        fieldNr: Int,
        value: T,
        encode: T.(WireEncoder) -> Unit,
    )

}

/**
 * Turns exceptions thrown by different platforms during encoding into [ProtobufEncodingException].
 */
@InternalRpcApi
public expect inline fun checkForPlatformEncodeException(block: () -> Unit)

@InternalRpcApi
public expect fun WireEncoder(sink: Sink): WireEncoder