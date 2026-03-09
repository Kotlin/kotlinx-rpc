/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.internal.utils.InternalRpcApi

public expect class GrpcMethodDescriptor<Request, Response> {
    public fun getFullMethodName(): String
    public fun getServiceName(): String?
    public fun getSchemaDescriptor(): Any?
    public fun isIdempotent(): Boolean
    public fun isSafe(): Boolean
    public fun isSampledToLocalTracing(): Boolean
}

public expect val GrpcMethodDescriptor<*, *>.methodType: GrpcMethodType

public enum class GrpcMethodType {
    UNARY,
    CLIENT_STREAMING,
    SERVER_STREAMING,
    BIDI_STREAMING,
    UNKNOWN,
}

/**
 * Creates a new [GrpcMethodDescriptor] instance.
 *
 * @param fullMethodName the full name of the method, consisting of the service name followed by a forward slash
 *      and the method name. It does not include a leading slash.
 */
@InternalRpcApi
public expect fun <Request, Response> methodDescriptor(
    fullMethodName: String,
    requestMarshaller: GrpcMarshaller<Request>,
    responseMarshaller: GrpcMarshaller<Response>,
    type: GrpcMethodType,
    schemaDescriptor: Any?,
    idempotent: Boolean,
    safe: Boolean,
    sampledToLocalTracing: Boolean,
): GrpcMethodDescriptor<Request, Response>
