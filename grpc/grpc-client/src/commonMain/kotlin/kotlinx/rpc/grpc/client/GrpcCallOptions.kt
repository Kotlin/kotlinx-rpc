/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.grpc.GrpcCompression
import kotlin.time.Duration

/**
 * The collection of runtime options for a new gRPC call.
 *
 * This class allows configuring per-call behavior such as timeouts.
 */
public class GrpcCallOptions {
    /**
     * The maximum duration to wait for the RPC to complete.
     *
     * If set, the RPC will be canceled (with `DEADLINE_EXCEEDED`)
     * if it does not complete within the specified duration.
     * The timeout is measured from the moment the call is initiated.
     * If `null`, no timeout is applied, and the call may run indefinitely.
     *
     * The default value is `null`.
     *
     * @see kotlin.time.Duration
     */
    public var timeout: Duration? = null

    /**
     * The compression algorithm to use for encoding outgoing messages in this call.
     *
     * When set to a value other than [GrpcCompression.None], the client will compress request messages
     * using the specified algorithm before sending them to the server. The chosen compression algorithm
     * is communicated to the server via the `grpc-encoding` header.
     *
     * ## Default Behavior
     * Defaults to [GrpcCompression.None], meaning no compression is applied to messages.
     *
     * ## Server Compatibility
     * **Important**: It is the caller's responsibility to ensure the server supports the chosen
     * compression algorithm. There is no automatic negotiation performed. If the server does not
     * support the requested compression, the call will fail.
     *
     * ## Available Algorithms
     * - [GrpcCompression.None]: No compression (identity encoding) - **default**
     * - [GrpcCompression.Gzip]: GZIP compression, widely supported
     *
     * @see GrpcCompression
     */
    public var compression: GrpcCompression = GrpcCompression.None

    /**
     * Per-call authentication credentials to apply to this RPC.
     *
     * Call credentials allow dynamic, per-request authentication by adding metadata headers
     * such as bearer tokens, API keys, or custom authentication information.
     *
     * ## Default Behavior
     * Defaults to [EmptyCallCredentials], which attaches no authentication headers.
     *
     * ## Usage Patterns
     *
     * ### Setting in Client Interceptors
     * Call credentials can be dynamically added or modified in client interceptors:
     *
     * ```kotlin
     * val client = GrpcClient("example.com", 443) {
     *     interceptors {
     *         clientInterceptor {
     *             callOptions.callCredentials += BearerTokenCredentials(getToken())
     *             proceed(it)
     *         }
     *     }
     * }
     * ```
     *
     * ### Combining Multiple Credentials
     * Multiple call credentials can be combined using the `+` operator:
     *
     * ```kotlin
     * callOptions.callCredentials = bearerToken + apiKey
     * // or incrementally:
     * callOptions.callCredentials += additionalCredential
     * ```
     *
     * ### Transport Security
     * If any call credential requires transport security ([GrpcCallCredentials.requiresTransportSecurity]),
     * the call will fail with [kotlinx.rpc.grpc.StatusCode.UNAUTHENTICATED] unless the channel
     * is configured with TLS credentials (which is the default, except if the client uses `plaintext()`).
     *
     * @see GrpcCallCredentials
     * @see EmptyCallCredentials
     * @see GrpcCallCredentials.plus
     */
    public var callCredentials: GrpcCallCredentials = EmptyCallCredentials
}