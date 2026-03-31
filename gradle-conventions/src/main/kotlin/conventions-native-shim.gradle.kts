/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.registerNativeDependencyTargets

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("conventions-publishing")
    id("conventions-common")
}

kotlin {
    registerNativeDependencyTargets()

    sourceSets {
        nativeMain {
            dependencies {
                implementation(project(":kotlinx-rpc-native-shims-annotation"))
            }
        }
    }
}
