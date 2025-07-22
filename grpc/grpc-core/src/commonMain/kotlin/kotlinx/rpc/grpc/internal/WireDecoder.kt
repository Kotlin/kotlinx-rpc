/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.io.Buffer

/**
 * A platform-specific decoder for wire format data.
 *
 * If one `read*()` method returns `null`, decoding the data failed and no further
 * decoding can be done.
 */
internal interface WireDecoder: AutoCloseable {
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

/**
 * Creates a platform-specific [WireDecoder].
 *
 * This constructor takes a [Buffer] instead of a [kotlinx.io.Source] because
 * the native implementation (`WireDecoderNative`) depends on [Buffer]'s internal structure.
 *
 * NOTE: Do not use the [source] buffer while the [WireDecoder] is still open.
 *
 * @param source The buffer containing the encoded wire-format data.
 */
internal expect fun WireDecoder(source: Buffer): WireDecoder