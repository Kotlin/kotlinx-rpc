/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal.logging

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.internal.logging.impl.RpcInternalCommonLoggerFactoryImpl
import kotlin.reflect.KClass

@InternalRpcApi
public interface RpcInternalCommonLoggerFactory {
    public fun getLogger(name: String): RpcInternalCommonLogger

    public fun getLogger(func: () -> Unit): RpcInternalCommonLogger
}

@InternalRpcApi
public interface RpcInternalCommonLogger {
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

    @InternalRpcApi
    public companion object {
        private val factory = RpcInternalCommonLoggerFactoryImpl

        public fun logger(name: String): RpcInternalCommonLogger {
            return factory.getLogger(name)
        }

        public fun <T : Any> logger(kClass: KClass<T>): RpcInternalCommonLogger {
            return logger(kClass.qualifiedName ?: kClass.toString())
        }

        public inline fun <reified T : Any> logger(): RpcInternalCommonLogger {
            return logger(T::class)
        }

        public fun logger(func: () -> Unit = {}): RpcInternalCommonLogger {
            return factory.getLogger(func)
        }
    }
}
