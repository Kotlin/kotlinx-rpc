/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.io.Sink

/**
 * A platform-specific class that encodes values into protobuf's wire format.
 *
 * If one `write*()` method returns false, the encoding of the value failed
 * and no further encodings can be performed on this [WireEncoder].
 *
 * [flush] must be called to ensure that all data is written to the [Sink].
 */
@OptIn(ExperimentalUnsignedTypes::class)
internal interface WireEncoder {
    fun flush()
    fun writeBool(fieldNr: Int, value: Boolean): Boolean
    fun writeInt32(fieldNr: Int, value: Int): Boolean
    fun writeInt64(fieldNr: Int, value: Long): Boolean
    fun writeUInt32(fieldNr: Int, value: UInt): Boolean
    fun writeUInt64(fieldNr: Int, value: ULong): Boolean
    fun writeSInt32(fieldNr: Int, value: Int): Boolean
    fun writeSInt64(fieldNr: Int, value: Long): Boolean
    fun writeFixed32(fieldNr: Int, value: UInt): Boolean
    fun writeFixed64(fieldNr: Int, value: ULong): Boolean
    fun writeSFixed32(fieldNr: Int, value: Int): Boolean
    fun writeSFixed64(fieldNr: Int, value: Long): Boolean
    fun writeFloat(fieldNr: Int, value: Float): Boolean
    fun writeDouble(fieldNr: Int, value: Double): Boolean
    fun writeEnum(fieldNr: Int, value: Int): Boolean
    fun writeBytes(fieldNr: Int, value: ByteArray): Boolean
    fun writeString(fieldNr: Int, value: String): Boolean
    fun writePackedBool(fieldNr: Int, value: List<Boolean>, fieldSize: Int): Boolean
    fun writePackedInt32(fieldNr: Int, value: List<Int>, fieldSize: Int): Boolean
    fun writePackedInt64(fieldNr: Int, value: List<Long>, fieldSize: Int): Boolean
    fun writePackedUInt32(fieldNr: Int, value: List<UInt>, fieldSize: Int): Boolean
    fun writePackedUInt64(fieldNr: Int, value: List<ULong>, fieldSize: Int): Boolean
    fun writePackedSInt32(fieldNr: Int, value: List<Int>, fieldSize: Int): Boolean
    fun writePackedSInt64(fieldNr: Int, value: List<Long>, fieldSize: Int): Boolean
    fun writePackedFixed32(fieldNr: Int, value: List<UInt>): Boolean
    fun writePackedFixed64(fieldNr: Int, value: List<ULong>): Boolean
    fun writePackedSFixed32(fieldNr: Int, value: List<Int>): Boolean
    fun writePackedSFixed64(fieldNr: Int, value: List<Long>): Boolean
    fun writePackedFloat(fieldNr: Int, value: List<Float>): Boolean
    fun writePackedDouble(fieldNr: Int, value: List<Double>): Boolean
    fun writePackedEnum(fieldNr: Int, value: List<Int>, fieldSize: Int) =
        writePackedInt32(fieldNr, value, fieldSize)
}


internal expect fun WireEncoder(sink: Sink): WireEncoder
