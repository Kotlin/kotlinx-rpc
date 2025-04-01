/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal.logging.impl

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLoggerFactory

internal object RpcInternalCommonLoggerFactoryImpl : RpcInternalCommonLoggerFactory {
    override fun getLogger(name: String): RpcInternalCommonLogger {
        return RpcInternalCommonLoggerImpl(KotlinLogging.logger(name))
    }

    override fun getLogger(func: () -> Unit): RpcInternalCommonLogger {
       return RpcInternalCommonLoggerImpl(KotlinLogging.logger(func))
    }
}

internal class RpcInternalCommonLoggerImpl(private val logger: KLogger) : RpcInternalCommonLogger {
    override fun debug(msg: () -> Any?) {
        logger.debug(msg)
    }

    override fun debug(t: Throwable?, msg: () -> Any?) {
        logger.debug(t, msg)
    }

    override fun error(msg: () -> Any?) {
        logger.error(msg)
    }

    override fun error(t: Throwable?, msg: () -> Any?) {
        logger.error(t, msg)
    }

    override fun info(msg: () -> Any?) {
        logger.info(msg)
    }

    override fun info(t: Throwable?, msg: () -> Any?) {
        logger.info(t, msg)
    }

    override fun trace(msg: () -> Any?) {
        logger.trace(msg)
    }

    override fun trace(t: Throwable?, msg: () -> Any?) {
        logger.trace(t, msg)
    }

    override fun warn(msg: () -> Any?) {
        logger.warn(msg)
    }

    override fun warn(t: Throwable?, msg: () -> Any?) {
        logger.warn(t, msg)
    }
}
