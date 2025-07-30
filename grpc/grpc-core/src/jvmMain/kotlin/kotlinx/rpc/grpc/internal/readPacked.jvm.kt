/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.pb.WireDecoder
import kotlinx.rpc.grpc.pb.WireDecoderJvm

internal actual fun WireDecoder.pushLimit(byteLen: Int): Int {
    return (this as WireDecoderJvm).codedInputStream.pushLimit(byteLen)
}

internal actual fun WireDecoder.popLimit(limit: Int) {
    (this as WireDecoderJvm).codedInputStream.popLimit(limit)
}

internal actual fun WireDecoder.bytesUntilLimit(): Int {
    return (this as WireDecoderJvm).codedInputStream.bytesUntilLimit
}
