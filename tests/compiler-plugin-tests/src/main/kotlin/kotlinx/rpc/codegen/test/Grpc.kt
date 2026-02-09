/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import io.grpc.MethodDescriptor
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.descriptor.GrpcServiceDelegate
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.protobuf.input.stream.InputStream
import kotlin.reflect.KType

/**
 * To check in methods:
 * ```
 * bareMethodName
 * fullMethodName
 * type
 * serviceName
 * isSafe
 * isIdempotent
 * isSampledToLocalTracing
 * ```
 *
 * Types:
 * ```
 * MethodDescriptor.MethodType.UNARY
 * MethodDescriptor.MethodType.SERVER_STREAMING
 * MethodDescriptor.MethodType.CLIENT_STREAMING
 * MethodDescriptor.MethodType.BIDI_STREAMING
 * ```
 */
@Suppress("unused")
inline fun <@Grpc reified T : Any> grpcDelegate(): GrpcServiceDelegate {
    val descriptor = serviceDescriptorOf<T>() as GrpcServiceDescriptor
    val delegate = descriptor.delegate(SimpleResolver, null)

    return delegate
}

fun MethodDescriptor<*, *>.checkMethod(
    expectedMethodName: String,
    expectedFullMethodName: String,
    expectedMethodType: MethodDescriptor.MethodType,
    expectedServiceName: String,
    expectedIsSafe: Boolean = false,
    expectedIsIdempotent: Boolean = false,
    expectedIsSampledToLocalTracing: Boolean = true,
): String? {
    return when {
        bareMethodName != expectedMethodName -> "wrong bareMethodName: $bareMethodName"
        fullMethodName != expectedFullMethodName -> "wrong bareMethodName: $fullMethodName"
        type != expectedMethodType -> "wrong type: $type"
        serviceName != expectedServiceName -> "wrong service name: $fullMethodName"
        isSafe != expectedIsSafe -> "wrong isSafe: $isSafe"
        isIdempotent != expectedIsIdempotent -> "wrong isIdempotent: $isIdempotent"
        isSampledToLocalTracing != expectedIsSampledToLocalTracing -> "wrong isSampledToLocalTracing: $isSampledToLocalTracing"
        else -> null
    }
}

object SimpleResolver : MessageCodecResolver {
    override fun resolveOrNull(kType: KType): MessageCodec<*>? {
        return when (kType.classifier) {
            String::class -> StringCodec
            Unit::class -> UnitCodec
            Message::class -> MessageClassCodec
            else -> null
        }
    }
}

object StringCodec : MessageCodec<String> {
    override fun encode(value: String, config: CodecConfig?): InputStream {
        TODO("Not yet implemented")
    }

    override fun decode(stream: InputStream, config: CodecConfig?): String {
        TODO("Not yet implemented")
    }
}

object UnitCodec : MessageCodec<String> {
    override fun encode(value: String, config: CodecConfig?): InputStream {
        TODO("Not yet implemented")
    }

    override fun decode(stream: InputStream, config: CodecConfig?): String {
        TODO("Not yet implemented")
    }
}

@Suppress("unused")
class Message(val a: Int, val b: String)

object MessageClassCodec : MessageCodec<Message> {
    override fun encode(value: Message, config: CodecConfig?): InputStream {
        TODO("Not yet implemented")
    }

    override fun decode(stream: InputStream, config: CodecConfig?): Message {
        TODO("Not yet implemented")
    }
}
