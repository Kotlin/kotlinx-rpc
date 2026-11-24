/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.utils.InternalForSubclassingRpcApi
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyWithoutEqualsHashCodeOrToString

/**
 * The credentials of a gRPC client. A value has one of two parts, or both:
 *
 * - [GrpcTransportCredentials]: the security of the connection, plaintext or TLS.
 * - [GrpcCallCredentials]: the authentication data that the client sends with each call.
 *
 * To make a value with both parts, use the [plus] operator:
 * ```kotlin
 * val credentials = GrpcTlsClientCredentials { ... } + BearerTokenCredentials(token)
 * ```
 *
 * @see GrpcClientConfiguration.credentials
 * @see callCredentials
 */
@SubclassOptInRequired(InternalForSubclassingRpcApi::class)
public interface GrpcClientCredentials

/**
 * The credentials that set the security of the connection to the gRPC server.
 *
 * Variations:
 * - [GrpcInsecureClientCredentials] - no transport security (plaintext).
 * - [GrpcTlsClientCredentials] - TLS, with optional mutual TLS (mTLS).
 */
@SubclassOptInRequired(InternalForSubclassingRpcApi::class)
public interface GrpcTransportCredentials : GrpcClientCredentials

/**
 * Returns credentials with the transport credentials of the receiver,
 * and the call credentials of the receiver followed by [other].
 *
 * ```kotlin
 * val client = GrpcClient("example.com", 443) {
 *     credentials = tls { trustManager(serverCertPem) } + BearerTokenCredentials("my-token")
 * }
 * ```
 *
 * @see combine
 */
public operator fun GrpcClientCredentials.plus(other: GrpcCallCredentials): GrpcClientCredentials {
    val transport = transportCredentials ?: return callCredentials + other
    return GrpcCombinedClientCredentials(transport, callCredentials + other)
}

/**
 * The call credentials of these credentials,
 * or [GrpcEmptyCallCredentials] when they have only transport credentials.
 *
 * Use this property for a `GrpcClient` factory that takes only call credentials:
 *
 * ```kotlin
 * val credentials = GrpcTlsClientCredentials { ... } + BearerTokenCredentials(token)
 * val bearerToken = credentials.callCredentials
 * ```
 */
public val GrpcClientCredentials.callCredentials: GrpcCallCredentials
    get() = when (this) {
        is GrpcCombinedClientCredentials -> call
        is GrpcCallCredentials -> this
        is GrpcTransportCredentials -> GrpcEmptyCallCredentials
        else -> internalError("Unknown client credentials type: $this")
    }

internal val GrpcClientCredentials.transportCredentials: GrpcTransportCredentials?
    get() = when (this) {
        is GrpcCombinedClientCredentials -> transport
        is GrpcTransportCredentials -> this
        is GrpcCallCredentials -> null
        else -> internalError("Unknown client credentials type: $this")
    }

/**
 * Adds call credentials to these credentials. The same as the [plus] operator.
 */
public fun GrpcClientCredentials.combine(other: GrpcCallCredentials): GrpcClientCredentials = this + other

/**
 * Plaintext credentials with no transport security.
 *
 * Use this credential type for unencrypted connections to gRPC servers. This should only
 * be used in development or testing environments or when connecting to services within
 * a secure network perimeter.
 *
 * ## Example
 *
 * ```kotlin
 * val client = GrpcClient("localhost", 9090) {
 *     credentials = plaintext()
 * }
 * ```
 *
 * **Warning**: Plaintext credentials transmit data without encryption. Do not use in production
 * for sensitive data or over untrusted networks.
 *
 * @see GrpcTlsClientCredentials
 */
public class GrpcInsecureClientCredentials : GrpcTransportCredentials

/**
 * TLS/SSL credentials for secure transport.
 *
 * TLS credentials establish an encrypted connection to the gRPC server using TLS/SSL.
 * This credential type supports both server authentication (standard TLS) and mutual TLS (mTLS)
 * where the client also authenticates to the server.
 *
 * ## Server Authentication (TLS)
 *
 * Verify the server's identity using a trust manager with root certificates:
 *
 * ```kotlin
 * val tlsCredentials = GrpcTlsClientCredentials {
 *     trustManager(serverCertPem)
 * }
 *
 * val client = GrpcClient("example.com", 443) {
 *     credentials = tlsCredentials
 * }
 * ```
 *
 * ## Mutual TLS (mTLS)
 *
 * For mutual authentication, provide both trust manager and key manager:
 *
 * ```kotlin
 * val credentials = GrpcTlsClientCredentials {
 *     trustManager(caCertPem)              // Server's CA certificate
 *     keyManager(clientCertPem, clientKeyPem)  // Client's certificate and private key
 * }
 * ```
 *
 * ## Default System Trust Store
 *
 * If no trust manager is configured, the system's default trust store is used:
 *
 * ```kotlin
 * val credentials = TlsClientCredentials { }  // Uses system CA certificates
 * ```
 *
 * **Note**: The server certificate's Common Name (CN) or Subject Alternative Name (SAN)
 * must match the authority specified in the client configuration, or the connection will fail.
 *
 * @see GrpcTlsClientCredentialsBuilder
 * @see GrpcInsecureClientCredentials
 */
@IntentionallyWithoutEqualsHashCodeOrToString(reason = ExemptionReason.API_DESIGN)
public class GrpcTlsClientCredentials(
    internal val configure: GrpcTlsClientCredentialsBuilder.() -> Unit = {}
) : GrpcTransportCredentials

/**
 * Builder for configuring [GrpcTlsClientCredentials].
 *
 * This builder provides methods to configure trust managers for server authentication
 * and key managers for client authentication (mTLS).
 *
 * @see GrpcTlsClientCredentials
 * @see trustManager
 * @see keyManager
 */
@SubclassOptInRequired(InternalForSubclassingRpcApi::class)
public interface GrpcTlsClientCredentialsBuilder {
    /**
     * Configures the trust manager with root CA certificates for server authentication.
     *
     * The trust manager validates the server's certificate chain. The provided root certificates
     * are used to verify that the server's certificate is signed by a trusted CA.
     *
     * If not specified, the system's default trust store is used.
     *
     * ## Example
     *
     * ```kotlin
     * TlsClientCredentials {
     *     trustManager("""
     *         -----BEGIN CERTIFICATE-----
     *         MIIDXTCCAkWgAwIBAgIJAKl...
     *         -----END CERTIFICATE-----
     *     """.trimIndent())
     * }
     * ```
     *
     * @param rootCertsPem PEM-encoded root CA certificates for validating the server's certificate.
     * @return This builder for chaining.
     * @see keyManager
     */
    public fun trustManager(rootCertsPem: String): GrpcTlsClientCredentialsBuilder

    /**
     * Configures the key manager with client certificate and private key for mutual TLS (mTLS).
     *
     * The key manager enables the client to authenticate itself to the server. This is required
     * when the server is configured to require or request client certificates.
     *
     * ## Example
     *
     * ```kotlin
     * TlsClientCredentials {
     *     trustManager(caCertPem)
     *     keyManager(
     *         certChainPem = """
     *             -----BEGIN CERTIFICATE-----
     *             MIIDXTCCAkWgAwIBAgIJAKl...
     *             -----END CERTIFICATE-----
     *         """.trimIndent(),
     *         privateKeyPem = """
     *             -----BEGIN PRIVATE KEY-----
     *             MIIEvQIBADANBgkqhkiG9w0...
     *             -----END PRIVATE KEY-----
     *         """.trimIndent()
     *     )
     * }
     * ```
     *
     * @param certChainPem PEM-encoded certificate chain for the client, starting with the client certificate.
     * @param privateKeyPem PEM-encoded private key corresponding to the client certificate.
     * @return This builder for chaining.
     * @see trustManager
     */
    public fun keyManager(certChainPem: String, privateKeyPem: String): GrpcTlsClientCredentialsBuilder
}

internal class GrpcCombinedClientCredentials(
    val transport: GrpcTransportCredentials,
    val call: GrpcCallCredentials,
) : GrpcClientCredentials
