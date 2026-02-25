/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.io.Source
import kotlinx.rpc.grpc.marshaller.MarshallerConfig
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * A [kotlinx.rpc.grpc.marshaller.MessageMarshaller] that delegates encoding and decoding to another marshaller,
 * but with a configured default [kotlinx.rpc.grpc.marshaller.MarshallerConfig].
 *
 * Used in the compiler plugin when creating/resolving the message marshaller in the methodDescriptor, to
 * return construct a message marshaller that wraps the actual message marshaller but applies the default config
 * given during client configuration.
 */
@InternalRpcApi
public class ConfiguredMessageMarshallerDelegate<T: Any>(
    private val config: MarshallerConfig,
    private val delegate: MessageMarshaller<T>
): MessageMarshaller<T> {
    override fun encode(
        value: T,
        config: MarshallerConfig?
    ): Source = delegate.encode(value, config ?: this.config)

    override fun decode(
        source: Source,
        config: MarshallerConfig?
    ): T = delegate.decode(source, config ?: this.config)
}
