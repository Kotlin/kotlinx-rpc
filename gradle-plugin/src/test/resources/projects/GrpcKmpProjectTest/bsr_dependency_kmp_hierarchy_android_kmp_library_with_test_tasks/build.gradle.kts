/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*
import kotlinx.rpc.buf.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
    id("com.android.kotlin.multiplatform.library") version "<android-version>"
}

// BSR-only project: each source set declares one BSR module, no local .proto files. The test
// verifies that the generated buf.yaml for every source set contains its own module plus every
// module inherited from its parent source sets in the KMP hierarchy.
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
        commonMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/common-main") }
        }
        commonTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/common-test") }
        }

        jvmMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/jvm-main") }
        }
        jvmTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/jvm-test") }
        }

        androidMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/android-main") }
        }

        named("androidHostTest").proto {
            bsrDeps { module("buf.build/kotlinx-rpc/android-host-test") }
        }
        named("androidDeviceTest").proto {
            bsrDeps { module("buf.build/kotlinx-rpc/android-device-test") }
        }
    }
}

rpc {
    protoc()
}
