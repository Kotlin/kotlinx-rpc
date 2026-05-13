/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    id("com.android.application") version "<android-version>"
    kotlin("android") version "<kotlin-version>"
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
