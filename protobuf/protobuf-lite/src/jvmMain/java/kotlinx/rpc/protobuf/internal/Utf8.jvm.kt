/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

internal actual fun Utf8.readUtf8(window: ByteWindow, index: Int, count: Int, chars: CharArray): String {
    val bytes = window.array
    val start = window.offset + index
    val result = String(bytes, start, count, Charsets.UTF_8)
    // The JDK replaces malformed UTF-8 with U+FFFD. Validate strings that contain it.
    if ('\uFFFD' in result) {
        Utf8.decode(bytes, start, count, chars)
    }
    return result
}
