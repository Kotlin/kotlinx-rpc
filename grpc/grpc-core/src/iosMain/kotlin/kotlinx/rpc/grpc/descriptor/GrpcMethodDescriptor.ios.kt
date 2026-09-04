/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class GrpcMethodDescriptor<Request, Response> internal constructor(
    fullMethodName: String,
    public val requestMarshaller: GrpcMarshaller<Request>,
    public val responseMarshaller: GrpcMarshaller<Response>,
    public val methodType: GrpcMethodType,
    schemaDescriptor: Any?,
    idempotent: Boolean,
    safe: Boolean,
    sampledToLocalTracing: Boolean,
) {
    public actual fun getFullMethodName(): String = TODO("Implement iOS gRPC method descriptors")

    public actual fun getServiceName(): String? = TODO("Implement iOS gRPC method descriptors")

    public actual fun getSchemaDescriptor(): Any? = TODO("Implement iOS gRPC method descriptors")

    public actual fun isIdempotent(): Boolean = TODO("Implement iOS gRPC method descriptors")

    public actual fun isSafe(): Boolean = TODO("Implement iOS gRPC method descriptors")

    public actual fun isSampledToLocalTracing(): Boolean = TODO("Implement iOS gRPC method descriptors")

    public companion object {
        public fun extractFullServiceName(fullMethodName: String): String? =
            TODO("Implement iOS gRPC method descriptors")
    }
}

public actual val GrpcMethodDescriptor<*, *>.methodType: GrpcMethodType
    get() = TODO("Implement iOS gRPC method descriptors")

@InternalRpcApi
public actual fun <Request, Response> methodDescriptor(
    fullMethodName: String,
    requestMarshaller: GrpcMarshaller<Request>,
    responseMarshaller: GrpcMarshaller<Response>,
    type: GrpcMethodType,
    schemaDescriptor: Any?,
    idempotent: Boolean,
    safe: Boolean,
    sampledToLocalTracing: Boolean,
): GrpcMethodDescriptor<Request, Response> = TODO("Implement iOS gRPC method descriptors")
