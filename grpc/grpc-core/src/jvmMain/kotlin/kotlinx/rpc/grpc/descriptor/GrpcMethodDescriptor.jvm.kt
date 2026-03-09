/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.io.asInputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.io.InputStream

public actual typealias GrpcMethodDescriptor<Request, Response> = io.grpc.MethodDescriptor<Request, Response>

public actual val GrpcMethodDescriptor<*, *>.methodType: GrpcMethodType
    get() = when (this.type) {
        io.grpc.MethodDescriptor.MethodType.UNARY -> GrpcMethodType.UNARY
        io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING -> GrpcMethodType.CLIENT_STREAMING
        io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING -> GrpcMethodType.SERVER_STREAMING
        io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING -> GrpcMethodType.BIDI_STREAMING
        io.grpc.MethodDescriptor.MethodType.UNKNOWN -> GrpcMethodType.UNKNOWN
    }

internal val GrpcMethodType.asJvm: io.grpc.MethodDescriptor.MethodType
    get() = when (this) {
        GrpcMethodType.UNARY -> io.grpc.MethodDescriptor.MethodType.UNARY
        GrpcMethodType.CLIENT_STREAMING -> io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING
        GrpcMethodType.SERVER_STREAMING -> io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING
        GrpcMethodType.BIDI_STREAMING -> io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING
        GrpcMethodType.UNKNOWN -> io.grpc.MethodDescriptor.MethodType.UNKNOWN
    }

private fun <T> GrpcMarshaller<T>.toMarshaller(): io.grpc.MethodDescriptor.Marshaller<T> {
    return object : io.grpc.MethodDescriptor.Marshaller<T> {
        override fun stream(value: T): InputStream {
            // wraps the source in a stream
            return encode(value).asInputStream()
        }

        override fun parse(stream: InputStream): T {
            // wraps the stream in a buffered source
            return decode(stream.asSource().buffered())
        }
    }
}

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
): GrpcMethodDescriptor<Request, Response> {
    return GrpcMethodDescriptor.newBuilder<Request, Response>()
        .setFullMethodName(fullMethodName)
        .setRequestMarshaller(requestMarshaller.toMarshaller())
        .setResponseMarshaller(responseMarshaller.toMarshaller())
        .setType(type.asJvm)
        .setSchemaDescriptor(schemaDescriptor)
        .setIdempotent(idempotent)
        .setSafe(safe)
        .setSampledToLocalTracing(sampledToLocalTracing)
        .build()
}
