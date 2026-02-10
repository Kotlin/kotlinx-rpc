/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.io.Source
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class MethodDescriptor<Request, Response> internal constructor(
    private val fullMethodName: String,
    @InternalRpcApi public val requestMarshaller: Marshaller<Request>,
    @InternalRpcApi public val responseMarshaller: Marshaller<Response>,
    @InternalRpcApi public val methodType: MethodType,
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

    public actual fun getSchemaDescriptor(): Any? = schemaDescriptor

    public actual fun isIdempotent(): Boolean = idempotent

    public actual fun isSafe(): Boolean = safe

    public actual fun isSampledToLocalTracing(): Boolean = sampledToLocalTracing

    @InternalRpcApi
    public interface Marshaller<T> {
        public fun stream(value: T): Source
        public fun parse(source: Source): T
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
        override fun stream(value: Request): Source {
            return requestCodec.encode(value)
        }

        override fun parse(source: Source): Request {
            return requestCodec.decode(source)
        }
    }

    val responseMarshaller = object : MethodDescriptor.Marshaller<Response> {
        override fun stream(value: Response): Source {
            return responseCodec.encode(value)
        }

        override fun parse(source: Source): Response {
            return responseCodec.decode(source)
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
