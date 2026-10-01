/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

/**
 * A convenience marshaller for formats that need to buffer a message before its encoded size is known.
 */
@ExperimentalRpcApi
public abstract class StreamingGrpcMarshaller<T> : GrpcMarshaller<T> {
    /** Encodes [value] into [sink]. */
    protected abstract fun encode(value: T, sink: Sink, config: GrpcMarshallerConfig?)

    /** Decodes a value from [source]. */
    protected abstract fun decode(source: Source, config: GrpcMarshallerConfig?): T

    final override fun prepare(value: T, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        GrpcEncodedMessage.of(Buffer().also { encode(value, it, config) })

    final override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): T =
        decode(reader.asSource(), config)
}
