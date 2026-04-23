/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

/**
 * Credentials used by a gRPC server to accept incoming connections. The same instance may be
 * safely passed to multiple [GrpcServer]s.
 */
public expect abstract class GrpcServerCredentials

public expect class GrpcInsecureServerCredentials : GrpcServerCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
internal expect fun createInsecureServerCredentials(): GrpcServerCredentials

public expect class GrpcTlsServerCredentials : GrpcServerCredentials

@Suppress("FunctionName")
public fun GrpcTlsServerCredentials(
    certChain: String,
    privateKey: String,
    configure: GrpcTlsServerCredentialsBuilder.() -> Unit = {},
): GrpcServerCredentials {
    val builder = GrpcTlsServerCredentialsBuilder(certChain, privateKey)
    builder.configure()
    return builder.build()
}

public enum class GrpcTlsClientAuth {
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

public interface GrpcTlsServerCredentialsBuilder {
    public fun trustManager(rootCertsPem: String): GrpcTlsServerCredentialsBuilder
    public fun clientAuth(clientAuth: GrpcTlsClientAuth): GrpcTlsServerCredentialsBuilder
}

internal expect fun GrpcTlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): GrpcTlsServerCredentialsBuilder

internal expect fun GrpcTlsServerCredentialsBuilder.build(): GrpcServerCredentials
