/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.job
import kotlinx.rpc.RpcServer
import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.descriptor.flowInvokator
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.descriptor.unaryInvokator
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.EmptyMessageCodecResolver
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.grpc.internal.MethodType
import kotlinx.rpc.grpc.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.internal.bidiStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.clientStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.serverStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.type
import kotlinx.rpc.grpc.internal.unaryServerMethodDefinition
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlin.time.Duration

private typealias RequestServer = Any
private typealias ResponseServer = Any

/**
 * GrpcServer is an implementation of both [RpcServer] and [Server] interfaces,
 * providing the ability to host gRPC services.
 *
 * @property port Specifies the port used by the server to listen for incoming connections.
 * @param parentContext
 * @param configure exposes platform-specific Server builder.
 */
public class GrpcServer internal constructor(
    override val port: Int = 8080,
    private val messageCodecResolver: MessageCodecResolver = EmptyMessageCodecResolver,
    parentContext: CoroutineContext = EmptyCoroutineContext,
    configure: ServerBuilder<*>.() -> Unit,
) : RpcServer, Server {
    private val internalContext = SupervisorJob(parentContext.job)
    private val internalScope = CoroutineScope(parentContext + internalContext)

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
        @Suppress("UNCHECKED_CAST")
        val descriptor = serviceDescriptorOf(serviceKClass) as? GrpcServiceDescriptor<Service>
            ?: error("Service $serviceKClass is not a gRPC service")

        val delegate = descriptor.delegate(messageCodecResolver)

        val methods = descriptor.callables.map {
            @Suppress("UNCHECKED_CAST")
            val methodDescriptor = delegate.getMethodDescriptor(it.name)
                    as? MethodDescriptor<RequestServer, ResponseServer>
                ?: error("Expected a gRPC method descriptor")

            it.toDefinitionOn(methodDescriptor, service)
        }

        return serverServiceDefinition(delegate.serviceDescriptor, methods)
    }

    private fun <@Grpc Service : Any> RpcCallable<Service>.toDefinitionOn(
        descriptor: MethodDescriptor<RequestServer, ResponseServer>,
        service: Service,
    ): ServerMethodDefinition<RequestServer, ResponseServer> {
        return when (descriptor.type) {
            MethodType.UNARY -> {
                internalScope.unaryServerMethodDefinition(descriptor) { request ->
                    unaryInvokator.call(service, arrayOf(request)) as ResponseServer
                }
            }

            MethodType.CLIENT_STREAMING -> {
                internalScope.clientStreamingServerMethodDefinition(descriptor) { requests ->
                    unaryInvokator.call(service, arrayOf(requests)) as ResponseServer
                }
            }

            MethodType.SERVER_STREAMING -> {
                internalScope.serverStreamingServerMethodDefinition(descriptor) { request ->
                    @Suppress("UNCHECKED_CAST")
                    flowInvokator.call(service, arrayOf(request)) as Flow<ResponseServer>
                }
            }

            MethodType.BIDI_STREAMING -> {
                internalScope.bidiStreamingServerMethodDefinition(descriptor) { requests ->
                    @Suppress("UNCHECKED_CAST")
                    flowInvokator.call(service, arrayOf(requests)) as Flow<ResponseServer>
                }
            }

            MethodType.UNKNOWN -> {
                error("Unsupported method type ${descriptor.type} for ${descriptor.getFullMethodName()}")
            }
        }
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
        internalContext.cancel("Shutting down server")
        internalServer.shutdown()
        return this
    }

    override fun shutdownNow(): GrpcServer {
        internalContext.cancel("Shutting down server now")
        internalServer.shutdownNow()
        return this
    }

    override suspend fun awaitTermination(duration: Duration): GrpcServer {
        internalContext.join()
        internalServer.awaitTermination(duration)
        return this
    }
}

/**
 * Constructor function for the [GrpcServer] class.
 */
public fun GrpcServer(
    port: Int,
    messageCodecResolver: MessageCodecResolver = EmptyMessageCodecResolver,
    parentContext: CoroutineContext = EmptyCoroutineContext,
    configure: ServerBuilder<*>.() -> Unit = {},
    builder: RpcServer.() -> Unit = {},
): GrpcServer {
    return GrpcServer(port, messageCodecResolver, parentContext, configure).apply(builder).apply { build() }
}
