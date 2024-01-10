/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.server.internal

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.RPCServiceMethodSerializationTypeProvider
import org.jetbrains.krpc.internal.findRPCProviderInCompanion
import org.jetbrains.krpc.internal.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Utility method that returns [KType] for the class which is used to serialize method request with the [methodName]
 */
@InternalKRPCApi
inline fun <reified T : RPC> rpcServiceMethodSerializationTypeOf(methodName: String): KType? {
    return rpcServiceMethodSerializationTypeOf(T::class, methodName)
}

/**
 * Utility method that returns [KType] for the class which is used to serialize method request with the [methodName]
 */
@InternalKRPCApi
fun rpcServiceMethodSerializationTypeOf(serviceType: KType, methodName: String): KType? {
    return rpcServiceMethodSerializationTypeOf(serviceType.kClass<Any>(), methodName)
}

/**
 * Utility method that returns [KType] for the class which is used to serialize method request with the [methodName]
 */
@InternalKRPCApi
fun rpcServiceMethodSerializationTypeOf(serviceKClass: KClass<*>, methodName: String): KType? {
    return findRPCProviderInCompanion<RPCServiceMethodSerializationTypeProvider>(serviceKClass).methodTypeOf(methodName)
}
