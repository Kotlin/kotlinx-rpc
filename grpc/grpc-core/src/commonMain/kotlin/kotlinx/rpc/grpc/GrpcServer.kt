/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.atomicfu.atomic
import kotlinx.rpc.RpcServer
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * GrpcServer is an implementation of both [RpcServer] and [Server] interfaces,
 * providing the ability to host gRPC services.
 *
 * @property port Specifies the port used by the server to listen for incoming connections.
 * @param configure exposes platform-specific Server builder.
 */
public class GrpcServer internal constructor(
    override val port: Int = 8080,
    configure: ServerBuilder<*>.() -> Unit,
) : RpcServer, Server {
    private var isBuilt = false
    private lateinit var internalServer: Server

    private val serverBuilder: ServerBuilder<*> = ServerBuilder(port).apply(configure)
    private val registry: MutableHandlerRegistry by lazy {
        MutableHandlerRegistry().apply { serverBuilder.fallbackHandlerRegistry(this) }
    }

    private val localRegistry = RpcInternalConcurrentHashMap<KClass<*>, ServerServiceDefinition>()

    override fun <@Grpc Service : Any> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: () -> Service,
    ) {
        val service = serviceFactory()

        val definition: ServerServiceDefinition = getDefinition(service, serviceKClass)

        if (isBuilt) {
            registry.addService(definition)
        } else {
            serverBuilder.addService(definition)
        }
    }

    override fun <Service : Any> deregisterService(serviceKClass: KClass<Service>) {
        localRegistry.remove(serviceKClass)?.let {
            registry.removeService(it)
        }
    }

    private fun <@Grpc Service : Any> getDefinition(
        service: Service,
        serviceKClass: KClass<Service>,
    ): ServerServiceDefinition {
        val descriptor = serviceDescriptorOf(serviceKClass)
        val grpc = (descriptor as? GrpcServiceDescriptor<Service>)
            ?: error("Service ${descriptor.fqName} is not a gRPC service")

        return grpc.delegate.definitionFor(service)
    }

    private val buildLock = atomic(false)

    internal fun build() {
        if (buildLock.compareAndSet(expect = false, update = true)) {
            internalServer = Server(serverBuilder)
            isBuilt = true
        }
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

/**
 * Constructor function for the [GrpcServer] class.
 */
public fun GrpcServer(
    port: Int,
    configure: ServerBuilder<*>.() -> Unit = {},
    builder: RpcServer.() -> Unit = {},
): GrpcServer {
    return GrpcServer(port, configure).apply(builder).apply { build() }
}
