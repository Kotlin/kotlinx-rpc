/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual typealias ChannelCredentials = io.grpc.ChannelCredentials

public actual typealias ServerCredentials = io.grpc.ServerCredentials


// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
public actual typealias InsecureChannelCredentials = io.grpc.InsecureChannelCredentials
public actual typealias InsecureServerCredentials = io.grpc.InsecureServerCredentials

public actual typealias TlsServerCredentials = io.grpc.TlsServerCredentials
public actual typealias TlsChannelCredentials = io.grpc.TlsChannelCredentials

public actual fun TlsChannelCredentials(): ChannelCredentials {
    return TlsChannelCredentialsBuilder().build()
}

public actual fun TlsServerCredentials(
    certChain: String,
    privateKey: String,
): ServerCredentials {
    return TlsServerCredentialsBuilder().keyManager(certChain, privateKey).build()
}

public actual fun TlsServerCredentialsBuilder(): TlsServerCredentialsBuilder = object : TlsServerCredentialsBuilder {
    private var sb = TlsServerCredentials.newBuilder()

    override fun keyManager(
        certChainPem: String,
        privateKeyPem: String,
    ): TlsServerCredentialsBuilder {
        sb.keyManager(certChainPem.byteInputStream(), privateKeyPem.byteInputStream())
        return this
    }

    override fun build(): ServerCredentials {
        return sb.build()
    }
}

public actual fun TlsChannelCredentialsBuilder(): TlsChannelCredentialsBuilder = object : TlsChannelCredentialsBuilder {
    private var cb = TlsChannelCredentials.newBuilder()

    override fun trustManager(rootCertsPem: String): TlsChannelCredentialsBuilder {
        cb.trustManager(rootCertsPem.byteInputStream())
        return this
    }

    override fun build(): ChannelCredentials {
        return cb.build()
    }
}


