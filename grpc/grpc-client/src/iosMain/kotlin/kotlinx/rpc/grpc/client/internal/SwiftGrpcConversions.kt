/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.rpc.grpc.GrpcCompression
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.visitEntries
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCompression
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCompressionGzip
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcCompressionNone
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMetadata
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMetadataVisitorProtocol
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodType
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeBidirectionalStreaming
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeClientStreaming
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeServerStreaming
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcMethodTypeUnary
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcStatus

internal fun GrpcMetadata.toSwift(): SwiftGrpcMetadata = SwiftGrpcMetadata().also { result ->
    visitEntries(
        onString = { key, value -> result.addStringValue(value, forKey = key) },
        onBinary = { key, value ->
            value.usePinned { pinned ->
                result.addBinaryValue(
                    bytes = if (value.isEmpty()) null else pinned.addressOf(0),
                    length = value.size.toLong(),
                    forKey = key,
                )
            }
        },
    )
}

internal fun SwiftGrpcMetadata.toKotlin(): GrpcMetadata = GrpcMetadata().also { result ->
    visitEntries(object : NSObject(), SwiftGrpcMetadataVisitorProtocol {
        override fun visitStringWithKey(key: String, value: String) {
            result.append(key, value)
        }

        override fun visitBinaryWithKey(key: String, bytes: COpaquePointer?, length: Long) {
            result.appendBinary(key, copySwiftByteArray(bytes, length))
        }
    })
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
