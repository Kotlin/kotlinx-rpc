/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import io.grpc.MethodDescriptor
import kotlinx.io.Source
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlinx.rpc.grpc.descriptor.GrpcServiceDelegate
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
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

object SimpleResolver : GrpcMarshallerResolver {
    override fun resolveOrNull(kType: KType): GrpcMarshaller<*>? {
        return when (kType.classifier) {
            String::class -> StringMarshaller
            Unit::class -> UnitMarshaller
            Message::class -> MessageClassMarshaller
            else -> null
        }
    }
}

object StringMarshaller : GrpcMarshaller<String> {
    override fun encode(value: String, config: GrpcMarshallerConfig?): Source {
        TODO("Not yet implemented")
    }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): String {
        TODO("Not yet implemented")
    }
}

object UnitMarshaller : GrpcMarshaller<String> {
    override fun encode(value: String, config: GrpcMarshallerConfig?): Source {
        TODO("Not yet implemented")
    }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): String {
        TODO("Not yet implemented")
    }
}

@Suppress("unused")
class Message(val a: Int, val b: String)

object MessageClassMarshaller : GrpcMarshaller<Message> {
    override fun encode(value: Message, config: GrpcMarshallerConfig?): Source {
        TODO("Not yet implemented")
    }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): Message {
        TODO("Not yet implemented")
    }
}
