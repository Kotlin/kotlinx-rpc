/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
    id("com.android.kotlin.multiplatform.library") version "<android-version>"
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "com.example.namespace"
        compileSdk = 34
    }

    sourceSets {
        commonMain.dependencies {
            proto(files("zip/common-main-dependency.zip"))
            protoImport(files("zip/common-main-import-dependency.zip"))
        }
        commonTest.dependencies {
            proto(files("zip/common-test-dependency.zip"))
            protoImport(files("zip/common-test-import-dependency.zip"))
        }

        jvmMain.dependencies {
            proto(files("zip/jvm-main-dependency.zip"))
            protoImport(files("zip/jvm-main-import-dependency.zip"))
        }
        jvmTest.dependencies {
            proto(files("zip/jvm-test-dependency.zip"))
            protoImport(files("zip/jvm-test-import-dependency.zip"))
        }

        androidMain.dependencies {
            proto(files("zip/android-main-dependency.zip"))
            protoImport(files("zip/android-main-import-dependency.zip"))
        }
    }
}

rpc {
    protoc()
}
