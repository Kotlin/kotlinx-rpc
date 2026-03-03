/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.grpc.marshaller.MarshallerConfig

/**
 * Configuration options for Protobuf message encoding and decoding.
 *
 * This class implements [MarshallerConfig] to provide Protobuf-specific configuration that controls
 * how messages are serialized and deserialized when using Protobuf marshallers generated from
 * `.proto` files using `kotlinx-rpc`.
 *
 * Example:
 * ```kotlin
 * // Create a marshaller with custom config
 * val config = ProtobufConfig(discardUnknownFields = true)
 * val myMarshaller = marshallerOf<MyMessage>(config)
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
 *
 * @see MarshallerConfig
 * @see kotlinx.rpc.grpc.marshaller.marshallerOf
 */
public class ProtobufConfig(
    public val discardUnknownFields: Boolean = false,
    public val recursionLimit: Int = DEFAULT_RECURSION_LIMIT,
): MarshallerConfig {
    public companion object {
        /**
         * The default recursion limit for decoding nested protobuf messages.
         * Matches the default used by Google's Java protobuf library.
         */
        public const val DEFAULT_RECURSION_LIMIT: Int = 100
    }
}
