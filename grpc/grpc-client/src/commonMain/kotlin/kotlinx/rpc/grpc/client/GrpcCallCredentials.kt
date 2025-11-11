/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.grpc.plus

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
 *     override suspend fun Context.getRequestMetadata(): GrpcMetadata {
 *         return buildGrpcMetadata {
 *             append("Authorization", "Bearer $token")
 *         }
 *     }
 * }
 * ```
 *
 * ## Context-Aware Credentials
 *
 * Use the [Context] to implement sophisticated authentication strategies:
 *
 * ```kotlin
 * class MethodScopedCredentials : GrpcCallCredentials {
 *     override suspend fun Context.getRequestMetadata(): GrpcMetadata {
 *         val scope = when (method.name) {
 *             "GetUser" -> "read:users"
 *             "UpdateUser" -> "write:users"
 *             else -> "default"
 *         }
 *         val token = fetchTokenWithScope(scope)
 *         return buildGrpcMetadata {
 *             append("Authorization", "Bearer $token")
 *         }
 *     }
 * }
 * ```
 *
 * ## Combining Credentials
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
 * @see getRequestMetadata
 * @see Context
 * @see requiresTransportSecurity
 * @see plus
 * @see combine
 */
public interface GrpcCallCredentials {

    /**
     * Retrieves authentication metadata for the gRPC call.
     *
     * This method is invoked before each gRPC call to generate authentication headers or metadata.
     * Implementations should return a [GrpcMetadata] object containing the necessary authentication
     * information for the request.
     *
     * The method is suspending to allow asynchronous operations such as:
     * - Token retrieval from secure storage
     * - OAuth token refresh or exchange
     * - Dynamic token generation or signing
     * - Network calls to authentication services
     *
     * ## Context Information
     *
     * The [Context] receiver provides access to call-specific information:
     * - [Context.method]: The method being invoked (for method-specific auth)
     * - [Context.authority]: The target authority (for tenant-aware auth)
     *
     * ## Examples
     *
     * Simple bearer token:
     * ```kotlin
     * override suspend fun Context.getRequestMetadata(): GrpcMetadata {
     *     return buildGrpcMetadata {
     *         append("Authorization", "Bearer $token")
     *     }
     * }
     * ```
     *
     * Throwing a [kotlinx.rpc.grpc.StatusException] to fail the call:
     * ```kotlin
     * override suspend fun Context.getRequestMetadata(): GrpcMetadata {
     *     val token = try {
     *         refreshToken()
     *     } catch (e: Exception) {
     *         throw StatusException(Status(StatusCode.UNAUTHENTICATED, "Token refresh failed"))
     *     }
     *
     *     return buildGrpcMetadata {
     *         append("Authorization", "Bearer $token")
     *     }
     * }
     * ```
     *
     * @receiver Context information about the call being authenticated.
     * @return Metadata containing authentication information to attach to the request.
     * @throws kotlinx.rpc.grpc.StatusException to abort the call with a specific gRPC status.
     */
    public suspend fun Context.getRequestMetadata(): GrpcMetadata

    /**
     * Indicates whether this credential requires transport security (TLS).
     *
     * When `true` (the default), the credential will only be applied to calls over secure transports.
     * If transport security is not present, the call will fail with [kotlinx.rpc.grpc.StatusCode.UNAUTHENTICATED].
     *
     * Set to `false` only for credentials that are safe to send over insecure connections,
     * such as in testing environments or for non-sensitive authentication mechanisms.
     *
     * @return `true` if transport security is required, `false` otherwise.
     */
    public val requiresTransportSecurity: Boolean
        get() = true

    /**
     * Context information available when retrieving call credentials.
     *
     * Provides metadata about the RPC call to enable method-specific authentication strategies.
     *
     * @property method The method descriptor of the RPC being invoked.
     * @property authority The authority (host:port) for this call.
     */
    // TODO: check whether we should add GrpcCallOptions in the context (KRPC-232)
    public data class Context(
        val method: MethodDescriptor<*, *>,
        val authority: String,
    )
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
    override suspend fun GrpcCallCredentials.Context.getRequestMetadata(): GrpcMetadata {
        return GrpcMetadata()
    }
    override val requiresTransportSecurity: Boolean = false
}

internal class CombinedCallCredentials(
    private val first: GrpcCallCredentials,
    private val second: GrpcCallCredentials
) : GrpcCallCredentials {
    override suspend fun GrpcCallCredentials.Context.getRequestMetadata(): GrpcMetadata {
        val firstMetadata = with(first) { getRequestMetadata() }
        val secondMetadata = with(second) { getRequestMetadata() }
        return firstMetadata + secondMetadata
    }

    override val requiresTransportSecurity: Boolean = first.requiresTransportSecurity || second.requiresTransportSecurity
}
