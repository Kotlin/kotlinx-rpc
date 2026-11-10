/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

/** Continuation bytes at the edges of the `80..BF` range, and one byte on each side of it. */
internal val TAIL_BYTES: IntArray = intArrayOf(0x7F, 0x80, 0xBF, 0xC0)

/** A string with each Unicode scalar value one time, in code point order. */
internal fun allScalarValues(): String {
    val builder = StringBuilder()
    for (codePoint in 0..0x10FFFF) {
        if (codePoint in 0xD800..0xDFFF) {
            continue
        }
        if (codePoint < 0x10000) {
            builder.append(codePoint.toChar())
        } else {
            builder.append((0xD800 + ((codePoint - 0x10000) shr 10)).toChar())
            builder.append((0xDC00 + ((codePoint - 0x10000) and 0x3FF)).toChar())
        }
    }
    return builder.toString()
}

/**
 * Runs [action] for these inputs, as byte values from 0 to 255:
 *
 * - Each input of 1 or 2 bytes.
 * - 3 bytes: each lead byte `E0..EF` and each second byte, with each third byte of [TAIL_BYTES].
 * - 4 bytes: each lead byte `F0..F7` and each second byte, with each third and fourth byte of [TAIL_BYTES].
 */
internal inline fun forEachShortSequence(action: (IntArray) -> Unit) {
    for (b0 in 0..0xFF) {
        action(intArrayOf(b0))
        for (b1 in 0..0xFF) {
            action(intArrayOf(b0, b1))
            if (b0 in 0xE0..0xEF) {
                for (b2 in TAIL_BYTES) {
                    action(intArrayOf(b0, b1, b2))
                }
            }
            if (b0 in 0xF0..0xF7) {
                for (b2 in TAIL_BYTES) {
                    for (b3 in TAIL_BYTES) {
                        action(intArrayOf(b0, b1, b2, b3))
                    }
                }
            }
        }
    }
}
