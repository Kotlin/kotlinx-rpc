/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlinx.rpc) apply false
}

allprojects {
    version = "0.1"

    repositories {
        mavenCentral()
    }
}
