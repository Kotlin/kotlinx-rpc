/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import util.other.optionalPropertyValue

/**
 * `true` when `kotlinx.rpc.apiWatchdog.skip=true`.
 *
 * Use this property when API Watchdog doesn't support the compiler version.
 */
val Project.apiWatchdogSkip: Boolean
    get() = optionalPropertyValue("skip", "apiWatchdog")
