/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.osacky.doctor")
}

doctor {
    enableTestCaching.assign(false)
    warnWhenNotUsingParallelGC.assign(true)
    disallowMultipleDaemons.assign(false)
    GCFailThreshold.assign(0.5f)
}
