/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.rpc.grpc.utils.BitSet
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public abstract class InternalMessage(fieldsWithPresence: Int) {
    public val presenceMask: BitSet = BitSet(fieldsWithPresence)
}