/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.io.Sink
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * A platform-specific class that encodes values into protobuf's wire format.
 *
 * If one `write*()` method returns false, the encoding of the value failed
 * and no further encodings can be performed on this [WireEncoder].
 *
 * [flush] must be called to ensure that all data is written to the [Sink].
 */
@InternalRpcApi
@OptIn(ExperimentalUnsignedTypes::class)
public interface WireEncoder {
    public fun flush()
    public fun writeBool(fieldNr: Int, value: Boolean): Boolean
    public fun writeInt32(fieldNr: Int, value: Int): Boolean
    public fun writeInt64(fieldNr: Int, value: Long): Boolean
    public fun writeUInt32(fieldNr: Int, value: UInt): Boolean
    public fun writeUInt64(fieldNr: Int, value: ULong): Boolean
    public fun writeSInt32(fieldNr: Int, value: Int): Boolean
    public fun writeSInt64(fieldNr: Int, value: Long): Boolean
    public fun writeFixed32(fieldNr: Int, value: UInt): Boolean
    public fun writeFixed64(fieldNr: Int, value: ULong): Boolean
    public fun writeSFixed32(fieldNr: Int, value: Int): Boolean
    public fun writeSFixed64(fieldNr: Int, value: Long): Boolean
    public fun writeFloat(fieldNr: Int, value: Float): Boolean
    public fun writeDouble(fieldNr: Int, value: Double): Boolean
    public fun writeEnum(fieldNr: Int, value: Int): Boolean
    public fun writeBytes(fieldNr: Int, value: ByteArray): Boolean
    public fun writeString(fieldNr: Int, value: String): Boolean
    public fun writePackedBool(fieldNr: Int, value: List<Boolean>, fieldSize: Int): Boolean
    public fun writePackedInt32(fieldNr: Int, value: List<Int>, fieldSize: Int): Boolean
    public fun writePackedInt64(fieldNr: Int, value: List<Long>, fieldSize: Int): Boolean
    public fun writePackedUInt32(fieldNr: Int, value: List<UInt>, fieldSize: Int): Boolean
    public fun writePackedUInt64(fieldNr: Int, value: List<ULong>, fieldSize: Int): Boolean
    public fun writePackedSInt32(fieldNr: Int, value: List<Int>, fieldSize: Int): Boolean
    public fun writePackedSInt64(fieldNr: Int, value: List<Long>, fieldSize: Int): Boolean
    public fun writePackedFixed32(fieldNr: Int, value: List<UInt>): Boolean
    public fun writePackedFixed64(fieldNr: Int, value: List<ULong>): Boolean
    public fun writePackedSFixed32(fieldNr: Int, value: List<Int>): Boolean
    public fun writePackedSFixed64(fieldNr: Int, value: List<Long>): Boolean
    public fun writePackedFloat(fieldNr: Int, value: List<Float>): Boolean
    public fun writePackedDouble(fieldNr: Int, value: List<Double>): Boolean
    public fun writePackedEnum(fieldNr: Int, value: List<Int>, fieldSize: Int): Boolean =
        writePackedInt32(fieldNr, value, fieldSize)

    public fun <T : InternalMessage> writeMessage(
        fieldNr: Int,
        value: T,
        encode: T.(WireEncoder) -> Unit
    )

}


internal expect fun WireEncoder(sink: Sink): WireEncoder