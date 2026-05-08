/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.kotlin.dsl.version

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

rpc {
    protoc()
}

dependencies {
    // Per-source-set / per-configuration zips: distinct content for each
    // archive so a regression that wires extractProtoMain and
    // extractProtoImportMain to the same source would surface as the wrong
    // proto file landing in the wrong workspace dir.
    proto(files("main-dependency.zip"))
    protoImport(files("main-import-dependency.zip"))
}
