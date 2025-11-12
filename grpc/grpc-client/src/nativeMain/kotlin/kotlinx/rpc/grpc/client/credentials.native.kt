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

public actual abstract class ClientCredentials {
    internal abstract val clientCredentials: ClientCredentials
    internal abstract val callCredentials: GrpcCallCredentials?

    internal abstract fun createRaw(parentJob: Job, coroutineDispatcher: CoroutineDispatcher): CPointer<grpc_channel_credentials>
}

public actual class InsecureClientCredentials : ClientCredentials() {
    override val clientCredentials: ClientCredentials
        get() = this
    override val callCredentials: GrpcCallCredentials?
        get() = null

    override fun createRaw(parentJob: Job, coroutineDispatcher: CoroutineDispatcher): CPointer<grpc_channel_credentials> {
        return grpc_insecure_credentials_create() ?: error("grpc_insecure_credentials_create() returned null")
    }
}

public actual class TlsClientCredentials internal constructor(
    internal var builder: NativeTlsClientCredentialsBuilder,
) : ClientCredentials() {

    override val clientCredentials: ClientCredentials
        get() = this
    override val callCredentials: GrpcCallCredentials?
        get() = null

    override fun createRaw(parentJob: Job, coroutineDispatcher: CoroutineDispatcher): CPointer<grpc_channel_credentials> {
        return builder.build()
    }
}

@InternalRpcApi
public actual fun createInsecureClientCredentials(): ClientCredentials {
    return InsecureClientCredentials()
}

internal actual fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder = NativeTlsClientCredentialsBuilder()
internal actual fun TlsClientCredentialsBuilder.build(): ClientCredentials {
    return TlsClientCredentials(this as NativeTlsClientCredentialsBuilder)
}

internal class NativeTlsClientCredentialsBuilder : TlsClientCredentialsBuilder {
    var optionsBuilder = TlsCredentialsOptionsBuilder()

    override fun trustManager(rootCertsPem: String): TlsClientCredentialsBuilder {
        optionsBuilder.trustManager(rootCertsPem)
        return this
    }

    override fun keyManager(
        certChainPem: String,
        privateKeyPem: String,
    ): TlsClientCredentialsBuilder {
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

internal class CombinedClientCredentials(
    override val clientCredentials: ClientCredentials,
    override val callCredentials: GrpcCallCredentials,
): ClientCredentials() {
    override fun createRaw(parentJob: Job, coroutineDispatcher: CoroutineDispatcher): CPointer<grpc_channel_credentials> {
        val rawChannelCredentials = clientCredentials.createRaw(parentJob, coroutineDispatcher)
        val rawCallCredentials = callCredentials.createRaw(parentJob, coroutineDispatcher)
        val rawComposite = grpc_composite_channel_credentials_create(
            channel_creds = rawChannelCredentials,
            call_creds = rawCallCredentials,
            reserved = null
        )
        // Release originals as composite now holds references to them
        grpc_channel_credentials_release(rawChannelCredentials)
        grpc_call_credentials_release(rawCallCredentials)
        return rawComposite ?: internalError("Failed to create composite credentials")
    }
}

public actual operator fun ClientCredentials.plus(other: GrpcCallCredentials): ClientCredentials {
    return CombinedClientCredentials(clientCredentials, callCredentials?.combine(other) ?: other)
}
