package org.jetbrains.krpc.internal

import org.jetbrains.krpc.RPC
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.findAssociatedObject

@InternalKRPCApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
//@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class WithRPCClientObject(
    @Suppress("unused")
    val client: KClass<out RPCClientObject<out RPC>>
)

@InternalKRPCApi
@OptIn(ExperimentalAssociatedObjects::class)
fun <R> withRPCClientObject(kClass: KClass<*>): R {
    @Suppress("UNCHECKED_CAST")
    return kClass.findAssociatedObject<WithRPCClientObject>() as? R
        ?: internalError("unable to find $kClass rpc client object")
}
