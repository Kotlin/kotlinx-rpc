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
    macosArm64()
}

rpc {
    protoc()
}

dependencies {
    // Declared on commonMain only. Must propagate to jvmMain and macosArm64Main
    // via extendsFrom — the entire point of "configurations are transitive".
    commonMainProtoImport(files("common-main-import-dependency.zip"))
}
