/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.server

import cnames.structs.grpc_server_credentials
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.internal.ResourceGuard
import kotlinx.rpc.grpc.internal.TlsCredentialsOptionsBuilder
import kotlinx.rpc.grpc.internal.cinterop.grpc_insecure_server_credentials_create
import kotlinx.rpc.grpc.internal.cinterop.grpc_server_credentials_release
import kotlinx.rpc.grpc.internal.cinterop.grpc_ssl_client_certificate_request_type
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_credentials_options_destroy
import kotlinx.rpc.grpc.internal.cinterop.grpc_tls_server_credentials_create
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

public actual abstract class GrpcServerCredentials internal constructor(
    internal val raw: CPointer<grpc_server_credentials>,
) {
    // Guards the application-owned +1 ref on grpc_server_credentials against double-free between
    // the explicit release performed by NativeServer.dispose() and the GC cleaner fallback.
    // KRPC-591.
    internal val rawGuard = ResourceGuard()

    @Suppress("unused")
    internal val rawCleaner = createCleaner(Pair(raw, rawGuard)) { (ptr, guard) ->
        if (guard.released.compareAndSet(expect = false, update = true)) {
            grpc_server_credentials_release(ptr)
        }
    }

    /**
     * Releases the application-owned +1 ref on [raw] deterministically. Safe to call multiple
     * times — the guard ensures the underlying `grpc_server_credentials_release` runs at most
     * once, after which the GC cleaner becomes a no-op. Called from `NativeServer.dispose()`
     * after `grpc_server_destroy` so the ref is gone before `grpc_shutdown` can run.
     */
    internal fun releaseRaw() {
        if (rawGuard.released.compareAndSet(expect = false, update = true)) {
            grpc_server_credentials_release(raw)
        }
    }
}

public actual class GrpcInsecureServerCredentials internal constructor(
    raw: CPointer<grpc_server_credentials>,
) : GrpcServerCredentials(raw)

public actual class GrpcTlsServerCredentials internal constructor(
    raw: CPointer<grpc_server_credentials>,
) : GrpcServerCredentials(raw)

internal actual fun createInsecureServerCredentials(): GrpcServerCredentials {
    return GrpcInsecureServerCredentials(
        grpc_insecure_server_credentials_create() ?: error("grpc_insecure_server_credentials_create() returned null")
    )
}

internal actual fun GrpcTlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
): GrpcTlsServerCredentialsBuilder = NativeTlsServerCredentialsBuilder(certChain, privateKey)

internal actual fun GrpcTlsServerCredentialsBuilder.build(): GrpcServerCredentials {
    return (this as NativeTlsServerCredentialsBuilder).build()
}

private class NativeTlsServerCredentialsBuilder(
    certChain: String,
    privateKey: String,
) : GrpcTlsServerCredentialsBuilder {
    var optionsBuilder = TlsCredentialsOptionsBuilder()

    init {
        optionsBuilder.keyManager(certChain, privateKey)
    }

    override fun trustManager(rootCertsPem: String): GrpcTlsServerCredentialsBuilder {
        optionsBuilder.trustManager(rootCertsPem)
        return this
    }

    override fun clientAuth(clientAuth: GrpcTlsClientAuth): GrpcTlsServerCredentialsBuilder {
        optionsBuilder.clientAuth(clientAuth.toRaw())
        return this
    }

    fun build(): GrpcTlsServerCredentials {
        val opts = optionsBuilder.build()
        val creds = grpc_tls_server_credentials_create(opts)
            ?: run {
                grpc_tls_credentials_options_destroy(opts);
                error("TLS server credential creation failed")
            }
        return GrpcTlsServerCredentials(creds)
    }
}

private fun GrpcTlsClientAuth.toRaw(): grpc_ssl_client_certificate_request_type =
    when (this) {
        GrpcTlsClientAuth.NONE ->
            grpc_ssl_client_certificate_request_type.GRPC_SSL_DONT_REQUEST_CLIENT_CERTIFICATE
        GrpcTlsClientAuth.OPTIONAL ->
            grpc_ssl_client_certificate_request_type
                .GRPC_SSL_REQUEST_CLIENT_CERTIFICATE_BUT_DONT_VERIFY
        GrpcTlsClientAuth.REQUIRE ->
            grpc_ssl_client_certificate_request_type
                .GRPC_SSL_REQUEST_AND_REQUIRE_CLIENT_CERTIFICATE_AND_VERIFY
    }
