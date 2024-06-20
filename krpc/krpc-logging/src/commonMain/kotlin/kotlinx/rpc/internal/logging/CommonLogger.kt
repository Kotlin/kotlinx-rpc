/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.logging

import kotlinx.rpc.internal.InternalRPCApi
import kotlinx.rpc.internal.logging.impl.CommonLoggerFactoryImpl
import kotlin.reflect.KClass

@InternalRPCApi
public interface CommonLoggerFactory {
    public fun getLogger(name: String): CommonLogger

    public fun getLogger(func: () -> Unit): CommonLogger
}

@InternalRPCApi
public interface CommonLogger {
    public fun debug(msg: () -> Any?)

    public fun debug(t: Throwable?, msg: () -> Any?)

    public fun error(msg: () -> Any?)

    public fun error(t: Throwable?, msg: () -> Any?)

    public fun info(msg: () -> Any?)

    public fun info(t: Throwable?, msg: () -> Any?)

    public fun trace(msg: () -> Any?)

    public fun trace(t: Throwable?, msg: () -> Any?)

    public fun warn(msg: () -> Any?)

    public fun warn(t: Throwable?, msg: () -> Any?)

    @InternalRPCApi
    public companion object {
        private val factory = CommonLoggerFactoryImpl

        public fun logger(name: String): CommonLogger {
            return factory.getLogger(name)
        }

        public fun <T : Any> logger(kClass: KClass<T>): CommonLogger {
            return logger(kClass::class.simpleName ?: kClass.toString())
        }

        public inline fun <reified T : Any> logger(): CommonLogger {
            return logger(T::class)
        }

        public fun logger(func: () -> Unit = {}): CommonLogger {
            return factory.getLogger(func)
        }
    }
}
