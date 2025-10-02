/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc

import cnames.structs.grpc_tls_credentials_options
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import libkgrpc.grpc_ssl_client_certificate_request_type
import libkgrpc.grpc_tls_certificate_provider_release
import libkgrpc.grpc_tls_certificate_provider_static_data_create
import libkgrpc.grpc_tls_credentials_options_create
import libkgrpc.grpc_tls_credentials_options_set_cert_request_type
import libkgrpc.grpc_tls_credentials_options_set_certificate_provider
import libkgrpc.grpc_tls_credentials_options_watch_identity_key_cert_pairs
import libkgrpc.grpc_tls_credentials_options_watch_root_certs
import libkgrpc.grpc_tls_identity_pairs_add_pair
import libkgrpc.grpc_tls_identity_pairs_create
import kotlin.experimental.ExperimentalNativeApi

@InternalRpcApi
public class TlsCredentialsOptionsBuilder {
    private var roots: String? = null
    private var cert: String? = null
    private var key: String? = null

    private var clientAuth: grpc_ssl_client_certificate_request_type? = null

    public fun trustManager(rootCertsPem: String) {
        roots = rootCertsPem
    }

    public fun keyManager(certChainPem: String, privateKeyPem: String) {
        cert = certChainPem; key = privateKeyPem
    }

    public fun clientAuth(clientAuth: grpc_ssl_client_certificate_request_type) {
        this.clientAuth = clientAuth
    }

    public fun build(): CPointer<grpc_tls_credentials_options> {
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
        if (clientAuth != null) grpc_tls_credentials_options_set_cert_request_type(opts, clientAuth)

        return opts
    }
}
