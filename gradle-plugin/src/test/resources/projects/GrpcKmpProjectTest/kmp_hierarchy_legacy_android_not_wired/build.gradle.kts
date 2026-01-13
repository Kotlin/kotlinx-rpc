/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
    id("com.android.application") version "<android-version>"
}

kotlin {
    jvm()

    androidTarget { }
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
}

rpc {
    protoc()
}
