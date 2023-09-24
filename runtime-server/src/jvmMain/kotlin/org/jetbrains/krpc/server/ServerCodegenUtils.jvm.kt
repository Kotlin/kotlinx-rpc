package org.jetbrains.krpc.server

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.RPCServiceMethodSerializationTypeProvider
import org.jetbrains.krpc.internal.kClass
import org.jetbrains.krpc.internal.withRPCClientObject
import kotlin.reflect.KClass
import kotlin.reflect.KType

actual inline fun <reified T : RPC> rpcServiceMethodSerializationTypeOf(methodName: String): KType? {
    return rpcServiceMethodSerializationTypeOf(T::class, methodName)
}

actual fun rpcServiceMethodSerializationTypeOf(serviceType: KType, methodName: String): KType? {
    return rpcServiceMethodSerializationTypeOf(serviceType.kClass(), methodName)
}

@OptIn(InternalKRPCApi::class)
fun <T : RPC> rpcServiceMethodSerializationTypeOf(kClass: KClass<T>, methodName: String): KType? {
    return withRPCClientObject<RPCServiceMethodSerializationTypeProvider>(kClass).methodTypeOf(methodName)
}
