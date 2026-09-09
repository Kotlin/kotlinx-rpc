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
import kotlinx.coroutines.launch
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.client.internal.applyToMetadata
import kotlinx.rpc.grpc.internal.internalError
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

internal fun GrpcClientCredentials.toJvm(): ChannelCredentials {
    return when (this) {
        is GrpcCombinedClientCredentials -> clientCredentials.toJvm()
        is GrpcInsecureClientCredentials -> InsecureChannelCredentials.create()
        is GrpcTlsClientCredentials -> JvmTlsCLientCredentialBuilder().apply(configure).build()
        else -> internalError("Unknown client credentials type: $this")
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

internal fun GrpcCallCredentials.toJvm(coroutineContext: CoroutineContext): CallCredentials {
    return object : CallCredentials() {
        override fun applyRequestMetadata(
            requestInfo: RequestInfo,
            appExecutor: Executor,
            applier: MetadataApplier
        ) {
            CoroutineScope(coroutineContext).launch {
                applyToMetadata(
                requestInfo.authority,
                requestInfo.methodDescriptor.fullMethodName,
                requestInfo.securityLevel == SecurityLevel.PRIVACY_AND_INTEGRITY,
                    { status -> applier.fail(status) },
                ) { metadata ->
                    applier.apply(metadata)
                }
            }
        }
    }
}
