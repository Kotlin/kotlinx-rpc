/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

public expect abstract class ChannelCredentials
public expect abstract class ServerCredentials

public expect class InsecureChannelCredentials : ChannelCredentials
public expect class InsecureServerCredentials : ServerCredentials

public expect class TlsChannelCredentials : ChannelCredentials
public expect class TlsServerCredentials : ServerCredentials

public fun TlsChannelCredentials(configure: TlsChannelCredentialsBuilder.() -> Unit = {}): ChannelCredentials {
    val builder = TlsChannelCredentialsBuilder()
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

public interface TlsChannelCredentialsBuilder {
    public fun trustManager(rootCertsPem: String): TlsChannelCredentialsBuilder
    public fun keyManager(certChainPem: String, privateKeyPem: String): TlsChannelCredentialsBuilder
    public fun build(): ChannelCredentials
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

internal expect fun TlsChannelCredentialsBuilder(): TlsChannelCredentialsBuilder
internal expect fun TlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): TlsServerCredentialsBuilder
