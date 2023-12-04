package org.jetbrains.krpc.internal.logging

import mu.KLogger
import mu.KotlinLogging

fun CommonLogger.Companion.initialized(): CommonLogger.Companion = apply {
    CommonLoggerFactoryImpl.init()
}

internal object CommonLoggerFactoryImpl : CommonLoggerFactory {
    fun init() {
        CommonLogger.upload(this)
    }

    override fun getLogger(name: String): CommonLogger {
        return CommonLoggerImpl(KotlinLogging.logger(name))
    }

    override fun getLogger(func: () -> Unit): CommonLogger {
       return CommonLoggerImpl(KotlinLogging.logger(func))
    }
}

internal class CommonLoggerImpl(private val logger: KLogger) : CommonLogger {
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