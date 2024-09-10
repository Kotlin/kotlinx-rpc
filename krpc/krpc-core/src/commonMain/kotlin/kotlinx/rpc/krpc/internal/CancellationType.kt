/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.IndexedEnum
import kotlinx.rpc.internal.InternalRPCApi

@InternalRPCApi
@Suppress("detekt.MagicNumber")
public enum class CancellationType(override val uniqueIndex: Int) : IndexedEnum {
    ENDPOINT(0),
    SERVICE(1),
    REQUEST(2),
    CANCELLATION_ACK(3),
    ;

    internal companion object {
        @Suppress("EnumValuesSoftDeprecate") // cannot use entries in Kotlin 1.8.10 or earlier
        private val values = values()

        fun valueOfNull(value: String): CancellationType? {
            return values.find { it.name == value }
        }
    }
}

@InternalRPCApi
public fun RPCMessage.cancellationType(): CancellationType? {
    return get(RPCPluginKey.CANCELLATION_TYPE)?.let { value ->
        CancellationType.valueOfNull(value)
    }
}
