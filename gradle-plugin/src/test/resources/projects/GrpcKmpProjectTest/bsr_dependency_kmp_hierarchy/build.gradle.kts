/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*
import kotlinx.rpc.buf.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

// This project declares only BSR module dependencies (no local .proto files) on each source set.
// The goal is to verify that the generated buf.yaml for every source set contains its own module
// plus every module inherited from its parent source sets in the KMP hierarchy.
kotlin {
    jvm()
    js {
        nodejs()
    }
    macosArm64()

    sourceSets {
        commonMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/common-main") }
        }
        commonTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/common-test") }
        }

        nativeMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/native-main") }
        }
        nativeTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/native-test") }
        }

        appleMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/apple-main") }
        }
        appleTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/apple-test") }
        }

        macosMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/macos-main") }
        }
        macosTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/macos-test") }
        }

        macosArm64Main.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/macos-arm64-main") }
        }
        macosArm64Test.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/macos-arm64-test") }
        }

        jvmMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/jvm-main") }
        }
        jvmTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/jvm-test") }
        }

        jsMain.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/js-main") }
        }
        jsTest.proto {
            bsrDeps { module("buf.build/kotlinx-rpc/js-test") }
        }

        // webMain/webTest only exist on Kotlin >= 2.2.20.
        if ("<kotlin-version>" >= "2.2.20") {
            configureEach {
                when (name) {
                    "webMain" -> proto {
                        bsrDeps { module("buf.build/kotlinx-rpc/web-main") }
                    }
                    "webTest" -> proto {
                        bsrDeps { module("buf.build/kotlinx-rpc/web-test") }
                    }
                }
            }
        }
    }
}

rpc {
    protoc()
}
