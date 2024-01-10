/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.logging

import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.service.CompanionServiceContainer
import kotlin.reflect.KClass

@InternalKRPCApi
interface CommonLoggerFactory {
    fun getLogger(name: String): CommonLogger

    fun getLogger(func: () -> Unit): CommonLogger
}

@InternalKRPCApi
interface CommonLogger {
    fun debug(msg: () -> Any?)

    fun debug(t: Throwable?, msg: () -> Any?)

    fun error(msg: () -> Any?)

    fun error(t: Throwable?, msg: () -> Any?)

    fun info(msg: () -> Any?)

    fun info(t: Throwable?, msg: () -> Any?)

    fun trace(msg: () -> Any?)

    fun trace(t: Throwable?, msg: () -> Any?)

    fun warn(msg: () -> Any?)

    fun warn(t: Throwable?, msg: () -> Any?)

    companion object : CompanionServiceContainer<CommonLoggerFactory>(CommonLoggerFactory::class) {
        private val factory by lazy { loadService() }

        fun logger(name: String): CommonLogger {
            return factory.getLogger(name)
        }

        fun <T : Any> logger(kClass: KClass<T>): CommonLogger {
            return logger(kClass::class.simpleName ?: kClass.toString())
        }

        inline fun <reified T : Any> logger(): CommonLogger {
            return logger(T::class)
        }

        fun logger(func: () -> Unit = {}): CommonLogger {
            return factory.getLogger(func)
        }
    }
}
