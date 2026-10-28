/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientCredentials
import kotlinx.rpc.grpc.client.GrpcTlsClientCredentials
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.coroutines.CoroutineContext

@InternalRpcApi
public actual fun <RequestT, ResponseT> ManagedChannel.createCall(
    methodDescriptor: GrpcMethodDescriptor<RequestT, ResponseT>,
    callOptions: GrpcCallOptions,
    coroutineContext: CoroutineContext,
): ClientCall<RequestT, ResponseT> {
    check(this is NativeManagedChannel) {
        internalError("Wrong channel type, expected NativeManagedChannel")
    }
    return newCall(methodDescriptor, callOptions, coroutineContext)
}

/**
 * Builder class for [ManagedChannel].
 */
@InternalRpcApi
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>> {
    internal var config: GrpcClientConfiguration? = null
}

internal class NativeManagedChannelBuilder(
    private val target: String,
    private val credentials: Lazy<GrpcClientCredentials>,
) : ManagedChannelBuilder<NativeManagedChannelBuilder>() {
    fun buildChannel(config: GrpcClientConfiguration): NativeManagedChannel {
        return NativeManagedChannel(
            target,
            overrideAuthority = config.overrideAuthority,
            keepAlive = config.keepAlive,
            userAgent = config.userAgent,
            maxInboundMessageSize = config.maxInboundMessageSize,
            maxInboundMetadataSize = config.maxInboundMetadataSize,
            idleTimeout = config.idleTimeout,
            clientCredentials = credentials.value,
        )
    }
}

internal actual fun ManagedChannelBuilder<*>.buildChannel(config: GrpcClientConfiguration): ManagedChannel {
    check(this is NativeManagedChannelBuilder) {
        internalError("Wrong builder type, expected NativeManagedChannelBuilder")
    }
    return buildChannel(config)
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    hostname: String,
    port: Int,
    credentials: GrpcClientCredentials?,
): ManagedChannelBuilder<*> {
    val credentials = if (credentials == null) { lazy { GrpcTlsClientCredentials() } } else { lazy { credentials } }
    return NativeManagedChannelBuilder(target = "$hostname:$port", credentials)
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(target: String, credentials: GrpcClientCredentials?): ManagedChannelBuilder<*> {
    val credentials = if (credentials == null) { lazy { GrpcTlsClientCredentials() } } else { lazy { credentials } }
    return NativeManagedChannelBuilder(target, credentials)
}
