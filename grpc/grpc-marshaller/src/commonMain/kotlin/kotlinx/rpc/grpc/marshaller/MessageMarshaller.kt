/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KType

@ExperimentalRpcApi
public fun interface MessageMarshallerResolver {
    public fun resolveOrNull(kType: KType): MessageMarshaller<*>?
}

@ExperimentalRpcApi
public object EmptyMessageMarshallerResolver : MessageMarshallerResolver {
    override fun resolveOrNull(kType: KType): MessageMarshaller<*>? {
        return null
    }
}

@ExperimentalRpcApi
public operator fun MessageMarshallerResolver.plus(other: MessageMarshallerResolver): MessageMarshallerResolver {
    return MessageMarshallerResolver { kType ->
        this.resolveOrNull(kType) ?: other.resolveOrNull(kType)
    }
}

/**
 * A marker interface for configurations passed to [MessageMarshaller]s during encoding and decoding operations.
 *
 * Implementations of this interface can provide marshaller-specific configuration options that control
 * the behavior of message serialization and deserialization. Each marshaller implementation may support
 * different configuration options by defining its own [MarshallerConfig] subtype.
 *
 * Configuration can be passed to marshallers in two ways:
 * - Per-operation: directly to [MessageMarshaller.encode] and [MessageMarshaller.decode] methods
 * - As default: when retrieving a marshaller using [marshallerOf], which wraps the marshaller to use the config by default
 *
 * Example:
 * ```kotlin
 * // Per-operation config
 * val marshaller = marshallerOf<MyProtoMessage>()
 * val encoded = marshaller.encode(message, ProtobufConfig(discardUnknownFields = true))
 *
 * // Default config
 * val marshallerWithConfig = marshallerOf<MyProtoMessage>(ProtobufConfig(discardUnknownFields = true))
 * val encoded = marshallerWithConfig.encode(message) // uses the default config
 * ```
 *
 *
 * @see MessageMarshaller
 * @see marshallerOf
 */
@ExperimentalRpcApi
public interface MarshallerConfig

@ExperimentalRpcApi
public interface MessageMarshaller<T> {
    public fun encode(value: T, config: MarshallerConfig? = null): Source
    public fun decode(source: Source, config: MarshallerConfig? = null): T
}

@InternalRpcApi
public object ThrowingMessageMarshallerResolver : MessageMarshallerResolver {
    override fun resolveOrNull(kType: KType): MessageMarshaller<*> {
        error("No marshaller found for type $kType")
    }
}
