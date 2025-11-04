/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import io.grpc.Channel
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.toJvm
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias GrpcChannel = Channel

@InternalRpcApi
public actual fun <RequestT, ResponseT> GrpcChannel.createCall(
    methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
    callOptions: GrpcCallOptions,
): ClientCall<RequestT, ResponseT> {
    return this.newCall(methodDescriptor, callOptions.toJvm())
}