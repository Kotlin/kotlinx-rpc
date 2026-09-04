/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class GrpcMetadata @InternalRpcApi actual constructor() {
    override fun toString(): String = TODO("Implement iOS gRPC metadata")
}

public actual class GrpcMetadataKey<T> public actual constructor(
    name: String,
    marshaller: GrpcMarshaller<T>,
) {
    public val name: String
        get() = TODO("Implement iOS gRPC metadata")

    public val marshaller: GrpcMarshaller<T>
        get() = TODO("Implement iOS gRPC metadata")
}

public actual operator fun GrpcMetadata.get(key: String): String? =
    TODO("Implement iOS gRPC metadata")

public actual operator fun <T> GrpcMetadata.get(key: GrpcMetadataKey<T>): T? =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.getBinary(key: String): ByteArray? =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.getBinary(key: GrpcMetadataKey<T>): T? =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.getAll(key: String): List<String> =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.getAll(key: GrpcMetadataKey<T>): List<T> =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.getAllBinary(key: String): List<ByteArray> =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.getAllBinary(key: GrpcMetadataKey<T>): List<T> =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.keys(): Set<String> =
    TODO("Implement iOS gRPC metadata")

public actual operator fun GrpcMetadata.contains(key: String): Boolean =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.append(key: String, value: String): Unit =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.append(key: GrpcMetadataKey<T>, value: T): Unit =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.appendBinary(key: String, value: ByteArray): Unit =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.appendBinary(key: GrpcMetadataKey<T>, value: T): Unit =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.remove(key: String, value: String): Boolean =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.remove(key: GrpcMetadataKey<T>, value: T): Boolean =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.removeBinary(key: GrpcMetadataKey<T>, value: T): Boolean =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.removeAll(key: String): List<String> =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.removeAll(key: GrpcMetadataKey<T>): List<T> =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray> =
    TODO("Implement iOS gRPC metadata")

public actual fun <T> GrpcMetadata.removeAllBinary(key: GrpcMetadataKey<T>): List<T> =
    TODO("Implement iOS gRPC metadata")

public actual fun GrpcMetadata.merge(other: GrpcMetadata): Unit =
    TODO("Implement iOS gRPC metadata")
