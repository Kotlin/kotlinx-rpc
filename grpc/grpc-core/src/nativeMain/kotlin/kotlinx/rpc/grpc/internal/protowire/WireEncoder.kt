/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal.protowire

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.pin
import libprotowire.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

@ExperimentalForeignApi
@OptIn(ExperimentalNativeApi::class)
internal class WireEncoder {
    internal val buffer: Pinned<UByteArray> = UByteArray(1024).pin()
    internal val raw = pw_encoder_new(buffer.addressOf(0), 1024u)
        ?: error("Failed to create proto wire encoder")

    init {
        // free the encoder once garbage collector collects its pointer
        createCleaner(raw) {
            pw_encoder_delete(it)
        }
    }

    fun writeBool(field: Int, value: Boolean): Boolean {
        return pw_encoder_write_bool(raw, field, value)
    }

    fun writeInt32(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_int32(raw, fieldNr, value)
    }

    fun writeInt64(fieldNr: Int, value: Long): Boolean {
        return pw_encoder_write_int64(raw, fieldNr, value)
    }

    fun writeUInt32(fieldNr: Int, value: UInt): Boolean {
        return pw_encoder_write_uint32(raw, fieldNr, value)
    }

    fun writeUInt64(fieldNr: Int, value: ULong): Boolean {
        return pw_encoder_write_uint64(raw, fieldNr, value)
    }

    fun writeSInt32(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_sint32(raw, fieldNr, value)
    }

    fun writeSInt64(fieldNr: Int, value: Long): Boolean {
        return pw_encoder_write_sint64(raw, fieldNr, value)
    }

    fun writeFixed32(fieldNr: Int, value: UInt): Boolean {
        return pw_encoder_write_fixed32(raw, fieldNr, value)
    }

    fun writeFixed64(fieldNr: Int, value: ULong): Boolean {
        return pw_encoder_write_fixed64(raw, fieldNr, value)
    }

    fun writeSFixed32(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_sfixed32(raw, fieldNr, value)
    }

    fun writeSFixed64(fieldNr: Int, value: Long): Boolean {
        return pw_encoder_write_sfixed64(raw, fieldNr, value)
    }

    fun writeEnum(fieldNr: Int, value: Int): Boolean {
        return pw_encoder_write_enum(raw, fieldNr, value)
    }

    fun writeString(field: Int, value: String): Boolean {
        val str = pw_string_new(value) ?: error("Failed to create string")
        val result = pw_encoder_write_string(raw, field, str)
        pw_string_delete(str)
        return result;
    }

}