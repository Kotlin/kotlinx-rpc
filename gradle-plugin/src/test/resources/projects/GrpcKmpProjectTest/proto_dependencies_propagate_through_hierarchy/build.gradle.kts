/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.protoc.*
import kotlinx.rpc.buf.*
import org.gradle.kotlin.dsl.version

plugins {
    kotlin("multiplatform") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

kotlin {
    macosArm64()
}

dependencies {
    commonMainProto(files("zip/dependency-protos.zip"))
}

rpc {
    protoc()
}