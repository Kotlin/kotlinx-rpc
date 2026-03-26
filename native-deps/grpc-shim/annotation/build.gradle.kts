/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.registerNativeDependencyTargets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("conventions-publishing")
}

kotlin {
    explicitApi()
    registerNativeDependencyTargets()
}
