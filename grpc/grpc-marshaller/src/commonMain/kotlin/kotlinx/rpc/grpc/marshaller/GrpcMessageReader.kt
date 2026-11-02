/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyMutableCollection
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyOpen

/**
 * A bounded view of one received gRPC message, supplied by a gRPC runtime.
 *
 * A reader is valid only while [GrpcMarshaller.decode] is running.
 */
@IntentionallyOpen(reason = ExemptionReason.API_DESIGN)
@ExperimentalRpcApi
public interface GrpcMessageReader {
    /** Total number of bytes in the message. */
    public val size: Int

    /** The number of bytes that may still be read. */
    public val remaining: Int

    /** Reads one byte. */
    public fun readByte(): Byte

    /** Reads exactly `endIndex - startIndex` bytes into [bytes]. */
    @IntentionallyMutableCollection(reason = ExemptionReason.FOR_BACKWARDS_COMPATIBILITY)
    public fun readTo(bytes: ByteArray, startIndex: Int = 0, endIndex: Int = bytes.size)

    /** Reads exactly [byteCount] bytes into a new array. */
    @IntentionallyMutableCollection(reason = ExemptionReason.FOR_BACKWARDS_COMPATIBILITY)
    public fun readByteArray(byteCount: Int = remaining): ByteArray

    /**
     * Returns a kotlinx-io view of the unread bytes.
     *
     * Buffer-backed readers share their buffer. Other implementations may copy the unread bytes.
     */
    public fun asSource(): Source
}
