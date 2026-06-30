/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlin.test.assertContentEquals

internal fun <M> M.encodeDecode(
    marshaller: GrpcMarshaller<M>,
): M {
    val source = marshaller.encode(this)
    return marshaller.decode(source)
}

internal fun ByteArray.asByteString(): ByteString = ByteString(*this)

internal fun ByteString?.toByteList(): List<Byte>? = this?.toByteArray()?.toList()

internal fun assertByteStringContentEquals(expected: ByteArray, actual: ByteString?, message: String? = null) {
    assertContentEquals(expected, actual?.toByteArray(), message)
}
