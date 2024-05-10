/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.server.internal

import kotlinx.rpc.RPC
import kotlinx.rpc.internal.InternalRPCApi
import kotlinx.rpc.internal.RPCServiceMethodSerializationTypeProvider
import kotlinx.rpc.internal.findRPCProviderInCompanion
import kotlinx.rpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Utility method that returns [KType] for the class which is used to serialize method request with the [methodName]
 */
@InternalRPCApi
public inline fun <reified T : RPC> rpcServiceMethodSerializationTypeOf(methodName: String): KType? {
    return rpcServiceMethodSerializationTypeOf(T::class, methodName)
}

/**
 * Utility method that returns [KType] for the class which is used to serialize method request with the [methodName]
 */
@InternalRPCApi
public fun rpcServiceMethodSerializationTypeOf(serviceType: KType, methodName: String): KType? {
    return rpcServiceMethodSerializationTypeOf(serviceType.kClass<Any>(), methodName)
}

/**
 * Utility method that returns [KType] for the class which is used to serialize method request with the [methodName]
 */
@InternalRPCApi
public fun rpcServiceMethodSerializationTypeOf(serviceKClass: KClass<*>, methodName: String): KType? {
    return findRPCProviderInCompanion<RPCServiceMethodSerializationTypeProvider>(serviceKClass).methodTypeOf(methodName)
}
