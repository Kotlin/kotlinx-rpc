/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server


public actual typealias ServerCredentials = io.grpc.ServerCredentials

public actual typealias InsecureServerCredentials = io.grpc.InsecureServerCredentials

public actual typealias TlsServerCredentials = io.grpc.TlsServerCredentials


// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
internal actual fun createInsecureServerCredentials(): ServerCredentials {
    return InsecureServerCredentials.create()
}

internal actual fun TlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): TlsServerCredentialsBuilder = JvmTlsServerCredentialBuilder(certChain, privateKey)

internal actual fun TlsServerCredentialsBuilder.build(): ServerCredentials {
    return (this as JvmTlsServerCredentialBuilder).build()
}

private class JvmTlsServerCredentialBuilder(certChain: String, privateKey: String) : TlsServerCredentialsBuilder {
    private var sb = TlsServerCredentials.newBuilder()

    init {
        sb.keyManager(certChain.byteInputStream(), privateKey.byteInputStream())
    }

    override fun trustManager(rootCertsPem: String): TlsServerCredentialsBuilder {
        sb.trustManager(rootCertsPem.byteInputStream())
        return this
    }

    override fun clientAuth(clientAuth: TlsClientAuth): TlsServerCredentialsBuilder {
        sb.clientAuth(clientAuth.toJava())
        return this
    }


    fun build(): ServerCredentials {
        return sb.build()
    }
}


private fun TlsClientAuth.toJava(): io.grpc.TlsServerCredentials.ClientAuth = when (this) {
    TlsClientAuth.NONE -> io.grpc.TlsServerCredentials.ClientAuth.NONE
    TlsClientAuth.OPTIONAL -> io.grpc.TlsServerCredentials.ClientAuth.OPTIONAL
    TlsClientAuth.REQUIRE -> io.grpc.TlsServerCredentials.ClientAuth.REQUIRE
}