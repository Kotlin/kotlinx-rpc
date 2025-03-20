/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.utils.RpcInternalIndexedEnum
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.RpcInternalShortEnumKSerializer
import kotlinx.serialization.Serializable

/**
 * Keys for [KrpcMessage.pluginParams] map.
 *
 * [associatedPlugin] is a [KrpcPlugin] that introduces this key into the map.
 * One [KrpcPlugin] can introduce multiple keys.
 *
 * IMPORTANT: Enum [uniqueIndex] MUST NOT be changed once set! This will cause unexpected behavior.
 *
 * Only entries with ordinals from 0 to 65500 are allowed, other are reserved for tests.
 */
@InternalRpcApi
@Serializable(with = KrpcPluginKeySerializer::class)
public enum class KrpcPluginKey(
    override val uniqueIndex: Int,
    private val associatedPlugin: KrpcPlugin,
): RpcInternalIndexedEnum {
    /**
     * Failed to decode key, possible due to different endpoint versions.
     */
    UNKNOWN(0, KrpcPlugin.UNKNOWN),

    GENERIC_MESSAGE_TYPE(1, KrpcPlugin.HANDSHAKE),

    /**
     * Represents the type of resource that is being canceled.
     * Possible values: 'request', 'service', 'endpoint'.
     */
    CANCELLATION_TYPE(2, KrpcPlugin.CANCELLATION),

    /**
     * Represents id of the resource that is being canceled.
     * It can be a service type name, service id, request call id or nothing (for endpoint cancellation).
     */
    CANCELLATION_ID(3, KrpcPlugin.CANCELLATION),

    /**
     * Represents a service id that is unique to a current connection.
     */
    CLIENT_SERVICE_ID(4, KrpcPlugin.CANCELLATION),

    /**
     * Marks a request as a one doesn't suspend and returns a flow.
     */
    NON_SUSPENDING_SERVER_FLOW_MARKER(5, KrpcPlugin.NON_SUSPENDING_SERVER_FLOWS),
    ;

    init {
        require(ordinal == 0 || associatedPlugin != KrpcPlugin.UNKNOWN) {
            error("associatedPlugin must not be $KrpcPlugin.${KrpcPlugin.UNKNOWN} " +
                    "for anything other than $KrpcPluginKey.UNKNOWN")
        }
    }

    @InternalRpcApi
    public companion object {
        public val ALL: Set<KrpcPluginKey> = KrpcPluginKey.entries.toSet() - UNKNOWN
    }
}

private class KrpcPluginKeySerializer : RpcInternalShortEnumKSerializer<KrpcPluginKey>(
    kClass = KrpcPluginKey::class,
    unknownValue = KrpcPluginKey.UNKNOWN,
    allValues = KrpcPluginKey.ALL,
)
