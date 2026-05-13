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
    "androidMainProto"(files("android-main-dependency.zip"))
    "androidMainProtoImport"(files("android-main-import-dependency.zip"))

    "androidTestProto"(files("android-test-dependency.zip"))
    "androidTestProtoImport"(files("android-test-import-dependency.zip"))

    "androidUnitTestProto"(files("android-unit-test-dependency.zip"))
    "androidUnitTestProtoImport"(files("android-unit-test-import-dependency.zip"))

    "androidInstrumentedTestProto"(files("android-instrumented-test-dependency.zip"))
    "androidInstrumentedTestProtoImport"(files("android-instrumented-test-import-dependency.zip"))

    // KMP android executable (these are created in a wierd way and probably are not used in real life anyway)
    afterEvaluate {
        "androidDebugProto"(files("android-debug-dependency.zip"))
        "androidDebugProtoImport"(files("android-debug-import-dependency.zip"))

        "androidReleaseProto"(files("android-release-dependency.zip"))
        "androidReleaseProtoImport"(files("android-release-import-dependency.zip"))

        "androidUnitTestDebugProto"(files("android-unit-test-debug-dependency.zip"))
        "androidUnitTestDebugProtoImport"(files("android-unit-test-debug-import-dependency.zip"))

        "androidUnitTestReleaseProto"(files("android-unit-test-release-dependency.zip"))
        "androidUnitTestReleaseProtoImport"(files("android-unit-test-release-import-dependency.zip"))

        "androidInstrumentedTestDebugProto"(files("android-instrumented-test-debug-dependency.zip"))
        "androidInstrumentedTestDebugProtoImport"(files("android-instrumented-test-debug-import-dependency.zip"))
    }

    // legacy android non-executable
    proto(files("main-dependency.zip"))
    protoImport(files("main-import-dependency.zip"))

    testProto(files("test-dependency.zip"))
    testProtoImport(files("test-import-dependency.zip"))

    testFixturesProto(files("test-fixtures-dependency.zip"))
    testFixturesProtoImport(files("test-fixtures-import-dependency.zip"))

    testFixturesDebugProto(files("test-fixtures-debug-dependency.zip"))
    testFixturesDebugProtoImport(files("test-fixtures-debug-import-dependency.zip"))

    testFixturesReleaseProto(files("test-fixtures-release-dependency.zip"))
    testFixturesReleaseProtoImport(files("test-fixtures-release-import-dependency.zip"))

    androidTestProto(files("android-test-dependency.zip"))
    androidTestProtoImport(files("android-test-import-dependency.zip"))

    // legacy android variants
    debugProto(files("debug-dependency.zip"))
    debugProtoImport(files("debug-import-dependency.zip"))

    releaseProto(files("release-dependency.zip"))
    releaseProtoImport(files("release-import-dependency.zip"))

    testDebugProto(files("test-debug-dependency.zip"))
    testDebugProtoImport(files("test-debug-import-dependency.zip"))

    testReleaseProto(files("test-release-dependency.zip"))
    testReleaseProtoImport(files("test-release-import-dependency.zip"))

    androidTestDebugProto(files("android-test-debug-dependency.zip"))
    androidTestDebugProtoImport(files("android-test-debug-import-dependency.zip"))
}
