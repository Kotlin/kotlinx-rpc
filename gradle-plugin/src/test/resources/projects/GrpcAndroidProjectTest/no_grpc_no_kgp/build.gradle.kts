/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    id("com.android.application") version "<android-version>"
    // No kotlin("android") plugin - using AGP 9.0+ built-in Kotlin
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}
