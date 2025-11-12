/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.client

import cnames.structs.grpc_channel_credentials
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.internal.TlsCredentialsOptionsBuilder
import kotlinx.rpc.internal.utils.InternalRpcApi
import libkgrpc.grpc_channel_credentials_release
import libkgrpc.grpc_insecure_credentials_create
import libkgrpc.grpc_tls_credentials_create
import libkgrpc.grpc_tls_credentials_options_destroy
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

public actual abstract class ClientCredentials {
    internal abstract val clientCredentials: ClientCredentials
    internal abstract val callCredentials: GrpcCallCredentials?

    internal abstract fun takeRaw(): CPointer<grpc_channel_credentials>
}

public actual class InsecureClientCredentials() : ClientCredentials() {
    override val clientCredentials: ClientCredentials
        get() = this
    override val callCredentials: GrpcCallCredentials?
        get() = null

    override fun takeRaw(): CPointer<grpc_channel_credentials> {
        return grpc_insecure_credentials_create() ?: error("grpc_insecure_credentials_create() returned null")
    }
}

public actual class TlsClientCredentials(
    private var credentials: CPointer<grpc_channel_credentials>?
) : ClientCredentials() {

    @Suppress("unused")
    private val rawCleaner = createCleaner(credentials) {
        if (it != null) {
            grpc_channel_credentials_release(it)
        }
    }

    override val clientCredentials: ClientCredentials
        get() = this
    override val callCredentials: GrpcCallCredentials?
        get() = null

    override fun takeRaw(): CPointer<grpc_channel_credentials> {
        val credentials = this.credentials
        this.credentials = null
        return credentials ?: error("Credentials are already taken")
    }
}

@InternalRpcApi
public actual fun createInsecureClientCredentials(): ClientCredentials {
    return InsecureClientCredentials()
}

internal actual fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder = NativeTlsClientCredentialsBuilder()
internal actual fun TlsClientCredentialsBuilder.build(): ClientCredentials {
    return (this as NativeTlsClientCredentialsBuilder).build()
}

private class NativeTlsClientCredentialsBuilder : TlsClientCredentialsBuilder {
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

    fun build(): ClientCredentials {
        val opts = optionsBuilder.build()
        val creds = grpc_tls_credentials_create(opts)
            ?: run {
                grpc_tls_credentials_options_destroy(opts);
                error("TLS channel credential creation failed")
            }
        return TlsClientCredentials(creds)
    }
}

internal class CombinedClientCredentials(
    override val clientCredentials: ClientCredentials,
    override val callCredentials: GrpcCallCredentials,
): ClientCredentials() {
    override fun takeRaw(): CPointer<grpc_channel_credentials> {
        // doesn't return a composite key but just the client credentials key.
        return clientCredentials.takeRaw()
    }
}

public actual operator fun ClientCredentials.plus(other: GrpcCallCredentials): ClientCredentials {
    return CombinedClientCredentials(clientCredentials, callCredentials?.combine(other) ?: other)
}
