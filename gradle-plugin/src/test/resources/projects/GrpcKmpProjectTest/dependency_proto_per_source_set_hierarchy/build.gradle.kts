/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

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

    // Explicitly apply the default hierarchy template to materialize intermediate source sets
    // (nativeMain/Test, appleMain/Test, macosMain/Test, webMain/Test) synchronously. Without
    // this call the template would auto-apply only at the end of evaluation, and the matching
    // <set>Proto/<set>ProtoImport configurations wouldn't exist when the top-level
    // `dependencies { }` block below evaluates.
    applyDefaultHierarchyTemplate()
}

rpc {
    protoc()
}

// Each source set declares its own pair of zip dependencies:
//   <set>Proto       — codegen, propagates to descendants as codegen via extendsFrom
//   <set>ProtoImport — import-only, propagates to descendants as imports via extendsFrom
dependencies {
    "commonMainProto"(files("common-main-dependency.zip"))
    "commonMainProtoImport"(files("common-main-import-dependency.zip"))
    "commonTestProto"(files("common-test-dependency.zip"))
    "commonTestProtoImport"(files("common-test-import-dependency.zip"))

    "nativeMainProto"(files("native-main-dependency.zip"))
    "nativeMainProtoImport"(files("native-main-import-dependency.zip"))
    "nativeTestProto"(files("native-test-dependency.zip"))
    "nativeTestProtoImport"(files("native-test-import-dependency.zip"))

    "appleMainProto"(files("apple-main-dependency.zip"))
    "appleMainProtoImport"(files("apple-main-import-dependency.zip"))
    "appleTestProto"(files("apple-test-dependency.zip"))
    "appleTestProtoImport"(files("apple-test-import-dependency.zip"))

    "macosMainProto"(files("macos-main-dependency.zip"))
    "macosMainProtoImport"(files("macos-main-import-dependency.zip"))
    "macosTestProto"(files("macos-test-dependency.zip"))
    "macosTestProtoImport"(files("macos-test-import-dependency.zip"))

    "macosArm64MainProto"(files("macos-arm64-main-dependency.zip"))
    "macosArm64MainProtoImport"(files("macos-arm64-main-import-dependency.zip"))
    "macosArm64TestProto"(files("macos-arm64-test-dependency.zip"))
    "macosArm64TestProtoImport"(files("macos-arm64-test-import-dependency.zip"))

    "jvmMainProto"(files("jvm-main-dependency.zip"))
    "jvmMainProtoImport"(files("jvm-main-import-dependency.zip"))
    "jvmTestProto"(files("jvm-test-dependency.zip"))
    "jvmTestProtoImport"(files("jvm-test-import-dependency.zip"))

    "webMainProto"(files("web-main-dependency.zip"))
    "webMainProtoImport"(files("web-main-import-dependency.zip"))
    "webTestProto"(files("web-test-dependency.zip"))
    "webTestProtoImport"(files("web-test-import-dependency.zip"))

    "jsMainProto"(files("js-main-dependency.zip"))
    "jsMainProtoImport"(files("js-main-import-dependency.zip"))
    "jsTestProto"(files("js-test-dependency.zip"))
    "jsTestProtoImport"(files("js-test-import-dependency.zip"))
}
