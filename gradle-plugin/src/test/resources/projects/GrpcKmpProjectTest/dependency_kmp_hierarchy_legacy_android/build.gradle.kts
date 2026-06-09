/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import kotlinx.rpc.protoc.*

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
    id("com.android.application") version "<android-version>"
}

kotlin {
    jvm()

    androidTarget {
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
        }

        unitTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
        }
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
    }
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
}

rpc {
    protoc()
}

configurations.all {
    if (name.endsWith("Proto") || name.endsWith("ProtoImport")) {
        println("configuration: $name")
    }
}

dependencies {
    // KMP android non-executable
    "androidMainProto"(files("zip/android-main-dependency.zip"))
    "androidMainProtoImport"(files("zip/android-main-import-dependency.zip"))

    "androidTestProto"(files("zip/android-test-dependency.zip"))
    "androidTestProtoImport"(files("zip/android-test-import-dependency.zip"))

    "androidUnitTestProto"(files("zip/android-unit-test-dependency.zip"))
    "androidUnitTestProtoImport"(files("zip/android-unit-test-import-dependency.zip"))

    "androidInstrumentedTestProto"(files("zip/android-instrumented-test-dependency.zip"))
    "androidInstrumentedTestProtoImport"(files("zip/android-instrumented-test-import-dependency.zip"))

    // KMP android executable (these are created in a wierd way and probably are not used in real life anyway)
    afterEvaluate {
        "androidDebugProto"(files("zip/android-debug-dependency.zip"))
        "androidDebugProtoImport"(files("zip/android-debug-import-dependency.zip"))

        "androidReleaseProto"(files("zip/android-release-dependency.zip"))
        "androidReleaseProtoImport"(files("zip/android-release-import-dependency.zip"))

        "androidUnitTestDebugProto"(files("zip/android-unit-test-debug-dependency.zip"))
        "androidUnitTestDebugProtoImport"(files("zip/android-unit-test-debug-import-dependency.zip"))

        "androidUnitTestReleaseProto"(files("zip/android-unit-test-release-dependency.zip"))
        "androidUnitTestReleaseProtoImport"(files("zip/android-unit-test-release-import-dependency.zip"))

        "androidInstrumentedTestDebugProto"(files("zip/android-instrumented-test-debug-dependency.zip"))
        "androidInstrumentedTestDebugProtoImport"(files("zip/android-instrumented-test-debug-import-dependency.zip"))
    }

    // legacy android non-executable
    proto(files("zip/main-dependency.zip"))
    protoImport(files("zip/main-import-dependency.zip"))

    testProto(files("zip/test-dependency.zip"))
    testProtoImport(files("zip/test-import-dependency.zip"))

    testFixturesProto(files("zip/test-fixtures-dependency.zip"))
    testFixturesProtoImport(files("zip/test-fixtures-import-dependency.zip"))

    testFixturesDebugProto(files("zip/test-fixtures-debug-dependency.zip"))
    testFixturesDebugProtoImport(files("zip/test-fixtures-debug-import-dependency.zip"))

    testFixturesReleaseProto(files("zip/test-fixtures-release-dependency.zip"))
    testFixturesReleaseProtoImport(files("zip/test-fixtures-release-import-dependency.zip"))

    androidTestProto(files("zip/android-test-dependency.zip"))
    androidTestProtoImport(files("zip/android-test-import-dependency.zip"))

    // legacy android variants
    debugProto(files("zip/debug-dependency.zip"))
    debugProtoImport(files("zip/debug-import-dependency.zip"))

    releaseProto(files("zip/release-dependency.zip"))
    releaseProtoImport(files("zip/release-import-dependency.zip"))

    testDebugProto(files("zip/test-debug-dependency.zip"))
    testDebugProtoImport(files("zip/test-debug-import-dependency.zip"))

    testReleaseProto(files("zip/test-release-dependency.zip"))
    testReleaseProtoImport(files("zip/test-release-import-dependency.zip"))

    androidTestDebugProto(files("zip/android-test-debug-dependency.zip"))
    androidTestDebugProtoImport(files("zip/android-test-debug-import-dependency.zip"))
}
