/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.RemoteService
import kotlinx.rpc.RpcClient
import kotlinx.rpc.internal.*
import kotlinx.rpc.internal.utils.ExperimentalRPCApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

@ExperimentalRPCApi
public inline fun <reified T : RemoteService> serviceDescriptorOf(): RpcServiceDescriptor<T> {
    return serviceDescriptorOf(T::class)
}

@ExperimentalRPCApi
public fun <T : RemoteService> serviceDescriptorOf(kType: KType): RpcServiceDescriptor<T> {
    return serviceDescriptorOf(kType.kClass())
}

@ExperimentalRPCApi
public fun <T : RemoteService> serviceDescriptorOf(kClass: KClass<T>): RpcServiceDescriptor<T> {
    val maybeDescriptor = internalServiceDescriptorOf(kClass)
        ?: internalError("Unable to find a service descriptor of the $kClass")

    if (RpcServiceDescriptor::class.isInstance(maybeDescriptor)) {
        @Suppress("UNCHECKED_CAST")
        return maybeDescriptor as RpcServiceDescriptor<T>
    }

    internalError(
        "Located descriptor object is not of a desired type ${RpcServiceDescriptor::class}, " +
                "instead found $maybeDescriptor of the class " +
                (maybeDescriptor::class.qualifiedClassNameOrNull ?: maybeDescriptor::class)
    )
}

@ExperimentalRPCApi
public interface RpcServiceDescriptor<T : RemoteService> {
    public val fqName: String

    public fun getFields(service: T): List<RpcDeferredField<*>>

    public fun getCallable(name: String): RpcCallable<T>?

    public fun createInstance(serviceId: Long, client: RpcClient): T
}

@ExperimentalRPCApi
public class RpcCallable<T : RemoteService>(
    public val name: String,
    public val dataType: KType,
    public val returnType: KType,
    public val invokator: RpcInvokator<T>,
    public val parameters: Array<RpcParameter>,
)

@ExperimentalRPCApi
public sealed interface RpcInvokator<T : RemoteService> {
    @ExperimentalRPCApi
    public fun interface Method<T : RemoteService> : RpcInvokator<T> {
        public suspend fun call(service: T, data: Any?): Any?
    }

    @ExperimentalRPCApi
    public fun interface Field<T : RemoteService> : RpcInvokator<T> {
        public fun call(service: T): Any?
    }
}

@ExperimentalRPCApi
public class RpcParameter(public val name: String, public val type: KType)
