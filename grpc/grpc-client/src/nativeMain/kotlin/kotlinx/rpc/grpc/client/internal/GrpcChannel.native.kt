/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.client.internal.ClientCall
import kotlinx.rpc.grpc.client.internal.GrpcCallOptions
import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual abstract class GrpcChannel {
    public actual abstract fun <RequestT, ResponseT> newCall(
        methodDescriptor: MethodDescriptor<RequestT, ResponseT>,
        callOptions: GrpcCallOptions,
    ): ClientCall<RequestT, ResponseT>

    public actual abstract fun authority(): String?
}
