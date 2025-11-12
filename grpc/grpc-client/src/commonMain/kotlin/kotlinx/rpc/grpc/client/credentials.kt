/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Base class for client channel credentials.
 *
 * Client credentials define the security mechanism used to establish a connection to the gRPC server.
 * Unlike [GrpcCallCredentials] which operate at the application layer for per-call authentication,
 * client credentials establish the transport layer security.
 *
 * ## Types of Credentials
 *
 * - **[InsecureClientCredentials]**: No transport security (plaintext)
 * - **[TlsClientCredentials]**: TLS/SSL transport security with optional mutual TLS (mTLS)
 *
 * Client credentials can be combined with call credentials using the [plus] operator:
 * ```kotlin
 * val credentials = TlsClientCredentials { ... } + BearerTokenCredentials(token)
 * ```
 *
 * @see TlsClientCredentials
 * @see InsecureClientCredentials
 * @see GrpcCallCredentials
 */
public expect abstract class ClientCredentials

/**
 * Combines client credentials with call credentials.
 *
 * This operator allows attaching per-call authentication credentials to a client credential,
 * enabling both transport security (via [ClientCredentials]) and application-layer authentication
 * (via [GrpcCallCredentials]) to be used together for all requests of the client.
 *
 * ## Example
 *
 * ```kotlin
 * val tlsCredentials = TlsClientCredentials {
 *     trustManager(serverCertPem)
 * }
 * val bearerToken = BearerTokenCredentials("my-token")
 * val combined = tlsCredentials + bearerToken
 *
 * val client = GrpcClient("example.com", 443) {
 *     credentials = combined
 * }
 * ```
 *
 * @param other The call credentials to combine with this client credential.
 * @return A new credential that includes both client and call credentials.
 * @see combine
 * @see GrpcCallCredentials
 */
public expect operator fun ClientCredentials.plus(other: GrpcCallCredentials): ClientCredentials

/**
 * Combines client credentials with call credentials.
 *
 * This is an alias for the [plus] operator, providing a more explicit method name
 * for combining credentials.
 *
 * @param other The call credentials to combine with this client credential.
 * @return A new credential that includes both client and call credentials.
 * @see plus
 */
public fun ClientCredentials.combine(other: GrpcCallCredentials): ClientCredentials = this + other


/**
 * Plaintext credentials with no transport security.
 *
 * Use this credential type for unencrypted connections to gRPC servers. This should only
 * be used in development or testing environments, or when connecting to services within
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
 * @see TlsClientCredentials
 */
public expect class InsecureClientCredentials : ClientCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
@InternalRpcApi
public expect fun createInsecureClientCredentials(): ClientCredentials

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
 * val credentials = TlsClientCredentials {
 *     trustManager(serverCertPem)
 * }
 *
 * val client = GrpcClient("example.com", 443) {
 *     credentials = credentials
 * }
 * ```
 *
 * ## Mutual TLS (mTLS)
 *
 * For mutual authentication, provide both trust manager and key manager:
 *
 * ```kotlin
 * val credentials = TlsClientCredentials {
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
 * @see TlsClientCredentialsBuilder
 * @see InsecureClientCredentials
 */
public expect class TlsClientCredentials : ClientCredentials

/**
 * Creates TLS client credentials using a builder DSL.
 *
 * This is the primary way to create [TlsClientCredentials], providing a convenient
 * builder interface for configuring trust managers and key managers.
 *
 * ## Examples
 *
 * Simple TLS with server authentication:
 * ```kotlin
 * val credentials = TlsClientCredentials {
 *     trustManager(serverCertPem)
 * }
 * ```
 *
 * Mutual TLS (mTLS):
 * ```kotlin
 * val credentials = TlsClientCredentials {
 *     trustManager(caCertPem)
 *     keyManager(clientCertPem, clientKeyPem)
 * }
 * ```
 *
 * Using system trust store:
 * ```kotlin
 * val credentials = TlsClientCredentials { }  // No configuration needed
 * ```
 *
 * @param configure A lambda to configure the TLS credentials using [TlsClientCredentialsBuilder].
 * @return Configured TLS client credentials.
 * @see TlsClientCredentialsBuilder
 */
public fun TlsClientCredentials(configure: TlsClientCredentialsBuilder.() -> Unit = {}): ClientCredentials {
    val builder = TlsClientCredentialsBuilder()
    builder.configure()
    return builder.build()
}

/**
 * Builder for configuring [TlsClientCredentials].
 *
 * This builder provides methods to configure trust managers for server authentication
 * and key managers for client authentication (mTLS).
 *
 * @see TlsClientCredentials
 * @see trustManager
 * @see keyManager
 */
public interface TlsClientCredentialsBuilder {
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
    public fun trustManager(rootCertsPem: String): TlsClientCredentialsBuilder

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
    public fun keyManager(certChainPem: String, privateKeyPem: String): TlsClientCredentialsBuilder
}

internal expect fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder

internal expect fun TlsClientCredentialsBuilder.build(): ClientCredentials
