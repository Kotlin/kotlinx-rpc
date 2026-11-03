/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

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
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.client.GrpcCallOptions
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
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcClient
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcClientConfiguration
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestSourceProtocol
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcWaitForReadyDisabled
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcWaitForReadyEnabled
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcWaitForReadyUnspecified
import kotlin.time.Duration

internal class SwiftManagedChannel(
    internal val client: SwiftGrpcClient,
    internal val authority: String,
    internal val secure: Boolean,
    private val maxResponseMessageBytes: Long,
) : ManagedChannel {
    private val shutdown = atomic(false)
    private val terminated = CompletableDeferred<Unit>()

    init {
        // Capture only the state so the Swift callback does not reference this channel and its client.
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
        callOptions: GrpcCallOptions,
        requestSource: SwiftGrpcRequestSourceProtocol,
    ): SwiftGrpcCallAdapter<Response> {
        check(!isShutdown) { internalError("Channel is shutdown") }

        val timeout = callOptions.timeout
        // Map an infinite timeout to -1, which means no deadline for grpc-swift.
        val swiftTimeout = if (timeout.isInfinite()) -1L else timeout.inWholeMilliseconds
        // Only an explicitly set option overrides the grpc-swift default.
        val swiftWaitForReady = when {
            callOptions.isWaitForReadyExplicitlySet && callOptions.waitForReady -> SwiftGrpcWaitForReadyEnabled
            callOptions.isWaitForReadyExplicitlySet -> SwiftGrpcWaitForReadyDisabled
            else -> SwiftGrpcWaitForReadyUnspecified
        }

        val swiftCall = memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            error.value = null
            val call = client.startCallWithFullMethodName(
                fullMethodName = method.getFullMethodName(),
                type = method.methodType.toSwift(),
                headers = headers.toSwift(),
                timeoutMilliseconds = swiftTimeout,
                waitForReady = swiftWaitForReady,
                maxResponseMessageBytes = maxResponseMessageBytes,
                compression = callOptions.compression.toSwift(),
                requestSource = requestSource,
                // Requests are always pulled from the source.
                initialRequest = null,
                error = error.ptr,
            )
            error.value?.let { throw SwiftGrpcInteropException(it) }
            call ?: error("grpc-swift returned neither a call nor an NSError")
        }
        return SwiftGrpcCallAdapter(swiftCall, method)
    }
}

@InternalRpcApi
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>>

private class SwiftManagedChannelBuilder(
    private val hostname: String,
    private val port: Int,
    private val credentials: GrpcClientCredentials?,
) : ManagedChannelBuilder<SwiftManagedChannelBuilder>() {
    fun buildChannel(config: GrpcClientConfiguration): SwiftManagedChannel {
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
            host = hostname,
            port = port.toLong(),
            plaintext = plaintext,
        )

        swiftConfig.overrideAuthority = config.overrideAuthority
        // Prefix the Swift runtime token with the application user-agent, when configured.
        swiftConfig.userAgent = composeSwiftGrpcUserAgent(config.userAgent)
        if (config.isIdleTimeoutExplicitlySet) {
            // A negative value disables idle mode. An unset timeout keeps the grpc-swift default,
            // which matches the Kotlin default of 30 minutes.
            swiftConfig.idleTimeoutMilliseconds =
                if (config.idleTimeout.isInfinite()) -1L else config.idleTimeout.inWholeMilliseconds
        }
        config.keepAlive?.let { keepAlive ->
            // An infinite ping interval means no keepalive pings at all, which is the
            // grpc-swift default, so only a finite interval configures keepalive.
            if (keepAlive.time.isFinite()) {
                swiftConfig.keepAliveTimeMilliseconds = keepAlive.time.inWholeMilliseconds
                swiftConfig.keepAliveTimeoutMilliseconds =
                    if (keepAlive.timeout.isInfinite()) {
                        INFINITE_KEEPALIVE_TIMEOUT_MILLISECONDS
                    } else {
                        keepAlive.timeout.inWholeMilliseconds
                    }
                swiftConfig.keepAliveWithoutCalls = keepAlive.withoutCalls
            }
        }
        swiftConfig.maxInboundMetadataSizeBytes = config.maxInboundMetadataSize.toLong()

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
            authority = config.overrideAuthority ?: composeAuthority(hostname, port),
            secure = !plaintext,
            // A negative value makes grpc-swift use its own default limit.
            maxResponseMessageBytes = if (config.isMaxInboundMessageSizeExplicitlySet) {
                config.maxInboundMessageSize.toLong()
            } else {
                -1L
            },
        )
    }
}

internal fun composeSwiftGrpcUserAgent(userAgentPrefix: String?): String =
    listOfNotNull(
        userAgentPrefix?.takeIf { it.isNotEmpty() },
        "kotlinx-rpc-swift/$KOTLINX_RPC_VERSION",
    ).joinToString(" ")

// grpc-swift needs a finite keepalive timeout. 24 hours stands in for Duration.INFINITE
private const val INFINITE_KEEPALIVE_TIMEOUT_MILLISECONDS: Long = 24 * 60L * 60 * 1_000

private fun composeAuthority(hostname: String, port: Int): String =
    // An IPv6 literal contains colons and must be enclosed in brackets, as in a URL.
    (if (hostname.contains(':')) "[$hostname]" else hostname) + ":" + port

// A temporary GrpcTlsClientCredentialsBuilder implementation which throws an error if a user
// wants to use a custom trustManager or keyManager.
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
    return SwiftManagedChannelBuilder(hostname, port, credentials)
}

internal actual fun ManagedChannelBuilder<*>.buildChannel(
    config: GrpcClientConfiguration,
): ManagedChannel {
    check(this is SwiftManagedChannelBuilder) {
        internalError("Wrong builder type, expected SwiftManagedChannelBuilder")
    }
    return buildChannel(config)
}
