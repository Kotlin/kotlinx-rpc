/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcProtocol
import kotlinx.rpc.RpcService
import kotlinx.rpc.internal.internalRpcError
import kotlinx.rpc.internal.internalServiceDescriptorOf
import kotlinx.rpc.internal.rpcInternalKClass
import kotlinx.rpc.internal.rpcInternalQualifiedClassNameOrNull
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

@ExperimentalRpcApi
public inline fun <reified Service : RpcService<*, Service>> serviceDescriptorOf(): RpcServiceDescriptor<*, Service> {
    return serviceDescriptorOf(Service::class)
}

@ExperimentalRpcApi
public fun <Service : RpcService<*, Service>> serviceDescriptorOf(kType: KType): RpcServiceDescriptor<*, Service> {
    return serviceDescriptorOf(kType.rpcInternalKClass())
}

@ExperimentalRpcApi
public fun <Service : RpcService<*, Service>> serviceDescriptorOf(kClass: KClass<Service>): RpcServiceDescriptor<*, Service> {
    val maybeDescriptor = internalServiceDescriptorOf(kClass)
        ?: internalRpcError("Unable to find a service descriptor of the $kClass. " +
                "Check that you have applied the “kotlinx.rpc” plugin to your build module.")

    if (maybeDescriptor is RpcServiceDescriptor<*, *>) {
        @Suppress("UNCHECKED_CAST")
        return maybeDescriptor as RpcServiceDescriptor<*, Service>
    }

    internalRpcError(
        "Located descriptor object is not of a desired type ${RpcServiceDescriptor::class}, " +
                "instead found $maybeDescriptor of the class " +
                (maybeDescriptor::class.rpcInternalQualifiedClassNameOrNull ?: maybeDescriptor::class)
    )
}

@ExperimentalRpcApi
public interface RpcServiceDescriptor<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>> {
    public val simpleName: String

    public val fqName: String

    public fun getCallable(name: String): RpcCallable<Protocol, Service>?

    public val callables: Map<String, RpcCallable<Protocol, Service>>
}

@ExperimentalRpcApi
public interface RpcCallable<Protocol: RpcProtocol, Service : RpcService<Protocol, Service>> {
    public val name: String
    public val returnType: RpcType
    public val invokator: RpcInvokator<Protocol, Service>
    public val parameters: Array<out RpcParameter>
}

@ExperimentalRpcApi
public val <Protocol: RpcProtocol, Service : RpcService<Protocol, Service>> RpcCallable<Protocol, Service>.unaryInvokator: RpcInvokator.UnaryResponse<Protocol, Service>
    get() = invokator as RpcInvokator.UnaryResponse<Protocol, Service>

@ExperimentalRpcApi
public val <Protocol: RpcProtocol, Service : RpcService<Protocol, Service>> RpcCallable<Protocol, Service>.flowInvokator: RpcInvokator.FlowResponse<Protocol, Service>
    get() = invokator as RpcInvokator.FlowResponse<Protocol, Service>

@ExperimentalRpcApi
public sealed interface RpcInvokator<Protocol: RpcProtocol, Service : RpcService<Protocol, Service>> {
    @ExperimentalRpcApi
    public fun interface UnaryResponse<Protocol: RpcProtocol, Service : RpcService<Protocol, Service>> : RpcInvokator<Protocol, Service> {
        public suspend fun call(service: Service, arguments: Array<Any?>): Any?
    }

    @ExperimentalRpcApi
    public fun interface FlowResponse<Protocol: RpcProtocol, Service : RpcService<Protocol, Service>> : RpcInvokator<Protocol, Service> {
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
