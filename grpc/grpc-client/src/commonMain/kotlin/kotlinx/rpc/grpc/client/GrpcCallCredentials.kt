/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.grpc.GrpcMetadata

/**
 * Provides per-call authentication credentials for gRPC calls.
 *
 * Call credentials are used to attach authentication information (such as tokens, API keys, or signatures)
 * to individual gRPC calls through metadata headers. Unlike client credentials, which establish
 * the transport security layer (e.g., TLS), call credentials operate at the application layer
 * and can be dynamically generated for each request.
 *
 * ## Usage
 *
 * Implement this interface to create custom authentication mechanisms:
 *
 * ```kotlin
 * class BearerTokenCredentials(private val token: String) : GrpcCallCredentials {
 *     override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
 *         append("Authorization", "Bearer $token")
 *     }
 * }
 * ```
 *
 * Credentials can be combined using the [plus] operator or [combine] function:
 *
 * ```kotlin
 * val credentials = TlsClientCredentials(...) + BearerTokenCredentials("my-token")
 * ```
 *
 * ## Transport Security
 *
 * By default, call credentials require transport security (TLS) to prevent credential leakage.
 * Override [requiresTransportSecurity] to `false` only for testing or non-production environments.
 *
 * @see applyOnMetadata
 * @see requiresTransportSecurity
 * @see plus
 * @see combine
 */
public interface GrpcCallCredentials {

    /**
     * Applies authentication metadata to the gRPC call.
     *
     * This method is invoked before each gRPC call to add authentication headers or metadata.
     * Implementations should append the necessary authentication information to the [GrpcMetadata] receiver.
     *
     * The method is suspending to allow asynchronous token retrieval or refresh operations,
     * such as fetching tokens from secure storage or performing OAuth token exchanges.
     *
     * ## Examples
     *
     * Adding a bearer token:
     * ```kotlin
     * override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
     *     append("Authorization", "Bearer $token")
     * }
     * ```
     *
     * Throwing a [kotlinx.rpc.grpc.StatusException] to fail the call:
     * ```kotlin
     * override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
     *     if (!isValid) {
     *         throw StatusException(Status(StatusCode.UNAUTHENTICATED, "Invalid credentials"))
     *     }
     *     append("Authorization", "Bearer $token")
     * }
     * ```
     *
     * @param callOptions The options for the current call, providing context and configuration.
     * @receiver The metadata to which authentication information should be added.
     * @throws kotlinx.rpc.grpc.StatusException to abort the call with a specific gRPC status.
     */
    public suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions)

    /**
     * Indicates whether this credential requires transport security (TLS).
     *
     * When `true` (the default), the credential will only be applied to calls over secure transports.
     * If transport security is not present, the call will fail with `UNAUTHENTICATED`.
     *
     * Set to `false` only for credentials that are safe to send over insecure connections,
     * such as in testing environments or for non-sensitive authentication mechanisms.
     *
     * @return `true` if transport security is required, `false` otherwise.
     */
    public val requiresTransportSecurity: Boolean
        get() = true
}

/**
 * Combines two call credentials into a single credential that applies both.
 *
 * The resulting credential will apply both sets of credentials in order, allowing
 * multiple authentication mechanisms to be used simultaneously. For example,
 * combining channel credentials with call credentials, or applying multiple
 * authentication headers to the same call.
 *
 * The combined credential requires transport security if either of the original
 * credentials requires it.
 *
 * ## Example
 *
 * ```kotlin
 * val tlsCreds = TlsClientCredentials { ... }
 * val bearerToken = BearerTokenCredentials("my-token")
 * val combined = tlsCreds + bearerToken
 * ```
 *
 * Multiple credentials can be chained:
 * ```kotlin
 * val combined = creds1 + creds2 + creds3
 * ```
 *
 * @param other The credential to combine with this one.
 * @return A new credential that applies both credentials.
 * @see combine
 */
public operator fun GrpcCallCredentials.plus(other: GrpcCallCredentials): GrpcCallCredentials {
    return CombinedCallCredentials(this, other)
}

/**
 * Combines two call credentials into a single credential that applies both.
 *
 * This is an alias for the [plus] operator, providing a more explicit method name
 * for combining credentials.
 *
 * @param other The credential to combine with this one.
 * @return A new credential that applies both credentials.
 * @see plus
 */
public fun GrpcCallCredentials.combine(other: GrpcCallCredentials): GrpcCallCredentials = this + other

/**
 * A call credential that performs no authentication.
 *
 * This is useful as a no-op placeholder or for disabling authentication in specific scenarios.
 * Since it performs no authentication, it does not require transport security.
 *
 * ## Example
 *
 * ```kotlin
 * val credentials = if (useAuth) {
 *     BearerTokenCredentials(token)
 * } else {
 *     EmptyCallCredentials
 * }
 * ```
 */
public object  EmptyCallCredentials : GrpcCallCredentials {
    override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
        // do nothing
    }
    override val requiresTransportSecurity: Boolean = false
}

internal class CombinedCallCredentials(
    private val first: GrpcCallCredentials,
    private val second: GrpcCallCredentials
) : GrpcCallCredentials {
    override suspend fun GrpcMetadata.applyOnMetadata(
        callOptions: GrpcCallOptions
    ) {
        with(first) {
            this@applyOnMetadata.applyOnMetadata(callOptions)
        }
        with(second) {
            this@applyOnMetadata.applyOnMetadata(callOptions)
        }
    }

    override val requiresTransportSecurity: Boolean = first.requiresTransportSecurity || second.requiresTransportSecurity
}
