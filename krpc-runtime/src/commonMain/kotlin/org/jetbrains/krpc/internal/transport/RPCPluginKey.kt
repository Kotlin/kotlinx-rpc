/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.transport

import kotlinx.serialization.Serializable
import org.jetbrains.krpc.internal.IndexedEnum
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.ShortEnumKSerializer

/**
 * Keys for [RPCAnyMessage.pluginParams] map.
 *
 * [associatedPlugin] is a [RPCPlugin] that introduces this key into the map.
 * One [RPCPlugin] can introduce multiple keys.
 *
 * IMPORTANT: Enum [uniqueIndex] MUST NOT be changed once set! This will cause unexpected behavior.
 *
 * Only entries with ordinals from 0 to 65500 are allowed, other are reserved for tests.
 */
@InternalKRPCApi
@Serializable(with = RPCPluginKeySerializer::class)
public enum class RPCPluginKey(override val uniqueIndex: Int, private val associatedPlugin: RPCPlugin): IndexedEnum {
    /**
     * Failed to decode key, possible due to different endpoint versions.
     */
    UNKNOWN(0, RPCPlugin.UNKNOWN),
    ;

    init {
        require(ordinal == 0 || associatedPlugin != RPCPlugin.UNKNOWN) {
            error("associatedPlugin must not be $RPCPlugin.${RPCPlugin.UNKNOWN} " +
                    "for anything other than $RPCPluginKey.${UNKNOWN}")
        }
    }

    @InternalKRPCApi
    public companion object {
        @Suppress("EnumValuesSoftDeprecate") // cannot use entries in Kotlin 1.8.10 or earlier
        public val ALL: Set<RPCPluginKey> = RPCPluginKey.values().toSet() - UNKNOWN
    }
}

private class RPCPluginKeySerializer : ShortEnumKSerializer<RPCPluginKey>(
    kClass = RPCPluginKey::class,
    unknownValue = RPCPluginKey.UNKNOWN,
    allValues = RPCPluginKey.ALL,
)
