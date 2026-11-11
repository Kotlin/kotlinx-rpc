/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter

internal actual fun encodeTo(writer: GrpcMessageWriter, block: (WireEncoder) -> Unit) {
    encodeToCommon(writer, block)
}

internal actual fun decodeFrom(reader: GrpcMessageReader, recursionLimit: Int, block: (WireDecoder) -> Unit) {
    decodeFromCommon(reader, recursionLimit, block)
}
