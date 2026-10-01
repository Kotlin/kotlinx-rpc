/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.io.Buffer
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.internal.utils.InternalRpcApi
import io.grpc.KnownLength
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
            return EncodedMessageInputStream(prepare(value), this@toMarshaller.javaClass.name)
        }

        override fun parse(stream: InputStream): T {
            val buffer = Buffer()
            if (stream is KnownLength) {
                val size = stream.available()
                require(size >= 0) { "The message size must be non-negative: $size" }
                val source = stream.asSource().buffered()
                while (buffer.size < size.toLong()) {
                    if (source.readAtMostTo(buffer, size - buffer.size) == -1L) break
                }
                check(buffer.size == size.toLong()) {
                    "The message stream ended after ${buffer.size} bytes, expected $size"
                }
            } else {
                stream.asSource().buffered().transferTo(buffer)
            }
            return decode(BufferMessageReader(buffer, buffer.size.toInt()))
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
