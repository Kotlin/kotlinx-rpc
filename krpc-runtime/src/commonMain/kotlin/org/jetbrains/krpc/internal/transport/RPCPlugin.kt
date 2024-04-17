/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.transport

import kotlinx.serialization.Serializable
import org.jetbrains.krpc.internal.IndexedEnum
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.RPCVersion
import org.jetbrains.krpc.internal.ShortEnumKSerializer

/**
 * Represents the set of RPC plugins supported by the kRPC protocol.
 * These plugins provide meta-information that helps endpoints understand how to communicate with their peers.
 *
 * IMPORTANT: Enum [uniqueIndex] MUST NOT be changed once set! This will cause unexpected behavior.
 *
 * Only entries with ordinals from 0 to 65500 are allowed, others are reserved for tests.
 */
@InternalKRPCApi
@Serializable(with = RPCPluginSerializer::class)
public enum class RPCPlugin(
    override val uniqueIndex: Int,
    /**
     * Only for maintenance purposes. Indicates when the plugin was added.
     */
    @Suppress("unused") private val since: RPCVersion,
) : IndexedEnum {
    /**
     * Represents all unknown plugins.
     * Endpoint may get this value from a peer, when peer has a newer version and with it some new plugins
     * which this endpoint does not know about.
     *
     * Can be safely ignored. Endpoint must only handle the plugins it knows of.
     */
    UNKNOWN(0, RPCVersion.V_6_0_BETA),

    /**
     * Represents the handshake plugin of the kRPC protocol.
     *
     * Handshake allows for exchange of meta-information such as supported plugins.
     * If the endpoint does not support handshake,
     * that means that it is very outdated and should be updated to newer versions.
     * However, servers will be able to communicate with clients that do not support handshake,
     * BUT not the other way around.
     */
    HANDSHAKE(1, RPCVersion.V_6_0_BETA),

    /**
     * This feature adds support for proper service/request cancellation over the network.
     */
    CANCELLATION(2, RPCVersion.V_6_0_BETA),
    ;

    @InternalKRPCApi
    public companion object {
        /**
         * A set of all plugins for the current version of the library.
         */
        @Suppress("EnumValuesSoftDeprecate") // cannot use entries in Kotlin 1.8.10 or earlier
        public val ALL: Set<RPCPlugin> = RPCPlugin.values().toSet() - UNKNOWN
    }
}

private class RPCPluginSerializer : ShortEnumKSerializer<RPCPlugin>(
    kClass = RPCPlugin::class,
    unknownValue = RPCPlugin.UNKNOWN,
    allValues = RPCPlugin.ALL,
)
