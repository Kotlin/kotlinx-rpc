/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader

internal val bytesMarshaller: GrpcMarshaller<ByteArray> = object : GrpcMarshaller<ByteArray> {
    override fun prepare(value: ByteArray, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        GrpcEncodedMessage.of(value)

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): ByteArray =
        reader.asSource().readByteArray()
}

internal val stringMarshaller: GrpcMarshaller<String> = object : GrpcMarshaller<String> {
    override fun prepare(value: String, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        GrpcEncodedMessage.of(Buffer().apply { writeString(value) })

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): String =
        reader.asSource().readString()
}

internal val unitMarshaller: GrpcMarshaller<Unit> = object : GrpcMarshaller<Unit> {
    override fun prepare(value: Unit, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        GrpcEncodedMessage.of(ByteArray(0))

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?) = Unit
}
