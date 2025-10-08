/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.RpcClient
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.*
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

@ExperimentalRpcApi
public inline fun <@Rpc reified T : Any> serviceDescriptorOf(): RpcServiceDescriptor<T> {
    return serviceDescriptorOf(T::class)
}

@ExperimentalRpcApi
public fun <@Rpc T : Any> serviceDescriptorOf(kType: KType): RpcServiceDescriptor<T> {
    return serviceDescriptorOf(kType.rpcInternalKClass())
}

@ExperimentalRpcApi
public fun <@Rpc T : Any> serviceDescriptorOf(kClass: KClass<T>): RpcServiceDescriptor<T> {
    val maybeDescriptor = internalServiceDescriptorOf(kClass)
        ?: internalRpcError("Unable to find a service descriptor of the $kClass")

    if (maybeDescriptor is RpcServiceDescriptor<*>) {
        @Suppress("UNCHECKED_CAST")
        return maybeDescriptor as RpcServiceDescriptor<T>
    }

    internalRpcError(
        "Located descriptor object is not of a desired type ${RpcServiceDescriptor::class}, " +
                "instead found $maybeDescriptor of the class " +
                (maybeDescriptor::class.rpcInternalQualifiedClassNameOrNull ?: maybeDescriptor::class)
    )
}

@ExperimentalRpcApi
public interface RpcServiceDescriptor<@Rpc T : Any> {
    public val fqName: String

    public fun getCallable(name: String): RpcCallable<T>?

    public val callableMap: Map<String, RpcCallable<T>>

    public fun createInstance(serviceId: Long, client: RpcClient): T
}

@ExperimentalRpcApi
public interface RpcCallable<@Rpc T : Any> {
    public val name: String
    public val returnType: RpcType
    public val invokator: RpcInvokator<T>
    public val parameters: Array<out RpcParameter>
    public val isNonSuspendFunction: Boolean
}

@ExperimentalRpcApi
public sealed interface RpcInvokator<@Rpc T : Any> {
    @ExperimentalRpcApi
    public fun interface Method<@Rpc T : Any> : RpcInvokator<T> {
        public suspend fun call(service: T, parameters: Array<Any?>): Any?
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
