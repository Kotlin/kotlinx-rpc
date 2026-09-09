/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.rpc.grpc.GrpcCompression
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.getAllBinary
import kotlinx.rpc.grpc.keys
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCompression
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCompressionGzip
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCompressionNone
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMetadata
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMetadataValueKindBinary
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMetadataValueKindString
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodType
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeBidirectionalStreaming
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeClientStreaming
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeServerStreaming
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeUnary
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcStatus

internal fun GrpcMetadata.toSwift(): SwiftGrpcMetadata = SwiftGrpcMetadata().also { result ->
    for (key in keys()) {
        if (key.endsWith("-bin")) {
            for (value in getAllBinary(key)) {
                value.usePinned { pinned ->
                    result.addBinaryValue(
                        bytes = if (value.isEmpty()) null else pinned.addressOf(0),
                        length = value.size.toLong(),
                        forKey = key,
                    )
                }
            }
        } else {
            for (value in getAll(key)) result.addStringValue(value, forKey = key)
        }
    }
}

internal fun SwiftGrpcMetadata.toKotlin(): GrpcMetadata = GrpcMetadata().also { result ->
    for (index in 0 until count) {
        val key = keyAtIndex(index) ?: error("grpc-swift returned metadata without a key at index $index")
        when (valueKindAtIndex(index)) {
            SwiftGrpcMetadataValueKindString -> {
                val value = stringValueAtIndex(index)
                    ?: error("grpc-swift returned string metadata without a value at index $index")
                result.append(key, value)
            }
            SwiftGrpcMetadataValueKindBinary -> {
                val copied = withBinaryValueAtIndex(index) { bytes, length ->
                    result.appendBinary(key, copySwiftByteArray(bytes, length))
                }
                check(copied) { "grpc-swift returned invalid binary metadata at index $index" }
            }
            else -> error("grpc-swift returned an invalid metadata value kind at index $index")
        }
    }
}

internal fun GrpcMethodType.toSwift(): SwiftGrpcMethodType = when (this) {
    GrpcMethodType.UNARY -> SwiftGrpcMethodTypeUnary
    GrpcMethodType.CLIENT_STREAMING -> SwiftGrpcMethodTypeClientStreaming
    GrpcMethodType.SERVER_STREAMING -> SwiftGrpcMethodTypeServerStreaming
    GrpcMethodType.BIDI_STREAMING -> SwiftGrpcMethodTypeBidirectionalStreaming
    else -> error("Unsupported gRPC method type: $this")
}

internal fun GrpcCompression.toSwift(): SwiftGrpcCompression = when (this) {
    GrpcCompression.None -> SwiftGrpcCompressionNone
    GrpcCompression.Gzip -> SwiftGrpcCompressionGzip
    else -> error("Unsupported gRPC compression: $name")
}

internal fun SwiftGrpcStatus.toKotlin(): GrpcStatus = GrpcStatus(
    code = GrpcStatusCode.entries.firstOrNull { it.value.toLong() == code }
        ?: GrpcStatusCode.UNKNOWN,
    description = message,
)
