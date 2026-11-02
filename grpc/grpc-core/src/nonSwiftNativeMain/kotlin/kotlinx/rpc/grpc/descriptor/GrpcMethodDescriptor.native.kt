/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class GrpcMethodDescriptor<Request, Response> internal constructor(
    private val fullMethodName: String,
    public val requestMarshaller: GrpcMarshaller<Request>,
    public val responseMarshaller: GrpcMarshaller<Response>,
    public val methodType: GrpcMethodType,
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

public actual val GrpcMethodDescriptor<*, *>.methodType: GrpcMethodType
    get() = this.methodType

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
    return GrpcMethodDescriptor(
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
