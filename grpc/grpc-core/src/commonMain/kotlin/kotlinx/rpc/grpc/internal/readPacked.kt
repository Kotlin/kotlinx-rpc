/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.pb.WireDecoder

internal expect fun WireDecoder.pushLimit(byteLen: Int): Int
internal expect fun WireDecoder.popLimit(limit: Int)
internal expect fun WireDecoder.bytesUntilLimit(): Int

internal inline fun <T : Any> WireDecoder.readPackedVarInternal(
    crossinline size: () -> Long,
    crossinline readFn: () -> T,
    crossinline withError: () -> Unit,
    crossinline hadError: () -> Boolean,
): List<T> {
    val byteLen = readInt32()
    if (hadError()) {
        return emptyList()
    }
    if (byteLen < 0) {
        return emptyList<T>().apply { withError() }
    }
    val size = size()
    // no size check on jvm
    if (size != -1L && size < byteLen) {
        return emptyList<T>().apply { withError() }
    }
    if (byteLen == 0) {
        return emptyList() // actually an empty list (no error)
    }

    val limit = pushLimit(byteLen)

    val result = mutableListOf<T>()

    while (bytesUntilLimit() > 0) {
        val elem = readFn()
        if (hadError()) {
            break
        }
        result.add(elem)
    }

    popLimit(limit)
    return result
}
