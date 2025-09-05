/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal.logging

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public interface RpcInternalDumpLogger {
    public val isEnabled: Boolean

    public fun dump(vararg tags: String, message: () -> String)
}

@InternalRpcApi
public fun RpcInternalCommonLogger.dumpLogger(): RpcInternalDumpLogger {
    return object : RpcInternalDumpLogger {
        override val isEnabled: Boolean = true

        override fun dump(vararg tags: String, message: () -> String) {
            this@dumpLogger.info { "${tags.joinToString(" ") { "[$it]" }} ${message()}" }
        }
    }
}

@InternalRpcApi
public object RpcInternalDumpLoggerContainer {
    private var logger: RpcInternalDumpLogger? = null

    public fun set(logger: RpcInternalDumpLogger?) {
        RpcInternalDumpLoggerContainer.logger = logger
    }

    public fun provide(): RpcInternalDumpLogger {
        return object : RpcInternalDumpLogger {
            override val isEnabled: Boolean get() = logger?.isEnabled ?: false

            override fun dump(vararg tags: String, message: () -> String) {
                if (isEnabled) {
                    logger?.dump(*tags, message = message)
                }
            }
        }
    }
}
