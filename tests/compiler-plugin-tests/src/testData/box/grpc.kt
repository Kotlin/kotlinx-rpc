/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: BACKEND

@file:OptIn(kotlinx.rpc.internal.utils.ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)

import io.grpc.MethodDescriptor
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.codegen.test.grpcDelegate
import kotlinx.rpc.codegen.test.checkMethod
import kotlinx.rpc.codegen.test.Message
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.protobuf.input.stream.InputStream

@WithCodec(Custom.Companion::class)
class Custom(val content: String) {
    companion object : MessageCodec<Custom> {
        override fun encode(value: Custom): InputStream {
            TODO("Not yet implemented")
        }

        override fun decode(stream: InputStream): Custom {
            TODO("Not yet implemented")
        }
    }
}

@Grpc
interface BoxService {
    suspend fun simple(value: String): String

    suspend fun unit()

    suspend fun clientStream(flow: Flow<String>)

    fun serverStream(): Flow<Unit>

    fun bidiStream(flow: Flow<Message>): Flow<Message>

    suspend fun custom(): Custom
}

fun box(): String {
    val delegate = grpcDelegate<BoxService>()

    if (delegate.serviceDescriptor.name != "BoxService") {
        return "Fail: Wrong service name: ${delegate.serviceDescriptor.name}"
    }

    delegate.getMethodDescriptor("simple")!!.checkMethod(
        expectedMethodName = "simple",
        expectedMethodType = MethodDescriptor.MethodType.UNARY,
        expectedServiceName = "BoxService",
    )?.let { return "Fail: $it"}

    delegate.getMethodDescriptor("unit")!!.checkMethod(
        expectedMethodName = "unit",
        expectedMethodType = MethodDescriptor.MethodType.UNARY,
        expectedServiceName = "BoxService",
    )?.let { return "Fail: $it"}

    delegate.getMethodDescriptor("custom")!!.checkMethod(
        expectedMethodName = "custom",
        expectedMethodType = MethodDescriptor.MethodType.UNARY,
        expectedServiceName = "BoxService",
    )?.let { return "Fail: $it"}

    delegate.getMethodDescriptor("clientStream")!!.checkMethod(
        expectedMethodName = "clientStream",
        expectedMethodType = MethodDescriptor.MethodType.CLIENT_STREAMING,
        expectedServiceName = "BoxService",
    )?.let { return "Fail: $it"}

    delegate.getMethodDescriptor("serverStream")!!.checkMethod(
        expectedMethodName = "serverStream",
        expectedMethodType = MethodDescriptor.MethodType.SERVER_STREAMING,
        expectedServiceName = "BoxService",
    )?.let { return "Fail: $it"}

    delegate.getMethodDescriptor("bidiStream")!!.checkMethod(
        expectedMethodName = "bidiStream",
        expectedMethodType = MethodDescriptor.MethodType.BIDI_STREAMING,
        expectedServiceName = "BoxService",
    )?.let { return "Fail: $it"}

    return "OK"
}
