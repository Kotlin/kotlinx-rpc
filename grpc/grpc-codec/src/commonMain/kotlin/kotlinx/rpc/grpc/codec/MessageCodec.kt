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

@ExperimentalRpcApi
public interface MessageCodec<T> {
    public fun encode(value: T): InputStream
    public fun decode(stream: InputStream): T
}

@ExperimentalRpcApi
public interface SourcedMessageCodec<T> : MessageCodec<T> {
    public fun encodeToSource(value: T): Source
    public fun decodeFromSource(stream: Source): T

    override fun encode(value: T): InputStream {
        return encodeToSource(value).asInputStream()
    }

    override fun decode(stream: InputStream): T {
        return decodeFromSource(stream.asSource())
    }
}

@InternalRpcApi
public object ThrowingMessageCodecResolver : MessageCodecResolver {
    override fun resolveOrNull(kType: KType): MessageCodec<*> {
        error("No codec found for type $kType")
    }
}
