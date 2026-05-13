/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    id("com.android.application") version "<android-version>"
    // No kotlin("android") plugin - using AGP 9.0+ built-in Kotlin
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

rpc {
    protoc()
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34
}

dependencies {
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
