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
            proto(files("common-main-dependency.zip"))
            protoImport(files("common-main-import-dependency.zip"))
        }
        commonTest.dependencies {
            proto(files("common-test-dependency.zip"))
            protoImport(files("common-test-import-dependency.zip"))
        }

        nativeMain.dependencies {
            proto(files("native-main-dependency.zip"))
            protoImport(files("native-main-import-dependency.zip"))
        }
        nativeTest.dependencies {
            proto(files("native-test-dependency.zip"))
            protoImport(files("native-test-import-dependency.zip"))
        }

        appleMain.dependencies {
            proto(files("apple-main-dependency.zip"))
            protoImport(files("apple-main-import-dependency.zip"))
        }
        appleTest.dependencies {
            proto(files("apple-test-dependency.zip"))
            protoImport(files("apple-test-import-dependency.zip"))
        }

        macosMain.dependencies {
            proto(files("macos-main-dependency.zip"))
            protoImport(files("macos-main-import-dependency.zip"))
        }
        macosTest.dependencies {
            proto(files("macos-test-dependency.zip"))
            protoImport(files("macos-test-import-dependency.zip"))
        }

        macosArm64Main.dependencies {
            proto(files("macos-arm64-main-dependency.zip"))
            protoImport(files("macos-arm64-main-import-dependency.zip"))
        }
        macosArm64Test.dependencies {
            proto(files("macos-arm64-test-dependency.zip"))
            protoImport(files("macos-arm64-test-import-dependency.zip"))
        }

        jvmMain.dependencies {
            proto(files("jvm-main-dependency.zip"))
            protoImport(files("jvm-main-import-dependency.zip"))
        }
        jvmTest.dependencies {
            proto(files("jvm-test-dependency.zip"))
            protoImport(files("jvm-test-import-dependency.zip"))
        }

        jsMain.dependencies {
            proto(files("js-main-dependency.zip"))
            protoImport(files("js-main-import-dependency.zip"))
        }
        jsTest.dependencies {
            proto(files("js-test-dependency.zip"))
            protoImport(files("js-test-import-dependency.zip"))
        }

        if ("<kotlin-version>" >= "2.2.20") {
            configureEach {
                if (name == "webMain") dependencies {
                    proto(files("web-main-dependency.zip"))
                    protoImport(files("web-main-import-dependency.zip"))
                }
            }
            configureEach {
                if (name == "webTest") dependencies {
                    proto(files("web-test-dependency.zip"))
                    protoImport(files("web-test-import-dependency.zip"))
                }
            }
        }
    }
}

rpc {
    protoc()
}
