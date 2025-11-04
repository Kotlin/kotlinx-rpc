/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

/**
 * Represents a compression algorithm for gRPC message encoding.
 *
 * Compression can be applied to gRPC messages to reduce bandwidth usage during transmission.
 *
 * ## Supported Algorithms
 * - [None] (identity): No compression is applied.
 * - [Gzip]: GZIP compression algorithm, widely supported and provides good compression ratios.
 *
 * This interface is not meant to be implemented by users.
 *
 * @property name The compression algorithm identifier sent in the `grpc-encoding` header.
 *
 * @see kotlinx.rpc.grpc.client.GrpcCallOptions.compression
 * @see GrpcCompression.None
 * @see GrpcCompression.Gzip
 */
@OptIn(ExperimentalSubclassOptIn::class)
@SubclassOptInRequired
public interface GrpcCompression {

    /**
     * The name of the compression algorithm as it appears in the `grpc-encoding` header.
     */
    public val name: String

    /**
     * Represents no compression (identity encoding).
     *
     * This is the default compression setting. When used, messages are transmitted without
     * any compression applied.
     */
    public object None : GrpcCompression {
        override val name: String = "identity"
    }

    /**
     * Represents GZIP compression.
     *
     * GZIP is a widely supported compression algorithm that provides good compression ratios
     * for most data types.
     *
     * **Note**: Ensure the server supports GZIP compression before using this option,
     * as the call will fail if the server cannot handle the requested compression algorithm.
     */
    public object Gzip : GrpcCompression {
        override val name: String = "gzip"
    }
}