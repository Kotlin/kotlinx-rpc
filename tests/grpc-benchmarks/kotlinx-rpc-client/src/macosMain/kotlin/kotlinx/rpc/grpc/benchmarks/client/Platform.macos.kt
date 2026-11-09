/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

internal actual val currentPlatform: String = "macos"

internal actual fun epochMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
