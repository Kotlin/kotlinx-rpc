/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

internal interface WireEncoder(sink: Sink) {
    fun writeBool(field: Int, value: Boolean): Boolean
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
    fun writeEnum(fieldNr: Int, value: Int): Boolean
    fun writeString(field: Int, value: String): Boolean
}