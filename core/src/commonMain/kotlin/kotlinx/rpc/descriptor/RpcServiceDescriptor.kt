/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcClient
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.*
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

@ExperimentalRpcApi
public inline fun <@Rpc reified Service : Any> serviceDescriptorOf(): RpcServiceDescriptor<Service> {
    return serviceDescriptorOf(Service::class)
}

@ExperimentalRpcApi
public fun <@Rpc Service : Any> serviceDescriptorOf(kType: KType): RpcServiceDescriptor<Service> {
    return serviceDescriptorOf(kType.rpcInternalKClass())
}

@ExperimentalRpcApi
public fun <@Rpc Service : Any> serviceDescriptorOf(kClass: KClass<Service>): RpcServiceDescriptor<Service> {
    val maybeDescriptor = internalServiceDescriptorOf(kClass)
        ?: internalRpcError("Unable to find a service descriptor of the $kClass")

    if (maybeDescriptor is RpcServiceDescriptor<*>) {
        @Suppress("UNCHECKED_CAST")
        return maybeDescriptor as RpcServiceDescriptor<Service>
    }

    internalRpcError(
        "Located descriptor object is not of a desired type ${RpcServiceDescriptor::class}, " +
                "instead found $maybeDescriptor of the class " +
                (maybeDescriptor::class.rpcInternalQualifiedClassNameOrNull ?: maybeDescriptor::class)
    )
}

@ExperimentalRpcApi
public interface RpcServiceDescriptor<@Rpc Service : Any> {
    public val simpleName: String

    public val fqName: String

    public fun getCallable(name: String): RpcCallable<Service>?

    public val callables: Map<String, RpcCallable<Service>>

    public fun createInstance(serviceId: Long, client: RpcClient): Service
}

@ExperimentalRpcApi
public interface RpcCallable<@Rpc Service : Any> {
    public val name: String
    public val returnType: RpcType
    public val invokator: RpcInvokator<Service>
    public val parameters: Array<out RpcParameter>
}

@ExperimentalRpcApi
public val <@Rpc Service : Any> RpcCallable<Service>.unaryInvokator: RpcInvokator.UnaryResponse<Service>
    get() = invokator as RpcInvokator.UnaryResponse<Service>

@ExperimentalRpcApi
public val <@Rpc Service : Any> RpcCallable<Service>.flowInvokator: RpcInvokator.FlowResponse<Service>
    get() = invokator as RpcInvokator.FlowResponse<Service>

@ExperimentalRpcApi
public sealed interface RpcInvokator<@Rpc Service : Any> {
    @ExperimentalRpcApi
    public fun interface UnaryResponse<@Rpc Service : Any> : RpcInvokator<Service> {
        public suspend fun call(service: Service, arguments: Array<Any?>): Any?
    }

    @ExperimentalRpcApi
    public fun interface FlowResponse<@Rpc Service : Any> : RpcInvokator<Service> {
        public fun call(service: Service, arguments: Array<Any?>): Flow<Any?>
    }
}

@ExperimentalRpcApi
public interface RpcParameter {
    public val name: String
    public val type: RpcType
    public val isOptional: Boolean

    /**
     * List of annotations with target [AnnotationTarget.VALUE_PARAMETER].
     *
     * Don't confuse with [RpcType.annotations].
     */
    public val annotations: List<Annotation>
}

@ExperimentalRpcApi
public interface RpcType {
    public val kType: KType

    /**
     * List of annotations with target [AnnotationTarget.TYPE].
     */
    public val annotations: List<Annotation>
}
