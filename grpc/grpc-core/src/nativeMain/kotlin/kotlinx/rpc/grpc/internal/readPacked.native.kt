/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.pb.WireDecoder
import kotlinx.rpc.grpc.pb.WireDecoderNative
import libprotowire.pw_decoder_bytes_until_limit
import libprotowire.pw_decoder_pop_limit
import libprotowire.pw_decoder_push_limit

internal actual fun WireDecoder.pushLimit(byteLen: Int): Int {
    return pw_decoder_push_limit((this as WireDecoderNative).raw, byteLen)
}

internal actual fun WireDecoder.popLimit(limit: Int) {
    pw_decoder_pop_limit((this as WireDecoderNative).raw, limit)
}

internal actual fun WireDecoder.bytesUntilLimit(): Int {
    return pw_decoder_bytes_until_limit((this as WireDecoderNative).raw)
}
