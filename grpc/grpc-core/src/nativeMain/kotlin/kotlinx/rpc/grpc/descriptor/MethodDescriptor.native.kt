/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.input.stream.InputStream

public actual class MethodDescriptor<Request, Response> internal constructor(
    private val fullMethodName: String,
    private val requestMarshaller: Marshaller<Request>,
    private val responseMarshaller: Marshaller<Response>,
    internal val type: MethodType,
    private val schemaDescriptor: Any?,
    private val idempotent: Boolean,
    private val safe: Boolean,
    private val sampledToLocalTracing: Boolean,
) {
    public actual fun getFullMethodName(): String = fullMethodName

    private val serviceName: String? by lazy {
        extractFullServiceName(fullMethodName)
    }

    public actual fun getServiceName(): String? = serviceName

    public actual fun getRequestMarshaller(): Marshaller<Request> = requestMarshaller

    public actual fun getResponseMarshaller(): Marshaller<Response> = responseMarshaller

    public actual fun getSchemaDescriptor(): Any? = schemaDescriptor

    public actual fun isIdempotent(): Boolean = idempotent

    public actual fun isSafe(): Boolean = safe

    public actual fun isSampledToLocalTracing(): Boolean = sampledToLocalTracing

    public actual interface Marshaller<T> {
        public actual fun stream(value: T): InputStream
        public actual fun parse(stream: InputStream): T
    }

    public companion object {
        public fun extractFullServiceName(fullMethodName: String): String? {
            val index = fullMethodName.lastIndexOf('/')
            return if (index == -1) {
                null
            } else {
                fullMethodName.take(index)
            }
        }
    }
}

public actual val MethodDescriptor<*, *>.methodType: MethodType
    get() = this.type

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
    val requestMarshaller = object : MethodDescriptor.Marshaller<Request> {
        override fun stream(value: Request): InputStream {
            return requestCodec.encode(value)
        }

        override fun parse(stream: InputStream): Request {
            return requestCodec.decode(stream)
        }
    }

    val responseMarshaller = object : MethodDescriptor.Marshaller<Response> {
        override fun stream(value: Response): InputStream {
            return responseCodec.encode(value)
        }

        override fun parse(stream: InputStream): Response {
            return responseCodec.decode(stream)
        }
    }

    return MethodDescriptor(
        fullMethodName = fullMethodName,
        requestMarshaller = requestMarshaller,
        responseMarshaller = responseMarshaller,
        type = type,
        schemaDescriptor = schemaDescriptor,
        idempotent = idempotent,
        safe = safe,
        sampledToLocalTracing = sampledToLocalTracing,
    )
}
