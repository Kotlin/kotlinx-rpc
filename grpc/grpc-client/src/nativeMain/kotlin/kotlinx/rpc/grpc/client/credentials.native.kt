/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.client

import cnames.structs.grpc_channel_credentials
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.rpc.grpc.internal.TlsCredentialsOptionsBuilder
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.utils.InternalRpcApi
import libkgrpc.grpc_call_credentials_release
import libkgrpc.grpc_channel_credentials_release
import libkgrpc.grpc_composite_channel_credentials_create
import libkgrpc.grpc_insecure_credentials_create
import libkgrpc.grpc_tls_credentials_create
import libkgrpc.grpc_tls_credentials_options_destroy
import kotlin.experimental.ExperimentalNativeApi

internal fun GrpcClientCredentials.createRaw(): CPointer<grpc_channel_credentials> {
    return when (this) {
        is GrpcCombinedClientCredentials ->
            // we don't create a composite credential, as we collect them and apply them on every call
            clientCredentials.createRaw()
        is GrpcInsecureClientCredentials -> grpc_insecure_credentials_create() ?: error("grpc_insecure_credentials_create() returned null")
        is GrpcTlsClientCredentials -> NativeTlsClientCredentialsBuilder().apply(configure).build()
    }
}

internal class NativeTlsClientCredentialsBuilder : GrpcTlsClientCredentialsBuilder {
    var optionsBuilder = TlsCredentialsOptionsBuilder()

    override fun trustManager(rootCertsPem: String): GrpcTlsClientCredentialsBuilder {
        optionsBuilder.trustManager(rootCertsPem)
        return this
    }

    override fun keyManager(
        certChainPem: String,
        privateKeyPem: String,
    ): GrpcTlsClientCredentialsBuilder {
        optionsBuilder.keyManager(certChainPem, privateKeyPem)
        return this
    }

    fun build(): CPointer<grpc_channel_credentials> {
        val opts = optionsBuilder.build()
        return grpc_tls_credentials_create(opts)
            ?: run {
                grpc_tls_credentials_options_destroy(opts);
                error("TLS channel credential creation failed")
            }
    }
}
