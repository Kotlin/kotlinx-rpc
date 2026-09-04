/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientCredentials
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual abstract class ManagedChannelPlatform : GrpcChannel()

@InternalRpcApi
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>>

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    hostname: String,
    port: Int,
    credentials: GrpcClientCredentials?,
): ManagedChannelBuilder<*> = TODO("Implement the iOS gRPC client")

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    target: String,
    credentials: GrpcClientCredentials?,
): ManagedChannelBuilder<*> = TODO("Implement the iOS gRPC client")

internal actual fun ManagedChannelBuilder<*>.applyConfig(
    config: GrpcClientConfiguration,
): ManagedChannelBuilder<*> = TODO("Implement the iOS gRPC client")

@InternalRpcApi
public actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel =
    TODO("Implement the iOS gRPC client")
