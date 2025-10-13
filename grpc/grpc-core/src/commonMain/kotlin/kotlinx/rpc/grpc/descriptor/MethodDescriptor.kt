/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.input.stream.InputStream

public expect class MethodDescriptor<Request, Response> {
    public fun getFullMethodName(): String
    public fun getServiceName(): String?
    public fun getRequestMarshaller(): Marshaller<Request>
    public fun getResponseMarshaller(): Marshaller<Response>
    public fun getSchemaDescriptor(): Any?
    public fun isIdempotent(): Boolean
    public fun isSafe(): Boolean
    public fun isSampledToLocalTracing(): Boolean

    public interface Marshaller<T> {
        public fun stream(value: T): InputStream
        public fun parse(stream: InputStream): T
    }
}

public expect val MethodDescriptor<*, *>.methodType: MethodType

public enum class MethodType {
    UNARY,
    CLIENT_STREAMING,
    SERVER_STREAMING,
    BIDI_STREAMING,
    UNKNOWN,
}

/**
 * Creates a new [MethodDescriptor] instance.
 *
 * @param fullMethodName the full name of the method, consisting of the service name followed by a forward slash
 *      and the method name. It does not include a leading slash.
 */
@InternalRpcApi
public expect fun <Request, Response> methodDescriptor(
    fullMethodName: String,
    requestCodec: MessageCodec<Request>,
    responseCodec: MessageCodec<Response>,
    type: MethodType,
    schemaDescriptor: Any?,
    idempotent: Boolean,
    safe: Boolean,
    sampledToLocalTracing: Boolean,
): MethodDescriptor<Request, Response>
