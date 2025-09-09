/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual typealias ClientCredentials = io.grpc.ChannelCredentials

public actual typealias ServerCredentials = io.grpc.ServerCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
public actual typealias InsecureClientCredentials = io.grpc.InsecureChannelCredentials
public actual typealias InsecureServerCredentials = io.grpc.InsecureServerCredentials

public actual typealias TlsClientCredentials = io.grpc.TlsChannelCredentials
public actual typealias TlsServerCredentials = io.grpc.TlsServerCredentials

internal actual fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder =
    object : TlsClientCredentialsBuilder {
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

        override fun build(): ClientCredentials {
            return cb.build()
        }
    }

internal actual fun TlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): TlsServerCredentialsBuilder = object : TlsServerCredentialsBuilder {
    private var sb = TlsServerCredentials.newBuilder()

    override fun trustManager(rootCertsPem: String): TlsServerCredentialsBuilder {
        sb.trustManager(rootCertsPem.byteInputStream())
        return this
    }

    override fun clientAuth(clientAuth: TlsClientAuth): TlsServerCredentialsBuilder {
        sb.clientAuth(clientAuth.toJava())
        return this
    }

    init {
        sb.keyManager(certChain.byteInputStream(), privateKey.byteInputStream())
    }

    override fun build(): ServerCredentials {
        return sb.build()
    }
}

private fun TlsClientAuth.toJava(): io.grpc.TlsServerCredentials.ClientAuth = when (this) {
    TlsClientAuth.NONE -> io.grpc.TlsServerCredentials.ClientAuth.NONE
    TlsClientAuth.OPTIONAL -> io.grpc.TlsServerCredentials.ClientAuth.OPTIONAL
    TlsClientAuth.REQUIRE -> io.grpc.TlsServerCredentials.ClientAuth.REQUIRE
}


