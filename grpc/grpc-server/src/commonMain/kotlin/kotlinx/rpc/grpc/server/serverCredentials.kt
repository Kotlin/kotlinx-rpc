/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

import kotlinx.rpc.grpc.TlsClientAuth


public expect abstract class ServerCredentials
public expect class InsecureServerCredentials : ServerCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
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

public interface TlsServerCredentialsBuilder {
    public fun trustManager(rootCertsPem: String): TlsServerCredentialsBuilder
    public fun clientAuth(clientAuth: TlsClientAuth): TlsServerCredentialsBuilder
}

internal expect fun TlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): TlsServerCredentialsBuilder

internal expect fun TlsServerCredentialsBuilder.build(): ServerCredentials
