/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc

import cnames.structs.grpc_channel_credentials
import cnames.structs.grpc_server_credentials
import cnames.structs.grpc_tls_credentials_options
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import libkgrpc.grpc_channel_credentials_release
import libkgrpc.grpc_insecure_credentials_create
import libkgrpc.grpc_insecure_server_credentials_create
import libkgrpc.grpc_server_credentials_release
import libkgrpc.grpc_ssl_client_certificate_request_type
import libkgrpc.grpc_tls_certificate_provider_release
import libkgrpc.grpc_tls_certificate_provider_static_data_create
import libkgrpc.grpc_tls_credentials_create
import libkgrpc.grpc_tls_credentials_options_create
import libkgrpc.grpc_tls_credentials_options_destroy
import libkgrpc.grpc_tls_credentials_options_set_cert_request_type
import libkgrpc.grpc_tls_credentials_options_set_certificate_provider
import libkgrpc.grpc_tls_credentials_options_watch_identity_key_cert_pairs
import libkgrpc.grpc_tls_credentials_options_watch_root_certs
import libkgrpc.grpc_tls_identity_pairs_add_pair
import libkgrpc.grpc_tls_identity_pairs_create
import libkgrpc.grpc_tls_server_credentials_create
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

public actual abstract class ChannelCredentials internal constructor(
    internal val raw: CPointer<grpc_channel_credentials>,
) {
    @Suppress("unused")
    internal val rawCleaner = createCleaner(raw) {
        grpc_channel_credentials_release(it)
    }
}

public actual abstract class ServerCredentials internal constructor(
    internal val raw: CPointer<grpc_server_credentials>,
) {
    @Suppress("unused")
    internal val rawCleaner = createCleaner(raw) {
        grpc_server_credentials_release(it)
    }
}

public actual class InsecureChannelCredentials internal constructor(
    raw: CPointer<grpc_channel_credentials>,
) : ChannelCredentials(raw)

public actual class InsecureServerCredentials internal constructor(
    raw: CPointer<grpc_server_credentials>,
) : ServerCredentials(raw)

public actual class TlsChannelCredentials internal constructor(
    raw: CPointer<grpc_channel_credentials>,
) : ChannelCredentials(raw)

public actual class TlsServerCredentials(
    raw: CPointer<grpc_server_credentials>,
) : ServerCredentials(raw)


public fun InsecureChannelCredentials(): ChannelCredentials {
    return InsecureChannelCredentials(
        grpc_insecure_credentials_create() ?: error("grpc_insecure_credentials_create() returned null")
    )
}

public fun InsecureServerCredentials(): ServerCredentials {
    return InsecureServerCredentials(
        grpc_insecure_server_credentials_create() ?: error("grpc_insecure_server_credentials_create() returned null")
    )
}

internal actual fun TlsChannelCredentialsBuilder(): TlsChannelCredentialsBuilder =
    object : TlsChannelCredentialsBuilder {
        var optionsBuilder = TlsCredentialsOptionsBuilder()

        override fun trustManager(rootCertsPem: String): TlsChannelCredentialsBuilder {
            optionsBuilder.trustManager(rootCertsPem)
            return this
        }

        override fun keyManager(
            certChainPem: String,
            privateKeyPem: String,
        ): TlsChannelCredentialsBuilder {
            optionsBuilder.keyManager(certChainPem, privateKeyPem)
            return this
        }

        override fun build(): ChannelCredentials {
            val opts = optionsBuilder.build()
            val creds = grpc_tls_credentials_create(opts)
                ?: run {
                    grpc_tls_credentials_options_destroy(opts);
                    error("TLS channel credential creation failed")
                }
            return TlsChannelCredentials(creds)
        }
    }

internal actual fun TlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): TlsServerCredentialsBuilder = object : TlsServerCredentialsBuilder {
    var optionsBuilder = TlsCredentialsOptionsBuilder()

    init {
        optionsBuilder.keyManager(certChain, privateKey)
    }

    override fun trustManager(rootCertsPem: String): TlsServerCredentialsBuilder {
        optionsBuilder.trustManager(rootCertsPem)
        return this
    }

    override fun clientAuth(clientAuth: TlsClientAuth): TlsServerCredentialsBuilder {
        optionsBuilder.clientAuth(clientAuth)
        return this
    }

    override fun build(): TlsServerCredentials {
        val opts = optionsBuilder.build()
        val creds = grpc_tls_server_credentials_create(opts)
            ?: run {
                grpc_tls_credentials_options_destroy(opts);
                error("TLS server credential creation failed")
            }
        return TlsServerCredentials(creds)
    }
}


private class TlsCredentialsOptionsBuilder {
    private var roots: String? = null
    private var cert: String? = null
    private var key: String? = null

    private var clientAuth: TlsClientAuth? = null

    fun trustManager(rootCertsPem: String) {
        roots = rootCertsPem
    }

    fun keyManager(certChainPem: String, privateKeyPem: String) = apply {
        cert = certChainPem; key = privateKeyPem
    }

    fun clientAuth(clientAuth: TlsClientAuth) {
        this.clientAuth = clientAuth
    }

    fun build(): CPointer<grpc_tls_credentials_options> {
        val opts = grpc_tls_credentials_options_create() ?: error("alloc opts failed")

        val pairs = if (cert != null && key != null) {
            val p = grpc_tls_identity_pairs_create() ?: error("pairs alloc failed")
            grpc_tls_identity_pairs_add_pair(p, key, cert);
            p
        } else null

        if (roots != null || pairs != null) {
            val provider = grpc_tls_certificate_provider_static_data_create(
                roots, pairs
            ) ?: error("provider alloc failed")
            grpc_tls_credentials_options_set_certificate_provider(opts, provider)
            grpc_tls_certificate_provider_release(provider)
        }


        if (pairs != null) grpc_tls_credentials_options_watch_identity_key_cert_pairs(opts)
        if (roots != null) grpc_tls_credentials_options_watch_root_certs(opts)

        val clientAuth = clientAuth
        if (clientAuth != null) grpc_tls_credentials_options_set_cert_request_type(opts, clientAuth.toRaw())

        return opts
    }
}

private fun TlsClientAuth.toRaw(): grpc_ssl_client_certificate_request_type = when (this) {
    TlsClientAuth.NONE -> grpc_ssl_client_certificate_request_type.GRPC_SSL_DONT_REQUEST_CLIENT_CERTIFICATE
    TlsClientAuth.OPTIONAL -> grpc_ssl_client_certificate_request_type.GRPC_SSL_REQUEST_CLIENT_CERTIFICATE_AND_VERIFY
    TlsClientAuth.REQUIRE -> grpc_ssl_client_certificate_request_type.GRPC_SSL_REQUEST_AND_REQUIRE_CLIENT_CERTIFICATE_AND_VERIFY
}