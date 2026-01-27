/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec

import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.input.stream.InputStream
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.input.stream.asSource
import kotlin.reflect.KType

@ExperimentalRpcApi
public fun interface MessageCodecResolver {
    public fun resolveOrNull(kType: KType): MessageCodec<*>?
}

@ExperimentalRpcApi
public object EmptyMessageCodecResolver : MessageCodecResolver {
    override fun resolveOrNull(kType: KType): MessageCodec<*>? {
        return null
    }
}

@ExperimentalRpcApi
public operator fun MessageCodecResolver.plus(other: MessageCodecResolver): MessageCodecResolver {
    return MessageCodecResolver { kType ->
        this.resolveOrNull(kType) ?: other.resolveOrNull(kType)
    }
}

/**
 * A marker interface for configurations passed to [MessageCodec]s.
 */
public interface CodecConfig

@ExperimentalRpcApi
public interface MessageCodec<T> {
    public fun encode(value: T, config: CodecConfig? = null): InputStream
    public fun decode(stream: InputStream, config: CodecConfig? = null): T
}

@ExperimentalRpcApi
public interface SourcedMessageCodec<T> : MessageCodec<T> {
    public fun encodeToSource(value: T, config: CodecConfig?): Source
    public fun decodeFromSource(stream: Source, config: CodecConfig?): T

    override fun encode(value: T, config: CodecConfig?): InputStream {
        return encodeToSource(value, config).asInputStream()
    }

    override fun decode(stream: InputStream, config: CodecConfig?): T {
        return decodeFromSource(stream.asSource(), config)
    }
}

@InternalRpcApi
public object ThrowingMessageCodecResolver : MessageCodecResolver {
    override fun resolveOrNull(kType: KType): MessageCodec<*> {
        error("No codec found for type $kType")
    }
}
