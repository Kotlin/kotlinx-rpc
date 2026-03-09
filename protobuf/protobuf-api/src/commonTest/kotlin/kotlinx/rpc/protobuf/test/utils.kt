/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.grpc.marshaller.GrpcMarshaller

internal fun <M> M.encodeDecode(
    marshaller: GrpcMarshaller<M>,
): M {
    val source = marshaller.encode(this)
    return marshaller.decode(source)
}
