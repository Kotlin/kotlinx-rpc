/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import io.grpc.CallOptions
import kotlinx.rpc.grpc.client.internal.GrpcCallOptions
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias GrpcCallOptions = CallOptions

@InternalRpcApi
public actual val GrpcDefaultCallOptions: GrpcCallOptions
    get() = GrpcCallOptions.DEFAULT
