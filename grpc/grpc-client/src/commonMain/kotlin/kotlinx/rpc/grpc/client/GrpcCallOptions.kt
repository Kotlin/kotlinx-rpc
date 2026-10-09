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
    internal var isTimeoutExplicitlySet = false
    internal var isWaitForReadyExplicitlySet = false

    /**
     * The maximum duration to wait for the RPC to complete.
     *
     * If the RPC exceeds the timeout, it fails with
     * [kotlinx.rpc.grpc.GrpcStatusCode.DEADLINE_EXCEEDED].
     * The timeout starts with the start of the call.
     *
     * The default is [Duration.INFINITE], which sets no per-call timeout.
     */
    public var timeout: Duration = Duration.INFINITE
        set(value) {
            isTimeoutExplicitlySet = true
            field = value
        }

    /**
     * Restores [timeout] to its default.
     */
    public fun clearTimeout() {
        timeout = Duration.INFINITE
        isTimeoutExplicitlySet = false
    }

    /**
     * Whether the call waits when the client can't connect to the server.
     *
     * - `false` (the default): the call fails immediately with
     *   [kotlinx.rpc.grpc.GrpcStatusCode.UNAVAILABLE].
     * - `true`: the call waits until the client connects. It fails with
     *   [kotlinx.rpc.grpc.GrpcStatusCode.DEADLINE_EXCEEDED] when its [timeout] expires.
     *
     * A call that starts while the client is connecting always waits for that connection attempt.
     *
     * When this option is `true`, set a finite [timeout].
     * Without a deadline, the call can remain pending indefinitely.
     *
     * @see [Wait-for-ready semantics](https://github.com/grpc/grpc/blob/master/doc/wait-for-ready.md)
     */
    public var waitForReady: Boolean = false
        set(value) {
            isWaitForReadyExplicitlySet = true
            field = value
        }

    /**
     * Restores [waitForReady] to its default.
     */
    public fun clearWaitForReady() {
        waitForReady = false
        isWaitForReadyExplicitlySet = false
    }

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
     * Defaults to [GrpcEmptyCallCredentials], which attaches no authentication headers.
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
     * the call will fail with [kotlinx.rpc.grpc.GrpcStatusCode.UNAUTHENTICATED] unless the channel
     * is configured with TLS credentials (which is the default, except if the client uses `plaintext()`).
     *
     * @see GrpcCallCredentials
     * @see GrpcEmptyCallCredentials
     * @see GrpcCallCredentials.plus
     */
    public var callCredentials: GrpcCallCredentials = GrpcEmptyCallCredentials
}

/**
 * Copies these options, so a call can change its options without affecting the original instance.
 */
internal fun GrpcCallOptions.copy(): GrpcCallOptions = GrpcCallOptions().also { copy ->
    copy.timeout = timeout
    copy.compression = compression
    copy.callCredentials = callCredentials
}
