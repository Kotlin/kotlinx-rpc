/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.CallCredentials
import io.grpc.ChannelCredentials
import io.grpc.InsecureChannelCredentials
import io.grpc.SecurityLevel
import io.grpc.TlsChannelCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.rpc.grpc.Status
import java.util.concurrent.Executor
import kotlin.coroutines.cancellation.CancellationException

internal fun GrpcClientCredentials.toJvm(): ChannelCredentials {
    return when (this) {
        is GrpcCombinedClientCredentials -> clientCredentials.toJvm()
        is GrpcInsecureClientCredentials -> InsecureChannelCredentials.create()
        is GrpcTlsClientCredentials -> JvmTlsCLientCredentialBuilder().apply(configure).build()
    }
}

private class JvmTlsCLientCredentialBuilder : GrpcTlsClientCredentialsBuilder {
    private var cb = TlsChannelCredentials.newBuilder()


    override fun trustManager(rootCertsPem: String): GrpcTlsClientCredentialsBuilder {
        cb.trustManager(rootCertsPem.byteInputStream())
        return this
    }

    override fun keyManager(
        certChainPem: String,
        privateKeyPem: String,
    ): GrpcTlsClientCredentialsBuilder {
        cb.keyManager(certChainPem.byteInputStream(), privateKeyPem.byteInputStream())
        return this
    }

    fun build(): ChannelCredentials {
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
