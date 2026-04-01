/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, InternalNativeProtobufApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.protobuf.internal.cinterop.pw_decoder_bytes_until_limit
import kotlinx.rpc.protobuf.internal.cinterop.pw_decoder_pop_limit
import kotlinx.rpc.protobuf.internal.cinterop.pw_decoder_push_limit
import kotlinx.rpc.protobuf.internal.shim.InternalNativeProtobufApi


internal actual fun WireDecoder.pushLimit(byteLen: Int): Int {
    return pw_decoder_push_limit((this as WireDecoderNative).raw, byteLen)
}

internal actual fun WireDecoder.popLimit(limit: Int) {
    pw_decoder_pop_limit((this as WireDecoderNative).raw, limit)
}

internal actual fun WireDecoder.bytesUntilLimit(): Int {
    return pw_decoder_bytes_until_limit((this as WireDecoderNative).raw)
}
