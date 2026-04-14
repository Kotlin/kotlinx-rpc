/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcServer
import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.descriptor.flowInvokator
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.descriptor.unaryInvokator
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.grpc.descriptor.methodType
import kotlinx.rpc.grpc.marshaller.GrpcEmptyMarshallerResolver
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlinx.rpc.grpc.marshaller.ThrowingGrpcMarshallerResolver
import kotlinx.rpc.grpc.marshaller.plus
import kotlinx.rpc.grpc.server.GrpcMutableHandlerRegistry
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.GrpcServerServiceDefinition
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.serverServiceDefinition
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.getValue
import kotlin.reflect.KClass
import kotlin.time.Duration

private typealias RequestType = Any
private typealias ResponseType = Any

/**
 * GrpcServer is an implementation of both [RpcServer] and [GrpcServer] interfaces,
 * providing the ability to host gRPC services.
 *
 * @param serverBuilder exposes platform-specific Server builder.
 * @param interceptors a list of interceptors that will be applied to all incoming gRPC calls
 * @param messageMarshallerResolver a custom [GrpcMarshallerResolver] that will be used to resolve message marshallers
 * @param marshallerConfig default [GrpcMarshallerConfig] that will be passed applied to all used message resolvers
 * during message serialization and deserialization.
 * @param parentContext
 */
@InternalRpcApi
public class GrpcServerImpl internal constructor(
    private val serverBuilder: ServerBuilder<*>,
    private val interceptors: List<GrpcServerInterceptor>,
    messageMarshallerResolver: GrpcMarshallerResolver = GrpcEmptyMarshallerResolver,
    private val marshallerConfig: GrpcMarshallerConfig? = null,
    parentContext: CoroutineContext = EmptyCoroutineContext,
) : GrpcServer {
    private val internalContext = SupervisorJob(parentContext[Job])
    private val internalScope = CoroutineScope(parentContext + internalContext)

    private val messageMarshallerResolver = messageMarshallerResolver + ThrowingGrpcMarshallerResolver

    private var isBuilt = false
    private lateinit var internalServer: PlatformServer

    override val port: Int
        get() = internalServer.port

    private val registry: GrpcMutableHandlerRegistry by lazy {
        GrpcMutableHandlerRegistry().apply { this@GrpcServerImpl.serverBuilder.fallbackHandlerRegistry(this) }
    }

    private val localRegistry = RpcInternalConcurrentHashMap<KClass<*>, GrpcServerServiceDefinition>()

    override fun <@Grpc Service : Any> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: () -> Service,
    ) {
        val service = serviceFactory()

        val definition: GrpcServerServiceDefinition = getDefinition(service, serviceKClass)

        if (isBuilt) {
            registry.addService(definition)
        } else {
            this@GrpcServerImpl.serverBuilder.addService(definition)
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
    ): GrpcServerServiceDefinition {
        @Suppress("UNCHECKED_CAST")
        val descriptor = serviceDescriptorOf(serviceKClass) as? GrpcServiceDescriptor<Service>
            ?: error("Service $serviceKClass is not a gRPC service")

        val delegate = descriptor.delegate(messageMarshallerResolver, marshallerConfig)

        val methods = descriptor.callables.values.map {
            @Suppress("UNCHECKED_CAST")
            val methodDescriptor = delegate.getMethodDescriptor(it.name)
                as? GrpcMethodDescriptor<RequestType, ResponseType>
                ?: error("Expected a gRPC method descriptor")

            // TODO: support per service and per method interceptors (KRPC-222)
            it.toDefinitionOn(methodDescriptor, service, interceptors)
        }

        return serverServiceDefinition(delegate.serviceDescriptor, methods)
    }

    private fun <@Grpc Service : Any> RpcCallable<Service>.toDefinitionOn(
        descriptor: GrpcMethodDescriptor<RequestType, ResponseType>,
        service: Service,
        interceptors: List<GrpcServerInterceptor>,
    ): ServerMethodDefinition<RequestType, ResponseType> {
        return when (descriptor.methodType) {
            GrpcMethodType.UNARY -> {
                internalScope.unaryServerMethodDefinition(
                    descriptor = descriptor,
                    responseKType = returnType.kType,
                    interceptors = interceptors,
                ) { request ->
                    unaryInvokator.call(service, arrayOf(request)) as ResponseType
                }
            }

            GrpcMethodType.CLIENT_STREAMING -> {
                internalScope.clientStreamingServerMethodDefinition(
                    descriptor = descriptor,
                    responseKType = returnType.kType,
                    interceptors = interceptors,
                ) { requests ->
                    unaryInvokator.call(service, arrayOf(requests)) as ResponseType
                }
            }

            GrpcMethodType.SERVER_STREAMING -> {
                internalScope.serverStreamingServerMethodDefinition(
                    descriptor = descriptor,
                    responseKType = returnType.kType,
                    interceptors = interceptors,
                ) { request ->
                    @Suppress("UNCHECKED_CAST")
                    flowInvokator.call(service, arrayOf(request)) as Flow<ResponseType>
                }
            }

            GrpcMethodType.BIDI_STREAMING -> {
                internalScope.bidiStreamingServerMethodDefinition(
                    descriptor = descriptor,
                    responseKType = returnType.kType,
                    interceptors = interceptors,
                ) { requests ->
                    @Suppress("UNCHECKED_CAST")
                    flowInvokator.call(service, arrayOf(requests)) as Flow<ResponseType>
                }
            }

            GrpcMethodType.UNKNOWN -> {
                error("Unsupported method type ${descriptor.methodType} for ${descriptor.getFullMethodName()}")
            }
        }
    }

    private val buildLock = atomic(false)

    internal fun build() {
        if (buildLock.compareAndSet(expect = false, update = true)) {
            internalServer = PlatformServer(this@GrpcServerImpl.serverBuilder)
            isBuilt = true
        }
    }

    override val isShutdown: Boolean
        get() = internalServer.isShutdown

    override val isTerminated: Boolean
        get() = internalServer.isTerminated

    override fun start(): GrpcServerImpl {
        internalServer.start()
        return this
    }

    override fun shutdown(): GrpcServerImpl {
        internalContext.cancel("Shutting down server")
        internalServer.shutdown()
        return this
    }

    override fun shutdownNow(): GrpcServerImpl {
        internalContext.cancel("Shutting down server now")
        internalServer.shutdownNow()
        return this
    }

    override suspend fun awaitTermination(duration: Duration): GrpcServerImpl {
        internalContext.join()
        internalServer.awaitTermination(duration)
        return this
    }
}
