/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.grpc.codec.CodecConfig

/**
 * Configuration options for Protobuf message encoding and decoding.
 *
 * This class implements [CodecConfig] to provide Protobuf-specific configuration that controls
 * how messages are serialized and deserialized when using Protobuf codecs generated from
 * `.proto` files using `kotlinx-rpc`.
 *
 * Example:
 * ```kotlin
 * // Create a codec with custom config
 * val config = ProtobufConfig(discardUnknownFields = true)
 * val myCodec = codec<MyMessage>(config)
 *
 * // Or pass config per-operation
 * val decoded = codec.decode(stream, config)
 * ```
 *
 * @property discardUnknownFields When `true`, unknown fields encountered during deserialization
 *   are silently discarded. When `false` (default), unknown fields are preserved.
 *
 * @see CodecConfig
 * @see kotlinx.rpc.grpc.codec.codec
 */
public class ProtobufConfig(
    public val discardUnknownFields: Boolean = false
): CodecConfig