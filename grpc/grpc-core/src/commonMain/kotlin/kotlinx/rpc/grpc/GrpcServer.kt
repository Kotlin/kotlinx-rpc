/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.rpc.RemoteService
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.internal.MutableHandlerRegistry
import kotlinx.rpc.grpc.internal.ServerServiceDefinition
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.time.Duration

public class GrpcServer internal constructor(
    override val port: Int = 8080,
    builder: ServerBuilder<*>.() -> Unit,
) : RpcServer, Server {
    private var isBuilt = false
    private lateinit var internalServer: Server

    private val serverBuilder: ServerBuilder<*> = ServerBuilder(port).apply(builder)
    private val registry: MutableHandlerRegistry by lazy {
        MutableHandlerRegistry().apply { serverBuilder.fallbackHandlerRegistry(this) }
    }

    override val coroutineContext: CoroutineContext
        get() = error("coroutineContext is not available for gRPC server builder")

    override fun <Service : RemoteService> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: (CoroutineContext) -> Service,
    ) {
        val childJob = SupervisorJob(coroutineContext.job)
        val service = serviceFactory(childJob)

        val definition: ServerServiceDefinition = getDefinition(service, serviceKClass)

        if (isBuilt) {
            registry.addService(definition)
        } else {
            serverBuilder.addService(definition)
        }
    }

    private fun <Service : RemoteService> getDefinition(
        service: Service,
        serviceKClass: KClass<Service>,
    ): ServerServiceDefinition {
        // generated locator
        TODO("Not yet implemented")
    }

    internal fun build() {
        internalServer = Server(serverBuilder)
        isBuilt = true
    }

    override val isShutdown: Boolean
        get() = internalServer.isShutdown

    override val isTerminated: Boolean
        get() = internalServer.isTerminated

    override fun start(): GrpcServer {
        internalServer.start()
        return this
    }

    override fun shutdown(): GrpcServer {
        internalServer.shutdown()
        return this
    }

    override fun shutdownNow(): GrpcServer {
        internalServer.shutdownNow()
        return this
    }

    override suspend fun awaitTermination(duration: Duration): GrpcServer {
        internalServer.awaitTermination(duration)
        return this
    }
}

public fun GrpcServer(
    port: Int,
    configure: ServerBuilder<*>.() -> Unit = {},
    builder: RpcServer.() -> Unit = {},
): GrpcServer {
    return GrpcServer(port, configure).apply(builder).apply { build() }
}
