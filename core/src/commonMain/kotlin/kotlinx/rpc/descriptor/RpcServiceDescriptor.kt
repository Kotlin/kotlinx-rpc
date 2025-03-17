/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.RpcClient
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.*
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
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

    @InternalRpcApi
    @Deprecated(
        "Fields are deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
        level = DeprecationLevel.WARNING,
    )
    public fun getFields(service: T): List<RpcDeferredField<*>>

    public fun getCallable(name: String): RpcCallable<T>?

    public fun createInstance(serviceId: Long, client: RpcClient): T
}

@ExperimentalRpcApi
public class RpcCallable<@Rpc T : Any>(
    public val name: String,
    public val dataType: RpcType,
    public val returnType: RpcType,
    public val invokator: RpcInvokator<T>,
    public val parameters: Array<RpcParameter>,
    public val isNonSuspendFunction: Boolean,
)

@ExperimentalRpcApi
public sealed interface RpcInvokator<@Rpc T : Any> {
    @ExperimentalRpcApi
    public fun interface Method<@Rpc T : Any> : RpcInvokator<T> {
        public suspend fun call(service: T, data: Any?): Any?
    }

    @ExperimentalRpcApi
    @Deprecated(
        "Fields are deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
        level = DeprecationLevel.WARNING,
    )
    public fun interface Field<@Rpc T : Any> : RpcInvokator<T> {
        public fun call(service: T): Any?
    }
}

@ExperimentalRpcApi
public class RpcParameter(public val name: String, public val type: RpcType)

@ExperimentalRpcApi
public class RpcType(public val kType: KType) {
    override fun toString(): String {
        return kType.toString()
    }
}
