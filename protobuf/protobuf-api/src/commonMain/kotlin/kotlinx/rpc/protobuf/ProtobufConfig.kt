/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig

/**
 * Configuration options for Protobuf message encoding and decoding.
 *
 * This class implements [GrpcMarshallerConfig] to provide Protobuf-specific configuration that controls
 * how messages are serialized and deserialized when using Protobuf marshallers generated from
 * `.proto` files using `kotlinx-rpc`.
 *
 * Example:
 * ```kotlin
 * // Create a marshaller with custom config
 * val config = ProtoConfig(discardUnknownFields = true)
 * val myMarshaller = grpcMarshallerOf<MyMessage>(config)
 *
 * // Or pass config per-operation
 * val decoded = marshaller.decode(stream, config)
 * ```
 *
 * @property discardUnknownFields When `true`, unknown fields encountered during deserialization
 *   are silently discarded. When `false` (default), unknown fields are preserved.
 * @property recursionLimit The maximum allowed nesting depth when decoding protobuf messages.
 *   Messages nested deeper than this limit will cause a [kotlinx.rpc.protobuf.internal.ProtobufDecodingException].
 *   Defaults to [DEFAULT_RECURSION_LIMIT] (100), matching Google's protobuf default.
 * @property extensionRegistry Registry of known protobuf extensions used during decoding.
 *   If set, extension fields matching registered descriptors are decoded.
 *   If `null` (default), extension fields are treated as unknown fields.
 *
 * @see GrpcMarshallerConfig
 * @see kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
 */
// TODO make DSL KRPC-264
public class ProtoConfig(
    public val discardUnknownFields: Boolean = false,
    public val recursionLimit: Int = DEFAULT_RECURSION_LIMIT,
    public val extensionRegistry: ProtoExtensionRegistry? = null
) : GrpcMarshallerConfig {
    public companion object {
        /**
         * The default recursion limit for decoding nested protobuf messages.
         * Matches the default used by Google's Java protobuf library.
         */
        public const val DEFAULT_RECURSION_LIMIT: Int = 100
    }
}
