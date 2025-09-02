/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_server
import cnames.structs.grpc_server_credentials
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.grpc.Server
import libkgrpc.grpc_insecure_server_credentials_create
import libkgrpc.grpc_server_add_http2_port
import libkgrpc.grpc_server_create
import libkgrpc.grpc_server_credentials_release
import libkgrpc.grpc_server_destroy
import libkgrpc.grpc_server_register_completion_queue
import libkgrpc.grpc_server_start
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner
import kotlin.time.Duration

/**
 * Wrapper for [cnames.structs.grpc_server_credentials].
 */
internal sealed class GrpcServerCredentials(
    internal val raw: CPointer<grpc_server_credentials>,
) {
    val rawCleaner = createCleaner(raw) {
        grpc_server_credentials_release(it)
    }
}

/**
 * Insecure credentials.
 */
internal class GrpcInsecureServerCredentials() :
    GrpcServerCredentials(grpc_insecure_server_credentials_create() ?: error("Failed to create server credentials"))


internal class NativeServer(
    override val port: Int,
    @Suppress("Redundant")
    private val credentials: GrpcServerCredentials,
) : Server {

    // a reference to make sure the grpc_init() was called. (it is released after shutdown)
    @Suppress("unused")
    private val rt = GrpcRuntime.acquire()

    private val cq = CompletionQueue()

    val raw: CPointer<grpc_server> = grpc_server_create(null, null)!!

    @Suppress("unused")
    private val rawCleaner = createCleaner(raw) {
        grpc_server_destroy(it)
    }

    init {
        grpc_server_register_completion_queue(raw, cq.raw, null)
        grpc_server_add_http2_port(raw, "0.0.0.0:$port", credentials.raw)
    }

    override val isShutdown: Boolean
        get() {
            TODO()
        }
    override val isTerminated: Boolean
        get() {
            TODO()
        }

    override fun start(): Server {
        grpc_server_start(raw)
    }

    override fun shutdown(): Server {
        TODO("Not yet implemented")
    }

    override fun shutdownNow(): Server {
        TODO("Not yet implemented")
    }

    override suspend fun awaitTermination(duration: Duration): Server {
        TODO("Not yet implemented")
    }
}