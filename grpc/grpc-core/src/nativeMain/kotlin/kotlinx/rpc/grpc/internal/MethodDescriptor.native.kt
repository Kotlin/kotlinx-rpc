/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual abstract class InputStream

@InternalRpcApi
internal actual val MethodDescriptor<*, *>.type: MethodType
    get() = TODO("Not yet implemented")

@InternalRpcApi
public actual class MethodDescriptor<Request, Response> {
    public actual fun getFullMethodName(): String {
        TODO("Not yet implemented")
    }

    public actual fun getServiceName(): String? {
        TODO("Not yet implemented")
    }

    public actual fun getRequestMarshaller(): Marshaller<Request> {
        TODO("Not yet implemented")
    }

    public actual fun getResponseMarshaller(): Marshaller<Response> {
        TODO("Not yet implemented")
    }

    public actual fun getSchemaDescriptor(): Any? {
        TODO("Not yet implemented")
    }

    public actual fun isIdempotent(): Boolean {
        TODO("Not yet implemented")
    }

    public actual fun isSafe(): Boolean {
        TODO("Not yet implemented")
    }

    public actual fun isSampledToLocalTracing(): Boolean {
        TODO("Not yet implemented")
    }

    public actual interface Marshaller<T> {
        public actual fun stream(value: T): InputStream
        public actual fun parse(stream: InputStream): T
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
    TODO("Not yet implemented")
}
