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
    sourceSets {
        create("common2")
    }
}

rpc {
    protoc()
}

protoSourceSets {
    named("commonMain") {
        importsFrom(project.provider { protoSourceSets.getByName("common2") })
    }
}

dependencies {
    "common2ProtoImport"(files("zip/common-2-import-dependency.zip"))
}
