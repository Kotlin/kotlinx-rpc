/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual abstract class GrpcChannel {
    public abstract fun <RequestT, ResponseT> newCall(
        methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
        callOptions: GrpcCallOptions,
    ): ClientCall<RequestT, ResponseT>
}

public actual fun <RequestT, ResponseT> GrpcChannel.createCall(
    methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
    callOptions: GrpcCallOptions,
): ClientCall<RequestT, ResponseT> {
    return this.newCall(methodDescriptor, callOptions)
}