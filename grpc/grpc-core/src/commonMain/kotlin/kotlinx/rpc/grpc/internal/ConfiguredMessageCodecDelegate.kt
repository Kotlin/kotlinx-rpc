/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.input.stream.InputStream

/**
 * A [MessageCodec] that delegates encoding and decoding to another codec,
 * but with a configured default [CodecConfig].
 */
// Used in the compiler plugin when creating/resolving the message codec in the methodDescriptor, to
// return construct a message codec that wraps the actual message codec but applies the default config
// given during client configuration.
@InternalRpcApi
public class ConfiguredMessageCodecDelegate<T: Any>(
    private val config: CodecConfig,
    private val delegate: MessageCodec<T>
): MessageCodec<T> {
    override fun encode(
        value: T,
        config: CodecConfig?
    ): InputStream = delegate.encode(value, config ?: this.config)

    override fun decode(
        stream: InputStream,
        config: CodecConfig?
    ): T = delegate.decode(stream, config ?: this.config)
}