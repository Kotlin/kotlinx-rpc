/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.transport

import kotlinx.rpc.internal.IndexedEnum
import kotlinx.rpc.internal.InternalKRPCApi
import kotlinx.rpc.internal.ShortEnumKSerializer
import kotlinx.serialization.Serializable

/**
 * Keys for [RPCMessage.pluginParams] map.
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
@Suppress("detekt.MagicNumber")
public enum class RPCPluginKey(override val uniqueIndex: Int, private val associatedPlugin: RPCPlugin): IndexedEnum {
    /**
     * Failed to decode key, possible due to different endpoint versions.
     */
    UNKNOWN(0, RPCPlugin.UNKNOWN),

    GENERIC_MESSAGE_TYPE(1, RPCPlugin.HANDSHAKE),

    /**
     * Represents the type of resource that is being canceled.
     * Possible values: 'request', 'service', 'endpoint'.
     */
    CANCELLATION_TYPE(2, RPCPlugin.CANCELLATION),

    /**
     * Represents id of the resource that is being canceled.
     * It can be a service type name, service id, request call id or nothing (for endpoint cancellation).
     */
    CANCELLATION_ID(3, RPCPlugin.CANCELLATION),

    /**
     * Represents a service id that is unique to a current connection.
     */
    CLIENT_SERVICE_ID(4, RPCPlugin.CANCELLATION),
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
