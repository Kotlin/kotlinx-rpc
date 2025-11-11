/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.internal.utils.InternalRpcApi

public expect abstract class ClientCredentials

public expect operator fun ClientCredentials.plus(other: GrpcCallCredentials): ClientCredentials

public fun ClientCredentials.combine(other: GrpcCallCredentials): ClientCredentials = this + other


public expect class InsecureClientCredentials : ClientCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
@InternalRpcApi
public expect fun createInsecureClientCredentials(): ClientCredentials

public expect class TlsClientCredentials : ClientCredentials

public fun TlsClientCredentials(configure: TlsClientCredentialsBuilder.() -> Unit = {}): ClientCredentials {
    val builder = TlsClientCredentialsBuilder()
    builder.configure()
    return builder.build()
}

public interface TlsClientCredentialsBuilder {
    public fun trustManager(rootCertsPem: String): TlsClientCredentialsBuilder
    public fun keyManager(certChainPem: String, privateKeyPem: String): TlsClientCredentialsBuilder
}

internal expect fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder

internal expect fun TlsClientCredentialsBuilder.build(): ClientCredentials
