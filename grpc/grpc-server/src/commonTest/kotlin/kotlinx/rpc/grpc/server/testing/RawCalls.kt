/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodDescriptor

/** Wire method names for raw calls that bypass the generated stub. */
internal object TestServiceMethods {
    const val EMPTY_CALL = "grpc.testing.TestService/EmptyCall"
    const val UNARY_CALL = "grpc.testing.TestService/UnaryCall"
    const val STREAMING_OUTPUT_CALL = "grpc.testing.TestService/StreamingOutputCall"
    const val STREAMING_INPUT_CALL = "grpc.testing.TestService/StreamingInputCall"
    const val FULL_DUPLEX_CALL = "grpc.testing.TestService/FullDuplexCall"
    const val HALF_DUPLEX_CALL = "grpc.testing.TestService/HalfDuplexCall"
}

/** Uses the requested call [type], even if the proto declares a different type for [fullMethodName]. */
internal fun rawMethod(fullMethodName: String, type: GrpcMethodType): GrpcMethodDescriptor<ByteArray, ByteArray> =
    methodDescriptor(
        fullMethodName = fullMethodName,
        requestMarshaller = bytesMarshaller,
        responseMarshaller = bytesMarshaller,
        type = type,
        schemaDescriptor = null,
        idempotent = false,
        safe = false,
        sampledToLocalTracing = false,
    )
