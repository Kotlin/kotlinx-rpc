/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.krpc)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()
    js {
        browser()
        binaries.executable()
    }
}

dependencies {
    commonMainApi(libs.kotlin.stdlib)
    commonMainApi(libs.kotlinx.serialization.json)
    commonMainApi(libs.ktor.client.core)
    commonMainApi(libs.kotlinx.coroutines.core)
    commonMainApi(libs.krpc.runtime)
}
