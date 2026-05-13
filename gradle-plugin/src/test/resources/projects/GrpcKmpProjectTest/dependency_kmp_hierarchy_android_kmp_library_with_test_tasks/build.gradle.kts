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

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        withHostTestBuilder {
            sourceSetTreeName = "test"
        }
    }

    sourceSets {
        commonMain.dependencies {
            proto(files("common-main-dependency.zip"))
            protoImport(files("common-main-import-dependency.zip"))
        }
        commonTest.dependencies {
            proto(files("common-test-dependency.zip"))
            protoImport(files("common-test-import-dependency.zip"))
        }

        jvmMain.dependencies {
            proto(files("jvm-main-dependency.zip"))
            protoImport(files("jvm-main-import-dependency.zip"))
        }
        jvmTest.dependencies {
            proto(files("jvm-test-dependency.zip"))
            protoImport(files("jvm-test-import-dependency.zip"))
        }

        androidMain.dependencies {
            proto(files("android-main-dependency.zip"))
            protoImport(files("android-main-import-dependency.zip"))
        }

        named("androidHostTest").dependencies {
            proto(files("android-host-test-dependency.zip"))
            protoImport(files("android-host-test-import-dependency.zip"))
        }

        named("androidDeviceTest").dependencies {
            proto(files("android-device-test-dependency.zip"))
            protoImport(files("android-device-test-import-dependency.zip"))
        }
    }
}

rpc {
    protoc()
}
