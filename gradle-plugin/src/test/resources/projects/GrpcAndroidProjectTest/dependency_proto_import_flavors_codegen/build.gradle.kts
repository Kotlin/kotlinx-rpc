/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    id("com.android.application") version "<android-version>"
    kotlin("android") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

rpc {
    protoc()
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34

    flavorDimensions("abi")

    productFlavors {
        create("arm") {
            dimension = "abi"
        }
        create("x86") {
            dimension = "abi"
        }
    }
}

dependencies {
    // protoImport on main — inherited by all variants (debug, release, arm*, x86*)
    protoImport(files("dependency-protos.zip"))
}
