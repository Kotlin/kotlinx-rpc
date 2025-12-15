/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.grpc.codec.MessageCodec

internal fun <M> M.encodeDecode(
    codec: MessageCodec<M>,
): M {
    val source = codec.encode(this)
    return codec.decode(source)
}