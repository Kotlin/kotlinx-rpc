/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

class TestLogAppender : AppenderBase<ILoggingEvent>() {
    init {
        start()
    }

    val events = mutableListOf<ILoggingEvent>()
    val errors get() = events.filter { it.level == Level.ERROR }
    val warnings get() = events.filter { it.level == Level.WARN }

    override fun append(eventObject: ILoggingEvent) {
        events.add(eventObject)
    }
}
