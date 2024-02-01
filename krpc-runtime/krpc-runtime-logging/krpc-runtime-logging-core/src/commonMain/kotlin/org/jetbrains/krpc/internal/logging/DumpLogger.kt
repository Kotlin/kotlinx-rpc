/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.logging

import org.jetbrains.krpc.internal.InternalKRPCApi

@InternalKRPCApi
public interface DumpLogger {
    public val isEnabled: Boolean

    public fun dump(vararg tags: String, message: () -> String)
}

@InternalKRPCApi
public object DumpLoggerContainer {
    private var logger: DumpLogger? = null

    public fun set(logger: DumpLogger?) {
        this.logger = logger
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
