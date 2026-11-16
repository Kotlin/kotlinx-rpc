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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.internal.credentialMetadataFailure
import kotlinx.rpc.grpc.client.internal.resolveRequestMetadata
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.status
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

@OptIn(DelicateCoroutinesApi::class)
internal fun GrpcCallCredentials.toJvm(coroutineContext: CoroutineContext): CallCredentials {
    return object : CallCredentials() {
        override fun applyRequestMetadata(
            requestInfo: RequestInfo,
            appExecutor: Executor,
            applier: MetadataApplier
        ) {
            // grpc-java waits for the applier before transport shutdown, even if this call is cancelled.
            // Therefore, we must start the launch in ATOMIC mode to ensure it is still
            // executed (until the next cancellation checkpoint), even on cancellation.
            CoroutineScope(coroutineContext).launch(start = CoroutineStart.ATOMIC) {
                val metadata = try {
                    // INTEGRITY alone doesn't encrypt, so only PRIVACY_AND_INTEGRITY counts as secure.
                    resolveRequestMetadata(
                        requestInfo.authority,
                        requestInfo.methodDescriptor.fullMethodName,
                        isTransportSecure = requestInfo.securityLevel == SecurityLevel.PRIVACY_AND_INTEGRITY,
                    )
                } catch (error: Throwable) {
                    // We must always call applier.fail(), otherwise we leak during client shutdown.
                    val status = when (error) {
                        is GrpcStatusException -> error.status
                        else -> credentialMetadataFailure(error)
                    }
                    applier.fail(status)
                    if (error is CancellationException || error is Error) {
                        throw error
                    }
                    return@launch
                }
                // Call it outside the try/catch to not call applier.fail() in case of `applier.apply()` failure.
                applier.apply(metadata)
            }
        }
    }
}
