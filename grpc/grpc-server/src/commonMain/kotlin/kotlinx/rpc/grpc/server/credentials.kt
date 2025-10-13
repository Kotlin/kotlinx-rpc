/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

public expect abstract class ServerCredentials

public expect class InsecureServerCredentials : ServerCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
internal expect fun createInsecureServerCredentials(): ServerCredentials

public expect class TlsServerCredentials : ServerCredentials

public fun TlsServerCredentials(
    certChain: String,
    privateKey: String,
    configure: TlsServerCredentialsBuilder.() -> Unit = {},
): ServerCredentials {
    val builder = TlsServerCredentialsBuilder(certChain, privateKey)
    builder.configure()
    return builder.build()
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
}

internal expect fun TlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): TlsServerCredentialsBuilder

internal expect fun TlsServerCredentialsBuilder.build(): ServerCredentials
