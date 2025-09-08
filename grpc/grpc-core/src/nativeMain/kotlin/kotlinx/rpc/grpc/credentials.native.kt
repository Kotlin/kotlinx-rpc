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
import libkgrpc.grpc_tls_certificate_provider_release
import libkgrpc.grpc_tls_certificate_provider_static_data_create
import libkgrpc.grpc_tls_credentials_create
import libkgrpc.grpc_tls_credentials_options_create
import libkgrpc.grpc_tls_credentials_options_destroy
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

public actual fun TlsChannelCredentials(): ChannelCredentials {
    val raw = grpc_tls_credentials_options_create()?.let {
        grpc_tls_credentials_create(it)
    } ?: error("Failed to create TLS credentials")
    return TlsChannelCredentials(raw)
}

public actual fun TlsServerCredentials(
    certChain: String,
    privateKey: String,
): ServerCredentials {
    return TlsServerCredentialsBuilder().keyManager(certChain, privateKey).build()
}

public actual fun TlsChannelCredentialsBuilder(): TlsChannelCredentialsBuilder = object : TlsChannelCredentialsBuilder {
    var optionsBuilder = TlsCredentialsOptionsBuilder()

    override fun trustManager(rootCertsPem: String): TlsChannelCredentialsBuilder {
        optionsBuilder.trustManager(rootCertsPem)
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

public actual fun TlsServerCredentialsBuilder(): TlsServerCredentialsBuilder = object : TlsServerCredentialsBuilder {
    var optionsBuilder = TlsCredentialsOptionsBuilder()

    override fun keyManager(certChainPem: String, privateKeyPem: String): TlsServerCredentialsBuilder {
        optionsBuilder.keyManager(certChainPem, privateKeyPem)
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

    fun trustManager(rootCertsPem: String) = apply { roots = rootCertsPem }
    fun keyManager(certChainPem: String, privateKeyPem: String) = apply {
        cert = certChainPem; key = privateKeyPem
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

        return opts
    }
}
