/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.RemoteService
import kotlinx.rpc.RpcClient
import kotlinx.rpc.internal.*
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

@ExperimentalRpcApi
public inline fun <reified T : RemoteService> serviceDescriptorOf(): RpcServiceDescriptor<T> {
    return serviceDescriptorOf(T::class)
}

@ExperimentalRpcApi
public fun <T : RemoteService> serviceDescriptorOf(kType: KType): RpcServiceDescriptor<T> {
    return serviceDescriptorOf(kType.kClass())
}

@ExperimentalRpcApi
public fun <T : RemoteService> serviceDescriptorOf(kClass: KClass<T>): RpcServiceDescriptor<T> {
    val maybeDescriptor = internalServiceDescriptorOf(kClass)
        ?: internalError("Unable to find a service descriptor of the $kClass")

    if (maybeDescriptor is RpcServiceDescriptor<*>) {
        @Suppress("UNCHECKED_CAST")
        return maybeDescriptor as RpcServiceDescriptor<T>
    }

    internalError(
        "Located descriptor object is not of a desired type ${RpcServiceDescriptor::class}, " +
                "instead found $maybeDescriptor of the class " +
                (maybeDescriptor::class.qualifiedClassNameOrNull ?: maybeDescriptor::class)
    )
}

@ExperimentalRpcApi
public interface RpcServiceDescriptor<T : RemoteService> {
    public val fqName: String

    @InternalRpcApi
    public fun getFields(service: T): List<RpcDeferredField<*>>

    public fun getCallable(name: String): RpcCallable<T>?

    public fun createInstance(serviceId: Long, client: RpcClient): T
}

@ExperimentalRpcApi
public class RpcCallable<T : RemoteService>(
    public val name: String,
    public val dataType: RpcType,
    public val returnType: RpcType,
    public val invokator: RpcInvokator<T>,
    public val parameters: Array<RpcParameter>,
)

@ExperimentalRpcApi
public sealed interface RpcInvokator<T : RemoteService> {
    @ExperimentalRpcApi
    public fun interface Method<T : RemoteService> : RpcInvokator<T> {
        public suspend fun call(service: T, data: Any?): Any?
    }

    @ExperimentalRpcApi
    public fun interface Field<T : RemoteService> : RpcInvokator<T> {
        public fun call(service: T): Any?
    }
}

@ExperimentalRpcApi
public class RpcParameter(public val name: String, public val type: RpcType)

@ExperimentalRpcApi
public class RpcType(public val kType: KType) {
    override fun toString(): String {
        return return kType.toString()
    }
}
