/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.osacky.doctor")
}

doctor {
    enableTestCaching = false
    warnWhenNotUsingParallelGC = true
    disallowMultipleDaemons = true
    GCFailThreshold = 0.5f
}
