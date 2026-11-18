/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

internal actual inline fun Utf8.readUtf8(
    window: ByteWindow,
    index: Int,
    count: Int,
    getCharBuffer: () -> CharArray,
): String {
    val chars = getCharBuffer()
    val charCount = Utf8.decode(window, index, count, chars)
    return chars.concatToString(0, charCount)
}
