/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

internal interface WireDecoder {
    fun readTag(): KTag?
    fun readBool(): Boolean?
    fun readInt32(): Int?
    fun readInt64(): Long?
    fun readUInt32(): UInt?
    fun readUInt64(): ULong?
    fun readSInt32(): Int?
    fun readSInt64(): Long?
    fun readFixed32(): UInt?
    fun readFixed64(): ULong?
    fun readSFixed32(): Int?
    fun readSFixed64(): Long?
    fun readEnum(): Int?
    fun readString(): String?
}