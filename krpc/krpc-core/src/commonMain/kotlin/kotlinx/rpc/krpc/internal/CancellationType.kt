/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.utils.RpcInternalIndexedEnum
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
@Suppress("detekt.MagicNumber")
public enum class CancellationType(override val uniqueIndex: Int) : RpcInternalIndexedEnum {
    ENDPOINT(0),
    @Deprecated("Service cancellation is deprecated.")
    SERVICE(1),
    REQUEST(2),
    @Deprecated("Cancellation acknowledgement is deprecated.")
    CANCELLATION_ACK(3),
    ;

    internal companion object {
        fun valueOfNull(value: String): CancellationType? {
            return entries.find { it.name == value }
        }
    }
}

@InternalRpcApi
public fun KrpcMessage.cancellationType(): CancellationType? {
    return get(KrpcPluginKey.CANCELLATION_TYPE)?.let { value ->
        CancellationType.valueOfNull(value)
    }
}
