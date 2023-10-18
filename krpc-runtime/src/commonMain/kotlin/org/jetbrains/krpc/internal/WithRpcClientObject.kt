package org.jetbrains.krpc.internal

import kotlin.reflect.KClass

@InternalKRPCApi
expect fun <R> findRPCProviderInCompanion(kClass: KClass<*>): R