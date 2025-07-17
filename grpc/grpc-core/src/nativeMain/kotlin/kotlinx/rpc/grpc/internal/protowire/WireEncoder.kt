/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal.protowire

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.pin
import libprotowire.pw_encoder_delete
import libprotowire.pw_encoder_new
import libprotowire.pw_encoder_write_bool
import libprotowire.pw_encoder_write_string
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

    fun write(field: Int, value: Boolean): Boolean {
        return pw_encoder_write_bool(raw, field, value)
    }

    fun write(field: Int, value: String): Boolean {
        return pw_encoder_write_string(raw, field, value)
    }

}