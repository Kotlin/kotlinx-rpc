/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal.logging

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public interface DumpLogger {
    public val isEnabled: Boolean

    public fun dump(vararg tags: String, message: () -> String)
}

@InternalRpcApi
public object DumpLoggerContainer {
    private var logger: DumpLogger? = null

    public fun set(logger: DumpLogger?) {
        DumpLoggerContainer.logger = logger
    }

    public fun provide(): DumpLogger {
        return object : DumpLogger {
            override val isEnabled: Boolean get() = logger?.isEnabled ?: false

            override fun dump(vararg tags: String, message: () -> String) {
                if (isEnabled) {
                    logger?.dump(*tags, message = message)
                }
            }
        }
    }
}
