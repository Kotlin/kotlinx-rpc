/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.io.Source
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.input.stream.InputStream

@InternalRpcApi
internal actual val MethodDescriptor<*, *>.type: MethodType
    get() = TODO("Not yet implemented")

@InternalRpcApi
public actual class MethodDescriptor<Request, Response> internal constructor(
    private val fullMethodName: String,
    private val requestMarshaller: Marshaller<Request>,
    private val responseMarshaller: Marshaller<Response>,
    internal val methodType: MethodType,
    private val schemaDescriptor: Any?,
    private val idempotent: Boolean,
    private val safe: Boolean,
    private val sampledToLocalTracing: Boolean,
) {
    public actual fun getFullMethodName(): String = fullMethodName

    private val serviceName: String? by lazy {
        val index = fullMethodName.lastIndexOf('/')
        if (index == -1) {
            null
        } else {
            fullMethodName.substring(0, index)
        }
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
}

@InternalRpcApi
internal actual val MethodDescriptor<*, *>.type: MethodType
    get() = this.methodType

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
            val source = requestCodec.encode(value)
            return object : InputStream(source) {}
        }

        override fun parse(stream: InputStream): Request {
            return requestCodec.decode(stream.source)
        }
    }

    val responseMarshaller = object : MethodDescriptor.Marshaller<Response> {
        override fun stream(value: Response): InputStream {
            val source = responseCodec.encode(value)
            return object : InputStream(source) {}
        }

        override fun parse(stream: InputStream): Response {
            return responseCodec.decode(stream.source)
        }
    }

    return MethodDescriptor(
        fullMethodName = fullMethodName,
        requestMarshaller = requestMarshaller,
        responseMarshaller = responseMarshaller,
        methodType = type,
        schemaDescriptor = schemaDescriptor,
        idempotent = idempotent,
        safe = safe,
        sampledToLocalTracing = sampledToLocalTracing,
    )
}
