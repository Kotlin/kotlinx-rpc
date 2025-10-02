/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi

public actual typealias ClientCredentials = io.grpc.ChannelCredentials

public actual typealias InsecureClientCredentials = io.grpc.InsecureChannelCredentials

public actual typealias TlsClientCredentials = io.grpc.TlsChannelCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
@InternalRpcApi
public actual fun createInsecureClientCredentials(): ClientCredentials {
    return InsecureClientCredentials.create()
}

internal actual fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder = JvmTlsCLientCredentialBuilder()
internal actual fun TlsClientCredentialsBuilder.build(): ClientCredentials {
    return (this as JvmTlsCLientCredentialBuilder).build()
}

private class JvmTlsCLientCredentialBuilder : TlsClientCredentialsBuilder {
    private var cb = TlsClientCredentials.newBuilder()


    override fun trustManager(rootCertsPem: String): TlsClientCredentialsBuilder {
        cb.trustManager(rootCertsPem.byteInputStream())
        return this
    }

    override fun keyManager(
        certChainPem: String,
        privateKeyPem: String,
    ): TlsClientCredentialsBuilder {
        cb.keyManager(certChainPem.byteInputStream(), privateKeyPem.byteInputStream())
        return this
    }

    fun build(): ClientCredentials {
        return cb.build()
    }
}
