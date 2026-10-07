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

// BSR-only project: each source set declares one BSR module, no local .proto files. Unlike the
// "with_test_tasks" variant, the host/device test builders are not attached to the "test" source
// set tree, so androidDeviceTest is NOT wired to commonTest. The test verifies buf.yaml deps
// reflect this reduced hierarchy.
kotlin {
    jvm()
    androidLibrary {
        namespace = "com.example.namespace"
        compileSdk = 34

        withDeviceTestBuilder { }

        withHostTestBuilder { }
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
