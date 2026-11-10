/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

/**
 * The input of a [WireInput] as a sequence of segments.
 *
 * Implementations skip empty segments, so a segment is never empty.
 */
internal interface InputSegments {
    /** Attaches the next segment to [window] and returns its length, or returns -1 when no segment remains. */
    fun next(window: ByteWindow): Int
}
