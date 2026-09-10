/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, InternalRpcApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.atomicfu.atomic
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.rpc.grpc.GrpcCompression
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientCredentials
import kotlinx.rpc.grpc.client.GrpcInsecureClientCredentials
import kotlinx.rpc.grpc.client.GrpcTlsClientCredentials
import kotlinx.rpc.grpc.client.GrpcTlsClientCredentialsBuilder
import kotlinx.rpc.grpc.client.realClientCredentials
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.KOTLINX_RPC_VERSION
import kotlinx.rpc.internal.utils.InternalRpcApi
import platform.Foundation.NSError
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcClient
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcClientConfiguration
import kotlin.time.Duration

internal class SwiftManagedChannel(
    internal val client: SwiftGrpcClient,
    internal val authority: String,
    internal val secure: Boolean,
) : ManagedChannel {
    private val shutdown = atomic(false)
    private val terminated = CompletableDeferred<Unit>()

    init {
        // Capture only the state so the Swift callback does not retain this channel and its client.
        val shutdown = shutdown
        val terminated = terminated
        client.notifyWhenTerminated {
            shutdown.value = true
            terminated.complete(Unit)
        }
    }

    override val isShutdown: Boolean
        get() = shutdown.value

    override val isTerminated: Boolean
        get() = terminated.isCompleted

    override suspend fun awaitTermination(duration: Duration): Boolean {
        withTimeoutOrNull(duration) {
            terminated.await()
        } ?: return false
        return true
    }

    override fun shutdown(): ManagedChannel {
        if (shutdown.compareAndSet(expect = false, update = true)) {
            client.beginGracefulShutdown()
        }
        return this
    }

    override fun shutdownNow(): ManagedChannel {
        shutdown.value = true
        client.shutdownNow()
        return this
    }

    fun <Request, Response> startCall(
        method: GrpcMethodDescriptor<Request, Response>,
        headers: GrpcMetadata,
        timeout: Duration?,
        compression: GrpcCompression,
        requestSource: KotlinGrpcRequestSource<Request>
    ): SwiftGrpcCallAdapter<Response> {
        // Map null and infinite timeouts to -1
        val swiftTimeout = timeout
            ?.takeUnless { it.isInfinite() }
            ?.inWholeMilliseconds
            ?: -1L

        val swiftCall = memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            error.value = null
            val call = client.startCallWithFullMethodName(
                fullMethodName = method.getFullMethodName(),
                type = method.methodType.toSwift(),
                headers = headers.toSwift(),
                timeoutMilliseconds = swiftTimeout,
                compression = compression.toSwift(),
                requestSource = requestSource,
                error = error.ptr,
            )
            error.value?.let { throw SwiftGrpcInteropException(it) }
            call ?: error("grpc-swift returned neither a call nor an NSError")
        }
        return SwiftGrpcCallAdapter(swiftCall, method)
    }

}

@InternalRpcApi
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>> {
    internal var config: GrpcClientConfiguration? = null
}

private class SwiftManagedChannelBuilder(
    private val target: GrpcClientTarget,
    private val credentials: GrpcClientCredentials?,
) : ManagedChannelBuilder<SwiftManagedChannelBuilder>() {
    fun buildChannel(): SwiftManagedChannel {
        val clientCredentials = (credentials ?: GrpcTlsClientCredentials()).realClientCredentials
        val plaintext = when (clientCredentials) {
            is GrpcInsecureClientCredentials -> true
            is GrpcTlsClientCredentials -> {
                clientCredentials.configure(UnsupportedSwiftTlsClientCredentialsBuilder)
                false
            }
            else -> internalError("Unknown client credentials type: $clientCredentials")
        }
        val swiftConfig = SwiftGrpcClientConfiguration(
            host = target.host,
            port = target.port.toLong(),
            plaintext = plaintext,
        )

        swiftConfig.overrideAuthority = config?.overrideAuthority
        // Prefix the Swift runtime token with the application user-agent, when configured.
        swiftConfig.userAgent = composeSwiftGrpcUserAgent(config?.userAgent)
        config?.keepAlive?.let { keepAlive ->
            require(keepAlive.time.isPositive()) { "keepalive time must be positive" }
            require(keepAlive.timeout.isPositive()) { "keepalive timeout must be positive" }
            if (keepAlive.time.isFinite()) {
                swiftConfig.keepAliveTimeMilliseconds = keepAlive.time.inWholeMilliseconds
                swiftConfig.keepAliveTimeoutMilliseconds = keepAlive.timeout.inWholeMilliseconds
                swiftConfig.keepAliveWithoutCalls = keepAlive.withoutCalls
            }
        }

        val swiftClient = memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            error.value = null
            val client = SwiftGrpcClient(
                configuration = swiftConfig,
                error = error.ptr,
            )
            error.value?.let { throw IllegalStateException(it.localizedDescription) }
            client
        }
        return SwiftManagedChannel(
            client = swiftClient,
            authority = config?.overrideAuthority ?: target.authority,
            secure = !plaintext,
        )
    }
}

internal fun composeSwiftGrpcUserAgent(userAgentPrefix: String?): String =
    listOfNotNull(
        userAgentPrefix?.takeIf { it.isNotEmpty() },
        "kotlinx-rpc-swift/$KOTLINX_RPC_VERSION",
    ).joinToString(" ")

private object UnsupportedSwiftTlsClientCredentialsBuilder : GrpcTlsClientCredentialsBuilder {
    override fun trustManager(rootCertsPem: String): GrpcTlsClientCredentialsBuilder {
        error("Custom TLS trust roots are not yet supported by the iOS grpc-swift transport")
    }

    override fun keyManager(
        certChainPem: String,
        privateKeyPem: String,
    ): GrpcTlsClientCredentialsBuilder {
        error("Mutual TLS is not yet supported by the iOS grpc-swift transport")
    }
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    hostname: String,
    port: Int,
    credentials: GrpcClientCredentials?,
): ManagedChannelBuilder<*> {
    require(hostname.isNotBlank()) { "gRPC hostname must not be blank" }
    require(port in 1..65535) { "gRPC target port must be in 1..65535, but was '$port'" }
    return SwiftManagedChannelBuilder(GrpcClientTarget(hostname, port), credentials)
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    target: String,
    credentials: GrpcClientCredentials?,
): ManagedChannelBuilder<*> =
    SwiftManagedChannelBuilder(GrpcClientTarget.parse(target), credentials)

internal actual fun ManagedChannelBuilder<*>.applyConfig(
    config: GrpcClientConfiguration,
): ManagedChannelBuilder<*> {
    this.config = config
    return this
}

@InternalRpcApi
public actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    check(this is SwiftManagedChannelBuilder) {
        internalError("Wrong builder type, expected SwiftManagedChannelBuilder")
    }
    return buildChannel()
}
