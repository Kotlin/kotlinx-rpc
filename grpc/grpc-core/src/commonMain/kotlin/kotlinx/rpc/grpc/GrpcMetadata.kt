/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

@Suppress("RedundantConstructorKeyword")
public expect class GrpcMetadata constructor()

public expect operator fun GrpcMetadata.get(key: String): String?
public expect fun GrpcMetadata.getBinary(key: String): ByteArray?
public expect fun GrpcMetadata.getAll(key: String): List<String>
public expect fun GrpcMetadata.getAllBinary(key: String): List<ByteArray>

public expect fun GrpcMetadata.keys(): Set<String>
public expect operator fun GrpcMetadata.contains(key: String): Boolean

public expect fun GrpcMetadata.append(key: String, value: String)
public expect fun GrpcMetadata.appendBinary(key: String, value: ByteArray)

public expect fun GrpcMetadata.remove(key: String, value: String): Boolean
public expect fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean
public expect fun GrpcMetadata.removeAll(key: String): List<String>
public expect fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray>

public expect fun GrpcMetadata.merge(other: GrpcMetadata)
public operator fun GrpcMetadata.plusAssign(other: GrpcMetadata): Unit = merge(other)

public fun GrpcMetadata.copy(): GrpcMetadata = GrpcMetadata().also { it.merge(this) }
public operator fun GrpcMetadata.plus(other: GrpcMetadata): GrpcMetadata = copy().apply { merge(other) }
