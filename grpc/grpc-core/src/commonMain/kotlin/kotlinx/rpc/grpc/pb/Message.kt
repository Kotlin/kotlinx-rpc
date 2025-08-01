/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.rpc.grpc.internal.BitSet
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public abstract class Message(fieldsWithPresence: Int) {

    public val presenceMask: BitSet = BitSet(fieldsWithPresence)

    @InternalRpcApi
    public interface Companion<T : Message> {

        public fun decodeWith(decoder: WireDecoder): T

    }

    public abstract fun encodeWith(encoder: WireEncoder)
}