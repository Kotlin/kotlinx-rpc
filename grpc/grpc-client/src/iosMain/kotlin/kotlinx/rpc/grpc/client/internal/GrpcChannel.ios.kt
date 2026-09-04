/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.coroutines.CoroutineContext

@InternalRpcApi
public actual abstract class GrpcChannel

@InternalRpcApi
public actual fun <RequestT, ResponseT> GrpcChannel.createCall(
    methodDescriptor: GrpcMethodDescriptor<RequestT, ResponseT>,
    callOptions: GrpcCallOptions,
    coroutineContext: CoroutineContext,
): ClientCall<RequestT, ResponseT> = TODO("Implement the iOS gRPC client")
