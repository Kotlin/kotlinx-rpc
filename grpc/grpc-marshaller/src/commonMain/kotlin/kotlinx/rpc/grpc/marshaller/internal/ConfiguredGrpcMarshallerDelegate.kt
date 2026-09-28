/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * A [GrpcMarshaller] that delegates encoding and decoding to another marshaller,
 * but with a configured default [GrpcMarshallerConfig].
 *
 * Used in the compiler plugin when creating/resolving the message marshaller in the methodDescriptor, to
 * return construct a message marshaller that wraps the actual message marshaller but applies the default config
 * given during client configuration.
 */
@InternalRpcApi
public class ConfiguredGrpcMarshallerDelegate<T: Any>(
    private val config: GrpcMarshallerConfig,
    private val delegate: GrpcMarshaller<T>
): GrpcMarshaller<T> {
    override fun prepare(
        value: T,
        config: GrpcMarshallerConfig?,
    ): GrpcEncodedMessage = delegate.prepare(value, config ?: this.config)

    override fun decode(
        reader: GrpcMessageReader,
        config: GrpcMarshallerConfig?,
    ): T = delegate.decode(reader, config ?: this.config)

}
