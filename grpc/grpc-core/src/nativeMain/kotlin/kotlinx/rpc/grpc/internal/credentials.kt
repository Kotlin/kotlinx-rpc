/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_tls_credentials_options
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.grpc.internal.cinterop.grpc_ssl_client_certificate_request_type
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_certificate_provider_in_memory_create
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_certificate_provider_in_memory_set_identity_certificate
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_certificate_provider_in_memory_set_root_certificate
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_certificate_provider_release
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_credentials_options_create
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_credentials_options_set_cert_request_type
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_credentials_options_set_identity_certificate_provider
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_credentials_options_set_root_certificate_provider
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_identity_pairs_add_pair
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_identity_pairs_create
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
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

        val provider = grpc_tls_certificate_provider_in_memory_create()
            ?: error("provider alloc failed")

        if (roots != null) {
            check(grpc_tls_certificate_provider_in_memory_set_root_certificate(provider, roots)) {
                "Failed to set root certificate on in-memory provider"
            }
            grpc_tls_credentials_options_set_root_certificate_provider(opts, provider)
        }

        if (cert != null && key != null) {
            val pairs = grpc_tls_identity_pairs_create() ?: error("pairs alloc failed")
            grpc_tls_identity_pairs_add_pair(pairs, key, cert)
            check(grpc_tls_certificate_provider_in_memory_set_identity_certificate(provider, pairs)) {
                "Failed to set identity certificate on in-memory provider"
            }
            // pairs ownership is transferred to set_identity_certificate
            grpc_tls_credentials_options_set_identity_certificate_provider(opts, provider)
        }

        grpc_tls_certificate_provider_release(provider)

        val clientAuth = clientAuth
        if (clientAuth != null) grpc_tls_credentials_options_set_cert_request_type(opts, clientAuth)

        return opts
    }
}
