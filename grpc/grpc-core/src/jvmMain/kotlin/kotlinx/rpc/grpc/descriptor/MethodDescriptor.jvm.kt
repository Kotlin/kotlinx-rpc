/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.io.InputStream

public actual typealias MethodDescriptor<Request, Response> = io.grpc.MethodDescriptor<Request, Response>

public actual val MethodDescriptor<*, *>.methodType: MethodType
    get() = when (this.type) {
        io.grpc.MethodDescriptor.MethodType.UNARY -> MethodType.UNARY
        io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING -> MethodType.CLIENT_STREAMING
        io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING -> MethodType.SERVER_STREAMING
        io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING -> MethodType.BIDI_STREAMING
        io.grpc.MethodDescriptor.MethodType.UNKNOWN -> MethodType.UNKNOWN
    }

internal val MethodType.asJvm: io.grpc.MethodDescriptor.MethodType
    get() = when (this) {
        MethodType.UNARY -> io.grpc.MethodDescriptor.MethodType.UNARY
        MethodType.CLIENT_STREAMING -> io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING
        MethodType.SERVER_STREAMING -> io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING
        MethodType.BIDI_STREAMING -> io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING
        MethodType.UNKNOWN -> io.grpc.MethodDescriptor.MethodType.UNKNOWN
    }

private fun <T> MessageCodec<T>.toJvm(): io.grpc.MethodDescriptor.Marshaller<T> {
    return object : io.grpc.MethodDescriptor.Marshaller<T> {
        override fun stream(value: T): InputStream {
            return encode(value)
        }

        override fun parse(stream: InputStream): T {
            return decode(stream)
        }
    }
}

@InternalRpcApi
public actual fun <Request, Response> methodDescriptor(
    fullMethodName: String,
    requestCodec: MessageCodec<Request>,
    responseCodec: MessageCodec<Response>,
    type: MethodType,
    schemaDescriptor: Any?,
    idempotent: Boolean,
    safe: Boolean,
    sampledToLocalTracing: Boolean,
): MethodDescriptor<Request, Response> {
    return MethodDescriptor.newBuilder<Request, Response>()
        .setFullMethodName(fullMethodName)
        .setRequestMarshaller(requestCodec.toJvm())
        .setResponseMarshaller(responseCodec.toJvm())
        .setType(type.asJvm)
        .setSchemaDescriptor(schemaDescriptor)
        .setIdempotent(idempotent)
        .setSafe(safe)
        .setSampledToLocalTracing(sampledToLocalTracing)
        .build()
}
