/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import kotlinx.rpc.protoc.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

kotlin {
    jvm()
    js {
        nodejs()
    }
    macosArm64()

    sourceSets {
        commonMain.dependencies {
            proto(files("zip/common-main-dependency.zip"))
            protoImport(files("zip/common-main-import-dependency.zip"))
        }
        commonTest.dependencies {
            proto(files("zip/common-test-dependency.zip"))
            protoImport(files("zip/common-test-import-dependency.zip"))
        }

        nativeMain.dependencies {
            proto(files("zip/native-main-dependency.zip"))
            protoImport(files("zip/native-main-import-dependency.zip"))
        }
        nativeTest.dependencies {
            proto(files("zip/native-test-dependency.zip"))
            protoImport(files("zip/native-test-import-dependency.zip"))
        }

        appleMain.dependencies {
            proto(files("zip/apple-main-dependency.zip"))
            protoImport(files("zip/apple-main-import-dependency.zip"))
        }
        appleTest.dependencies {
            proto(files("zip/apple-test-dependency.zip"))
            protoImport(files("zip/apple-test-import-dependency.zip"))
        }

        macosMain.dependencies {
            proto(files("zip/macos-main-dependency.zip"))
            protoImport(files("zip/macos-main-import-dependency.zip"))
        }
        macosTest.dependencies {
            proto(files("zip/macos-test-dependency.zip"))
            protoImport(files("zip/macos-test-import-dependency.zip"))
        }

        macosArm64Main.dependencies {
            proto(files("zip/macos-arm64-main-dependency.zip"))
            protoImport(files("zip/macos-arm64-main-import-dependency.zip"))
        }
        macosArm64Test.dependencies {
            proto(files("zip/macos-arm64-test-dependency.zip"))
            protoImport(files("zip/macos-arm64-test-import-dependency.zip"))
        }

        jvmMain.dependencies {
            proto(files("zip/jvm-main-dependency.zip"))
            protoImport(files("zip/jvm-main-import-dependency.zip"))
        }
        jvmTest.dependencies {
            proto(files("zip/jvm-test-dependency.zip"))
            protoImport(files("zip/jvm-test-import-dependency.zip"))
        }

        jsMain.dependencies {
            proto(files("zip/js-main-dependency.zip"))
            protoImport(files("zip/js-main-import-dependency.zip"))
        }
        jsTest.dependencies {
            proto(files("zip/js-test-dependency.zip"))
            protoImport(files("zip/js-test-import-dependency.zip"))
        }

        if ("<kotlin-version>" >= "2.2.20") {
            configureEach {
                if (name == "webMain") dependencies {
                    proto(files("zip/web-main-dependency.zip"))
                    protoImport(files("zip/web-main-import-dependency.zip"))
                }
            }
            configureEach {
                if (name == "webTest") dependencies {
                    proto(files("zip/web-test-dependency.zip"))
                    protoImport(files("zip/web-test-import-dependency.zip"))
                }
            }
        }
    }
}

rpc {
    protoc()
}
