/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.input.stream.InputStream

@InternalRpcApi
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

@InternalRpcApi
internal expect val MethodDescriptor<*, *>.type: MethodType

@InternalRpcApi
public enum class MethodType {
    UNARY,
    CLIENT_STREAMING,
    SERVER_STREAMING,
    BIDI_STREAMING,
    UNKNOWN,
}

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
