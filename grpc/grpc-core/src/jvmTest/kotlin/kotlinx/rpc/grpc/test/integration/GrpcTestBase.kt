/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcClientInterceptor
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.GrpcClientCredentials
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.GrpcServerConfiguration
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerCredentials
import kotlinx.rpc.grpc.server.GrpcServerInterceptor

abstract class GrpcTestBase {
    private val serverMutex = Mutex()

    abstract fun RpcServer.registerServices()

    fun runGrpcTest(
        serverCreds: GrpcServerCredentials? = null,
        clientCreds: GrpcClientCredentials? = null,
        overrideAuthority: String? = null,
        clientInterceptors: List<GrpcClientInterceptor> = emptyList(),
        serverInterceptors: List<GrpcServerInterceptor> = emptyList(),
        clientConfiguration: GrpcClientConfiguration.() -> Unit = {},
        serverConfiguration: GrpcServerConfiguration.() -> Unit = {},
        test: suspend (GrpcClient) -> Unit,
    ) = runBlocking {
        serverMutex.withLock {
            val grpcServer = GrpcServer(0) {
                credentials = serverCreds
                serverInterceptors.forEach { intercept(it) }
                services { registerServices() }
                serverConfiguration()
            }

            grpcServer.start()

            val grpcClient = GrpcClient("localhost", grpcServer.port) {
                credentials = clientCreds ?: plaintext()
                if (overrideAuthority != null) this.overrideAuthority = overrideAuthority
                clientInterceptors.forEach { intercept(it) }
                clientConfiguration()
            }

            try {
                test(grpcClient)
            } finally {
                // Use force shutdown (shutdownNow) to cancel in-flight calls immediately.
                // On native failure paths, cancelled calls may leave orphaned CQ batches that
                // delay graceful shutdown indefinitely, causing the test process to hang (KRPC-576).
                grpcClient.shutdownNow()
                grpcServer.shutdownNow()
                grpcServer.awaitTermination(30.seconds)
                grpcClient.awaitTermination(30.seconds)
            }
        }
    }

    internal fun serverInterceptor(
        block: GrpcServerCallScope<Any, Any>.(Flow<Any>) -> Flow<Any>,
    ): List<GrpcServerInterceptor> {
        return listOf(object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Req, Resp> GrpcServerCallScope<Req, Resp>.intercept(
                request: Flow<Req>,
            ): Flow<Resp> {
                with(this as GrpcServerCallScope<Any, Any>) {
                    return block(request as Flow<Any>) as Flow<Resp>
                }
            }
        })
    }

    internal fun clientInterceptor(
        block: GrpcClientCallScope<Any, Any>.(Flow<Any>) -> Flow<Any>,
    ): List<GrpcClientInterceptor> {
        return listOf(object : GrpcClientInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Req, Resp> GrpcClientCallScope<Req, Resp>.intercept(
                request: Flow<Req>,
            ): Flow<Resp> {
                with(this as GrpcClientCallScope<Any, Any>) {
                    return block(request as Flow<Any>) as Flow<Resp>
                }
            }
        })
    }
}
