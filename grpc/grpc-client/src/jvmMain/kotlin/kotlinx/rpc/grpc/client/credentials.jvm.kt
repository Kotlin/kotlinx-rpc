/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.CallCredentials
import io.grpc.ChannelCredentials
import io.grpc.CompositeChannelCredentials
import io.grpc.InsecureChannelCredentials
import io.grpc.SecurityLevel
import io.grpc.TlsChannelCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.util.concurrent.Executor
import kotlin.coroutines.cancellation.CancellationException

public actual typealias ClientCredentials = ChannelCredentials

public actual typealias InsecureClientCredentials = InsecureChannelCredentials

public actual typealias TlsClientCredentials = TlsChannelCredentials

// we need a wrapper for InsecureChannelCredentials as our constructor would conflict with the private
// java constructor.
@InternalRpcApi
public actual fun createInsecureClientCredentials(): ClientCredentials {
    return InsecureClientCredentials.create()
}

internal actual fun TlsClientCredentialsBuilder(): TlsClientCredentialsBuilder = JvmTlsCLientCredentialBuilder()
internal actual fun TlsClientCredentialsBuilder.build(): ClientCredentials {
    return (this as JvmTlsCLientCredentialBuilder).build()
}

private class JvmTlsCLientCredentialBuilder : TlsClientCredentialsBuilder {
    private var cb = TlsClientCredentials.newBuilder()


    override fun trustManager(rootCertsPem: String): TlsClientCredentialsBuilder {
        cb.trustManager(rootCertsPem.byteInputStream())
        return this
    }

    override fun keyManager(
        certChainPem: String,
        privateKeyPem: String,
    ): TlsClientCredentialsBuilder {
        cb.keyManager(certChainPem.byteInputStream(), privateKeyPem.byteInputStream())
        return this
    }

    fun build(): ClientCredentials {
        return cb.build()
    }
}

internal fun GrpcCallCredentials.toJvm(): CallCredentials {
    return object : CallCredentials() {
        override fun applyRequestMetadata(
            requestInfo: RequestInfo,
            appExecutor: Executor,
            applier: MetadataApplier
        ) {
            val dispatcher = appExecutor.asCoroutineDispatcher()
            CoroutineScope(dispatcher).launch {
                try {
                    check(!requiresTransportSecurity || requestInfo.securityLevel != SecurityLevel.NONE) {
                        "Established channel does not have a sufficient security level to transfer call credential."
                    }

                    val context = GrpcCallCredentials.Context(requestInfo.authority, requestInfo.methodDescriptor.fullMethodName)
                    val metadata = context.getRequestMetadata()
                    applier.apply(metadata)
                } catch (e: Exception) {
                    // we are not treating StatusExceptions separately, as currently there is no
                    // clean way to support the same feature on native. So for the sake of similar behavior,
                    // we always fail with Status.UNAVAILABLE. (KRPC-233)
                    val description = "Getting metadata from call credentials failed with error: ${e.message}"
                    applier.fail(Status.UNAVAILABLE.withDescription(description).withCause(e))
                    if (e is CancellationException) {
                        throw e
                    }
                }
            }
        }
    }
}

public actual operator fun ClientCredentials.plus(other: GrpcCallCredentials): ClientCredentials {
    return CompositeChannelCredentials.create(this, other.toJvm())
}
