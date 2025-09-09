/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

public expect abstract class ClientCredentials
public expect abstract class ServerCredentials

public expect class InsecureClientCredentials : ClientCredentials
public expect class InsecureServerCredentials : ServerCredentials

public expect class TlsClientCredentials : ClientCredentials
public expect class TlsServerCredentials : ServerCredentials

public fun TlsClientCredentials(configure: TlsClientCredentialsBuilder.() -> Unit = {}): ClientCredentials {
    val builder = TlsClientCredentialsBuilder()
    builder.configure()
    return builder.build()
}

public fun TlsServerCredentials(
    certChain: String,
    privateKey: String,
    configure: TlsServerCredentialsBuilder.() -> Unit = {},
): ServerCredentials {
    val builder = TlsServerCredentialsBuilder(certChain, privateKey)
    builder.configure()
    return builder.build()
}

public interface TlsClientCredentialsBuilder {
    public fun trustManager(rootCertsPem: String): TlsClientCredentialsBuilder
    public fun keyManager(certChainPem: String, privateKeyPem: String): TlsClientCredentialsBuilder
    public fun build(): ClientCredentials
}

public enum class TlsClientAuth {
    /** Clients will not present any identity.  */
    NONE,

    /**
     * Clients are requested to present their identity, but clients without identities are
     * permitted.
     * Also, if the client certificate is provided but cannot be verified,
     * the client is permitted.
     */
    OPTIONAL,

    /**
     * Clients are requested to present their identity, and are required to provide a valid
     * identity.
     */
    REQUIRE
}

public interface TlsServerCredentialsBuilder {
    public fun trustManager(rootCertsPem: String): TlsServerCredentialsBuilder
    public fun clientAuth(clientAuth: TlsClientAuth): TlsServerCredentialsBuilder
    public fun build(): ServerCredentials
}

internal expect fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder
internal expect fun TlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): TlsServerCredentialsBuilder
