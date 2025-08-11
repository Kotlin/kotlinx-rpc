/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.grpc.pb.ProtobufDecodingException

internal expect fun WireDecoder.pushLimit(byteLen: Int): Int
internal expect fun WireDecoder.popLimit(limit: Int)
internal expect fun WireDecoder.bytesUntilLimit(): Int

internal inline fun <T : Any> WireDecoder.readPackedVarInternal(
    crossinline size: () -> Long,
    crossinline readFn: () -> T,
): List<T> {
    val byteLen = readInt32()
    if (byteLen < 0) {
        throw ProtobufDecodingException.negativeSize()
    }
    val size = size()
    // no size check on jvm
    if (size != -1L && size < byteLen) {
        throw ProtobufDecodingException.truncatedMessage()
    }
    if (byteLen == 0) {
        return emptyList() // actually an empty list (no error)
    }

    val limit = pushLimit(byteLen)

    val result = mutableListOf<T>()

    while (bytesUntilLimit() > 0) {
        val elem = readFn()
        result.add(elem)
    }

    popLimit(limit)
    return result
}
