/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KType

@ExperimentalRpcApi
public fun interface GrpcMarshallerResolver {
    public fun resolveOrNull(kType: KType): GrpcMarshaller<*>?
}

@ExperimentalRpcApi
public object GrpcEmptyMarshallerResolver : GrpcMarshallerResolver {
    override fun resolveOrNull(kType: KType): GrpcMarshaller<*>? {
        return null
    }
}

@ExperimentalRpcApi
public operator fun GrpcMarshallerResolver.plus(other: GrpcMarshallerResolver): GrpcMarshallerResolver {
    return GrpcMarshallerResolver { kType ->
        this.resolveOrNull(kType) ?: other.resolveOrNull(kType)
    }
}

/**
 * A marker interface for configurations passed to [GrpcMarshaller]s during encoding and decoding operations.
 *
 * Implementations of this interface can provide marshaller-specific configuration options that control
 * the behavior of message serialization and deserialization. Each marshaller implementation may support
 * different configuration options by defining its own [GrpcMarshallerConfig] subtype.
 *
 * Configuration can be passed to marshallers in two ways:
 * - Per-operation: directly to [GrpcMarshaller.encode] and [GrpcMarshaller.decode] methods
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
 * @see GrpcMarshaller
 * @see marshallerOf
 */
@ExperimentalRpcApi
public interface GrpcMarshallerConfig

@ExperimentalRpcApi
public interface GrpcMarshaller<T> {
    public fun encode(value: T, config: GrpcMarshallerConfig? = null): Source
    public fun decode(source: Source, config: GrpcMarshallerConfig? = null): T
}

@InternalRpcApi
public object ThrowingGrpcMarshallerResolver : GrpcMarshallerResolver {
    override fun resolveOrNull(kType: KType): GrpcMarshaller<*> {
        error("No marshaller found for type $kType")
    }
}
