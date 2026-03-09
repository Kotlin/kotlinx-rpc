/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

public actual typealias GrpcServerCredentials = io.grpc.ServerCredentials

public actual typealias GrpcInsecureServerCredentials = io.grpc.InsecureServerCredentials

public actual typealias GrpcTlsServerCredentials = io.grpc.TlsServerCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
internal actual fun createInsecureServerCredentials(): GrpcServerCredentials {
    return GrpcInsecureServerCredentials.create()
}

internal actual fun GrpcTlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): GrpcTlsServerCredentialsBuilder = JvmTlsServerCredentialBuilder(certChain, privateKey)

internal actual fun GrpcTlsServerCredentialsBuilder.build(): GrpcServerCredentials {
    return (this as JvmTlsServerCredentialBuilder).build()
}

private class JvmTlsServerCredentialBuilder(certChain: String, privateKey: String) : GrpcTlsServerCredentialsBuilder {
    private var sb = GrpcTlsServerCredentials.newBuilder()

    init {
        sb.keyManager(certChain.byteInputStream(), privateKey.byteInputStream())
    }

    override fun trustManager(rootCertsPem: String): GrpcTlsServerCredentialsBuilder {
        sb.trustManager(rootCertsPem.byteInputStream())
        return this
    }

    override fun clientAuth(clientAuth: GrpcTlsClientAuth): GrpcTlsServerCredentialsBuilder {
        sb.clientAuth(clientAuth.toJava())
        return this
    }


    fun build(): GrpcServerCredentials {
        return sb.build()
    }
}

private fun GrpcTlsClientAuth.toJava(): io.grpc.TlsServerCredentials.ClientAuth = when (this) {
    GrpcTlsClientAuth.NONE -> io.grpc.TlsServerCredentials.ClientAuth.NONE
    GrpcTlsClientAuth.OPTIONAL -> io.grpc.TlsServerCredentials.ClientAuth.OPTIONAL
    GrpcTlsClientAuth.REQUIRE -> io.grpc.TlsServerCredentials.ClientAuth.REQUIRE
}
