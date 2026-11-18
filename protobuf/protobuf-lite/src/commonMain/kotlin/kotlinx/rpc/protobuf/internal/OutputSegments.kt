/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.protobuf.ProtobufEncodingException

/** The destination of a [WireOutput] as a sequence of segments. */
internal interface OutputSegments {
    /**
     * Attaches the next free segment to [window] and returns its capacity, which is more than 0.
     *
     * @throws ProtobufEncodingException if the destination has no space left.
     */
    fun next(window: ByteWindow): Int

    /** Gives the first [count] bytes of the attached segment to the destination. A count of 0 is valid at any time. */
    fun commit(count: Int)
}
